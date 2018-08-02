package routes.vnf;

import io.kubernetes.client.ApiClient;
import io.kubernetes.client.Configuration;
import io.kubernetes.client.apis.CoreV1Api;
import io.kubernetes.client.models.V1Pod;
import io.kubernetes.client.models.V1PodList;
import io.kubernetes.client.util.Config;
import org.json.JSONObject;
import org.slf4j.Logger;
import spark.Request;
import spark.Response;
import spark.Route;
import util.ConfigManager;

public class LauncherRoute implements Route {

    final private static Logger LOG = ConfigManager.getConfig().getApplicationLogger(LauncherRoute.class);

    @Override
    public Object handle(Request request, Response response) throws Exception {

        LOG.debug("LauncherRoute called ");

        // TODO create a parser family based on the type of data sent. For the moment, we just assume yalm is sent
        /*String body = request.body();

        Yaml yaml = new Yaml();

        yaml.load(body);
        yaml.setName(request.params(":id"));

        return yaml.dump(yaml);*/

        ApiClient client = Config.defaultClient();
        client.setBasePath(ConfigManager.getConfig().getFullKubernetesAddress());
        Configuration.setDefaultApiClient(client);

        CoreV1Api api = new CoreV1Api();
        V1PodList list =
                api.listPodForAllNamespaces(null, null, null, null, null, null, null, null, null);
        for (V1Pod item : list.getItems()) {
            LOG.info(item.getMetadata().getName());
        }

        JSONObject res = new JSONObject();
        res.put("result", "ok");

        return res;
    }
}
