package com.frostsowner.magic.weather.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Collections;
import java.util.List;

public class ConnectionUtils {

	private static final String TAG = "MU.ConnectionUtils";
	
	/**
	 * Get IP address from first non-localhost interface
	 *
	 *            true=return ipv4, false=return ipv6
	 * @return address or empty string
	 */
	public static String getIPAddress(boolean useIPv4) {
		try {
			List<NetworkInterface> interfaces = Collections.list(NetworkInterface.getNetworkInterfaces());
			for (NetworkInterface intf : interfaces) {
				List<InetAddress> addrs = Collections.list(intf.getInetAddresses());
				for (InetAddress addr : addrs) {
					if (!addr.isLoopbackAddress()) {
						String sAddr = addr.getHostAddress();
						// boolean isIPv4 =
						// InetAddressUtils.isIPv4Address(sAddr);
						boolean isIPv4 = sAddr.indexOf(':') < 0;

						if (useIPv4) {
							if (isIPv4)
								return sAddr;
						} else {
							if (!isIPv4) {
								int delim = sAddr.indexOf('%'); // drop ip6 zone
																// suffix
								return delim < 0 ? sAddr.toUpperCase() : sAddr.substring(0, delim).toUpperCase();
							}
						}
					}
				}
			}
		} catch (Exception ex) {
		} // for now eat exceptions
		return "";
	}

	/**
	 * 网络是否可用
	 * @param cotext
	 * @return true : 有网络可用； false ： 无网络可用
	 */
	public static boolean isConnectionAvailable(Context cotext) {
		boolean isConnected = false;
		ConnectivityManager connectivityManager = (ConnectivityManager) cotext
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (connectivityManager != null) {
			NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
			if (activeNetworkInfo == null || !activeNetworkInfo.isConnected()) {
				isConnected = false;
			} else {
				isConnected = true;
			}
		} else {
			Log.e(TAG, "Can't get connectivitManager");
		}
		return isConnected;
	}
}
