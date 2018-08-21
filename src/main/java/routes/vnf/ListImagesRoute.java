package routes.vnf;

import routes.util.ResponseCreator;
import spark.Request;
import spark.Response;
import spark.Route;
import util.ConfigManager;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ListImagesRoute implements Route {

    @Override
    public Object handle(Request request, Response response) throws Exception {
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
