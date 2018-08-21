package k8s.exceptions;

/**
 * Exception specifying and error regarding the initialization of a Kubernetes resources, such as login, kubernetes
 * reachability, and so on
 */
public class K8sInitFailureException extends K8sException {

    public K8sInitFailureException(String message) {
        super(message);
    }

    public K8sInitFailureException() {
        super();
    }
}
