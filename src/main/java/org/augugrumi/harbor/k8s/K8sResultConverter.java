package org.augugrumi.harbor.k8s;

/**
 * Output converter. It allows to convert the output coming from a library or API call into something else, i.e. a
 * JSONObject or other common structure. This allows an easy transition from an output format to another one, especially
 * when there is the need to have a standardized output between different API implementations
 *
 * @see K8sCommandOutput
 * @see K8sAPI
 */
public interface K8sResultConverter {

    /**
     * Convert the result to something else
     * @param res the API result, encapsulated inside a generic K8sCommandOutput
     * @return an object that can be literally anything: this design choice allows to convert the API output to a
     * specific format or to another class, when needed
     */
    Object convert(K8sCommandOutput res);
}
