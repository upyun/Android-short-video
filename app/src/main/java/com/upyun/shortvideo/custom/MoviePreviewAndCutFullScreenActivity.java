/**
 * TuSDKVideoDemo
 * MoviePreviewAndCutFullScreenActivity.java
 *
 * @author  Yanlin
 * @Date  Feb 21, 2017 8:52:11 PM
 * @Copright (c) 2016 tusdk.com. All rights reserved.
 *
 */
package com.upyun.shortvideo.custom;
import org.lasque.tusdk.core.TuSdkContext;
import org.lasque.tusdk.core.struct.TuSdkSize;
import org.lasque.tusdk.core.utils.RectHelper;

import android.graphics.Rect;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.SurfaceView;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

/**
 * 视频预览 + 获取视频裁剪范围
 * 
 * @author leone.xia
 */
public class MoviePreviewAndCutFullScreenActivity extends MoviePreviewAndCutRatioActivity  
{
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		hideNavigationBar();
		setIntentClass(MovieEditorFullScreenActivity.class);
	}
	
	/**
	 * 初始化视图
	 */
	@Override
	protected void initView()
	{
		super.initView();
        RelativeLayout titleLayout = (RelativeLayout) findViewById(com.upyun.shortvideo.R.id.lsq_title_item);
        titleLayout.setBackgroundColor(TuSdkContext.getColor(com.upyun.shortvideo.R.color.lsq_color_transparent));
        
        FrameLayout moviePreviewLayout = (FrameLayout) findViewById(com.upyun.shortvideo.R.id.movie_layout);
        LayoutParams lp = (LayoutParams) moviePreviewLayout.getLayoutParams();
        lp.width = LayoutParams.MATCH_PARENT;
        lp.height = LayoutParams.MATCH_PARENT;
        
        LinearLayout timeLayout = (LinearLayout) findViewById(com.upyun.shortvideo.R.id.time_layout);
        timeLayout.setBackgroundColor(TuSdkContext.getColor("lsq_color_transparent"));
	}

    @Override
    protected void onResume()
    {
        super.onResume();
        hideNavigationBar();
    }
    
   @Override
	public void setVideoSize(SurfaceView surfaceView, int width, int height)
   	{
       if(surfaceView!=null)
       {
           DisplayMetrics dm = new DisplayMetrics();
           getWindowManager().getDefaultDisplay().getRealMetrics(dm);
           int screenWidth= (int) dm.widthPixels;
           int screenHeight= (int) dm.heightPixels;
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
           lp.addRule(RelativeLayout.CENTER_IN_PARENT);
           surfaceView.setLayoutParams(lp);
       }
   	}
}