package org.augugrumi.harbor.persistence.data;

import org.augugrumi.harbor.persistence.PersistenceRetriever;
import org.augugrumi.harbor.persistence.Result;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class NetworkService extends AbsNetworkData {

    public interface Fields extends Data.Fields {
        String CHAIN = "ns";
    }

    public NetworkService(String id) {
        super(id, PersistenceRetriever.getNSDb());
    }

    public List<VirtualNetworkFunction> getVNFClaims() {
        checkValidityOrThrow();
        Result<JSONObject> qRes = getDB().get(getMyQuery());
        if (qRes.isSuccessful()) {
            return (List) qRes.getContent().getJSONArray(Fields.CHAIN).toList();
        }
        return new ArrayList<>();
    }

    @Override
    public String toString() {
        if (isValid()) {
            Result<JSONObject> res = getDB().get(getMyQuery());
            if (res.isSuccessful()) {
                return res.getContent().toString();
            } else {
                return new JSONObject().toString();
            }
        } else {
            return new JSONObject().toString();
        }
    }
}
