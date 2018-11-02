package org.augugrumi.harbor.orchestration.components;

public interface Component {

    boolean isOk();

    boolean isDeployed();

    boolean deploy();

    boolean destroy();
}
