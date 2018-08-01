import org.slf4j.Logger;
import util.ConfigManager;
import util.DynamicAPILoader;

import java.io.IOException;

import static spark.Spark.port;

public class Main {

    final private static Logger LOG = ConfigManager.getConfig().getApplicationLogger(Main.class);

    public static void main (String args[]) {

        LOG.info("Harbor started");

        if (ConfigManager.getConfig().isRunningInKubernetes()) {
            LOG.info("Detected Kubernetes environment");
        }

        port(ConfigManager.getConfig().getPort());

        try {
            DynamicAPILoader apiLoader = new DynamicAPILoader(ConfigManager.getConfig().getAPIConfig());
            apiLoader.load();
        } catch (IOException e) {
            LOG.error("Application could not load a valid API configuration");
            e.printStackTrace();
            System.exit(1);
        }

        LOG.info("Route successfully set");
    }
}
