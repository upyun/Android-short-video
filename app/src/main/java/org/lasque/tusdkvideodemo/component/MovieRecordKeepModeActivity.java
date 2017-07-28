/**
 * TuSDKVideoDemo
 * MovieRecordKeepModeActivity.java
 *
 * @author     LiuHang
 * @Date:      May 22, 2017 7:28:12 PM
 * @Copyright: (c) 2017 tusdk.com. All rights reserved.
 *
 */
package org.lasque.tusdkvideodemo.component;

import org.lasque.tusdk.core.TuSdkContext;
import org.lasque.tusdk.core.encoder.video.TuSDKVideoEncoderSetting;
import org.lasque.tusdk.core.struct.TuSdkSize;
import org.lasque.tusdk.core.utils.ContextUtils;
import org.lasque.tusdk.core.utils.RectHelper;
import org.lasque.tusdk.core.utils.TLog;
import org.lasque.tusdk.core.utils.hardware.TuSDKRecordVideoCamera.RecordError;
import org.lasque.tusdk.core.utils.hardware.TuSDKRecordVideoCamera.RecordMode;
import org.lasque.tusdk.core.utils.hardware.TuSDKRecordVideoCamera.RecordState;
import org.lasque.tusdk.core.utils.hardware.TuSDKRecordVideoCamera.TuSDKRecordVideoCameraDelegate;
import org.lasque.tusdk.core.video.TuSDKVideoResult;
import org.lasque.tusdk.impl.view.widget.RegionDefaultHandler;
import org.lasque.tusdkvideodemo.R;
import org.lasque.tusdkvideodemo.SimpleCameraActivity;
import org.lasque.tusdkvideodemo.utils.Constants;
import org.lasque.tusdkvideodemo.views.record.MovieRecordView;
import org.lasque.tusdkvideodemo.views.record.MovieRecordView.TuSDKMovieRecordDelegate;

import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;

/**
 * 断点续拍模式录制相机
 * 
 * @author LiuHang
 */
public class MovieRecordKeepModeActivity extends SimpleCameraActivity implements TuSDKMovieRecordDelegate, TuSDKRecordVideoCameraDelegate
{
	// 录制界面视图
	private MovieRecordView mRecordView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.movie_record_activity);

        initCamera();
        
        getRecordView();
        
        RelativeLayout cameraView = (RelativeLayout) findViewById(R.id.lsq_cameraView);
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
		if (mRecordView != null)
			mRecordView.setActived(true);
    }

    @Override
    protected void onPause()
    {
        super.onPause();
		if (mRecordView != null)
			mRecordView.setActived(false);
		
		getRecordView().initProgressList();
    }
    
	/**
	 * 录制界面视图
	 */
	protected MovieRecordView getRecordView()
	{
		if (mRecordView == null)
		{
			mRecordView = (MovieRecordView) findViewById(R.id.lsq_movie_record_view);
			mRecordView.setActived(true);
			mRecordView.setDelegate(this);
			mRecordView.setUpCamera(this, mVideoCamera);
			mRecordView.getMovieImportButton().setVisibility(View.GONE);
		}
		return mRecordView;
	}

	protected void initCamera()
	{
		super.initCamera();
		
		mVideoCamera.setVideoDelegate(this);		
		mVideoCamera.setMinRecordingTime(Constants.MIN_RECORDING_TIME);
		mVideoCamera.setMaxRecordingTime(Constants.MAX_RECORDING_TIME);
		
		// 设置使用录制相机最小空间限制,开发者可根据需要自行设置（默认：50M）
		//mVideoCamera.setMinAvailableSpaceBytes(1024*1024*50l);
		
		// 录制模式
		mVideoCamera.setRecordMode(RecordMode.Keep);
		// 指定为 4/3 画面比例，近距离使用前置摄像头时人脸看起来较小
		mVideoCamera.setPreviewRatio(4.f/3);
		TuSdkSize screenSize = TuSdkContext.getDisplaySize();
		
    	// 编码配置
    	TuSDKVideoEncoderSetting encoderSetting = TuSDKVideoEncoderSetting.getDefaultRecordSetting();
    	// 1: 1 输出，必须和 regionRatio 保持一致比例
    	// 这里可以根据实际使用场景，设为固定的值，比如 480 * 480
    	encoderSetting.videoSize = TuSdkSize.create(screenSize.width, screenSize.width);
    	
    	mVideoCamera.setVideoEncoderSetting(encoderSetting);
	}
	
	protected void startCameraLater()
	{
		/**
		 * 两种方式自定义输出区域
		 * 
		 * 1. setRegionRatio 方法, 显示区域居中
		 * 
		 * 2. 通过 RegionHandler，可以精确控制显示区域
		 * 
		 */
		mVideoCamera.setRegionHandler(customeRegionHandler);
		
		// 刷新视图，使 regionHandler 生效, 1:1 视图
		mVideoCamera.changeRegionRatio(1.0f);
		
		startCameraCapture();
	}
	
	private RegionDefaultHandler customeRegionHandler = new RegionDefaultHandler()
	{
		@Override
		public void setWrapSize(TuSdkSize size)
		{
			super.setWrapSize(size);
		}
		
		@Override
		protected RectF recalculate(float ratio, TuSdkSize size)
		{
			if (ratio == 0 || size == null || !size.isSize())
			{
				return new RectF(0, 0, 1, 1);
			}

			TuSdkSize tSize = TuSdkSize.create(size);
			tSize.width = (int) (size.height * ratio);

			Rect insetRect = RectHelper.makeRectWithAspectRatioInsideRect(tSize, new Rect(0, 0, size.width, size.height));
			
			int topBarHeight = ContextUtils.dip2px(getBaseContext(), 60);
			
			float rl = insetRect.left / (float) size.width;
			float rt = topBarHeight/ (float) size.height;
			float rr = insetRect.right / (float) size.width;
			float rb = (topBarHeight + size.width)/ (float) size.height;
			
			RectF rect = new RectF(rl, rt, rr, rb);
			return rect;
		}
		
		@Override
		public RectF changeWithRatio(float ratio, RegionChangerListener listener)
		{
			if (ratio == this.getRatio()) return this.getRectPercent();
			this.setRatio(ratio);
			
			listener.onRegionChanged(this.getRectPercent());

			return this.getRectPercent();
		}
	};

	@Override
	public void onMovieRecordComplete(TuSDKVideoResult result) 
	{
		mRecordView.updateViewOnMovieRecordComplete(isRecording());
	}
	
	@Override
	public void onMovieRecordProgressChanged(float progress,
			float durationTime)
	{
		mRecordView.updateViewOnMovieRecordProgressChanged(progress, durationTime);
	}
	
	@Override
	public void onMovieRecordStateChanged(RecordState state)
	{
		mRecordView.updateViewOnMovieRecordStateChanged(state, isRecording());
		
		if(state == RecordState.Paused)
		{
			mRecordView.updateViewOnPauseRecording(mVideoCamera.getMovieDuration() >= mVideoCamera.getMinRecordingTime());
		}
	}
	
	@Override
	public void onMovieRecordFailed(RecordError error)
	{
		TLog.e("RecordError : %s",error);
		mRecordView.updateViewOnMovieRecordFailed(error, isRecording());
	}
 
	@Override
    public void stopRecording()
    {
		if (mVideoCamera.isRecording())
		{
			mVideoCamera.stopRecording();
		}
		
		mRecordView.updateViewOnStopRecording(mVideoCamera.isRecording());
    }
    
    @Override
    public void pauseRecording()
    {
    	if (mVideoCamera.isRecording())
		{
			mVideoCamera.pauseRecording();			
		}
    }
    
    @Override
    public void startRecording()
    {
    	if (!mVideoCamera.isRecording())
    	{
    		mVideoCamera.startRecording();
    	}
    
    	mRecordView.updateViewOnStartRecording(mVideoCamera.isRecording());
    }

    @Override
    public boolean isRecording()
    {
    	return mVideoCamera.isRecording();
    }

	@Override
	public void finishRecordActivity()
	{
		this.finish();
	}
}