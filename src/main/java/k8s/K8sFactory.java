package k8s;

import k8s.cli.K8sCli;
import k8s.lib.K8sJavaAPI;

public class K8sFactory {

    private K8sFactory() {
    }

    public static K8sAPI getCliAPI() {
        return new K8sCli();
    }

    public static K8sAPI getLibAPI() {
        return new K8sJavaAPI();
    }
}
