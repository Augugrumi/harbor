package util;

import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import spark.Route;

import java.io.FileReader;
import java.io.IOException;

import static spark.Spark.*;

public class DynamicAPILoader {

    final private static Logger LOG = ConfigManager.getConfig().getApplicationLogger(DynamicAPILoader.class);

    final private JSONArray jsonPathConfig;

    public DynamicAPILoader (String jsonPath) throws IOException {

        LOG.debug("Route file: " + jsonPath);
        FileReader reader = new FileReader(jsonPath);

        StringBuilder res = new StringBuilder();

        int justRead;
        while ((justRead = reader.read()) != -1) {

            res.append((char)justRead);
        }

        LOG.debug("JSON Read:\n" + res.toString());
        this.jsonPathConfig = new JSONArray(res.toString());

    }

    public void load () {
        addSubDir(jsonPathConfig, "");
    }

    private void addSubDir (JSONArray jsonPath, String ancestor) {

        LOG.debug("jsonPath length: " + jsonPath.length());

        for (Object firstLevelPath: jsonPath) {

            String pathName = ((JSONObject) firstLevelPath).getString("name");
            String functionName = ((JSONObject) firstLevelPath).optString("function");
            String requestType = ((JSONObject) firstLevelPath).optString("type", "get");
            JSONArray subDirs = ((JSONObject) firstLevelPath).optJSONArray("path");

            if (ancestor.length() != 0 && !ancestor.endsWith("/") && !pathName.startsWith("/")) {
                ancestor += "/";
            }

            if (functionName != null && !functionName.equals("")) {

                switch (requestType.toLowerCase()) {
                    case "get":
                        get(ancestor + pathName, instantiate(functionName, Route.class));
                        break;
                    case "post":
                        post(ancestor + pathName, instantiate(functionName, Route.class));
                        break;
                    case "delete":
                        delete(ancestor + pathName, instantiate(functionName, Route.class));
                        break;
                    case "put":
                        put(ancestor + pathName, instantiate(functionName, Route.class));
                        break;
                    default:
                        throw new IllegalArgumentException("The type of the request for path " + ancestor + pathName +
                                " is not valid: it can only be:\n" +
                                "- GET\n" +
                                "- POST\n" +
                                "- PUT\n" +
                                "- DELETE\n");
                }

                LOG.info("Load api: " + ancestor + pathName + " in function: " + functionName +
                        ". Type of request: " + requestType);
            }

            if (subDirs != null && subDirs.length() > 0) {
                LOG.info("Loading subpath " + pathName + ". Number of elements: " + subDirs.length());
                addSubDir(subDirs, ancestor + pathName);
            }
        }
    }

    private <T> T instantiate(final String className, final Class<T> type){
        try{
            return type.cast(Class.forName(className).newInstance());
        } catch(InstantiationException
                | IllegalAccessException
                | ClassNotFoundException e){
            throw new IllegalStateException(e);
        }
    }
}
