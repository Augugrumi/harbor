package routes.vnf;

import org.slf4j.Logger;
import routes.util.FileNameUtils;
import routes.util.ResponseCreator;
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

        LOG.debug(this.getClass().getSimpleName() + " called");

        // TODO create a parser family based on the type of data sent. For the moment, we just assume yaml is sent
        final File yamlFolder = new File(ConfigManager.getConfig().getYamlStorageFolder());

        if (!yamlFolder.exists()) {
            if (!yamlFolder.mkdirs()) {
                throw new IOException("Impossible to create YAML storage folder. Check your filesystem permissions");
            }
        }

        final String filename = FileNameUtils.validateFileName(request.params(":id"));
        final File yamlFile = new File(ConfigManager.getConfig().getYamlStorageFolder() + File.separator + filename);

        ResponseCreator toSendBack;

        // TODO we need to validate this YAML before adding it!!
        if (yamlFile.createNewFile()) {

            FileOutputStream yamlToSave = new FileOutputStream(yamlFile);
            yamlToSave.write(request.bodyAsBytes());
            yamlToSave.flush();
            yamlToSave.close();

            LOG.info("Creation for " + filename + " completed");

            toSendBack = new ResponseCreator(ResponseCreator.ResponseType.OK);
        } else {
            toSendBack = new ResponseCreator(ResponseCreator.ResponseType.ERROR);
            toSendBack.add(ResponseCreator.Fields.REASON, "A YAML with the same key already exists");
        }
        return toSendBack;
    }
}
