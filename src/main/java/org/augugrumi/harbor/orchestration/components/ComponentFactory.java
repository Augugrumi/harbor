package org.augugrumi.harbor.orchestration.components;

public class ComponentFactory {

    private ComponentFactory() {
    }

    public static RouteControllerComponent getController() {

        return new RouteControllerComponent();
    }

    public static IngressComponent getIngress() {
        return new IngressComponent();
    }

    public static EgressComponent getEgress() {
        return new EgressComponent();
    }
}
