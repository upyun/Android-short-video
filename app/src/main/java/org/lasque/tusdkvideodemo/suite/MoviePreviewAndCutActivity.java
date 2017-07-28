/**
 * TuSDKVideoDemo
 * MoviePreviewAndCutActivity.java
 *
 * @author  Yanlin
 * @Date  Feb 21, 2017 8:52:11 PM
 * @Copright (c) 2016 tusdk.com. All rights reserved.
 *
 */
package org.lasque.tusdkvideodemo.suite;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.lasque.tusdk.core.TuSdkContext;
import org.lasque.tusdk.core.struct.TuSdkSize;
import org.lasque.tusdk.core.utils.RectHelper;
import org.lasque.tusdk.core.view.TuSdkViewHelper;
import org.lasque.tusdk.movie.player.TuSDKMoviePlayer;
import org.lasque.tusdk.movie.player.TuSDKMoviePlayer.OnSeekToPreviewListener;
import org.lasque.tusdk.movie.player.TuSDKMoviePlayer.PlayerState;
import org.lasque.tusdk.movie.player.TuSDKMoviePlayer.TuSDKMoviePlayerDelegate;
import org.lasque.tusdk.video.editor.TuSDKVideoImageExtractor;
import org.lasque.tusdk.video.editor.TuSDKVideoImageExtractor.TuSDKVideoImageExtractorDelegate;
import org.lasque.tusdk.video.mixer.TuSDKMediaDataSource;
import org.lasque.tusdkvideodemo.R;
import org.lasque.tusdkvideodemo.SimpleCameraActivity;
import org.lasque.tusdkvideodemo.component.MovieEditorActivity;
import org.lasque.tusdkvideodemo.views.HVScrollView;
import org.lasque.tusdkvideodemo.views.HVScrollView.OnScrollChangeListener;
import org.lasque.tusdkvideodemo.views.MovieRangeSelectionBar;
import org.lasque.tusdkvideodemo.views.MovieRangeSelectionBar.OnCursorChangeListener;
import org.lasque.tusdkvideodemo.views.MovieRangeSelectionBar.Type;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.RectF;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * 视频预览 + 获取视频裁剪范围
 * 
 * @author leone.xia
 */
public class MoviePreviewAndCutActivity extends SimpleCameraActivity  
{
	/** requestCode */
	private static final int REQUEST_CODE  = 0;
	/** 非比例自适应 */
	private final boolean RATIO_ADAPTION = false;
	/** 播放  Button */
	private Button mPlayButton;
	/** 返回  TextView */
	private TextView mBackTextView;
	/** 标题  TextView */
	private TextView mTitleTextView;
	/** 下一步  TextView */
	private TextView mNextTextView;
	/** PLAY TIME TextView */
	private TextView mPlayTextView;
	/** LEFT TIME TextView */
	private TextView mLeftTextView;
	/** RIGHT TIME TextView */
	private TextView mRightTextView;
	/** 视频裁剪控件 */
	private MovieRangeSelectionBar mRangeSelectionBar;
	/** 记录裁剪控件的宽度  */
	private int mSeekBarWidth;
    /** 视频显示样式 */
    private int mShowStyle;
    /** 视频横屏样式 */
    private final int SHOW_HORIZONETAL = 1;
    /** 视频竖屏样式 */
    private final int SHOW_VERTICAL = 2;
    /** 选取视频的左端位置,范围0.0f-1.0f */
    private static float mCutRectLeftPercent;
    /** 选取视频的顶端位置,范围0.0f-1.0f */
    private static float mCutRectTopPercent;
    /** 选取视频的右端位置,范围0.0f-1.0f */
    private static float mCutRectRightPercent;
    /** 选取视频的底端位置,范围0.0f-1.0f */
    private static float mCutRectBottomPercent;
    /** 记录滚动条X坐标 */
    private static int mScrollViewContentOffseX;
    /** 记录滚动条Y坐标 */
    private static int mScrollViewContentOffseY;
	/** 滚动控件 */
	private HVScrollView mHVScrollView;
	/** 用于显示视频  */
	private SurfaceView mSurfaceView;
	/** 视频播放模块布局 */
	private FrameLayout mMovieLayout;
	/** 选择时间范围模块布局 */
	private LinearLayout mSelectTimeLayout;
	/** TuSDKMoviePlayer 播放器  */
	private TuSDKMoviePlayer mPlayer;
    /** 视频播放地址  */
    private String mVideoPath ;
    /** 记录时间时长 */
    private int mVideoTime;
    /** 裁剪视频的开始时间 */
    private static int mStartTime;
    /** 裁剪视频的结束时间 */
    private static int mEndTime;
    /** 是否播放视频 */
    private boolean mIsPlaying;
    /** 视频是否播放完成 */
	private boolean mIsVideoFinished = false;
	/** 是否暂停视频 */
	private boolean mIsPaused;
	/** 记录缩略图列表容器 */
	private List<Bitmap> mThumbList;
	/** 记录屏幕宽度 */
	private int mScreenWidth;
	/** 记录屏幕高度 */
	private int mScreenHeight;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.movie_range_selection_activity);
		initView();
	}

	/** 初始化视图 */
	protected void initView()
	{
		mVideoPath = getIntent().getStringExtra("videoPath");

		mBackTextView = (TextView) this.findViewById(R.id.lsq_back);
		mBackTextView.setOnClickListener(mOnClickListener);
		
		mTitleTextView = (TextView) this.findViewById(R.id.lsq_title);
		mTitleTextView.setText(R.string.lsq_clip);
		
		mNextTextView = (TextView) this.findViewById(R.id.lsq_next);
		mNextTextView.setOnClickListener(mOnClickListener);
		
		mPlayTextView  = (TextView) this.findViewById(R.id.lsq_play_time);
		mLeftTextView  = (TextView) this.findViewById(R.id.lsq_left_time);
		mRightTextView  = (TextView) this.findViewById(R.id.lsq_right_time);
		
		mPlayTextView.setText(R.string.lsq_text_time_tv);
		mLeftTextView.setText(R.string.lsq_text_time_tv);
		mRightTextView.setText(R.string.lsq_text_time_tv);
		
        mPlayButton = (Button) this.findViewById(R.id.lsq_play_btn);
        mPlayButton.setOnClickListener(mOnClickListener);
        
        mHVScrollView = (HVScrollView) this.findViewById(R.id.hvScrollView);
        
        mSurfaceView = (SurfaceView) this.findViewById(R.id.lsq_video_view);
        mSurfaceView.setOnClickListener(mOnClickListener);
        
        mMovieLayout = (FrameLayout) this.findViewById(R.id.movie_layout);
        mSelectTimeLayout = (LinearLayout) this.findViewById(R.id.time_layout);
        
        showPlayButton();
        
        initMoviePlayer();
        initRangeSelectionBar();
	}
	
	private void initMoviePlayer()
	{
        mPlayer = TuSDKMoviePlayer.createMoviePlayer();
        mPlayer.setLooping(false);
        
        Uri uri = mVideoPath == null ? null : Uri.fromFile(new File(mVideoPath));
        mPlayer.initVideoPlayer(this, uri, mSurfaceView);
        mPlayer.setDelegate(mMoviePlayerDelegate);
	}
	
	private void initRangeSelectionBar()
	{
        mRangeSelectionBar = (MovieRangeSelectionBar) this.findViewById(R.id.lsq_seekbar);
        mRangeSelectionBar.setShowPlayCursor(false);
        mRangeSelectionBar.setType(Type.Clip);
        mRangeSelectionBar.setLeftSelection(0);
        mRangeSelectionBar.setPlaySelection(0);
        mRangeSelectionBar.setRightSelection(100);
        mRangeSelectionBar.setOnCursorChangeListener(mOnCursorChangeListener);
        
        setBarSpace();
	}
	
	/** 加载视频缩略图 */
	public void loadVideoThumbList()
	{
		if (mRangeSelectionBar != null && mRangeSelectionBar.getList() == null)
		{
			TuSdkSize tuSdkSize = TuSdkSize.create(TuSdkContext.dip2px(56),TuSdkContext.dip2px(56));
			
			TuSDKVideoImageExtractor extractor = TuSDKVideoImageExtractor.createExtractor();
			
			extractor.setOutputImageSize(tuSdkSize)
					.setVideoDataSource(TuSDKMediaDataSource.create(mVideoPath))
					.setExtractFrameCount(6);
			
			
			mThumbList = new ArrayList<Bitmap>();
			mRangeSelectionBar.setList(mThumbList);
			extractor.asyncExtractImageList(new TuSDKVideoImageExtractorDelegate() 
			{
				@Override   
				public void onVideoImageListDidLoaded(List<Bitmap> images) {
					mThumbList = images;
					mRangeSelectionBar.invalidate();
				}
				
				@Override
				public void onVideoNewImageLoaded(Bitmap bitmap){
					mThumbList.add(bitmap);
					mRangeSelectionBar.invalidate();
				}
			});	
		}
	}

	/** 开始播放 */
	public void startPlayer()
	{
		if (mPlayer == null) return;
		
		mPlayer.start();
		hidePlayButton();
		mIsPlaying = true;
		mIsVideoFinished = false;
	}
	
	/** 暂停播放 */
	public void pausePlayer()
	{
		if (mPlayer == null) return;
		
		mPlayer.pause();
		showPlayButton();
		mIsPlaying = false;
	}
	
	/** 停止播放 */
	public void stopPlayer()
	{
		if (mPlayer == null) return;
		
		mPlayer.stop();
		showPlayButton();
		mIsPlaying = false;
	}
	
	/** 显示播放按钮  */
	public void showPlayButton()
	{
		if (mPlayButton != null)
		{   
			mPlayButton.setVisibility(View.VISIBLE);
			mPlayButton.setBackgroundResource(R.drawable.lsq_style_default_crop_btn_record);
		}		
	}
	
	/** 隐藏播放按钮  */
	public void hidePlayButton()
	{
		if (mPlayButton != null)
		{ 
			mPlayButton.setVisibility(View.VISIBLE);
			mPlayButton.setBackgroundColor(Color.TRANSPARENT);
		}		
	}
	
	/**隐藏裁剪控件播放指针  */
	public void hidePlayCursor()
	{
		if (mRangeSelectionBar != null)
		{
			mRangeSelectionBar.setPlaySelection(-1);
			mRangeSelectionBar.setShowPlayCursor(false);
		}
	}
	
	/** 点击控制播放与暂停  */
	private void handleClickSurfaceView()
	{
		if (!mIsPlaying && !mIsVideoFinished)
		{
			startPlayer();
		}
		else if (!mIsPlaying && mIsVideoFinished)
		{
			seekToPreview(mStartTime,new OnSeekToPreviewListener()
			{
				@Override
				public void onSeekToComplete()
				{
					startPlayer();
				}
			});
		}
		else
		{
			pausePlayer();	
		}
	}
	
	/** 点击下一步按钮  */
	private void handleClickNextButton()
	{
		Intent intent = new Intent(this, MovieEditorActivity.class);
		intent.putExtra("startTime", mStartTime);
		intent.putExtra("endTime", mEndTime);
		intent.putExtra("videoPath", mVideoPath);
		intent.putExtra("movieLeft", mCutRectLeftPercent);
		intent.putExtra("movieTop", mCutRectTopPercent);
		intent.putExtra("movieRight", mCutRectRightPercent);
		intent.putExtra("movieBottom", mCutRectBottomPercent);
		intent.putExtra("ratioAdaption", RATIO_ADAPTION);
		startActivityForResult(intent, REQUEST_CODE);
	}

    @Override
    protected void onResume()
    {
        super.onResume();
        loadVideoThumbList();
        if (mIsPaused)
        {
            startPlayer();
        	mIsPaused = false;
        }
    }

    @Override
    protected void onPause() 
    {
        super.onPause();
        if (!mIsPaused && mIsPlaying)
        {
        	pausePlayer();
        	mIsPaused = true;
        }
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
    
    @Override
	public void onBackPressed()
	{
		finish();
	}

	private OnClickListener mOnClickListener = new TuSdkViewHelper.OnSafeClickListener()
	{
		
		@Override
		public void onSafeClick(View v)
		{
			switch (v.getId())
			{
			case R.id.lsq_back:
				onBackPressed();
				break;
			case R.id.lsq_play_btn:
			case R.id.lsq_video_view:
				handleClickSurfaceView();
				break;
			case R.id.lsq_next:
				handleClickNextButton();
				break;

			default:
				break;
			}
		}
	};

    /** 用于监听裁剪控件  */
	private OnCursorChangeListener mOnCursorChangeListener = new OnCursorChangeListener()
	{
		
		@Override
		public void onSeeekBarChanged(int width, int height)
		{
			mSeekBarWidth = width;
			setBarSpace();
		}

		@Override
		public void onLeftCursorChanged(int percent)
		{
			mIsPlaying = false;
			
			mStartTime = percent * mVideoTime / 100;
			seekToPreview(mStartTime,null);
			updateLeftCursorTime();
			updatePlayCursorTime();
			showPlayButton();
			hidePlayCursor();
		}

		@Override
		public void onPlayCursorChanged(int percent)
		{
		}

		@Override
		public void onRightCursorChanged(int percent)
		{
			if (mPlayer == null) return;
			
			mIsPlaying = false;
			mEndTime = percent*mVideoTime/100;
			seekToPreview(mEndTime,null);
			updateRightCursorTime();
			updatePlayCursorTime();
			showPlayButton();
			hidePlayCursor();
		}

		@Override
		public void onLeftCursorUp()
		{
			seekToPreview(mStartTime,null);
		}

		@Override
		public void onRightCursorUp()
		{
			seekToPreview(mStartTime,null);
		}
	};
	
	public void seekToPreview(final int time,final OnSeekToPreviewListener listener)
	{
		if (mPlayer == null) return;
		
		mPlayer.seekToPreview(time,listener);
	}
	
	/** 设置裁剪控件开始与结束的最小间隔距离 */
	public void setBarSpace()
	{ 
		if (mVideoTime == 0 || mRangeSelectionBar == null) return;
		
		double percent = 1000 / (double)Math.max(mVideoTime, 2000);
		mSeekBarWidth = mSeekBarWidth==0?640:mSeekBarWidth;
		int space = (int) (percent*mSeekBarWidth);
		mRangeSelectionBar.setCursorSpace(space);
	}
	
	public void updateTextViewTime(TextView textView,int time)
	{
		if (textView != null)
		{
			StringBuffer sb= new StringBuffer();
			int tmpTime = time/1000;
			
			int temp=(tmpTime%3600/60);
			sb.append((temp<10)?("0"+temp+":"):(temp+":"));
			
			temp=(int) (tmpTime%3600%60);
			sb.append((temp<10)?("0"+temp+""):(temp+""));
			
			textView.setText(sb.toString());
			textView.invalidate();			
		}
	}
	
	public void updateLeftCursorTime()
	{
		updateTextViewTime(mLeftTextView,mStartTime);
	}
	
	public void updatePlayCursorTime()
	{
		updateTextViewTime(mPlayTextView,(mEndTime - mStartTime));
	}
	
	public void updateRightCursorTime()
	{
		updateTextViewTime(mRightTextView,mEndTime);
	}

  	
  	public void setupHVScrollView(final int w,final int h)
  	{
  		// 监听滚动坐标
    	mHVScrollView.setOnScrollChangeListener(new OnScrollChangeListener()
    	{
			
			@Override
			public void onScrollChange(HVScrollView v, int scrollX, int scrollY,
					int oldScrollX, int oldScrollY)
			{
				mScrollViewContentOffseX = (scrollX > w-mScreenWidth) ? (w-mScreenWidth) : scrollX;
				mScrollViewContentOffseY = (scrollY > h-mScreenWidth) ? (h-mScreenWidth) : scrollY;
				mCutRectLeftPercent = mScrollViewContentOffseX / (float)w;
				mCutRectTopPercent = mScrollViewContentOffseY / (float)h;
				mCutRectRightPercent = mCutRectLeftPercent+mScreenWidth / (float)w;
				mCutRectBottomPercent = mCutRectTopPercent+mScreenWidth / (float)h;
			}
		});
    	
        if (w < h)
        {
        	mHVScrollView.setScrollOrientation(HVScrollView.SCROLL_ORIENTATION_VERTICAL);
        	mShowStyle = SHOW_VERTICAL;
        }
        else if (w > h)
        {
        	mHVScrollView.setScrollOrientation(HVScrollView.SCROLL_ORIENTATION_HORIZONTAL);
        	mShowStyle = SHOW_HORIZONETAL;
        }
        else
        {
        	mHVScrollView.setScrollOrientation(HVScrollView.SCROLL_ORIENTATION_NONE);
        }
  	}
  	
  	public void updateMovieLayout(final int titleHeight)
  	{
  		if (mMovieLayout == null) return;
  		
        FrameLayout.LayoutParams time_lp = new FrameLayout
                .LayoutParams(mScreenWidth,mScreenWidth);
        time_lp.setMargins(0, titleHeight, 0, 0);
        mMovieLayout.setLayoutParams(time_lp);	
  	}
  	public void updateSelectTimeLayout(final int selectTimeHeight)
  	{
  		if (mSelectTimeLayout == null) return;
  		
        FrameLayout.LayoutParams time_lp = new FrameLayout
                .LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,selectTimeHeight);
        time_lp.setMargins(0, mScreenHeight -selectTimeHeight, 0, 0);
        mSelectTimeLayout.setLayoutParams(time_lp);		
  	}
  	
  	public void updateHVScrollViewLaytout()
  	{
  		if (mHVScrollView == null) return;
  		
        FrameLayout.LayoutParams hv_lp = new FrameLayout
                .LayoutParams(mScreenWidth,mScreenWidth);
        mHVScrollView.setLayoutParams(hv_lp);	
  	}
  	
  	public void updateHVScrollViewScolling(final RectF rect ,final int w, final int h)
  	{
  		if (mHVScrollView == null) return;
  		
        mHVScrollView.postDelayed(new Runnable()
        {
			@Override
			public void run() 
			{
	
		        if (mShowStyle == SHOW_HORIZONETAL)
		        {   
		        	int x0 = (int) ((rect.right - mScreenWidth)/2);
		        	mHVScrollView.scrollTo(x0,0);
		        	
		            mCutRectLeftPercent = x0 / (float)w;
		            mCutRectTopPercent = 0.0f;;
		            mCutRectRightPercent = mCutRectLeftPercent+mScreenWidth / (float)w;
		            mCutRectBottomPercent = mCutRectTopPercent+mScreenWidth / (float)h;
		        }
		        else if (mShowStyle == SHOW_VERTICAL)
		        {  
		        	int y0 = (int) ((rect.bottom - mScreenWidth)/2);
		        	mHVScrollView.scrollTo(0,y0);
		        	
		            mCutRectLeftPercent = 0.0f;
		            mCutRectTopPercent = y0 / (float)h;
		            mCutRectRightPercent = mCutRectLeftPercent+mScreenWidth / (float)w;
		            mCutRectBottomPercent = mCutRectTopPercent+mScreenWidth / (float)h;
		        }
			}
		},100);
  	}
  	
  	public void updateSurfaceViewLayout(final int w, final int h)
  	{
  		if (mSurfaceView == null) return;
  		
        RelativeLayout.LayoutParams lp = new RelativeLayout
                .LayoutParams(w,h);
        mSurfaceView.setLayoutParams(lp);	
  	}
  	
	/**
	 * 对原始视频进行缩放
	 * @param width
	 * @param height
	 */
    private void scaleVideoViews(int width,int height)
    {
		if(mHVScrollView == null||mMovieLayout == null
				||mSurfaceView == null ||mSelectTimeLayout == null) return;
		
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        // 显示屏幕区域
        mScreenWidth = (int) dm.widthPixels;
        mScreenHeight = (int) dm.heightPixels;
        
         Resources resources=getResources();
        // 显示title布局高度 ,单位像素
        int titleHeight = (int) resources.getDimension(R.dimen.lsq_title_height);
        // 显示时间选择范围布局高度
        int selectTimeHeight = (int) (mScreenHeight - titleHeight-mScreenWidth);
        RectF boundingRect = new RectF();
        boundingRect.left = 0;
        boundingRect.right = mScreenWidth;
        boundingRect.top = 0;
        boundingRect.bottom = mScreenWidth;
        RectF rect = RectHelper.makeRectWithAspectRatioOutsideRect(new TuSdkSize(width, height), boundingRect);
        int w = (int) rect.width();
        int h = (int) rect.height();
        mScrollViewContentOffseX = 0;
        mScrollViewContentOffseY = 0;
        mCutRectLeftPercent = 0.0f;
        mCutRectTopPercent = 0.0f;;
        mCutRectRightPercent = mCutRectLeftPercent+mScreenWidth/(float)w;
        mCutRectBottomPercent = mCutRectTopPercent+mScreenWidth/(float)h;
        
        // 动态设置滚动控件
        setupHVScrollView(w,h);
        
        // 重新调整播放视频的显示区域
        updateMovieLayout(titleHeight);
        
        // 重新调整选择时间范围的显示区域
        updateSelectTimeLayout(selectTimeHeight);
        
        // 重新设置滚动控件布局的显示区域
        updateHVScrollViewLaytout();
        
        // 滚动控件设置延迟滚动
        updateHVScrollViewScolling(rect,w,h);
        
        // 重新设置SurfaceView控件的显示区域
        updateSurfaceViewLayout(w,h);
    }
    
    public void playerStateChanged(PlayerState state)
    {
  		if (mPlayer == null) return;
  		
		if (state == PlayerState.INITIALIZED)
		{
			mStartTime = 0;
			mEndTime = mPlayer.getDuration();
			mVideoTime = mPlayer.getDuration();
			mPlayer.seekTo(1);
			initRangeSelectionBar();
			updateLeftCursorTime();
			updateRightCursorTime();
			updatePlayCursorTime();
		}
    }
    
    public void updateProgress(int progress)
    {
    	if (mPlayer == null || mRangeSelectionBar == null || !mIsPlaying) return;
  		
		int  time = progress * mVideoTime / 100;
    	if (time < mStartTime) return;
  		if (time<mEndTime)
  		{
  			if (!mRangeSelectionBar.isShowPlayCursor())
  				mRangeSelectionBar.setShowPlayCursor(true);
  			mRangeSelectionBar.setPlaySelection(progress);
  		}
  		else
  		{
  			if (mRangeSelectionBar.isShowPlayCursor())
  				mRangeSelectionBar.setShowPlayCursor(false);
  			mIsVideoFinished = true;
  			pausePlayer();
  			showPlayButton();
  		}
    }
    
	private TuSDKMoviePlayerDelegate mMoviePlayerDelegate = new TuSDKMoviePlayerDelegate()
	{

		@Override
		public void onStateChanged(PlayerState state) 
		{
			playerStateChanged(state);
		}

		@Override
		public void onVideSizeChanged(MediaPlayer mp,int width, int height)
		{
			scaleVideoViews(width,height);
		}

		@Override
		public void onProgress(int progress)
		{
			updateProgress(progress);
		}

		@Override
		public void onSeekComplete()
		{
		}

		@Override
		public void onCompletion() 
		{
  			if (mRangeSelectionBar.isShowPlayCursor())
  				mRangeSelectionBar.setShowPlayCursor(false);
  			mIsVideoFinished = true;
  			pausePlayer();
  			showPlayButton();
		}
	};
}