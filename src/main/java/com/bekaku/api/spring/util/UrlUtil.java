package com.bekaku.api.spring.util;

import java.net.InetAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.UnknownHostException;

public class UrlUtil {

    private UrlUtil() {
    }

    public static void validatePublicUrl(String url) {
        URI uri;
        try {
            uri = new URI(url);
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException("Invalid url");
        }
        String scheme = uri.getScheme() != null ? uri.getScheme().toLowerCase() : null;
        if ((!"http".equals(scheme) && !"https".equals(scheme)) || uri.getHost() == null) {
            throw new IllegalArgumentException("Only http and https urls are allowed");
        }
        try {
            for (InetAddress address : InetAddress.getAllByName(uri.getHost())) {
                if (isInternalAddress(address)) {
                    throw new IllegalArgumentException("Access to internal networks is not allowed");
                }
            }
        } catch (UnknownHostException e) {
            throw new IllegalArgumentException("Unknown host");
        }
    }

    private static boolean isInternalAddress(InetAddress address) {
        if (address.isAnyLocalAddress() || address.isLoopbackAddress()
                || address.isLinkLocalAddress() || address.isSiteLocalAddress()
                || address.isMulticastAddress()) {
            return true;
        }
        byte[] bytes = address.getAddress();
        return bytes.length == 16 && (bytes[0] & 0xfe) == 0xfc;
    }

    public static String resolveRedirect(String base, String location) {
        try {
            return new URI(base).resolve(location).toString();
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException("Invalid redirect location");
        }
    }
}
