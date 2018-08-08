package k8s;

public class K8sCommandOutput<T> {

    final private boolean success;
    final private T attachment;

    public K8sCommandOutput(boolean success, T attachment) {
        this.success = success;
        this.attachment = attachment;
    }

    public boolean isSuccess() {
        return success;
    }

    public T getAttachment() {
        return attachment;
    }
}
