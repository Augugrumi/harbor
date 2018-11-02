package org.augugrumi.harbor.orchestration.components;

import org.augugrumi.harbor.k8s.K8sAPI;
import org.augugrumi.harbor.k8s.K8sRetriever;
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
    private String yamlConfig;
    private boolean deployed;
    private boolean ok; // TODO this variable should indicate if the service is still up

    AbsComponent(String configName) {
        ok = true;
        deployed = false;
        try (FileInputStream fis = new FileInputStream(getComponentConfigByName(configName))) {
            yamlConfig = FileUtils.readFile(fis);
        } catch (FileNotFoundException e) {
            LOG.error("Impossile to load configuration for the " + configName + " component");
            e.printStackTrace();
        } catch (IOException e) {
            LOG.error("Error while reading configuration for the " + configName + " component");
            e.printStackTrace();
        }
    }

    private String getYamlConfig() {
        return yamlConfig;
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
    public boolean deploy() {
        try {
            return (boolean) API.createFromYaml(
                    FileUtils.createTmpFile("hrbr", ".yaml", yamlConfig).toURI().toURL(),
                    res -> {
                        deployed = res.isSuccess();
                        return res.isSuccess();
                    });
        } catch (IOException e) {
            LOG.error("Error while deploying the configuration on Kubernetes");
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean destroy() {
        try {
            return (boolean) API.deleteFromYaml(
                    FileUtils.createTmpFile("hrbr", ".yaml", yamlConfig).toURI().toURL(),
                    res -> {
                        if (res.isSuccess()) {
                            deployed = false;
                        }
                        return res.isSuccess();
                    });
        } catch (IOException e) {
            LOG.error("Error while destroying the current configuration on Kubernetes");
            e.printStackTrace();
            return false;
        }
    }
}
