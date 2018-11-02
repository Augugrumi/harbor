package org.augugrumi.harbor.orchestration.components;

import org.augugrumi.harbor.orchestration.Orchestrator;

public class EgressComponent extends AbsComponent {
    EgressComponent() {
        super(Orchestrator.ComponentRole.EGRESS);
    }
}
