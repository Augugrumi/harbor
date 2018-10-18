package org.augugrumi.harbor.persistence.data;

import org.augugrumi.harbor.persistence.Result;
import org.augugrumi.harbor.persistence.query.SimpleQuery;
import org.augugrumi.harbor.util.ConfigManager;
import org.slf4j.Logger;

import java.util.List;

public class DataWizard {

    private final static Logger LOG = ConfigManager.getConfig().getApplicationLogger(DataWizard.class);

    private DataWizard() {
    }

    public static Result<NetworkService> newNS(String id, List<VirtualNetworkFunction> vnfs) {
        NetworkService toSave = new NetworkService(id, vnfs);
        for (final VirtualNetworkFunction vnf : vnfs) {
            if (!vnf.isValid()) {
                return new Result<>(false, toSave);
            }
        }
        boolean isSaved = toSave.saveAndClean();
        if (isSaved && toSave.isValid()) {
            vnfs.forEach(item -> {
                boolean res = item.addNSClaim(toSave);
                if (!res) {
                    LOG.warn("Impossible to update entry for VNF: " + item.getID());
                }
            });
        }
        return new Result<>(isSaved, toSave);
    }

    public static Result<VirtualNetworkFunction> newVNF(String id, String content) {
        VirtualNetworkFunction toSave = new VirtualNetworkFunction(id, content);
        return new Result<>(toSave.saveAndClean(), toSave);
    }

    public static Result<NetworkService> getNs(String id) {
        SimpleQuery q = new SimpleQuery(id);
        NetworkService ns = new NetworkService(id);
        return new Result<NetworkService>(ns.isValid(), ns);
    }

    public static Result<VirtualNetworkFunction> getVNF(String id) {
        SimpleQuery q = new SimpleQuery(id);
        VirtualNetworkFunction vnf = new VirtualNetworkFunction(id);
        return new Result<VirtualNetworkFunction>(vnf.isValid(), vnf);
    }

    public static boolean deleteNS(String id) {
        NetworkService toDelete = new NetworkService(id);
        if (toDelete.isValid()) {
            List<VirtualNetworkFunction> vnfs = toDelete.getChain();
            LOG.debug("Chain size: " + vnfs.size());
            for (final VirtualNetworkFunction vnf : vnfs) {
                if (vnf.isValid()) {
                    LOG.debug("Vnf " + vnf.getID() + " is valid, deleting myself from its list");
                    vnf.deleteNSClaim(toDelete);
                }
            }
        }
        return toDelete.makeInValid();
    }

    public static boolean deleteVNF(String id) {
        VirtualNetworkFunction toDelete = new VirtualNetworkFunction(id);
        if (toDelete.isValid() && toDelete.getNsClaimsSize() != 0) {
            return false;
        }
        return toDelete.makeInValid();
    }
}
