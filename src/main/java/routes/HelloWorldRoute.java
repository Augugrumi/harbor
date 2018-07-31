package routes;

import spark.Request;
import spark.Response;
import spark.Route;

public class HelloWorldRoute implements Route {
    @Override
    public Object handle(Request request, Response response) throws Exception {
        return "Hello world";
    }
}
