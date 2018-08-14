package routes.vnf;

import org.json.JSONObject;
import org.slf4j.Logger;
import routes.util.FileNameUtils;
import spark.Request;
import spark.Response;
import spark.Route;
import util.ConfigManager;

import java.io.File;
import java.io.FileInputStream;

/**
 * This route returns the YAML definition of the given id
 */
public class GetVnfRoute implements Route {

    final private static Logger LOG = ConfigManager.getConfig().getApplicationLogger(GetVnfRoute.class);

    /**
     * The request handler. If the required YAML exists (thus the id is valid) the content is returned inside a JSON.
     *
     * @param request  the data sent from the client
     * @param response optional fields to set in the reply
     * @return If the operation is successful:
     * <pre>
     *     {
     *         "result": "ok",
     *         "yaml": "The actual YAML content"
     *     }
     * </pre>
     * Otherwise, the method returns an JSON error formatted in this way:
     * <pre>
     *     {
     *         "result": "error",
     *         "reason": "The requested file doesn't exist"
     *     }
     * </pre>
     * @throws Exception when the handler fails to read the YAML file a 500 Internal server error gets returned
     */
    @Override
    public Object handle(Request request, Response response) throws Exception {

        LOG.debug(this.getClass().getSimpleName() + " called");

        final JSONObject toSendBack = new JSONObject();
        final String filename = FileNameUtils.validateFileName(request.params(":id"));
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
