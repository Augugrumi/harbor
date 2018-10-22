package org.augugrumi.harbor.util;

import org.json.JSONObject;
import org.yaml.snakeyaml.Yaml;

import java.util.Map;

public class ObjectConverter {

    private ObjectConverter() {
    }

    public static String json2yaml(JSONObject json, String field) {
        Yaml yaml = new Yaml();
        return yaml.dump(yaml.load(json.getJSONObject(field).toString()));
    }

    public static String json2yaml(String json) {
        Yaml yaml = new Yaml();
        return yaml.dump(yaml.load(json));
    }

    public static JSONObject yaml2json(String strYaml) {
        Yaml yaml = new Yaml();
        return new JSONObject((Map) yaml.load(strYaml));
    }
}
