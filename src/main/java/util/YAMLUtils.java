package util;

import org.json.JSONObject;
import org.yaml.snakeyaml.Yaml;

import java.util.Map;

public class YAMLUtils {
    public static String convertToJson(String yamlString) {
        Yaml yaml= new Yaml();
        Map<String,Object> map= (Map<String, Object>) yaml.load(yamlString);

        JSONObject jsonObject=new JSONObject(map);
        return jsonObject.toString();
    }
}
