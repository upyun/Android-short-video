/**
 * TuSDKVideoDemo 
 * MovieRecordAndPreviewEditorActivity.java
 * 
 * @author LiuHang
 * @Date   June 22, 2017 7:28:12 PM
 * @Copright (c) 2017 tusdk.com. All rights reserved.
 * 
 */
package com.upyun.shortvideo.suite;

import com.upyun.shortvideo.component.MovieRecordKeepModeActivity;

import org.lasque.tusdk.core.video.TuSDKVideoResult;

/**
 * 断点续拍相机 + 视频编辑
 * 
 * @author LiuHang
 */
public class MovieRecordAndPreviewEditorActivity extends MovieRecordKeepModeActivity
{
	@Override
	protected void initCamera() 
	{
		super.initCamera();
		
		mVideoCamera.setWaterMarkImage(null);
	}

	@Override
	public void onMovieRecordComplete(TuSDKVideoResult result) 
	{
		super.onMovieRecordComplete(result);
		
		startActivityWithClassName(MoviePreviewAndCutActivity.class.getName(), result.videoPath.toString());
	}
}