package org.augugrumi.harbor.orchestration;

import org.augugrumi.harbor.orchestration.components.Component;
import org.augugrumi.harbor.orchestration.components.ComponentFactory;
import org.augugrumi.harbor.orchestration.exceptions.StartUpException;

import java.util.HashMap;
import java.util.Map;

class K8sOrchestrator implements Orchestrator {

    private final Map<String, Component> components;
    private static K8sOrchestrator ourInstance = null;

    private K8sOrchestrator() {
        components = new HashMap<>();
        components.put(ComponentRole.ROUTE_CONTROLLER, ComponentFactory.getController());
        components.put(ComponentRole.EGRESS, ComponentFactory.getEgress());
        components.put(ComponentRole.INGRESS, ComponentFactory.getIngress());
    }

    /**
     * Getter method to return object instance. Please note that the Singleton will be created lazily: in fact, it's
     * not possible to instantiate the object during static time because the arguments and the environment variables
     * have still to be parsed, thus creating invalid components configurations.
     *
     * @return a pointer to the Singleton, lazily created
     * @see Component
     */
    static synchronized K8sOrchestrator getInstance() {
        if (ourInstance == null) {
            ourInstance = new K8sOrchestrator();
        }

        return ourInstance;
    }

    @Override
    public boolean isHealthy() {

        boolean res = true;
        for (final Map.Entry<String, Component> c : components.entrySet()) {
            res = res && c.getValue().isDeployed() && c.getValue().isOk();
        }

        return res;
    }

    @Override
    public boolean isHealthy(String componentName) {
        Component c = components.get(componentName);
        return c.isDeployed() && c.isOk();
    }

    @Override
    public void startUpCheck() throws StartUpException {
        // TODO launch every non-healthy component
        for (final Map.Entry<String, Component> c : components.entrySet()) {
            Component component = c.getValue();
            if (!component.isOk()) {
                if (component.isDeployed()) {
                    component.destroy();
                }
                component.deploy();
            }
        }

        // TODO should we sleep here waiting for all the components to go up? Find a way to manage this
        if (!isHealthy()) {
            throw new StartUpException("Impossible to successfully start up all the components");
        }
    }
}
