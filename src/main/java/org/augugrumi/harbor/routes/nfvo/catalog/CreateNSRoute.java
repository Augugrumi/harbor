package org.augugrumi.harbor.routes.nfvo.catalog;

import org.augugrumi.harbor.persistence.Result;
import org.augugrumi.harbor.persistence.data.Data;
import org.augugrumi.harbor.persistence.data.DataWizard;
import org.augugrumi.harbor.persistence.data.NetworkService;
import org.augugrumi.harbor.persistence.data.VirtualNetworkFunction;
import org.augugrumi.harbor.routes.util.Errors;
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

            // Checking VNF validity
            for (Object vnfo : vnfsJson) {
                JSONObject vnfJson = (JSONObject) vnfo;
                VirtualNetworkFunction vnf = new VirtualNetworkFunction(vnfJson.getString(Data.Fields.ID));
                vnfs.add(vnf);
            }
            Result<NetworkService> ns = DataWizard.newNS(request.params(ParamConstants.ID), vnfs);
            if (ns.isSuccessful() && ns.getContent().isValid()) {
                return new ResponseCreator(ResponseCreator.ResponseType.OK);
            } else {
                return new ResponseCreator(ResponseCreator.ResponseType.ERROR)
                        .add(ResponseCreator.Fields.REASON, Errors.DB_ADD);
            }
        } catch (JSONException e) {
            return new ResponseCreator(ResponseCreator.ResponseType.ERROR)
                    .add(ResponseCreator.Fields.REASON, e.getMessage());
        }
    }
}
