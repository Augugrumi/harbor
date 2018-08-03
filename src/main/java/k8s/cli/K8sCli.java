package k8s.cli;

import k8s.K8sAPI;
import k8s.K8sResultConverter;
import k8s.exceptions.K8sException;
import k8s.exceptions.K8sInitFailureException;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import util.ConfigManager;

import java.io.*;
import java.net.URL;

public class K8sCli implements K8sAPI {

    final private static Logger LOG = ConfigManager.getConfig().getApplicationLogger(K8sCli.class);

    private static String kubectlPath;

    public K8sCli() throws K8sInitFailureException {

        // TODO check that K8s cluster runs 1.11.x!

        try {
            // Check if kubelet is up and Kubectl exists
            ProcessBuilder kubelet = new ProcessBuilder(
                    "systemctl", "status", "kubelet", "|", "grep Active", "|", "cut", "-d", "' '", "-f5"
                    // systemctl status kubelet | grep Active | cut -d" " -f5
            );
            kubelet.redirectErrorStream(false);
            if (!"active".equals(getOutputFromCommand(kubelet))) {
                throw new K8sInitFailureException();
            }

            ProcessBuilder kubectl = new ProcessBuilder(
                    "which", "kubectl"
            );
            kubectlPath = getOutputFromCommand(kubectl);
            LOG.debug("Kubectl path is: " + kubectlPath);
        } catch (IOException e) {
            e.printStackTrace();
            throw new K8sInitFailureException();
        }
    }

    private String getOutputFromCommand(ProcessBuilder process) throws IOException {
        Process prc = process.start();
        InputStream is = prc.getInputStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));

        StringBuilder res = new StringBuilder();
        String line = null;
        while ((line = reader.readLine()) != null) {
            res.append(line);
        }

        return res.toString();
    }

    @Override
    public Object createFromYaml(URL pathToFile, K8sResultConverter converter)
            throws K8sException, IOException {
        // TODO add support for online URL (like www.example.com/example.yaml or https://www.example.com/example.yaml)
        JSONObject payload = new JSONObject();

        File yaml = new File(pathToFile.getPath());

        if (!yaml.exists()) {
            throw new FileNotFoundException();
        }

        ProcessBuilder resCreatorYaml = new ProcessBuilder("kubectl", "create", "-f", yaml.getAbsolutePath());
        resCreatorYaml.redirectErrorStream(false);

        String out = getOutputFromCommand(resCreatorYaml);
        LOG.debug("Create from YAML response: \n" + out);

        String[] lines = out.split(System.getProperty("line.separator"));
        JSONArray array = new JSONArray();

        for (String line : lines) {
            String[] words = line.split(" ");
            JSONObject toAdd = new JSONObject();
            toAdd.put("type", words[0]);
            toAdd.put("name", words[1]);
            toAdd.put("status", words[2]);

            array.put(toAdd);
        }
        payload.put("output", array);

        // The output of the terminal will be parsed into a JSON
        K8sCommandJSONOutput res = new K8sCommandJSONOutput(true, payload);

        return converter.convert(res);
    }
}
