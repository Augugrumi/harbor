package org.augugrumi.harbor.orchestration;

import org.augugrumi.harbor.orchestration.exceptions.StartUpException;

public interface Orchestrator {

    boolean isHealthy();

    void startUpCheck() throws StartUpException;

    interface ComponentRole {
        String ROUTE_CONTROLLER = "controller";
        String INGRESS = "ingress";
        String EGRESS = "egress";
    }
}
