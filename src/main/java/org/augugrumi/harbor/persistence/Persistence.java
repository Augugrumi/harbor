package org.augugrumi.harbor.persistence;

import java.util.List;

public interface Persistence {

    Result save(Query q);

    Result get(Query q);

    List<Result<String>> get();

    Result pop(Query q);

    Result<Void> delete(Query q);

    Result<Boolean> exists(Query q);
}
