/**
 * TuSDKVideoDemo
 * MultipleCameraActivity.java
 *
 * @author     LiuHang
 * @Date:      May 15, 2017 7:28:12 PM
 * @Copyright: (c) 2017 tusdk.com. All rights reserved.
 *
 */
package com.upyun.shortvideo.custom;

import com.upyun.shortvideo.component.MultipleCameraActivity;

/**
 * 多功能相机示 + 视频裁剪 + 视频编辑 （自定义比例）
 * 
 * @author LiuHang
 */
public class MultipleCameraRatioActivity extends MultipleCameraActivity
{
	
	@Override
	protected void initCamera() 
	{
		super.initCamera();
		
		mVideoCamera.setWaterMarkImage(null);
	}

	@Override
	public void onMovieSaveSucceed(String videoPath)
	{
		startActivityWithClassName(MoviePreviewAndCutRatioActivity.class.getName(), videoPath);
	}
}