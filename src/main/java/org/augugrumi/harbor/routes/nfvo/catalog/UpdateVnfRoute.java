package org.augugrumi.harbor.routes.nfvo.catalog;

import org.augugrumi.harbor.util.ConfigManager;
import org.json.JSONObject;
import org.slf4j.Logger;
import routes.util.ResponseCreator;
import spark.Request;
import spark.Response;
import spark.Route;

/**
 * This route allows to update an already existing YAML configuration with a new one. Please note that if this
 * configuration is already running, it won't be substituted with the new, and this could cause problems when the new
 * configuration gets referenced to make operations on the old one.
 * <p>
 * Also, is not possible to use this class instead of CreateVnfRoute to "update" an empty route with a new one.
 *
 * @see CreateVnfRoute
 */
public class UpdateVnfRoute implements Route {

    final private static Logger LOG = ConfigManager.getConfig().getApplicationLogger(UpdateVnfRoute.class);

    /**
     * Method that handles the request. It uses a combination of DeleteVnfRoute and CreateVnfRoute to first delete the
     * old YAML configuration and then creating a new one. Thus, the JSON returned is one of DeleteVnfRoute of
     * CreateVnfRoute respectively.
     * @param request the data sent from the client
     * @param response optional fields to set in the reply
     * @return See DeleteVnfRoute and CreateVnfRoute "handle" method to get what's returned.
     * @throws Exception if an exception occurs from the deletion or the creation of the new YAML file it's not handled
     * here, thus resulting in a 500 Internal Server Error
     * @see DeleteVnfRoute
     * @see CreateVnfRoute
     */
    @Override
    public Object handle(Request request, Response response) throws Exception {

        LOG.debug(this.getClass().getSimpleName() + " called");

        // Update = Deletion + Creation
        Route deletion = new DeleteVnfRoute();
        Route creation = new CreateVnfRoute();

        ResponseCreator toSendBack = (ResponseCreator) deletion.handle(request, response);
        JSONObject replyToJSONObject = new JSONObject(toSendBack.toString());

        if (ResponseCreator.ResponseType.OK.toString().equalsIgnoreCase(
                replyToJSONObject.getString(ResponseCreator.Fields.RESULT.toString().toLowerCase()))) {
            toSendBack = (ResponseCreator) creation.handle(request, response);
            replyToJSONObject = new JSONObject(toSendBack.toString());
            if (ResponseCreator.ResponseType.OK.toString().equalsIgnoreCase(
                    replyToJSONObject.getString(ResponseCreator.Fields.RESULT.toString().toLowerCase()))) {
                toSendBack = new ResponseCreator(ResponseCreator.ResponseType.OK);
            }
        }

        return toSendBack;
    }
}
