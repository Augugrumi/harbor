package org.augugrumi.harbor.routes.nfvo.catalog;

import org.augugrumi.harbor.persistence.data.VirtualNetworkFunction;
import org.augugrumi.harbor.routes.util.Errors;
import org.augugrumi.harbor.routes.util.ParamConstants;
import org.augugrumi.harbor.util.ConfigManager;
import org.slf4j.Logger;
import routes.util.ResponseCreator;
import spark.Request;
import spark.Response;
import spark.Route;

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
     */
    @Override
    public Object handle(Request request, Response response) {

        LOG.debug(this.getClass().getSimpleName() + " called");
        VirtualNetworkFunction vnf = new VirtualNetworkFunction(request.params(ParamConstants.ID));
        if (vnf.isValid()) {
            return new ResponseCreator(ResponseCreator.ResponseType.OK).add(ResponseCreator.Fields.CONTENT, vnf.toJson());
        } else {
            return new ResponseCreator(ResponseCreator.ResponseType.ERROR)
                    .add(ResponseCreator.Fields.REASON, Errors.NO_SUCH_ELEMENT);
        }

        /*

        final Persistence db = PersistenceRetriever.getVnfDb();
        final Query q = new RequestQuery(ID, request);
        ResponseCreator toSendBack;

        Result res = db.get(q);
        if (res.isSuccessful()) {
            toSendBack = new ResponseCreator(ResponseCreator.ResponseType.OK);
            toSendBack.add(ResponseCreator.Fields.CONTENT, res.getContent());
        } else {
            toSendBack = new ResponseCreator(ResponseCreator.ResponseType.ERROR);
            if ((Integer) res.getContent() == -1) {
                toSendBack.add(ResponseCreator.Fields.REASON, "The requested file doesn't exist");
            } else {
                return dbErr();
            }
        }
        return toSendBack;*/
    }
}
