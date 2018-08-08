package routes.util;

public class FileNameUtils {

    public static String validateFileName(String toCheck) {
        // TODO consider the case where the filename is ending with .yml
        return toCheck.endsWith(".yaml") ? toCheck : toCheck + ".yaml";
    }
}
