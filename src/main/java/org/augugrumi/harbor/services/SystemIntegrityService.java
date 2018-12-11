package org.augugrumi.harbor.services;

import org.augugrumi.harbor.orchestration.Orchestrator;
import org.augugrumi.harbor.orchestration.components.Component;
import org.augugrumi.harbor.util.ConfigManager;
import org.slf4j.Logger;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Future;

class SystemIntegrityService implements Service, Orchestrator.Listener {

    private final static Logger LOG = ConfigManager.getConfig().getApplicationLogger(SystemIntegrityService.class);
    private final static Map<Component, Future<?>> COMPONENTS_WATCHED = new HashMap<>();
    private final static int INTERVAL_CHECK = 120; // 2 mins

    @Override
    public void onComponentLaunched(Component c) {
        synchronized (COMPONENTS_WATCHED) {
            COMPONENTS_WATCHED.put(c, ServiceExecutor.getInstance().addService(() -> {
                LOG.info("New element " + c.getComponentRole() + " registered for healthy checks");
                while (true) {
                    try {
                        Thread.sleep(INTERVAL_CHECK);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    if (!c.isDeployed()) {
                        // Remove myself from the watch list, since it's not useful anymore
                        break;
                    }
                    if (!c.isOk()) {
                        LOG.warn("Element " + c.getComponentRole() + " is not healthy. Restarting the deployment");
                        c.restart();
                    }
                }
            }));
        }
    }

    @Override
    public void onComponentStopped(Component c) {
        synchronized (COMPONENTS_WATCHED) {
            Future<?> jobToStop = COMPONENTS_WATCHED.get(c);
            if (jobToStop != null) {
                jobToStop.cancel(true);
                LOG.info(c.getComponentRole() + " successfully removed from system healthy checks");
            } else {
                LOG.warn("Required removal of component " + c.getComponentRole() + " from healthy checks: component" +
                        "not found.");
            }
        }
    }
}
