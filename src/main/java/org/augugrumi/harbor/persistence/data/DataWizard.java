package org.augugrumi.harbor.persistence.data;

import org.augugrumi.harbor.persistence.Result;
import org.augugrumi.harbor.persistence.query.SimpleQuery;

import java.util.List;

public class DataWizard {

    private DataWizard() {
    }

    public static Result<NetworkService> newNS(String id, List<VirtualNetworkFunction> vnfs) {
        NetworkService toSave = new NetworkService(id, vnfs);
        return new Result<>(toSave.saveAndClean(), toSave);
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
}
