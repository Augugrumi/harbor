package org.augugrumi.harbor.persistence;

import static org.augugrumi.harbor.persistence.Costants.NS_HOME;
import static org.augugrumi.harbor.persistence.Costants.VNF_HOME;

public class PersistenceRetriever {

    private PersistenceRetriever() {
    }

    public static Persistence getVnfDb() {
        return PersistenceFactory.getFSPersistence(VNF_HOME);
    }

    public static Persistence getNSDb() {
        return PersistenceFactory.getFSPersistence(NS_HOME);
    }
}
