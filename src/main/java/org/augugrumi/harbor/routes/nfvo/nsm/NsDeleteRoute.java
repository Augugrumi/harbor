package org.augugrumi.harbor.routes.nfvo.nsm;

import org.augugrumi.harbor.persistence.data.DataWizard;
import org.augugrumi.harbor.routes.util.Errors;
import org.augugrumi.harbor.routes.util.ParamConstants;
import org.augugrumi.harbor.util.ConfigManager;
import org.slf4j.Logger;
import routes.util.ResponseCreator;
import spark.Request;
import spark.Response;
import spark.Route;

public class NsDeleteRoute implements Route {

    private final static Logger LOG = ConfigManager.getConfig().getApplicationLogger(NsDeleteRoute.class);

    @Override
    public Object handle(Request request, Response response) {

        // TODO VNF reference counting?
        LOG.debug(this.getClass().getSimpleName() + " called");
        final boolean nsRes = DataWizard.deleteNS(request.params(ParamConstants.ID));
        if (nsRes) {
            return new ResponseCreator(ResponseCreator.ResponseType.OK);
        } else {
            return new ResponseCreator(ResponseCreator.ResponseType.ERROR)
                    .add(ResponseCreator.Fields.REASON, Errors.DB_REMOVE);
        }
    }
}
