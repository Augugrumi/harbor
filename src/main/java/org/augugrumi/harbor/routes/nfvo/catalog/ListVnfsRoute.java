package org.augugrumi.harbor.routes.nfvo.catalog;

import org.augugrumi.harbor.persistence.Persistence;
import org.augugrumi.harbor.persistence.PersistenceRetriever;
import org.augugrumi.harbor.persistence.Result;
import org.augugrumi.harbor.util.ConfigManager;
import org.json.JSONObject;
import org.slf4j.Logger;
import routes.util.ResponseCreator;
import spark.Request;
import spark.Response;
import spark.Route;

import java.util.ArrayList;
import java.util.List;

import static org.augugrumi.harbor.routes.util.ErrorHandling.dbErr;

/**
 * The route returns a list of images currently uploaded in Harbor
 */
public class ListVnfsRoute implements Route {

    final private static Logger LOG = ConfigManager.getConfig().getApplicationLogger(DeleteVnfRoute.class);

    /**
     * The request handler lists all the file contained in the YAML designed folder and returns them
     *
     * @param request  the data sent from the client
     * @param response optional fields to set in the reply
     * @return A JSON Array containing all the images names
     */
    @Override
    public Object handle(Request request, Response response) {
        // TODO is it worth to create a dedicate DataWizard method?
        LOG.debug(this.getClass().getSimpleName() + " called");
        final Persistence db = PersistenceRetriever.getVnfDb();
        ResponseCreator toSendBack;

        Result<List<JSONObject>> res = db.get();
        if (res.isSuccessful()) {
            List<String> vnfNames = new ArrayList<>();
            for (final JSONObject o : res.getContent()) {
                vnfNames.add(o.getString(Persistence.Fields.ID));
            }
            toSendBack = new ResponseCreator(ResponseCreator.ResponseType.OK);
            toSendBack.add(ResponseCreator.Fields.CONTENT, vnfNames);
            return toSendBack;
        } else {
            return dbErr();
        }
    }
}
