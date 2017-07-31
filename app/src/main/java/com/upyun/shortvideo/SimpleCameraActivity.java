/**
 * TuSDKVideoDemo
 * SimpleCameraActivity.java
 *
 * @author Yanlin
 * @Date 7:19:13 PM
 * @Copright (c) 2015 tusdk.com. All rights reserved.
 */
package com.upyun.shortvideo;

import org.lasque.tusdk.core.TuSdk;
import org.lasque.tusdk.core.utils.TuSdkWaterMarkOption.WaterMarkPosition;
import org.lasque.tusdk.core.utils.hardware.CameraConfigs.CameraAntibanding;
import org.lasque.tusdk.core.utils.hardware.TuSDKRecordVideoCamera;
import org.lasque.tusdk.core.utils.image.BitmapHelper;
import org.lasque.tusdk.core.video.TuSDKVideoCaptureSetting;
import org.lasque.tusdk.core.video.TuSDKVideoCaptureSetting.AVCodecType;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.media.MediaMetadataRetriever;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.RelativeLayout;

/**
 * 相机界面父类
 *
 * @author Yanlin
 */
public class SimpleCameraActivity extends Activity {
    protected TuSDKRecordVideoCamera mVideoCamera;

    // 页面状态
    protected boolean mActived = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        // 设置弹窗提示是否在隐藏虚拟键的情况下使用
        TuSdk.messageHub().applyToViewWithNavigationBarHidden(false);
        mActived = true;
    }

    protected void initCamera() {
        RelativeLayout cameraView = (RelativeLayout) findViewById(com.upyun.shortvideo.R.id.lsq_cameraView);

        // 录制相机采集配置，目前只支持硬编
        TuSDKVideoCaptureSetting captureSetting = new TuSDKVideoCaptureSetting();
        captureSetting.fps = 30;
        captureSetting.videoAVCodecType = AVCodecType.HW_CODEC;

        mVideoCamera = new TuSDKRecordVideoCamera(getBaseContext(), captureSetting, cameraView);
        // 是否开启动态贴纸
        mVideoCamera.setEnableLiveSticker(true);
        // 是否开启美颜 (默认: false)
        mVideoCamera.setEnableBeauty(true);
        // 禁用自动持续对焦 (默认: false)
        mVideoCamera.setDisableContinueFoucs(true);
        // 启用防闪烁功能，默认关闭。
        mVideoCamera.setAntibandingMode(CameraAntibanding.Auto);

        // 设置水印，默认为空
        mVideoCamera.setWaterMarkImage(BitmapHelper.getBitmapFormRaw(this, com.upyun.shortvideo.R.raw.sample_watermark));
        mVideoCamera.setWaterMarkPosition(WaterMarkPosition.TopLeft);

        mVideoCamera.initOutputSettings();
    }

    protected String getStringFromResource(String fieldName) {
        int stringID = this.getResources().getIdentifier(fieldName, "string", this.getApplicationContext().getPackageName());

        return getResources().getString(stringID);
    }

    /**
     * 隐藏虚拟按键，并且全屏
     */
    @TargetApi(Build.VERSION_CODES.KITKAT)
    protected void hideNavigationBar() {
        if (Build.VERSION.SDK_INT > 11 && Build.VERSION.SDK_INT < 19) {
            View decorview = getWindow().getDecorView();
            decorview.setSystemUiVisibility(View.GONE);
        } else if (Build.VERSION.SDK_INT >= 19) {
            View decorView = getWindow().getDecorView();
            int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY | View.SYSTEM_UI_FLAG_FULLSCREEN;
            decorView.setSystemUiVisibility(uiOptions);
        }
    }

    /**
     * 根据视频路径获取视频时长
     *
     * @param videoPath
     * @return
     */
    public float getVideoDuration(String videoPath) {
        MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever();
        mediaMetadataRetriever.setDataSource(videoPath);
        // 播放时长单位为毫秒
        String duration = mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);

        // 如果 duration 为 null，返回 0
        return duration == null ? 0 : Integer.parseInt(duration) / 1000;
    }

    /**
     * 根据 className 打开对应 Activity
     *
     * @param className
     * @param path
     */
    protected void startActivityWithClassName(String className, String path) {
        if (mVideoCamera != null) {
            mVideoCamera.destroy();
            mVideoCamera = null;
        }

        try {
            Intent intent = new Intent(this, Class.forName(className));
            intent.putExtra("videoPath", path);
            startActivity(intent);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        this.finish();
    }

    /**
     * Start camera capturing
     */
    protected void startCameraCapture() {
        if (mVideoCamera == null) return;

        mVideoCamera.startCameraCapture();
    }

    /**
     * Resume camera capturing
     */
    protected void resumeCameraCapture() {
        if (mVideoCamera == null) return;

        mVideoCamera.resumeCameraCapture();
    }

    /**
     * Pause camera capturing
     */
    protected void pauseCameraCapture() {
        if (mVideoCamera == null) return;

        mVideoCamera.pauseCameraCapture();
    }

    /**
     * Stop camera capturing
     */
    protected void stopCameraCapture() {
        if (mVideoCamera == null) return;

        mVideoCamera.stopCameraCapture();
    }

    @Override
    protected void onStop() {
        super.onStop();

        mActived = false;
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (!mActived) {
            startCameraCapture();
        }

        mActived = true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        mActived = false;

        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        stopCameraCapture();
        TuSdk.messageHub().dismissRightNow();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        stopCameraCapture();

        if (mVideoCamera != null) {
            mVideoCamera.destroy();
        }
    }
}
