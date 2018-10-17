package org.augugrumi.harbor.persistence.data;

import org.augugrumi.harbor.persistence.FieldPath;
import org.augugrumi.harbor.persistence.PersistenceRetriever;
import org.augugrumi.harbor.persistence.Query;
import org.augugrumi.harbor.persistence.Result;
import org.augugrumi.harbor.routes.util.exceptions.NoSuchNetworkComponentException;
import org.augugrumi.harbor.util.ConfigManager;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.yaml.snakeyaml.Yaml;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class VirtualNetworkFunction extends AbsNetworkData {

    private final static Logger LOG = ConfigManager.getConfig().getApplicationLogger(VirtualNetworkFunction.class);
    private String vnfDefinition;

    public interface Fields extends Data.Fields {

        String DEFINITION = "definition";
        String NS_CLAIMS = "nsClaims";
    }

    private void clean() {
        vnfDefinition = null;
    }

    @Override
    boolean saveAndClean() {
        if (isValid()) {
            return false;
        }
        JSONObject myselfJson = new JSONObject();
        Yaml yaml = new Yaml();
        JSONObject convertedFromYaml = new JSONObject((Map) yaml.load(vnfDefinition));
        myselfJson.put(Fields.DEFINITION, convertedFromYaml);
        Query myselfQuery = new Query() {
            @Override
            public String getID() {
                return VirtualNetworkFunction.this.getID();
            }

            @Override
            public String getContent() {
                return myselfJson.toString(); // FIXME escape the \n character!
            }
        };
        Result<Void> queryRes = getDB().save(myselfQuery);
        if (queryRes.isSuccessful()) {
            clean();
        }
        return queryRes.isSuccessful();
    }

    public VirtualNetworkFunction(String id) {
        super(id, PersistenceRetriever.getVnfDb());
    }

    VirtualNetworkFunction(String id, String definition) {
        super(id, PersistenceRetriever.getVnfDb());

        vnfDefinition = definition;
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

    @Override
    public String toString() {
        JSONObject converted = new JSONObject(super.toString());
        LOG.info(converted.toString());
        Yaml yaml = new Yaml();
        Map<String, Object> yamlMap = yaml.load(converted.getJSONObject(Fields.DEFINITION).toString());
        String convert = yaml.dump(yamlMap);
        LOG.info(convert);
        converted.put(Fields.DEFINITION, convert);
        return converted.toString();
    }

    @Override
    public JSONObject toJson() {
        JSONObject json = super.toJson();

        Yaml yaml = new Yaml();
        Map<String, Object> yamlMap = yaml.load(json.getJSONObject(Fields.DEFINITION).toString());
        String convert = yaml.dump(yamlMap);
        json.put(Fields.DEFINITION, convert);
        return json;
    }
}
