package routes.vnf;

import org.slf4j.Logger;
import routes.util.ResponseCreator;
import spark.Request;
import spark.Response;
import spark.Route;
import util.ConfigManager;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * The route returns a list of images currently uploaded in Harbor
 */
public class ListImagesRoute implements Route {

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

        final File folder = new File(ConfigManager.getConfig().getYamlStorageFolder());
        final File[] listOfFiles = folder.listFiles();
        final List<String> images = new ArrayList<>();
        if (listOfFiles != null) {
            for (File f : listOfFiles) {
                if (f.isFile() && f.getName().matches(".*[.]ya?ml")) {
                    images.add(f.getName());
                }
            }
        }
        final ResponseCreator toSendBack = new ResponseCreator(ResponseCreator.ResponseType.OK);
        toSendBack.add(ResponseCreator.Fields.CONTENT, images);
        return toSendBack;
    }

}
