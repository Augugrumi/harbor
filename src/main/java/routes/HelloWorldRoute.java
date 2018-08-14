package routes;

import spark.Request;
import spark.Response;
import spark.Route;

/**
 * Simple example to check if the backend is replying correctly
 */
public class HelloWorldRoute implements Route {
    /**
     * It always return a "Hello world" string, no matter what
     *
     * @param request  the request coming from the browser
     * @param response optional settings
     * @return an "Hello world" String
     */
    @Override
    public Object handle(Request request, Response response) {
        return "Hello world";
    }
}
