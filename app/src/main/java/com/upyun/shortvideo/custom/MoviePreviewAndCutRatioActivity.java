/**
 * TuSDKVideoDemo
 * MoviePreviewAndCutRatioActivity.java
 *
 * @author  Yanlin
 * @Date  Feb 21, 2017 8:52:11 PM
 * @Copright (c) 2016 tusdk.com. All rights reserved.
 *
 */
package com.upyun.shortvideo.custom;
import java.util.ArrayList;
import java.util.List;

import org.lasque.tusdk.core.TuSdk;
import org.lasque.tusdk.core.TuSdkContext;
import org.lasque.tusdk.core.struct.TuSdkSize;
import org.lasque.tusdk.core.utils.RectHelper;
import org.lasque.tusdk.core.utils.StringHelper;
import org.lasque.tusdk.core.utils.ThreadHelper;
import org.lasque.tusdk.video.editor.TuSDKVideoImageExtractor;
import org.lasque.tusdk.video.editor.TuSDKVideoImageExtractor.TuSDKVideoImageExtractorDelegate;
import org.lasque.tusdk.video.mixer.TuSDKMediaDataSource;
import com.upyun.shortvideo.R;
import com.upyun.shortvideo.SimpleCameraActivity;
import com.upyun.shortvideo.component.MovieEditorActivity;
import com.upyun.shortvideo.views.MovieRangeSelectionBar;
import com.upyun.shortvideo.views.MovieRangeSelectionBar.OnCursorChangeListener;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Rect;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.media.MediaPlayer.OnSeekCompleteListener;
import android.media.MediaPlayer.OnVideoSizeChangedListener;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * 视频预览 + 获取视频裁剪范围
 * 
 * @author leone.xia
 * @param <T>
 */
public class MoviePreviewAndCutRatioActivity extends SimpleCameraActivity  
{
	/** requestCode */
	private static final int REQUEST_CODE  = 0;
	/** 播放  Button */
	private Button mPlayButton;
	/** 返回  TextView */
	private TextView mBackTextView;
	/** 下一步  TextView */
	private TextView mNextTextView;
	/** PLAY TIME TextView */
	private TextView mPlayTextView;
	/** LEFT TIME TextView */
	private TextView mLeftTextView;
	/** RIGHT TIME TextView */
	private TextView mRightTextView;
	/** 视频裁剪控件  */
	private MovieRangeSelectionBar mRangeSelectionBar;
	/** 记录裁剪控件的宽度  */
	private int seekBarWidth;
	/** 用于显示视频  */
	private SurfaceView mSurfaceView;
	/** MediaPlayer 播放器  */
    private MediaPlayer mMediaPlayer;
    /** 视频播放地址  */
    private String mVideoPath ;
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
	/** 记录缩略图列表容器  */
	private List<Bitmap> list;
	
	/** 跳转的类  */
	private String mIntentClassName;
	
    
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.movie_range_selection_activity);
		initView();
		setIntentClass(MovieEditorActivity.class.getName());
	}
	
	/**
	 * 初始化视图
	 */
	protected void initView()
	{
		mVideoPath = getIntent().getStringExtra("videoPath");

		mBackTextView = (TextView) this.findViewById(R.id.lsq_back);
		mBackTextView.setOnClickListener(mClickListener);
		
		mNextTextView = (TextView) this.findViewById(R.id.lsq_next);
		mNextTextView.setOnClickListener(mClickListener);
		
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
        
        // 加载视频缩略图 
        loadVideoThumbList();
        showPlayButton();
        isFirstLoadVideo = false;
        mSurfaceView.getHolder().addCallback(mCallback);
	}
	
	/** 加载视频缩略图 */
	public void loadVideoThumbList()
	{
		if(mRangeSelectionBar != null &&
				mRangeSelectionBar.getList() == null){
			TuSdkSize tuSdkSize = TuSdkSize.create(TuSdkContext.dip2px(56),
					TuSdkContext.dip2px(56));
			TuSDKVideoImageExtractor extractor = TuSDKVideoImageExtractor.createExtractor();
			
			extractor.setOutputImageSize(tuSdkSize)
					 .setVideoDataSource(TuSDKMediaDataSource.create(mVideoPath))
					 .setExtractFrameCount(6);
			
			list = new ArrayList<Bitmap>();
			mRangeSelectionBar.setList(list);
			extractor.asyncExtractImageList(new TuSDKVideoImageExtractorDelegate() 
			{
				@Override   
				public void onVideoImageListDidLoaded(List<Bitmap> images) {
					list = images;
					mRangeSelectionBar.invalidate();
				}
				
				@Override
				public void onVideoNewImageLoaded(Bitmap bitmap){
					list.add(bitmap);
					mRangeSelectionBar.invalidate();
				}
				
			});	
		}
	}
	
	/** 初始化播放器 */
    public void initMediaPlayer(SurfaceHolder holder)
    {
		if(StringHelper.isEmpty(mVideoPath))
		{
        	TuSdk.messageHub().showToast(this, R.string.lsq_video_empty_error);
        	return;
        }

        isInit = false;
        mMediaPlayer = new MediaPlayer();
        mMediaPlayer.reset();
        mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        
        // 将视频画面输出到SurfaceView 
        mMediaPlayer.setDisplay(holder);
        
        // 设置需要播放的视频 
        try
        {
            mMediaPlayer.setDataSource(mVideoPath);  
            mMediaPlayer.prepareAsync();  

        } catch (Exception e){e.printStackTrace();}
        
        mMediaPlayer.setOnPreparedListener(mOnPreparedListener);
        mMediaPlayer.setOnSeekCompleteListener(mOnSeekCompleteListener);
        mMediaPlayer.setOnVideoSizeChangedListener(mOnVideoSizeChangedListener);
        mMediaPlayer.setOnCompletionListener(mOnCompletionListener);

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
    public void preparePlay(){
        if(!isInit){
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
            mMediaPlayer.setDataSource(mVideoPath);
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
	private void handleClickNextButton()
	{
		if(!isInit){
			TuSdk.messageHub().showToast(this, R.string.lsq_video_read_prepare);
			return;
		}
		pauseVideo();
		Intent intent = null;
		try 
		{
			intent = new Intent(this, Class.forName(mIntentClassName));
		} 
		catch (ClassNotFoundException e)
		{
			e.printStackTrace();
		}
		intent.putExtra("startTime", mStart_time);
		intent.putExtra("endTime", mEnd_time);
		intent.putExtra("videoPath", mVideoPath);
		startActivityForResult(intent, REQUEST_CODE);
	}

	public  void setIntentClass(String className)
	{
		this.mIntentClassName = className;
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
				handleClickNextButton();
				break;

			default:
				break;
			}
		}
	};
	
	public void seekToStart(){
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
    		if(mRangeSelectionBar!=null){
    			mRangeSelectionBar.destroyBitmap();
    		}
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
				int videoCurTime = percent*mVideoTotalTime/100;
				mMediaPlayer.seekTo(videoCurTime);
				mMediaPlayer.start();
				mStart_time = videoCurTime;
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
				int videoCurTime = percent*mVideoTotalTime/100;
				mMediaPlayer.seekTo(videoCurTime);
				mMediaPlayer.start();
				mEnd_time = videoCurTime;
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
}