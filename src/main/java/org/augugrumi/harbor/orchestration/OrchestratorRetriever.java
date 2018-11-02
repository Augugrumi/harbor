package org.augugrumi.harbor.orchestration;

public class OrchestratorRetriever {

    private OrchestratorRetriever() {
    }

    public static Orchestrator getK8sOrchestrator() {
        return new K8sOrchestrator();
    }
}
