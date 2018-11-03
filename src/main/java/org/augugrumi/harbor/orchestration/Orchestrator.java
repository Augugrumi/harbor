package org.augugrumi.harbor.orchestration;

import org.augugrumi.harbor.orchestration.components.Component;
import org.augugrumi.harbor.orchestration.exceptions.StartUpException;

public interface Orchestrator {

    boolean isHealthy();

    boolean isHealthy(String componentName);

    void startUpCheck() throws StartUpException;

    interface ComponentRole {
        String ROUTE_CONTROLLER = "controller";
        String INGRESS = "ingress";
        String EGRESS = "egress";
    }

    interface Listener {
        void onComponentLaunched(Component c);
        void onComponentStopped(Component c);
    }
}
