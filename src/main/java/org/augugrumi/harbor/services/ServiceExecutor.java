package org.augugrumi.harbor.services;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

class ServiceExecutor {

    private static ServiceExecutor ourInstance = new ServiceExecutor();
    private final ExecutorService THREAD_POOL;

    private ServiceExecutor() {
        THREAD_POOL = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
    }

    static ServiceExecutor getInstance() {
        return ourInstance;
    }

    Future<?> addService(Runnable task) {
        return THREAD_POOL.submit(task);
    }
}
