package org.augugrumi.harbor.persistence;

public interface Persistence {

    Result save(Query q);

    Result retrieve(Query q);

    Result pop(Query q);

    Result<Void> delete(Query q);

    Result<Void> exists(Query q);
}
