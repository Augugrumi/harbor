package org.augugrumi.harbor.orchestration.components;

public interface Component {

    String getComponentRole();

    boolean isOk();

    boolean isDeployed();

    boolean deploy();

    boolean destroy();

    boolean restart();
}
