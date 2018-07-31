import util.ConfigManager;

import static spark.Spark.get;
import static spark.Spark.port;

public class Main {

    public static void main (String args[]) {

        ConfigManager.getConfig().getApplicationLogger().info("Harbor started");

        port(ConfigManager.getConfig().getPort());

        get("/hello", (req, res) -> "Hello World");

        ConfigManager.getConfig().getApplicationLogger().info("Route successfully set");
    }
}
