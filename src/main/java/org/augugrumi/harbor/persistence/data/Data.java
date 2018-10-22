package org.augugrumi.harbor.persistence.data;

import org.json.JSONObject;

public interface Data {

    interface Fields {
        String ID = "id";
    }

    String getID();

    boolean isValid();

    boolean makeValid();

    boolean makeInValid();

    JSONObject toJson();

    @Override
    boolean equals(Object o);
}
