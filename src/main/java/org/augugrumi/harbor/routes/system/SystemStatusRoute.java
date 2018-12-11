package org.augugrumi.harbor.routes.system;

import org.augugrumi.harbor.orchestration.Orchestrator;
import org.augugrumi.harbor.orchestration.OrchestratorRetriever;
import org.augugrumi.harbor.routes.util.Errors;
import org.augugrumi.harbor.util.ConfigManager;
import org.json.JSONObject;
import org.slf4j.Logger;
import routes.util.ResponseCreator;
import spark.Request;
import spark.Response;
import spark.Route;

import static org.augugrumi.harbor.orchestration.Orchestrator.ComponentRole.*;

public class SystemStatusRoute implements Route {

    private final static Logger LOG = ConfigManager.getConfig().getApplicationLogger(SystemStatusRoute.class);

    private static String ok2up(boolean status) {
        return status ? "up" : "degraded";
    }

    @Override
    public Object handle(Request request, Response response) {

        try {
            JSONObject res = new JSONObject();
            Orchestrator orchestrator = OrchestratorRetriever.getK8sOrchestrator();
            res.put(INGRESS, ok2up(orchestrator.isHealthy(INGRESS)));
            res.put(EGRESS, ok2up(orchestrator.isHealthy(EGRESS)));
            res.put(ROUTE_CONTROLLER, ok2up(orchestrator.isHealthy(ROUTE_CONTROLLER)));

            return new ResponseCreator(ResponseCreator.ResponseType.OK)
                    .add(ResponseCreator.Fields.CONTENT, res);
        } catch (Exception e) {
            LOG.error(Errors.NO_STATS);
            e.printStackTrace();
            return new ResponseCreator(ResponseCreator.ResponseType.ERROR)
                    .add(ResponseCreator.Fields.REASON, Errors.NO_STATS);
        }
    }
}
