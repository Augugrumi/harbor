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

        // Init config keys
        final private String HB_PORT = "HARBOR_PORT";
        final private String HB_API = "HARBOR_API_CONFIG";
        // End config keys

        final private static Logger LOG = LoggerFactory.getLogger(Config.class);

        final private int PORT;
        final private String API_CONFIG_PATH;


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
    }


}
