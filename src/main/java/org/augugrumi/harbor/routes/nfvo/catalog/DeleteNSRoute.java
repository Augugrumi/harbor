package org.augugrumi.harbor.routes.nfvo.catalog;

import org.augugrumi.harbor.persistence.Persistence;
import org.augugrumi.harbor.persistence.PersistenceRetriever;
import org.augugrumi.harbor.persistence.Query;
import org.augugrumi.harbor.persistence.Result;
import org.augugrumi.harbor.routes.util.ParamConstants;
import org.augugrumi.harbor.routes.util.RequestQuery;
import org.augugrumi.harbor.util.ConfigManager;
import org.slf4j.Logger;
import routes.util.ResponseCreator;
import spark.Request;
import spark.Response;
import spark.Route;

import static org.augugrumi.harbor.routes.util.ErrorHandling.dbErr;

public class DeleteNSRoute implements Route {

    private static final Logger LOG = ConfigManager.getConfig().getApplicationLogger(DeleteNSRoute.class);

    @Override
    public Object handle(Request request, Response response) throws Exception {

        final Persistence nsDb = PersistenceRetriever.getNSDb();
        final Query nsElement = new RequestQuery(ParamConstants.ID, request);
        final Result<Boolean> exist = nsDb.exists(nsElement);
        if (exist.isSuccessful()) {
            if (exist.getContent()) {
                final Result<Boolean> query = nsDb.delete(nsElement);
                if (query.isSuccessful()) {
                    if (query.getContent()) {
                        return new ResponseCreator(ResponseCreator.ResponseType.OK);
                    } else {
                        return new ResponseCreator(ResponseCreator.ResponseType.ERROR)
                                .add(ResponseCreator.Fields.REASON, "Failure deleting from DB");
                    }
                } else {
                    return dbErr();
                }
            } else {
                return new ResponseCreator(ResponseCreator.ResponseType.ERROR)
                        .add(ResponseCreator.Fields.REASON, "The element you are trying to delete it " +
                                "doesn't exist");
            }
        } else {
            return dbErr();
        }
    }
}
