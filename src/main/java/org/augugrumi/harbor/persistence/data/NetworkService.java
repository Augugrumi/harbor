package org.augugrumi.harbor.persistence.data;

import org.augugrumi.harbor.persistence.FieldPath;
import org.augugrumi.harbor.persistence.PersistenceRetriever;
import org.augugrumi.harbor.persistence.Query;
import org.augugrumi.harbor.persistence.Result;
import org.augugrumi.harbor.util.ConfigManager;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.List;

public class NetworkService extends AbsNetworkData {

    private final static Logger LOG = ConfigManager.getConfig().getApplicationLogger(NetworkService.class);

    private List<VirtualNetworkFunction> vnfs;

    public interface Fields extends Data.Fields {
        String CHAIN = "ns";
    }

    public NetworkService(String id) {
        super(id, PersistenceRetriever.getNSDb());
    }

    NetworkService(String id, List<VirtualNetworkFunction> vnfs) {
        super(id, PersistenceRetriever.getNSDb());
        this.vnfs = vnfs;
    }

    private void clean() {
        vnfs = null;
    }

    @Override
    boolean saveAndClean() {
        if (isValid()) {
            return false;
        }
        JSONObject myselfJson = new JSONObject();
        JSONArray vnfsJson = new JSONArray();
        if (vnfs != null && vnfs.size() != 0) {
            for (final VirtualNetworkFunction vnf : vnfs) {
                vnfsJson.put(vnf.getID());
            }
        }
        myselfJson.put(Fields.CHAIN, vnfsJson);
        Query myselfQuery = new Query() {
            @Override
            public String getID() {
                return NetworkService.this.getID();
            }

            @Override
            public String getContent() {
                return myselfJson.toString();
            }
        };
        Result<Void> queryRes = getDB().save(myselfQuery);
        if (queryRes.isSuccessful()) {
            clean();
        }
        return queryRes.isSuccessful();
    }

    public List<VirtualNetworkFunction> getChain() {
        checkValidityOrThrow();
        Result<JSONObject> qRes = getDB().get(getMyQuery());
        if (qRes.isSuccessful()) {

            List<VirtualNetworkFunction> res = new ArrayList<>();
            for (Object vnfId : qRes.getContent().optJSONArray(Fields.CHAIN)) {
                res.add(new VirtualNetworkFunction((String) vnfId));
            }

            return res;
        }
        return new ArrayList<>();
    }

    // FIXME we should accept a list of VNFs instead!
    public boolean setChain(JSONArray chain) {
        return genericSet(new FieldPath(Fields.CHAIN), chain);
    }

    public boolean updateChain(int beginning, int end, NetworkService toUpdate) {
        checkValidityOrThrow();
        // Check if the current boundaries are inside the ns length
        return false;
    }

    public int size() {
        return getChain().size();
    }
}
