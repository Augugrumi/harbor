package org.augugrumi.harbor.routes.vnfm;

import k8s.K8sAPI;
import k8s.K8sFactory;
import org.augugrumi.harbor.util.ConfigManager;
import org.slf4j.Logger;
import routes.util.FileNameUtils;
import routes.util.ResponseCreator;
import spark.Request;
import spark.Response;
import spark.Route;

import java.io.File;

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
     *          "reason": "The requested YAML doesn't exist"
     *      }
     * </pre>
     * @throws Exception an exception is thrown if there is an error reading the YAML configuration or there is an error
     *                   talking with Kubernetes
     */
    @Override
    public Object handle(Request request, Response response) throws Exception {

        LOG.debug(this.getClass().getSimpleName() + " called");

        // TODO consider the case where the filename is ending with .yml
        final String filename = FileNameUtils.validateFileName(request.params(":id"));
        final File yamlFile = new File(ConfigManager.getConfig().getYamlStorageFolder() + File.separator + filename);

        if (yamlFile.exists()) {

            LOG.info("Getting new launch request for " + filename);

            final K8sAPI api = K8sFactory.getCliAPI();
            return api.createFromYaml(yamlFile.toURI().toURL(), res -> res.getAttachment().toString());
        } else {
            final ResponseCreator toSendBack = new ResponseCreator(ResponseCreator.ResponseType.ERROR);
            toSendBack.add(ResponseCreator.Fields.REASON, "The requested YAML doesn't exist");
            return toSendBack;
        }
    }
}
