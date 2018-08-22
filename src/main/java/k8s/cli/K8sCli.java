package k8s.cli;

import k8s.K8sAPI;
import k8s.K8sResultConverter;
import k8s.exceptions.K8sException;
import k8s.exceptions.K8sInitFailureException;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import routes.util.ResponseCreator;
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

/**
 * Specific Kubernetes API implementation based on CLI interaction and output parsing. Even if error prone, this could
 * be useful when Harbor is running outside a Kubernetes cluster or it has only access to the <code>kubectl</code>
 * command
 *
 * @see CommandExec
 * @see Process
 */
public class K8sCli implements K8sAPI {

    final private static Logger LOG = ConfigManager.getConfig().getApplicationLogger(K8sCli.class);

    private static String kubectlPath;

    /**
     * Initialize the CLI API. It first checks if kubelet is running, then it tries to detect where the
     * <code>kubectl</code> command is located. If both commands are successful, then the initialization is complete,
     * otherwise an exception is thrown
     * @throws K8sInitFailureException this exception is thrown when one of the mandatory elements of the initialization
     * fails, i.e. <code>kubectl</code> is not present or <code>kubelet</code> is not present in the system
     */
    public K8sCli() throws K8sInitFailureException {

        try {

            // Check kubectl version: kubectl version | grep Client | cut -d',' -f3 | cut -d':' -f2 | cut -d'"' -f2
            final List<Process> kubectlVersion = new LinkedList<>();
            kubectlVersion.add(new ProcessBuilder("kubectl", "version").start());
            kubectlVersion.add(new ProcessBuilder("grep", "Client").start());
            kubectlVersion.add(new ProcessBuilder("cut", "-d", ",", "-f3").start());
            kubectlVersion.add(new ProcessBuilder("cut", "-d", ":", "-f2").start());
            kubectlVersion.add(new ProcessBuilder("cut", "-d", "\"", "-f2").start());

            final List<Process> kubectl = new LinkedList<>();
            kubectl.add(new ProcessBuilder("which", "kubectl").start());

            final Map<List<Process>, CommandExec.Result> chainOfCommandsOutput = new CommandExec.Builder()
                    .add(kubectl)
                    .add(kubectlVersion)
                    .build()
                    .exec();

            if (chainOfCommandsOutput.get(kubectl).getExitCode() == 0) {
                kubectlPath = chainOfCommandsOutput.get(kubectl).getOutput();
            } else {
                LOG.error("Which cannot find kubectl command");
                throw new K8sInitFailureException("Which cannot find kubectl command");
            }

            LOG.debug("Kubectl path is: " + kubectlPath);
            LOG.debug("Kubectl exit code is: " + chainOfCommandsOutput.get(kubectl).getExitCode());

            final String versionOutput = chainOfCommandsOutput.get(kubectlVersion).getOutput();

            if (versionOutput.matches("v1\\.11(\\.\\d+)?")) {
                LOG.debug("kubectl version found: " + chainOfCommandsOutput.get(kubectlVersion).getOutput());
            } else {
                throw new K8sInitFailureException("Wrong kubectl version detected. Only v1.11.x version are " +
                        "supported. Installed version: " + chainOfCommandsOutput.get(kubectlVersion).getOutput());
            }

        } catch (IOException e) {
            e.printStackTrace();
            throw new K8sInitFailureException("Failure while trying to execute shell commands");
        }

    }

    @Override
    public Object createFromYaml(URL pathToFile, K8sResultConverter converter)
            throws K8sException, IOException {

        ResponseCreator toSendBack;

        File yaml = new File(pathToFile.getPath());

        if (!yaml.exists()) {
            throw new FileNotFoundException();
        }

        List<Process> yamlCreation = new ArrayList<>();

        yamlCreation.add(new ProcessBuilder(kubectlPath, "create", "-f", yaml.getAbsolutePath()).start());

        final CommandExec.Result commandRes = new CommandExec.Builder()
                .add(yamlCreation)
                .build()
                .exec()
                .get(yamlCreation);

        LOG.debug("Create from YAML response: \n" + commandRes.getOutput());


        final String out = commandRes.getOutput();
        final String[] lines = out.split(System.getProperty("line.separator"));
        final JSONArray array = new JSONArray();

        if (commandRes.getExitCode() == 0) {
            LOG.info("Resources for file: " + yaml.getAbsolutePath() + " successfully created");
            toSendBack = new ResponseCreator(ResponseCreator.ResponseType.OK);
            for (final String line : lines) {
                final String[] words = line.split(" ");
                final JSONObject toAdd = new JSONObject();

                final String[] typeAndName = words[0].split("/");
                toAdd.put("type", typeAndName[0]);
                toAdd.put("name", typeAndName[1]);
                toAdd.put("status", words[1]);

                array.put(toAdd);
            }
            toSendBack.add(ResponseCreator.Fields.CONTENT, array);
        } else {
            toSendBack = new ResponseCreator(ResponseCreator.ResponseType.ERROR);
            toSendBack.add(ResponseCreator.Fields.REASON, out);
        }


        // The output of the terminal will be parsed into a JSON
        K8sCommandJSONOutput res = new K8sCommandJSONOutput(true, new JSONObject(toSendBack.toString()));
        return converter.convert(res);
    }

    @Override
    public Object deleteFromYaml(URL pathToFile, K8sResultConverter converter) throws K8sException, IOException {

        ResponseCreator toSendBack;

        File yamlToDelete = new File(pathToFile.getPath());

        if (!yamlToDelete.exists()) {
            throw new FileNotFoundException();
        }

        List<Process> yamlCreation = new ArrayList<>();

        yamlCreation.add(new ProcessBuilder(kubectlPath, "delete", "-f", yamlToDelete.getAbsolutePath()).start());

        final CommandExec.Result commandRes = new CommandExec.Builder()
                .add(yamlCreation)
                .build()
                .exec()
                .get(yamlCreation);

        LOG.debug("Delete from YAML response: \n" + commandRes.getOutput());


        final String out = commandRes.getOutput();

        // TODO perform better error and success parsing
        if (commandRes.getExitCode() == 0) {
            toSendBack = new ResponseCreator(ResponseCreator.ResponseType.OK);
            toSendBack.add(ResponseCreator.Fields.CONTENT, out);
        } else {
            toSendBack = new ResponseCreator(ResponseCreator.ResponseType.ERROR);
            toSendBack.add(ResponseCreator.Fields.REASON, out);
        }

        K8sCommandJSONOutput res = new K8sCommandJSONOutput(true, new JSONObject(toSendBack.toString()));
        return converter.convert(res);
    }
}
