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

/**
 * This route adds to the internal database a new Kubernetes YAML. Note that you have to provide an unique id to this
 * API: this id will be used internally to distinguish various YAMLs. You can't add two YAML with the same ids,
 * otherwise you'll get an error.
 */
public class CreateVnfRoute implements Route {

    final private static Logger LOG = ConfigManager.getConfig().getApplicationLogger(CreateVnfRoute.class);

    /**
     * The request handler. It checks that the id is unique and then it adds it to the database.
     *
     * @param request  the data sent from the client
     * @param response optional fields to set in the reply
     * @return a valid JSON: <pre>
     *     {
     *         "result": "ok"
     *     }
     * </pre>
     * when the operation is successful, otherwise if the id is already in use it returns: <pre>
     *     {
     *         "result": "error",
     *         "reason": "A YAML with the same key already exists"
     *     }
     * </pre>
     * @throws Exception when an internal error occurs
     */
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
