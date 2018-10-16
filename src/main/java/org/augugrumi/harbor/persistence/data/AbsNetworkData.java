package org.augugrumi.harbor.persistence.data;

import org.augugrumi.harbor.persistence.Persistence;
import org.augugrumi.harbor.persistence.Result;
import org.augugrumi.harbor.persistence.query.SimpleQuery;
import org.augugrumi.harbor.routes.util.exceptions.NoSuchNetworkComponentException;

public class AbsNetworkData implements Data {

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

    @Override
    public String getID() {
        return SELF.getID();
    }

    @Override
    public boolean isValid() {
        Result<Boolean> query = DB.exists(SELF);
        return query.isSuccessful() && query.getContent();
    }
}
