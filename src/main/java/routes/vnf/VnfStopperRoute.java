package routes.vnf;

import k8s.K8sAPI;
import k8s.K8sFactory;
import org.slf4j.Logger;
import routes.util.FileNameUtils;
import routes.util.ResponseCreator;
import spark.Request;
import spark.Response;
import spark.Route;
import util.ConfigManager;

import java.io.File;

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
     *         "reason": "The requested YAML doesn't exist"
     *     }
     * </pre>
     * @throws Exception an exception is thrown when an I/O operation reading the YAML configuration file fails,
     *                   resulting in a 500 Internal Server Error
     */
    @Override
    public Object handle(Request request, Response response) throws Exception {

        LOG.debug(this.getClass().getSimpleName() + " called");

        // TODO consider the case where the filename is ending with .yml
        final String filename = FileNameUtils.validateFileName(request.params(":id"));
        final File yamlFile = new File(ConfigManager.getConfig().getYamlStorageFolder() + File.separator + filename);

        if (yamlFile.exists()) {

            LOG.info("Getting new stop request for " + filename);

            final K8sAPI api = K8sFactory.getCliAPI();
            return api.deleteFromYaml(yamlFile.toURI().toURL(), res -> res.getAttachment().toString());
        } else {
            final ResponseCreator toSendBack = new ResponseCreator(ResponseCreator.ResponseType.ERROR);
            toSendBack.add(ResponseCreator.Fields.REASON, "The requested YAML doesn't exist");
            return toSendBack;
        }
    }
}
