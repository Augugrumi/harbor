package routes.vnf;

import k8s.K8sAPI;
import k8s.K8sFactory;
import org.json.JSONObject;
import org.slf4j.Logger;
import spark.Request;
import spark.Response;
import spark.Route;
import util.ConfigManager;

import java.net.URL;

public class LauncherRoute implements Route {

    final private static Logger LOG = ConfigManager.getConfig().getApplicationLogger(LauncherRoute.class);

    @Override
    public Object handle(Request request, Response response) throws Exception {

        LOG.debug("LauncherRoute called ");

        // TODO create a parser family based on the type of data sent. For the moment, we just assume yaml is sent
        /*String body = request.body();

        Yaml yaml = new Yaml();

        yaml.load(body);
        yaml.setName(request.params(":id"));

        return yaml.dump(yaml);*/

        /*ApiClient client = Config.defaultClient();
        client.setBasePath(ConfigManager.getConfig().getFullKubernetesAddress());
        Configuration.setDefaultApiClient(client);

        /*ApiClient client = Config.defaultClient();
        client.setBasePath("https://kubernetes.default");
        Configuration.setDefaultApiClient(client);

        CoreV1Api api = new CoreV1Api();
        V1PodList list =
                api.listPodForAllNamespaces(null, null, null, null, null, null, null, null, null);
        for (V1Pod item : list.getItems()) {
            LOG.info(item.getMetadata().getName());
        }*/

        K8sAPI api = K8sFactory.getCliAPI();
        JSONObject toSendBack = new JSONObject();
        toSendBack.put("result", "ok");

        return api.createFromYaml(new URL("/home/centos/busyboxplus.yaml"), res -> res.getAttachment().toString());
    }
}
