/**
 * TuSDKVideoDemo
 * MovieRecordNormalModeActivity.java
 *
 * @author     LiuHang
 * @Date:      May 22, 2017 7:28:12 PM
 * @Copyright: (c) 2017 tusdk.com. All rights reserved.
 *
 */
package com.upyun.shortvideo.component;

import org.lasque.tusdk.core.utils.hardware.TuSDKRecordVideoCamera.RecordMode;

/**
 * 正常模式录制相机
 * 
 * @author LiuHang
 */
public class MovieRecordNormalModeActivity extends MovieRecordKeepModeActivity
{
	protected void initCamera()
	{
		super.initCamera();
		
		// 录制模式
		mVideoCamera.setRecordMode(RecordMode.Normal);
	}
}