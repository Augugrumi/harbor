package k8s;

public interface K8sResultConverter {

    Object convert(K8sCommandOutput res);
}
