package util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
        // End config keys

        final private static Logger LOG = LoggerFactory.getLogger(Config.class);

        final private int PORT;
        final private String API_CONFIG_PATH;
        final private String KUBERNETES_URL;
        final private String KUBERNETES_PORT;


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
            return this.KUBERNETES_URL + this.KUBERNETES_PORT;
        }

        public boolean isRunningInKubernetes() {
            return System.getenv(K8S_API_ENDPOINT) != null;
        }
    }


}
