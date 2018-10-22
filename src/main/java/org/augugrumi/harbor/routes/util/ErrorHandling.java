package org.augugrumi.harbor.routes.util;

import routes.util.ResponseCreator;

public class ErrorHandling {

    public static ResponseCreator dbErr() {
        routes.util.ResponseCreator err = new routes.util.ResponseCreator(routes.util.ResponseCreator.ResponseType.ERROR);
        err.add(routes.util.ResponseCreator.Fields.REASON, "Impossible to connect to the database");
        return err;
    }
}
