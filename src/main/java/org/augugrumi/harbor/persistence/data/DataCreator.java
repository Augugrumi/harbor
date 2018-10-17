package org.augugrumi.harbor.persistence.data;

import org.augugrumi.harbor.persistence.Result;

import java.util.List;

public class DataCreator {

    private DataCreator() {
    }

    public static Result<NetworkService> newNS(String id, List<VirtualNetworkFunction> vnfs) {
        NetworkService toSave = new NetworkService(id, vnfs);
        return new Result<>(toSave.saveAndClean(), toSave);
    }
}
