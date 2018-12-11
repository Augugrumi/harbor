package org.augugrumi.harbor.services;

import org.augugrumi.harbor.k8s.K8sAPI;
import org.augugrumi.harbor.k8s.K8sRetriever;
import org.augugrumi.harbor.persistence.data.NetworkService;
import org.augugrumi.harbor.persistence.data.VirtualNetworkFunction;
import org.augugrumi.harbor.util.ConfigManager;
import org.augugrumi.harbor.util.FileUtils;
import org.slf4j.Logger;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Future;

class RefCountingService implements Service, NetworkService.Listener {

    private final static Logger LOG = ConfigManager.getConfig().getApplicationLogger(RefCountingService.class);
    /*
     * Need to synchronize on the VNFS_TO_DELETE object to avoid an check-then-act race condition, since this
     * callback can be called by multiple objects having the same IDs. In this way, the map doesn't need to be
     * concurrent since the operations in it are already synchronized.
     */
    private final static Map<VirtualNetworkFunction, Future<?>> VNFS_TO_DELETE = new HashMap<>();
    private final static int SLEEP_TIME_SECONDS = 300;
    private final static K8sAPI api = K8sRetriever.getK8sAPI();

    private int getRunningChains(VirtualNetworkFunction vnf) {
        int runningChains = 0;
        for (final NetworkService ns : vnf.getNsClaims()) {
            if (NetworkService.STATUS_UP.equalsIgnoreCase(ns.getStatus())) {
                runningChains++;
            }
        }
        return runningChains;
    }

    private void vnfStopPrune(VirtualNetworkFunction vnf) {
        LOG.info("Removing pending pruning operation for VNF " + vnf.getID());
        final Future<?> operation = VNFS_TO_DELETE.get(vnf);
        if (operation != null) { // an actual deletion was scheduled
            operation.cancel(true);
            LOG.info("Deletion for VNF " + vnf.getID() + " performed");
        } else {
            LOG.info("Nothing to clean, no pending deletion for VNF " + vnf.getID());
        }
    }

    private void vnfAddPrune(VirtualNetworkFunction vnf) {
        // need to add the vnf in the pruning queue, if it doesn't exist already!
        if (VNFS_TO_DELETE.get(vnf) == null) { // we won't schedule two deletions for the same object
            LOG.info("Scheduling pruning operation for VNF " + vnf.getID() + "...");
            VNFS_TO_DELETE.put(vnf, ServiceExecutor.getInstance().addService(() -> {
                try {
                    LOG.info("Deletion for VNF " + vnf.getID() + " scheduled");
                    Thread.sleep(SLEEP_TIME_SECONDS * 1000); // count down
                    api.deleteFromYaml(
                            FileUtils.createTmpFile("hrbr", ".yaml",
                                    vnf.getDefinition()).toURI().toURL(),
                            res -> {
                                LOG.info(res.getAttachment().toString());
                                return res.getAttachment().toString();
                            });
                    LOG.info("VNF " + vnf.getID() + " pruned from K8s after " + SLEEP_TIME_SECONDS + "seconds " +
                            " of inactivity");
                } catch (IOException | InterruptedException e) {
                    e.printStackTrace();
                }
            }));
        }
    }

    @Override
    public void onNSCreation(NetworkService ns) {
        synchronized (VNFS_TO_DELETE) {
            ns.getChain()
                    .parallelStream()
                    .forEach(this::vnfStopPrune);
        }
    }

    @Override
    public void onNSDeletion(NetworkService ns) {
        synchronized (VNFS_TO_DELETE) {
            // FIXME should check how many services (deployed) are using this vnf
            ns.getChain()
                    .parallelStream()
                    .forEach(item -> {
                        if (getRunningChains(item) == 0) {
                            vnfAddPrune(item);
                        }
                    });
        }
    }
}
