package org.augugrumi.harbor.orchestration;

public class OrchestratorRetriever {

    private OrchestratorRetriever() {
    }

    public static Orchestrator getK8sOrchestrator() {
        return K8sOrchestrator.getInstance();
    }
}
