////////////////////////////////////////////////////////////////////////////////
//
//    Copyright (c) 2022 - 2024.
//    Haixing Hu, Qubit Co. Ltd.
//
//    All rights reserved.
//
////////////////////////////////////////////////////////////////////////////////
package ltd.qubit.android.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.telephony.TelephonyManager;
import cn.njzhyl.model.contact.Location;
import cn.njzhyl.model.contact.Phone;
import cn.njzhyl.model.device.DataNetworkType;
import cn.njzhyl.model.device.SimCardStatus;
import cn.njzhyl.model.util.Info;

public class SimCardUtils {

  private static final Logger LOGGER = LoggerFactory.getLogger(SimCardUtils.class);

  /**
   * 获取此设备的 TelephonyManager。
   *
   * @param context
   *     此应用的上下文。
   * @return
   *     此设备的TelephonyManager，或{@code null}若此设备不支持SIM卡。
   */
  public static TelephonyManager getTelephonyManager(final Context context) {
    final PackageManager pm = context.getPackageManager();
    if (pm.hasSystemFeature(PackageManager.FEATURE_TELEPHONY)) {
      return (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
    } else {
      return null;
    }
  }

  /**
   * 获取 IMEI  (International Mobile Equipment Identity) 码。
   *
   * <p>IMEI 码适用于GSM、WCDMA、LTE制式的移动电话和卫星电话。</p>
   *
   * <p>全球每部通过正规渠道销售的 GSM 手机均有唯一的 IMEI 码。IMEI 码由 GSMA 协会统一规划，
   * 并授权各地区组织进行分配，在中国由工业和信息化部电信终端测试技术协会（TAF）负责国内手机的
   * 入网认证，其他分配机构包括英国 BABT、美国 CTIA 等。</p>
   *
   * <p>使用此函数需添加权限
   * {@code &lt;uses-permission android:name="android.permission.READ_PHONE_STATE"/&gt;}和
   * {@code &lt;uses-permission android:name="android.permission.READ_PRIVILEGED_PHONE_STATE"/&gt;}</p>
   *
   * @param tm
   *     TelephoneManager对象。
   * @return
   *     当前SIM卡的 IMEI 码，或{@code null}若无法获取。
   */
  @SuppressLint("HardwareIds")
  public static String getImei(final TelephonyManager tm) {
    try {
      return tm.getImei();
    } catch (final Exception e) {
      LOGGER.error("Failed to get the IMEI of the SIM card slot: {}", e.getMessage(), e);
      return null;
    }
  }

  /**
   * 获取 MEID (Mobile Equipment Identifier) 码。
   *
   * <p>适用于 CDMA 制式手机，是一个由字母A-F、数字0-9组成的14位字符。从作用上说，MEID 也
   * 是移动网络中手机的唯一标识。</p>
   *
   * <p>MEID 号码是由 Telecommunications Industry Association（TIA）进行分配管理的。
   * 申请 MEID 是需要付费的。目前的价格是每 1M 范围的 MEID 的费用是 8000 美元，每增加 1M
   * 范围的 MEID 号码需要额外付费 8000 美元。
   *
   * <p>使用此函数需添加权限
   * {@code &lt;uses-permission android:name="android.permission.READ_PHONE_STATE"/&gt;}和
   * {@code &lt;uses-permission android:name="android.permission.READ_PRIVILEGED_PHONE_STATE"/&gt;}</p>
   *
   * @param tm
   *     TelephoneManager对象。
   * @return
   *     当前SIM卡的 IMEI 码，或{@code null}若无法获取。
   */
  @SuppressLint("HardwareIds")
  public static String getMeid(final TelephonyManager tm) {
    try {
      return tm.getMeid();
    } catch (final Exception e) {
      LOGGER.error("Failed to get the MEID of the SIM card slot: {}", e.getMessage(), e);
      return null;
    }
  }

  /**
   * 获取 ICCID  码。
   *
   * <p>需添加权限
   * {@code &lt;uses-permission android:name="android.permission.READ_PHONE_STATE"/&gt;}和
   * {@code &lt;uses-permission android:name="android.permission.READ_PRIVILEGED_PHONE_STATE"/&gt;}</p>
   *
   * @return IMSI码
   */
  @SuppressLint("HardwareIds")
  public static String getIccid(final TelephonyManager tm) {
    try {
      return tm.getSimSerialNumber();
    } catch (final Exception e) {
      LOGGER.error("Failed to get the ICCID of the SIM card: {}", e.getMessage(), e);
      return null;
    }
  }

  /**
   * 获取 IMSI  码。
   *
   * <p>需添加权限 {@code &lt;uses-permission android:name="android.permission.READ_PHONE_STATE"/&gt;}</p>
   *
   * @return IMSI码
   */
  @SuppressLint("HardwareIds")
  public static String getIMSI(final TelephonyManager tm) {
    try {
      return tm.getSubscriberId();
    } catch (final Exception e) {
      LOGGER.error("Failed to get the IMSI of the SIM card: {}", e.getMessage(), e);
      return null;
    }
  }

  /**
   * 判断sim卡是否准备好.
   *
   * @return {@code true}: 是<br>{@code false}: 否
   */
  public static boolean isReady(final TelephonyManager tm) {
    return tm.getSimState() == TelephonyManager.SIM_STATE_READY;
  }

  /**
   * 获取Sim卡运营商名称
   *
   * <p>中国移动、如中国联通、中国电信</p>
   *
   * @return sim卡运营商名称
   */
  public static String getOperator(final TelephonyManager tm) {
    final String name = getOperatorName(tm);
    if (name == null || name.isEmpty()) {
      return getOperatorNameByMnc(tm);
    } else {
      return name;
    }
  }

  /**
   * 获取Sim卡运营商名称
   *
   * <p>中国移动、如中国联通、中国电信</p>
   *
   * @return sim卡运营商名称
   */
  public static String getOperatorName(final TelephonyManager tm) {
    try {
      return tm.getSimOperatorName();
    } catch (final Exception e) {
      LOGGER.error("Failed to get the operator name of the SIM card: {}", e.getMessage(), e);
      return null;
    }
  }

  /**
   * 根据Sim卡运营商代码获取运营商名称。
   *
   * <p>在国内可能的名称包括：中国移动、中国联通、中国电信</p>
   *
   * @return
   *     移动网络运营商名称
   */
  public static String getOperatorNameByMnc(final TelephonyManager tm) {
    try {
      final String operator = tm.getSimOperator();
      if (operator == null) {
        return null;
      }
      switch (operator) {
        case "46000":
        case "46002":
        case "46007":
          return "中国移动";
        case "46001":
          return "中国联通";
        case "46003":
          return "中国电信";
        default:
          return operator;
      }
    } catch (final Exception e) {
      LOGGER.error("Failed to get the operator of the SIM card: {}", e.getMessage(), e);
      return null;
    }
  }

  /**
   * 获取Sim卡序列号
   *
   * <p>需添加权限 {@code &lt;uses-permission android:name="android.permission.READ_PHONE_STATE"/&gt;}</p>
   *
   * @return 序列号
   */
  @SuppressLint("HardwareIds")
  public static String getSerialNumber(final TelephonyManager tm) {
    try {
      return tm.getSimSerialNumber();
    } catch (final Exception e) {
      LOGGER.error("Failed to get the serial number of the SIM card: {}", e.getMessage(), e);
      return null;
    }
  }

  /**
   * 获取 SIM 卡所属国家的基本信息。
   *
   * @param tm
   *      Telephony Manager.
   * @return
   *      SIM 卡所属国家的基本信息，或{@code null}如果没有。
   */
  public static Info getCountryInfo(final TelephonyManager tm) {
    final String code = tm.getSimCountryIso();
    if (code == null || code.isEmpty()) {
      return null;
    } else {
      final Info country = new Info();
      country.setCode(code.toUpperCase());
      return country;
    }
  }

  public static SimCardStatus getStatus(final TelephonyManager tm) {
    final int state = tm.getSimState();
    switch (state) {
      case TelephonyManager.SIM_STATE_ABSENT:
        return SimCardStatus.ABSENT;
      case TelephonyManager.SIM_STATE_PIN_REQUIRED:
        return SimCardStatus.PIN_REQUIRED;
      case TelephonyManager.SIM_STATE_PUK_REQUIRED:
        return SimCardStatus.PUK_REQUIRED;
      case TelephonyManager.SIM_STATE_NETWORK_LOCKED:
        return SimCardStatus.NETWORK_LOCKED;
      case TelephonyManager.SIM_STATE_READY:
        return SimCardStatus.READY;
      case TelephonyManager.SIM_STATE_NOT_READY:
        return SimCardStatus.NOT_READY;
      case TelephonyManager.SIM_STATE_PERM_DISABLED:
        return SimCardStatus.PERM_DISABLED;
      case TelephonyManager.SIM_STATE_CARD_IO_ERROR:
        return SimCardStatus.CARD_IO_ERROR;
      case TelephonyManager.SIM_STATE_CARD_RESTRICTED:
        return SimCardStatus.CARD_RESTRICTED;
      case TelephonyManager.SIM_STATE_UNKNOWN:
      default:
        return SimCardStatus.UNKNOWN;
    }
  }

  @SuppressLint({"MissingPermission", "HardwareIds"})
  public static Phone getPhoneNumber(final TelephonyManager tm) {
    try {
      final String number = tm.getLine1Number();
      if (number == null || number.isEmpty()) {
        return null;
      } else {
        return new Phone(number);
      }
    } catch (final Exception e) {
      LOGGER.error("Failed to get the phone number of the SIM card: {}", e.getMessage(), e);
      return null;
    }
  }

  @SuppressLint("MissingPermission")
  public static DataNetworkType getNetworkType(final TelephonyManager tm) {
    try {
      final int type = tm.getDataNetworkType();
      switch (type) {
        case TelephonyManager.NETWORK_TYPE_GPRS:
          return DataNetworkType.GPRS;
        case TelephonyManager.NETWORK_TYPE_EDGE:
          return DataNetworkType.EDGE;
        case TelephonyManager.NETWORK_TYPE_UMTS:
          return DataNetworkType.UMTS;
        case TelephonyManager.NETWORK_TYPE_HSDPA:
          return DataNetworkType.HSDPA;
        case TelephonyManager.NETWORK_TYPE_HSUPA:
          return DataNetworkType.HSUPA;
        case TelephonyManager.NETWORK_TYPE_HSPA:
          return DataNetworkType.HSPA;
        case TelephonyManager.NETWORK_TYPE_CDMA:
          return DataNetworkType.CDMA;
        case TelephonyManager.NETWORK_TYPE_EVDO_0:
          return DataNetworkType.EVDO_0;
        case TelephonyManager.NETWORK_TYPE_EVDO_A:
          return DataNetworkType.EVDO_A;
        case TelephonyManager.NETWORK_TYPE_EVDO_B:
          return DataNetworkType.EVDO_B;
        case TelephonyManager.NETWORK_TYPE_1xRTT:
          return DataNetworkType.ONE_X_RTT;
        case TelephonyManager.NETWORK_TYPE_IDEN:
          return DataNetworkType.IDEN;
        case TelephonyManager.NETWORK_TYPE_LTE:
          return DataNetworkType.LTE;
        case TelephonyManager.NETWORK_TYPE_EHRPD:
          return DataNetworkType.EHRPD;
        case TelephonyManager.NETWORK_TYPE_HSPAP:
          return DataNetworkType.HSPAP;
        // case TelephonyManager.NETWORK_TYPE_NR:
        //   return DataNetworkType.NR;
        case TelephonyManager.NETWORK_TYPE_GSM:
          return DataNetworkType.GSM;
        case TelephonyManager.NETWORK_TYPE_IWLAN:
          return DataNetworkType.IWLAN;
        case TelephonyManager.NETWORK_TYPE_TD_SCDMA:
          return DataNetworkType.TD_SCDMA;
        case TelephonyManager.NETWORK_TYPE_UNKNOWN:
        default:
          return DataNetworkType.UNKNOWN;
      }
    } catch (final Exception e) {
      LOGGER.error("Failed to get the data network type of the SIM card: {}", e.getMessage(), e);
      return null;
    }
  }

  public static Location getLocation(final TelephonyManager tm) {
    // TODO
    return null;
  }

//  @SuppressLint("MissingPermission")
//  public static Location getLocation(final TelephonyManager tm) {
//    final List<CellInfo> cellInfos = tm.getAllCellInfo();
//    if (cellInfos == null) {
//      LOGGER.warn("The cell information is unavailable.");
//      return null;
//    }
//    for (final CellInfo info : cellInfos) {
//      if (info.isRegistered()) {
//        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
//          final CellIdentity identity = info.getCellIdentity();
//        }
//
//      }
//    }
//    LOGGER.warn("No cell information is registered.");
//    return null;
//  }


//  @SuppressLint("MissingPermission")
//  public static Location getLocationBy(final TelephonyManager tm) {
//    try {
//      final CellLocation cellLocation = tm.getCellLocation();
//      if (cellLocation == null) {
//        LOGGER.warn("The cell location is unavailable.");
//        return null;
//      }
//      if (cellLocation instanceof CdmaCellLocation) {
//
//
//      } else if (cellLocation instanceof GsmCellLocation) {
//
//
//      } else {
//        LOGGER.warn("Unsupported cell location: {}", cellLocation);
//        return null;
//      }
//    } catch (Exception e) {
//      LOGGER.error("Failed to get the cell location: {}", e.getMessage(), e);
//      return null;
//    }
//  }
}
