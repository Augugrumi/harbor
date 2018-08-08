package routes.vnf;

import org.json.JSONObject;
import org.slf4j.Logger;
import spark.Request;
import spark.Response;
import spark.Route;
import util.ConfigManager;

public class UpdateVnfRoute implements Route {

    final private static Logger LOG = ConfigManager.getConfig().getApplicationLogger(UpdateVnfRoute.class);

    @Override
    public Object handle(Request request, Response response) throws Exception {

        LOG.debug("UpdateVnfRoute called");

        // Update = Deletion + Creation
        Route deletion = new DeleteVnfRoute();
        Route creation = new UpdateVnfRoute();

        JSONObject toSendBack = new JSONObject();

        JSONObject reply = (JSONObject) deletion.handle(request, response);
        if ("ok".equals(reply.getString("result"))) {
            reply = (JSONObject) creation.handle(request, response);
            if ("ok".equals(reply.getString("result"))) {
                toSendBack.put("result", "ok");
            } else {
                toSendBack = reply;
            }
        } else {
            toSendBack = reply;
        }

        return toSendBack;
    }
}
