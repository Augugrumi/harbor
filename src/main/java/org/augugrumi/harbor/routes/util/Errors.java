package org.augugrumi.harbor.routes.util;

public interface Errors {
    String NO_SUCH_ELEMENT = "The requested element was not found";
    String DB_ADD = "Impossible to add the object in the database";
    String DB_REMOVE = "Impossible to remove the object from the database";
    String ROULETTE_UPDATE_FAILURE = "Impossible to update Roulette entry";
    String ROULETTE_EMPTY_REPLY = "Roulette replied with an empty message";
    String ROULETTE_UPDATE_NOT_COMPLETE = "Some roulette db weren't updated correctly.";
    String KUBERNETES_IO_ERROR = "An IO exception occurred while interacting with Kubernetes";
    String NO_STATS = "Error while retrieving stats";
}
