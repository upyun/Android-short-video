/**
 * TuSDKVideoDemo
 * MovieEditorFullScreenActivity.java
 *
 * @author     LiuHang
 * @Date:      May 22, 2017 7:28:12 PM
 * @Copyright: (c) 2017 tusdk.com. All rights reserved.
 *
 */

package com.upyun.shortvideo.custom;

import java.io.IOException;

import org.lasque.tusdk.core.TuSdkContext;
import org.lasque.tusdk.core.struct.TuSdkSize;

import com.upyun.shortvideo.component.MovieEditorActivity;

import android.graphics.Rect;
import android.media.MediaExtractor;
import android.media.MediaFormat;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.widget.RelativeLayout;

/**
 * 视频编辑全屏示例
 * 
 * 功能：
 * 1. 预览视频，添加滤镜查看效果
 * 2. 导出新的视频
 * 
 * @author LiuHang
 */
public class MovieEditorFullScreenActivity extends MovieEditorActivity 
{
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		
		// 背景设置为透明效果
		RelativeLayout topBarLayout = (RelativeLayout) findViewById(com.upyun.shortvideo.R.id.lsq_topBar);
		topBarLayout.setBackgroundColor(TuSdkContext.getColor("lsq_color_transparent"));
		getBottomNavigationLayout().setBackgroundColor(TuSdkContext.getColor("lsq_color_transparent"));
		getMixingListViewWrap().setBackgroundColor(TuSdkContext.getColor("lsq_color_transparent"));
		getFilterMVLayoutWrap().setBackgroundColor(TuSdkContext.getColor("lsq_color_transparent"));
		TuSdkSize tuSdkSize = getVideoSize(mVideoPath);
		setPreviewSize(getCameraView(),tuSdkSize.width,tuSdkSize.height);
		isSupportFulllScreen(true);
	}	
	
	/**
	 * 根据视频路径获取视频宽高
	 * 
	 * @param videoPath
	 * @return
	 */
	private TuSdkSize getVideoSize(String videoPath)
	{
		MediaExtractor mediaExtractor = new MediaExtractor();
		try 
		{
			mediaExtractor.setDataSource(videoPath);
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}
		
		MediaFormat mediaFormat = getVideoTrackFormat(mediaExtractor);
        int videoWidth = mediaFormat.getInteger(MediaFormat.KEY_WIDTH);
        int videoHeight = mediaFormat.getInteger(MediaFormat.KEY_HEIGHT);
        TuSdkSize tuSdkSize = new TuSdkSize(videoWidth,videoHeight);
        
        mediaExtractor.release();
        mediaExtractor = null;
        mediaFormat = null;
        
		return tuSdkSize;
	}
	
    /**
     * 获取视频轨道MediaFormat
     * @return  MediaFormat
     */
	private MediaFormat getVideoTrackFormat(MediaExtractor mediaExtractor)
	{
		if(mediaExtractor == null) return null;
		
		int videoTrackIndex = findVideoTrack(mediaExtractor);
		
		if(videoTrackIndex < 0) return null;	
		
		return mediaExtractor.getTrackFormat(videoTrackIndex);
	}
	
	private int findVideoTrack(MediaExtractor mediaExtractor)
	{
		int numTracks = mediaExtractor.getTrackCount();
	      
		   for (int i = 0; i < numTracks; i++)
		   {
	            MediaFormat format = mediaExtractor.getTrackFormat(i);
	            String mime = format.getString(MediaFormat.KEY_MIME);
	            if (mime.startsWith("video/")) 
	            {
	                return i;
	            }
	       }
		   return -1;
	}
	
	/**
	 * 根据视频大小计算预览区域
	 * 
	 * @param cameraView
	 */
	private void setPreviewSize(RelativeLayout cameraView,int width,int height)
	{
		if(cameraView == null) return;
		
		 DisplayMetrics dm = new DisplayMetrics();
         getWindowManager().getDefaultDisplay().getRealMetrics(dm);
         int screenWidth= (int) dm.widthPixels;
         int screenHeight= (int) dm.heightPixels;
         
         Rect boundingRect = new Rect();
         boundingRect.left = 0;
         boundingRect.right = screenWidth;
         boundingRect.top = 0;
         boundingRect.bottom = screenHeight;
         
         Rect rect = makeRectWithAspectRatioInsideRect(new TuSdkSize(width, height), boundingRect);
         int w = rect.right- rect.left;
         int h = rect.bottom - rect.top;
         
         RelativeLayout.LayoutParams lp = new RelativeLayout
                 .LayoutParams(w,h);
         cameraView.setLayoutParams(lp);
	}
	
	/**
	 * 计算在Rect内按比例缩放Size后新的Rect
	 * 
	 * @param aspectRatio
	 * @param boundingRect
	 * @return
	 */
	public  Rect makeRectWithAspectRatioInsideRect(TuSdkSize aspectRatio, Rect boundingRect)
	{
		if (aspectRatio == null || boundingRect == null) return null;

		TuSdkSize cacheSize = new TuSdkSize();
		float ratio = boundingRect.height() /(float) boundingRect.width();
		cacheSize.height = boundingRect.height();
		cacheSize.width = (int) Math.floor(cacheSize.height / ratio);
		if (cacheSize.width > boundingRect.width())
		{
			cacheSize.width = boundingRect.height();
			cacheSize.height = (int) Math.floor(cacheSize.width * ratio);			
		}

		Rect rect = new Rect(boundingRect);
		rect.left = boundingRect.left + (boundingRect.width() - cacheSize.width) / 2;
		rect.right = rect.left + cacheSize.width;
		rect.top = boundingRect.top + (boundingRect.height() - cacheSize.height) / 2;
		rect.bottom = rect.top + cacheSize.height;
		return rect;
	}
}