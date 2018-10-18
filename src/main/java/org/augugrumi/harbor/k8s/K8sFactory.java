package org.augugrumi.harbor.k8s;

import org.augugrumi.harbor.k8s.cli.K8sCli;
import org.augugrumi.harbor.k8s.lib.K8sJavaAPI;

/**
 * Class implementing the Abstract Factory design pattern. It return the requested type of API client
 */
public class K8sFactory {

    private K8sFactory() {
    }

    /**
     * Getter method for obtaining the API implemented using CLI calls
     *
     * @return K8sCli, that uses CLI calls to implement Kubernetes API calls. This implementation is intrinsically
     * prone to errors because it relies on output parsing, and not type matching, although it works well when using
     * the backend outside the Kubernetes environment.
     * @see K8sCli
     */
    public static K8sAPI getCliAPI() {
        return new K8sCli();
    }

    /**
     * Getter method for obtaining the API calls implemented using the official library for Kubernetes
     * @return K8sJavaAPI, that uses the official Kubernetes API to implements API calls. At the moment this class is
     * not implemented, and it will result in a RuntimeException
     * @see RuntimeException
     * @see K8sJavaAPI
     */
    public static K8sAPI getLibAPI() {
        return new K8sJavaAPI();
    }
}
