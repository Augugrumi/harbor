package org.augugrumi.harbor.persistence.data;

import org.augugrumi.harbor.persistence.FieldPath;
import org.augugrumi.harbor.persistence.Persistence;
import org.augugrumi.harbor.persistence.Result;
import org.augugrumi.harbor.persistence.exception.DbException;
import org.augugrumi.harbor.persistence.query.SimpleQuery;
import org.augugrumi.harbor.routes.util.exceptions.NoSuchNetworkComponentException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public abstract class AbsNetworkData implements Data {

    private final SimpleQuery SELF;
    private final Persistence DB;

    protected AbsNetworkData(String id, Persistence db) {
        SELF = new SimpleQuery(id);
        DB = db;
    }

    protected SimpleQuery getMyQuery() {
        return SELF;
    }

    protected Persistence getDB() {
        return DB;
    }

    protected void checkValidityOrThrow() throws NoSuchNetworkComponentException {
        if (!isValid()) {
            throw new NoSuchNetworkComponentException("No component with id " + getID() + " found");
        }
    }

    protected void checkValidityOrMake() throws DbException {
        if (!isValid()) {
            if (!makeValid()) {
                throw new DbException("Impossible to save object in the database");
            }
        }
    }

    protected boolean genericSet(FieldPath path, Object o) {
        checkValidityOrMake();

        Map<FieldPath, Object> toUpdate = new HashMap<>();
        toUpdate.put(path, o);

        Result<Boolean> update = getDB().update(getMyQuery(), toUpdate);
        return update.isSuccessful() && update.getContent();
    }

    @Override
    public String getID() {
        return SELF.getID();
    }

    @Override
    public boolean isValid() {
        Result<Boolean> query = DB.exists(SELF);
        return query.isSuccessful() && query.getContent();
    }

    @Override
    public boolean makeValid() {
        if (!isValid()) {
            Result<Void> query = DB.save(SELF);
            return query.isSuccessful();
        }
        return true;
    }

    @Override
    public String toString() {
        if (isValid()) {
            Result<JSONObject> res = getDB().get(getMyQuery());
            if (res.isSuccessful()) {
                return res.getContent().toString();
            } else {
                return new JSONObject().toString();
            }
        } else {
            return new JSONObject().toString();
        }
    }
}