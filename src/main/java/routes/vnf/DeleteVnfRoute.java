package routes.vnf;

import org.json.JSONObject;
import org.slf4j.Logger;
import spark.Request;
import spark.Response;
import spark.Route;
import util.ConfigManager;

import java.io.File;

public class DeleteVnfRoute implements Route {

    final private static Logger LOG = ConfigManager.getConfig().getApplicationLogger(DeleteVnfRoute.class);

    @Override
    public Object handle(Request request, Response response) {

        LOG.debug("DeleteVnfRoute called");

        final String filename = Utils.validateFileName(request.params(":id"));
        final File yamlToDelete = new File(ConfigManager.getConfig().getYamlStorageFolder() + File.separator + filename);
        final JSONObject toSendBack = new JSONObject();

        if (yamlToDelete.exists()) {
            if (yamlToDelete.delete()) {
                toSendBack.put("result", "ok");
            } else {
                toSendBack.put("result", "error");
                toSendBack.put("reason", "Failed to delete the file!");
            }
        } else {
            toSendBack.put("result", "error");
            toSendBack.put("reason", "The requested YAML doesn't exist");
        }

        return toSendBack;
    }
}
