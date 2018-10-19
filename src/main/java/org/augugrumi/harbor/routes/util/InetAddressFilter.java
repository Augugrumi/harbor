package org.augugrumi.harbor.routes.util;

import org.apache.commons.validator.routines.InetAddressValidator;

import java.net.InetAddress;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

public class InetAddressFilter {

    /**
     * Filters all the host possible IPv6
     *
     * @param initialUrl the host URL
     * @return a list containing only IPv4
     * @throws UnknownHostException @see InetAddress
     */
    public static List<InetAddress> filterIPv6(URL initialUrl) throws UnknownHostException {
        List<InetAddress> res = new ArrayList<>();
        for (InetAddress toCheck : InetAddress.getAllByName(initialUrl.getHost())) {
            if (InetAddressValidator.getInstance().isValidInet4Address(toCheck.getHostAddress())) {
                res.add(toCheck);
            }
        }
        return res;
    }

    /**
     * Filters all the host possible IPv4
     * @param initialUrl the host URL
     * @return a list containing only IPv6
     * @throws UnknownHostException @see InetAddress
     */
    public static List<InetAddress> filterIPv4(URL initialUrl) throws UnknownHostException {
        List<InetAddress> res = new ArrayList<>();
        for (InetAddress toCheck : InetAddress.getAllByName(initialUrl.getHost())) {
            if (InetAddressValidator.getInstance().isValidInet6Address(toCheck.getHostAddress())) {
                res.add(toCheck);
            }
        }
        return res;
    }
}
