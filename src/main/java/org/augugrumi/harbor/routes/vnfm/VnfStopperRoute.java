package org.augugrumi.harbor.routes.vnfm;

import org.augugrumi.harbor.k8s.K8sAPI;
import org.augugrumi.harbor.k8s.K8sRetriever;
import org.augugrumi.harbor.persistence.Persistence;
import org.augugrumi.harbor.persistence.PersistenceRetriever;
import org.augugrumi.harbor.persistence.Query;
import org.augugrumi.harbor.persistence.data.VirtualNetworkFunction;
import org.augugrumi.harbor.routes.util.RequestQuery;
import org.augugrumi.harbor.routes.util.exceptions.NoSuchNetworkComponentException;
import org.augugrumi.harbor.util.ConfigManager;
import org.augugrumi.harbor.util.FileUtils;
import org.slf4j.Logger;
import routes.util.ResponseCreator;
import spark.Request;
import spark.Response;
import spark.Route;

import static org.augugrumi.harbor.routes.util.ParamConstants.ID;

/**
 * Route stopping the selected YAML id. Please note that if you've previously updated the YAML with another one, this
 * method could fail.
 */
public class VnfStopperRoute implements Route {

    final private static Logger LOG = ConfigManager.getConfig().getApplicationLogger(VnfStopperRoute.class);

    /**
     * The route handler. It deletes and frees the resources described in the selected YAML from the Kubernetes cluster
     *
     * @param request  the data sent from the client
     * @param response optional fields to set in the reply
     * @return If the operation ends successfully, then a JSON with a "result" filed with "ok" is returned. In that
     * JSON, an additional filed, "content", is present, although the content it's not consistent and may vary.
     * The operation could end in an error too, if the requested YAML doesn't exist. In that case, the following
     * JSON is returned:
     * <pre>
     *     {
     *         "result": "error",
     *         "reason": "The requested VNF doesn't exist"
     *     }
     * </pre>
     * @throws Exception an exception is thrown when an I/O operation reading the YAML configuration file fails,
     *                   resulting in a 500 Internal Server Error
     */
    @Override
    public Object handle(Request request, Response response) throws Exception {

        LOG.debug(this.getClass().getSimpleName() + " called");
        final Persistence db = PersistenceRetriever.getVnfDb();
        final Query q = new RequestQuery(ID, request);
        final K8sAPI api = K8sRetriever.getK8sAPI();

        try {
            VirtualNetworkFunction vnf = new VirtualNetworkFunction(request.params(ID));
            return api.deleteFromYaml(
                    FileUtils.createTmpFile("hrbr", ".yaml", vnf.getDefinition()).toURI().toURL(),
                    res -> res.getAttachment().toString());
        } catch (NoSuchNetworkComponentException e) {
            return new ResponseCreator(ResponseCreator.ResponseType.ERROR)
                    .add(ResponseCreator.Fields.REASON, e.getMessage());
        }
    }
}
