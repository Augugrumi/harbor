package routes.vnf;

import org.json.JSONObject;
import org.slf4j.Logger;
import routes.util.ResponseCreator;
import spark.Request;
import spark.Response;
import spark.Route;
import util.ConfigManager;

public class UpdateVnfRoute implements Route {

    final private static Logger LOG = ConfigManager.getConfig().getApplicationLogger(UpdateVnfRoute.class);

    @Override
    public Object handle(Request request, Response response) throws Exception {

        LOG.debug(this.getClass().getSimpleName() + " called");

        // Update = Deletion + Creation
        Route deletion = new DeleteVnfRoute();
        Route creation = new CreateVnfRoute();

        ResponseCreator toSendBack;

        ResponseCreator reply = (ResponseCreator) deletion.handle(request, response);
        JSONObject replyToJSONObject = new JSONObject(reply.toString());


        // FIXME this code sucks, please find a better solution :(
        if (ResponseCreator.ResponseType.OK.toString().equalsIgnoreCase(
                replyToJSONObject.getString(ResponseCreator.Fields.RESULT.toString().toLowerCase()))) {
            reply = (ResponseCreator) creation.handle(request, response);
            replyToJSONObject = new JSONObject(reply.toString());
            if (ResponseCreator.ResponseType.OK.toString().equalsIgnoreCase(
                    replyToJSONObject.getString(ResponseCreator.Fields.RESULT.toString().toLowerCase()))) {
                toSendBack = new ResponseCreator(ResponseCreator.ResponseType.OK);
            } else {
                toSendBack = reply;
            }
        } else {
            toSendBack = reply;
        }

        return toSendBack;
    }
}
