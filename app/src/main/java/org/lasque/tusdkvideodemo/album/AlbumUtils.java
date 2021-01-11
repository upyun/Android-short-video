package org.lasque.tusdkvideodemo.album;

import android.content.Context;
import android.content.Intent;

import org.lasque.tusdk.core.TuSdkContext;

/**
 * 相册工具类
 */


public class AlbumUtils
{
    /**
     * 打开相册选取视频
     */
    public static void openVideoAlbum(String intentClassName,int selectMax)
    {
        Context context = TuSdkContext.context();
        if (context == null) return;

        Intent intent = new Intent(context,MovieAlbumActivity.class);
        intent.putExtra("cutClassName",intentClassName);
        intent.putExtra("selectMax", selectMax);
        // 修复个别机型上使用ApplicationContext启动Activity崩溃问题
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    public static void openMediaAlbum(String intentClassName,int selectMax){
        Context context = TuSdkContext.context();
        if (context == null) return;

        Intent intent = new Intent(context,MediaAlbumActivity.class);
        intent.putExtra("cutClassName",intentClassName);
        intent.putExtra("selectMax", selectMax);
        // 修复个别机型上使用ApplicationContext启动Activity崩溃问题
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }
}
