package routes.util;

import org.json.JSONObject;
import org.slf4j.Logger;
import routes.util.exceptions.ResponseCreatorException;
import util.ConfigManager;

import java.util.Map;

public class ResponseCreator {

    final private static Logger LOG = ConfigManager.getConfig().getApplicationLogger(ResponseCreator.class);

    final private JSONObject response;

    public enum ResponseType {
        OK,
        ERROR
    }

    public enum Fields {
        RESULT,
        CONTENT,
        ERRORCODE,
        REASON
    }

    public ResponseCreator(ResponseType typeOfResponse) {
        this.response = new JSONObject();

        response.put(toLowerString(Fields.RESULT), toLowerString(typeOfResponse));
    }

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

    public ResponseCreator add(Map<Fields, Object> bulkAdd) {
        for (Map.Entry<Fields, Object> e : bulkAdd.entrySet()) {
            add(e.getKey(), e.getValue());
        }
        return this;
    }

    public String getResponse() {
        return toString();
    }

    @Override
    public String toString() {
        return response.toString(2);
    }

    private <T> String toLowerString(T toLowerEnum) {
        return toLowerEnum.toString().toLowerCase();
    }
}
