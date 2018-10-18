package org.augugrumi.harbor.k8s.cli;

import org.augugrumi.harbor.k8s.K8sCommandOutput;
import org.json.JSONObject;

/**
 * This class specify the type of the command output to be a JSONObject. So K8sCommandJSONOutput return the output
 * encapsulated inside a JSONObject
 *
 * @see K8sCommandOutput
 * @see K8sResultConverter
 */
public class K8sCommandJSONOutput extends K8sCommandOutput<JSONObject> {

    /**
     * Builds a new K8sCommandJSONOutput
     * @param success true if the command exited with status 0, otherwise false
     * @param attachment the JSONObject output
     */
    public K8sCommandJSONOutput(boolean success, JSONObject attachment) {
        super(success, attachment);
    }
}
