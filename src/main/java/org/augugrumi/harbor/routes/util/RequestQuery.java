package org.augugrumi.harbor.routes.util;

import org.augugrumi.harbor.persistence.Query;
import spark.Request;

public class RequestQuery implements Query {

    private final String ID;
    private final Request REQUEST;

    public RequestQuery(String id, Request request) {
        this.ID = id;
        this.REQUEST = request;
    }

    @Override
    public String getId() {
        return REQUEST.params(ID);
    }

    @Override
    public String getContent() {
        return REQUEST.body();
    }
}
