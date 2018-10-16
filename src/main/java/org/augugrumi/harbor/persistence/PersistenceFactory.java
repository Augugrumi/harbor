package org.augugrumi.harbor.persistence;

import org.augugrumi.harbor.persistence.fs.FSPersistence;

class PersistenceFactory {

    private PersistenceFactory() {
    }

    public static Persistence getMongoPersistence(String id) {
        return null;
    }

    public static Persistence getFSPersistence(String id) {
        return new FSPersistence(id);
    }
}
