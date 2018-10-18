package org.augugrumi.harbor.k8s.cli;

import org.augugrumi.harbor.k8s.K8sAPI;
import org.augugrumi.harbor.k8s.K8sResultConverter;
import org.augugrumi.harbor.k8s.exceptions.K8sException;
import org.augugrumi.harbor.k8s.exceptions.K8sInitFailureException;
import org.augugrumi.harbor.util.CommandExec;
import org.augugrumi.harbor.util.ConfigManager;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import routes.util.ResponseCreator;

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

        // TODO check that K8s cluster runs 1.12.x!
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
