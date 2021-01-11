/**
 * droid-sdk-video
 * org.lasque.tusdkvideodemo.utils
 *
 * @author LiuHang
 * @Date 10/10/2017 10:43 AM
 * @Copright (c) 2017 tusdk.com. All rights reserved.
 */

package org.lasque.tusdkvideodemo.utils;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;

/**
 * 运行时权限授权工具类
 */

public class PermissionUtils
{
    // 请求权限的标示code
    public static final int REQUEST_PERMISSION_CODE = 1;
    /**
     * 权限授权结果委托
     */
    public interface GrantedResultDelgate
    {
        // 授予权限的结果，在对话结束后调用
         void onPermissionGrantedResult(boolean permissionGranted);
    }

    /**
     * 要求的权限是否已被授予
     *
     * @return
     *            true or false
     */
    @TargetApi(Build.VERSION_CODES.M)
    public static boolean hasRequiredPermissions(Activity activity, String[] permissions)
    {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) return true;

        if (permissions != null && permissions.length > 0)
        {
            for (String key : permissions)
            {
                if (activity.checkSelfPermission(key) != PackageManager.PERMISSION_GRANTED )
                {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * 请求权限
     */
    public static void requestRequiredPermissions(Activity activity, String[] permissions)
    {
        if (permissions != null && permissions.length > 0)
        {
            activity.requestPermissions(permissions, REQUEST_PERMISSION_CODE);
        }
    }

    /**
     * 处理用户的许可结果
     */
    public static void handleRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults, Activity activity,GrantedResultDelgate grantedResultDelegate)
    {
        if (requestCode == REQUEST_PERMISSION_CODE)
        {
            boolean isOK = true;

            for (int value : grantResults)
            {
                if (value != PackageManager.PERMISSION_GRANTED)
                {
                    isOK = false;
                    break;
                }
            }

            isOK = hasRequiredPermissions(activity, permissions);

            if (grantedResultDelegate != null) grantedResultDelegate.onPermissionGrantedResult(isOK);
        }
    }
}
