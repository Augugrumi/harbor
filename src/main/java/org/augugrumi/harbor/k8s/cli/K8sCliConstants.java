package org.augugrumi.harbor.k8s.cli;

interface K8sCliConstants {

    String NAMESPACE_FLAG = "-n";
    String OUTPUT_FLAG = "-o";
    String FILE_FLAG = "-f";

    String CREATE = "create";
    String DELETE = "delete";
    String SERVICE = "service";

    String STATUS = "status";
    String GET = "get";

    String[] JSON_OPTION = {OUTPUT_FLAG, "json"};

    String KUBECTL_DEFAULT_NAME = "kubectl";
    String KUBELET_DEFAULT_NAME = "kubelet";
}
