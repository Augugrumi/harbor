package org.augugrumi.harbor.routes.nfvo.catalog;

import org.augugrumi.harbor.persistence.Persistence;
import org.augugrumi.harbor.persistence.PersistenceRetriever;
import org.augugrumi.harbor.persistence.Query;
import org.augugrumi.harbor.persistence.Result;
import org.augugrumi.harbor.routes.util.RequestQuery;
import org.augugrumi.harbor.util.ConfigManager;
import org.slf4j.Logger;
import routes.util.ResponseCreator;
import spark.Request;
import spark.Response;
import spark.Route;

import static org.augugrumi.harbor.routes.util.ParamConstants.ID;

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
     *         "reason": "Failed to delete the file"
     *     }
     * </pre>
     * or, if the id doesn't exists: <pre>
     *     {
     *         "result": "error",
     *         "reason": "The requested YAML doesn't exist"
     *     }
     * </pre>
     * Finally, the DB could not be reachable. In that case, the error will be:
     * <pre>
     *     {
     *         "result": "error",
     *         "reason": "Impossible to access the DB"
     *     }
     * </pre>
     * <p>
     * This method never occur in an 500 Internal Error.
     */
    @Override
    public Object handle(Request request, Response response) {

        LOG.debug(this.getClass().getSimpleName() + " called");
        final Persistence db = PersistenceRetriever.getVnfDb();
        final Query q = new RequestQuery(ID, request);
        ResponseCreator toSendBack;

        Result<Boolean> exist = db.exists(q);
        if (exist.isSuccessful()) {
            if (exist.getContent()) {
                final Result<Void> res = db.delete(q);
                if (res.isSuccessful()) {
                    toSendBack = new ResponseCreator(ResponseCreator.ResponseType.OK);
                } else {
                    toSendBack = new ResponseCreator(ResponseCreator.ResponseType.ERROR);
                    toSendBack.add(ResponseCreator.Fields.REASON, "Failed to delete the file");
                }
            } else {
                toSendBack = new ResponseCreator(ResponseCreator.ResponseType.ERROR);
                toSendBack.add(ResponseCreator.Fields.REASON, "The requested YAML doesn't exist");
            }
        } else {
            toSendBack = new ResponseCreator(ResponseCreator.ResponseType.ERROR);
            toSendBack.add(ResponseCreator.Fields.REASON, "Impossible to access the DB");
        }
        return toSendBack;
    }
}
