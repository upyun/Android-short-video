package com.upyun.shortvideo.api;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.lasque.tusdk.core.TuSdk;
import org.lasque.tusdk.core.TuSdkContext;
import org.lasque.tusdk.core.utils.StringHelper;
import org.lasque.tusdk.core.utils.TLog;
import org.lasque.tusdk.core.utils.image.AlbumHelper;
import org.lasque.tusdk.movie.muxer.TuSDKMovieSplicer;
import org.lasque.tusdk.movie.muxer.TuSDKMovieSplicer.TuSDKMovieSegment;
import org.lasque.tusdk.movie.muxer.TuSDKMovieSplicer.TuSDKMovieSplicerOption;
import org.lasque.tusdk.movie.player.TuSDKMoviePlayer;
import com.upyun.shortvideo.R;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

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
	private ArrayList<Uri> mMoviePathList = new ArrayList<Uri>();

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
	
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.movie_splicer_activity);
        initView();
    }
    
    public void initView()
    {
    	mBackBtn = (TextView) findViewById(R.id.lsq_back);
		mBackBtn.setOnClickListener(mOnClickListener);
		TextView titleView = (TextView) findViewById(R.id.lsq_title);
		titleView.setText(TuSdkContext.getString("lsq_movie_splicer_text"));
		TextView nextBtn = (TextView) findViewById(R.id.lsq_next);
		nextBtn.setVisibility(View.GONE);
    	
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
		Uri firstVideoUri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.tusdk_sample_splice_video);
        mMoviePathList.add(firstVideoUri);
        mMediaPlayerOne.initVideoPlayer(this, firstVideoUri, mPreViewOne);
        
        mMediaPlayerTwo = TuSDKMoviePlayer.createMoviePlayer();
        mMediaPlayerTwo.setLooping(true);
        Uri secondVideoUri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.tusdk_sample_video);
        mMoviePathList.add(secondVideoUri);
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
	    List<TuSDKMovieSegment> list = new ArrayList<TuSDKMovieSegment>();
	    for (int i = 0; i < 2; i++)
	    {
	    	TuSDKMovieSegment segment = new TuSDKMovieSegment();
            segment.sourceUri = mMoviePathList.get(i);
            // 可以指定开始与结束时间,单位为毫秒
            // segment.startTime = 1000;
            // segment.endTime = 3000;
            list.add(segment);
	    }
	    TuSDKMovieSplicerOption option = new TuSDKMovieSplicerOption();
	    option.savePath = muxerPath;
	    option.listener = mMovieSplicerListener;
	    TuSDKMovieSplicer movieDataHelper = new TuSDKMovieSplicer(option);
	    movieDataHelper.start(list);
	}
	
	/**
	 * 刷新图库
	 * 
	 * @param file
	 */
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
	 * 视频拼接状态通知
	 */
	private TuSDKMovieSplicer.TuSDKMovieSplicerListener mMovieSplicerListener = new TuSDKMovieSplicer.TuSDKMovieSplicerListener()
	{
		
		@Override
		public void onStart()
		{
			String hintMsg = getResources().getString(R.string.lsq_movie_splicer_processing);
			TuSdk.messageHub().setStatus(MovieSplicerActivity.this, hintMsg);
		}
		
		@Override
		public void onError(Exception exception)
		{
			String hintMsg = getResources().getString(R.string.lsq_movie_splicer_error);
			TuSdk.messageHub().showError(MovieSplicerActivity.this, hintMsg);
		}
		
		@Override
		public void onDone() 
		{
			String hintMsg = getResources().getString(R.string.lsq_movie_splicer_success);
			TuSdk.messageHub().showToast(MovieSplicerActivity.this, hintMsg);
			refreshFile(new File(mMuxerResultPath));
		}
	}; 
}
