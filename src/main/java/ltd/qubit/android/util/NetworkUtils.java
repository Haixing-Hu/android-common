////////////////////////////////////////////////////////////////////////////////
//
//    Copyright (c) 2022 - 2024.
//    Haixing Hu, Qubit Co. Ltd.
//
//    All rights reserved.
//
////////////////////////////////////////////////////////////////////////////////
package ltd.qubit.android.util;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.Socket;
import java.util.Enumeration;

import android.annotation.TargetApi;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.os.Build;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NetworkUtils {

  private static final Logger LOGGER = LoggerFactory.getLogger(NetworkUtils.class);

  private static final String DEFAULT_MAC_ADDRESS = "02:00:00:00:00:00";

  private static final String MAC_ADDRESS_FILE = "/sys/class/net/wlan0/address";

  private static final String WIFI_INTERFACE = "wlan0";

  /**
   * Formats the IPv4 address.
   *
   * @param ip
   *     the IPv4 address to be formatted, represented as an integer.
   * @return
   *     the formatted IPv4 address.
   * @author Haixing Hu
   */
  public static String formatIpV4(final int ip) {
    final int x1 = (ip & 0xFF);
    final int x2 = ((ip >> 8) & 0xFF);
    final int x3 = ((ip >> 16) & 0xFF);
    final int x4 = ((ip >> 24) & 0xFF);
    return String.valueOf(x1) + '.' + x2 + '.' + x3 + '.' + x4;
  }

  /**
   * Tests whether the device is online, i.e., has an active connection.
   *
   * @param context
   *     the context of the current application.
   * @return
   *     {@code true} if the device is online, {@code false} otherwise.
   * @author Haixing Hu
   */
  public static boolean isOnline(final Context context) {
    final ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
    if (cm != null) {
      final Network network = cm.getActiveNetwork();
      if (network != null) {
        final NetworkCapabilities capabilities = cm.getNetworkCapabilities(network);
        if (capabilities != null) {
          return capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)
              || capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)
              || capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET);
        }
      }
    }
    return false;
  }

  /**
   * Tests whether the device can access the specified host at the specified port.
   *
   * @param host
   *     the host to be tested.
   * @param port
   *     the port to be tested.
   * @param timeout
   *     the timeout in milliseconds.
   * @return
   *     {@code true} if the device can access the specified host at the specified port,
   *     {@code false} otherwise.
   * @author Haixing Hu
   */
  public static boolean pingHost(final String host, final int port, final int timeout) {
    LOGGER.info("Connecting to {}:{}...", host, port);
    try (final Socket socket = new Socket()) {
      final InetSocketAddress address = new InetSocketAddress(host, port);
      socket.connect(address, timeout);
      LOGGER.info("Connect to {}:{} success.", host, port);
      return true;
    } catch (final IOException e) {
      LOGGER.error("Connect to {}:{} failed: {}", host, port, e.getMessage());
      return false;
    }
  }

//  /**
//   * 获取本机WIFI设备详细信息
//   *
//   * @param wifiInfos
//   * @return
//   */
//  public static NetWorkData getNetWorkInfo(NetWorkData wifiInfos) {
//    WifiManager mWifiManager = (WifiManager) UtilsApp.getApp()
//        .getApplicationContext()
//        .getSystemService(Context.WIFI_SERVICE);
//    WifiInfo info = mWifiManager.getConnectionInfo();
//    wifiInfos.current_wifi.bssid = info.getBSSID();
//    String ssid = info.getSSID().replace("\"", "");
//    wifiInfos.current_wifi.name = ssid;
//    wifiInfos.current_wifi.ssid = ssid;
//    wifiInfos.current_wifi.mac = info.getMacAddress();
//    wifiInfos.ip = int2ip(info.getIpAddress());
//    wifiInfos.configured_wifi.addAll(getAroundWifiDeciceInfo());
//    return wifiInfos;
//  }
//
//  public static String int2ip(int ipInt) {
//    StringBuilder sb = new StringBuilder();
//    sb.append(ipInt & 0xFF).append(".");
//    sb.append((ipInt >> 8) & 0xFF).append(".");
//    sb.append((ipInt >> 16) & 0xFF).append(".");
//    sb.append((ipInt >> 24) & 0xFF);
//    return sb.toString();
//  }

//  /**
//   * 搜索到的周边WIFI信号信息
//   *
//   * @return
//   */
//  public static List<NetWorkData.NetWorkInfo> getAroundWifiDeciceInfo() {
//    StringBuffer sInfo = new StringBuffer();
//    WifiManager mWifiManager = (WifiManager) UtilsApp.getApp()
//        .getApplicationContext()
//        .getSystemService(Context.WIFI_SERVICE);
//    List<ScanResult> scanResults = mWifiManager.getScanResults();//搜索到的设备列表
//    List<NetWorkData.NetWorkInfo> wifiLists = new ArrayList<>();
//    for (ScanResult scanResult : scanResults) {
//      wifiLists.add(new NetWorkData.NetWorkInfo(scanResult.BSSID,
//          scanResult.SSID,
//          scanResult.SSID));
//    }
//    return wifiLists;
//  }

  /**
   * 获取当前设备的 Wi-Fi MAC 地址。
   *
   * 不考虑 Android 6.0 以前的情况。
   *
   * @return
   *    当前设备的 Wi-Fi MAC 地址，或{@code null}若无法获取。
   */
  public static String getWifiMacAddress() {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
//      return getMacAddressBeforeAndroid6();
      LOGGER.error("This program is NOT compatible with Android under version 6.");
      return null;
    }
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
      return getWifiMacAddressForAndroid6ToAndroid7();
    }
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
      return getWifiMacAddressAboveAndroid7();
    }
    return null;
  }

//
//  /**
//   * 获取当前设备的 MAC 地址。
//   * 适用于 Android  6.0 之前（不包括6.0）。
//   *
//   * 必需的权限<code>&lt;uses-permission android:name="android.permission.ACCESS_WIFI_STATE"/&gt;</code>
//   *
//   * @return
//   *    当前设备的 MAC 地址，或{@code null}若无法获取。
//   */
//  private static String getMacAddressBeforeAndroid6() {
//    String mac = "02:00:00:00:00:00";
//    WifiManager wifi = (WifiManager) UtilsApp.getApp()
//        .getApplicationContext()
//        .getSystemService(Context.WIFI_SERVICE);
//    if (wifi == null) {
//      LOGGER.error("Cannot find Wi-Fi for this device.");
//      return null;
//    }
//    WifiInfo info = null;
//    try {
//      info = wifi.getConnectionInfo();
//    } catch (Exception e) {
//    }
//    if (info == null) {
//      return null;
//    }
//    mac = info.getMacAddress();
//    if (!TextUtils.isEmpty(mac)) {
//      mac = mac.toUpperCase(Locale.ENGLISH);
//    }
//    return mac;
//  }

  /**
   * 获取当前设备的 Wi-Fi MAC 地址。
   *
   * 适用于Android 6.0（包括）—— Android 7.0（不包括）。
   *
   * @return
   *    当前设备的 Wi-Fi MAC 地址，或{@code null}若无法获取。
   */
  private static String getWifiMacAddressForAndroid6ToAndroid7() {
    try {
      return new BufferedReader(new FileReader(MAC_ADDRESS_FILE)).readLine();
    } catch (IOException e) {
      LOGGER.error("Failed to read {}: {}", MAC_ADDRESS_FILE, e.getMessage(), e);
      return null;
    }
  }

  /**
   * 获取当前设备的 Wi-Fi MAC 地址。
   *
   * 适用于Android 7.0（包括）及以上。
   *
   * 此方法将遍历循环所有的网络接口，找到接口是 wlan0。必须的权限为
   * <code>
   *   &lt;uses-permission android:name="android.permission.INTERNET" /&gt;
   * </code>
   *
   * @return
   *     当前设备的 Wi-Fi MAC 地址，或{@code null}若无法获取。
   */
  private static String getWifiMacAddressAboveAndroid7() {
    try {
      final Enumeration<NetworkInterface> e = NetworkInterface.getNetworkInterfaces();
      while (e.hasMoreElements()) {
        final NetworkInterface nif = e.nextElement();
        if (!nif.getName().equalsIgnoreCase(WIFI_INTERFACE)) {
          continue;
        }
        byte[] macBytes = nif.getHardwareAddress();
        if (macBytes == null) {
          LOGGER.error("Can't get the hardware address of the network interface {}.", WIFI_INTERFACE);
          return null;
        }
        StringBuilder builder = new StringBuilder();
        for (byte b : macBytes) {
          builder.append(String.format("%02X:", b));
        }
        if (builder.length() > 0) {
          builder.deleteCharAt(builder.length() - 1);
        }
        return builder.toString();
      }
      LOGGER.error("Cannot find the network interface {}.", WIFI_INTERFACE);
      return null;
    } catch (Exception e) {
      LOGGER.error("Failed to enumerate the network interfaces: {}",e.getMessage(), e);
      return null;
    }
  }


  public static String getBluetoothMacAddress() {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
//      return Settings.Secure.getString(UtilsApp.getApp().getContentResolver(),
//          "bluetooth_address");
      LOGGER.error("This program is NOT compatible with Android under version 6.");
      return null;
    } else {
      return getBluetoothMacAddressAboveAndroid6();
    }
  }

  @TargetApi(23)
  public static String getBluetoothMacAddressAboveAndroid6() {
    final BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
    if (adapter == null) {
      LOGGER.error("The device has no bluetooth adaptor.");
      return null;
    }
    Class<? extends BluetoothAdapter> btAdapterClass = adapter.getClass();
    try {
      Class<?> btClass = Class.forName("android.bluetooth.IBluetooth");
      Field bluetooth = btAdapterClass.getDeclaredField("mService");
      bluetooth.setAccessible(true);
      Method btAddress = btClass.getMethod("getAddress");
      btAddress.setAccessible(true);
      return (String) btAddress.invoke(bluetooth.get(adapter));
    } catch (Exception e) {
      LOGGER.error("Failed to get the bluetooth MAC address: {}", e.getMessage(), e);
      return null;
    }
  }
}
