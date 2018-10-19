package org.augugrumi.harbor.routes.util;

import java.net.InetAddress;

public class UpdateError {

    private final InetAddress addr;
    private final String cause;

    public UpdateError(InetAddress addr, String cause) {
        this.addr = addr;
        this.cause = cause;
    }

    public InetAddress getAddr() {
        return addr;
    }

    public String getCause() {
        return cause;
    }

    @Override
    public String toString() {
        return addr.getHostAddress() + " - " + cause;
    }
}
