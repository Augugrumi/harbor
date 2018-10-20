package org.augugrumi.harbor.services;

public class ServiceRetriever {

    public static RefCountingService getRefCountingService() {
        return new RefCountingService();
    }
}
