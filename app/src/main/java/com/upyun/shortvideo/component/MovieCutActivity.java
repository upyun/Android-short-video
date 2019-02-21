package com.upyun.shortvideo.component;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.RectF;
import android.media.AudioManager;
import android.media.MediaCodecInfo;
import android.media.MediaFormat;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.lasque.tusdk.api.video.retriever.TuSDKVideoImageExtractor;
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
import org.lasque.tusdk.core.utils.TuSdkDate;
import org.lasque.tusdk.core.utils.image.AlbumHelper;
import org.lasque.tusdk.core.utils.image.ImageOrientation;
import org.lasque.tusdk.core.view.widget.button.TuSdkNavigatorBackButton;
import org.lasque.tusdk.video.editor.TuSdkTimeRange;

import com.upyun.shortvideo.ScreenAdapterActivity;
import com.upyun.shortvideo.album.MovieInfo;
import com.upyun.shortvideo.R;
import com.upyun.shortvideo.views.editor.LineView;

import java.io.File;
import java.io.IOException;
import java.util.List;

import at.grabner.circleprogress.CircleProgressView;

/** 视频裁剪 **/
public class MovieCutActivity extends ScreenAdapterActivity {

    /** 返回按钮 */
    private TuSdkNavigatorBackButton mBackBtn;
    /** 开始裁剪按钮 */
    private Button mStarCutBtn;
    /** MediaPlayer 播放器 */
    private MediaPlayer mMediaPlayer;
    /** 用于显示视频 */
    private SurfaceView mSurfaceView;
    //播放按钮
    private Button mPlayButton;
    /** PLAY TIME TextView */
    private TextView mPlayTextView;
    /** LEFT TIME TextView */
    private TextView mLeftTextView;
    /** RIGHT TIME TextView */
    private TextView mRightTextView;
    private LineView mRangeSelectionBar;
    /** 转码进度视图 */
    private FrameLayout mLoadContent;
    /** 剪切进度 */
    private CircleProgressView mCircleView;
    /** 视频播放地址 */
    private String mInputPath;
    /** 视频总时长 */
    private int mVideoTotalTime;
    /** 裁剪视频的开始时间 */
    private int mStart_time;
    /** 裁剪视频的结束时间 */
    private int mEnd_time;
    /** 记录是否移动裁剪控件左光标 */
    private boolean isMoveLeft;
    /** 记录是否移动裁剪控件右光标 */
    private boolean isMoveRight;

    private boolean isMoveStartTime;
    /** 是否播放视频 */
    private boolean isPlay;
    /** 是否暂停视频 */
    private boolean isPause;
    /**
     * 视频是否正在准备中
     * true 表示正在准备中
     * false 表示准备完成
     */
    private boolean isInit = false;
    /** 视频是否 是第一次加载 */
    private boolean isFirstLoadVideo = false;
    /** 是否正在裁剪中 **/
    private boolean isCutting = false;
    /** 裁剪后视频时长,单位s */
    private TuSdkTimeRange mCuTimeRange;
    /** VideoInfo */
    private TuSDKVideoInfo videoInfo;

    private SurfaceHolder.Callback mCallback = new SurfaceHolder.Callback() {

        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            /** 初始化播放器  */
            initMediaPlayer(holder);
        }

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
            if (mMediaPlayer != null) {
                mMediaPlayer.stop();
            }
        }
    };

    /** 销毁播放器 */
    public void destoryMediaPlayer() {
        if (mMediaPlayer != null) {
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
    }

    /** 裁剪进度回调 **/
    private TuSdkMediaProgress mCuterMediaProgress = new TuSdkMediaProgress() {

        @Override
        public void onProgress(float progress, TuSdkMediaDataSource mediaDataSource,
                               int index, int total) {
            mCircleView.setValue(progress * 100);
        }

        @Override
        public void onCompleted(Exception e, TuSdkMediaDataSource outputFile, int total) {
            Toast.makeText(getBaseContext(), e == null ? getResources().getString(R.string.lsq_movie_cut_done) : getResources().getString(R.string.lsq_movie_cut_error), Toast.LENGTH_SHORT).show();
            mLoadContent.setVisibility(View.GONE);
            mCircleView.setValue(0);
            isCutting = false;
            try {
                mMediaPlayer.reset();
                mMediaPlayer.setDataSource(mInputPath);
                mMediaPlayer.prepareAsync();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
    };

    private LineView.OnSelectTimeChangeListener onSelectTimeChangeListener = new LineView.OnSelectTimeChangeListener() {
        @Override
        public void onTimeChange(long startTime, long endTime, long selectTime, float startTimePercent, float endTimePercent, float selectTimePercent) {
            setTotalTime((float) (endTime - startTime) / (float)1000);
        }

        @Override
        public void onLeftTimeChange(long startTime, float startTimePercent) {
            mCuTimeRange.setStartTimeUs(startTime);
            setTextTime(mLeftTextView, (int) (startTime / 1000));
        }

        @Override
        public void onRightTimeChange(long endTime, float endTimePercent) {
            mCuTimeRange.setEndTimeUs(endTime);
            setTextTime(mRightTextView, (int) (endTime / 1000));
        }

        @Override
        public void onMaxValue() {

        }

        @Override
        public void onMinValue() {

        }
    };

    /** 选择进度回调 **/
    private LineView.OnPlayPointerChangeListener onPlayPointerChangeListener =  new LineView.OnPlayPointerChangeListener() {
        @Override
        public void onPlayPointerPosition(long playPointerPositionTime, float playPointerPositionTimePercent) {
            if(mMediaPlayer!=null && mRangeSelectionBar.getTouchingState()) {
                mMediaPlayer.seekTo((int) (playPointerPositionTime / 1000));
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_cut);
        initView();
        initData();
    }

    private void initData(){
        videoInfo = TuSDKMediaUtils.getVideoInfo(mInputPath);
        setTotalTime(videoInfo.durationTimeUs / 1000f);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isPause) {
            preparePlay();
            isPause = false;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (!isPause && isPlay) {
            pauseVideo();
            isPause = true;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        destoryMediaPlayer();
    }

    /**
     * 初始化View
     */
    private void initView() {

        List<MovieInfo> mInputPaths = (List<MovieInfo>) getIntent().getSerializableExtra("videoPaths");
        mInputPath = mInputPaths.get(0).getPath();

        mBackBtn = findViewById(R.id.lsq_backButton);
        mBackBtn.setOnClickListener(mOnClickListener);

        TextView titleView = findViewById(R.id.lsq_titleView);
        titleView.setText(TuSdkContext.getString("lsq_movie_cut_text"));

        mPlayButton = findViewById(R.id.lsq_play_btn);
        mPlayButton.setClickable(false);
        mStarCutBtn = findViewById(R.id.lsq_movie_cut_btn);
        mStarCutBtn.setOnClickListener(mOnClickListener);


        mSurfaceView = (SurfaceView) this.findViewById(R.id.lsq_video_view);
        mSurfaceView.setOnClickListener(mOnClickListener);


        mPlayTextView = this.findViewById(R.id.lsq_play_time);
        mLeftTextView = this.findViewById(R.id.lsq_left_time);
        mRightTextView = this.findViewById(R.id.lsq_right_time);

        mLeftTextView.setText(R.string.lsq_text_time_tv);
        mRightTextView.setText(R.string.lsq_text_time_tv);

        mRangeSelectionBar = this.findViewById(R.id.lsq_range_line);
        mRangeSelectionBar.setInitType(LineView.LineViewType.DrawPointer, getResources().getColor(R.color.lsq_color_white));
        mRangeSelectionBar.setOnSelectTimeChangeListener(onSelectTimeChangeListener);
        mRangeSelectionBar.setOnPlayPointerChangeListener(onPlayPointerChangeListener);
        mRangeSelectionBar.loadView();

        TuSdk.messageHub().applyToViewWithNavigationBarHidden(false);

        mLoadContent = findViewById(R.id.lsq_editor_cut_load);
        mCircleView = findViewById(R.id.circleView);

        // 加载视频缩略图
        loadVideoThumbList();
        showPlayButton();
        isFirstLoadVideo = false;
        // 裁剪后视频时长
        mCuTimeRange = new TuSdkTimeRange();

        mSurfaceView.getHolder().addCallback(mCallback);
    }

    /** 加载视频缩略图 */
    public void loadVideoThumbList() {
        if (mRangeSelectionBar != null) {
            TuSdkSize tuSdkSize = TuSdkSize.create(TuSdkContext.dip2px(56),
                    TuSdkContext.dip2px(56));
            TuSDKVideoImageExtractor extractor = TuSDKVideoImageExtractor.createExtractor();

            extractor.setOutputImageSize(tuSdkSize)
                    .setVideoDataSource(TuSDKMediaDataSource.create(mInputPath))
                    .setExtractFrameCount(20);

            extractor.asyncExtractImageList(new TuSDKVideoImageExtractor.TuSDKVideoImageExtractorDelegate() {
                @Override
                public void onVideoImageListDidLoaded(List<Bitmap> images) {
                    if(mMediaPlayer != null) {
                        mRangeSelectionBar.setTotalTimeUs(mMediaPlayer.getDuration() * 1000);
                        setTextTime(mRightTextView, mMediaPlayer.getDuration());
                    }
                }

                @Override
                public void onVideoNewImageLoaded(Bitmap bitmap) {
                    mRangeSelectionBar.addBitmap(bitmap);
                }

            });
        }
    }

    /** 设置时间 **/
    private void setTextTime(TextView textView,int times){
        TuSdkDate date = TuSdkDate.create(times);
        int minute = date.minute();
        int second = date.second();
        textView.setText(String.format("%02d:%02d",minute,second));
    }

    /** 设置总时长 微秒 **/
    private void setTotalTime(float times){
        float totalTime = times/(float)1000;
        mPlayTextView.setText(String.format(getString(R.id.lsq_movie_cut_selecttime) + "%.1fs",totalTime));

    }

    /**
     * 初始化播放器
     */
    /** 初始化播放器 */
    public void initMediaPlayer(SurfaceHolder holder) {
        isInit = false;
        mMediaPlayer = new MediaPlayer();
        mMediaPlayer.reset();
        mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

        // 将视频画面输出到SurfaceView
        mMediaPlayer.setDisplay(holder);

        // 设置需要播放的视频
        try {
            setDataSource(mInputPath);
            mMediaPlayer.prepareAsync();
        } catch (Exception e) {
            e.printStackTrace();
        }

        mMediaPlayer.setOnPreparedListener(mOnPreparedListener);
        mMediaPlayer.setOnSeekCompleteListener(mOnSeekCompleteListener);
        mMediaPlayer.setOnVideoSizeChangedListener(mOnVideoSizeChangedListener);
        mMediaPlayer.setOnCompletionListener(mOnCompletionListener);

    }

    private void setDataSource(String mInputPath) {
        if (mMediaPlayer == null) mMediaPlayer = new MediaPlayer();
        try {
            mMediaPlayer.setDataSource(mInputPath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private MediaPlayer.OnCompletionListener mOnCompletionListener = new MediaPlayer.OnCompletionListener() {
        @Override
        public void onCompletion(MediaPlayer mp) {
            pauseVideo();
        }
    };


    /** 用于监听播放进度 */
    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {
                int time = mMediaPlayer.getCurrentPosition();
                time = (time < mStart_time) ? (int) mStart_time : time;
                if (time >= (int) mStart_time && time < mEnd_time) {
                    /** 用于刷新播放进度条  */
                    if (mRangeSelectionBar != null) {
                        float percent =  ((float) time  / (float)mVideoTotalTime );
                        mRangeSelectionBar.pointerMoveToPercent(percent);
                    }
                } else {
                    showPlayButton();
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

    private MediaPlayer.OnPreparedListener mOnPreparedListener = new MediaPlayer.OnPreparedListener() {
        @Override
        public void onPrepared(MediaPlayer mp) {

            if (!isFirstLoadVideo) {
                isFirstLoadVideo = true;
                // 获取视频总时长
                mVideoTotalTime = mMediaPlayer.getDuration();
                mEnd_time = mVideoTotalTime;
                mCuTimeRange.setStartTime(0.0f);
                mCuTimeRange.setEndTime(mVideoTotalTime);

                seekToStart();
                isInit = true;
                return;
            }
            if (!isInit) {
                seekToStart();
                isInit = true;
                return;
            }

            playVideo();
        }
    };

    private MediaPlayer.OnSeekCompleteListener mOnSeekCompleteListener = new MediaPlayer.OnSeekCompleteListener() {
        @Override
        public void onSeekComplete(MediaPlayer mp) {
            if (isMoveLeft || isMoveRight || isMoveStartTime) {
                mp.pause();
                isMoveLeft = false;
                isMoveRight = false;
                isMoveStartTime = false;
                // 显示播放按钮
                showPlayButton();
            }
        }
    };

    private MediaPlayer.OnVideoSizeChangedListener mOnVideoSizeChangedListener = new MediaPlayer.OnVideoSizeChangedListener() {

        @Override
        public void onVideoSizeChanged(MediaPlayer mp, int width, int height) {
            // 将视频进行等比例显示处理
            setVideoSize(mSurfaceView, width, height);
        }
    };

    public void setVideoSize(SurfaceView surfaceView, int width, int height) {
        if (surfaceView != null) {
            DisplayMetrics dm = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(dm);
            int screenWidth = (int) dm.widthPixels;
            int screenHeight = (int) (360 * dm.density);

            Rect boundingRect = new Rect();
            boundingRect.left = 0;
            boundingRect.right = screenWidth;
            boundingRect.top = 0;
            boundingRect.bottom = screenHeight;
            Rect rect = RectHelper.makeRectWithAspectRatioInsideRect(new TuSdkSize(width, height), boundingRect);

            int w = rect.right - rect.left;
            int h = rect.bottom - rect.top;
            RelativeLayout.LayoutParams lp = new RelativeLayout
                    .LayoutParams(w, h);
            lp.setMargins(rect.left, rect.top, 0, 0);
            surfaceView.setLayoutParams(lp);
        }
    }

    /** 暂停播放 */
    public void pauseVideo() {
        if (mMediaPlayer != null) {
            mMediaPlayer.pause();
            showPlayButton();
            isPlay = false;
        }
    }

    /** 显示播放按钮 */
    public void showPlayButton() {
        if (mPlayButton != null) {
            mPlayButton.setVisibility(View.VISIBLE);
            mPlayButton.setBackgroundResource(R.drawable.lsq_editor_ic_play);
        }
    }

    /** 隐藏播放按钮 */
    public void hidePlayButton() {
        if (mPlayButton != null) {
            mPlayButton.setVisibility(View.VISIBLE);
            mPlayButton.setBackgroundColor(Color.TRANSPARENT);
        }
    }


    public void seekToStart() {
        isMoveStartTime = true;
        mStart_time = (mStart_time > 1) ? mStart_time : 1;
        mMediaPlayer.seekTo((int) mStart_time);
        mMediaPlayer.start();
    }

    /**
     * 播放视频
     * 由于部分小米手机同步加载视频不能正常播放，视频播放
     * 方式选用异步方式加载(即使用prepareAsync()方式加载,
     * 在onPrepared()方法中开始播放)
     */
    public void playVideo() {
        if (!isInit) {
            TuSdk.messageHub().showToast(this, R.string.lsq_video_read_prepare);
            return;
        }
        if (mMediaPlayer == null) {
            TuSdk.messageHub().showToast(this, R.string.lsq_video_empty_error);
            return;
        }
        isPlay = true;
        if (mMediaPlayer.isPlaying()) {
            mMediaPlayer.stop();
        }

        // 在指定位置进行播放
        mMediaPlayer.seekTo((int) mStart_time);
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

    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.lsq_backButton:
                    finish();
                    break;
                case R.id.lsq_movie_mixer_btn:
                    break;
                case R.id.lsq_movie_cut_btn:
                    startMovieClipper();
                    break;
                case R.id.lsq_video_view:
                    if (!isPlay) {
                        // 准备播放
                        preparePlay();
                    } else {
                        // 暂停
                        pauseVideo();
                    }
                    break;
            }
        }
    };

    /**
     * 开始裁剪
     */
    private void startMovieClipper() {
        if (isCutting) return;
        isCutting = true;

        MediaFormat ouputVideoFormat = getOutputVideoFormat(videoInfo);
        MediaFormat ouputAudioFormat = getOutputAudioFormat();

        TuSdkMediaTimeSlice timeSlice = new TuSdkMediaTimeSlice(mCuTimeRange.getStartTimeUS(), mCuTimeRange.getEndTimeUS());

        //画布大小 0 ~ 1
        RectF rectDrawF = new RectF(0, 0, 1, 1);
        //裁剪大小0 ~ 1
        RectF rectCutF = new RectF(0, 0, 1, 1);

        TuSdkMediaSuit.cuter(new TuSdkMediaDataSource(mInputPath), getOutPutFilePath(), ouputVideoFormat, ouputAudioFormat, ImageOrientation.Up,
                rectDrawF, rectCutF, timeSlice, mCuterMediaProgress);
        mLoadContent.setVisibility(View.VISIBLE);
    }


    /**
     * 获取输出文件的视频格式信息
     *
     * @param videoInfo 当前的音频信息
     * @return MediaFormat
     */
    protected MediaFormat getOutputVideoFormat(TuSDKVideoInfo videoInfo) {
        int fps = videoInfo.fps;
        int bitrate = videoInfo.bitrate;
        TuSdkSize size = TuSdkSize.create(videoInfo.width,videoInfo.height);

        if (videoInfo.videoOrientation == ImageOrientation.Right
                || videoInfo.videoOrientation == ImageOrientation.Left
                || videoInfo.videoOrientation == ImageOrientation.RightMirrored
                || videoInfo.videoOrientation == ImageOrientation.LeftMirrored)
            size.set(size.height,size.width);

        MediaFormat mediaFormat = TuSdkMediaFormat.buildSafeVideoEncodecFormat(size.width, size.height,
                fps, bitrate, MediaCodecInfo.CodecCapabilities.COLOR_FormatSurface, 0, 1);

        return mediaFormat;
    }

    /**
     * 获取输出的音频格式信息
     *
     * @return MediaFormat
     */
    protected MediaFormat getOutputAudioFormat() {
        MediaFormat audioFormat = TuSdkMediaFormat.buildSafeAudioEncodecFormat();
        return audioFormat;
    }

    private String getOutPutFilePath() {
        return new File(AlbumHelper.getAblumPath(),
                String.format("lsq_cut_%s.mp4", StringHelper.timeStampString())).toString();
    }


    /**
     * 准备播放视频
     * 由于部分小米手机同步加载视频不能正常播放，视频播放
     * 方式选用异步方式加载(即使用prepareAsync()方式加载,
     * 在onPrepared()方法中开始播放)
     */
    public void preparePlay() {
        if (!isInit) {
            TuSdk.messageHub().showToast(this, R.string.lsq_video_read_prepare);
            return;
        }
        if (mMediaPlayer == null) {
            TuSdk.messageHub().showToast(this, R.string.lsq_video_empty_error);
            return;
        }

        // 重置播放器设置
        mMediaPlayer.reset();
        mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        try {
            // 设置播放资源路径，准备播放
            setDataSource(mInputPath);
            // 设置异步播放
            mMediaPlayer.prepareAsync();
        } catch (Exception e) {
            TuSdk.messageHub().showToast(this, R.string.lsq_video_read_prepare);
            e.printStackTrace();
        }
    }

}
