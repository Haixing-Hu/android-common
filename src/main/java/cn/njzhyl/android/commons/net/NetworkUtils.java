////////////////////////////////////////////////////////////////////////////////
//
//    Copyright (c) 2017 - 2024.
//    Nanjing Smart Medical Investment Operation Service Co. Ltd.
//
//    All rights reserved.
//
////////////////////////////////////////////////////////////////////////////////
package cn.njzhyl.android.commons.net;

import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.Manifest;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import androidx.annotation.RequiresPermission;
import cn.njzhyl.commons.util.filter.Filter;

import static cn.njzhyl.commons.net.InetAddressUtils.formatMacAddress;

public class NetworkUtils {

  private static final Logger LOGGER = LoggerFactory.getLogger(NetworkUtils.class);

  private static final String ETHERNET_NAME_REGEXP = "^(?i)(eth|en)\\d*$";

  private static final String WIFI_NAME_REGEXP = "^(?i)wlan\\d*$";

  private static final String FAKE_MAC_ADDRESS = "02:00:00:00:00:00";

  /**
   * 获取当前设备所有以太网网卡的MAC地址，按字典序从小到大排序。
   * <p>
   * 此函数的实现适用于安卓8及以上系统。
   * <p>
   * 此函数需要在`AndroidManifest.xml`中添加以下权限：
   * <p>
   * <pre><code>
   * <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
   * </code></pre>
   *
   * @return
   *     当前设备所有以太网网卡的MAC地址，按字典序从小到大排序。如无法获取，返回{@code null}；
   *     如当前设备没有以太网网卡，返回空数组。
   */
  @RequiresPermission(Manifest.permission.ACCESS_NETWORK_STATE)
  public static String[] getEthernetMacAddresses() {
    try {
      return getMacAddressesImpl((i) ->
          (!i.isVirtual()) && i.getName().matches(ETHERNET_NAME_REGEXP)
      );
    } catch (final Exception e) {
      LOGGER.error("Failed to get the ethernet MAC addresses: {}", e.getMessage(), e);
      return null;
    }
  }

  /**
   * 获取当前设备所有Wi-Fi网卡的MAC地址，按字典序从小到大排序。
   * <p>
   * 此函数的实现适用于安卓8及以上系统。
   * <p>
   * 此函数需要在`AndroidManifest.xml`中添加以下权限：
   * <p>
   * <pre><code>
   * <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
   * </code></pre>
   *
   * @return
   *     当前设备所有Wi-Fi网卡的MAC地址，按字典序从小到大排序。如无法获取，返回{@code null}；
   *     如当前设备没有Wi-Fi网卡，返回空数组。
   */
  @RequiresPermission(Manifest.permission.ACCESS_NETWORK_STATE)
  public static String[] getWiFiMacAddresses() {
    try {
      return getMacAddressesImpl((i) ->
        (!i.isVirtual()) && i.getName().matches(WIFI_NAME_REGEXP)
      );
    } catch (final Exception e) {
      LOGGER.error("Failed to get the Wi-Fi MAC addresses: {}", e.getMessage(), e);
      return null;
    }
  }

  /**
   * 获取当前设备所有非虚拟网卡的MAC地址，按字典序从小到大排序。
   * <p>
   * 此函数的实现适用于安卓8及以上系统。
   * <p>
   * 此函数需要在`AndroidManifest.xml`中添加以下权限：
   * <p>
   * <pre><code>
   * <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
   * </code></pre>
   *
   * @return
   *     当前设备所有非虚拟网卡的MAC地址，按字典序从小到大排序。如无法获取，返回{@code null}；
   *     如当前设备没有非虚拟网卡，返回空数组。
   */
  @RequiresPermission(Manifest.permission.ACCESS_NETWORK_STATE)
  public static String[] getMacAddresses() {
    try {
      return getMacAddressesImpl((i) -> (!i.isVirtual()));
    } catch (final Exception e) {
      LOGGER.error("Failed to get the non-virtual MAC addresses: {}", e.getMessage(), e);
      return null;
    }
  }

  /**
   * 获取当前设备所有WIFI网卡的MAC地址，按字典序从小到大排序。
   *
   * @param filter
   *     过滤器，用于过滤网卡。只有名称满足过滤器的要求的网卡才会被返回。
   * @return
   *     当前设备所有WIFI网卡的MAC地址，按字典序从小到大排序。
   * @throws SocketException
   *     如果发生Socket异常。
   * @author 胡海星
   */
  @RequiresPermission(Manifest.permission.ACCESS_NETWORK_STATE)
  private static String[] getMacAddressesImpl(final Filter<NetworkInterface> filter)
      throws SocketException {
    final Enumeration<NetworkInterface> e = NetworkInterface.getNetworkInterfaces();
    final List<String> result = new ArrayList<>();
    while (e.hasMoreElements()) {
      final NetworkInterface nif = e.nextElement();
      if (filter.accept(nif)) {
        final byte[] macBytes = nif.getHardwareAddress();
        if (macBytes == null) {
          LOGGER.warn("Can't get the hardware MAC address of the network interface: {}",
              nif.getName());
          continue;
        }
        final String macAddress = formatMacAddress(macBytes);
        if (macAddress.equals(FAKE_MAC_ADDRESS)) {
          LOGGER.warn("The hardware MAC address of the network interface {} is fake: {}",
              nif.getName(), macAddress);
          continue;
        }
        result.add(macAddress);
      }
    }
    result.sort(null);
    return result.toArray(new String[0]);
  }

  /**
   * 获取当前设备的 ConnectivityManager.
   *
   * @param context
   *     此应用的上下文。
   * @return
   *     当前设备的 ConnectivityManager.
   */
  public static ConnectivityManager getConnectivityManager(final Context context) {
    return (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
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
    final ConnectivityManager cm = getConnectivityManager(context);
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
}
