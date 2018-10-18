package org.augugrumi.harbor.routes.nfvo.catalog;

import org.augugrumi.harbor.persistence.data.DataWizard;
import org.augugrumi.harbor.routes.util.Errors;
import org.augugrumi.harbor.routes.util.ParamConstants;
import org.augugrumi.harbor.util.ConfigManager;
import org.slf4j.Logger;
import routes.util.ResponseCreator;
import spark.Request;
import spark.Response;
import spark.Route;

/**
 * Deletes a YAML configuration given the right id. The operation fails if a bogus id is provided, or if the backend
 * hasn't the I/O possibility to do this
 */
public class DeleteVnfRoute implements Route {

    final private static Logger LOG = ConfigManager.getConfig().getApplicationLogger(DeleteVnfRoute.class);

    /**
     * It handles the delete operation
     *
     * @param request  the data sent from the client
     * @param response optional fields to set in the reply
     * @return a valid JSON: <pre>
     *     {
     *         "result": "ok"
     *     }
     * </pre>
     * when the operation is successful, otherwise if the backend is not able to delete the file it returns: <pre>
     *     {
     *         "result": "error",
     *         "reason": "Impossible to remove the object from the database"
     *     }
     * </pre>
     */
    @Override
    public Object handle(Request request, Response response) {

        LOG.debug(this.getClass().getSimpleName() + " called");
        if (DataWizard.deleteVNF(request.params(ParamConstants.ID))) {
            return new ResponseCreator(ResponseCreator.ResponseType.OK);
        } else {
            return new ResponseCreator(ResponseCreator.ResponseType.ERROR)
                    .add(ResponseCreator.Fields.REASON, Errors.DB_REMOVE);
        }
    }
}
