package com.upyun.shortvideo.api;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.media.MediaCodecInfo;
import android.media.MediaFormat;
import android.net.Uri;
import android.os.Bundle;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.upyun.shortvideo.album.MovieInfo;

import org.lasque.tusdk.api.movie.player.TuSDKMoviePlayer;
import org.lasque.tusdk.core.TuSdkContext;
import org.lasque.tusdk.core.api.extend.TuSdkMediaProgress;
import org.lasque.tusdk.core.common.TuSDKMediaUtils;
import org.lasque.tusdk.core.decoder.TuSDKVideoInfo;
import org.lasque.tusdk.core.media.codec.extend.TuSdkMediaFormat;
import org.lasque.tusdk.core.media.suit.TuSdkMediaSuit;
import org.lasque.tusdk.core.struct.TuSdkMediaDataSource;
import org.lasque.tusdk.core.struct.TuSdkSize;
import org.lasque.tusdk.core.utils.StringHelper;
import org.lasque.tusdk.core.utils.TLog;
import org.lasque.tusdk.core.utils.image.AlbumHelper;
import org.lasque.tusdk.core.utils.image.ImageOrientation;
import com.upyun.shortvideo.R;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import at.grabner.circleprogress.CircleProgressView;

import static android.widget.Toast.LENGTH_SHORT;

/**
 * 音视频文件拼接与裁剪
 *    注意：目前拼接工具类只支持相同的视频格式的mp4视频文件的拼接，
 *          且拼接的视频文件宽高，帧率，编码方式等都必须相同
 *          
 * @author leone.xia
 */
public class MovieSplicerActivity extends Activity
{
	/** 合成视频 Button */
	private Button mMuxerButton;
	
	/** 用于记录片段路径信息 */
	private List<TuSdkMediaDataSource> mMoviePathList = new ArrayList<>();

	/** 输入视频路径 */
	private String mInputPath;
	private List<MovieInfo> movieInfos;

	/** 保存路径 */
	private String mMuxerResultPath;
	
	/** 返回按钮 */
	private TextView mBackBtn;
	
	/** 第一个视频预览界面  */
	private SurfaceView mPreViewOne;
	
	/** 第二个视频预览界面 */
	private SurfaceView mPreViewTwo;
	
	/** 第一个视频播放器 */
    private TuSDKMoviePlayer mMediaPlayerOne;
    
    /** 第二个视频播放器 */
	private TuSDKMoviePlayer mMediaPlayerTwo;

	/** 压缩进度 */
	private CircleProgressView mCircleView;
	
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.movie_splicer_activity);
        initView();
    }
    
    public void initView()
    {
		Intent intent = getIntent();
    	if(intent.hasExtra("videoPath")){
			mInputPath = getIntent().getStringExtra("videoPath");
		}
		else if(intent.hasExtra("videoPaths"))
		{
			movieInfos = (List<MovieInfo>)getIntent().getSerializableExtra("videoPaths");
		}

    	mBackBtn = (TextView) findViewById(R.id.lsq_back);
		mBackBtn.setOnClickListener(mOnClickListener);
		TextView titleView = (TextView) findViewById(R.id.lsq_title);
		titleView.setText(TuSdkContext.getString("lsq_movie_splicer_text"));
		TextView nextBtn = (TextView) findViewById(R.id.lsq_next);
		nextBtn.setVisibility(View.GONE);

		mCircleView = findViewById(R.id.circleView);
		mCircleView.setTextSize(50);
		mCircleView.setAutoTextSize(true);
		mCircleView.setTextColor(Color.WHITE);
		mCircleView.setVisibility(View.GONE);
    	
    	mMuxerButton = (Button)this.findViewById(R.id.lsq_movie_mixer_btn);
    	mMuxerButton.setOnClickListener(mOnClickListener);
    	mPreViewOne = (SurfaceView) findViewById(R.id.lsq_movie_preview_one);
    	mPreViewOne.getLayoutParams().height = TuSdkContext.getScreenSize().width*9/16;
		mPreViewTwo = (SurfaceView) findViewById(R.id.lsq_movie_preview_two);
		mPreViewTwo.getLayoutParams().height = TuSdkContext.getScreenSize().width*9/16;
		initMediaPlayer();
    }
    
	private void initMediaPlayer()
	{
		mMediaPlayerOne = TuSDKMoviePlayer.createMoviePlayer();
		mMediaPlayerOne.setLooping(true);
		String firstPath = getIntent().hasExtra("videoPath") ? mInputPath : movieInfos.get(0).getPath();
		Uri firstVideoUri = Uri.parse(firstPath);
        mMoviePathList.add(new TuSdkMediaDataSource(firstPath));
        mMediaPlayerOne.initVideoPlayer(this, firstVideoUri, mPreViewOne);
        
        mMediaPlayerTwo = TuSDKMoviePlayer.createMoviePlayer();
        mMediaPlayerTwo.setLooping(true);
        String secondPath = getIntent().hasExtra("videoPath") ? mInputPath : movieInfos.get(1).getPath();
        Uri secondVideoUri = Uri.parse(secondPath);
        mMoviePathList.add(new TuSdkMediaDataSource(secondPath));
        mMediaPlayerTwo.initVideoPlayer(this, secondVideoUri, mPreViewTwo);		
	}
	
	@Override
	protected void onResume()
	{
		super.onResume();
		mMediaPlayerOne.start();
		mMediaPlayerTwo.start();
	}
	
	@Override
	protected void onPause()
	{
		super.onPause();
		mMediaPlayerOne.stop();
		mMediaPlayerTwo.stop();
	}
	
	@Override
	protected void onDestroy() 
	{
		super.onDestroy();
		mMediaPlayerOne.destory();
		mMediaPlayerTwo.destory();
		mMediaPlayerOne = null;
		mMediaPlayerTwo = null;
	}
	
	private OnClickListener mOnClickListener = new OnClickListener()
	{
		@Override
		public void onClick(View v)
		{
			switch (v.getId())
			{
			case R.id.lsq_back:
				finish();
				break;
			case R.id.lsq_movie_mixer_btn:
				 handleMuxerMovieFragmentData(getMovieResultPath());
			  	 break;	
			}
		}
	};
	
	/**
	 * 获取视频保存的路径
	 * 
	 * @return
	 */
	private String getMovieResultPath()
	{
		// 每次点击保存视频时，根据时间戳生成路径
		mMuxerResultPath = new File(AlbumHelper.getAblumPath(),
				String.format("lsq_%s.mp4", StringHelper.timeStampString())).toString();
		return mMuxerResultPath;
	}
	/**
	 * 音视频合成处理
	 * 
	 * @param muxerPath
	 */
	private void handleMuxerMovieFragmentData(String muxerPath)
	{
		TuSDKVideoInfo videoInfo = TuSDKMediaUtils.getVideoInfo(getIntent().hasExtra("videoPath") ? mInputPath : movieInfos.get(0).getPath());
		MediaFormat ouputVideoFormat = getOutputVideoFormat(videoInfo);
		MediaFormat ouputAudioFormat = getOutputAudioFormat();

		TuSdkMediaSuit.merge(mMoviePathList,muxerPath,ouputVideoFormat,ouputAudioFormat,mediaProgress);
		mCircleView.setVisibility(View.VISIBLE);
		Toast.makeText(MovieSplicerActivity.this, R.string.lsq_movie_splicer_processing, LENGTH_SHORT).show();
	}

	/**
	 * 视频拼接状态通知
	 */
	private TuSdkMediaProgress mediaProgress = new TuSdkMediaProgress()
	{
		@Override
		public void onProgress(float progress, TuSdkMediaDataSource mediaDataSource, int index,
                               int total)
		{
			TLog.i("onProgressChanged: " + progress);
			mCircleView.setText((progress * 100)+"%");
			mCircleView.setValue(progress);
		}

		@Override
		public void onCompleted(Exception e, TuSdkMediaDataSource outputFile, int total)
		{
			if(e == null)
			{
				Toast.makeText(MovieSplicerActivity.this, R.string.lsq_movie_splicer_success, LENGTH_SHORT).show();
			}
			else
			{
				Toast.makeText(MovieSplicerActivity.this, R.string.lsq_movie_splicer_error, LENGTH_SHORT).show();
			}
			hideCircleView();
		}
	};

	/**
	 * 隐藏进度
	 */
	private void hideCircleView()
	{
		mCircleView.setVisibility(View.GONE);
		mCircleView.setText("0%");
		mCircleView.setValue(0);
	}

	private int mFps = 0;
	private int mBitrate = 0;

	/**
	 * 获取输出文件的视频格式信息
	 *
	 * @return MediaFormat
	 */
	protected MediaFormat getOutputVideoFormat(TuSDKVideoInfo videoInfo)
	{
		int fps = mFps==0 ? videoInfo.fps : mFps;
		int bitrate = mBitrate==0 ? videoInfo.bitrate : mBitrate;
		TuSdkSize videoSize = TuSdkSize.create(videoInfo.width,videoInfo.height);

		if (videoInfo.videoOrientation == ImageOrientation.Right || videoInfo.videoOrientation == ImageOrientation.Left)
			videoSize = TuSdkSize.create(videoSize.height,videoSize.width);

		MediaFormat mediaFormat = TuSdkMediaFormat.buildSafeVideoEncodecFormat(videoSize.width, videoSize.height,
				fps, bitrate, MediaCodecInfo.CodecCapabilities.COLOR_FormatSurface, 0,1);

		return mediaFormat;
	}

	/**
	 * 获取输出的音频格式信息
	 * @return MediaFormat
	 */
	protected MediaFormat getOutputAudioFormat()
	{
		MediaFormat audioFormat = TuSdkMediaFormat.buildSafeAudioEncodecFormat();
		return audioFormat;
	}
	
}
