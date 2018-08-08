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

public class VnfStopperRoute implements Route {

    final private static Logger LOG = ConfigManager.getConfig().getApplicationLogger(VnfStopperRoute.class);

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
