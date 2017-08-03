package com.upyun.upplayer.utils;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.provider.Settings.Secure;
import android.telephony.TelephonyManager;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class DeviceIdUtil {
	public static String getDeviceId(Context context) {
		//1.IMEI
		TelephonyManager TelephonyMgr = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE); 
		String m_szImei = TelephonyMgr.getDeviceId(); 
		
		//2.Pseudo-Unique ID, 这个在任何Android手机中都有效
		String m_szDevIDShort = "35" + //we make this look like a valid IMEI 
		Build.BOARD.length()%10 + 
		Build.BRAND.length()%10 + 
		Build.CPU_ABI.length()%10 + 
		Build.DEVICE.length()%10 + 
		Build.DISPLAY.length()%10 + 
		Build.HOST.length()%10 + 
		Build.ID.length()%10 + 
		Build.MANUFACTURER.length()%10 + 
		Build.MODEL.length()%10 + 
		Build.PRODUCT.length()%10 + 
		Build.TAGS.length()%10 + 
		Build.TYPE.length()%10 + 
		Build.USER.length()%10 ; //13 digits
		//3. The Android ID
		String m_szAndroidID = Secure.getString(context.getContentResolver(), Secure.ANDROID_ID);
		//4. The WLAN MAC Address string
		WifiManager wm = (WifiManager)context.getSystemService(Context.WIFI_SERVICE); 
		String m_szWLANMAC = wm.getConnectionInfo().getMacAddress();
		//5. The BT MAC Address string
		BluetoothAdapter m_BluetoothAdapter = null; // Local Bluetooth adapter      
		m_BluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		String m_szBTMAC = "";
		if (m_BluetoothAdapter != null)
			m_szBTMAC = m_BluetoothAdapter.getAddress();
		
		//Combined Device ID
		String m_szLongID = m_szImei + m_szDevIDShort 
			    + m_szAndroidID+ m_szWLANMAC + m_szBTMAC;      
			// compute md5     
			 MessageDigest m = null;   
			try {
			 m = MessageDigest.getInstance("MD5");
			 } catch (NoSuchAlgorithmException e) {
			 e.printStackTrace();   
			}    
			m.update(m_szLongID.getBytes(),0,m_szLongID.length());   
			// get md5 bytes   
			byte p_md5Data[] = m.digest();   
			// create a hex string   
			String m_szUniqueID = new String();   
			for (int i=0;i<p_md5Data.length;i++) {   
			     int b =  (0xFF & p_md5Data[i]);    
			// if it is a single digit, make sure it have 0 in front (proper padding)    
			    if (b <= 0xF) 
			        m_szUniqueID+="0";    
			// add number to string    
			    m_szUniqueID+=Integer.toHexString(b); 
			   }   // hex string to uppercase   
			m_szUniqueID = m_szUniqueID.toUpperCase();
			return m_szUniqueID;
	}
}
