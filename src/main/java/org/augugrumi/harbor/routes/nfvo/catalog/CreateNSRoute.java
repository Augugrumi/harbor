package org.augugrumi.harbor.routes.nfvo.catalog;

import org.augugrumi.harbor.persistence.Persistence;
import org.augugrumi.harbor.persistence.PersistenceRetriever;
import org.augugrumi.harbor.persistence.Query;
import org.augugrumi.harbor.persistence.Result;
import org.augugrumi.harbor.routes.nfvo.definition.NSConstants;
import org.augugrumi.harbor.routes.util.ParamConstants;
import org.augugrumi.harbor.routes.util.RequestQuery;
import org.augugrumi.harbor.util.ConfigManager;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import routes.util.ResponseCreator;
import spark.Request;
import spark.Response;
import spark.Route;

import java.util.ArrayList;
import java.util.List;

public class CreateNSRoute implements Route {

    private final static Logger LOG = ConfigManager.getConfig().getApplicationLogger(CreateNSRoute.class);

    private ResponseCreator dbErr() {
        ResponseCreator err = new ResponseCreator(ResponseCreator.ResponseType.ERROR);
        err.add(ResponseCreator.Fields.REASON, "Impossible to connect to the database");
        return err;
    }

    @Override
    public Object handle(Request request, Response response) throws Exception {

        LOG.debug(this.getClass().getSimpleName() + " called");
        final Persistence vnfDb = PersistenceRetriever.getVnfDb();
        final Persistence nsDb = PersistenceRetriever.getNSDb();
        final Query nsElement = new RequestQuery(ParamConstants.ID, request);
        final Result<Boolean> query = nsDb.exists(nsElement);
        if (query.isSuccessful()) {
            if (!query.getContent()) {
                try {
                    JSONArray body = new JSONArray(request.body());
                    List<Query> toJoin = new ArrayList<>();
                    for (final Object o : body) {
                        JSONObject element = (JSONObject) o;
                        String id = element.getString(NSConstants.ID);
                        Query q = new Query() {
                            @Override
                            public String getId() {
                                return id;
                            }

                            @Override
                            public String getContent() {
                                return null;
                            }
                        };
                        toJoin.add(q);
                    }
                    Result<List<Boolean>> vnfJoin = vnfDb.exists(toJoin);
                    List<String> missing = new ArrayList<>();
                    if (vnfJoin.isSuccessful()) {
                        List<Boolean> content = vnfJoin.getContent();
                        for (int i = 0; i < content.size(); i++) {
                            if (!content.get(i)) {
                                missing.add(body.getJSONObject(i).getString(NSConstants.ID));
                            }
                        }

                        if (missing.size() != 0) {
                            ResponseCreator err = new ResponseCreator(ResponseCreator.ResponseType.ERROR);
                            StringBuilder listOfMissing = new StringBuilder();
                            for (final String m : missing) {
                                listOfMissing.append(m);
                                listOfMissing.append(" ");
                            }
                            err.add(ResponseCreator.Fields.REASON, "The following VNF were not found: "
                                    + listOfMissing.toString().trim());
                            return err;
                        } else {
                            Result insert = nsDb.save(nsElement);
                            if (insert.isSuccessful()) {
                                return new ResponseCreator(ResponseCreator.ResponseType.OK);
                            } else {
                                return dbErr();
                            }
                        }
                    } else {
                        return dbErr();
                    }
                } catch (JSONException e) {
                    ResponseCreator error = new ResponseCreator(ResponseCreator.ResponseType.ERROR);
                    error.add(ResponseCreator.Fields.REASON, e.getMessage());
                    return error;
                } catch (ClassCastException e) {
                    ResponseCreator error = new ResponseCreator(ResponseCreator.ResponseType.ERROR);
                    error.add(ResponseCreator.Fields.REASON, "VNF element not well defined. Are you sure it" +
                            " is a proper JSON object?");
                    return error;
                }
            } else {
                ResponseCreator err = new ResponseCreator(ResponseCreator.ResponseType.ERROR);
                err.add(ResponseCreator.Fields.REASON, "NS already existing");
                return err;
            }
        } else {
            return dbErr();
        }
    }
}
