package k8s.lib;

import k8s.K8sAPI;
import k8s.K8sResultConverter;
import k8s.exceptions.K8sException;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.net.URL;

public class K8sJavaAPI implements K8sAPI {

    public K8sJavaAPI() {
        throw new NotImplementedException();
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
