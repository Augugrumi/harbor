package org.augugrumi.harbor.routes.vnfm;

import k8s.K8sAPI;
import k8s.K8sFactory;
import org.augugrumi.harbor.persistence.Persistence;
import org.augugrumi.harbor.persistence.PersistenceRetriever;
import org.augugrumi.harbor.persistence.Query;
import org.augugrumi.harbor.persistence.Result;
import org.augugrumi.harbor.routes.util.RequestQuery;
import org.augugrumi.harbor.util.ConfigManager;
import org.augugrumi.harbor.util.FileUtils;
import org.slf4j.Logger;
import routes.util.ResponseCreator;
import spark.Request;
import spark.Response;
import spark.Route;

import static org.augugrumi.harbor.routes.util.ParamConstants.ID;

/**
 * VnfLauncherRoute launches existing YAML configuration in Kubernetes. The process of launching a YAML is asynchronous,
 * even though the process waits for the output to be returned. At the moment, the JSON gets returned in a
 * non-standardized way, but usually is always present the field "result", that can be "ok" or "error", as usual.
 * @see Process
 */
public class VnfLauncherRoute implements Route {

    final private static Logger LOG = ConfigManager.getConfig().getApplicationLogger(VnfLauncherRoute.class);

    /**
     * The method handling the request. It first searches for the YAML configuration to be present, then it try to
     * launch it in Kubernetes.
     *
     * @param request  the data sent from the client
     * @param response optional fields to set in the reply
     * @return If the operation is successful, a JSON with "ok" "result" field is returned.
     * If the YAML doesn't exist, the returned JSON is:
     * <pre>
     *      {
     *          "result": "error",
     *          "reason": "The requested VNF doesn't exist"
     *      }
     * </pre>
     * @throws Exception an exception is thrown if there is an error reading the YAML configuration or there is an error
     *                   talking with Kubernetes
     */
    @Override
    public Object handle(Request request, Response response) throws Exception {

        LOG.debug(this.getClass().getSimpleName() + " called");
        final Persistence db = PersistenceRetriever.getVnfDb();
        final Query q = new RequestQuery(ID, request);

        Result<String> dbRes = db.get(q);
        if (dbRes.isSuccessful()) {
            final K8sAPI api = K8sFactory.getCliAPI();
            return api.createFromYaml(
                    FileUtils.createTmpFile("hrbr", ".yaml", dbRes.getContent()).toURI().toURL(),
                    res -> res.getAttachment().toString());
        } else {
            final ResponseCreator toSendBack = new ResponseCreator(ResponseCreator.ResponseType.ERROR);
            toSendBack.add(ResponseCreator.Fields.REASON, "The requested VNF doesn't exist");
            return toSendBack;
        }
    }
}
