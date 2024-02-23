package com.viettel.vtnet.distributedjmeter.common;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.stream.Collectors;

public class GetLinuxSystemInfo {
  public static List<String> getListSystemIP() {
    List<String> ipAddresses = Collections.emptyList();
    try {
      Enumeration<NetworkInterface> nets = NetworkInterface.getNetworkInterfaces();
      ipAddresses = Collections.list(nets).stream()
          .flatMap(ni -> Collections.list(ni.getInetAddresses()).stream())
          .map(InetAddress::getHostAddress)
          .collect(Collectors.toList());
    } catch (Exception e) {
      e.printStackTrace();
    }
    return ipAddresses;
  }

  public static String getDefaultSystemJmeterBin() {
    return System.getProperty("user.home") + "/apache-jmeter-5.6.2/bin";
  }

  public static boolean isLinux() {
    return System.getProperty("os.name")
        .toLowerCase().startsWith("linux");
  }
}
