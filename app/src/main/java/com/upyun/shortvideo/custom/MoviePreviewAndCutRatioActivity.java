/**
 * TuSDKVideoDemo
 * MoviePreviewAndCutRatioActivity.java
 *
 * @author  Yanlin
 * @Date  Feb 21, 2017 8:52:11 PM
 * @Copright (c) 2016 tusdk.com. All rights reserved.
 *
 */
package com.upyun.shortvideo.custom;

import android.graphics.Rect;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.SurfaceView;
import android.widget.RelativeLayout;

import com.upyun.shortvideo.suite.MoviePreviewAndCutActivity;

import org.lasque.tusdk.core.struct.TuSdkSize;
import org.lasque.tusdk.core.utils.RectHelper;

/**
 * 视频预览 + 获取视频裁剪范围
 * 
 * @author leone.xia
 * @param <T>
 */
public class MoviePreviewAndCutRatioActivity extends MoviePreviewAndCutActivity
{
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		// 设置视频比例自适应
		RATIO_ADAPTION = true;
	}
	
	@Override
	public void onVideSizeChanged(MediaPlayer mp, int width, int height) 
	{
		setVideoSize(mSurfaceView, width, height);
	}
	
	public void setVideoSize(SurfaceView surfaceView, int width, int height)
	{
       if(surfaceView!=null)
       {
           DisplayMetrics dm = new DisplayMetrics();
           getWindowManager().getDefaultDisplay().getMetrics(dm);
           int screenWidth= (int) dm.widthPixels;
           int screenHeight= (int) (360*dm.density);
           
           Rect boundingRect = new Rect();
           boundingRect.left = 0;
           boundingRect.right = screenWidth;
           boundingRect.top = 0;
           boundingRect.bottom = screenHeight;
           Rect rect = RectHelper.makeRectWithAspectRatioInsideRect(new TuSdkSize(width, height), boundingRect);

           int w = rect.right- rect.left;
           int h = rect.bottom - rect.top;
           RelativeLayout.LayoutParams lp = new RelativeLayout
                   .LayoutParams(w,h);
           lp.setMargins(rect.left, rect.top, 0, 0);
           surfaceView.setLayoutParams(lp);
       }
   }
}