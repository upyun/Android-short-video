/**
 * TuSDKVideoDemo
 * MovieRecordKeepModeActivity.java
 *
 * @author     LiuHang
 * @Date:      May 22, 2017 7:28:12 PM
 * @Copyright: (c) 2017 tusdk.com. All rights reserved.
 *
 */
package com.upyun.shortvideo.component;


import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;

import com.upyun.shortvideo.SimpleCameraActivity;
import com.upyun.shortvideo.utils.Constants;
import com.upyun.shortvideo.views.CompoundDrawableTextView;

import org.lasque.tusdk.core.TuSdkContext;
import org.lasque.tusdk.core.encoder.video.TuSDKVideoEncoderSetting;
import org.lasque.tusdk.core.struct.TuSdkSize;
import org.lasque.tusdk.core.utils.TLog;
import org.lasque.tusdk.core.utils.hardware.TuSDKRecordVideoCamera.RecordError;
import org.lasque.tusdk.core.utils.hardware.TuSDKRecordVideoCamera.RecordMode;
import org.lasque.tusdk.core.utils.hardware.TuSDKRecordVideoCamera.RecordState;
import org.lasque.tusdk.core.utils.hardware.TuSDKRecordVideoCamera.TuSDKRecordVideoCameraDelegate;
import org.lasque.tusdk.core.video.TuSDKVideoResult;

import com.upyun.shortvideo.views.record.MovieRecordView;
import com.upyun.shortvideo.views.record.MovieRecordView.TuSDKMovieRecordDelegate;

/**
 * 断点续拍模式录制相机
 *
 * @author LiuHang
 */
public class MovieRecordKeepModeActivity extends SimpleCameraActivity implements TuSDKMovieRecordDelegate, TuSDKRecordVideoCameraDelegate
{
	// 录制界面视图
	protected MovieRecordView mRecordView;

	protected int getLayoutId()
	{
		return com.upyun.shortvideo.R.layout.movie_record_activity;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		
		setContentView(getLayoutId());

        initCamera();

        getRecordView();

		RelativeLayout cameraView = (RelativeLayout)findViewById(com.upyun.shortvideo.R.id.lsq_cameraView);
		// 确认视图初始化完毕
		cameraView.post(new Runnable()
		{
			@Override
			public void run()
			{
				changeCameraRegionRatio();
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
			mRecordView = (MovieRecordView) findViewById(com.upyun.shortvideo.R.id.lsq_movie_record_view);
			mRecordView.setActived(true);
			mRecordView.setDelegate(this);
			mRecordView.setUpCamera(this, mVideoCamera);
			CompoundDrawableTextView importButton = mRecordView.getMovieImportButton();
			if (importButton != null)
				importButton.setVisibility(View.GONE);
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

    	// 这里可以修改帧率和码率; RECORD_MEDIUM2第一个参数代表帧率，第二参数代表码率;选择VideoQuality参数尽量选用RECORD开头(专门为视频录制设计)
   		// encoderSetting.videoQuality = TuSDKVideoEncoderSetting.VideoQuality.RECORD_MEDIUM2;
		// 完全自定义帧率和码率
		// encoderSetting.videoQuality = TuSDKVideoEncoderSetting.VideoQuality.RECORD_MEDIUM2.setFps(30).setBitrate(3000 * 1000);

    	mVideoCamera.setVideoEncoderSetting(encoderSetting);

	}
	
	protected void changeCameraRegionRatio()
	{
		// 获取预览区域size
		TuSdkSize wrapSize = mVideoCamera.getRegionHandler().getWrapSize();

		// 顶部工具栏 + 进度栏高度
		int topBarHeight = getRecordView().calculateHeaderLayoutHeight();

		// 设置预览区域顶部偏移量 必须在 changeRegionRatio 之前设置
		mVideoCamera.getRegionHandler().setOffsetTopPercent(topBarHeight/(float)wrapSize.height);

		/**
		 * 两种方式自定义输出区域
		 * 
		 * 1. setRegionRatio 方法, 显示区域居中
		 * 
		 * 2. 通过 RegionHandler，可以精确控制显示区域
		 * 
		 */
		//mVideoCamera.setRegionHandler(customeRegionHandler);
		
		// 刷新视图，使 regionHandler 生效, 1:1 视图
		mVideoCamera.changeRegionRatio(1.0f);
	}

	/* 通过 RegionHandler，可以精确控制显示区域

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

			int topBarHeight = ContextUtils.dip2px(getBaseContext(), 90);

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
	*/

	/** ----------- 注意事项：如果视频录制完成后需要跳转到视频编辑页面,需要将录制视频页面销毁掉; 视频编辑跳转视频录制也是如此 ---------------------------*/
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