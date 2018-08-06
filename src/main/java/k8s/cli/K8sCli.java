package k8s.cli;

import k8s.K8sAPI;
import k8s.K8sResultConverter;
import k8s.exceptions.K8sException;
import k8s.exceptions.K8sInitFailureException;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import util.CommandExec;
import util.ConfigManager;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class K8sCli implements K8sAPI {

    final private static Logger LOG = ConfigManager.getConfig().getApplicationLogger(K8sCli.class);

    private static String kubectlPath;

    public K8sCli() throws K8sInitFailureException {

        // TODO check that K8s cluster runs 1.11.x!
        try {
            // Check if kubelet is up and Kubectl exists
            List<Process> kubelet = new LinkedList<>();
            kubelet.add(new ProcessBuilder("systemctl", "status", "kubelet").start());
            kubelet.add(new ProcessBuilder("grep", "Active").start());
            kubelet.add(new ProcessBuilder("cut", "-d", " ", "-f5").start());

            List<Process> kubectl = new LinkedList<>();
            kubectl.add(new ProcessBuilder("which", "kubectl").start());

            Map<List<Process>, CommandExec.Result> chainOfCommandsOutput = new CommandExec.Builder()
                    .add(kubelet)
                    .add(kubectl)
                    .build()
                    .exec();

            if (!"active".equals(chainOfCommandsOutput.get(kubelet).getOutput())) {
                LOG.error("Kubectl output: " + chainOfCommandsOutput.get(kubelet).getOutput());
                LOG.error("Kubectl exit code: " + chainOfCommandsOutput.get(kubelet).getExitCode());
                throw new K8sInitFailureException();
            }

            if (chainOfCommandsOutput.get(kubectl).getExitCode() == 0) {
                kubectlPath = chainOfCommandsOutput.get(kubectl).getOutput();
            } else {
                LOG.error("Which cannot find kubectl command");
                throw new K8sInitFailureException();
            }
            LOG.debug("Kubectl path is: " + kubectlPath);
            LOG.debug("Kubectl exit code is: " + chainOfCommandsOutput.get(kubectl).getExitCode());

        } catch (IOException e) {
            e.printStackTrace();
            throw new K8sInitFailureException();
        }

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

        List<Process> yamlcreation = new ArrayList<>();
        yamlcreation.add(new ProcessBuilder("kubectl", "create", "-f", yaml.getAbsolutePath()).start());

        String out = new CommandExec.Builder()
                .add(yamlcreation)
                .build()
                .exec()
                .get(yamlcreation)
                .getOutput();

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
