package util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

public class ConfigManager {

    private static Config configuration;

    private ConfigManager () {

    }

    public static synchronized Config getConfig () {
        if (configuration == null) {
            configuration = new Config();
        }

        return configuration;
    }

    public static class Config {

        // External env variables
        final private String K8S_API_ENDPOINT = "KUBERNETES_SERVICE_HOST";
        final private String K8S_API_PORT = "KUBERNETES_SERVICE_PORT_HTTPS";
        // End external env variables

        // Init config keys
        final private String HB_PORT = "HARBOR_PORT";
        final private String HB_API = "HARBOR_API_CONFIG";
        final private String HB_KUBERNETES = "HARBOR_KUBERNETES_URL";
        final private String HB_YAML_STORAGE = "HARBOR_YAML_STORAGE_PATH";
        // End config keys

        final private static Logger LOG = LoggerFactory.getLogger(Config.class);

        private int PORT;
        private String API_CONFIG_PATH;
        private String KUBERNETES_URL;
        private String KUBERNETES_PORT;
        final private String YAML_STORAGE;


        private Config () {

            LOG.debug("Environment variable " + HB_PORT + " set to: " + System.getenv(HB_PORT));
            if (System.getenv(HB_PORT) != null) {
                this.PORT = Integer.parseInt(System.getenv(HB_PORT));
            } else {
                this.PORT = 80;
            }
            LOG.info("Set running port to: " + PORT);

            LOG.debug("Environment variable " + HB_API + " set to: " + System.getenv(HB_API));
            this.API_CONFIG_PATH = System.getenv(HB_API);

            if (System.getenv(HB_KUBERNETES) != null) {
                this.KUBERNETES_URL = System.getenv(HB_KUBERNETES);
            } else if (System.getenv(K8S_API_ENDPOINT) != null) {
                this.KUBERNETES_URL = System.getenv(K8S_API_ENDPOINT);
            } else {
                this.KUBERNETES_URL = "localhost";
            }
            LOG.debug("Environment variable" + HB_KUBERNETES + " set to: " + this.KUBERNETES_URL);

            this.KUBERNETES_PORT = System.getenv(K8S_API_PORT);
            LOG.debug("Environment variable" + K8S_API_PORT + " set to: " + this.KUBERNETES_PORT);

            if (System.getenv(HB_YAML_STORAGE) != null) {
                this.YAML_STORAGE = System.getenv(HB_YAML_STORAGE);
            } else {
                this.YAML_STORAGE = System.getProperty("user.home") +
                        File.separator + ".harbor" +
                        File.separator + "yaml";

                File firstRunCheck = new File(this.YAML_STORAGE);
                if (firstRunCheck.isDirectory() && !firstRunCheck.exists()) {
                    firstRunCheck.mkdirs();
                }
            }
            LOG.debug("Environment variable" + HB_YAML_STORAGE + " set to: " + this.YAML_STORAGE);
        }

        public int getPort () {
            return PORT;
        }

        public Logger getApplicationLogger (Class name) {
            return LoggerFactory.getLogger(name);
        }

        public String getAPIConfig () {
            return API_CONFIG_PATH;
        }

        public String getFullKubernetesAddress() {

            String toAttach = this.KUBERNETES_PORT == null ? "" : ":" + this.KUBERNETES_PORT;
            String prot = "443".equals(this.KUBERNETES_PORT) || "8443".equals(this.KUBERNETES_PORT) ? "https" : "http";

            return prot + "://" + this.KUBERNETES_URL + toAttach;
        }

        public String getYamlStorageFolder() {
            return this.YAML_STORAGE;
        }


        void setPort(int port) {
            this.PORT = port;
        }

        void setAPIConfig(String APIPath) {
            this.API_CONFIG_PATH = APIPath;
        }

        void setKubernetesAddress(String k8sAddress) throws MalformedURLException {

            URL newURL = new URL(k8sAddress);

            this.KUBERNETES_URL = newURL.getHost();
            this.KUBERNETES_PORT = newURL.getPort() == -1 ?
                    String.valueOf(newURL.getDefaultPort()) : String.valueOf(newURL.getPort());
        }

        public boolean isRunningInKubernetes() {
            return System.getenv(K8S_API_ENDPOINT) != null;
        }
    }


}
