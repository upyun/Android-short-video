/**
 * TuSDKVideoDemo
 * MovieEditorActivity.java
 *
 * @author Yanlin
 * @Date Feb 21, 2017 8:52:11 PM
 * @Copright (c) 2016 tusdk.com. All rights reserved.
 */
package com.upyun.shortvideo.component;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.RectF;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

import com.upyun.shortvideo.SimpleCameraActivity;
import com.upyun.shortvideo.utils.Constants;
import com.upyun.shortvideo.views.AudioEffectCellView;
import com.upyun.shortvideo.views.CompoundConfigView;
import com.upyun.shortvideo.views.ConfigViewParams;
import com.upyun.shortvideo.views.ConfigViewParams.ConfigViewArg;
import com.upyun.shortvideo.views.ConfigViewSeekBar;
import com.upyun.shortvideo.views.DubbingLayout;
import com.upyun.shortvideo.views.DubbingRecodLayout;
import com.upyun.shortvideo.views.EffectsTimelineView;
import com.upyun.shortvideo.views.FilterCellView;
import com.upyun.shortvideo.views.FilterConfigSeekbar;
import com.upyun.shortvideo.views.FilterConfigView;
import com.upyun.shortvideo.views.FilterConfigView.FilterConfigViewSeekBarDelegate;
import com.upyun.shortvideo.views.FilterListView;
import com.upyun.shortvideo.views.MovieEditorTabBar;
import com.upyun.shortvideo.views.MovieRangeSelectionBar;
import com.upyun.shortvideo.views.MovieRangeSelectionBar.OnCursorChangeListener;
import com.upyun.shortvideo.views.MovieRangeSelectionBar.Type;
import com.upyun.shortvideo.views.SceneEffectLayout;
import com.upyun.shortvideo.views.SceneEffectListView;
import com.upyun.shortvideo.views.StickerAudioEffectCellView;
import com.upyun.shortvideo.views.StickerAudioEffectListView;
import com.upyun.shortvideo.views.TextEffectLayout;
import com.upyun.shortvideo.views.TimeEffectsLayout;

import org.lasque.tusdk.api.video.retriever.TuSDKVideoImageExtractor;
import org.lasque.tusdk.core.TuSdk;
import org.lasque.tusdk.core.TuSdkContext;
import org.lasque.tusdk.core.common.TuSDKMediaDataSource;
import org.lasque.tusdk.core.decoder.TuSDKAudioDecoderTaskManager;
import org.lasque.tusdk.core.decoder.TuSDKVideoInfo;
import org.lasque.tusdk.core.seles.SelesParameters;
import org.lasque.tusdk.core.seles.SelesParameters.FilterArg;
import org.lasque.tusdk.core.seles.sources.TuSdkEditorEffector;
import org.lasque.tusdk.core.seles.sources.TuSdkEditorPlayer;
import org.lasque.tusdk.core.seles.sources.TuSdkEditorSaver;
import org.lasque.tusdk.core.seles.sources.TuSdkEditorTranscoder;
import org.lasque.tusdk.core.seles.sources.TuSdkMovieEditor;
import org.lasque.tusdk.core.seles.sources.TuSdkMovieEditorImpl;
import org.lasque.tusdk.core.seles.tusdk.FilterWrap;
import org.lasque.tusdk.core.struct.TuSdkMediaDataSource;
import org.lasque.tusdk.core.struct.TuSdkSize;
import org.lasque.tusdk.core.utils.ThreadHelper;
import org.lasque.tusdk.core.utils.TuSdkWaterMarkOption;
import org.lasque.tusdk.core.utils.image.BitmapHelper;
import org.lasque.tusdk.core.view.TuSdkViewHelper;
import org.lasque.tusdk.core.view.recyclerview.TuSdkTableView.TuSdkTableViewItemClickDelegate;
import org.lasque.tusdk.impl.components.widget.sticker.StickerView;
import org.lasque.tusdk.modules.view.widget.sticker.StickerGroup;
import org.lasque.tusdk.modules.view.widget.sticker.StickerLocalPackage;
import org.lasque.tusdk.video.editor.TuSDKMediaAudioEffectData;
import org.lasque.tusdk.video.editor.TuSDKMediaEffectData;
import org.lasque.tusdk.video.editor.TuSDKMediaFilterEffectData;
import org.lasque.tusdk.video.editor.TuSDKMediaSceneEffectData;
import org.lasque.tusdk.video.editor.TuSDKMediaStickerAudioEffectData;
import org.lasque.tusdk.video.editor.TuSDKMediaStickerEffectData;
import org.lasque.tusdk.video.editor.TuSDKMediaTextEffectData;
import org.lasque.tusdk.video.editor.TuSDKTimeRange;

import com.upyun.shortvideo.R;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import at.grabner.circleprogress.CircleProgressView;

import static com.upyun.shortvideo.views.MovieEditorTabBar.TabType.FilterTabType;
import static org.lasque.tusdk.video.editor.TuSDKMediaEffectData.TuSDKMediaEffectDataType.TuSDKMediaEffectDataTypeAudio;
import static org.lasque.tusdk.video.editor.TuSDKMediaEffectData.TuSDKMediaEffectDataType.TuSDKMediaEffectDataTypeFilter;
import static org.lasque.tusdk.video.editor.TuSDKMediaEffectData.TuSDKMediaEffectDataType.TuSDKMediaEffectDataTypeSticker;

/**
 * 视频编辑示例
 * <p>
 * 功能：
 * 1. 预览视频，添加滤镜，场景特效查看效果
 * 2. 导出新的视频
 *
 * @author Yanlin
 */
public class MovieEditorActivity extends SimpleCameraActivity implements View.OnClickListener,
        MovieEditorTabBar.MovieEditorTabBarDelegate, TuSDKVideoImageExtractor.TuSDKVideoImageExtractorDelegate {

    /************************************  SDK  ************************************ /

     /** 视频路径 */
    protected String mVideoPath;
    /**
     * 编辑器
     */
    protected TuSdkMovieEditorImpl mMovieEditor;

    /**
     * 视频信息
     */
    private TuSDKVideoInfo mVideoInfo;

    /************************************  Views  ************************************ /

     /** 底部 TabBar */
    protected MovieEditorTabBar mTabBar;
    /**
     * 顶部 TopBar
     **/
    protected RelativeLayout mTopBar;
    /**
     * 保存按钮
     */
    protected TextView mSaveButton;
    /**
     * 标题  TextView
     */
    private TextView mTitleTextView;
    /**
     * 返回按钮
     */
    protected TextView mBackTextView;
    /**
     * 开始播放按钮
     */
    protected Button mActionButton;
    /**
     * 滤镜栏
     */
    private FilterListView mFilterListView;
    /**
     * 滤镜参数调节栏
     */
    private FilterConfigView mConfigView;
    /**
     * 贴纸栏视图
     */
    protected StickerAudioEffectListView mMvListView;
    /**
     * 时间特效视图
     */
    protected TimeEffectsLayout mTimeEffectsLayout;
    /**
     * 文字特效视图
     */
    protected TextEffectLayout mTextEffectsLayout;
    /**
     * 文字区域视图
     **/
    protected StickerView mStickerView;
    /**
     * 滤镜区域视图
     **/
    protected LinearLayout mFilterLayout;

    /**
     * 视频裁剪控件
     */
    protected MovieRangeSelectionBar mRangeSelectionBar;

    // == 配音 == /

    /**
     * 配音列表视图
     */
    protected DubbingLayout mDubbingLayout;
    /**
     * 录制配音视图
     */
    protected DubbingRecodLayout mDubbingRecodLayout;

    /**
     * 声音强度调节栏
     */
    private CompoundConfigView mVoiceVolumeConfigView;

    // == 场景特效 == /

    /* 场景特效视图 */
    protected SceneEffectLayout mScenceEffectLayout;

    /************************************  辅助信息  ************************************ /

     /** 记录缩略图列表容器 */
    private List<Bitmap> mVideoThumbList;
    /**
     * 用于记录当前调节栏磨皮系数
     */
    private float mSmoothingProgress = -1.0f;
    /**
     * 用于记录当前调节栏效果系数
     */
    private float mMixiedProgress = -1.0f;

    /**
     * 用于记录焦点位置
     */
    public int mFocusPostion = 0;

    /**
     * 记录点击文字按钮前的状态
     **/
    private MovieEditorTabBar.TabType mCurrentTabType;

    private CircleProgressView mCircleView;

    //是否原比例
    protected boolean isRatioAdaption = true;
    //进行输出的比例
    private RectF mOutputRegion;

    /**
     * MV音效资源
     */
    @SuppressLint("UseSparseArrays")
    private Map<Integer, Integer> mMusicMap = new HashMap<Integer, Integer>();

    private boolean mActionButtonIsShow = true;

    protected void setActionButtonStatus(boolean isShow) {
        this.mActionButtonIsShow = isShow;

        if (mActionButtonIsShow)
            mActionButton.setVisibility(View.VISIBLE);
        else
            mActionButton.setVisibility(View.GONE);
    }

    protected int getLayoutId() {
        return R.layout.movie_editor_activity;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(getLayoutId());

        mSaveButton = findViewById(R.id.lsq_next);
        mSaveButton.setText(R.string.lsq_save);
        mSaveButton.setOnClickListener(this);
        mSaveButton.setEnabled(false);

        mTitleTextView = this.findViewById(R.id.lsq_title);
        mTitleTextView.setText(R.string.lsq_add_filter);
        mActionButton = findViewById(R.id.lsq_actButton);
        mActionButton.setVisibility(View.INVISIBLE);
        mActionButton.setOnClickListener(this);

        mBackTextView = findViewById(R.id.lsq_back);
        mBackTextView.setOnClickListener(this);


        mCircleView = (CircleProgressView) findViewById(R.id.circleView);
        mCircleView.setTextSize(50);
        mCircleView.setAutoTextSize(true);
        mCircleView.setTextColor(Color.WHITE);


        mTopBar = findViewById(R.id.lsq_topBar);

        mRangeSelectionBar = this.findViewById(R.id.lsq_rangeseekbar);
        mRangeSelectionBar.setShowPlayCursor(false);
        mRangeSelectionBar.setType(Type.MV);
        mRangeSelectionBar.setLeftSelection(0);
        mRangeSelectionBar.setPlaySelection(0);
        mRangeSelectionBar.setRightSelection(100);
        mRangeSelectionBar.setOnCursorChangeListener(onMVRangeSelectionBarListener);

        mStickerView = this.findViewById(R.id.lsq_stickerView);

        hideNavigationBar();
        // 设置弹窗提示是否在隐藏虚拟键的情况下使用
        TuSdk.messageHub().applyToViewWithNavigationBarHidden(true);

        setCameraViewSize(TuSdkContext.getScreenSize().width, TuSdkContext.getScreenSize().width);

        mVideoPath = getIntent().getStringExtra("videoPath");

        // 初始化编辑器
        initMovieEditor();

        getTabBar().setBackgroundColor(TuSdkContext.getColor("lsq_color_white"));
        getTabBar().setDelegate(this);


        // 滤镜视图
        initFilterLayout();

        // MV 视图
        initMVLayout();

        // 加载场景特效视图
        initScenceEffectLayout();

        // 配音视图
        initDubbingLayout();

        // 时间特效视图
        initTimeEffectsLayout();

        // 加载文字贴图
        initTextEffectLayout();

        // 加载视频缩略图
        loadVideoThumbList();

        updateSelectedTabType();
    }

    /**
     * 更新底部栏选中类型
     */
    protected void updateSelectedTabType() {
        // 底部栏默认选中滤镜
        getTabBar().updateButtonStatus(getTabBar().getFilterTab(), true);
        getTabBar().getDelegate().onSelectedTabType(FilterTabType);
    }


    @Override
    protected void onResume() {
        hideNavigationBar();
        super.onResume();
        startEffectsPreview();

    }

    @Override
    protected void onPause() {
        super.onPause();
        TuSdk.messageHub().dismissRightNow();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (mMovieEditor != null)
            mMovieEditor.onDestroy();

        if (mRangeSelectionBar != null)
            mRangeSelectionBar.clearVideoThumbList();

        if (mScenceEffectLayout != null)
            mScenceEffectLayout.getSceneEffectsTimelineView().clearVideoThumbList();

        if (mTextEffectsLayout != null)
            mTextEffectsLayout.clearVideoThumbList();

        if (mVideoThumbList != null) {
            for (Bitmap bitmap : mVideoThumbList)
                BitmapHelper.recycled(bitmap);

            mVideoThumbList.clear();
            mVideoThumbList = null;
        }

    }

    /************************************  SDK  ************************************ /
     *
     */
    /**
     * 初始化 TuSDKMovieEditorImpl
     */
    protected void initMovieEditor() {
        float movieLeft = getIntent().getFloatExtra("movieLeft", 0.0f);
        float movieTop = getIntent().getFloatExtra("movieTop", 0.0f);
        float movieRight = getIntent().getFloatExtra("movieRight", 1.0f);
        float movieBottom = getIntent().getFloatExtra("movieBottom", 1.0f);


        isRatioAdaption = getIntent().getBooleanExtra("ratioAdaption", true);

        mOutputRegion = new RectF(movieLeft, movieTop, movieRight, movieBottom);

        // 视频裁切区域时间
        TuSDKTimeRange cutTimeRange = TuSDKTimeRange.makeRange(getIntent().getFloatExtra("startTime", 0) / (float) 1000, getIntent().getFloatExtra("endTime", 0) / (float) 1000);


        TuSdkMovieEditor.TuSdkMovieEditorOptions defaultOptions = TuSdkMovieEditor.TuSdkMovieEditorOptions.defaultOptions();
        defaultOptions.setVideoDataSource(new TuSdkMediaDataSource(mVideoPath))
                .setCutTimeRange(cutTimeRange)
                .setIncludeAudioInVideo(true) // 设置是否保存或者播放原音
                .setClearAudioDecodeCacheInfoOnDestory(false)// 设置MovieEditor销毁时是否自动清除缓存音频解码信息
                .setWaterImage(BitmapHelper.getBitmapFormRaw(this, R.raw.sample_watermark), TuSdkWaterMarkOption.WaterMarkPosition.TopRight, true);


        mMovieEditor = new TuSdkMovieEditorImpl(this.getBaseContext(), getCameraView(), defaultOptions);

        mMovieEditor.setVideoPath(mVideoPath);
        //设置转码的回调
        mMovieEditor.getEditorTransCoder().addTransCoderProgressListener(mTransCoderListener);
        //设置播放回调
        mMovieEditor.getEditorPlayer().addProgressListener(mPlayerProgressListener);
        //设置滤镜设置的回调
        mMovieEditor.getEditorEffector().setFilterChangeListener(mFilterChangeListener);
        //设置音效回调
        mMovieEditor.getEditorMixer().addTaskStateListener(mAudioTaskStateListener);

        mMovieEditor.loadVideo();

    }

    /**
     * 转码回调
     */
    private TuSdkEditorTranscoder.TuSdkTranscoderProgressListener mTransCoderListener = new TuSdkEditorTranscoder.TuSdkTranscoderProgressListener() {
        @Override
        public void onProgressChanged(float percentage) {
            mCircleView.setVisibility(View.VISIBLE);
            mCircleView.setText((percentage * 100) + "%");
            mCircleView.setValue(percentage);
        }

        @Override
        public void onLoadComplete(final TuSDKVideoInfo videoInfo, TuSdkMediaDataSource videoSource) {
            loadComplete(videoInfo);
        }

        @Override
        public void onError(Exception e) {
            loadFailed();
        }
    };


    /**
     * 加载完成
     */
    protected void loadComplete(final TuSDKVideoInfo videoInfo) {
        ThreadHelper.post(new Runnable() {
            @Override
            public void run() {
                getTabBar().setEnable(true);
                mCircleView.setVisibility(View.GONE);
                onVideoInfoReady(videoInfo);

                //同步文字的StickerView
                mStickerView.resizeForVideo(TuSdkSize.create(videoInfo.width, videoInfo.height), isRatioAdaption);

                selectNormalFilterAndNormalMv();
                mSaveButton.setEnabled(true);

                mActionButton.setVisibility(View.VISIBLE);

                mTimeEffectsLayout.setVideoDuration();
            }
        });
    }

    /**
     * 加载失败
     */
    protected void loadFailed() {
        TuSdk.messageHub().dismissRightNow();
        mCircleView.setVisibility(View.GONE);
        TuSdk.messageHub().showError(MovieEditorActivity.this, getStringFromResource("lsq_loadvideo_failed"));
    }

    /**
     * 播放器进度回调
     */
    private TuSdkEditorPlayer.TuSdkProgressListener mPlayerProgressListener = new TuSdkEditorPlayer.TuSdkProgressListener() {
        @Override
        public void onStateChanged(int state) {
            onPlayStateChanged(state);
        }

        @Override
        public void onProgress(long playbackTimeUs, long totalTimeUs, float percentage) {
            onPlayerProgressChanged(playbackTimeUs, percentage);
            if (mTimeEffectsLayout != null) mTimeEffectsLayout.seek(percentage);
        }
    };

    /**
     * 播放状态改变
     **/
    protected void onPlayStateChanged(int state) {
        mActionButton.setVisibility(state == 0 ? View.INVISIBLE : View.VISIBLE);
    }

    protected void onPlayerProgressChanged(long playbackTimeUs, float percentage) {
        if (!mRangeSelectionBar.isShowPlayCursor())
            mRangeSelectionBar.setShowPlayCursor(true);

        mRangeSelectionBar.setPlaySelection((int) (percentage * 100));

        if (!mTextEffectsLayout.isShowPlayCursor())
            mTextEffectsLayout.setShowPlayCursor(true);
        mTextEffectsLayout.setPlaySelection((int) (percentage * 100));


        mScenceEffectLayout.getSceneEffectsTimelineView().setProgress(percentage);
        mScenceEffectLayout.getSceneEffectsTimelineView().updateLastEffectModelEndTime(percentage);
        mScenceEffectLayout.getSceneEffectListView().setPlayerPercent(percentage);
    }

    /**
     * 滤镜切换回调
     */
    private TuSdkEditorEffector.TuSdkEffectorFilterChangeListener mFilterChangeListener = new TuSdkEditorEffector.TuSdkEffectorFilterChangeListener() {
        @Override
        public void onFilterChanged(FilterWrap filter) {
            if (filter == null) return;
            SelesParameters params = filter.getFilterParameter();
            List<FilterArg> list = params.getArgs();
            for (FilterArg arg : list) {
                if (arg.equalsKey("smoothing") && mSmoothingProgress != -1.0f)
                    arg.setPrecentValue(mSmoothingProgress);
                else if (arg.equalsKey("smoothing") && mSmoothingProgress == -1.0f)
                    mSmoothingProgress = arg.getPrecentValue();
                else if (arg.equalsKey("mixied") && mMixiedProgress != -1.0f)
                    arg.setPrecentValue(mMixiedProgress);
                else if (arg.equalsKey("mixied") && mMixiedProgress == -1.0f)
                    mMixiedProgress = arg.getPrecentValue();

            }
            filter.setFilterParameter(params);

            getFilterConfigView().setSelesFilter(filter.getFilter());
        }
    };

    /**
     * 声音加载状态的回调
     */
    private TuSDKAudioDecoderTaskManager.TuSDKAudioDecoderTaskStateListener mAudioTaskStateListener = new TuSDKAudioDecoderTaskManager.TuSDKAudioDecoderTaskStateListener() {
        @Override
        public void onStateChanged(TuSDKAudioDecoderTaskManager.State state) {
            TuSdk.messageHub().dismissRightNow();
            if (state == TuSDKAudioDecoderTaskManager.State.Decoding) {
                String msg = getStringFromResource("new_movie_audio_effect_loading");
                TuSdk.messageHub().setStatus(MovieEditorActivity.this, msg);
                mMovieEditor.getEditorPlayer().pausePreview();
            } else if (state == TuSDKAudioDecoderTaskManager.State.Complete) {
                mMovieEditor.getEditorMixer().notifyLoadCompleted();
                mMovieEditor.getEditorPlayer().startPreview();
            } else {
                mMovieEditor.getEditorPlayer().startPreview();
            }
        }
    };

    /**
     * 切换滤镜
     *
     * @param code 滤镜code
     */
    protected void switchFilter(String code) {
        // 切换滤镜前必须打开视频预览, 滤镜切换依赖于视频的编解码
        // 如果视频暂停情况下切换滤镜会导致切换失败，onFilterChanged方法也不会回调
        startEffectsPreview();
        TuSDKMediaFilterEffectData mediaFilterEffectData = new TuSDKMediaFilterEffectData(code);
        mediaFilterEffectData.setAtTimeRange(TuSDKTimeRange.makeTimeUsRange(0, Long.MAX_VALUE));
        mMovieEditor.getEditorEffector().removeMediaEffectsWithType(TuSDKMediaEffectDataTypeFilter);
        mMovieEditor.getEditorEffector().addMediaEffectData(mediaFilterEffectData);
    }

    /**
     * 更新特效播放时间
     */
    private void updateMediaEffectsTimeRange() {
        if (mVideoInfo == null) return;

        long startTimeUs = (long) ((mRangeSelectionBar.getLeftCursorPercent() / 100.0f) * mVideoInfo.durationTimeUs);
        long endTimeUs = (long) ((mRangeSelectionBar.getRightCursorPercent() / 100.0f) * mVideoInfo.durationTimeUs);

        TuSDKTimeRange timeRange = TuSDKTimeRange.makeTimeUsRange(startTimeUs, endTimeUs);

        // TODO : 更新音效和贴纸时间
        // 设置音频特效播放区间
        if (mMovieEditor.getEditorEffector().mediaEffectsWithType(TuSDKMediaEffectDataTypeAudio) != null) {
            for (TuSDKMediaEffectData mediaEffectData : mMovieEditor.getEditorEffector().mediaEffectsWithType(TuSDKMediaEffectDataTypeAudio))
                mediaEffectData.setAtTimeRange(timeRange);
        }
        // 设置贴纸特效播放区间
        if (mMovieEditor.getEditorEffector().mediaEffectsWithType(TuSDKMediaEffectDataTypeSticker) != null) {
            for (TuSDKMediaEffectData mediaEffectData : mMovieEditor.getEditorEffector().mediaEffectsWithType(TuSDKMediaEffectDataTypeSticker))
                mediaEffectData.setAtTimeRange(timeRange);
        }
    }

    /**
     * 启动视频预览
     */
    protected void startPreView() {
        if (mMovieEditor == null) return;
        mMovieEditor.getEditorPlayer().startPreview();
    }

    /**
     * 开始播放视频并预览设置的特效
     */
    protected void startEffectsPreview() {
        if (mMovieEditor == null) return;
        // 添加设置的特效信息
        updateMediaEffectsTimeRange();
        mMovieEditor.getEditorPlayer().startPreview();

    }

    /**
     * 暂停预览
     */
    protected void pausePreview() {
        if (mMovieEditor == null) return;
        mMovieEditor.getEditorPlayer().pausePreview();
    }

    /**
     * 应用用户设置的所有特效
     */
    protected void applyMediaEffects() {
        updateMediaEffectsTimeRange();
    }

    /**
     * 启动录制
     */
    private void startRecording() {
        if (mMovieEditor == null || mMovieEditor.getEditorSaver().getStatus() == TuSdkEditorSaver.Saving)
            return;

        // 更新音效时间
        updateMediaEffectsTimeRange();

        pausePreview();
        // 生成视频文件
        mMovieEditor.getEditorSaver().addSaverProgressListener(mSaveProgressListener);
        mMovieEditor.saveVideo();
    }

    /**
     * 视频保存事件委托
     */
    private TuSdkEditorSaver.TuSdkSaverProgressListener mSaveProgressListener = new TuSdkEditorSaver.TuSdkSaverProgressListener() {
        @Override
        public void onProgress(float progress) {
            mCircleView.setVisibility(View.VISIBLE);
            mCircleView.setText((progress * 100) + "%");
            mCircleView.setValue(progress);
        }

        @Override
        public void onCompleted(TuSdkMediaDataSource outputFile) {
            mCircleView.setVisibility(View.GONE);
            setResult(RESULT_OK);
            saveSuccess();
        }

        @Override
        public void onError(Exception e) {
            mCircleView.setVisibility(View.GONE);
            saveError();
        }


        private void saveSuccess() {
            TuSdk.messageHub().dismissRightNow();
            TuSdk.messageHub().showSuccess(MovieEditorActivity.this, R.string.lsq_video_save_ok);
            //延时关闭
            ThreadHelper.postDelayed(new Runnable() {
                @Override
                public void run() {
                    finish();
                }
            }, 500);
        }


        private void saveError() {
            TuSdk.messageHub().dismissRightNow();
            TuSdk.messageHub().showError(MovieEditorActivity.this, R.string.new_movie_error_saving);
        }

    };

    /**
     * 获取到视频信息
     *
     * @param videoInfo
     */
    protected void onVideoInfoReady(TuSDKVideoInfo videoInfo) {
        this.mVideoInfo = videoInfo;
        getScenceEffectLayout().getSceneEffectsTimelineView().setDurationTimueUs(videoInfo.durationTimeUs);
    }

    /**
     * 获取底部TabBar
     *
     * @return
     */
    public MovieEditorTabBar getTabBar() {
        if (mTabBar == null) {
            mTabBar = findViewById(R.id.lsq_bottom_navigator);
            mTabBar.loadView();
            mTabBar.setEnable(false);
        }

        return mTabBar;
    }

    /**
     * 加载场景特效视图
     */
    private void initScenceEffectLayout() {
        getScenceEffectLayout().setDelegate(mSceneEffectListViewDelegate);
        getScenceEffectLayout().setMovieEditor(mMovieEditor);
    }

    public SceneEffectLayout getScenceEffectLayout() {
        if (mScenceEffectLayout == null) {
            mScenceEffectLayout = findViewById(R.id.lsq_scence_effect_layout);
            mScenceEffectLayout.loadView();
        }

        return mScenceEffectLayout;
    }

    /**
     * 初始化音效视图
     */
    private void initDubbingLayout() {
        mDubbingLayout = findViewById(R.id.lsq_dubbing_wrap);
        mDubbingLayout.loadView();

        mDubbingLayout.getDubbingListView().setBackgroundColor(TuSdkContext.getColor("lsq_color_white"));
        mDubbingLayout.getDubbingListView().setItemClickDelegate(mMixingTableItemClickDelegate);


        mDubbingRecodLayout = findViewById(R.id.lsq_editor_dubbing_record_layout);
        mDubbingRecodLayout.loadView();
        mDubbingRecodLayout.setVisibility(View.GONE);

        /** 设置录制委托 */
        mDubbingRecodLayout.setDelegate(new DubbingRecodLayout.DubbingRecodLayoutDelegate() {

            /**
             * 录制完成
             *
             * @param file 录制的文件
             */
            @Override
            public void onRecordCompleted(File file) {

                mMovieEditor.getEditorPlayer().setVideoSoundVolume(1.0f);
                getConfigViewSeekBar(0).setProgress(1.0f);

                if (file != null) {
                    applyAudioEffect(Uri.fromFile(file));
                    startEffectsPreview();
                }

            }

            /**
             * 开始录制
             */
            @Override
            public void onRecordStarted() {
                mMovieEditor.getEditorPlayer().pausePreview();
                startEffectsPreview();
            }

            /**
             * 录制已停止
             */
            @Override
            public void onRecordStoped() {

                mMovieEditor.getEditorPlayer().pausePreview();
            }
        });

        getVoiceVolumeConfigView();

    }

    protected CompoundConfigView getVoiceVolumeConfigView() {
        if (mVoiceVolumeConfigView == null) {
            mVoiceVolumeConfigView = (CompoundConfigView) findViewById(R.id.lsq_voice_volume_config_view);
            mVoiceVolumeConfigView.setBackgroundResource(R.color.lsq_alpha_white_99);
            mVoiceVolumeConfigView.setDelegate(mFilterConfigSeekbarDelegate);

            ConfigViewParams params = new ConfigViewParams();
            params.appendFloatArg(TuSdkContext.getString("originIntensity"), 1.0f);
            params.appendFloatArg(TuSdkContext.getString("dubbingIntensity"), 1.0f);
            mVoiceVolumeConfigView.setCompoundConfigView(params);
        }

        return mVoiceVolumeConfigView;
    }

    /**
     * 原音配音调节栏委托事件
     */
    private ConfigViewSeekBar.ConfigSeekbarDelegate mFilterConfigSeekbarDelegate = new ConfigViewSeekBar.ConfigSeekbarDelegate() {

        @Override
        public void onSeekbarDataChanged(ConfigViewSeekBar seekbar, ConfigViewArg arg) {
            if (arg.getKey().equals("originIntensity")) {
                mMovieEditor.getEditorMixer().setMasterAudioTrack(arg.getPercentValue());
            } else if (arg.getKey().equals("dubbingIntensity")) {
                mMovieEditor.getEditorMixer().setSecondAudioTrack(arg.getPercentValue());
            }
        }
    };


    /**
     * 加载视频缩略图
     */
    public void loadVideoThumbList() {

        if ((mRangeSelectionBar != null || mRangeSelectionBar.getVideoThumbList().size() == 0)
                && (mScenceEffectLayout != null || mRangeSelectionBar.getVideoThumbList().size() == 0)) {

            // 视频裁切区域时间
            TuSDKTimeRange cutTimeRange = TuSDKTimeRange.makeRange(getIntent().getFloatExtra("startTime", 0) / (float) 1000, getIntent().getFloatExtra("endTime", 0) / (float) 1000);

            TuSdkSize tuSdkSize = TuSdkSize.create(TuSdkContext.dip2px(56), TuSdkContext.dip2px(56));
            TuSDKVideoImageExtractor extractor = TuSDKVideoImageExtractor.createExtractor();
            extractor.setOutputImageSize(tuSdkSize);
            extractor.setVideoDataSource(TuSDKMediaDataSource.create(mVideoPath));
            extractor.setExtractFrameCount(6);
            extractor.setTimeRange(cutTimeRange);

            extractor.asyncExtractImageList(this);

        }

    }

    @Override
    public void onVideoImageListDidLoaded(List<Bitmap> images) {
        if (mTimeEffectsLayout != null) {
            mTimeEffectsLayout.setCoverImageList(images);
        }
    }

    @Override
    public void onVideoNewImageLoaded(Bitmap bitmap) {
        mRangeSelectionBar.drawVideoThumb(bitmap);
        mTextEffectsLayout.drawVideoThumb(bitmap);
        mScenceEffectLayout.getSceneEffectsTimelineView().drawVideoThumb(bitmap);
    }


    protected RelativeLayout getCameraView() {
        RelativeLayout cameraView = (RelativeLayout) findViewById(R.id.lsq_cameraView);
        return cameraView;
    }

    /**
     * 设置视频播放区域大小
     */
    protected void setCameraViewSize(int width, int height) {
        LayoutParams lp = (LayoutParams) getCameraView().getLayoutParams();
        lp.width = width;
        lp.height = height;
    }

    /**
     * 初始化MV贴纸视图
     */
    protected void initMVLayout() {
        getMVLayout().setBackgroundColor(TuSdkContext.getColor("lsq_color_white"));

        getMvListView();

        if (mMvListView == null) return;

        mMusicMap.put(1420, R.raw.lsq_audio_cat);
        mMusicMap.put(1427, R.raw.lsq_audio_crow);
        mMusicMap.put(1432, R.raw.lsq_audio_tangyuan);
        mMusicMap.put(1446, R.raw.lsq_audio_children);

        List<StickerGroup> groups = new ArrayList<StickerGroup>();
        List<StickerGroup> smartStickerGroups = StickerLocalPackage.shared().getSmartStickerGroups(false);

        for (StickerGroup smartStickerGroup : smartStickerGroups) {
            if (mMusicMap.containsKey((int) smartStickerGroup.groupId))
                groups.add(smartStickerGroup);
        }

        groups.add(0, new StickerGroup());
        this.mMvListView.setModeList(groups);
    }


    public RelativeLayout getMVLayout() {
        RelativeLayout mvLayoutWrap = (RelativeLayout) findViewById(R.id.filter_mv_wrap_layout);
        return mvLayoutWrap;
    }

    /**
     * 贴纸组视图
     */
    private StickerAudioEffectListView getMvListView() {
        if (mMvListView == null) {
            mMvListView = findViewById(R.id.lsq_mv_list_view);
            mMvListView.loadView();
            mMvListView.setCellLayoutId(R.layout.movie_editor_sticker_audio_effect_cell_view);
            mMvListView.setItemClickDelegate(mMvTableItemClickDelegate);
            mMvListView.reloadData();
        }

        return mMvListView;
    }

    /**
     * 混音列表点击事件
     */
    private TuSdkTableViewItemClickDelegate<AudioEffectCellView.AudioEffectEntity, AudioEffectCellView> mMixingTableItemClickDelegate = new TuSdkTableViewItemClickDelegate<AudioEffectCellView.AudioEffectEntity, AudioEffectCellView>() {
        @Override
        public void onTableViewItemClick(AudioEffectCellView.AudioEffectEntity itemData, AudioEffectCellView itemView, final int position) {

            if (TuSdkViewHelper.isFastDoubleClick()) return;

            mDubbingLayout.getDubbingListView().setSelectedPosition(position);

            // 停止预览
            mMovieEditor.getEditorPlayer().pausePreview();

            ThreadHelper.post(new Runnable() {
                @Override
                public void run() {
                    // 应用配音特效
                    changeAudioEffect(position);
                }
            });
        }
    };

    /**
     * MV 列表点击事件
     */
    private TuSdkTableViewItemClickDelegate<StickerGroup, StickerAudioEffectCellView> mMvTableItemClickDelegate = new TuSdkTableViewItemClickDelegate<StickerGroup, StickerAudioEffectCellView>() {
        @Override
        public void onTableViewItemClick(final StickerGroup itemData, StickerAudioEffectCellView itemView, final int position) {
            if (TuSdkViewHelper.isFastDoubleClick()) return;

            getMvListView().setSelectedPosition(position);

            // 选中当前 MV
            mMovieEditor.getEditorPlayer().pausePreview();

            ThreadHelper.post(new Runnable() {
                @Override
                public void run() {
                    // 应用 MV 特效
                    changeMvEffect(position, itemData);
                }
            });
        }
    };


    /**
     * 滤镜组列表点击事件
     */
    private TuSdkTableViewItemClickDelegate<String, FilterCellView> mFilterTableItemClickDelegate = new TuSdkTableViewItemClickDelegate<String, FilterCellView>() {
        @Override
        public void onTableViewItemClick(String itemData, FilterCellView itemView, int position) {
            if (TuSdkViewHelper.isFastDoubleClick()) return;

            startPreView();

            mFocusPostion = position;
            mFilterListView.setSelectedPosition(mFocusPostion);

            getFilterConfigView().setVisibility((position == 0) ? View.INVISIBLE : View.VISIBLE);

            switchFilter(itemData);
        }
    };

    /**
     * 应用背景音乐特效
     *
     * @param position
     */
    protected void changeAudioEffect(int position) {

        mMovieEditor.getEditorEffector().removeMediaEffectsWithType(TuSDKMediaEffectDataTypeSticker);
        mMovieEditor.getEditorEffector().removeMediaEffectsWithType(TuSDKMediaEffectDataTypeAudio);
        mMovieEditor.getEditorMixer().clearAllAudioData();

        if (position == 0) {
            mMovieEditor.getEditorEffector().removeMediaEffectsWithType(TuSDKMediaEffectDataTypeSticker);
            mMovieEditor.getEditorEffector().removeMediaEffectsWithType(TuSDKMediaEffectDataTypeAudio);
            mMovieEditor.getEditorMixer().clearAllAudioData();
            setDubbingSeekbarProgress(0.0f);
        } else if (position == 1) {
            handleAudioRecordBtn();
            return;
        } else if (position > 1) {

            Uri uri = Uri.parse("android.resource://" + getPackageName() + "/" + Constants.AUDIO_EFFECTS[position - 2]);

            applyAudioEffect(uri);
        }

        // 启动视频预览
        startEffectsPreview();
    }

    /**
     * 设置配音音效
     *
     * @param audioPathUri
     */
    private void applyAudioEffect(Uri audioPathUri) {
        if (audioPathUri == null) return;

        TuSDKMediaAudioEffectData audioEffectData = new TuSDKMediaAudioEffectData(new TuSdkMediaDataSource(this, audioPathUri));

        //添加音效
        mMovieEditor.getEditorEffector().addMediaEffectData(audioEffectData);
        mMovieEditor.getEditorMixer().addAudioRenderEntry(audioEffectData.getAudioEntry());
        updateMediaEffectsTimeRange();
        mMovieEditor.getEditorMixer().loadAudio();
        setDubbingSeekbarProgress(1.0f);

    }

    /**
     * 处理自己录音按钮操作
     */
    private void handleAudioRecordBtn() {
        if (mVideoInfo == null) return;

        mDubbingRecodLayout.setVisibility(View.VISIBLE);

        // 设置最大录制时长
        mDubbingRecodLayout.setMaxRecordTime(mVideoInfo.durationTimeUs / 1000000l);
        mDubbingRecodLayout.updateAudioProgressBar(0);

        // 将原音强度设为0
        mMovieEditor.getEditorPlayer().setVideoSoundVolume(0.0f);

        getConfigViewSeekBar(0).setProgress(0.0f);

    }

    /**
     * 应用MV特效
     *
     * @param position
     * @param itemData
     */
    protected void changeMvEffect(int position, StickerGroup itemData) {
        if (position < 0) return;

        mMovieEditor.getEditorEffector().removeMediaEffectsWithType(TuSDKMediaEffectDataTypeAudio);
        mMovieEditor.getEditorEffector().removeMediaEffectsWithType(TuSDKMediaEffectDataTypeSticker);

        if (position >= 0) {
            int groupId = (int) itemData.groupId;
            if (position == 0) {
                mMovieEditor.getEditorMixer().clearAllAudioData();
            }
            if (mMusicMap != null && mMusicMap.containsKey(groupId)) {
                //带音效的MV
                Uri uri = Uri.parse("android.resource://" + getPackageName() + "/" + mMusicMap.get(groupId));
                TuSDKMediaStickerAudioEffectData stickerAudioEffectDat = new TuSDKMediaStickerAudioEffectData(new TuSdkMediaDataSource(this, uri), itemData);
                stickerAudioEffectDat.setAtTimeRange(TuSDKTimeRange.makeRange(0, Float.MAX_VALUE));
                stickerAudioEffectDat.getMediaAudioEffectData().getAudioEntry().setLooping(true);
                mMovieEditor.getEditorEffector().addMediaEffectData(stickerAudioEffectDat);
            } else {
                //纯贴纸的MV
                TuSDKMediaStickerEffectData stickerEffectData = new TuSDKMediaStickerEffectData(itemData);
                stickerEffectData.setAtTimeRange(TuSDKTimeRange.makeRange(0, Float.MAX_VALUE));
                mMovieEditor.getEditorEffector().addMediaEffectData(stickerEffectData);
            }
        }

        //循环播放视频
        startEffectsPreview();
    }

    /**
     * 初始化配音强度SeekBar
     *
     * @param progress
     */
    public void setDubbingSeekbarProgress(float progress) {
        getConfigViewSeekBar(1).setProgress(progress);
    }

    /**
     * 初始化滤镜栏视图
     */
    protected void initFilterLayout() {
        mFilterLayout = (LinearLayout) findViewById(R.id.lsq_filter_layout);
        mFilterLayout.setVisibility(View.GONE);

        getFilterListView();

        if (mFilterListView == null) return;

        this.mFilterListView.setModeList(Arrays.asList(Constants.EDITORFILTERS));
    }

    /**
     * 滤镜栏视图
     *
     * @return
     */
    private FilterListView getFilterListView() {
        if (mFilterListView == null) {
            mFilterListView = (FilterListView) findViewById(R.id.lsq_filter_list_view);

            if (mFilterListView == null) return null;

            mFilterListView.loadView();
            mFilterListView.setCellLayoutId(R.layout.filter_list_cell_view);
            mFilterListView.setCellWidth(TuSdkContext.dip2px(62));
            mFilterListView.setItemClickDelegate(mFilterTableItemClickDelegate);
            mFilterListView.reloadData();
            mFilterListView.selectPosition(mFocusPostion);
        }
        return mFilterListView;
    }

    protected FilterConfigView getFilterConfigView() {
        if (mConfigView == null) {
            mConfigView = (FilterConfigView) findViewById(R.id.lsq_filter_config_view);
            mConfigView.setBackgroundResource(R.color.lsq_alpha_white_99);
            mConfigView.setSeekBarDelegate(mConfigSeekBarDelegate);
        }

        return mConfigView;
    }

    // 初始化时间特效界面
    private void initTimeEffectsLayout() {
        mTimeEffectsLayout = findViewById(R.id.lsq_time_effects_layout);
        mTimeEffectsLayout.setVisibility(View.GONE);
        mTimeEffectsLayout.setMovieEditor(mMovieEditor);
        mTimeEffectsLayout.loadView();
    }


    /**
     * 初始化文字特效
     */
    private void initTextEffectLayout() {
        mTextEffectsLayout = findViewById(R.id.lsq_text_effects_layout);


        //绑定相关
        mTextEffectsLayout.setMovieEditor(mMovieEditor);
        mTextEffectsLayout.setActivity(this);
        mTextEffectsLayout.setStickerView(mStickerView);

        //设置文字的初始样式
        mTextEffectsLayout.setText("请输入文字");
        mTextEffectsLayout.setTextColor("#ffffff");
        mTextEffectsLayout.setTextPaddings(20);
        mTextEffectsLayout.setTextShadowColor("#fff222");
        mTextEffectsLayout.setTextSize(20);
        mTextEffectsLayout.setColorBarHeight(TuSdkContext.dip2px(20));
        mTextEffectsLayout.setColorIndicatorWidth(TuSdkContext.dip2px(20));
        mTextEffectsLayout.setColorIndicatorHeight(TuSdkContext.dip2px(20));
        mTextEffectsLayout.setColorBarPaddingTop(TuSdkContext.dip2px(5));
        mTextEffectsLayout.loadView();

        mTextEffectsLayout.setOnTextStickerApplyListener(new TextEffectLayout.OnTextStickerApplyListener() {
            @Override
            public void onApply(TuSDKMediaTextEffectData textMediaEffectData) {
                mMovieEditor.getEditorEffector().addMediaEffectData(textMediaEffectData);
            }
        });
    }


    /**
     * 切换Tab事件
     *
     * @param tabType
     */
    @Override
    public void onSelectedTabType(MovieEditorTabBar.TabType tabType) {

        if (tabType != MovieEditorTabBar.TabType.TextEffectTabType) {
            mCurrentTabType = tabType;
        }


        switch (tabType) {
            case TimeEffectTabType:
                //时间特效
                toggleTimeEffectMode();
                break;
            case FilterTabType:
                toggleFilterMode();
                break;
            case DubbingTabType:
                toggleDubbingMode();
                break;
            case SenceEffectTabType:
                toggleScenceEffectMode();
                break;
            case MVTabType:
                toggleMVMode();
                break;
            case TextEffectTabType:
                toggleTextEffectMode();
        }
    }

    /**
     * 文字特效
     */
    private void toggleTextEffectMode() {

        mRangeSelectionBar.setVisibility(View.GONE);
        mTextEffectsLayout.setVisibility(View.VISIBLE);

        getTabBar().setVisibility(View.GONE);
        getMVLayout().setVisibility(View.GONE);

        mFilterLayout.setVisibility(View.GONE);
        mDubbingLayout.setVisibility(View.GONE);
        getVoiceVolumeConfigView().setVisibility(View.GONE);
        getFilterConfigView().setVisibility(View.GONE);
        mScenceEffectLayout.setVisibility(View.GONE);
        mTimeEffectsLayout.setVisibility(View.GONE);

        //选择文字之后默认添加一个Item
        mTextEffectsLayout.addStickerItemView();

//		startEffectsPreview();
    }


    /**
     * 时间特效
     */
    protected void toggleTimeEffectMode() {

        getMVLayout().setVisibility(View.GONE);
        mRangeSelectionBar.setVisibility(View.GONE);

        mFilterLayout.setVisibility(View.GONE);
        mDubbingLayout.setVisibility(View.GONE);
        getVoiceVolumeConfigView().setVisibility(View.GONE);
        getFilterConfigView().setVisibility(View.GONE);
        mScenceEffectLayout.setVisibility(View.GONE);
        mTextEffectsLayout.setVisibility(View.GONE);
        mTimeEffectsLayout.setVisibility(View.VISIBLE);

    }

    /**
     * 选择滤镜
     */
    protected void toggleFilterMode() {
        mFilterLayout.setVisibility(View.VISIBLE);

        getMVLayout().setVisibility(View.GONE);
        getVoiceVolumeConfigView().setVisibility(View.INVISIBLE);
        mScenceEffectLayout.setVisibility(View.GONE);
        mRangeSelectionBar.setVisibility(View.GONE);
        mDubbingLayout.setVisibility(View.GONE);
        mTimeEffectsLayout.setVisibility(View.GONE);
        mTextEffectsLayout.setVisibility(View.GONE);

        if (mFocusPostion == 0)
            getFilterConfigView().setVisibility(View.GONE);
        else
            getFilterConfigView().setVisibility(View.VISIBLE);
    }

    /**
     * 选择MV
     */
    protected void toggleMVMode() {
        getMVLayout().setVisibility(View.VISIBLE);
        mRangeSelectionBar.setVisibility(View.VISIBLE);

        mFilterLayout.setVisibility(View.GONE);
        mDubbingLayout.setVisibility(View.GONE);
        getVoiceVolumeConfigView().setVisibility(View.VISIBLE);
        getFilterConfigView().setVisibility(View.GONE);
        mScenceEffectLayout.setVisibility(View.GONE);
        mTimeEffectsLayout.setVisibility(View.GONE);
        mTextEffectsLayout.setVisibility(View.GONE);

    }

    /**
     * 点击配音按钮实现的功能
     */
    protected void toggleDubbingMode() {
        mDubbingLayout.setVisibility(View.VISIBLE);
        getVoiceVolumeConfigView().setVisibility(View.VISIBLE);
        mRangeSelectionBar.setVisibility(View.VISIBLE);

        mFilterLayout.setVisibility(View.GONE);
        mScenceEffectLayout.setVisibility(View.GONE);
        getMVLayout().setVisibility(View.GONE);
        getFilterConfigView().setVisibility(View.GONE);
        mTimeEffectsLayout.setVisibility(View.GONE);
        mTextEffectsLayout.setVisibility(View.GONE);
    }

    /**
     * 切换场景特效模式
     */
    protected void toggleScenceEffectMode() {
        mScenceEffectLayout.setVisibility(View.VISIBLE);

        mRangeSelectionBar.setVisibility(View.GONE);
        mFilterLayout.setVisibility(View.GONE);
        mDubbingLayout.setVisibility(View.GONE);
        getVoiceVolumeConfigView().setVisibility(View.GONE);
        getMVLayout().setVisibility(View.GONE);
        getFilterConfigView().setVisibility(View.GONE);
        mTimeEffectsLayout.setVisibility(View.GONE);
        mTextEffectsLayout.setVisibility(View.GONE);

        applyMediaEffects();
    }

    @Override
    public void onClick(View v) {
        if (v == mBackTextView) {
            finish();
        } else if (v == mActionButton) {
            handleActionButton();

        } else if (v == mSaveButton) {
            startRecording();
        }
    }


    private ConfigViewSeekBar getConfigViewSeekBar(int index) {
        ConfigViewSeekBar configViewSeekBar = getVoiceVolumeConfigView().getSeekBarList().get(index);
        return configViewSeekBar;
    }


    /**
     * 滤镜拖动条监听事件
     */
    private FilterConfigViewSeekBarDelegate mConfigSeekBarDelegate = new FilterConfigViewSeekBarDelegate() {

        @Override
        public void onSeekbarDataChanged(FilterConfigSeekbar seekbar, FilterArg arg) {
            if (arg == null) return;

            if (arg.equalsKey("mixied"))
                mMixiedProgress = arg.getPrecentValue();
        }

    };

    /**
     * 处理开始、暂停事件
     */
    private void handleActionButton() {
        if (!mMovieEditor.getEditorPlayer().isPause()) {
            pausePreview();
        } else {
            startEffectsPreview();
        }
    }

    /**
     * 第一次进入MV页面默认选中无效果MV
     */
    private void selectNormalFilterAndNormalMv() {
        // 选中默认 MV
        StickerAudioEffectCellView firstMVItem = (StickerAudioEffectCellView) mMvListView.getChildAt(0);
        if (firstMVItem == null) return;

        // 选中默认滤镜
        FilterCellView firstFilterItem = (FilterCellView) mFilterListView.getChildAt(0);
        if (firstFilterItem == null) return;

        getFilterListView().setSelectedPosition(0);
    }


    /**
     * 场景特效列表委托
     */
    private SceneEffectListView.SceneEffectListViewDelegate mSceneEffectListViewDelegate = new SceneEffectListView.SceneEffectListViewDelegate() {
        //是否到一次的结尾
        private boolean isEnd = false;

        @Override
        public void onUndo() {
            // 移除场景特效
            if (mScenceEffectLayout.getSceneEffectsTimelineView().lastEffectMode() != null)
                mMovieEditor.getEditorEffector().removeMediaEffectData(mScenceEffectLayout.getSceneEffectsTimelineView().lastEffectMode().getCurrentMediaEffectData());

            mScenceEffectLayout.getSceneEffectsTimelineView().removeLastEffectMode();

        }

        @Override
        public void onPressSceneEffect(String code) {
            /** 开始播放视频并预览已设置的特效 */
            TuSDKMediaSceneEffectData mediaSceneEffectData = new TuSDKMediaSceneEffectData(code);

            //设置ViewModel
            EffectsTimelineView.EffectsTimelineSegmentViewModel sceneEffectModel = new EffectsTimelineView.EffectsTimelineSegmentViewModel("lsq_scence_effect_color_" + code);
            long startTimeUs = mMovieEditor.getEditorPlayer().getCurrentSampleTimeUs();
            if (mMovieEditor.getEditorPlayer().getCurrentSampleTimeUs() == mMovieEditor.getEditorPlayer().getTotalTimeUS()) {
                startTimeUs = 0;
                isEnd = true;
            }
            mediaSceneEffectData.setAtTimeRange(TuSDKTimeRange.makeTimeUsRange(startTimeUs, Long.MAX_VALUE));
            float startProgress = (float) startTimeUs / (float) mMovieEditor.getEditorPlayer().getTotalTimeUS();
            float endProgress = (float) startTimeUs / (float) mMovieEditor.getEditorPlayer().getTotalTimeUS();
            sceneEffectModel.makeProgressRange(startProgress, endProgress);
            sceneEffectModel.setMediaEffectData(mediaSceneEffectData);

            // 按下时启用场景特效编辑功能
            mScenceEffectLayout.setEditable(true);
            mScenceEffectLayout.getSceneEffectsTimelineView().addEffectMode(sceneEffectModel);

            // 预览场景特效
            mMovieEditor.getEditorEffector().addMediaEffectData(mediaSceneEffectData);

            startEffectsPreview();
        }

        @Override
        public void onReleaseSceneEffect(String code) {
            // 松手时场景特效禁用编辑功能
            mScenceEffectLayout.setEditable(false);

//			 取消预览场景特效
            TuSDKMediaSceneEffectData mediaData = (TuSDKMediaSceneEffectData) mScenceEffectLayout.getSceneEffectsTimelineView().lastEffectMode().getCurrentMediaEffectData();
            mediaData.getAtTimeRange().setEndTimeUs(mMovieEditor.getEditorPlayer().getCurrentSampleTimeUs());
            EffectsTimelineView.EffectsTimelineSegmentViewModel model = mScenceEffectLayout.getSceneEffectsTimelineView().lastEffectMode();
            float endProgress = (float) mMovieEditor.getEditorPlayer().getCurrentSampleTimeUs() / (float) mMovieEditor.getEditorPlayer().getTotalTimeUS();
            if (endProgress > model.getProgressRange().getStartProgress() && !(isEnd && endProgress == 1)) {
                model.makeProgressRange(model.getProgressRange().getStartProgress(), endProgress);
                isEnd = false;
            }

            if (mScenceEffectLayout.getSceneEffectsTimelineView().lastEffectMode().getProgressRange() == null) {
                mScenceEffectLayout.getSceneEffectsTimelineView().removeLastEffectMode();
            }

            pausePreview();
        }
    };


    /**
     * 用于监听 MV 裁剪控件
     */
    private OnCursorChangeListener onMVRangeSelectionBarListener = new OnCursorChangeListener() {

        @Override
        public void onSeeekBarChanged(int width, int height) {
            setBarSpace();
        }

        /**
         *
         * @param percent (0 - 100)
         */
        @Override
        public void onLeftCursorChanged(final int percent) {
            if (mMovieEditor != null && mVideoInfo != null) {
                updateMediaEffectsTimeRange();

                mMovieEditor.getEditorPlayer().pausePreview();
                mMovieEditor.getEditorPlayer().seekTimeUs(percent);

            }

            hidePlayCursor();
        }

        @Override
        public void onRightCursorChanged(final int percent) {
            if (mMovieEditor != null) {
                updateMediaEffectsTimeRange();

                mMovieEditor.getEditorPlayer().pausePreview();
            }

            hidePlayCursor();
        }

        @Override
        public void onPlayCursorChanged(int percent) {
        }

        @Override
        public void onLeftCursorUp() {
        }

        @Override
        public void onRightCursorUp() {
        }
    };

    /**
     * 设置裁剪控件开始与结束的最小间隔距离
     */
    public void setBarSpace() {
        if (mVideoInfo == null || mVideoInfo.durationTimeUs <= 0) return;

        if (mRangeSelectionBar != null) {
            /**
             * 需求需要，需设定最小间隔为1秒的
             *
             */
            double percent = (1 / (double) mVideoInfo.durationTimeUs / 1000000l);
            int space = (int) (percent * mRangeSelectionBar.getWidth());
            mRangeSelectionBar.setCursorSpace(space);
        }
    }

    /**
     * 隐藏裁剪控件播放指针
     */
    public void hidePlayCursor() {
        if (mRangeSelectionBar != null) {
            mRangeSelectionBar.setPlaySelection(-1);
            mRangeSelectionBar.setShowPlayCursor(false);
        }
    }

    /* 获取顶部View */
    public View getTopBar() {
        return mTopBar;
    }


    /**
     * 返回到文字之前的特效
     */
    public void backToPreviousEffect() {
        mTabBar.setVisibility(View.VISIBLE);
//		onSelectedTabType(mCurrentTabType);

    }
}