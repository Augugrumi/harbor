package org.augugrumi.harbor.routes.nfvo.catalog;

import org.augugrumi.harbor.persistence.data.NetworkService;
import org.augugrumi.harbor.routes.util.ParamConstants;
import org.augugrumi.harbor.util.ConfigManager;
import org.slf4j.Logger;
import routes.util.ResponseCreator;
import spark.Request;
import spark.Response;
import spark.Route;

public class GetNSRoute implements Route {

    private static final Logger LOG = ConfigManager.getConfig().getApplicationLogger(GetNSRoute.class);

    @Override
    public Object handle(Request request, Response response) {
        LOG.debug(this.getClass().getSimpleName() + " called");
        NetworkService ns = new NetworkService(request.params(ParamConstants.ID));
        return new ResponseCreator(ResponseCreator.ResponseType.OK).add(ResponseCreator.Fields.CONTENT, ns.toJson());
    }
}
