/*
 * 蘑菇街 Inc.
 * Copyright (c) 2010-2015 All Rights Reserved.
 *
 * Author: wuya
 * Create Date: 2015年12月11日 下午2:01:02
 */

package com.mogujie.jarvis.core.util;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Enumeration;

import com.google.common.base.Throwables;

public class IPUtils {

    public static String getIPV4Address() {
        try {
            String ip = Inet4Address.getLocalHost().getHostName();
            if (!ip.startsWith("127")) {
                return ip;
            }

            Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
            while (networkInterfaces.hasMoreElements()) {
                NetworkInterface networkInterface = networkInterfaces.nextElement();
                Enumeration<InetAddress> inetAddresses = networkInterface.getInetAddresses();
                while (inetAddresses.hasMoreElements()) {
                    InetAddress inetAddress = inetAddresses.nextElement();
                    if (inetAddress instanceof Inet4Address && !inetAddress.isLoopbackAddress() && !inetAddress.isLinkLocalAddress()) {
                        return inetAddress.getHostAddress();
                    }
                }
            }
        } catch (UnknownHostException e) {
            Throwables.propagate(e);
        } catch (SocketException e) {
            Throwables.propagate(e);
        }

        return null;
    }
}
