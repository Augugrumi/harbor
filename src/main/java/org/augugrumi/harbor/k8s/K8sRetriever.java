package org.augugrumi.harbor.k8s;

public class K8sRetriever {

    private K8sRetriever() {
    }

    public static K8sAPI getK8sAPI() {
        return K8sFactory.getCliAPI();
    }
}
