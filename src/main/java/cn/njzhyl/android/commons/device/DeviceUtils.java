////////////////////////////////////////////////////////////////////////////////
//
//    Copyright (c) 2017 - 2024.
//    Nanjing Smart Medical Investment Operation Service Co. Ltd.
//
//    All rights reserved.
//
////////////////////////////////////////////////////////////////////////////////
package cn.njzhyl.android.commons.device;

import java.util.UUID;

import android.Manifest;
import android.content.Context;
import android.os.Build;
import android.telephony.TelephonyManager;
import androidx.annotation.RequiresPermission;
import cn.njzhyl.android.commons.net.NetworkUtils;
import cn.njzhyl.commons.lang.Hash64;
import cn.njzhyl.model.device.Device;
import cn.njzhyl.model.device.Hardware;
import cn.njzhyl.model.device.Software;
import cn.njzhyl.model.system.Platform;

public class DeviceUtils {

  /**
   * 获取此设备信息。
   *
   * @param context
   *     此应用的上下文。
   * @return
   *     此设备信息。
   * @author Haixing Hu
   */
  @RequiresPermission(allOf = {
      Manifest.permission.READ_PHONE_STATE,
      Manifest.permission.ACCESS_NETWORK_STATE
  })
  public static Device getDeviceInfo(final Context context) {
    final Device device = new Device();
    final Hardware hardware = getHardwareInfo(context);
    final Software os = getOperatingSystemInfo();
    device.setHardware(hardware);
    device.setOperatingSystem(os);
    final String udid = getUDID(hardware);
    device.setUdid(udid);
    return device;
  }

  /**
   * 通过读取设备的ROM版本号、厂商名、CPU型号和其他硬件信息来组合出一串15位的号码。
   * <p>
   * 此函数通过“Build.getSerial()”的值来保证ID的独一无二。
   *
   * <p>需要拥有权限{@code android.permission.READ_PRIVILEGED_PHONE_STATE}。
   *
   * @param context
   *     此应用的上下文。
   * @return
   *     当前设备的伪唯一ID.
   * @see #getUDID(Hardware)
   * @author Haixing Hu
   */
  @RequiresPermission(allOf = {
      Manifest.permission.READ_PHONE_STATE,
      Manifest.permission.ACCESS_NETWORK_STATE
  })
  public static String getUDID(final Context context) {
    final Hardware hardwareInfo = getHardwareInfo(context);
    return getUDID(hardwareInfo);
  }

  /**
   * 通过指定设备的硬件信息构造该设备的唯一ID。
   * <p>
   * 注意我们只能使用硬件相关的参数构造UDID，不能用软件版本号，系统版本号，编译号等软件相
   * 关参数。另外需注意为了利用到更多的信息构造UDID，我们使用64位哈希值。
   *
   * @param hardware
   *     指定设备的硬件信息。
   * @return
   *     指定设备的伪唯一ID.
   * @see #getUDID(Context)
   * @author Haixing Hu
   */
  public static String getUDID(final Hardware hardware) {
    final long multiplier = 3;
    long hash = 17;
    hash = Hash64.combine(hash, multiplier, hardware.getDevice());
    hash = Hash64.combine(hash, multiplier, hardware.getModel());
    hash = Hash64.combine(hash, multiplier, hardware.getBrand());
    hash = Hash64.combine(hash, multiplier, hardware.getManufacturer());
    hash = Hash64.combine(hash, multiplier, hardware.getProduct());
    hash = Hash64.combine(hash, multiplier, hardware.getDisplay());
    hash = Hash64.combine(hash, multiplier, hardware.getBoard());
    hash = Hash64.combine(hash, multiplier, hardware.getHardware());
    hash = Hash64.combine(hash, multiplier, hardware.getSupportedAbis());
    hash = Hash64.combine(hash, multiplier, hardware.getMacAddresses());
    hash = Hash64.combine(hash, multiplier, hardware.getImei());
    hash = Hash64.combine(hash, multiplier, hardware.getMeid());
    final String serial = hardware.getSerial();
    return new UUID(hash, Hash64.hash(serial)).toString();
  }

  /**
   * 获取当前设备的硬件信息。
   *
   * @param context
   *     此应用的上下文。
   * @return
   *     当前设备的硬件信息。
   */
  @RequiresPermission(allOf = {
    Manifest.permission.READ_PHONE_STATE,
    Manifest.permission.ACCESS_NETWORK_STATE
  })
  public static Hardware getHardwareInfo(final Context context) {
    final Hardware hardware = new Hardware();
    hardware.setDevice(Build.DEVICE);
    hardware.setModel(Build.MODEL);
    hardware.setBrand(Build.BRAND);
    hardware.setManufacturer(Build.MANUFACTURER);
    hardware.setProduct(Build.PRODUCT);
    hardware.setDisplay(Build.DISPLAY);
    hardware.setBoard(Build.BOARD);
    hardware.setHardware(Build.HARDWARE);
    hardware.setSupportedAbis(Build.SUPPORTED_ABIS);
    hardware.setMacAddresses(NetworkUtils.getMacAddresses());
    final TelephonyManager tm = SimCardUtils.getTelephonyManager(context);
    if (tm != null) {
      hardware.setImei(SimCardUtils.getImei(tm));
      hardware.setMeid(SimCardUtils.getMeid(tm));
      hardware.setIccid(SimCardUtils.getIccid(tm));
    }
    hardware.setSerial(Build.getSerial());
    final String udid = getUDID(hardware);
    hardware.setUdid(udid);
    return hardware;
  }

  /**
   * 获取当前设备的安卓操作系统的软件信息。
   *
   * @return
   *     当前设备的安卓操作系统的软件信息。
   */
  public static Software getOperatingSystemInfo() {
    final Software software = new Software();
    software.setPlatform(Platform.ANDROID);
    software.setName("Android");
    software.setVersion(Build.VERSION.RELEASE);
    software.setBuild(Build.VERSION.INCREMENTAL);
    software.setPatch(Build.VERSION.SECURITY_PATCH);
    software.setCodeName(Build.VERSION.CODENAME);
    software.setManufacturer("Google");
    software.setDescription("Google Android " + Build.VERSION.RELEASE);
    return software;
  }
}
