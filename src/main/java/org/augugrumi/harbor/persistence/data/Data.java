package org.augugrumi.harbor.persistence.data;

public interface Data {

    interface Fields {
        String ID = "id";
    }

    String getID();

    boolean isValid();

    boolean makeValid();
}
