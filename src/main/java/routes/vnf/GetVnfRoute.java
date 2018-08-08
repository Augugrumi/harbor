package routes.vnf;

import org.json.JSONObject;
import org.slf4j.Logger;
import spark.Request;
import spark.Response;
import spark.Route;
import util.ConfigManager;

import java.io.File;
import java.io.FileInputStream;

public class GetVnfRoute implements Route {

    final private static Logger LOG = ConfigManager.getConfig().getApplicationLogger(GetVnfRoute.class);

    @Override
    public Object handle(Request request, Response response) throws Exception {

        LOG.debug("GetVnfRoute called");

        final JSONObject toSendBack = new JSONObject();
        final String filename = Utils.validateFileName(request.params(":id"));
        final File fileToReturn = new File(ConfigManager.getConfig().getYamlStorageFolder() + File.separator + filename);

        if (fileToReturn.exists()) {
            FileInputStream fis = new FileInputStream(fileToReturn);

            StringBuilder fileRead = new StringBuilder();
            int read;
            while ((read = fis.read()) != -1) {
                fileRead.append((char) read);
            }

            toSendBack.put("result", "ok");
            toSendBack.put("yaml", fileRead.toString());
        } else {
            toSendBack.put("result", "error");
            toSendBack.put("reason", "The requested file doesn't exist");
        }

        return toSendBack;
    }
}
