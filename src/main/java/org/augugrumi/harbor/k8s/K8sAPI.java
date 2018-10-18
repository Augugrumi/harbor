package org.augugrumi.harbor.k8s;

import org.augugrumi.harbor.k8s.exceptions.K8sException;

import java.io.IOException;
import java.net.URL;

/**
 * K8sAPI describes the available operations in Kubernetes. In particular, it defines a common set of methods at which
 * all the different API implementations have to adhere.
 */
public interface K8sAPI {

    /**
     * Creates Kubernetes resources from a YAML file. The YAML has to be properly formatted, and Kubernetes has to be
     * ready to accepts jobs
     *
     * @param pathToFile an URL containing a valid path to the file (at the moment, only local files are supported)
     * @param converter  a result converter, that allows the API call result to be formatted as the user wishes
     * @return The class returns a generic Object that the user can downcast to what she prefers, since the result is
     * what K8sResultConverter.convert() returns.
     * @throws K8sException exception thrown if an operation with Kubernetes (e.g. resource creation, missing
     *                      permissions, etc) fails for whatever reason
     * @throws IOException exception thrown if an operation with the YAML resource such as accessing or reading it fails
     */
    Object createFromYaml(URL pathToFile, K8sResultConverter converter) throws K8sException, IOException;

    /**
     * Deletes Kubernetes resources from a YAML file. As the createFromYaml method, the YAML has to be properly,
     * formatted, and Kubernetes has to be reachable. The actual deletion of the resources is delegated to Kubernetes,
     * that could delay the operation until is possible to do so.
     * @param pathToFile an URL containing a valid path to the file (at the moment, only local files are supported)
     * @param converter a result converter, that allows the API call result to be formatted as the user wishes
     * @return The class returns a generic Object that the user can downcast to what she prefers, since the result is
     * what K8sResultConverter.convert() returns.
     * @throws K8sException exception thrown if an operation with Kubernetes (e.g. resource creation, missing
     * permissions, etc) fails for whatever reason
     * @throws IOException exception thrown if an operation with the YAML resource such as accessing or reading it fails
     */
    Object deleteFromYaml(URL pathToFile, K8sResultConverter converter) throws K8sException, IOException;
}
