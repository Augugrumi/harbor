package org.augugrumi.harbor.persistence;

import org.augugrumi.harbor.persistence.exception.DbException;
import org.json.JSONObject;

import java.util.List;

public interface Persistence {

    public interface Fields {
        String ID = "id";
    }

    Result<Void> save(Query q) throws DbException;

    Result<JSONObject> get(Query q) throws DbException;

    //List<Result<JSONArray>> get() throws DbException;
    Result<List<JSONObject>> get() throws DbException;

    Result<JSONObject> pop(Query q) throws DbException;

    Result<Boolean> delete(Query q) throws DbException;

    Result<Boolean> exists(Query q) throws DbException;

    Result<List<Boolean>> exists(List<Query> q) throws DbException;
}
