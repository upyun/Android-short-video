/**
 * TuSDKVideoDemo
 * ClickAndLongPressedListener.java
 *
 * @author  XiaShengCui
 * @Date  Jun 1, 2017 7:36:45 PM
 * @Copyright: (c) 2017 tusdk.com. All rights reserved.
 *
 */
package com.upyun.shortvideo.utils;

import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

import java.util.Calendar;

/**
 * 点击长按监听
 * 
 * @author XiaShengCui
 *
 */
public class ClickAndLongPressedListener implements OnTouchListener {

	/** 最小响应长按的时间 */
	private static final long MIN_LONG_PRESS_TIME = 300;

	/** 当前点击时间 */
	private long mCurrentClickTime;

	private Handler mBaseHandler = new Handler();
	/** 长按释放事件 */
	private LongPressedUpRunnable mLogPressedUpRunnable;
	/** 长按按下事件 */
	private LongPressedDownRunnable mLongPressedDownRunnable;
	/** 点击事件 */
	private ClickRunnable mClickRunnable;
	
	/** 点击长按 回调接口 */
	private ClickAndLongPressedInterface mLongPressedAndClickInterface;

	@Override
	public boolean onTouch(View v, MotionEvent event) {

		switch (event.getAction()) {

		case MotionEvent.ACTION_DOWN:
			mCurrentClickTime = Calendar.getInstance().getTimeInMillis();
			if (mClickRunnable != null) 
				mBaseHandler.removeCallbacks(mClickRunnable);
			
			if (mLongPressedDownRunnable != null)
				mBaseHandler.removeCallbacks(mLongPressedDownRunnable);
			
			mLongPressedDownRunnable = new LongPressedDownRunnable();
			mBaseHandler.postDelayed(mLongPressedDownRunnable, MIN_LONG_PRESS_TIME);
			break;

		case MotionEvent.ACTION_MOVE:
			break;

		case MotionEvent.ACTION_UP:
			
			if (mLongPressedDownRunnable != null)
				mBaseHandler.removeCallbacks(mLongPressedDownRunnable);
			
			if (mClickRunnable != null)
				mBaseHandler.removeCallbacks(mClickRunnable);
			
            long offset = Calendar.getInstance().getTimeInMillis()- mCurrentClickTime ;
			if (offset < MIN_LONG_PRESS_TIME)
			{
				mClickRunnable = new ClickRunnable();
				mBaseHandler.post(mClickRunnable);
			}
			else 
			{
				mLogPressedUpRunnable = new LongPressedUpRunnable();
				mBaseHandler.post(mLogPressedUpRunnable);
			}
			break;
		default:
			break;
		}
		return true;
	}

	private class LongPressedDownRunnable implements Runnable 
	{

		@Override
		public void run()
		{
			 if(mLongPressedAndClickInterface != null)
				 mLongPressedAndClickInterface.onLongPressedDown();
		}

	}

	private class LongPressedUpRunnable implements Runnable
	{

		@Override
		public void run()
		{
			 if(mLongPressedAndClickInterface != null)
				 mLongPressedAndClickInterface.onLongPressedUp();
		}

	}
	
	private class ClickRunnable implements Runnable
	{

		@Override
		public void run()
		{
              if(mLongPressedAndClickInterface != null)
            	  mLongPressedAndClickInterface.onClick();
		}

	}

	public ClickAndLongPressedInterface getLongPressedAndClickInterface()
	{
		return mLongPressedAndClickInterface;
	}

	public void setLongPressedAndClickInterface(ClickAndLongPressedInterface callBack)
	{
		this.mLongPressedAndClickInterface = callBack;
	}

}
