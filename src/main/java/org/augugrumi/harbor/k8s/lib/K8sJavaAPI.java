package org.augugrumi.harbor.k8s.lib;

import org.augugrumi.harbor.k8s.K8sAPI;
import org.augugrumi.harbor.k8s.K8sResultConverter;
import org.augugrumi.harbor.k8s.exceptions.K8sException;

import java.net.URL;

/**
 * API implementation that uses the <a href="https://github.com/kubernetes-client/java">official java API for
 * Kubernetes</a>
 */
public class K8sJavaAPI implements K8sAPI {

    /**
     * Since this class is still not implemented, it's constructor will always return a RuntimeException
     */
    public K8sJavaAPI() {
        throw new RuntimeException();
    }

    @Override
    public Object createFromYaml(URL pathToFile, K8sResultConverter converter) throws K8sException {
        return null;
    }

    @Override
    public Object deleteFromYaml(URL pathToFile, K8sResultConverter converter) throws K8sException {
        return null;
    }
}
