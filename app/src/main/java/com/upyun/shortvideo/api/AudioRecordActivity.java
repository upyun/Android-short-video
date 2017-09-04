/**
 * TuSDKVideoDemo
 * AudioRecordActivity.java
 *
 * @author  LiuHang
 * @Date  Jul 13, 2017 14:52:11 PM
 * @Copright (c) 2017 tusdk.com. All rights reserved.
 *
 */

package com.upyun.shortvideo.api;

import java.io.File;

import org.lasque.tusdk.core.TuSdk;
import org.lasque.tusdk.core.TuSdkContext;
import org.lasque.tusdk.core.audio.TuSDKAudioFileRecorder;
import org.lasque.tusdk.core.audio.TuSDKAudioFileRecorder.OutputFormat;
import org.lasque.tusdk.core.audio.TuSDKAudioFileRecorder.RecordError;
import org.lasque.tusdk.core.audio.TuSDKAudioFileRecorder.RecordState;
import org.lasque.tusdk.core.audio.TuSDKAudioFileRecorder.TuSDKRecordAudioDelegate;

import android.app.Activity;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

/**
 * 录制音频
 * 
 * @author LiuHang
 *
 */
public class AudioRecordActivity extends Activity
{
	/** 返回按钮 */
	private TextView mBackBtn;
	
	/** 开始录音按钮 */
	private Button mStartRecordButton;
	
	/** 播放录音按钮*/
	private Button mPlayAudioButton;
	
	/** 结束录音按钮  */
	private Button mStopRecordButton;
	
	/** 生成的录音文件 */
	private File mAudioFile;
	
	/** 音频文件录制实例 */
	private TuSDKAudioFileRecorder mAudioRecorder;
	
	/** 音视频播放器 */
	private MediaPlayer mMediaPlayer;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(com.upyun.shortvideo.R.layout.audio_record_activity);
		initView();
	}
	
	private void initView()
	{
		mBackBtn = (TextView) findViewById(com.upyun.shortvideo.R.id.lsq_back);
		mBackBtn.setOnClickListener(mOnClickListener);
		TextView titleView = (TextView) findViewById(com.upyun.shortvideo.R.id.lsq_title);
		titleView.setText(TuSdkContext.getString("lsq_audio_record_text"));
		TextView nextBtn = (TextView) findViewById(com.upyun.shortvideo.R.id.lsq_next);
		nextBtn.setVisibility(View.GONE);
		mStartRecordButton = (Button) findViewById(com.upyun.shortvideo.R.id.lsq_audio_record_btn);
		mStopRecordButton = (Button) findViewById(com.upyun.shortvideo.R.id.lsq_audio_stop_btn);
		mPlayAudioButton = (Button) findViewById(com.upyun.shortvideo.R.id.lsq_audio_play_btn);
		mStartRecordButton.setOnClickListener(mOnClickListener);
		mPlayAudioButton.setOnClickListener(mOnClickListener);
		mStopRecordButton.setOnClickListener(mOnClickListener);
		initAudioFileRecorder();
		initMediaPlayer();
	}

	private void initAudioFileRecorder()
	{
		mAudioRecorder = new TuSDKAudioFileRecorder();
		mAudioRecorder.setOutputFormat(OutputFormat.AAC);
		mAudioRecorder.setAudioRecordDelegate(mRecordAudioDelegate);
	}
	
	/**
	 * 录音委托事件
	 */
	private TuSDKRecordAudioDelegate mRecordAudioDelegate = new TuSDKRecordAudioDelegate(){

		@Override
		public void onAudioRecordComplete(File file)
		{
			mAudioFile = file;
		}

		@Override
		public void onAudioRecordStateChanged(RecordState state)
		{
			if (state == RecordState.Recording)
			{
	            String hintMsg = getResources().getString(com.upyun.shortvideo.R.string.lsq_audio_record_recording);
	            TuSdk.messageHub().showToast(AudioRecordActivity.this, hintMsg);
			}
			else if (state == RecordState.Stoped)
			{
				String hintMsg = getResources().getString(com.upyun.shortvideo.R.string.lsq_audio_record_stopped);
	            TuSdk.messageHub().showToast(AudioRecordActivity.this, hintMsg);
			}
		}

		@Override
		public void onAudioRecordError(RecordError error)
		{
			if(error == RecordError.InitializationFailed)
			{
				String hintMsg = getResources().getString(com.upyun.shortvideo.R.string.lsq_audio_initialization_failed_hint);
				TuSdk.messageHub().showError(AudioRecordActivity.this,hintMsg);
			}
		}
	};
	
	private void initMediaPlayer()
	{
		mMediaPlayer = new MediaPlayer();  
		mMediaPlayer.setOnCompletionListener(mOnCompletionListener);
	}
	
	/**
	 * 音频播放结束监听事件
	 */
	private MediaPlayer.OnCompletionListener mOnCompletionListener = new MediaPlayer.OnCompletionListener() 
	{
		
		@Override
		public void onCompletion(MediaPlayer mp) 
		{
			String hintMsg = getResources().getString(com.upyun.shortvideo.R.string.lsq_audio_record_played);
			TuSdk.messageHub().showToast(AudioRecordActivity.this, hintMsg);
		}
	};
	
	private View.OnClickListener mOnClickListener = new View.OnClickListener()
	{
		
		@Override
		public void onClick(View v) 
		{
			if (v == mBackBtn)
			{
				finish();
			}
			else if (v == mStartRecordButton)
			{
				mAudioRecorder.start();
			}
			else if (v == mPlayAudioButton)
			{
				try {
					mMediaPlayer.setDataSource(mAudioFile.toString());
					mMediaPlayer.prepare();  
					mMediaPlayer.setOnPreparedListener(mOnPreparedListener);
				} catch (Exception e) {
					e.printStackTrace();
				}  
			}
			else if (v == mStopRecordButton)
			{
				mAudioRecorder.stop();
			}
		}
	};
	
	private MediaPlayer.OnPreparedListener mOnPreparedListener = new MediaPlayer.OnPreparedListener()
	{
		
		@Override
		public void onPrepared(MediaPlayer mp) 
		{
			mMediaPlayer.start();  
			String hintMsg = getResources().getString(com.upyun.shortvideo.R.string.lsq_audio_record_playing);
			TuSdk.messageHub().showToast(AudioRecordActivity.this, hintMsg);
		}
	};
}
