package org.augugrumi.harbor.routes.nfvo.catalog;

import org.augugrumi.harbor.persistence.Persistence;
import org.augugrumi.harbor.persistence.PersistenceFactory;
import org.augugrumi.harbor.persistence.Result;
import org.augugrumi.harbor.util.ConfigManager;
import org.slf4j.Logger;
import routes.util.ResponseCreator;
import spark.Request;
import spark.Response;
import spark.Route;

import java.util.ArrayList;
import java.util.List;

import static org.augugrumi.harbor.persistence.Costants.VNF_HOME;

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
        LOG.debug(this.getClass().getSimpleName() + " called");
        final Persistence db = PersistenceFactory.getFSPersistence(VNF_HOME);
        ResponseCreator toSendBack;

        List<Result<String>> res = db.get();
        List<String> vnfNames = new ArrayList<>();
        for (final Result<String> r : res) {
            if (r.isSuccessful()) {
                vnfNames.add(r.getContent());
            }
        }
        toSendBack = new ResponseCreator(ResponseCreator.ResponseType.OK);
        toSendBack.add(ResponseCreator.Fields.CONTENT, vnfNames);
        return toSendBack;
    }

}
