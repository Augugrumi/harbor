package routes.vnf;

import org.slf4j.Logger;
import routes.util.FileNameUtils;
import routes.util.ResponseCreator;
import spark.Request;
import spark.Response;
import spark.Route;
import util.ConfigManager;

import java.io.File;

public class DeleteVnfRoute implements Route {

    final private static Logger LOG = ConfigManager.getConfig().getApplicationLogger(DeleteVnfRoute.class);

    @Override
    public Object handle(Request request, Response response) {

        LOG.debug(this.getClass().getSimpleName() + " called");

        final String filename = FileNameUtils.validateFileName(request.params(":id"));
        final File yamlToDelete = new File(ConfigManager.getConfig().getYamlStorageFolder() + File.separator + filename);
        final ResponseCreator toSendBack;

        if (yamlToDelete.exists()) {
            if (yamlToDelete.delete()) {
                toSendBack = new ResponseCreator(ResponseCreator.ResponseType.OK);
            } else {
                toSendBack = new ResponseCreator(ResponseCreator.ResponseType.ERROR);
                toSendBack.add(ResponseCreator.Fields.REASON, "Failed to delete the file");
            }
        } else {
            toSendBack = new ResponseCreator(ResponseCreator.ResponseType.ERROR);
            toSendBack.add(ResponseCreator.Fields.REASON, "The requested YAML doesn't exist");
        }

        return toSendBack;
    }
}
