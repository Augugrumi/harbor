package routes.vnf;

import org.json.JSONObject;
import org.slf4j.Logger;
import spark.Request;
import spark.Response;
import spark.Route;
import util.ConfigManager;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class CreateVnfRoute implements Route {

    final private static Logger LOG = ConfigManager.getConfig().getApplicationLogger(CreateVnfRoute.class);

    @Override
    public Object handle(Request request, Response response) throws Exception {

        LOG.debug("CreateVnfRoute called");

        // TODO create a parser family based on the type of data sent. For the moment, we just assume yaml is sent
        final File yamlFolder = new File(ConfigManager.getConfig().getYamlStorageFolder());

        if (!yamlFolder.exists()) {
            if (!yamlFolder.mkdirs()) {
                throw new IOException("Impossible to create YAML storage folder. Check your filesystem permissions");
            }
        }

        final String filename = Utils.validateFileName(request.params(":id"));
        final File yamlFile = new File(ConfigManager.getConfig().getYamlStorageFolder() + File.separator + filename);

        final JSONObject toSendBack = new JSONObject();

        if (yamlFile.createNewFile()) {

            FileOutputStream yamlToSave = new FileOutputStream(yamlFile);
            yamlToSave.write(request.bodyAsBytes());
            yamlToSave.flush();
            yamlToSave.close();

            LOG.info("Creation for " + filename + " completed");

            toSendBack.put("result", "ok");
        } else {
            toSendBack.put("result", "error");
            toSendBack.put("reason", "A YAML with the same key already exists");
        }
        return toSendBack;
    }
}
