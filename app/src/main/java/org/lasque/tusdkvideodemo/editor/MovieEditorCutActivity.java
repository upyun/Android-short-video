package org.lasque.tusdkvideodemo.editor;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.media.MediaCodecInfo;
import android.media.MediaFormat;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.lasque.tusdk.core.TuSdk;
import org.lasque.tusdk.core.api.extend.TuSdkMediaPlayerListener;
import org.lasque.tusdk.core.api.extend.TuSdkMediaProgress;
import org.lasque.tusdk.core.common.TuSDKMediaUtils;
import org.lasque.tusdk.core.decoder.TuSDKVideoInfo;
import org.lasque.tusdk.core.media.codec.extend.TuSdkMediaFormat;
import org.lasque.tusdk.core.media.codec.extend.TuSdkMediaTimeSlice;
import org.lasque.tusdk.core.media.codec.suit.mutablePlayer.TuSdkMediaFilesCuterImpl;
import org.lasque.tusdk.core.media.codec.suit.mutablePlayer.TuSdkMediaMutableFilePlayer;
import org.lasque.tusdk.core.media.codec.suit.mutablePlayer.TuSdkVideoImageExtractor;
import org.lasque.tusdk.core.media.codec.suit.mutablePlayer.TuSdkVideoImageExtractor.TuSdkVideoImageExtractorListener;
import org.lasque.tusdk.core.media.codec.video.TuSdkVideoQuality;
import org.lasque.tusdk.core.media.suit.TuSdkMediaSuit;
import org.lasque.tusdk.core.seles.output.SelesView;
import org.lasque.tusdk.core.struct.TuSdkMediaDataSource;
import org.lasque.tusdk.core.utils.JVMUtils;
import org.lasque.tusdk.core.utils.StringHelper;
import org.lasque.tusdk.core.utils.TLog;
import org.lasque.tusdk.core.utils.ThreadHelper;
import com.upyun.shortvideo.R;
import org.lasque.tusdkvideodemo.ScreenAdapterActivity;
import org.lasque.tusdkvideodemo.album.MovieInfo;
import org.lasque.tusdkvideodemo.views.editor.EditorCutView;
import org.lasque.tusdkvideodemo.views.editor.SpeedView;
import org.lasque.tusdkvideodemo.views.editor.playview.TuSdkMovieScrollContent;
import org.lasque.tusdkvideodemo.views.editor.playview.TuSdkRangeSelectionBar;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import at.grabner.circleprogress.CircleProgressView;

import static org.lasque.tusdk.core.media.codec.suit.mutablePlayer.TuSdkMediaMutableFilePlayerImpl.TuSdkMediaPlayerStatus.Playing;

/**
 * droid-sdk-video
 *
 * @author MirsFang
 * @Date 2018/9/21 14:42
 * @Copright (c) 2018 tusdk.com. All rights reserved.
 * <p>
 * 编辑-裁剪页面
 * @since v3.0.0
 */
public class MovieEditorCutActivity extends ScreenAdapterActivity {
    private static final String TAG = "MovieEditorCutActivity";
    //底部裁剪的控件
    private EditorCutView mEditorCutView;
    //返回按钮
    private TextView mBackBtn;
    //下一步按钮
    private TextView mNextBtn;
    //播放按钮
    private ImageView mPlayBtn;
    //播放器父视图
    private RelativeLayout mContent;
    //转码进度视图
    private FrameLayout mLoadContent;
    private CircleProgressView mLoadProgress;
    //播放器
    private TuSdkMediaMutableFilePlayer mVideoPlayer;
    //播放视图
    private SelesView mVideoView;
    //视频路径
    private List<MovieInfo> mVideoPaths;

    /** 当前剪裁后的持续时间   微秒 **/
    private long mDurationTimeUs;
    /** 左边控件选择的时间     微秒 **/
    private long mLeftTimeRangUs;
    /** 右边控件选择的时间     微秒**/
    private long mRightTimeRangUs;
    /** 最小裁切时间 */
    private long mMinCutTimeUs = 3 * 1000000;
    /** 裁切工具 */
    private TuSdkMediaFilesCuterImpl cuter;
    /** 是否已经设置总时间 **/
    private boolean isSetDuration = false;
    /** 是否正在裁剪中 **/
    private boolean isCutting = false;
    //播放器回调
    private TuSdkMediaPlayerListener mMediaPlayerListener = new TuSdkMediaPlayerListener() {
        @Override
        public void onStateChanged(final int state) {
            if(!isCutting) mPlayBtn.setVisibility(state == Playing.ordinal()? View.GONE:View.VISIBLE );
            mDurationTimeUs = mVideoPlayer.durationUs();
        }

        @Override
        public void onFrameAvailable() {
            mVideoView.requestRender();
        }

        @Override
        public void onProgress(long playbackTimeUs, TuSdkMediaDataSource mediaDataSource, long totalTimeUs) {
            if(mEditorCutView == null )return;
            TLog.e("playbackTimeUs %s",playbackTimeUs);
            float playPercent = (float)playbackTimeUs/(float) totalTimeUs;
            mEditorCutView.setVideoPlayPercent(playPercent);
        }

        @Override
        public void onCompleted(Exception e, TuSdkMediaDataSource mediaDataSource) {
            if(e != null) TLog.e(e);
        }
    };

    private float mCurrentSpeed = 1f;
    private float mCurrentLeftPercent = 0f;
    private float mCurrentRightPercent = 1.0f;

    /**
     * 播放速度控制器回调
     */
    private SpeedView.OnPlayingSpeedChangeListener mPlayingSpeedListener = new SpeedView.OnPlayingSpeedChangeListener() {
        @Override
        public void onChanged(float speed) {
            mCurrentSpeed = speed;
            mVideoPlayer.pause();
            mVideoPlayer.seekToPercentage(0);
            mVideoPlayer.setSpeed(speed);
            mEditorCutView.setTotalTime((long) (mDurationTimeUs / speed));
            mEditorCutView.setSpeedChangeRangTime(mEditorCutView.getRangTime()/speed);
            mLeftTimeRangUs = (long) ((mCurrentLeftPercent * mVideoPlayer.durationUs()) / mCurrentSpeed);
            mRightTimeRangUs = (long) (mCurrentRightPercent * mVideoPlayer.durationUs() / mCurrentSpeed);
            float selectTime = (mRightTimeRangUs - mLeftTimeRangUs) / 1000000.0f;
            mEditorCutView.setRangTime(Math.max(3.0f,selectTime));
            mVideoPlayer.resume();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_editor_cut);
        initView();

        //加载封面图
        loadVideoThumbList();


        // 初始化播放器
        initPlayer();
    }

    /** 初始化 **/
    private void initView() {
        mVideoPaths = (List<MovieInfo>) getIntent().getSerializableExtra("videoPaths");

        mEditorCutView = new EditorCutView(this);
        mBackBtn = findViewById(R.id.lsq_back);
        mBackBtn.setOnClickListener(mOnClickListener);
        mNextBtn = findViewById(R.id.lsq_next);
        mNextBtn.setOnClickListener(mOnClickListener);
        mPlayBtn = findViewById(R.id.lsq_play_btn);
        mLoadContent = findViewById(R.id.lsq_editor_cut_load);
        mLoadProgress = findViewById(R.id.lsq_editor_cut_load_parogress);


        mEditorCutView.setOnPlayPointerChangeListener(new TuSdkMovieScrollContent.OnPlayProgressChangeListener() {
            @Override
            public void onProgressChange(float percent) {
                if(!mVideoPlayer.isPause()){
                    mVideoPlayer.pause();
                    mPlayBtn.setVisibility(View.VISIBLE);
                }
                mVideoPlayer.seekToPercentage(percent);
            }
        });

        mEditorCutView.getLineView().setExceedCriticalValueListener(new TuSdkRangeSelectionBar.OnExceedCriticalValueListener() {
            @Override
            public void onMaxValueExceed() {

            }

            @Override
            public void onMinValueExceed() {
                Integer minTime = (int) (mMinCutTimeUs / 1000000);
                @SuppressLint("StringFormatMatches") String tips = String.format(getString(R.string.lsq_min_time_effect_tips), minTime);
                TuSdk.messageHub().showToast(MovieEditorCutActivity.this, tips);
            }
        });

        mEditorCutView.setOnSelectCeoverTimeListener(new TuSdkRangeSelectionBar.OnSelectRangeChangedListener() {
            @Override
            public void onSelectRangeChanged(float leftPercent, float rightPercent, int type) {
                mCurrentLeftPercent = leftPercent;
                mCurrentRightPercent = rightPercent;
                if(type == 0 ){
                    mLeftTimeRangUs = (long) ((leftPercent * mVideoPlayer.durationUs()) / mCurrentSpeed);
                    float selectTime = (mRightTimeRangUs - mLeftTimeRangUs) / 1000000.0f;
                    if(selectTime < 3.0)selectTime = 3.0f;
                    mEditorCutView.setRangTime(selectTime);
                    if(!mVideoPlayer.isPause()){
                        mVideoPlayer.pause();
                        mPlayBtn.setVisibility(View.VISIBLE);
                    }
                    mEditorCutView.setVideoPlayPercent(leftPercent);
                    mVideoPlayer.seekToPercentage(leftPercent);
                }else if(type == 1){
                    mRightTimeRangUs = (long) (rightPercent * mVideoPlayer.durationUs() / mCurrentSpeed);
                    float selectTime = (mRightTimeRangUs - mLeftTimeRangUs) / 1000000.0f;
                    if(selectTime < 3.0)selectTime = 3.0f;
                    mEditorCutView.setRangTime(selectTime);
                    if(!mVideoPlayer.isPause()){
                        mVideoPlayer.pause();
                        mPlayBtn.setVisibility(View.VISIBLE);
                    }
                    mEditorCutView.setVideoPlayPercent(rightPercent);
                    mVideoPlayer.seekToPercentage(rightPercent);
                }
            }
        });

        mEditorCutView.setOnPlayingSpeedChangeListener(mPlayingSpeedListener);

        mContent = findViewById(R.id.lsq_content);
        mContent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mVideoPlayer == null)return;
                if (mVideoPlayer.elapsedUs() == mVideoPlayer.durationUs())  {
                    mVideoPlayer.pause();
                    //增加延时等待seek时间
                    ThreadHelper.postDelayed(new Runnable(){
                        @Override
                        public void run() {
                                mVideoPlayer.resume();
                        }
                    },100);
                }
                if (mVideoPlayer.isPause()) {
                    mVideoPlayer.resume();
                } else {
                    mVideoPlayer.pause();
                }
            }
        });

    }

    @Override
    protected void onPause() {
        super.onPause();
        if(mVideoPlayer == null || mVideoPlayer.isPause()) return;
        mVideoPlayer.pause();
        mPlayBtn.setVisibility(View.VISIBLE);
    }

    private void setEnable(boolean enable){
        mBackBtn.setEnabled(enable);
        mNextBtn.setEnabled(enable);
        mPlayBtn.setEnabled(enable);
        mContent.setEnabled(enable);
    }

    /**
     * 点击事件监听
     */
    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.lsq_back:
                    //返回按钮
                    finish();
                    break;
                case R.id.lsq_next:
                    setEnable(false);
                    mVideoPlayer.pause();
                    mPlayBtn.setVisibility(View.GONE);
                    startCompound();
                    mLoadContent.setVisibility(View.VISIBLE);
                    mEditorCutView.setEnable(false);
                    break;
            }
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        if(mEditorCutView != null)
            mEditorCutView.setEnable(true);
    }

    /** 初始化播放器 **/
    public void initPlayer(){
        List<TuSdkMediaDataSource> sourceList = new ArrayList<>();

        for (MovieInfo movieInfo : mVideoPaths) {
            TLog.e("[Debug] video format = %s",TuSDKMediaUtils.getVideoFormat(TuSdkMediaDataSource.create(movieInfo.getPath()).get(0)));
            sourceList.add(TuSdkMediaDataSource.create(movieInfo.getPath()).get(0));
            mDurationTimeUs += TuSDKMediaUtils.getVideoInfo(movieInfo.getPath()).durationTimeUs;
        }

        float duration = mDurationTimeUs / 1000000.0f;
        mRightTimeRangUs = mDurationTimeUs;
        mEditorCutView.setRangTime(duration);
        mEditorCutView.setTotalTime(mDurationTimeUs);


        mVideoPlayer = (TuSdkMediaMutableFilePlayer) TuSdkMediaSuit.playMedia(sourceList,true,mMediaPlayerListener);

        if (mVideoPlayer == null) {
            TLog.e("%s directorPlayer create failed.", TAG);
            return;
        }
        /** 创建预览视图 */
        mVideoView = new SelesView(this);
        mVideoView.setFillMode(SelesView.SelesFillModeType.PreserveAspectRatio);
        mVideoView.setRenderer(mVideoPlayer.getExtenalRenderer());

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        mContent.addView(mVideoView, 0, params);
        /** Step 3: 连接视图对象 */
        mVideoPlayer.getFilterBridge().addTarget(mVideoView, 0);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mVideoPlayer.release();
        if (cuter != null){
            cuter.stop();
            cuter = null;
        }
        isNeedRelease = true;
        JVMUtils.runGC();
    }

    /**
     * 开始合成视频
     */
    private void startCompound(){
        if (cuter != null) {
            return;
        }

        isCutting = true;

        boolean enableAudioCheck = false;

        List<TuSdkMediaDataSource> sourceList = new ArrayList<>();

        // 遍历视频源
        for (MovieInfo movieInfo : mVideoPaths) {
            sourceList.add(TuSdkMediaDataSource.create(movieInfo.getPath()).get(0));
            TuSDKVideoInfo videoInfo = TuSDKMediaUtils.getVideoInfo(movieInfo.getPath());
            if(videoInfo.fps >= 55){
                enableAudioCheck = true;
            }
        }
        // 准备切片时间
        TuSdkMediaTimeSlice tuSdkMediaTimeSlice = new TuSdkMediaTimeSlice((long)(mLeftTimeRangUs * mCurrentSpeed),(long) (mRightTimeRangUs * mCurrentSpeed));
        tuSdkMediaTimeSlice.speed = mVideoPlayer.speed();

        // 准备裁剪对象
        cuter = new TuSdkMediaFilesCuterImpl();
        // 设置裁剪切片时间
        cuter.setTimeSlice(tuSdkMediaTimeSlice);
        // 设置数据源
        cuter.setMediaDataSources(sourceList);
        // 设置文件输出路径
        cuter.setOutputFilePath(getOutputTempFilePath().getPath());

        cuter.setEnableAudioCheck(enableAudioCheck);

        // 准备视频格式
        MediaFormat videoFormat = TuSdkMediaFormat.buildSafeVideoEncodecFormat( cuter.preferredOutputSize().width,  cuter.preferredOutputSize().height,
                30, TuSdkVideoQuality.RECORD_MEDIUM2.getBitrate(), MediaCodecInfo.CodecCapabilities.COLOR_FormatSurface, 0, 0);

        // 设置视频输出格式
        cuter.setOutputVideoFormat(videoFormat);
        // 设置音频输出格式
        cuter.setOutputAudioFormat(TuSdkMediaFormat.buildSafeAudioEncodecFormat());

        // 开始裁剪
        cuter.run(new TuSdkMediaProgress() {
            /**
             *  裁剪进度回调
             * @param progress        进度百分比 0-1
             * @param mediaDataSource 当前处理的视频媒体源
             * @param index           当前处理的视频索引
             * @param total           总共需要处理的文件数
             */
            @Override
            public void onProgress(final float progress, TuSdkMediaDataSource mediaDataSource, int index, int total) {
                ThreadHelper.post(new Runnable() {
                    @Override
                    public void run() {
                        mLoadContent.setVisibility(View.VISIBLE);
                        mLoadProgress.setValue(progress * 100);
                    }
                });
            }

            /**
             *  裁剪结束回调
             * @param e 如果成功则为Null
             * @param outputFile 输出文件路径
             * @param total 处理文件总数
             */
            @Override
            public void onCompleted(Exception e, TuSdkMediaDataSource outputFile, int total) {
                isCutting = false;
                ThreadHelper.post(new Runnable() {
                    @Override
                    public void run() {
                        setEnable(true);
                        mLoadContent.setVisibility(View.GONE);
                        mLoadProgress.setValue(0);
                        mPlayBtn.setVisibility(mVideoPlayer.isPause()?View.VISIBLE:View.GONE);
                    }
                });
                Intent intent = new Intent(MovieEditorCutActivity.this,MovieEditorActivity.class);
                intent.putExtra("videoPath", outputFile.getPath());
                startActivity(intent);
                cuter = null;
            }
        });
    }

    private boolean isNeedRelease = false;

    /** 获取临时文件路径 */
    protected File getOutputTempFilePath() {
        return new File(TuSdk.getAppTempPath(), String.format("lsq_%s.mp4", StringHelper.timeStampString()));
    }

    /** 加载视频缩略图 */
    public void loadVideoThumbList() {

        List<TuSdkMediaDataSource> sourceList = new ArrayList<>();

        for (MovieInfo movieInfo : mVideoPaths)
            sourceList.add(TuSdkMediaDataSource.create(movieInfo.getPath()).get(0));

        /** 准备视频缩略图抽取器 */
        final TuSdkVideoImageExtractor imageThumbExtractor = new TuSdkVideoImageExtractor(sourceList);
        imageThumbExtractor
               //.setOutputImageSize(TuSdkSize.create(50,50)) // 设置抽取的缩略图大小
                .setExtractFrameCount(20) // 设置抽取的图片数量
                .setImageListener(new TuSdkVideoImageExtractorListener() {

                    /**
                     * 输出一帧略图信息
                     *
                     * @param videoImage 视频图片
                     * @since v3.2.1
                     */
                    public void onOutputFrameImage(final TuSdkVideoImageExtractor.VideoImage videoImage) {
                        if (isNeedRelease){
                            imageThumbExtractor.setImageListener(null);
                            imageThumbExtractor.release();
                            return;
                        }
                        ThreadHelper.post(new Runnable() {
                            @Override
                            public void run() {
                                mEditorCutView.addBitmap(videoImage.bitmap);
                                if(!isSetDuration) {
                                    float duration = mVideoPlayer.durationUs() / 1000000.0f;
                                    mEditorCutView.setRangTime(duration);
                                    mEditorCutView.setTotalTime(mVideoPlayer.durationUs());
                                    if(duration >0)
                                    isSetDuration = true;
                                }
                                mEditorCutView.setMinCutTimeUs(mMinCutTimeUs/(float)mDurationTimeUs);
                            }
                        });
                    }

                    /**
                     * 抽取器抽取完成
                     *
                     * @since v3.2.1
                     */
                    @Override
                    public void onImageExtractorCompleted(List<TuSdkVideoImageExtractor.VideoImage> videoImagesList) {
                        /** 注意： videoImagesList 需要开发者自己释放 bitmap */
                        imageThumbExtractor.release();

                    }
                 })
                .extractImages(); // 抽取图片

    }
}
