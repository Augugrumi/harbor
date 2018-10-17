package org.augugrumi.harbor.persistence.data;

import org.augugrumi.harbor.persistence.FieldPath;
import org.augugrumi.harbor.persistence.PersistenceRetriever;
import org.augugrumi.harbor.persistence.Result;
import org.augugrumi.harbor.routes.util.exceptions.NoSuchNetworkComponentException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class VirtualNetworkFunction extends AbsNetworkData {

    public interface Fields extends Data.Fields {
        String DEFINITION = "definition";
        String NS_CLAIMS = "nsClaims";
    }

    public VirtualNetworkFunction(String id) {
        super(id, PersistenceRetriever.getVnfDb());
    }

    public String getDefinition() throws NoSuchNetworkComponentException {
        checkValidityOrThrow();
        Result<JSONObject> res = getDB().get(getMyQuery());
        if (res.isSuccessful()) {
            return getDB().get(getMyQuery()).getContent().getString(Fields.DEFINITION);
        } else {
            return "";
        }
    }

    public boolean setDefinition(String newDefinition) {
        return genericSet(new FieldPath(Fields.DEFINITION), newDefinition);
    }

    public List<NetworkService> getNsClaims() throws NoSuchNetworkComponentException {
        checkValidityOrThrow();
        List<NetworkService> res = new ArrayList<>();
        Result<JSONObject> qRes = getDB().get(getMyQuery());
        if (qRes.isSuccessful()) {

            List<String> services = (List) qRes.getContent().getJSONArray(Fields.NS_CLAIMS).toList();
            for (final String s : services) {
                res.add(new NetworkService(s));
            }
        }
        return res;
    }
}
