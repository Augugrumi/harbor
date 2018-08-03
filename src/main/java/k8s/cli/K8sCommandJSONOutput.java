package k8s.cli;

import k8s.K8sCommandOutput;
import org.json.JSONObject;

public class K8sCommandJSONOutput extends K8sCommandOutput<JSONObject> {

    public K8sCommandJSONOutput(boolean success, JSONObject attachment) {
        super(success, attachment);
    }
}
