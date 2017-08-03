package com.upyun.upplayer.utils;

/**
 * 枚举网络状态
 * NET_NO：没有网络
 * NET_2G:2g网络
 * NET_3G：3g网络
 * NET_4G：4g网络
 * NET_WIFI：wifi
 * NET_UNKNOWN：未知网络
 */
public enum NetState {
    NET_NO("没有网络"), NET_2G("2G"), NET_3G("3G"), NET_4G("4G"), NET_WIFI("WI-FI"), NET_UNKNOWN("未知网络");

    private String name;

    NetState(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}
