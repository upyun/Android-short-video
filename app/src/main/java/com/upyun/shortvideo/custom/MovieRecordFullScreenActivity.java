/**
 * TuSDKVideoDemo
 * MovieRecordFullScreenActivity.java
 *
 * @author     LiuHang
 * @Date:      May 22, 2017 7:28:12 PM
 * @Copyright: (c) 2017 tusdk.com. All rights reserved.
 *
 */

package com.upyun.shortvideo.custom;

import android.os.Bundle;
import android.widget.ProgressBar;

import com.upyun.shortvideo.component.MovieRecordKeepModeActivity;
import com.upyun.shortvideo.views.record.MovieRecordView;

import org.lasque.tusdk.core.TuSdk;
import org.lasque.tusdk.core.TuSdkContext;
import org.lasque.tusdk.core.encoder.video.TuSDKVideoEncoderSetting;
import org.lasque.tusdk.core.struct.TuSdkSize;
import org.lasque.tusdk.core.video.TuSDKVideoResult;
import com.upyun.shortvideo.R;
/**
 * 断点续拍全屏 + 视频编辑全屏
 * 
 * @author LiuHang
 */
public class MovieRecordFullScreenActivity extends MovieRecordKeepModeActivity
{
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		// 设置录制界面背景为透明色
		setRecordViewBackgroundColor(getRecordView());
		getRecordView().setSquareSticker(false);
		getRecordView().isShowRatioButton(true);
		getRecordView().setRatioType(getRecordView().getRatioType());
		
		hideNavigationBar();
        TuSdk.messageHub().applyToViewWithNavigationBarHidden(true);
	}

	@Override
	protected int getLayoutId()
	{
		return R.layout.movie_record_full_screen_activity;
	}

	@Override
	protected void onResume() 
	{
		super.onResume();
		hideNavigationBar();
	}

	private void setRecordViewBackgroundColor(MovieRecordView recordView)
	{
		/** 全透明 colorId */
		int transparentColorId = R.color.lsq_color_transparent;
		int semiTransparentColorId = R.color.lsq_color_semitransparent;
		
		ProgressBar bar = (ProgressBar) recordView.findViewById(R.id.lsq_record_progressbar);
		if (bar != null)
			bar.setProgressDrawable(TuSdkContext.getDrawable(R.drawable.tusdk_view_widget_full_screen_progress_video_timer));
		
		recordView.getFilterBottomView().setBackgroundColor(TuSdkContext.getColor(semiTransparentColorId));
		recordView.getStickerBottomView().setBackgroundColor(TuSdkContext.getColor(semiTransparentColorId));
		recordView.getBottomBarLayout().setBackgroundColor(TuSdkContext.getColor(transparentColorId));
		recordView.getTopBarLayout().setBackgroundColor(TuSdkContext.getColor(transparentColorId));
	}
	
	@Override
	protected void initCamera()
	{
		super.initCamera();
		
		mVideoCamera.setWaterMarkImage(null);

		// 限制录制尺寸不超过 1280
		mVideoCamera.setPreviewEffectScale(1.0f);
		mVideoCamera.setPreviewMaxSize(1280);
		mVideoCamera.setPreviewRatio(0);
		
    	// 编码配置
    	TuSDKVideoEncoderSetting encoderSetting = TuSDKVideoEncoderSetting.getDefaultRecordSetting();
    	// 输出全屏尺寸
    	encoderSetting.videoSize = TuSdkSize.create(0, 0);
    	// 输出较高画质
    	encoderSetting.videoQuality = TuSDKVideoEncoderSetting.VideoQuality.RECORD_MEDIUM2;
    	
    	mVideoCamera.setVideoEncoderSetting(encoderSetting);
	}

	@Override
	protected void changeCameraRegionRatio()
	{
	}

	@Override
	public void onMovieRecordComplete(TuSDKVideoResult result)
	{
		super.onMovieRecordComplete(result);
	}
}