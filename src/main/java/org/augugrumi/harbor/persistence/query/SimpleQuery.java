package org.augugrumi.harbor.persistence.query;

import org.augugrumi.harbor.persistence.Query;

public class SimpleQuery implements Query {

    private final String ID;

    public SimpleQuery(String id) {
        this.ID = id;
    }

    @Override
    public String getID() {
        return ID;
    }

    @Override
    public String getContent() {
        return null;
    }
}
