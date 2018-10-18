package org.augugrumi.harbor.routes.nfvo.catalog;

import org.augugrumi.harbor.persistence.Result;
import org.augugrumi.harbor.persistence.data.DataWizard;
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
        Result<VirtualNetworkFunction> vnfRes = DataWizard.getVNF(request.params(ParamConstants.ID));
        if (vnfRes.isSuccessful()) {
            return new ResponseCreator(ResponseCreator.ResponseType.OK)
                    .add(ResponseCreator.Fields.CONTENT, vnfRes.getContent().toJson());
        } else {
            return new ResponseCreator(ResponseCreator.ResponseType.ERROR)
                    .add(ResponseCreator.Fields.REASON, Errors.NO_SUCH_ELEMENT);
        }
    }
}
