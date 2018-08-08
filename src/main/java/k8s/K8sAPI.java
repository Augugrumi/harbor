package k8s;

import k8s.exceptions.K8sException;

import java.io.IOException;
import java.net.URL;

public interface K8sAPI {

    Object createFromYaml(URL pathToFile, K8sResultConverter converter) throws K8sException, IOException;

    Object deleteFromYaml(URL pathToFile, K8sResultConverter converter) throws K8sException, IOException;
}
