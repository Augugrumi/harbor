package util;

import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import spark.Route;

import java.io.FileReader;
import java.io.IOException;

import static spark.Spark.*;

/**
 * Dynamically builds the API structure reading from the defined API JSON. The API JSON must be a valid JSON defined in
 * this way:
 * <pre>
 * [
 *   {
 *     "name": "/hello",
 *     "type": "get",
 *     "function": "routes.HelloWorldRoute"
 *   }
 * ]
 * </pre>
 * The JSON is defined as follows:
 * <ul>
 * <li>"name" must contain a valid API path. It can contains wildcards and params</li>
 * <li>"type" element can be of four types: get, post, put and delete.</li>
 * <li>"function" indicates the function to call when the route is hit by a request. It must point to a valid
 * java classpath otherwise a runtime error will be thrown when loading the JSON file</li>
 * </ul>
 * Inside a path definition, another path definition can be nest, in this way:
 * <pre>
 * [
 *   {
 *     "name": "/vnf",
 *     "path": [
 *       {
 *         "name": "/launch/:id",
 *         "type": "get",
 *         "function": "routes.vnf.VnfLauncherRoute"
 *       },
 *       {
 *         "name": "/stop/:id",
 *         "type": "get",
 *         "function": "routes.vnf.VnfStopperRoute"
 *       }
 *     ]
 *   }
 * ]
 * </pre>
 * This allow to create complex paths.
 *
 * @see <a href="http://sparkjava.com/documentation#routes">Spark Java routes</a>
 * @see <a href="http://sparkjava.com/documentation">Spark Java official documentation</a>
 */
public class DynamicAPILoader {

    final private static Logger LOG = ConfigManager.getConfig().getApplicationLogger(DynamicAPILoader.class);

    final private JSONArray jsonPathConfig;

    /**
     * It loads the API path into RAM, without applying it. This means that the file is read into memory, but the
     * actual server configuration isn't touched until <i>load</i> is called
     * @param jsonPath the path to the JSON file
     * @throws IOException is thrown if some operation accessing the JSON file fails
     */
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

    /**
     * It applies the JSON configuration to the server
     */
    public void load () {
        addSubDir(jsonPathConfig, "");
    }

    /**
     * Recursive method that explore the JSON as a tree and adds every path it founds
     * @param jsonPath JSON array of that recursive call
     * @param ancestor path containing the parents of the recursive call
     */
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

    /**
     * Generic method to dynamically load the method defined in the "function" JSON field
     * @param className full java path to the method
     * @param type the type that the loaded class has to be casted
     * @param <T> generic type returning the desired type for the loaded object
     * @return a dynamically loaded class instantiated with the right type
     */
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
