/**
 * TuSDKVideoDemo
 * MultipleCameraActivity.java
 *
 * @author     LiuHang
 * @Date:      May 15, 2017 7:28:12 PM
 * @Copyright: (c) 2017 tusdk.com. All rights reserved.
 *
 */
package com.upyun.shortvideo.component;

import org.lasque.tusdk.core.encoder.video.TuSDKVideoEncoderSetting;
import org.lasque.tusdk.core.seles.sources.SelesOutInput;
import org.lasque.tusdk.core.seles.sources.SelesVideoCameraInterface;
import org.lasque.tusdk.core.struct.TuSdkSize;
import org.lasque.tusdk.core.utils.hardware.TuSDKRecordVideoCamera.RecordError;
import org.lasque.tusdk.core.utils.hardware.TuSDKRecordVideoCamera.RecordMode;
import org.lasque.tusdk.core.utils.hardware.TuSDKRecordVideoCamera.RecordState;
import org.lasque.tusdk.core.utils.hardware.TuSDKRecordVideoCamera.TuSDKRecordVideoCameraDelegate;
import org.lasque.tusdk.core.utils.hardware.TuSDKVideoCamera.TuSDKVideoCameraDelegate;
import org.lasque.tusdk.core.utils.hardware.TuSdkStillCameraAdapter.CameraState;
import org.lasque.tusdk.core.video.TuSDKVideoResult;

import com.upyun.shortvideo.SimpleCameraActivity;
import com.upyun.shortvideo.views.FilterConfigView;
import com.upyun.shortvideo.views.FilterListView;
import com.upyun.shortvideo.views.StickerListView;
import com.upyun.shortvideo.views.record.MultipleCameraView;
import com.upyun.shortvideo.views.record.MultipleCameraView.TuSDKMultipleCameraDelegate;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.RelativeLayout;

/**
 * 多功能相机示例，点击拍照，长按录像
 * 
 * @author LiuHang
 */
@SuppressLint("ClickableViewAccessibility") 
public class MultipleCameraActivity extends SimpleCameraActivity implements TuSDKMultipleCameraDelegate
{
	/** 最大录制时间 */
	private final int MAX_RECORDING_TIME = 8;
	/** 最小录制时间 */
	private final int MIN_RECORDING_TIME = 0;
	
	/** 参数调节视图 */
	protected FilterConfigView mConfigView;
	/** 滤镜栏视图 */
	protected FilterListView mFilterListView;
	/** 贴纸栏视图 */
	protected StickerListView mStickerListView;
	/** 录制界面视图 */
	private MultipleCameraView mMultipleCameraView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		hideNavigationBar();
		setContentView(com.upyun.shortvideo.R.layout.multiple_camera_activity);
		
		initCamera();
		getMultipleCameraView();
		
		RelativeLayout cameraView = (RelativeLayout) findViewById(com.upyun.shortvideo.R.id.lsq_cameraView);
	    // 延迟启动相机，确认视图已经初始化完毕
	    cameraView.post(new Runnable()
	    {
	    	@Override
	    	public void run()
	    	{
	    		startCameraLater();
	    	}
	    });		   

	}
	
    @Override
    protected void onResume()                  
    {
        super.onResume();
        if (mMultipleCameraView != null)
        {
        	mMultipleCameraView.resumePlayer();
        }
       
        hideNavigationBar();
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        if (mMultipleCameraView != null)
        {
        	mMultipleCameraView.pausePlayer();
        }
    }
	
	@Override
	public void pauseCameraCapture()
	{
		super.pauseCameraCapture();
	}
	
	@Override
	public void resumeCameraCapture()
	{
		super.resumeCameraCapture();
	}
	
	/**
	 * 录制界面视图
	 */
	private MultipleCameraView getMultipleCameraView()
	{
		if (mMultipleCameraView == null)
		{
			mMultipleCameraView = (MultipleCameraView) findViewById(com.upyun.shortvideo.R.id.lsq_multiple_camera_view);
			mMultipleCameraView.setDelegate(this);
			mMultipleCameraView.setUpCamera(this,mVideoCamera);
		}
		
		return mMultipleCameraView;
	}
	
	protected void initCamera()
	{
		super.initCamera();
		
		mVideoCamera.setVideoDelegate(mRecordResultDelegate);
		mVideoCamera.setDelegate(mVideoCameraDelegate);
		
		mVideoCamera.setMinRecordingTime(MIN_RECORDING_TIME);
		mVideoCamera.setMaxRecordingTime(MAX_RECORDING_TIME);
		
		// 录制模式
		mVideoCamera.setRecordMode(RecordMode.Normal);
		
		// 限制录制尺寸不超过 1280
		mVideoCamera.setPreviewEffectScale(1.0f);
		mVideoCamera.setPreviewMaxSize(1280);
		
    	// 编码配置
    	TuSDKVideoEncoderSetting encoderSetting = TuSDKVideoEncoderSetting.getDefaultRecordSetting();
    	// 输出全屏尺寸
    	encoderSetting.videoSize = TuSdkSize.create(0, 0);
    	// 输出较高画质
    	encoderSetting.videoQuality = TuSDKVideoEncoderSetting.VideoQuality.RECORD_MEDIUM2;
    	
    	mVideoCamera.setVideoEncoderSetting(encoderSetting);
	}
	
	/**
	 * 滤镜效果改变监听事件
	 */
	protected TuSDKVideoCameraDelegate mVideoCameraDelegate = new TuSDKVideoCameraDelegate() 
    {
        @Override
        public void onFilterChanged(SelesOutInput selesOutInput)
        {
        	mMultipleCameraView.updateViewOnFilterChanged(selesOutInput);
        }

		@Override
		public void onVideoCameraStateChanged(SelesVideoCameraInterface camera, CameraState newState)
		{
		}

		@Override
		public void onVideoCameraScreenShot(SelesVideoCameraInterface camera, Bitmap bitmap) 
		{
			mMultipleCameraView.updateViewOnVideoCameraScreenShot(camera, bitmap);
		}
    };
  
	/** 
	 * 录制相机事件委托 
	 */
    private TuSDKRecordVideoCameraDelegate mRecordResultDelegate = new TuSDKRecordVideoCameraDelegate()
	{
		public void onMovieRecordComplete(TuSDKVideoResult result) 
		{
			mMultipleCameraView.updateViewOnMovieRecordComplete(result);
		}

		@Override
		public void onMovieRecordProgressChanged(float progress,
				float durationTime)
		{
		     mMultipleCameraView.updateViewOnMovieRecordProgressChanged(progress,durationTime);
		}
		
		@Override
		public void onMovieRecordFailed(RecordError error)
		{
           mMultipleCameraView.updateViewOnMovieRecordFailed(error);
		}

        @Override
        public void onMovieRecordStateChanged(RecordState state)
        {
            mMultipleCameraView.updateViewOnMovieRecordStateChanged(state);
        }
	};

	private void startCameraLater()
	{
		startCameraCapture();
	}

	@Override
	public void onMovieSaveSucceed(String videoPath)
	{
		
	}
}