/**
 * TuSDKVideoDemo
 * MovieEditorActivity.java
 *
 * @author  Yanlin
 * @Date  Feb 21, 2017 8:52:11 PM
 * @Copright (c) 2016 tusdk.com. All rights reserved.
 *
 */
package com.upyun.shortvideo.component;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.RectF;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

import com.upyun.shortvideo.Config;
import com.upyun.shortvideo.SimpleCameraActivity;
import com.upyun.shortvideo.utils.Constants;
import com.upyun.shortvideo.views.AudioEffectCellView;
import com.upyun.shortvideo.views.ConfigViewParams;
import com.upyun.shortvideo.views.ConfigViewSeekBar;
import com.upyun.shortvideo.views.DubbingRecodLayout;
import com.upyun.shortvideo.views.EffectsTimelineView;
import com.upyun.shortvideo.views.FilterCellView;
import com.upyun.shortvideo.views.FilterConfigSeekbar;
import com.upyun.shortvideo.views.FilterConfigView;
import com.upyun.shortvideo.views.MagicEffectModel;
import com.upyun.shortvideo.views.MovieEditorTabBar;
import com.upyun.shortvideo.views.MovieRangeSelectionBar;
import com.upyun.shortvideo.views.SceneEffectLayout;
import com.upyun.shortvideo.views.SceneEffectModel;
import com.upyun.shortvideo.views.StickerAudioEffectListView;

import org.lasque.tusdk.api.video.retriever.TuSDKVideoImageExtractor;
import org.lasque.tusdk.core.TuSdk;
import org.lasque.tusdk.core.TuSdkContext;
import org.lasque.tusdk.core.common.TuSDKMediaDataSource;
import org.lasque.tusdk.core.encoder.video.TuSDKVideoEncoderSetting;
import org.lasque.tusdk.core.seles.SelesParameters;
import org.lasque.tusdk.core.seles.SelesParameters.FilterArg;
import org.lasque.tusdk.core.seles.tusdk.FilterWrap;
import org.lasque.tusdk.core.struct.TuSdkSize;
import org.lasque.tusdk.core.utils.ThreadHelper;
import org.lasque.tusdk.core.utils.TuSdkWaterMarkOption.WaterMarkPosition;
import org.lasque.tusdk.core.utils.image.BitmapHelper;
import org.lasque.tusdk.core.video.TuSDKVideoResult;
import org.lasque.tusdk.core.view.recyclerview.TuSdkTableView.TuSdkTableViewItemClickDelegate;
import org.lasque.tusdk.modules.view.widget.sticker.StickerGroup;
import org.lasque.tusdk.modules.view.widget.sticker.StickerLocalPackage;
import org.lasque.tusdk.video.editor.TuSDKMediaAudioEffectData;
import org.lasque.tusdk.video.editor.TuSDKMediaStickerAudioEffectData;
import org.lasque.tusdk.video.editor.TuSDKMediaStickerEffectData;
import org.lasque.tusdk.video.editor.TuSDKMovieEditor;
import org.lasque.tusdk.video.editor.TuSDKMovieEditor.TuSDKMovieEditorDelegate;
import org.lasque.tusdk.video.editor.TuSDKMovieEditor.TuSDKMovieEditorSoundStatus;
import org.lasque.tusdk.video.editor.TuSDKMovieEditor.TuSDKMovieEditorStatus;
import org.lasque.tusdk.video.editor.TuSDKMovieEditorOptions;
import org.lasque.tusdk.video.editor.TuSDKTimeRange;

import com.upyun.shortvideo.utils.VideoInfoUtils;
import com.upyun.shortvideo.views.CompoundConfigView;
import com.upyun.shortvideo.views.DubbingLayout;
import com.upyun.shortvideo.views.FilterListView;
import com.upyun.shortvideo.views.SceneEffectListView;
import com.upyun.shortvideo.views.StickerAudioEffectCellView;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 视频编辑示例
 *
 * 功能：
 * 1. 预览视频，添加滤镜，场景特效查看效果
 * 2. 导出新的视频
 *
 * @author Yanlin
 */
public class MovieEditorActivity extends SimpleCameraActivity implements View.OnClickListener,
		MovieEditorTabBar.MovieEditorTabBarDelegate, TuSDKVideoImageExtractor.TuSDKVideoImageExtractorDelegate, TuSDKMovieEditorDelegate
{

    /************************************  SDK  ************************************ /

     /** 视频路径 */
    protected String mVideoPath;
    /** 视频裁切区域 */
    protected TuSDKTimeRange mCutTimeRange;
     /** 编辑器 */
    protected TuSDKMovieEditor mMovieEditor;

	/************************************  Views  ************************************ /

	/** 底部 TabBar */
	protected MovieEditorTabBar mTabBar;
	/** 保存按钮 */
	protected TextView mSaveButton;
	/** 标题  TextView */
	private TextView mTitleTextView;
	/** 返回按钮 */
	protected TextView mBackTextView;
    /** 开始播放按钮 */
    protected Button mActionButton;
    /** 滤镜栏 */
	private FilterListView	mFilterListView;
	/** 滤镜参数调节栏 */
    private FilterConfigView mConfigView;
	/** 贴纸栏视图 */
	protected StickerAudioEffectListView mMvListView;

    protected LinearLayout mFilterLayout;

	/** 视频裁剪控件 */
	protected MovieRangeSelectionBar mRangeSelectionBar;

	// == 配音 == /

	/** 配音列表视图 */
	protected DubbingLayout mDubbingLayout;
	/** 录制配音视图 */
	protected DubbingRecodLayout mDubbingRecodLayout;

	/** 声音强度调节栏 */
	private CompoundConfigView mVoiceVolumeConfigView;

    // == 场景特效 == /

	/* 场景特效视图 */
	protected SceneEffectLayout mScenceEffectLayout;

    /************************************  辅助信息  ************************************ /

    /** 记录缩略图列表容器 */
    private List<Bitmap> mVideoThumbList;
    /** 用于记录当前调节栏磨皮系数 */
    private float mSmoothingProgress = -1.0f;
    /** 用于记录当前调节栏效果系数 */
    private float mMixiedProgress = -1.0f;

    /** 用于记录焦点位置 */
    public int mFocusPostion = 1;

	/** MV音效资源  */
	@SuppressLint("UseSparseArrays")
	private Map<Integer, Integer> mMusicMap = new HashMap<Integer, Integer>();

    protected int getLayoutId()
    {
    	return com.upyun.shortvideo.R.layout.movie_editor_activity;
    }

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		setContentView(getLayoutId());

		mSaveButton = findViewById(com.upyun.shortvideo.R.id.lsq_next);
		mSaveButton.setText(com.upyun.shortvideo.R.string.lsq_save);
		mSaveButton.setOnClickListener(this);
		mSaveButton.setEnabled(false);

		mTitleTextView = this.findViewById(com.upyun.shortvideo.R.id.lsq_title);
		mTitleTextView.setText(com.upyun.shortvideo.R.string.lsq_add_filter);
        mActionButton = findViewById(com.upyun.shortvideo.R.id.lsq_actButton);
        mActionButton.setVisibility(View.INVISIBLE);
		mActionButton.setOnClickListener(this);

        mBackTextView = findViewById(com.upyun.shortvideo.R.id.lsq_back);
        mBackTextView.setOnClickListener(this);


        mRangeSelectionBar =  this.findViewById(com.upyun.shortvideo.R.id.lsq_rangeseekbar);
        mRangeSelectionBar.setShowPlayCursor(false);
        mRangeSelectionBar.setType(MovieRangeSelectionBar.Type.MV);
        mRangeSelectionBar.setLeftSelection(0);
        mRangeSelectionBar.setPlaySelection(0);
        mRangeSelectionBar.setRightSelection(100);
        mRangeSelectionBar.setOnCursorChangeListener(mOnCursorChangeListener);

		hideNavigationBar();
        // 设置弹窗提示是否在隐藏虚拟键的情况下使用
        TuSdk.messageHub().applyToViewWithNavigationBarHidden(true);

        setCameraViewSize(TuSdkContext.getScreenSize().width,TuSdkContext.getScreenSize().width);

        Intent intent = getIntent();
		mVideoPath = intent.getStringExtra("videoPath");

		// 视频裁切区域时间
		mCutTimeRange = TuSDKTimeRange.makeRange(intent.getFloatExtra("startTime", 0) / (float)1000, intent.getFloatExtra("endTime", 0) / (float)1000);

		// 如果没有传递开始和结束时间，默认视频编辑时长为总时长
		if(mCutTimeRange.duration() == 0 && mVideoPath != null)
		{
			mCutTimeRange = TuSDKTimeRange.makeRange(0, VideoInfoUtils.getVideoDuration(mVideoPath));
		}

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

		// 加载视频缩略图
        loadVideoThumbList();

		updateSelectedTabType();
	}

	/**
	 * 更新底部栏选中类型
	 */
	protected void updateSelectedTabType()
	{
		// 底部栏默认选中滤镜
		getTabBar().updateButtonStatus(getTabBar().getFilterTab(), true);
		getTabBar().getDelegate().onSelectedTabType(MovieEditorTabBar.TabType.FilterTabType);
	}


    @Override
    protected void onResume()
    {
        hideNavigationBar();
        super.onResume();

        startEffectsPreview();
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        TuSdk.messageHub().dismissRightNow();

        if (mMovieEditor.isPreviewing())
            mMovieEditor.stopPreview();
        else
            mMovieEditor.cancelRecording();
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();

        if (mMovieEditor != null)
            mMovieEditor.destroy();

        if (mRangeSelectionBar != null)
            mRangeSelectionBar.clearVideoThumbList();

        if (mScenceEffectLayout != null)
            mScenceEffectLayout.getSceneEffectsTimelineView().clearVideoThumbList();

        if(mVideoThumbList != null)
        {
            for (Bitmap bitmap : mVideoThumbList)
                BitmapHelper.recycled(bitmap);

            mVideoThumbList.clear();
            mVideoThumbList = null;
        }

		MediaEffectsManager.getMediaEffectManager().clearMediaEffectList();

    }

    /************************************  SDK  ************************************ /
     *
     */
	/**
	 * 初始化 TuSDKMovieEditor
	 */
	protected void initMovieEditor()
	{
        float movieLeft = getIntent().getFloatExtra("movieLeft", 0.0f);
        float movieTop = getIntent().getFloatExtra("movieTop", 0.0f);
        float movieRight = getIntent().getFloatExtra("movieRight", 1.0f);
        float movieBottom = getIntent().getFloatExtra("movieBottom", 1.0f);

        boolean isRatioAdaption = getIntent().getBooleanExtra("ratioAdaption", true);

        TuSDKMovieEditorOptions defaultOptions = TuSDKMovieEditorOptions.defaultOptions();
        defaultOptions.setMoviePath(mVideoPath)
                .setCutTimeRange(mCutTimeRange)
                // 是否需要按原视频比例显示
                .setOutputRegion(isRatioAdaption ? null : new RectF(movieLeft,movieTop, movieRight, movieBottom) )
                .setIncludeAudioInVideo(true) // 设置是否保存或者播放原音
                .setLoopingPlay(true) // 设置是否循环播放视频
                .setAutoPlay(true) // 设置视频加载完成后是否自动播放
                .setClearAudioDecodeCacheInfoOnDestory(false); // 设置MovieEditor销毁时是否自动清除缓存音频解码信息


        mMovieEditor = new TuSDKMovieEditor(this.getBaseContext(), getCameraView(), defaultOptions);

		// 视频原音音量
		mMovieEditor.setVideoSoundVolume(1f);

		// 设置水印，默认为空
		mMovieEditor.setWaterMarkImage(BitmapHelper.getBitmapFormRaw(this, com.upyun.shortvideo.R.raw.sample_watermark));
		mMovieEditor.setWaterMarkPosition(WaterMarkPosition.TopRight);
		mMovieEditor.setDelegate(this);

//		TuSDKVideoEncoderSetting encoderSetting = mMovieEditor.getVideoEncoderSetting();
//		encoderSetting.videoSize = TuSdkSize.create(Config.EDITORWIDTH, Config.EDITORHEIGHT);
//		encoderSetting.videoQuality = TuSDKVideoEncoderSetting.VideoQuality.RECORD_LOW1.setBitrate(Config.EDITORBITRATE*1000).setFps(Config.EDITORFPS);

		mMovieEditor.loadVideo();

	}

    /**
     * 切换滤镜
     *
     * @param code
     * 			滤镜code
     */
    protected void switchFilter(String code)
    {
    	// 切换滤镜前, 将场景特效和魔法效果全部清空
		MediaEffectsManager.getMediaEffectManager().setSceneEffectDataList(new ArrayList<SceneEffectModel>());
		MediaEffectsManager.getMediaEffectManager().setMagicEffectDataList(new ArrayList<MagicEffectModel>());

		// 切换滤镜前必须打开视频预览, 滤镜切换依赖于视频的编解码
		// 如果视频暂停情况下切换滤镜会导致切换失败，onFilterChanged方法也不会回调
		startEffectsPreview();
		mMovieEditor.switchFilter(code);
    }

	/**
	 * 更新特效播放时间
	 */
	private void updateMediaEffectsTimeRange()
    {
        float startTime = mRangeSelectionBar.getLeftCursorPercent() * mCutTimeRange.duration() / 100;
		float endTime = mRangeSelectionBar.getRightCursorPercent() * mCutTimeRange.duration() / 100;

        TuSDKTimeRange timeRange = TuSDKTimeRange.makeRange(startTime, endTime);

        // 设置音频特效播放区间
        if (MediaEffectsManager.getMediaEffectManager().getAudioEffectData() != null)
            MediaEffectsManager.getMediaEffectManager().getAudioEffectData().setAtTimeRange(timeRange);

        // 设置贴纸特效播放区间
        if (MediaEffectsManager.getMediaEffectManager().getStickerAudioEffectData() != null)
            MediaEffectsManager.getMediaEffectManager().getStickerAudioEffectData().setAtTimeRange(timeRange);
    }

	/**
	 * 启动视频预览
	 */
	protected void startPreView()
	{
		if (mMovieEditor == null || mMovieEditor.isPreviewing()) return;

		mMovieEditor.removeAllMediaEffects();

		mMovieEditor.startPreview();
	}

	/**
	 * 开始播放视频并预览设置的特效
	 */
	protected void startEffectsPreview()
	{
		if (mMovieEditor == null || mMovieEditor.isPreviewing()) return;

		// 添加设置的特效信息
		applyMediaEffects();

		mMovieEditor.startPreview();
	}

	/**
	 * 暂停预览
	 */
	protected void pausePreview()
	{
		if (mMovieEditor == null) return;

		mMovieEditor.pausePreview();
	}

	/**
	 * 应用用户设置的所有特效
	 */
	protected void applyMediaEffects()
	{
        updateMediaEffectsTimeRange();

        // 设置特效数据
        mMovieEditor.setMediaEffectList(MediaEffectsManager.getMediaEffectManager().getAllMediaEffectList());
	}

	/**
	 * 启动录制
	 */
	private void startRecording()
	{
		if (mMovieEditor == null || mMovieEditor.isRecording()) return;

		String msg = getStringFromResource("new_movie_saving");
		TuSdk.messageHub().setStatus(MovieEditorActivity.this, msg);

        // 设置特效数据
		applyMediaEffects();

		// 生成视频文件
		mMovieEditor.startRecording();

	}

    /**
     * 视频处理完成
     *
     * @param result
     *            生成的新视频信息，预览时该对象为 null
     */
    @Override
    public void onMovieEditComplete(TuSDKVideoResult result)
    {
        String msg = result == null ? getStringFromResource("new_movie_error_saving")
                : getStringFromResource("new_movie_saved");
        TuSdk.messageHub().showError(MovieEditorActivity.this, msg);

        setResult(RESULT_OK);
        finish();
    }

	/**
	 * 视频处理进度事件
	 *
	 * @param durationTimes
	 *            当前时间 单位：秒
	 * @param progress
	 * 				当前进度
	 */
	@Override
    public void onMovieEditProgressChanged(float durationTimes, float progress)
    {
        if(!mRangeSelectionBar.isShowPlayCursor())
            mRangeSelectionBar.setShowPlayCursor(true);

        mRangeSelectionBar.setPlaySelection((int)(progress * 100));

        mScenceEffectLayout.getSceneEffectsTimelineView().setProgress(progress);
        mScenceEffectLayout.getSceneEffectsTimelineView().updateLastEffectModelEndTime(durationTimes);

    }

	/**
	 *  TuSDKMovieEditor 状态改变通知
	 *
	 * @param status TuSDKMovieEditorStatus
	 *
	 */
	@Override
    public void onMovieEditorStatusChanged(TuSDKMovieEditorStatus status)
    {
        TuSdk.messageHub().dismissRightNow();

        mActionButton.setVisibility((status == TuSDKMovieEditorStatus.Previewing || status == TuSDKMovieEditorStatus.Recording) ? View.INVISIBLE : View.VISIBLE);

        switch (status)
        {
            case Loaded:

                // 首次进入时，选中 MV 和滤镜默认效果
                selectNormalFilterAndNormalMv();

                mSaveButton.setEnabled(true);

            break;
            case Recording:
                TuSdk.messageHub().setStatus(MovieEditorActivity.this,getStringFromResource("new_movie_saving"));
            break;
            case LoadVideoFailed:
                TuSdk.messageHub().showError(MovieEditorActivity.this, getStringFromResource("lsq_loadvideo_failed"));
             break;
            case RecordingFailed:
                mMovieEditor.stopPreview();
                TuSdk.messageHub().showError(MovieEditorActivity.this, getStringFromResource("new_movie_error_saving"));
                 break;
        }

    }

    /**
     * 视频原音和音效状态
     */
    @Override
    public void onMovieEditorSoundStatusChanged(TuSDKMovieEditorSoundStatus status)
    {
        TuSdk.messageHub().dismissRightNow();

        if(status == TuSDKMovieEditorSoundStatus.Loading)
        {
            String msg = getStringFromResource("new_movie_audio_effect_loading");
            TuSdk.messageHub().setStatus(MovieEditorActivity.this, msg);
        }
    }

	/**
	 * 滤镜改变通知
	 *
	 * @param selesOutInput
	 *
	 */
	@Override
    public void onFilterChanged(final FilterWrap selesOutInput)
    {
		if (selesOutInput == null) return;

        SelesParameters params = selesOutInput.getFilterParameter();
        List<FilterArg> list = params.getArgs();
        for (FilterArg arg : list)
        {
            if (arg.equalsKey("smoothing") && mSmoothingProgress !=  -1.0f)
                arg.setPrecentValue(mSmoothingProgress);
            else if (arg.equalsKey("smoothing") && mSmoothingProgress == -1.0f)
                mSmoothingProgress = arg.getPrecentValue();
            else if (arg.equalsKey("mixied") && mMixiedProgress !=  -1.0f)
                arg.setPrecentValue(mMixiedProgress);
            else if (arg.equalsKey("mixied") && mMixiedProgress == -1.0f)
                mMixiedProgress = arg.getPrecentValue();

        }
        selesOutInput.setFilterParameter(params);

		getFilterConfigView().setSelesFilter(selesOutInput.getFilter());
    }

    /**
     * 获取底部TabBar
     *
     * @return
     */
	public MovieEditorTabBar getTabBar()
	{
	    if (mTabBar == null)
        {
            mTabBar = findViewById(com.upyun.shortvideo.R.id.lsq_bottom_navigator);
            mTabBar.loadView();
        }

		return mTabBar;
	}

	/**
	 * 加载场景特效视图
	 */
	private void initScenceEffectLayout()
	{
		getScenceEffectLayout().getSceneEffectsTimelineView().setDuration(mCutTimeRange.duration());
		getScenceEffectLayout().setDelegate(mSceneEffectListViewDelegate);
		getScenceEffectLayout().setDelegate(mEffectsTimelineViewDelegate);

	}

	public SceneEffectLayout getScenceEffectLayout()
	{
		if (mScenceEffectLayout == null)
		{
			mScenceEffectLayout = findViewById(com.upyun.shortvideo.R.id.lsq_scence_effect_layout);
			mScenceEffectLayout.loadView();
		}

		return mScenceEffectLayout;
	}

	/**
	 * 初始化音效视图
	 */
	private void initDubbingLayout()
	{
        mDubbingLayout = findViewById(com.upyun.shortvideo.R.id.lsq_dubbing_wrap);
		mDubbingLayout.loadView();

		mDubbingLayout.getDubbingListView().setBackgroundColor(TuSdkContext.getColor("lsq_color_white"));
		mDubbingLayout.getDubbingListView().setItemClickDelegate(mMixingTableItemClickDelegate);

		mDubbingRecodLayout = findViewById(com.upyun.shortvideo.R.id.lsq_editor_dubbing_record_layout);
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
			public void onRecordCompleted(File file)
			{

				mMovieEditor.setVideoSoundVolume(1.0f);
				getConfigViewSeekBar(0).setProgress(1.0f);

				if (file != null)
				{
					applyAudioEffect(Uri.fromFile(file));
					startEffectsPreview();
				}

			}

			/**
			 * 开始录制
			 */
			@Override
			public void onRecordStarted()
			{
				mMovieEditor.stopPreview();
				startEffectsPreview();
			}

			/**
			 * 录制已停止
			 */
			@Override
			public void onRecordStoped() {

				mMovieEditor.stopPreview();
			}
		});

		getVoiceVolumeConfigView();

	}

	protected CompoundConfigView getVoiceVolumeConfigView()
	{
		if (mVoiceVolumeConfigView == null)
		{
			mVoiceVolumeConfigView = (CompoundConfigView) findViewById(com.upyun.shortvideo.R.id.lsq_voice_volume_config_view);
			mVoiceVolumeConfigView.setBackgroundResource(com.upyun.shortvideo.R.color.lsq_alpha_white_99);
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
	private ConfigViewSeekBar.ConfigSeekbarDelegate mFilterConfigSeekbarDelegate = new ConfigViewSeekBar.ConfigSeekbarDelegate()
	{

		@Override
		public void onSeekbarDataChanged(ConfigViewSeekBar seekbar, ConfigViewParams.ConfigViewArg arg)
		{
			if(arg.getKey().equals("originIntensity"))
			{
				mMovieEditor.setVideoSoundVolume(arg.getPercentValue());
			}
			else if(arg.getKey().equals("dubbingIntensity"))
			{
				if (MediaEffectsManager.getMediaEffectManager().getAudioEffectData() == null) return;

                MediaEffectsManager.getMediaEffectManager().getAudioEffectData().setVolume(arg.getPercentValue());
			}
		}
	};


	/**
	 * 加载视频缩略图
	 */
	public void loadVideoThumbList()
	{

		if ( (mRangeSelectionBar != null || mRangeSelectionBar.getVideoThumbList().size() == 0)
				&& (mScenceEffectLayout != null || mRangeSelectionBar.getVideoThumbList().size() == 0))
		{

			TuSdkSize tuSdkSize = TuSdkSize.create(TuSdkContext.dip2px(56), TuSdkContext.dip2px(56));
			TuSDKVideoImageExtractor extractor = TuSDKVideoImageExtractor.createExtractor();
			extractor.setOutputImageSize(tuSdkSize);
			extractor.setVideoDataSource(TuSDKMediaDataSource.create(mVideoPath));
			extractor.setExtractFrameCount(6);
			extractor.setTimeRange(mCutTimeRange);

			extractor.asyncExtractImageList(this);

		}

	}

	@Override
	public void onVideoImageListDidLoaded(List<Bitmap> images) {
	}

	@Override
	public void onVideoNewImageLoaded(Bitmap bitmap)
	{
		mRangeSelectionBar.drawVideoThumb(bitmap);
		mScenceEffectLayout.getSceneEffectsTimelineView().drawVideoThumb(bitmap);
	}


	protected RelativeLayout getCameraView()
	{
		RelativeLayout cameraView =  (RelativeLayout)findViewById(com.upyun.shortvideo.R.id.lsq_cameraView);

		return cameraView;
	}

	/**
	 * 设置视频播放区域大小
	 */
	protected void setCameraViewSize(int width,int height)
	{
		LayoutParams lp = (LayoutParams) getCameraView().getLayoutParams();
		lp.width = width;
		lp.height = height;
	}

	/**
	 * 初始化MV贴纸视图
	 */
	protected void initMVLayout()
	{
		getMVLayout().setBackgroundColor(TuSdkContext.getColor("lsq_color_white"));

		getMvListView();

		if (mMvListView == null) return;

		mMusicMap.put(1420, com.upyun.shortvideo.R.raw.lsq_audio_cat);
		mMusicMap.put(1427, com.upyun.shortvideo.R.raw.lsq_audio_crow);
		mMusicMap.put(1432, com.upyun.shortvideo.R.raw.lsq_audio_tangyuan);
		mMusicMap.put(1446, com.upyun.shortvideo.R.raw.lsq_audio_children);

		List<StickerGroup> groups = new ArrayList<StickerGroup>();
		List<StickerGroup> smartStickerGroups = StickerLocalPackage.shared().getSmartStickerGroups(false);

		for (StickerGroup smartStickerGroup : smartStickerGroups)
		{
			if (mMusicMap.containsKey((int)smartStickerGroup.groupId))
				groups.add(smartStickerGroup);
		}

		groups.add(0,new StickerGroup());
		this.mMvListView.setModeList(groups);
	}


	public RelativeLayout getMVLayout()
	{
		RelativeLayout mvLayoutWrap = (RelativeLayout) findViewById(com.upyun.shortvideo.R.id.filter_mv_wrap_layout);
		return mvLayoutWrap;
	}

	/**
	 * 贴纸组视图
	 */
	private StickerAudioEffectListView getMvListView()
	{
		if (mMvListView == null)
		{
			mMvListView = findViewById(com.upyun.shortvideo.R.id.lsq_mv_list_view);
			mMvListView.loadView();
			mMvListView.setCellLayoutId(com.upyun.shortvideo.R.layout.movie_editor_sticker_audio_effect_cell_view);
			mMvListView.setItemClickDelegate(mMvTableItemClickDelegate);
			mMvListView.reloadData();
		}

		return mMvListView;
	}

	/**
	 *  混音列表点击事件
	 */
	private TuSdkTableViewItemClickDelegate<AudioEffectCellView.AudioEffectEntity, AudioEffectCellView> mMixingTableItemClickDelegate = new TuSdkTableViewItemClickDelegate<AudioEffectCellView.AudioEffectEntity, AudioEffectCellView>()
	{
		@Override
		public void onTableViewItemClick(AudioEffectCellView.AudioEffectEntity itemData, AudioEffectCellView itemView,final int position)
		{
			mDubbingLayout.getDubbingListView().setSelectedPosition(position);

			// 停止预览
			mMovieEditor.stopPreview();

			ThreadHelper.post(new Runnable()
			{
				@Override
				public void run()
				{
					// 应用配音特效
					changeAudioEffect(position);
				}
			});
		}
	};

	/**
	 * MV 列表点击事件
	 */
	private TuSdkTableViewItemClickDelegate<StickerGroup, StickerAudioEffectCellView> mMvTableItemClickDelegate = new TuSdkTableViewItemClickDelegate<StickerGroup, StickerAudioEffectCellView>()
	{
		@Override
		public void onTableViewItemClick(final StickerGroup itemData, StickerAudioEffectCellView itemView,final int position)
		{
			getMvListView().setSelectedPosition(position);

			// 选中当前 MV
			mMovieEditor.stopPreview();

			ThreadHelper.post(new Runnable()
			{
				@Override
				public void run()
				{
					// 应用 MV 特效
					changeMvEffect(position, itemData);
				}
			});
		}
	};


	/** 滤镜组列表点击事件 */
	private TuSdkTableViewItemClickDelegate<String, FilterCellView> mFilterTableItemClickDelegate = new TuSdkTableViewItemClickDelegate<String, FilterCellView>()
	{
		@Override
		public void onTableViewItemClick(String itemData,FilterCellView itemView, int position)
		{
			startPreView();

			mFocusPostion = position;
			mFilterListView.setSelectedPosition(mFocusPostion);

			getFilterConfigView().setVisibility((position == 0)?View.INVISIBLE:View.VISIBLE);

			switchFilter(itemData);
		}
	};

 	/**
 	 * 应用背景音乐特效
 	 *
 	 * @param position
 	 */
    protected void changeAudioEffect(int position)
    {
    	if (position == 0)
    	{
    		mMovieEditor.removeAllMediaEffects();
    		setDubbingSeekbarProgress(0.0f);
    	}
    	else if (position == 1)
    	{
    		handleAudioRecordBtn();
    		return;
    	}
    	else if (position > 1)
    	{

			Uri uri = Uri.parse("android.resource://" + getPackageName() + "/" + Constants.AUDIO_EFFECTS[position-2]);

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
	private void applyAudioEffect(Uri audioPathUri)
	{
		if (audioPathUri == null) return;

        TuSDKMediaAudioEffectData  audioEffectData = new TuSDKMediaAudioEffectData(TuSDKMediaDataSource.create(audioPathUri));

        MediaEffectsManager.getMediaEffectManager().setAudioEffectData(audioEffectData);

        applyMediaEffects();

        setDubbingSeekbarProgress(1.0f);

	}

	/** 处理自己录音按钮操作 */
    private void handleAudioRecordBtn()
    {
		mDubbingRecodLayout.setVisibility(View.VISIBLE);

		// 设置最大录制时长
		mDubbingRecodLayout.setMaxRecordTime(mCutTimeRange.duration());
		mDubbingRecodLayout.updateAudioProgressBar(0);

    	// 将原音强度设为0
    	mMovieEditor.setVideoSoundVolume(0.0f);

    	getConfigViewSeekBar(0).setProgress(0.0f);

    }

	/**
	 * 应用MV特效
	 * @param position
	 * @param itemData
	 */
	protected void changeMvEffect(int position, StickerGroup itemData)
	{
		if (position < 0) return;

		if (position == 0 )
		{
			MediaEffectsManager.getMediaEffectManager().setStickerAudioEffectData(null);
		}
		else
		{
			int groupId = (int) itemData.groupId;

			if (mMusicMap!=null && mMusicMap.containsKey(groupId))
			{
				Uri	uri = Uri.parse("android.resource://" + getPackageName() + "/" + mMusicMap.get(groupId));

                MediaEffectsManager.getMediaEffectManager().setStickerAudioEffectData(new TuSDKMediaStickerAudioEffectData(TuSDKMediaDataSource.create(uri), itemData));
			}
			else
			{
                MediaEffectsManager.getMediaEffectManager().setStickerEffectData(new TuSDKMediaStickerEffectData(itemData));
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
    public void setDubbingSeekbarProgress(float progress)
    {
		getConfigViewSeekBar(1).setProgress(progress);
    }

	/**
	 * 初始化滤镜栏视图
	 */
	protected void initFilterLayout()
	{
		mFilterLayout =(LinearLayout) findViewById(com.upyun.shortvideo.R.id.lsq_filter_layout);
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
	private FilterListView getFilterListView()
	{
		if (mFilterListView == null)
		{
			mFilterListView = (FilterListView) findViewById(com.upyun.shortvideo.R.id.lsq_filter_list_view);

			if (mFilterListView == null) return null;

			mFilterListView.loadView();
			mFilterListView.setCellLayoutId(com.upyun.shortvideo.R.layout.filter_list_cell_view);
			mFilterListView.setCellWidth(TuSdkContext.dip2px(62));
			mFilterListView.setItemClickDelegate(mFilterTableItemClickDelegate);
			mFilterListView.reloadData();
			mFilterListView.selectPosition(mFocusPostion);
		}
		return mFilterListView;
	}

	protected FilterConfigView getFilterConfigView()
	{
		if (mConfigView == null)
		{
			mConfigView = (FilterConfigView)findViewById(com.upyun.shortvideo.R.id.lsq_filter_config_view);
			mConfigView.setBackgroundResource(com.upyun.shortvideo.R.color.lsq_alpha_white_99);
			mConfigView.setSeekBarDelegate(mConfigSeekBarDelegate);
//			getFilterConfigView().invalidate();
		}

		return mConfigView;
	}

    /**
     * 切换Tab事件
     *
     * @param tabType
     */
    @Override
    public void onSelectedTabType(MovieEditorTabBar.TabType tabType) {

	    switch (tabType)
        {
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
        }
    }

	/**
	 * 选择滤镜
	 */
	protected void toggleFilterMode()
	{
		switchFilter(Constants.EDITORFILTERS[mFocusPostion]);
		mFilterLayout.setVisibility(View.VISIBLE);

		getMVLayout().setVisibility(View.GONE);
		getVoiceVolumeConfigView().setVisibility(View.INVISIBLE);
		mScenceEffectLayout.setVisibility(View.GONE);
		mRangeSelectionBar.setVisibility(View.GONE);
		mDubbingLayout.setVisibility(View.GONE);

		if (mFocusPostion == 0)
			getFilterConfigView().setVisibility(View.GONE);
		else
		getFilterConfigView().setVisibility(View.VISIBLE);
	}

	/**
	 * 选择MV
	 */
	protected void toggleMVMode()
	{
		getMVLayout().setVisibility(View.VISIBLE);
		mRangeSelectionBar.setVisibility(View.VISIBLE);

		mFilterLayout.setVisibility(View.GONE);
		mDubbingLayout.setVisibility(View.GONE);
		getVoiceVolumeConfigView().setVisibility(View.VISIBLE);
		getFilterConfigView().setVisibility(View.GONE);
		mScenceEffectLayout.setVisibility(View.GONE);

	}

	/**
	 * 点击配音按钮实现的功能
	 */
	protected void toggleDubbingMode()
	{
		mDubbingLayout.setVisibility(View.VISIBLE);
		getVoiceVolumeConfigView().setVisibility(View.VISIBLE);
		mRangeSelectionBar.setVisibility(View.VISIBLE);

		mFilterLayout.setVisibility(View.GONE);
		mScenceEffectLayout.setVisibility(View.GONE);
		getMVLayout().setVisibility(View.GONE);
		getFilterConfigView().setVisibility(View.GONE);

	}

	/**
	 * 切换场景特效模式
	 */
	protected void toggleScenceEffectMode()
	{
		mScenceEffectLayout.setVisibility(View.VISIBLE);

		mRangeSelectionBar.setVisibility(View.GONE);
		mFilterLayout.setVisibility(View.GONE);
		mDubbingLayout.setVisibility(View.GONE);
		getVoiceVolumeConfigView().setVisibility(View.GONE);
		getMVLayout().setVisibility(View.GONE);
		getFilterConfigView().setVisibility(View.GONE);

        /**  设置场景特效 */
		MediaEffectsManager.getMediaEffectManager().setSceneEffectDataList(mScenceEffectLayout.getSceneEffectsTimelineView().getAllMediaEffectData());

		applyMediaEffects();
	}

    @Override
    public void onClick(View v)
    {
			if(v == mBackTextView)
			{
                finish();
            }
		   else if(v == mActionButton)
		  {
		  		handleActionButton();

		  }else if(v == mSaveButton)
			{
				startRecording();
			}
    }


	 private ConfigViewSeekBar getConfigViewSeekBar(int index)
	 {
		 ConfigViewSeekBar configViewSeekBar = getVoiceVolumeConfigView().getSeekBarList().get(index);
		 return configViewSeekBar;
	 }


    /** 滤镜拖动条监听事件 */
    private FilterConfigView.FilterConfigViewSeekBarDelegate mConfigSeekBarDelegate = new FilterConfigView.FilterConfigViewSeekBarDelegate()
    {

		@Override
		public void onSeekbarDataChanged(FilterConfigSeekbar seekbar, FilterArg arg)
		{
			if (arg == null) return;

    		if (arg.equalsKey("mixied"))
    			mMixiedProgress = arg.getPrecentValue();
		}

    };

    /**
     * 处理开始、暂停事件
     */
    private void handleActionButton()
    {
		if (mMovieEditor.isPreviewing())
		{
			pausePreview();
		}
		else
		{
			startEffectsPreview();
		}
    }

    /**
     * 第一次进入MV页面默认选中无效果MV
     */
    private void selectNormalFilterAndNormalMv()
    {
    	// 选中默认 MV
		StickerAudioEffectCellView firstMVItem = (StickerAudioEffectCellView) mMvListView.getChildAt(0);
		if(firstMVItem == null) return;

		// 选中默认滤镜
		FilterCellView firstFilterItem = (FilterCellView) mFilterListView.getChildAt(0);
		if(firstFilterItem == null) return;

		getFilterListView().setSelectedPosition(0);
    }


	/**
	 * 场景特效列表委托
	 */
	private SceneEffectListView.SceneEffectListViewDelegate mSceneEffectListViewDelegate = new SceneEffectListView.SceneEffectListViewDelegate()
	{

		@Override
		public void onUndo()
		{
            // 移除场景特效
			mMovieEditor.removeMediaEffect(mScenceEffectLayout.getSceneEffectsTimelineView().lastEffectMode());

			mScenceEffectLayout.getSceneEffectsTimelineView().removeLastEffectMode();

		}

		@Override
		public void onPressSceneEffect(String code)
		{
            startPreView();

            SceneEffectModel sceneEffectModel = new SceneEffectModel(code);
            sceneEffectModel.setAtTimeRange(TuSDKTimeRange.makeTimeUsRange(mMovieEditor.getCurrentSampleTimeUs(),0));

			// 按下时启用场景特效编辑功能
			mScenceEffectLayout.setEditable(true);
			mScenceEffectLayout.getSceneEffectsTimelineView().addEffectMode(sceneEffectModel);

            mMovieEditor.setLooping(false);

            // 预览场景特效 (不会将特效添加到 MovieEditor 中)
            mMovieEditor.applyMediaEffect(sceneEffectModel);

        }

		@Override
		public void onReleaseSceneEffect(String code)
		{
			// 松手时场景特效禁用编辑功能
			mScenceEffectLayout.setEditable(false);

			mMovieEditor.setLooping(true);
			// 取消预览场景特效
			mMovieEditor.unApplyMediaEffect(mScenceEffectLayout.getSceneEffectsTimelineView().lastEffectMode());

            pausePreview();

        }
	};

	/** 场景特效时间轴变化 */
	protected EffectsTimelineView.EffectsTimelineViewDelegate mEffectsTimelineViewDelegate = new EffectsTimelineView.EffectsTimelineViewDelegate()
    {
        @Override
        public void onProgressCursorWillChaned()
        {
            mMovieEditor.stopPreview();
        }

        @Override
        public void onProgressChaned(float progress)
        {
            if (mMovieEditor.getTimeRange() != null && mMovieEditor.getTimeRange().isValid())
                mMovieEditor.seekTimeUs((long)(mMovieEditor.getTimeRange().durationTimeUS() * progress));

        }

		@Override
		public void onEffectNumChanged(int effectNum)
		{
			mScenceEffectLayout.getSceneEffectListView().updateUndoButtonState(effectNum == 0 ? false :true);
		}
	};

	/** 用于监听裁剪控件  */
	private MovieRangeSelectionBar.OnCursorChangeListener mOnCursorChangeListener = new MovieRangeSelectionBar.OnCursorChangeListener()
	{

		@Override
		public void onSeeekBarChanged(int width, int height)
		{
			setBarSpace();
		}

		/**
		 *
		 * @param percent (0 - 100)
		 */
		@Override
		public void onLeftCursorChanged(final int percent)
		{
			if (mMovieEditor != null)
			{
                updateMediaEffectsTimeRange();

				mMovieEditor.seekTimeUs((long)(percent * mCutTimeRange.durationTimeUS() / 100));
                mMovieEditor.stopPreview();
			}

			hidePlayCursor();
		}

        @Override
        public void onRightCursorChanged(final int percent)
        {
            if (mMovieEditor != null)
            {
                updateMediaEffectsTimeRange();

                mMovieEditor.stopPreview();
            }

            hidePlayCursor();
        }

        @Override
		public void onPlayCursorChanged(int percent){}
		@Override
		public void onLeftCursorUp() {}
		@Override
		public void onRightCursorUp() {}
	};

	/** 设置裁剪控件开始与结束的最小间隔距离 */
	public void setBarSpace()
	{
		if(mCutTimeRange.duration() == 0 ) return;
		if(mRangeSelectionBar!=null)
		{
			/**
			 * 需求需要，需设定最小间隔为1秒的
			 *
			 */
			double percent = (1/mCutTimeRange.duration());
			int space = (int) (percent*mRangeSelectionBar.getWidth());
			mRangeSelectionBar.setCursorSpace(space);
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

}