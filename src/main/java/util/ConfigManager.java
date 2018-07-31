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
        final private String HBPPORT = "HARBOR_PORT";
        // End config keys

        final private static Logger LOGGER = LoggerFactory.getLogger(Config.class);

        final private int PORT;


        private Config () {

            LOGGER.debug("Environment variable " + HBPPORT + " set to: " + System.getenv(HBPPORT));
            if (System.getenv(HBPPORT) != null) {
                this.PORT = Integer.parseInt(System.getenv(HBPPORT));
            } else {
                this.PORT = 80;
            }
            LOGGER.info("Set running port to: " + PORT);
        }

        public int getPort() {
            return PORT;
        }

        public Logger getApplicationLogger() {
            return LOGGER;
        }
    }


}
