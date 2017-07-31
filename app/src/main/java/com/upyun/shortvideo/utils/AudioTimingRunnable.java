/**
 * TuSDKVideoDemo
 * AudioTimingRunnable.java
 *
 * @author  LiuHang
 * @Date  Jun 30, 2017 3:04:26 PM
 * @Copright (c) 2017 tusdk.com. All rights reserved.
 *
 */
package com.upyun.shortvideo.utils;


/**
 * 计时线程
 * 
 * @author LiuHang
 *
 */
public class AudioTimingRunnable implements Runnable
{	
	/** 标记录音是否结束 */
	public boolean isAudioRecordStopped = false;
	
	/** 录音开始时间 */
	public long mAudioRecordStartTime;
	
	/** 录音时长 */
	public float mAudioRecordDuration;
	
	public interface AudioRecordProgressDelegate
	{
		/**
		 * 录音进度
		 * @param duration
		 */
		void onAudioRecordPogressChanged(float duration);
	}
	
	/** 录音进度委托事件 */
	public AudioRecordProgressDelegate mAudioRecordDelegate;
	
	public void setDelegate(AudioRecordProgressDelegate audioRecordDelegate)
	{
		this.mAudioRecordDelegate = audioRecordDelegate;
	}
	
	public AudioRecordProgressDelegate getDelegate()
	{
		return mAudioRecordDelegate;
	}
	
	public void setAudioRecordStartTime(long audioRecordStartTime)
	{
		this.mAudioRecordStartTime = audioRecordStartTime;
	}
	
	/**
	 * 录音计时线程
	 */
	@Override
	public void run() 
	{
		while(!isAudioRecordStopped)
		{
			mAudioRecordDuration = (System.currentTimeMillis()-mAudioRecordStartTime)/(float)1000;
			getDelegate().onAudioRecordPogressChanged(mAudioRecordDuration);
			
			try 
			{
				Thread.sleep(50);
			} 
			catch 
			(InterruptedException e)
			{
				e.printStackTrace();
			}
		}
	}
	
	public void start()
	{
		isAudioRecordStopped = false;
	}
	
	public void stop()
	{
		isAudioRecordStopped = true;
	}

}
