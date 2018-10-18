package org.augugrumi.harbor.routes.nfvo.catalog;

import org.augugrumi.harbor.persistence.Result;
import org.augugrumi.harbor.persistence.data.DataWizard;
import org.augugrumi.harbor.persistence.data.VirtualNetworkFunction;
import org.augugrumi.harbor.routes.util.Errors;
import org.augugrumi.harbor.util.ConfigManager;
import org.slf4j.Logger;
import routes.util.ResponseCreator;
import spark.Request;
import spark.Response;
import spark.Route;

import static org.augugrumi.harbor.routes.util.ParamConstants.ID;

/**
 * This route adds to the internal database a new Kubernetes YAML. Note that you have to provide an unique id to this
 * API: this id will be used internally to distinguish various YAMLs. You can't add two YAML with the same ids,
 * otherwise you'll get an error.
 */
public class CreateVnfRoute implements Route {

    final private static Logger LOG = ConfigManager.getConfig().getApplicationLogger(CreateVnfRoute.class);

    /**
     * The request handler. It checks that the id is unique and then it adds it to the database.
     *
     * @param request  the data sent from the client
     * @param response optional fields to set in the reply
     * @return a valid JSON: <pre>
     *     {
     *         "result": "ok"
     *     }
     * </pre>
     * when the operation is successful, otherwise it returns: <pre>
     *     {
     *         "result": "error",
     *         "reason": "Impossible to add the object in the database"
     *     }
     */
    @Override
    public Object handle(Request request, Response response) {

        LOG.debug(this.getClass().getSimpleName() + " called");
        Result<VirtualNetworkFunction> vnf = DataWizard.newVNF(request.params(ID), request.body());
        if (vnf.isSuccessful() && vnf.getContent().isValid()) {
            return new ResponseCreator(ResponseCreator.ResponseType.OK);
        } else {
            return new ResponseCreator(ResponseCreator.ResponseType.ERROR)
                    .add(ResponseCreator.Fields.REASON, Errors.DB_ADD);
        }
    }
}
