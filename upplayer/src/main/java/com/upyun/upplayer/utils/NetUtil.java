package com.upyun.upplayer.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.upyun.upplayer.model.IP;
import com.upyun.upplayer.model.Monitor;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URL;
import java.util.List;


public class NetUtil {

    private static final String TAG = "NetUtil";

    /**
     * 判断当前是否网络连接
     *
     * @param context
     * @return 状态码
     */
    public static NetState isConnected(Context context) {
        NetState stateCode = NetState.NET_NO;
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        if (ni != null && ni.isConnectedOrConnecting()) {
            switch (ni.getType()) {
                case ConnectivityManager.TYPE_WIFI:
                    stateCode = NetState.NET_WIFI;
                    break;
                case ConnectivityManager.TYPE_MOBILE:
                    switch (ni.getSubtype()) {
                        case TelephonyManager.NETWORK_TYPE_GPRS: //联通2g
                        case TelephonyManager.NETWORK_TYPE_CDMA: //电信2g
                        case TelephonyManager.NETWORK_TYPE_EDGE: //移动2g
                        case TelephonyManager.NETWORK_TYPE_1xRTT:
                        case TelephonyManager.NETWORK_TYPE_IDEN:
                            stateCode = NetState.NET_2G;
                            break;
                        case TelephonyManager.NETWORK_TYPE_EVDO_A: //电信3g
                        case TelephonyManager.NETWORK_TYPE_UMTS:
                        case TelephonyManager.NETWORK_TYPE_EVDO_0:
                        case TelephonyManager.NETWORK_TYPE_HSDPA:
                        case TelephonyManager.NETWORK_TYPE_HSUPA:
                        case TelephonyManager.NETWORK_TYPE_HSPA:
                        case TelephonyManager.NETWORK_TYPE_EVDO_B:
                        case TelephonyManager.NETWORK_TYPE_EHRPD:
                        case TelephonyManager.NETWORK_TYPE_HSPAP:
                            stateCode = NetState.NET_3G;
                            break;
                        case TelephonyManager.NETWORK_TYPE_LTE:
                            stateCode = NetState.NET_4G;
                            break;
                        default:
                            stateCode = NetState.NET_UNKNOWN;
                    }
                    break;
                default:
                    stateCode = NetState.NET_UNKNOWN;
            }
        }
        return stateCode;
    }

    /**
     * 获取客户端IP地址
     *
     * @param monitor
     */
    public static void getClientIp(final Monitor monitor) {

        new Thread() {
            @Override
            public void run() {

                HttpURLConnection urlConnection = null;
                try {
                    URL url = new URL("http://ip.taobao.com/service/getIpInfo.php?ip=myip");
                    urlConnection = (HttpURLConnection) url.openConnection();
                    int HttpResult = urlConnection.getResponseCode();
                    if (HttpResult == HttpURLConnection.HTTP_OK) {
                        InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                        String response = readStream(in);
//                        Log.e(TAG, "response:" + response);
                        Gson gson = new Gson();
                        IP ip = gson.fromJson(response, IP.class);
                        if (ip == null) {
                            return;
                        }
                        monitor.setClientIp(ip.getData().getIp());
                        monitor.setIsp(ip.getData().getIsp());
                        monitor.setCountry(ip.getData().getCountry());
                        monitor.setProvince(ip.getData().getRegion());
                        monitor.setCountry(ip.getData().getCity());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (urlConnection != null) {
                        urlConnection.disconnect();
                    }
                }
            }
        }.start();
    }

    private static String readStream(InputStream in) throws IOException {
        if (in.available() == 0) {
            return null;
        }
        byte[] buffer = new byte[1024];
        int length = in.read(buffer);
        in.close();
        return new String(buffer, 0, length);
    }


    /**
     * 上传统计信息到服务器
     *
     * @param monitorList
     */
    public static void postMonitor(List<Monitor> monitorList) {
        Gson gson = new Gson();
        Type typeOfSrc = new TypeToken<Monitor>() {
        }.getType();

        StringBuffer sb = new StringBuffer();

        for (Monitor monitor : monitorList) {
            String json = gson.toJson(monitor, typeOfSrc);
            sb.append(json + " \n");
        }

        final String result = sb.toString();
//        Log.e(TAG, result);

        new Thread() {
            @Override
            public void run() {

                try {
//                    String host = "60.191.72.5";
                    String host = "uplog.tianchaijz.me";
                    int port = 3100;
                    Socket client = new Socket(host, port);
                    PrintStream out = new PrintStream(client.getOutputStream());
                    out.print(result);
                    out.close();
                    client.close();
//                    Log.e(TAG, "post succced");
                } catch (IOException e) {
                    e.printStackTrace();
                }


            }
        }.start();
    }
}