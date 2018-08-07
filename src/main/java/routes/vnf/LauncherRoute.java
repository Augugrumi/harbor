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
import java.io.FileOutputStream;
import java.io.IOException;

public class LauncherRoute implements Route {

    final private static Logger LOG = ConfigManager.getConfig().getApplicationLogger(LauncherRoute.class);

    @Override
    public Object handle(Request request, Response response) throws Exception {

        LOG.debug("LauncherRoute called ");

        // TODO create a parser family based on the type of data sent. For the moment, we just assume yaml is sent
        final File yamlFolder = new File(ConfigManager.getConfig().getYamlStorageFolder());

        if (!yamlFolder.exists()) {
            if (!yamlFolder.mkdirs()) {
                throw new IOException("Impossible to create YAML storage folder. Check your filesystem permissions");
            }
        }

        // TODO consider the case where the filename is ending with .yml
        final String filename = request.params(":id").endsWith(".yaml") ?
                request.params(":id") : request.params(":id") + ".yaml";
        final File yamlFile = new File(ConfigManager.getConfig().getYamlStorageFolder() + File.separator + filename);

        // TODO we need to validate this YAML before executing it!!
        if (yamlFile.createNewFile()) {

            FileOutputStream yamlToSave = new FileOutputStream(yamlFile);
            yamlToSave.write(request.bodyAsBytes());
            yamlToSave.flush();
            yamlToSave.close();

            LOG.info("Getting new launch request for " + filename);

            final K8sAPI api = K8sFactory.getCliAPI();
            return api.createFromYaml(yamlFile.toURI().toURL(), res -> res.getAttachment().toString());
        } else {
            final JSONObject toSendBack = new JSONObject();
            toSendBack.put("result", "error");
            toSendBack.put("reason", "A YAML with the same key already exists");
            return toSendBack;
        }
    }
}
