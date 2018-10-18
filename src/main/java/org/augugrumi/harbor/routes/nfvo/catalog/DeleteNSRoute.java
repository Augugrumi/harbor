package org.augugrumi.harbor.routes.nfvo.catalog;

import org.augugrumi.harbor.persistence.data.DataWizard;
import org.augugrumi.harbor.routes.util.Errors;
import org.augugrumi.harbor.routes.util.ParamConstants;
import org.augugrumi.harbor.util.ConfigManager;
import org.slf4j.Logger;
import routes.util.ResponseCreator;
import spark.Request;
import spark.Response;
import spark.Route;

public class DeleteNSRoute implements Route {

    private static final Logger LOG = ConfigManager.getConfig().getApplicationLogger(DeleteNSRoute.class);

    @Override
    public Object handle(Request request, Response response) {

        LOG.debug(this.getClass().getSimpleName() + " called");
        if (DataWizard.deleteNS(request.params(ParamConstants.ID))) {
            return new ResponseCreator(ResponseCreator.ResponseType.OK);
        } else {
            return new ResponseCreator(ResponseCreator.ResponseType.ERROR)
                    .add(ResponseCreator.Fields.REASON, Errors.DB_REMOVE);
        }
    }
}
