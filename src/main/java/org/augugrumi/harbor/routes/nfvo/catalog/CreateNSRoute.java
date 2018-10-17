package org.augugrumi.harbor.routes.nfvo.catalog;

import org.augugrumi.harbor.persistence.Result;
import org.augugrumi.harbor.persistence.data.Data;
import org.augugrumi.harbor.persistence.data.DataCreator;
import org.augugrumi.harbor.persistence.data.NetworkService;
import org.augugrumi.harbor.persistence.data.VirtualNetworkFunction;
import org.augugrumi.harbor.routes.util.ParamConstants;
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

    @Override
    public Object handle(Request request, Response response) {

        LOG.debug(this.getClass().getSimpleName() + " called");
        try {

            JSONObject body = new JSONObject(request.body());
            JSONArray vnfsJson = body.getJSONArray(NetworkService.Fields.CHAIN);
            List<VirtualNetworkFunction> vnfs = new ArrayList<>();

            List<String> missing = new ArrayList<>();
            // Checking VNF validity
            for (Object vnfo : vnfsJson) {
                JSONObject vnfJson = (JSONObject) vnfo;
                VirtualNetworkFunction vnf = new VirtualNetworkFunction(vnfJson.getString(Data.Fields.ID));
                if (!vnf.isValid()) {
                    missing.add(vnf.getID());
                } else {
                    vnfs.add(vnf);
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
            }
            Result<NetworkService> ns = DataCreator.newNS(request.params(ParamConstants.ID), vnfs);
            if (ns.isSuccessful() && ns.getContent().isValid()) {
                return new ResponseCreator(ResponseCreator.ResponseType.OK);
            } else {
                return new ResponseCreator(ResponseCreator.ResponseType.ERROR)
                        .add(ResponseCreator.Fields.REASON, "Impossible to add the object in the database");
            }
        } catch (JSONException e) {
            return new ResponseCreator(ResponseCreator.ResponseType.ERROR)
                    .add(ResponseCreator.Fields.REASON, e.getMessage());
        }
    }
}
