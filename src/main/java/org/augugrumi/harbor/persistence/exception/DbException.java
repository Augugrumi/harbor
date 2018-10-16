package org.augugrumi.harbor.persistence.exception;

public class DbException extends RuntimeException {
    public DbException(String e) {
        super(e);
    }
}
