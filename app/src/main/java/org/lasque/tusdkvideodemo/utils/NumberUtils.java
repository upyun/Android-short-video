package org.lasque.tusdkvideodemo.utils;

import java.text.DecimalFormat;

/**
 * droid-sdk-video
 *
 * @author MirsFang
 * @Date 2018/11/6 21:57
 * @Copright (c) 2018 tusdk.com. All rights reserved.
 * <p>
 * 数字工具类
 */
public class NumberUtils {

    /**
     * 格式化为两位保留两位小数
     *
     * @return 格式化之后的数字
     */
    public static float formatFloat2f(float num) {
        DecimalFormat fnum = new DecimalFormat(".00");
        return Float.valueOf(fnum.format(num));
    }
}
