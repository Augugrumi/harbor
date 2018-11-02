package org.augugrumi.harbor.orchestration.components;

import org.augugrumi.harbor.orchestration.Orchestrator;

public class IngressComponent extends AbsComponent {
    IngressComponent() {
        super(Orchestrator.ComponentRole.INGRESS);
    }
}
