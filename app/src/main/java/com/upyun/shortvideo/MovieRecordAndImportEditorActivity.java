/**
 * TuSDKVideoDemo 
 * MovieRecordActivity.java
 * 
 * @author Yanlin
 * @Date 7:19:13 PM
 * @Copright (c) 2015 tusdk.com. All rights reserved.
 * 
 */
package com.upyun.shortvideo;

import android.os.Bundle;
import android.view.View;

import com.upyun.shortvideo.component.MovieRecordKeepModeActivity;

import org.lasque.tusdk.core.video.TuSDKVideoResult;

/**
 * 断点续拍录制相机 + 视频编辑
 * 
 * @author Yanlin
 */
public class MovieRecordAndImportEditorActivity extends MovieRecordKeepModeActivity
{
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
        
        getRecordView().getMovieImportButton().setVisibility(View.VISIBLE);
	}

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
	}
}