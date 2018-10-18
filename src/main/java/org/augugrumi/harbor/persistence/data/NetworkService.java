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
        String CHAIN_ID = "spi";
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
    synchronized boolean saveAndClean() {
        if (isValid()) {
            return false;
        }
        // Write down chain
        JSONObject myselfJson = new JSONObject();
        JSONArray vnfsJson = new JSONArray();
        if (vnfs != null && vnfs.size() != 0) {
            for (final VirtualNetworkFunction vnf : vnfs) {
                vnfsJson.put(vnf.getID());
            }
        }
        myselfJson.put(Fields.CHAIN, vnfsJson);

        // Write down chain ID
        int currentMaxSPI = 0;
        Result<List<JSONObject>> jsonNSRes = getDB().get();
        if (jsonNSRes.isSuccessful()) {
            for (JSONObject ns : jsonNSRes.getContent()) {
                int current = ns.optInt(Fields.CHAIN_ID, -1);
                if (current > currentMaxSPI) {
                    currentMaxSPI = current;
                }
            }
        }
        // My ID = currentMaxSPI + 1 ;)
        myselfJson.put(Fields.CHAIN_ID, currentMaxSPI + 1);

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

    public int getSPI() {
        checkValidityOrThrow();
        Result<JSONObject> qRes = getDB().get(getMyQuery());
        if (qRes.isSuccessful()) {
            return qRes.getContent().optInt(Fields.CHAIN_ID, -1);
        }
        return -2;
    }

    public boolean setSPI(String spi) {
        return genericSet(new FieldPath(Fields.CHAIN_ID), spi);
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

    public boolean setChain(List<VirtualNetworkFunction> vnfs) {

        JSONArray chainsId = new JSONArray();
        for (final VirtualNetworkFunction vnf : vnfs) {
            chainsId.put(vnf.getID());
        }

        return genericSet(new FieldPath(Fields.CHAIN), chainsId);
    }

    public boolean updateChain(int beginning, int end, NetworkService toUpdate) {
        checkValidityOrThrow();
        // Check if the current boundaries are inside the ns length
        if (beginning <= end && end <= getChain().size()) {
        }
        return false;
    }

    public int size() {
        return getChain().size();
    }
}
