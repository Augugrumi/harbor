package org.augugrumi.harbor.k8s.json;

public interface Service extends Common {
    String ITEMS = "items";

    interface Item {
        String METADATA = "metadata";

        interface Metadata {
            String NAME = "name";
        }
    }
}
