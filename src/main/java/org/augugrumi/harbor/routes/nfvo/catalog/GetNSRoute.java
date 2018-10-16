package org.augugrumi.harbor.routes.nfvo.catalog;

import org.augugrumi.harbor.persistence.Persistence;
import org.augugrumi.harbor.persistence.PersistenceRetriever;
import org.augugrumi.harbor.persistence.Result;
import org.augugrumi.harbor.routes.util.RequestQuery;
import org.augugrumi.harbor.util.ConfigManager;
import org.json.JSONArray;
import org.slf4j.Logger;
import routes.util.ResponseCreator;
import spark.Request;
import spark.Response;
import spark.Route;

import static org.augugrumi.harbor.routes.util.ParamConstants.ID;

public class GetNSRoute implements Route {

    private static final Logger LOG = ConfigManager.getConfig().getApplicationLogger(GetNSRoute.class);

    @Override
    public Object handle(Request request, Response response) throws Exception {
        LOG.debug(this.getClass().getSimpleName() + " called");
        final Persistence nsDb = PersistenceRetriever.getNSDb();
        final Result qRes = nsDb.get(new RequestQuery(ID, request));

        if (qRes.isSuccessful()) {
            ResponseCreator ok = new ResponseCreator(ResponseCreator.ResponseType.OK);
            ok.add(ResponseCreator.Fields.CONTENT, new JSONArray(qRes.getContent().toString()).toString());
            return ok;
        } else {
            ResponseCreator err = new ResponseCreator(ResponseCreator.ResponseType.ERROR);
            err.add(ResponseCreator.Fields.REASON, "The requested element was not found in the DB");
            return err;
        }
    }
}
