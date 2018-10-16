package org.augugrumi.harbor.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Singleton handling requests to the true configuration class
 *
 * @see Config
 */
public class ConfigManager {

    private static Config configuration;

    private ConfigManager () {

    }

    /**
     * Getter method to obtain the Singleton configuration. Note that this is a "lazy" singleton, so the real singleton
     * gets created the first time it's required, and not at the ConfigManager creation.
     * @return If it's the first call, it returns a new Config instance, otherwise an already existing Config it's
     * returned
     * @see Config
     */
    public static synchronized Config getConfig () {
        if (configuration == null) {
            configuration = new Config();
        }

        return configuration;
    }

    /**
     * The true class containing global variables and global resources
     */
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
        private String YAML_STORAGE;


        /**
         * Creates a Config class, taking all the needed environment variables and coping them into local variables.
         */
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
                        File.separator + "persistence";

            }
            LOG.debug("Environment variable" + HB_YAML_STORAGE + " set to: " + this.YAML_STORAGE);
        }

        /**
         * Getter method to obtain the port number in with the server will listen to request
         * @return it returns the port in with Sparks Java is running
         */
        public int getPort () {
            return PORT;
        }

        /**
         * Get the application logger based on the class name provided
         * @param name the class description
         * @return a logger with the configured class name
         */
        public Logger getApplicationLogger (Class name) {
            return LoggerFactory.getLogger(name);
        }

        /**
         * Getter method to obtain the filepath to the JSON API config
         * @return a filepath to the API config JSON
         */
        public String getAPIConfig () {
            return API_CONFIG_PATH;
        }

        /**
         * Getter method to obtain the full kubernetes API address. Useful when Harbor is running inside a container in
         * a Kubernetes environment
         * @return an URL to the kubernetes-api container
         */
        public String getFullKubernetesAddress() {

            String toAttach = this.KUBERNETES_PORT == null ? "" : ":" + this.KUBERNETES_PORT;
            String prot = "443".equals(this.KUBERNETES_PORT) || "8443".equals(this.KUBERNETES_PORT) ? "https" : "http";

            return prot + "://" + this.KUBERNETES_URL + toAttach;
        }

        /**
         * Getter method to retrieve the YAML storage folder path
         * @return return the YAML folder path where YAML configurations will be saved
         */
        public String getYamlStorageFolder() {
            return this.YAML_STORAGE;
        }


        /**
         * Setter method to change port number
         * @param port a new port destination
         */
        void setPort(int port) {
            this.PORT = port;
        }

        /**
         * Setter method to change API filepath
         * @param APIPath the new API filepath
         */
        void setAPIConfig(String APIPath) {
            this.API_CONFIG_PATH = APIPath;
        }

        /**
         * Setter method to change Kubernetes API address
         * @param k8sAddress a new URL to the kubernetes-api container
         * @throws MalformedURLException if the given url is not valid
         */
        void setKubernetesAddress(String k8sAddress) throws MalformedURLException {

            URL newURL = new URL(k8sAddress);

            this.KUBERNETES_URL = newURL.getHost();
            this.KUBERNETES_PORT = newURL.getPort() == -1 ?
                    String.valueOf(newURL.getDefaultPort()) : String.valueOf(newURL.getPort());
        }

        /**
         * Setter method to change YAML storage home
         * @param newPath a new directory to store YAML configuration files
         */
        void setYAMLHome(String newPath) {
            this.YAML_STORAGE = newPath;
        }

        /**
         * Get if running inside the kubernetes environment or not
         * @return True if running inside a Kubernetes cluster, false otherwise
         */
        public boolean isRunningInKubernetes() {
            return System.getenv(K8S_API_ENDPOINT) != null;
        }
    }


}
