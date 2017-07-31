/**
 * TuSDKVideoDemo
 * MovieMixerActivity.java
 *
 * @author     gh.li
 * @Date:      2017-6-27 下午8:45:55
 * @Copyright: (c) 2017 tusdk.com. All rights reserved.
 *
 */
package com.upyun.shortvideo.api;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.lasque.tusdk.core.TuSdk;
import org.lasque.tusdk.core.TuSdkContext;
import org.lasque.tusdk.core.utils.StringHelper;
import org.lasque.tusdk.core.utils.TLog;
import org.lasque.tusdk.core.utils.image.AlbumHelper;
import org.lasque.tusdk.core.video.TuSDKVideoResult;
import org.lasque.tusdk.movie.player.TuSDKMoviePlayer;
import org.lasque.tusdk.movie.player.TuSDKMoviePlayer.PlayerState;
import org.lasque.tusdk.movie.player.TuSDKMoviePlayer.TuSDKMoviePlayerDelegate;
import org.lasque.tusdk.movie.player.TuSDKMutiAudioPlayer;
import org.lasque.tusdk.movie.player.TuSDKMutiAudioPlayer.TuSDKMutiAudioPlayerDelegate;
import org.lasque.tusdk.video.mixer.TuSDKAudioEntry;
import org.lasque.tusdk.video.mixer.TuSDKMP4MovieMixer;
import org.lasque.tusdk.video.mixer.TuSDKMP4MovieMixer.ErrorCode;
import org.lasque.tusdk.video.mixer.TuSDKMP4MovieMixer.OnMP4MovieMixerDelegate;
import org.lasque.tusdk.video.mixer.TuSDKMP4MovieMixer.State;
import org.lasque.tusdk.video.mixer.TuSDKMediaDataSource;
import com.upyun.shortvideo.R;
import com.upyun.shortvideo.views.CompoundConfigView;
import com.upyun.shortvideo.views.ConfigViewParams;
import com.upyun.shortvideo.views.ConfigViewParams.ConfigViewArg;
import com.upyun.shortvideo.views.ConfigViewSeekBar;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

/**
 * 音视频混合
 * 
 * 支持将多个音频数据混合后写入视频
 * 
 * @author gh.li
 */
public class MovieMixerActivity extends Activity implements OnMP4MovieMixerDelegate
{
	private final static int[] AUIDOENTRY_RESIDARRAY = new int[]{R.raw.tusdk_sample_video,R.raw.lsq_audio_oldmovie, R.raw.lsq_audio_relieve};
	
	/** MP4视频格式混合 */
	private TuSDKMP4MovieMixer mMP4MovieMixer;
	/** 混合的音频数据 */
	private List<TuSDKAudioEntry> mAudioEntryList = new ArrayList<TuSDKAudioEntry>();
	
	/** 音频音量调节栏 */
	private CompoundConfigView mVoiceConfigView;
	
	/** 多音轨播放器 */
	private TuSDKMutiAudioPlayer mMutiAudioPlayer;
	
	/** 视频预览界面 */
	private SurfaceView mMoviePreviewLayout;
	
	/** 视频播放器  */
	private TuSDKMoviePlayer mMoviePlayer;
	
	/** 返回按钮 */
	private TextView mBackBtn;
	
	/** 视频合成按钮 */
	private Button mMovieMixerButton;
	
	/** 混合后的视频地址 */
	private String mMixedVideoPath;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.movie_mixer_activity);
		initView();
		getAudioEntryList();
		initMediaPlayer();
		initVoiceConfigView();
	}
	
	private void initMediaPlayer()
	{
		mMoviePlayer = TuSDKMoviePlayer.createMoviePlayer();
		mMoviePlayer.setLooping(true);
        mMoviePlayer.initVideoPlayer(this, getVideoPath(), getPreviewLayout());
        mMoviePlayer.setDelegate(mMoviePlayerDelegate);
	}
	
	private TuSDKMoviePlayerDelegate mMoviePlayerDelegate = new TuSDKMoviePlayerDelegate()
	{
		@Override
		public void onStateChanged(PlayerState state) 
		{
			if (state == PlayerState.INITIALIZED)
			{
				mMoviePlayer.setVolume(0.0f);
				prepareAudio();
			}
		}

		@Override
		public void onVideSizeChanged(MediaPlayer mp,int width, int height)
		{
		}

		@Override
		public void onProgress(int progress)
		{
		}

		@Override
		public void onSeekComplete()
		{
		}

		@Override
		public void onCompletion() 
		{

		}
	};
	
	private Uri getVideoPath()
	{
		Uri videoPathUri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.tusdk_sample_video);
		return videoPathUri;
	}

	/**
	 * 准备音频播放器
	 */
	private void prepareAudio()
	{
		if (mMutiAudioPlayer == null) initMutiAudioPlayer();
		mMutiAudioPlayer.setLooping(true);
		mMutiAudioPlayer.asyncPrepare(getAudioEntryList());
	}
	
	/**
	 * 初始化多音轨播放器
	 */
	private void initMutiAudioPlayer()
	{
		mMutiAudioPlayer = new TuSDKMutiAudioPlayer();
		mMutiAudioPlayer.setDelegate(mMutiAudioPlayerDelegate);
	}
	
	/**
	 * 多音轨混合播放器Delegate
	 */
	private TuSDKMutiAudioPlayerDelegate mMutiAudioPlayerDelegate = new TuSDKMutiAudioPlayerDelegate() 
	{
		/**
		 * 播放器状态改变事件
		 */
		@Override
		public void onStateChanged(TuSDKMutiAudioPlayer.State state)
		{
			if (state == TuSDKMutiAudioPlayer.State.PrePared)
			{
				startMutiAudioPlayer();
				startMoviePlayer();
			}
				
		}
	};
	
	/**
	 * 开始播放音频数据
	 */
	private void startMutiAudioPlayer()
	{
		if (mMutiAudioPlayer == null) return;
		mMutiAudioPlayer.start();
	}
	
	/**
	 * 停止音频播放器
	 */
	private void stopMutiAudioPlayer()
	{
		if (mMutiAudioPlayer == null) return;
		mMutiAudioPlayer.stop();
	}
	
	private void startMoviePlayer()
	{
		if (mMoviePlayer == null) return;
		mMoviePlayer.start();
	}
	
	private void stopMoviePlayer()
	{
		if (mMoviePlayer == null) return;
		mMoviePlayer.stop();
	}
	private boolean isFirst = true;
	@Override
	protected void onResume()
	{
		super.onResume();
		if(!isFirst)
		{
			startMutiAudioPlayer();
			startMoviePlayer();
		}
	}
	
	@Override
	protected void onPause() 
	{
		super.onPause();
		stopMutiAudioPlayer();
		stopMoviePlayer();
		isFirst = false;
	}
	
	@Override
	protected void onDestroy() 
	{
		super.onDestroy();
		stopMutiAudioPlayer();
		stopMoviePlayer();
		mMoviePlayer = null;
		mMutiAudioPlayer = null;
	}
	
	private SurfaceView getPreviewLayout()
	{
		if (mMoviePreviewLayout == null)
		{
			mMoviePreviewLayout = (SurfaceView) findViewById(R.id.lsq_movie_mixer_preview);
			int movieWidth = TuSdkContext.getScreenSize().width;
			int movieHeight = movieWidth*9/16;
			LinearLayout.LayoutParams lp =  (LayoutParams) mMoviePreviewLayout.getLayoutParams();
			lp.width = movieWidth;
			lp.height = movieHeight;
		}
		return mMoviePreviewLayout;
	}

	/**
	 * 初始化视图
	 */
	private void initView()
	{
		mBackBtn = (TextView) findViewById(R.id.lsq_back);
		mBackBtn.setOnClickListener(mOnClickListener);
		TextView titleView = (TextView) findViewById(R.id.lsq_title);
		titleView.setText(TuSdkContext.getString("lsq_movie_mixer_text"));
		TextView nextBtn = (TextView) findViewById(R.id.lsq_next);
		nextBtn.setVisibility(View.GONE);
		mMovieMixerButton = (Button) findViewById(R.id.lsq_movie_mixer_start);
		mMovieMixerButton.setOnClickListener(mOnClickListener);
		TuSdk.messageHub().applyToViewWithNavigationBarHidden(false);

	}
	
	private String getMixedVideoPath()
	{
		mMixedVideoPath = new File(AlbumHelper.getAblumPath(),String.format("lsq_%s.mp4",
    			StringHelper.timeStampString())).getPath();
		return mMixedVideoPath;
	}
	
	/**
	 * 准备音频数据
	 */
	private List<TuSDKAudioEntry> getAudioEntryList()
	{
		if (mAudioEntryList != null && mAudioEntryList.size() > 0) return mAudioEntryList;
		
		mAudioEntryList = new ArrayList<TuSDKAudioEntry>(2);
		
		for (int i = 0; i < AUIDOENTRY_RESIDARRAY.length; i++)
		{
			Uri uri = Uri.parse("android.resource://" + getPackageName() + "/" + AUIDOENTRY_RESIDARRAY[i]);
			
			TuSDKAudioEntry audioEntry = new TuSDKAudioEntry(uri);
			audioEntry.setTrunk( i == 0 );
			
			mAudioEntryList.add(audioEntry);
		}
		
		return mAudioEntryList;
	}
	
	/**
	 * 初始化音频音量调节栏
	 */
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
			getVoiceConfigView().setDelegate(mVoiceConfigSeekbarDelegate);
			getVoiceConfigView().setCompoundConfigView(params);
			
			for(int i = 0; i < getVoiceConfigView().getSeekBarList().size(); i++ )
			{
				// 初始化音量调节SeeKBar
				this.setSeekBarProgress(i, 0.5f);
			}			
		}	
	}

	private void setSeekBarProgress(int index, float progress)
	{
		getVoiceConfigView().getSeekBarList().get(index).setProgress(progress);
		
		// 设置音频音量
		getAudioEntryList().get(index).setVolume(progress);
	}
	
	/**
	 * 原音配音调节栏委托事件
	 */
	private ConfigViewSeekBar.ConfigSeekbarDelegate mVoiceConfigSeekbarDelegate = new ConfigViewSeekBar.ConfigSeekbarDelegate() 
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
	
	private CompoundConfigView getVoiceConfigView()
	{
		if (mVoiceConfigView == null)
		{
			mVoiceConfigView = (CompoundConfigView) findViewById(R.id.lsq_voice_config_view);
		}

		return mVoiceConfigView;
	}
	
	/**
	 * 混合音视频
	 */
	private void startMovieMixer()
	{
		// 混合音视频前需将音频、视频暂停
		stopMoviePlayer();
		stopMutiAudioPlayer();
		
		mMP4MovieMixer = new TuSDKMP4MovieMixer();
		mMP4MovieMixer.setDelegate(this)
		  			  .setOutputFilePath(getMixedVideoPath()) // 设置输出路径
		  			  .setVideoSoundVolume(1.f) // 设置音乐音量
		  			  .setClearAudioDecodeCacheInfoOnCompleted(true) // 设置音视频混合完成后是否清除缓存信息 默认：true （false:再次混合时可加快混合速度）
		  			  .mix(TuSDKMediaDataSource.create(getVideoPath()), mAudioEntryList, false); //  mVideoDataSource : 视频路径 mAudioTracks : 待混合的音频数据 true ： 是否混合视频原音
	}

	 public void refreshFile(File file) 
	 {
		if (file == null) {
			TLog.e("refreshFile file == null");
			return;
		}
		
		Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
		Uri uri = Uri.fromFile(file);
		intent.setData(uri);
		this.sendBroadcast(intent);
	 }
	 
	/**
	 * 混合状态通知
	 */
	@Override
	public void onStateChanged(State state)
	{
		if(state == State.Decoding)
		{
			TuSdk.messageHub().setStatus(this, "正在解码...");
			
		}else if(state == State.Mixing)
		{
			TuSdk.messageHub().setStatus(this, "正在混合...");
			
		}else if(state == State.Failed)
		{
			TuSdk.messageHub().setStatus(this, "混合失败");
		}
		else 
		{
			TuSdk.messageHub().dismissRightNow();
		}
	}
	
	/**
	 * 混合结果回调
	 */
	@Override
	public void onMixerComplete(TuSDKVideoResult result) 
	{
		if(result != null)
		{
			TuSdk.messageHub().showSuccess(this, "混合完成,请到 DCIM 目录下查看");
			refreshFile(new File(mMixedVideoPath));
			TLog.d("result： %s", result.videoInfo);
		}
	}

	@Override
	public void onErrrCode(ErrorCode code) 
	{
		if(code == ErrorCode.UnsupportedVideoFormat)
		{
			TuSdk.messageHub().showError(this, "不支持的视频格式");
		}
	}
	
	private View.OnClickListener mOnClickListener = new View.OnClickListener()
	{
		
		@Override
		public void onClick(View v) 
		{
			switch (v.getId())
			{
				case R.id.lsq_movie_mixer_start:
					startMovieMixer();
					break;
				case R.id.lsq_back:
					finish();
					break;
			}
		}
	};
}
