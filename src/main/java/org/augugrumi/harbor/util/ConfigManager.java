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
        final private static String K8S_API_ENDPOINT = "KUBERNETES_SERVICE_HOST";
        final private static String K8S_API_PORT = "KUBERNETES_SERVICE_PORT_HTTPS";
        // End external env variables

        // Init config keys
        final private static String HB_PORT = "HARBOR_PORT";
        final private static String HB_API = "HARBOR_API_CONFIG";
        final private static String HB_KUBERNETES = "HARBOR_KUBERNETES_URL";
        final private static String HB_STORAGE = "HARBOR_STORAGE_PATH";
        final private static String HB_ROULETTE = "HARBOR_ROULETTE_URL";
        // End config keys

        // Default variables
        final private static String LOCALHOST = "localhost";
        final private static String HB_HOME = File.separator + ".harbor" + File.separator + "persistence";
        // End default variables

        final private static Logger LOG = LoggerFactory.getLogger(Config.class);

        private int port;
        private URL rouletteUrl;
        private URL kubernetesUrl;
        private String apiConfigPath;
        private String storageFolder;


        /**
         * Creates a Config class, taking all the needed environment variables and coping them into local variables.
         */
        private Config () {

            LOG.debug("Environment variable " + HB_PORT + " set to: " + System.getenv(HB_PORT));
            if (System.getenv(HB_PORT) != null) {
                this.port = Integer.parseInt(System.getenv(HB_PORT));
            } else {
                this.port = 80;
            }
            LOG.info("Set running port to: " + port);

            LOG.debug("Environment variable " + HB_API + " set to: " + System.getenv(HB_API));
            this.apiConfigPath = System.getenv(HB_API);

            try {
                if (System.getenv(HB_KUBERNETES) != null) {
                    this.kubernetesUrl = new URL(System.getenv(HB_KUBERNETES));
                } else if (System.getenv(K8S_API_ENDPOINT) != null) {
                    this.kubernetesUrl = new URL(System.getenv(K8S_API_ENDPOINT));
                } else {
                    this.kubernetesUrl = new URL(System.getenv(LOCALHOST));
                }
                LOG.debug("Environment variable" + HB_KUBERNETES + " set to: " + this.kubernetesUrl);

                if (System.getenv(HB_ROULETTE) != null) {
                    this.rouletteUrl = new URL(System.getenv(HB_ROULETTE));
                } else {
                    this.rouletteUrl = new URL(System.getenv(LOCALHOST));
                }
                LOG.debug("Environment variable" + HB_ROULETTE + " set to: " + this.rouletteUrl);
            } catch (MalformedURLException e) {
                e.printStackTrace();
                System.exit(1);
            }

            if (System.getenv(HB_STORAGE) != null) {
                this.storageFolder = System.getenv(HB_STORAGE);
            } else {
                this.storageFolder = System.getProperty("user.home") + HB_HOME;
            }
            LOG.debug("Environment variable" + HB_STORAGE + " set to: " + this.storageFolder);
        }

        /**
         * Getter method to obtain the port number in with the server will listen to request
         * @return it returns the port in with Sparks Java is running
         */
        public int getPort () {
            return port;
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
            return apiConfigPath;
        }

        /**
         * Getter method to obtain the full kubernetes API address. Useful when Harbor is running inside a container in
         * a Kubernetes environment
         * @return an URL to the kubernetes-api container
         */
        public String getFullKubernetesAddress() {
            return kubernetesUrl.toString();
        }

        /**
         * Getter method to retrieve the YAML storage folder path
         * @return return the YAML folder path where YAML configurations will be saved
         */
        public String getStorageFolder() {
            return this.storageFolder;
        }

        public String getRouletteUrl() {
            return rouletteUrl.toString();
        }

        /**
         * Get if running inside the kubernetes environment or not
         *
         * @return True if running inside a Kubernetes cluster, false otherwise
         */
        public boolean isRunningInKubernetes() {
            return System.getenv(K8S_API_ENDPOINT) != null;
        }

        /**
         * Setter method to change port number
         * @param port a new port destination
         */
        void setPort(int port) {
            this.port = port;
        }

        /**
         * Setter method to change API filepath
         * @param APIPath the new API filepath
         */
        void setAPIConfig(String APIPath) {
            this.apiConfigPath = APIPath;
        }

        /**
         * Setter method to change Kubernetes API address
         * @param k8sAddress a new URL to the kubernetes-api container
         * @throws MalformedURLException if the given url is not valid
         */
        void setKubernetesUrl(String k8sAddress) throws MalformedURLException {
            kubernetesUrl = new URL(k8sAddress);
        }

        /**
         * Setter method to change YAML storage home
         * @param newPath a new directory to store YAML configuration files
         */
        void setStorageFolder(String newPath) {
            this.storageFolder = newPath;
        }

        void setRouletteUrl(String rouletteAddress) throws MalformedURLException {
            rouletteUrl = new URL(rouletteAddress);
        }
    }


}
