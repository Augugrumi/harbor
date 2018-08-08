package routes.vnf;

import k8s.K8sAPI;
import k8s.K8sFactory;
import org.json.JSONObject;
import org.slf4j.Logger;
import spark.Request;
import spark.Response;
import spark.Route;
import util.ConfigManager;

import java.io.File;

public class VnfLauncherRoute implements Route {

    final private static Logger LOG = ConfigManager.getConfig().getApplicationLogger(VnfLauncherRoute.class);

    @Override
    public Object handle(Request request, Response response) throws Exception {

        LOG.debug("VnfLauncherRoute called");

        // TODO consider the case where the filename is ending with .yml
        final String filename = Utils.validateFileName(request.params(":id"));
        final File yamlFile = new File(ConfigManager.getConfig().getYamlStorageFolder() + File.separator + filename);

        if (yamlFile.exists()) {

            LOG.info("Getting new launch request for " + filename);

            final K8sAPI api = K8sFactory.getCliAPI();
            return api.createFromYaml(yamlFile.toURI().toURL(), res -> res.getAttachment().toString());
        } else {
            final JSONObject toSendBack = new JSONObject();
            toSendBack.put("result", "error");
            toSendBack.put("reason", "The requested YAML doesn't exist");
            return toSendBack;
        }
    }
}
