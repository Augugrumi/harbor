package org.augugrumi.harbor.persistence;

import org.augugrumi.harbor.persistence.exception.DbException;
import org.augugrumi.harbor.persistence.query.SimpleQuery;
import org.json.JSONObject;

import java.util.List;
import java.util.Map;

public interface Persistence {

    interface Fields {
        String ID = "id";
    }

    // FIXME it should be Result<Boolean> too!
    Result<Void> save(Query q) throws DbException;

    Result<JSONObject> get(Query q) throws DbException;

    Result<List<JSONObject>> get() throws DbException;

    Result<Boolean> update(Query q) throws DbException;

    Result<Boolean> update(SimpleQuery q, Map<FieldPath, Object> toUpdate) throws DbException;

    Result<JSONObject> pop(Query q) throws DbException;

    Result<Boolean> delete(Query q) throws DbException;

    Result<Boolean> exists(Query q) throws DbException;

    Result<List<Boolean>> exists(List<Query> q) throws DbException;
}
