/**
 * TuSDKVideoDemo
 * AudioMixerActivity.java
 *
 * @author  LiuHang
 * @Date  Jul 21, 2017 14:52:11 PM
 * @Copright (c) 2017 tusdk.com. All rights reserved.
 *
 */

package com.upyun.shortvideo.api;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


import org.lasque.tusdk.core.TuSdk;
import org.lasque.tusdk.core.TuSdkContext;
import org.lasque.tusdk.core.decoder.TuSDKAudioInfo;
import org.lasque.tusdk.core.encoder.audio.TuSDKAACAudioFileEncoder;
import org.lasque.tusdk.core.encoder.audio.TuSDKAudioEncoderSetting;
import org.lasque.tusdk.core.utils.StringHelper;
import org.lasque.tusdk.core.utils.image.AlbumHelper;
import org.lasque.tusdk.api.audio.preproc.mixer.TuSDKAudioEntry;
import org.lasque.tusdk.api.audio.preproc.mixer.TuSDKAudioMixer;
import org.lasque.tusdk.api.audio.preproc.mixer.TuSDKAudioMixer.OnAudioMixerDelegate;
import org.lasque.tusdk.api.audio.preproc.mixer.TuSDKAverageAudioMixer;
import org.lasque.tusdk.api.audio.player.TuSDKMutiAudioPlayer;

import com.upyun.shortvideo.views.CompoundConfigView;
import com.upyun.shortvideo.views.ConfigViewParams;
import com.upyun.shortvideo.views.ConfigViewParams.ConfigViewArg;
import com.upyun.shortvideo.views.ConfigViewSeekBar;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

/**
 * 多音轨混合
 * 
 * @author LiuHang
 *
 */
public class AudioMixerActivity extends Activity
{
	private int[] mAudioEntries = new int[]{com.upyun.shortvideo.R.raw.lsq_audio_lively, com.upyun.shortvideo.R.raw.lsq_audio_oldmovie, com.upyun.shortvideo.R.raw.lsq_audio_relieve};
	
	private TextView mBackBtn;
	private Button mAudioMixerButton;
	private Button mDeleteMixingButton;
	private Button mPlayMixingButton;
	private Button mPauseMixingButton;
	private CompoundConfigView mVoiceConfigView;

	/** 音频混合对象 */
	private TuSDKAverageAudioMixer mAudioMixer;
	/** AAC音频文件编码器 */
	private TuSDKAACAudioFileEncoder mAACFileEncoder;
	/** 多音轨播放器 */
	private TuSDKMutiAudioPlayer mMutiAudioPlayer;
	/** 混合的音频数据 */
	private List<TuSDKAudioEntry> mAudioEntryList;
	/** 混合后输出的文件地址 */
	private String mMixedAudioPath;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(com.upyun.shortvideo.R.layout.audio_mixer_activity);
		// 初始化多音轨播放器 用于播放混音后的音频
		initMutiAudioPlayer();
		
		initView();
		// 初始化多音轨混合对象 用于对音频数据进行混音
		initAudioMixer();
	}
	
	private void initView() 
	{
		mBackBtn = (TextView) findViewById(com.upyun.shortvideo.R.id.lsq_back);
		mBackBtn.setOnClickListener(mOnClickListener);
		TextView titleView = (TextView) findViewById(com.upyun.shortvideo.R.id.lsq_title);
		titleView.setText(TuSdkContext.getString("lsq_audio_mixer_text"));
		TextView nextBtn = (TextView) findViewById(com.upyun.shortvideo.R.id.lsq_next);
		nextBtn.setVisibility(View.GONE);
		initVoiceConfigView();
		mAudioMixerButton = (Button) findViewById(com.upyun.shortvideo.R.id.lsq_audio_mixer_btn);
		mDeleteMixingButton = (Button) findViewById(com.upyun.shortvideo.R.id.lsq_delete_mixing_btn);
		mPlayMixingButton = (Button) findViewById(com.upyun.shortvideo.R.id.lsq_play_mixing_btn);
		mPauseMixingButton = (Button) findViewById(com.upyun.shortvideo.R.id.lsq_pause_mixing_btn);
		mAudioMixerButton.setOnClickListener(mOnClickListener);
		mDeleteMixingButton.setOnClickListener(mOnClickListener);
		mPlayMixingButton.setOnClickListener(mOnClickListener);
		mPauseMixingButton.setOnClickListener(mOnClickListener);
	}
	
	private CompoundConfigView getVoiceConfigView()
	{
		if (mVoiceConfigView == null)
		{
			mVoiceConfigView = (CompoundConfigView) findViewById(com.upyun.shortvideo.R.id.lsq_voice_volume_config_view);
		}

		return mVoiceConfigView;
	}
	
	private void initVoiceConfigView()
	{
		if(getVoiceConfigView() != null)
		{
			ConfigViewParams params = new ConfigViewParams();
			params.appendFloatArg(TuSdkContext.getString("origin"), 1.0f);
			params.appendFloatArg(TuSdkContext.getString("dubbingone"), 1.0f);
			params.appendFloatArg(TuSdkContext.getString("dubbingtwo"), 1.0f);
			getVoiceConfigView().setSeekBarHeight(TuSdkContext.dip2px(50));
			getVoiceConfigView().setSeekBarTitleWidh(TuSdkContext.dip2px(50));
			getVoiceConfigView().setDelegate(mFilterConfigSeekbarDelegate);
			getVoiceConfigView().setCompoundConfigView(params);
			
			for(int i = 0; i < getVoiceConfigView().getSeekBarList().size(); i++ )
			{
				// 初始化音量调节SeeKBar
				this.setSeekBarProgress(i, 0.5f);
			}			
		}	
	}

	/**
	 * 准备音频数据
	 */
	private List<TuSDKAudioEntry> getAudioEntryList()
	{
		if (mAudioEntryList != null && mAudioEntryList.size() > 0) return mAudioEntryList;
		
		mAudioEntryList = new ArrayList<TuSDKAudioEntry>(3);
		
		for (int i = 0; i < mAudioEntries.length; i++)
		{
			Uri uri = Uri.parse("android.resource://" + getPackageName() + "/" + mAudioEntries[i]);
			
			TuSDKAudioEntry audioEntry = new TuSDKAudioEntry(uri);
			audioEntry.setTrunk( i == 0 );
			
			mAudioEntryList.add(audioEntry);
		}
		
		return mAudioEntryList;
	}
	
	@Override
	protected void onPause() 
	{
		super.onPause();
		
		if( mMutiAudioPlayer != null)
			mMutiAudioPlayer.stop();
	}
	
	/**
	 * 
	 * @return
	 */
	private String getMixedAudioPath()
	{
		mMixedAudioPath = new File(AlbumHelper.getAblumPath(),String.format("lsq_%s.aac", StringHelper.timeStampString())).getPath();		
		return mMixedAudioPath;
	}
	
	/**
	 * 初始化多音轨混合对象
	 */
	private void initAudioMixer()
	{
		 mAudioMixer = new TuSDKAverageAudioMixer();
		 mAudioMixer.setOnAudioMixDelegate(mAudioMixerDelegate);
	}
	
	/**
	 * 启动音频混合
	 */
	private void startAudioMixer()
	{
		stopMutiAudioPlayer();
		/** AAC 音频文件编码器，可将混合的音频数据编码为AAC文件 */
		mAACFileEncoder = new TuSDKAACAudioFileEncoder();
		
		// 初始化音频编码器
		mAACFileEncoder.initEncoder(TuSDKAudioEncoderSetting.defaultEncoderSetting());
		mAACFileEncoder.setOutputFilePath(getMixedAudioPath());
		mAACFileEncoder.start();
		
		mAudioMixer.mixAudios(getAudioEntryList());
	}
	
	/**
	 * 取消音频混合
	 */
	private void cancelAudioMixer()
	{
		mAudioMixer.cancel();
	}
	
	/**
	 * 删除混合的音频
	 */
	private void delMixedFile()
	{
		if (mMixedAudioPath == null) return;
		stopMutiAudioPlayer();
		new File(mMixedAudioPath).delete();
		if(!new File(mMixedAudioPath).exists())
		{
			String hintMsg = getResources().getString(com.upyun.shortvideo.R.string.lsq_audio_mixer_delete_success);
			TuSdk.messageHub().showToast(this, hintMsg);
			for(int i = 0; i < getVoiceConfigView().getSeekBarList().size(); i++ )
			{
				this.setSeekBarProgress(i, 0.0f);
			}	
		}
		else 
		{
			String hintMsg = getResources().getString(com.upyun.shortvideo.R.string.lsq_audio_mixer_delete_failed);
			TuSdk.messageHub().showToast(this, hintMsg);
		}
	}
	
	/**
	 * 音频混合Delegate
	 */
	private OnAudioMixerDelegate mAudioMixerDelegate = new OnAudioMixerDelegate() 
	{
		/**
		 * 混合状态改变事件
		 */
		@Override
		public void onStateChanged(TuSDKAudioMixer.State state) 
		{
			if (state == TuSDKAudioMixer.State.Complete)
			{
				// 停止AAC编码器
				mAACFileEncoder.stop();
				
				TuSdk.messageHub().showSuccess(AudioMixerActivity.this, "混合完成");
				
			}else if(state == TuSDKAudioMixer.State.Decoding || state == TuSDKAudioMixer.State.Mixing)
			{
				TuSdk.messageHub().setStatus(AudioMixerActivity.this, "混合中");		
				
			}else if(state == TuSDKAudioMixer.State.Cancelled)
			{
				delMixedFile();
			}
		}
		
		/**
		 * 当前解析到主背景音乐信息时回调该方法，其他音乐将参考该信息进行混合
		 */
		@Override
		public void onReayTrunkTrackInfo(TuSDKAudioInfo rawInfo)
		{
		}
		
		@Override
		public void onMixingError(int errorCode) 
		{
			TuSdk.messageHub().showError(AudioMixerActivity.this, "混合失败");
		}
		
		/**
		 * 混合后的音频数据（未经编码）
		 */
		@Override
		public void onMixed(byte[] mixedBytes) 
		{
			// 编码音频数据
			mAACFileEncoder.queueAudio(mixedBytes);
		}
	};
	
	/**
	 * 初始化多音轨播放器
	 */
	private void initMutiAudioPlayer()
	{
		mMutiAudioPlayer = new TuSDKMutiAudioPlayer();
		mMutiAudioPlayer.setDelegate(mMutiAudioPlayerDelegate);
		mMutiAudioPlayer.setLooping(true);
		mMutiAudioPlayer.asyncPrepare(getAudioEntryList());
	}
	
	/**
	 * 开始播放音频数据
	 */
	private void startMutiAudioPlayer()
	{
		mMutiAudioPlayer.start();
	}
	
	/**
	 * 停止音频播放器
	 */
	private void stopMutiAudioPlayer()
	{
		mMutiAudioPlayer.stop();
	}
	
	/**
	 * 多音轨混合播放器Delegate
	 */
	private TuSDKMutiAudioPlayer.TuSDKMutiAudioPlayerDelegate mMutiAudioPlayerDelegate = new TuSDKMutiAudioPlayer.TuSDKMutiAudioPlayerDelegate()
	{
		/**
		 * 播放器状态改变事件
		 */
		@Override
		public void onStateChanged(TuSDKMutiAudioPlayer.State state) {
			
			if (state == TuSDKMutiAudioPlayer.State.PrePared)
				startMutiAudioPlayer();
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
			else if (v == mAudioMixerButton) 
			{
				startAudioMixer();
			}
			else if (v == mDeleteMixingButton) 
			{
				delMixedFile();
			}
			else if (v == mPlayMixingButton) 
			{
				if (getMixedAudioEntry(mMixedAudioPath) == null) return;
				mMutiAudioPlayer.asyncPrepare(getMixedAudioEntry(mMixedAudioPath));
			}
			else if (v == mPauseMixingButton) 
			{
				cancelAudioMixer();
				stopMutiAudioPlayer();
			}
		}
	};
	
	private List<TuSDKAudioEntry> getMixedAudioEntry(String mixedAudioPath)
	{
		if (!new File(mixedAudioPath).exists()) return null;
		List<TuSDKAudioEntry> audioEntryList = new ArrayList<TuSDKAudioEntry>();
		TuSDKAudioEntry audioEntry = new TuSDKAudioEntry(mixedAudioPath);
		audioEntryList.add(audioEntry);
		return audioEntryList;
	}
	
	private void setSeekBarProgress(int index, float preogress)
	{
		getVoiceConfigView().getSeekBarList().get(index).setProgress(preogress);
		
		/**
		 * 设置音频音量
		 */
		getAudioEntryList().get(index).setVolume(preogress);
		if (mMutiAudioPlayer.getState() == TuSDKMutiAudioPlayer.State.Idle)
			mMutiAudioPlayer.asyncPrepare(getAudioEntryList());
	}
	
	/**
	 * 原音配音调节栏委托事件
	 */
	private ConfigViewSeekBar.ConfigSeekbarDelegate mFilterConfigSeekbarDelegate = new ConfigViewSeekBar.ConfigSeekbarDelegate() 
	{
		
		@Override
		public void onSeekbarDataChanged(ConfigViewSeekBar seekbar, ConfigViewArg arg) 
		{
			if (arg.getKey().equals("origin"))
					setSeekBarProgress(0,arg.getPercentValue());
			else if (arg.getKey().equals("dubbingone"))
					setSeekBarProgress(1,arg.getPercentValue());
			else if (arg.getKey().equals("dubbingtwo"))
				setSeekBarProgress(2,arg.getPercentValue());
		}
	};
}