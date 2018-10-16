package org.augugrumi.harbor.routes.nfvo.catalog;

import org.augugrumi.harbor.persistence.Persistence;
import org.augugrumi.harbor.persistence.PersistenceFactory;
import org.augugrumi.harbor.persistence.Query;
import org.augugrumi.harbor.persistence.Result;
import org.augugrumi.harbor.routes.util.RequestQuery;
import org.augugrumi.harbor.util.ConfigManager;
import org.slf4j.Logger;
import routes.util.ResponseCreator;
import spark.Request;
import spark.Response;
import spark.Route;

import java.io.IOException;

import static org.augugrumi.harbor.persistence.Costants.VNF_HOME;
import static org.augugrumi.harbor.routes.util.Costants.ID;

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
        final Persistence db = PersistenceFactory.getFSPersistence(VNF_HOME);
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
                throw new IOException("The server encountered an IO error while accessing " + request.params(ID));
            }
        }
        return toSendBack;
    }
}
