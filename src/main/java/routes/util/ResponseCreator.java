package routes.util;

import org.json.JSONObject;
import org.slf4j.Logger;
import routes.util.exceptions.ResponseCreatorException;
import util.ConfigManager;

import java.util.Map;

/**
 * Creates a consistent response to send back to the client
 */
public class ResponseCreator {

    final private static Logger LOG = ConfigManager.getConfig().getApplicationLogger(ResponseCreator.class);

    final private JSONObject response;

    /**
     * The type of the response, that can be <code>OK</code> or <code>ERROR</code>
     */
    public enum ResponseType {
        OK,
        ERROR
    }

    /**
     * The allowed fields in a response. Some fields exclude others
     */
    public enum Fields {
        RESULT,
        CONTENT,
        ERRORCODE,
        REASON
    }

    /**
     * Public constructor. It create a positive or negative response based on the ResponseType
     *
     * @param typeOfResponse the type of the response
     * @see ResponseType
     */
    public ResponseCreator(ResponseType typeOfResponse) {
        this.response = new JSONObject();

        response.put(toLowerString(Fields.RESULT), toLowerString(typeOfResponse));
    }

    /**
     * This method add a field the the existing responde
     * @param newField the new type of field to add
     * @param newData the object to attach to that field, that it will be converted to String
     * @return a ResponseCreator with an additional field
     */
    public ResponseCreator add(Fields newField, Object newData) {

        if (response.has(toLowerString(newField))) {
            LOG.warn("Field " + toLowerString(newField) + " already set in JSON response, overwriting it");
        }

        if (response.getString(toLowerString(Fields.RESULT)).equals(toLowerString(ResponseType.OK)) &&
                (toLowerString(newField).equals(toLowerString(Fields.REASON)) ||
                        toLowerString(newField).equals(toLowerString(Fields.ERRORCODE)))) {
            LOG.error("Cannot set " + Fields.REASON +
                    " or " + Fields.ERRORCODE +
                    " fields if the ResponseType is equal to " + ResponseType.OK);
            throw new ResponseCreatorException();
        }

        response.put(toLowerString(newField), newData);
        return this;
    }

    /**
     * Convenience method to bulk add entries in a Response
     * @param bulkAdd a map containing multiple fields to add
     * @return a ResponseCreator with multiple fields added
     */
    public ResponseCreator add(Map<Fields, Object> bulkAdd) {
        for (Map.Entry<Fields, Object> e : bulkAdd.entrySet()) {
            add(e.getKey(), e.getValue());
        }
        return this;
    }

    /**
     * Convenience method for toString()
     * @return toString() invocation
     */
    public String getResponse() {
        return toString();
    }

    @Override
    public String toString() {
        return response.toString(2);
    }

    /**
     * Generic method to convert enum type to lowercase
     * @param toLowerEnum to enum type to lowercase
     * @param <T> the enum
     * @return the enum name in lowercase
     * @see ResponseType
     * @see Fields
     */
    private <T> String toLowerString(T toLowerEnum) {
        return toLowerEnum.toString().toLowerCase();
    }
}
