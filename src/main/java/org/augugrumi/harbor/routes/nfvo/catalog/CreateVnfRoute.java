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

import static org.augugrumi.harbor.persistence.Costants.VNF_HOME;
import static org.augugrumi.harbor.routes.util.Costants.ID;

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
     * when the operation is successful, otherwise if the id is already in use it returns: <pre>
     *     {
     *         "result": "error",
     *         "reason": "A YAML with the same key already exists"
     *     }
     * </pre>
     * Finally, there is the possibility that the DB can't store more data. In this case, the json returned will be:
     * <pre>
     *     {
     *         "result": "error",
     *         "reason": "Impossible to save data in the DB"
     *     }
     * </pre>
     * @throws Exception when an internal error occurs
     */
    @Override
    public Object handle(Request request, Response response) throws Exception {

        LOG.debug(this.getClass().getSimpleName() + " called");
        final Persistence db = PersistenceFactory.getFSPersistence(VNF_HOME);
        final Query q = new RequestQuery(ID, request);
        ResponseCreator toSendBack;

        if (db.exists(q).isSuccessful()) {
            toSendBack = new ResponseCreator(ResponseCreator.ResponseType.ERROR);
            toSendBack.add(ResponseCreator.Fields.REASON, "A YAML with the same key already exists");
        } else {
            Result res = db.save(q);
            if (res.isSuccessful()) {
                toSendBack = new ResponseCreator(ResponseCreator.ResponseType.OK);
            } else {
                toSendBack = new ResponseCreator(ResponseCreator.ResponseType.ERROR);
                toSendBack.add(ResponseCreator.Fields.REASON, "Impossible to save data in the DB");
            }
        }
        return toSendBack;
    }
}
