package com.upyun.upplayer.common;

/**
 * 视频卡顿{@link Config#SwitchBlockTime}次后 自动向下切换码率
 * <p>
 * 视频卡顿{@link Config#PostBlockTime}次后 自动上传统计数据
 * <p>
 * 视频播放统计信息上传至{@link Config#postAddress}
 */
public class Config {

    public static final String  VERSION = "upyun player 1.0";
    public static int SwitchBlockTime = 5;
    public static int PostBlockTime = 5;
    public static String postAddress="http://124.160.114.202:18989/echo";
}
