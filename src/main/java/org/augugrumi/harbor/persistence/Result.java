package org.augugrumi.harbor.persistence;

public class Result<T> {

    private final boolean success;
    private final T content;

    public Result(boolean isSuccess, T content) {
        success = isSuccess;
        this.content = content;
    }

    public Result(boolean isSuccess) {
        success = isSuccess;
        this.content = null;
    }

    public boolean isSuccessful() {
        return success;
    }

    public T getContent() {
        return content;
    }

    ;
}
