/**
 * TuSDKVideoDemo
 *
 * @author  LiuHang
 * @Date  Jul 13, 2017 16:52:11 PM
 * @Copright (c) 2017 tusdk.com. All rights reserved.
 *
 */

package com.upyun.shortvideo.api;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.RectF;
import android.media.AudioManager;
import android.media.MediaCodecInfo;
import android.media.MediaFormat;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.media.MediaPlayer.OnSeekCompleteListener;
import android.media.MediaPlayer.OnVideoSizeChangedListener;
import android.net.Uri;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.upyun.shortvideo.views.MovieRangeSelectionBar;
import com.upyun.shortvideo.views.MovieRangeSelectionBar.OnCursorChangeListener;

import org.lasque.tusdk.api.video.retriever.TuSDKVideoImageExtractor;
import org.lasque.tusdk.api.video.retriever.TuSDKVideoImageExtractor.TuSDKVideoImageExtractorDelegate;
import org.lasque.tusdk.core.TuSdk;
import org.lasque.tusdk.core.TuSdkContext;
import org.lasque.tusdk.core.api.extend.TuSdkMediaProgress;
import org.lasque.tusdk.core.common.TuSDKMediaDataSource;
import org.lasque.tusdk.core.common.TuSDKMediaUtils;
import org.lasque.tusdk.core.decoder.TuSDKVideoInfo;
import org.lasque.tusdk.core.media.codec.extend.TuSdkMediaFormat;
import org.lasque.tusdk.core.media.codec.extend.TuSdkMediaTimeSlice;
import org.lasque.tusdk.core.media.suit.TuSdkMediaSuit;
import org.lasque.tusdk.core.struct.TuSdkMediaDataSource;
import org.lasque.tusdk.core.struct.TuSdkSize;
import org.lasque.tusdk.core.utils.RectHelper;
import org.lasque.tusdk.core.utils.StringHelper;
import org.lasque.tusdk.core.utils.ThreadHelper;
import org.lasque.tusdk.core.utils.image.AlbumHelper;
import org.lasque.tusdk.core.utils.image.ImageOrientation;
import org.lasque.tusdk.video.editor.TuSDKTimeRange;
import com.upyun.shortvideo.R;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import at.grabner.circleprogress.CircleProgressView;

/**
 * 视频时间裁剪
 * 
 * @author LiuHang
 *
 */
public class MovieCutActivity extends Activity
{
	private static final int REQUEST_CODE  = 0;
	/** 播放  Button */
	private Button mPlayButton;
	/** 返回  TextView */
	private TextView mBackTextView;
	/** 下一步  TextView */
	private TextView mCutTextView;
	/** PLAY TIME TextView */
	private TextView mPlayTextView;
	/** LEFT TIME TextView */
	private TextView mLeftTextView;
	/** RIGHT TIME TextView */
	private TextView mRightTextView;
	/** 视频输出宽度 **/
	private EditText outputWidth;
	/** 视频输出高度 **/
	private EditText outputHeight;
	/** 视频裁剪控件  */
	private MovieRangeSelectionBar mRangeSelectionBar;
	/** 记录裁剪控件的宽度  */
	private int seekBarWidth;
	/** 用于显示视频  */
	private SurfaceView mSurfaceView;
	/** MediaPlayer 播放器  */
    private MediaPlayer mMediaPlayer;
    /** 视频播放地址  */
    private Uri mVideoPathUri ;
    /** 视频总时长  */
    private int mVideoTotalTime;
    /** 裁剪视频的开始时间 */
    private int mStart_time;
    /** 裁剪视频的结束时间  */
    private int mEnd_time;
    /** 记录是否移动裁剪控件左光标  */
    private boolean isMoveLeft;
    /** 记录是否移动裁剪控件右光标  */
    private boolean isMoveRight;
    
    private boolean isMoveStartTime;
    /** 是否播放视频   */
    private boolean isPlay;
	/** 是否暂停视频   */
	private boolean isPause;
    /** 
     *  视频是否正在准备中
     *  true 表示正在准备中
     *  false 表示准备完成
     */
	private boolean isInit = false;
    /** 
     *  视频是否 是第一次加载
     *  true 
     *  false 
     */
	private boolean isFirstLoadVideo = false;

	/** 裁剪后视频时长,单位s*/
	private TuSDKTimeRange mCuTimeRange;

	/** 视频路径 */
	private String mInputPath;

	/** 剪切进度 */
	private CircleProgressView mCircleView;

	/** 剪裁区域设置 */
	private LinearLayout mCutRect;

	/** 视频格式设置 */
	private LinearLayout mVideoFormatBar;

	/** 帧率组 */
	private RadioGroup fpsGroup;

	/** 码率组 */
	private RadioGroup bitrateGroup;

	/** 剪裁区域输入框 */
	private List<EditText> editTexts = new ArrayList<>();

	/** 裁剪进度回调  **/
	private TuSdkMediaProgress mCuterMediaProgress = new TuSdkMediaProgress()
	{

		@Override
		public void onProgress(float progress, TuSdkMediaDataSource mediaDataSource,
							   int index, int total)
		{
			mCircleView.setText((progress * 100)+"%");
			mCircleView.setValue(progress);
		}

		@Override
		public void onCompleted(Exception e, TuSdkMediaDataSource outputFile, int total)
		{
			Toast.makeText(getBaseContext(),e == null ? getResources().getString(R.string.lsq_movie_cut_done) : getResources().getString(R.string.lsq_movie_cut_error),Toast.LENGTH_SHORT).show();
			mCircleView.setVisibility(View.GONE);
			mCircleView.setText("0%");
			mCircleView.setValue(0);

			try {
				mMediaPlayer.reset();
				mMediaPlayer.setDataSource(outputFile.getPath());
				mMediaPlayer.prepareAsync();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.movie_cut_activity);

		initView();
		initEdit();
	}

	/**
	 * 初始化输入框
	 */
	private void initEdit()
	{
		EditText rectLeft = findViewById(R.id.lsq_draw_rect_left_et);
		EditText rectTop = findViewById(R.id.lsq_draw_rect_top_et);
		EditText rectRight = findViewById(R.id.lsq_draw_rect_right_et);
		EditText rectBottom = findViewById(R.id.lsq_draw_rect_bottom_et);
		EditText cutLeft = findViewById(R.id.lsq_cut_rect_left_et);
		EditText cutTop = findViewById(R.id.lsq_cut_rect_top_et);
		EditText cutRight = findViewById(R.id.lsq_cut_rect_right_et);
		EditText cutBottom = findViewById(R.id.lsq_cut_rect_bottom_et);
		outputWidth = findViewById(R.id.lsq_output_width);
		outputHeight = findViewById(R.id.lsq_output_height);

		editTexts.add(rectLeft);
		editTexts.add(rectTop);
		editTexts.add(rectRight);
		editTexts.add(rectBottom);
		editTexts.add(cutLeft);
		editTexts.add(cutTop);
		editTexts.add(cutRight);
		editTexts.add(cutBottom);

		TuSDKVideoInfo videoInfo = TuSDKMediaUtils.getVideoInfo(mInputPath);


		TuSdkSize videoSize = TuSdkSize.create(videoInfo.width,videoInfo.height);

		// 如果视频拍摄的是有方向的
        if (videoInfo.videoOrientation == ImageOrientation.Right || videoInfo.videoOrientation == ImageOrientation.Left)
            videoSize = TuSdkSize.create(videoSize.height,videoSize.width);

		outputWidth.setText(String.valueOf(videoSize.width));
		outputHeight.setText(String.valueOf(videoSize.height));
	}

	/**
	 * 初始化视图
	 */
	protected void initView()
	{
		mInputPath = getIntent().getStringExtra("videoPath");

		mVideoPathUri = Uri.parse(mInputPath);
		mBackTextView = (TextView) this.findViewById(R.id.lsq_back);
		mBackTextView.setOnClickListener(mClickListener);
		
		mCutTextView = (TextView) this.findViewById(R.id.lsq_next);
		mCutTextView.setText(TuSdkContext.getString("lsq_cut"));
		mCutTextView.setOnClickListener(mClickListener);
		
		mPlayTextView  = (TextView) this.findViewById(R.id.lsq_play_time);
		mLeftTextView  = (TextView) this.findViewById(R.id.lsq_left_time);
		mRightTextView  = (TextView) this.findViewById(R.id.lsq_right_time);

		mPlayTextView.setText(R.string.lsq_text_time_tv);
		mLeftTextView.setText(R.string.lsq_text_time_tv);
		mRightTextView.setText(R.string.lsq_text_time_tv);
		
        mPlayButton = (Button) this.findViewById(R.id.lsq_play_btn);
        mPlayButton.setOnClickListener(mClickListener);
        
        mSurfaceView = (SurfaceView) this.findViewById(R.id.lsq_video_view);
        mSurfaceView.setOnClickListener(mClickListener);
      
        mRangeSelectionBar = (MovieRangeSelectionBar) this.findViewById(R.id.lsq_seekbar);
        mRangeSelectionBar.setShowPlayCursor(false);
        mRangeSelectionBar.setLeftSelection(0);
        mRangeSelectionBar.setPlaySelection(0);
        mRangeSelectionBar.setRightSelection(100);
        mRangeSelectionBar.setOnCursorChangeListener(mOnCursorChangeListener);
        
        TextView titleTextView = (TextView) findViewById(R.id.lsq_title);
        titleTextView.setText(getResources().getString(R.string.lsq_movie_cut_text));

		mVideoFormatBar = findViewById(R.id.lsq_video_format);
		mVideoFormatBar.setVisibility(View.GONE);
		fpsGroup = findViewById(R.id.lsq_fps_group);
		fpsGroup.setOnCheckedChangeListener(checkedChangeListener);
		RadioButton fpsNormal = findViewById(R.id.lsq_fps_normal);
		fpsNormal.setChecked(true);
		bitrateGroup = findViewById(R.id.lsq_bitrate_group);
		bitrateGroup.setOnCheckedChangeListener(checkedChangeListener);
		RadioButton bitNormal = findViewById(R.id.lsq_bit_normal);
		bitNormal.setChecked(true);

		mCircleView = findViewById(R.id.circleView);
		mCircleView.setTextSize(50);
		mCircleView.setAutoTextSize(true);
		mCircleView.setTextColor(Color.WHITE);
		mCircleView.setVisibility(View.GONE);

		mCutRect = findViewById(R.id.lsq_cut_rect);
		mCutRect.setVisibility(View.GONE);


        // 加载视频缩略图 
        loadVideoThumbList();
        showPlayButton();
        isFirstLoadVideo = false;
        mSurfaceView.getHolder().addCallback(mCallback);
        // 裁剪后视频时长
        mCuTimeRange = new TuSDKTimeRange();
	}

	private int mFps = 0;
	private int mBitrate = 0;

	/**
	 * 视频格式单选框
	 */
	private RadioGroup.OnCheckedChangeListener checkedChangeListener = new RadioGroup.OnCheckedChangeListener()
	{

		@Override
		public void onCheckedChanged(RadioGroup group, int checkedId)
		{
			switch (checkedId)
			{
				case R.id.lsq_fps_normal:
					mFps = 0;
					break;
				case R.id.lsq_fps_10:
					mFps = 10;
					break;
				case R.id.lsq_fps_20:
					mFps = 20;
					break;
				case R.id.lsq_fps_30:
					mFps = 30;
					break;
				case R.id.lsq_bit_normal:
					mBitrate = 0;
					break;
				case R.id.lsq_bit_1000:
					mBitrate = 1000*1000;
					break;
				case R.id.lsq_bit_2000:
					mBitrate = 2*1000*1000;
					break;
				case R.id.lsq_bit_3000:
					mBitrate = 3*1000*1000;
					break;
			}
		}
	};
	
	/** 加载视频缩略图 */
	public void loadVideoThumbList()
	{
		if(mRangeSelectionBar != null && mRangeSelectionBar.getVideoThumbList() == null)
		{
			TuSdkSize tuSdkSize = TuSdkSize.create(TuSdkContext.dip2px(56),
					TuSdkContext.dip2px(56));
			TuSDKVideoImageExtractor extractor = TuSDKVideoImageExtractor.createExtractor();
			
			extractor.setOutputImageSize(tuSdkSize)
					 .setVideoDataSource(TuSDKMediaDataSource.create(mVideoPathUri))
					 .setExtractFrameCount(6);
			
			extractor.asyncExtractImageList(new TuSDKVideoImageExtractorDelegate()
			{
				@Override   
				public void onVideoImageListDidLoaded(List<Bitmap> images)
				{

				}
				
				@Override
				public void onVideoNewImageLoaded(Bitmap bitmap){
					mRangeSelectionBar.drawVideoThumb(bitmap);
				}
				
			});	
		}
	}
	
	/** 初始化播放器 */
    public void initMediaPlayer(SurfaceHolder holder)
    {
    	isInit = false;
        mMediaPlayer = new MediaPlayer();
        mMediaPlayer.reset();
        mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        
        // 将视频画面输出到SurfaceView 
        mMediaPlayer.setDisplay(holder);
        
        // 设置需要播放的视频 
        try
        {
        	setDataSource(mInputPath);
            mMediaPlayer.prepareAsync();  

        } catch (Exception e){e.printStackTrace();}
        
        mMediaPlayer.setOnPreparedListener(mOnPreparedListener);
        mMediaPlayer.setOnSeekCompleteListener(mOnSeekCompleteListener);
        mMediaPlayer.setOnVideoSizeChangedListener(mOnVideoSizeChangedListener);
        mMediaPlayer.setOnCompletionListener(mOnCompletionListener);

    }

    private void setDataSource(String mInputPath)
    {
    	if (mMediaPlayer == null) mMediaPlayer = new MediaPlayer();
		try {
			mMediaPlayer.setDataSource(mInputPath);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/** 销毁播放器 */
    public void destoryMediaPlayer()
    {
    	if(mMediaPlayer!=null)
    	{
    		mMediaPlayer.release();
    		mMediaPlayer = null;
    	}
    }

	/** 
	 * 准备播放视频
	 * 由于部分小米手机同步加载视频不能正常播放，视频播放
	 * 方式选用异步方式加载(即使用prepareAsync()方式加载,
	 * 在onPrepared()方法中开始播放)
	 */
    public void preparePlay()
	{
        if(!isInit)
        {
            TuSdk.messageHub().showToast(this, R.string.lsq_video_read_prepare);
            return;
        }
        if(mMediaPlayer==null)
        {
            TuSdk.messageHub().showToast(this, R.string.lsq_video_empty_error);
            return;
        }
      
        // 重置播放器设置 
        mMediaPlayer.reset();
        mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        try
        {   
            // 设置播放资源路径，准备播放 
        	setDataSource(mInputPath);
            // 设置异步播放
            mMediaPlayer.prepareAsync();
        } catch (Exception e)
        {
            TuSdk.messageHub().showToast(this, R.string.lsq_video_read_prepare);
            e.printStackTrace();
        }
    }
    
	/** 
	 * 播放视频
	 * 由于部分小米手机同步加载视频不能正常播放，视频播放
	 * 方式选用异步方式加载(即使用prepareAsync()方式加载,
	 * 在onPrepared()方法中开始播放)
	 */
	public void playVideo()
	{
		if(!isInit){
			TuSdk.messageHub().showToast(this, R.string.lsq_video_read_prepare);
			return;
		}
        if(mMediaPlayer==null)
        {
        	TuSdk.messageHub().showToast(this, R.string.lsq_video_empty_error);
        	return;
        }
        isPlay = true;
        if(mMediaPlayer.isPlaying())
        {
            mMediaPlayer.stop();        	
        }

        // 在指定位置进行播放 
        mMediaPlayer.seekTo((int)mStart_time);
        mMediaPlayer.start();
        
		/** 启动计时器,用于获取加载进度  */
        ThreadHelper.runThread(new Runnable() {
			
			@Override
			public void run() {
				ThreadHelper.post(runnable);
			}
		});
        
        // 点击播放后隐藏播放按钮
        hidePlayButton();
	}
	
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
    	if (requestCode != REQUEST_CODE) return;
    	
    	switch (resultCode)
		{
		case RESULT_OK:
			// 关闭界面
			this.finish();
			break;

		default:
			break;
		}
    }
	
	/** 暂停播放 */
	public void pauseVideo()
	{
		if(mMediaPlayer!=null)
		{
			mMediaPlayer.pause();
			showPlayButton();
			isPlay = false;
		}
	}
	
	/** 停止播放 */
	public void stopVideo()
	{
		if(mMediaPlayer!=null&&isPlay)
		{
			mMediaPlayer.stop();
			showPlayButton();
        	hidePlayCursor();
			isPlay = false;
		}		
	}
	
	/** 显示播放按钮  */
	public void showPlayButton()
	{
		if(mPlayButton!=null)
		{   
			mPlayButton.setVisibility(View.VISIBLE);
			mPlayButton.setBackgroundResource(R.drawable.lsq_style_default_crop_btn_record);
		}		
	}
	
	/** 隐藏播放按钮  */
	public void hidePlayButton()
	{
		if(mPlayButton!=null)
		{ 
			mPlayButton.setVisibility(View.VISIBLE);
			mPlayButton.setBackgroundColor(Color.TRANSPARENT);
		}		
	}
	
	/**隐藏裁剪控件播放指针  */
	public void hidePlayCursor()
	{
		if(mRangeSelectionBar!=null)
		{
			mRangeSelectionBar.setPlaySelection(-1);
			mRangeSelectionBar.setShowPlayCursor(false);
		}
	}
	
	/** 点击下一步按钮  */
	private void handleCutButton()
	{
		// 自定义裁剪
		startMovieClipper();
	}


	/**
	 * 自定义裁剪
	 */
	private void startMovieClipper()
	{
		if(!isInputRight())
		{
			Toast.makeText(MovieCutActivity.this,"输入有误",Toast.LENGTH_SHORT).show();
			return;
		}

		TuSDKVideoInfo videoInfo = TuSDKMediaUtils.getVideoInfo(mInputPath);

		MediaFormat ouputVideoFormat = getOutputVideoFormat(videoInfo);
		MediaFormat ouputAudioFormat = getOutputAudioFormat();

		TuSdkMediaTimeSlice timeSlice = new TuSdkMediaTimeSlice(mCuTimeRange.getStartTimeUS(),mCuTimeRange.getEndTimeUS());

		RectF rectDrawF = new RectF(inputFormat(0),inputFormat(1),inputFormat(2),inputFormat(3));
		RectF rectCutF = new RectF(inputFormat(4),inputFormat(5),inputFormat(6),inputFormat(7));

		TuSdkMediaSuit.cuter(new TuSdkMediaDataSource(mInputPath), getOutPutFilePath(), ouputVideoFormat, ouputAudioFormat, ImageOrientation.Up,
				rectDrawF, rectCutF, timeSlice,mCuterMediaProgress );
		mCircleView.setVisibility(View.VISIBLE);
	}

	private boolean isInputRight()
	{
		for (int i = 0; i < editTexts.size(); i++)
		{
			if(!editTexts.get(i).getText().toString().equals(""))
			{
				float inputF = Float.valueOf(editTexts.get(i).getText().toString());
				if(inputF < 0 || inputF >1) return false;
			}
		}
		return true;
	}

	private float inputFormat(int position)
	{
		if(editTexts.get(position).getText().toString().equals(""))
		{
			return 0;
		}
		else
		{
			return Float.valueOf(editTexts.get(position).getText().toString());
		}
	}

	/**
	 * 获取输出视频大小
	 * @return 输出视频大小
	 */
	private TuSdkSize getOutputSize()
	{

		int width = 0;
		int height = 0;

		if(outputWidth.getText().toString().equals(""))
		{
			width = 0;
		}

		if(outputHeight.getText().toString().equals(""))
		{
			height = 0;
		}

		width = Integer.valueOf(outputWidth.getText().toString());
		height = Integer.valueOf(outputHeight.getText().toString());

		return TuSdkSize.create(width,height);
	}

	/**
	 * 获取输出文件的视频格式信息
	 * @param videoInfo 当前的音频信息
	 * @return MediaFormat
	 */
	protected MediaFormat getOutputVideoFormat(TuSDKVideoInfo videoInfo)
	{
		int fps = mFps==0 ? videoInfo.fps : mFps;
		int bitrate = mBitrate==0 ? videoInfo.bitrate : mBitrate;

		MediaFormat mediaFormat = TuSdkMediaFormat.buildSafeVideoEncodecFormat(getOutputSize().width, getOutputSize().height,
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

	private String getOutPutFilePath()
	{
		return new File(AlbumHelper.getAblumPath(),
				String.format("lsq_cut_%s.mp4", StringHelper.timeStampString())).toString();
	}
	
    @Override
    protected void onResume()
    {
        super.onResume();
        if(isPause)
        {
        	preparePlay();
        	isPause = false;
        }
        loadVideoThumbList();
    }

    @Override
    protected void onPause() 
    {
        super.onPause();
        if(!isPause && isPlay)
        {
        	pauseVideo();
        	isPause = true;
        }
    }

    @Override
    protected void onDestroy()
    {
    	super.onDestroy();
    }
    
    @Override
	public void onBackPressed()
	{
		finish();
	}

	private OnClickListener mClickListener = new OnClickListener()
	{
		@Override
		public void onClick(View v)
		{
			switch (v.getId())
			{
			case R.id.lsq_back:
				// 返回  
				onBackPressed();
				break;
			case R.id.lsq_play_btn:
			case R.id.lsq_video_view:
				if(!isPlay)
				{
					// 准备播放
					preparePlay();
				}
				else
				{
					// 暂停
					pauseVideo();					
				}

				break;
			case R.id.lsq_next:
				// 下一步
				handleCutButton();
				break;

			default:
				break;
			}
		}
	};
	
	public void seekToStart()
	{
		isMoveStartTime = true;
		mStart_time =(mStart_time>1)?mStart_time:1;
    	mMediaPlayer.seekTo((int) mStart_time);
    	mMediaPlayer.start();
	}
	
	private SurfaceHolder.Callback mCallback = new SurfaceHolder.Callback()
	{

        @Override
        public void surfaceCreated(SurfaceHolder holder)
        {
        	 /** 初始化播放器  */
        	 initMediaPlayer(holder);
        }

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height)
        {
        	
        }
        @Override
        public void surfaceDestroyed(SurfaceHolder holder)
        {
        	destoryMediaPlayer();

    		if (mRangeSelectionBar != null)
    			mRangeSelectionBar.clearVideoThumbList();
        }
	};
	
	private OnPreparedListener mOnPreparedListener = new OnPreparedListener()
    {
        @Override
        public void onPrepared(MediaPlayer mp)
        {

			if(!isFirstLoadVideo){
				isFirstLoadVideo = true;
                // 获取视频总时长
                mVideoTotalTime  = mMediaPlayer.getDuration();
                mEnd_time  = mVideoTotalTime;
                mCuTimeRange.setStartTime(0.0f);
                mCuTimeRange.setEndTime(mVideoTotalTime);
                setBarSpace();
                updatePlayTime();
            	updateRightTime();
            	seekToStart();
            	isInit = true;
                return;
        	}
			if(!isInit){
				seekToStart();
	        	isInit = true;
	        	return;
			}

			playVideo();
        }
    };
    
    private OnSeekCompleteListener mOnSeekCompleteListener= new OnSeekCompleteListener()
    {
          @Override
          public void onSeekComplete(MediaPlayer mp)
          {
              if(isMoveLeft||isMoveRight||isMoveStartTime)
              {
                  mp.pause();
                  isMoveLeft = false;
                  isMoveRight = false;
                  isMoveStartTime = false;
                  // 显示播放按钮
                  showPlayButton();
              }  
          }
    };
	
    private OnVideoSizeChangedListener mOnVideoSizeChangedListener = new OnVideoSizeChangedListener()
    {
        
        @Override
        public void onVideoSizeChanged(MediaPlayer mp, int width, int height)
        {
        	// 将视频进行等比例显示处理
        	setVideoSize(mSurfaceView, width, height);
        }
   };
   
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
   
   private OnCompletionListener mOnCompletionListener =  new OnCompletionListener()
   {
        @Override
        public void onCompletion(MediaPlayer mp)
        {
        	pauseVideo();
        }
    };
    
    /** 用于监听裁剪控件  */
	private OnCursorChangeListener mOnCursorChangeListener = new OnCursorChangeListener()
	{
		
		@Override
		public void onSeeekBarChanged(int width, int height)
		{
			seekBarWidth = width;
			setBarSpace();
		}

		@Override
		public void onLeftCursorChanged(int percent)
		{
			isPlay = false;
			isMoveLeft = true;
			if (mMediaPlayer != null)
			{
				mCuTimeRange.setStartTime( percent*mVideoTotalTime/(100*1000));
				mMediaPlayer.seekTo((int) mCuTimeRange.getStartTime()*1000);
				mMediaPlayer.start();
				mStart_time = (int) mCuTimeRange.getStartTime()*1000;
				updateLeftTime();
				updatePlayTime();
			}
			
			hidePlayCursor();
		}

		@Override
		public void onPlayCursorChanged(int percent)
		{

		}

		@Override
		public void onRightCursorChanged(int percent)
		{
			isPlay = false;
			isMoveRight = true;
			if (mMediaPlayer != null)
			{
				mCuTimeRange.setEndTime( percent*mVideoTotalTime/(100*1000));
				mMediaPlayer.seekTo((int) mCuTimeRange.getEndTime()*1000);
				mMediaPlayer.start();
				mEnd_time = (int) mCuTimeRange.getEndTime()*1000;
				updateRightTime();
				updatePlayTime();
			}
			hidePlayCursor();
		}

		@Override
		public void onLeftCursorUp() 
		{	
		}

		@Override
		public void onRightCursorUp() 
		{
		}
	};
	
	/** 设置裁剪控件开始与结束的最小间隔距离 */
	public void setBarSpace()
	{ 
		if(mVideoTotalTime == 0 ) return;
		if(mRangeSelectionBar!=null)
		{
			/** 
			 * 需求需要，需设定最小间隔为1秒的
			 * 间隔距离，单位秒要转化为毫秒；
			 */
			double percent = (1000/mVideoTotalTime);
			seekBarWidth = seekBarWidth==0?640:seekBarWidth;
			int space = (int) (percent*seekBarWidth);
			mRangeSelectionBar.setCursorSpace(space);
		}
	}
	
	public void updateLeftTime()
	{
		if(mLeftTextView!=null)
		{
			StringBuffer sb=new StringBuffer();
			int s_time = (int) (mStart_time/1000);
			
			int temp=(s_time%3600/60);
			sb.append((temp<10)?("0"+temp+":"):(temp+":"));
			
			temp=(int) (s_time%3600%60);
			sb.append((temp<10)?("0"+temp+""):(temp+""));
			
			mLeftTextView.setText(sb.toString());
			mLeftTextView.invalidate();			
		}
	}
	
	public void updatePlayTime()
	{
		if(mPlayTextView!=null)
		{
			
			int time = (int) (mEnd_time - mStart_time);
			
			StringBuffer sb=new StringBuffer();
			int s_time = (int) (time/1000);
			
			int temp=(s_time%3600/60);
			sb.append((temp<10)?("0"+temp+":"):(temp+":"));
			
			temp=(int) (s_time%3600%60);
			sb.append((temp<10)?("0"+temp):(temp+""));
			
			mPlayTextView.setText(sb.toString());
			mPlayTextView.invalidate();
		}

	}
	
	public void updateRightTime()
	{
		if(mRightTextView!=null)
		{
			StringBuffer sb=new StringBuffer();
			double s_time = mEnd_time/1000;
			
			int temp=(int) (s_time%3600/60);
			sb.append((temp<10)?("0"+temp+":"):(temp+":"));
			
			temp=(int) (s_time%3600%60);
			sb.append((temp<10)?("0"+temp+""):(temp+""));
			
			mRightTextView.setText(sb.toString());
			mRightTextView.invalidate();	
		}
	}

    /** 用于监听播放进度  */
    Runnable runnable=new Runnable()
    {
  	    @Override
  	    public void run()
  	    {
			if(mMediaPlayer!=null&&mMediaPlayer.isPlaying())
			{
				int  time = mMediaPlayer.getCurrentPosition();
				time = (time<mStart_time)?(int) mStart_time:time;
				if(time>=(int)mStart_time&&time<mEnd_time)
				{
					/** 用于刷新播放进度条  */
					if(mRangeSelectionBar!=null)
					{
						int percent = (int) (time*100/mVideoTotalTime)+1;
						mRangeSelectionBar.setPlaySelection(percent);
						if(!mRangeSelectionBar.isShowPlayCursor()){
							mRangeSelectionBar.setShowPlayCursor(true);
						}
					}
				}
				else 
				{
					showPlayButton();
					hidePlayCursor();
					/** 暂停播放  */
					pauseVideo();
					/** 移除循环回调  */
					ThreadHelper.cancel(this);
				}
				/** 设置循环延时  */
				ThreadHelper.post(this);
  	         }
  	     }
    };

	/**
	 * 设置剪裁区域
	 * @param view
	 */
	public void showCutRect(View view)
	{
		mVideoFormatBar.setVisibility(View.GONE);
		mCutRect.setVisibility(mCutRect.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);
	}

	/**
	 * 设置视频格式
	 * @param view
	 */
	public void showVideoFormat(View view)
	{
		mCutRect.setVisibility(View.GONE);
		mVideoFormatBar.setVisibility(mVideoFormatBar.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);
	}
}
