package util;

import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import spark.Route;

import static spark.Spark.*;

import java.io.FileReader;
import java.io.IOException;

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

        for (Object firstLevelPath: jsonPath) {

            String pathName = ((JSONObject) firstLevelPath).getString("name");
            String functionName = ((JSONObject) firstLevelPath).getString("function");
            String requestType = ((JSONObject) firstLevelPath).getString("type");
            JSONArray subDirs = ((JSONObject) firstLevelPath).optJSONArray("path");

            if (ancestor.length() != 0 && !ancestor.endsWith("/") && !pathName.startsWith("/")) {
                ancestor += "/";
            }

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
