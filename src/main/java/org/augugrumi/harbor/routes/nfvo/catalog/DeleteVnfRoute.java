package org.augugrumi.harbor.routes.nfvo.catalog;

import org.augugrumi.harbor.util.ConfigManager;
import org.slf4j.Logger;
import routes.util.FileNameUtils;
import routes.util.ResponseCreator;
import spark.Request;
import spark.Response;
import spark.Route;

import java.io.File;

/**
 * Deletes a YAML configuration given the right id. The operation fails if a bogus id is provided, or if the backend
 * hasn't the I/O possibility to do this
 */
public class DeleteVnfRoute implements Route {

    final private static Logger LOG = ConfigManager.getConfig().getApplicationLogger(DeleteVnfRoute.class);

    /**
     * It handles the delete operation
     *
     * @param request  the data sent from the client
     * @param response optional fields to set in the reply
     * @return a valid JSON: <pre>
     *     {
     *         "result": "ok"
     *     }
     * </pre>
     * when the operation is successful, otherwise if the backend is not able to delete the file it returns: <pre>
     *     {
     *         "result": "error",
     *         "reason": "Failed to delete the file"
     *     }
     * </pre>
     * or, if the id doesn't exists: <pre>
     *     {
     *         "result": "error",
     *         "reason": "The requested YAML doesn't exist"
     *     }
     * </pre>
     * <p>
     * This method never occur in an 500 Internal Error.
     */
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
