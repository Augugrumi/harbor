package org.augugrumi.harbor.orchestration.components;

import org.augugrumi.harbor.k8s.K8sAPI;
import org.augugrumi.harbor.k8s.K8sRetriever;
import org.augugrumi.harbor.orchestration.Orchestrator;
import org.augugrumi.harbor.services.ServiceRetriever;
import org.augugrumi.harbor.util.ConfigManager;
import org.augugrumi.harbor.util.FileUtils;
import org.slf4j.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public abstract class AbsComponent implements Component {

    final static K8sAPI API = K8sRetriever.getK8sAPI();
    final private static String TOPOLOGY_PATH = ConfigManager.getConfig().getTopologyPath();
    final private static Logger LOG = ConfigManager.getConfig().getApplicationLogger(AbsComponent.class);
    final private static Orchestrator.Listener LISTENER = ServiceRetriever.getSystemStatusService();
    private String yamlConfig;
    private final String componentName;
    private boolean deployed;
    private boolean ok; // TODO this variable should indicate if the service is still up

    AbsComponent(String configName) {
        componentName = configName;
        ok = true;
        deployed = false;
        try (FileInputStream fis = new FileInputStream(getComponentConfigByName(configName))) {
            yamlConfig = FileUtils.readFile(fis);
        } catch (FileNotFoundException e) {
            LOG.error(Errors.NO_CONFIG + componentName);
            e.printStackTrace();
        } catch (IOException e) {
            LOG.error(Errors.CONFIG_IO + componentName);
            e.printStackTrace();
        }
    }

    private boolean launch(String yaml) throws IOException {
        return (boolean) API.createFromYaml(
                FileUtils.createTmpFile("hrbr", ".yaml", yaml).toURI().toURL(),
                res -> {
                    deployed = res.isSuccess();
                    return res.isSuccess();
                });
    }

    private String getYamlConfig() {
        return yamlConfig;
    }

    private boolean stop(String yaml) throws IOException {
        return (boolean) API.deleteFromYaml(
                FileUtils.createTmpFile("hrbr", ".yaml", yaml).toURI().toURL(),
                res -> {
                    if (res.isSuccess()) {
                        deployed = false;
                    }
                    return res.isSuccess();
                });
    }

    @Override
    public boolean deploy() {
        try {
            boolean res = launch(yamlConfig);
            LISTENER.onComponentLaunched(this);
            return res;
        } catch (IOException e) {
            LOG.error(Errors.CMP_LAUNCH);
            e.printStackTrace();
            return false;
        }
    }

    File[] getAvailableComponentsConfigurations() {
        File tp = new File(TOPOLOGY_PATH);
        if (tp.isDirectory()) {
            return tp.listFiles((file, s) -> s.matches(".*[.]ya?ml"));
        } else {
            return null;
        }
    }

    File getComponentConfigByName(String configName) {
        configName += ".yaml";
        for (File configuration : getAvailableComponentsConfigurations()) {
            if (configuration.getName().equalsIgnoreCase(configName)) {
                return configuration;
            }
        }
        return null;
    }

    @Override
    public boolean isOk() {
        return ok;
    }

    @Override
    public boolean isDeployed() {
        return deployed;
    }

    @Override
    public boolean destroy() {
        try {
            boolean res = stop(yamlConfig);
            LISTENER.onComponentStopped(this);
            return res;
        } catch (IOException e) {
            LOG.error(Errors.CMP_STOP);
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean restart() {
        try {
            if (isDeployed()) {
                return stop(yamlConfig) && launch(yamlConfig);
            } else {
                deploy();
            }
        } catch (IOException e) {
            LOG.error(Errors.CMP_RESTART);
            e.printStackTrace();
        }
        return false;
    }

    interface Errors {
        String CMP_LAUNCH = "Error while deploying the configuration on Kubernetes";
        String CMP_STOP = "Error while destroying the current configuration on Kubernetes";
        String CMP_RESTART = "Error while restarting the current configuration on Kubernetes";
        String NO_CONFIG = "Impossible to load configuration for the component ";
        String CONFIG_IO = "Error while reading configuration for the component ";
    }

    @Override
    public String getComponentRole() {
        return componentName;
    }
}
