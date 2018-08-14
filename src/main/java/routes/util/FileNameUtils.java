package routes.util;

/**
 * Class to standardize file name
 */
public class FileNameUtils {

    /**
     * It add ".yaml" extension to id without it
     *
     * @param toCheck id to check
     * @return a standardized id
     */
    public static String validateFileName(String toCheck) {
        // TODO consider the case where the filename is ending with .yml
        return toCheck.matches(".*[.]ya?ml") ? toCheck : toCheck + ".yaml";
    }
}
