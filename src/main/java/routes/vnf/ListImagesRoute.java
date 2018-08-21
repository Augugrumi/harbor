package routes.vnf;

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

    /**
     * The request handler lists all the file contained in the YAML designed folder and returns them
     *
     * @param request  the data sent from the client
     * @param response optional fields to set in the reply
     * @return A JSON Array containing all the images names
     */
    @Override
    public Object handle(Request request, Response response) {
        File folder = new File(ConfigManager.getConfig().getYamlStorageFolder());
        File[] listOfFiles = folder.listFiles();
        List<String> images = new ArrayList<>();
        if (listOfFiles != null) {
            for (File f : listOfFiles) {
                if (f.isFile() && f.getName().matches(".*[.]ya?ml")) {
                    images.add(f.getName());
                }
            }
        }
        ResponseCreator toSendBack = new ResponseCreator(ResponseCreator.ResponseType.OK);
        toSendBack.add(ResponseCreator.Fields.CONTENT, images);
        return toSendBack;
    }

}
