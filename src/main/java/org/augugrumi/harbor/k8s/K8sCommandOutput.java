package org.augugrumi.harbor.k8s;

/**
 * Generic class containing the output of the Kubernetes command
 *
 * @param <T> indicates the format containing the output, that could inside whatever you like: a JSON, an XML but also a
 *            String
 */
public class K8sCommandOutput<T> {

    final private boolean success;
    final private T attachment;

    /**
     * Class constructor.
     * @param success boolean indicating if the commands exited successfully or not
     * @param attachment the output, generically structured for type T
     */
    public K8sCommandOutput(boolean success, T attachment) {
        this.success = success;
        this.attachment = attachment;
    }

    /**
     * Getter method to determine is the command exited successfully or not
     * @return True if the command was successful, False otherwise
     */
    public boolean isSuccess() {
        return success;
    }

    /**
     * Getter method to return the command output
     * @return the command output with the generic type T
     */
    public T getAttachment() {
        return attachment;
    }
}
