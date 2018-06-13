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

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.os.Bundle;

import com.upyun.shortvideo.SimpleCameraActivity;

import org.lasque.tusdk.core.encoder.video.TuSDKVideoEncoderSetting;
import org.lasque.tusdk.core.seles.sources.SelesVideoCameraInterface;
import org.lasque.tusdk.core.seles.tusdk.FilterWrap;
import org.lasque.tusdk.core.struct.TuSdkSize;
import org.lasque.tusdk.core.utils.hardware.TuSDKRecordVideoCamera.RecordError;
import org.lasque.tusdk.core.utils.hardware.TuSDKRecordVideoCamera.RecordMode;
import org.lasque.tusdk.core.utils.hardware.TuSDKRecordVideoCamera.RecordState;
import org.lasque.tusdk.core.utils.hardware.TuSDKRecordVideoCamera.TuSDKRecordVideoCameraDelegate;
import org.lasque.tusdk.core.utils.hardware.TuSDKVideoCamera.TuSDKVideoCameraDelegate;
import org.lasque.tusdk.core.utils.hardware.TuSdkStillCameraAdapter.CameraState;
import org.lasque.tusdk.core.video.TuSDKVideoResult;

import com.upyun.shortvideo.views.record.MultipleCameraView;
import com.upyun.shortvideo.views.record.MultipleCameraView.TuSDKMultipleCameraDelegate;
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
        public void onFilterChanged(FilterWrap selesOutInput)
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

	@Override
	public void onMovieSaveSucceed(String videoPath)
	{
		
	}
}