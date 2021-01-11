package org.lasque.tusdkvideodemo.views.record;

import android.animation.ValueAnimator;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.PointF;
import android.graphics.drawable.Drawable;
import android.net.Uri;

import androidx.annotation.DrawableRes;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.core.view.ViewCompat;
import androidx.lifecycle.Lifecycle;
import androidx.core.view.ViewPropertyAnimatorListener;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import org.lasque.tusdk.api.audio.preproc.processor.TuSdkAudioPitchEngine;
import org.lasque.tusdk.core.TuSdk;
import org.lasque.tusdk.core.TuSdkContext;
import org.lasque.tusdk.core.components.camera.TuSdkVideoFocusTouchViewBase;
import org.lasque.tusdk.core.seles.SelesParameters;
import org.lasque.tusdk.core.seles.tusdk.FilterGroup;
import org.lasque.tusdk.core.seles.tusdk.FilterOption;
import org.lasque.tusdk.core.seles.tusdk.FilterWrap;
import org.lasque.tusdk.core.struct.TuSdkSize;
import org.lasque.tusdk.core.utils.TLog;
import org.lasque.tusdk.core.utils.ThreadHelper;
import org.lasque.tusdk.core.utils.hardware.CameraConfigs;
import org.lasque.tusdk.core.utils.hardware.TuSdkRecorderVideoCamera;
import org.lasque.tusdk.core.utils.hardware.TuSdkRecorderVideoCameraImpl;
import org.lasque.tusdk.core.utils.hardware.TuSdkStillCameraAdapter;
import org.lasque.tusdk.core.utils.image.AlbumHelper;
import org.lasque.tusdk.core.utils.image.RatioType;
import org.lasque.tusdk.core.utils.sqllite.ImageSqlHelper;
import org.lasque.tusdk.core.view.TuSdkViewHelper;
import org.lasque.tusdk.core.view.widget.button.TuSdkTextButton;
import org.lasque.tusdk.video.editor.TuSdkMediaComicEffectData;
import org.lasque.tusdk.video.editor.TuSdkMediaCosmeticEffectData;
import org.lasque.tusdk.video.editor.TuSdkMediaEffectData;
import org.lasque.tusdk.video.editor.TuSdkMediaFilterEffectData;
import org.lasque.tusdk.video.editor.TuSdkMediaPlasticFaceEffect;
import org.lasque.tusdk.video.editor.TuSdkMediaSkinFaceEffect;
import com.upyun.shortvideo.R;
import org.lasque.tusdkvideodemo.views.BeautyPlasticRecyclerAdapter;
import org.lasque.tusdkvideodemo.views.BeautyRecyclerAdapter;
import org.lasque.tusdkvideodemo.views.FilterConfigSeekbar;
import org.lasque.tusdkvideodemo.views.HorizontalProgressBar;
import org.lasque.tusdkvideodemo.views.ParamsConfigView;
import org.lasque.tusdkvideodemo.utils.Constants;
import org.lasque.tusdkvideodemo.views.FilterRecyclerAdapter;
import org.lasque.tusdkvideodemo.views.TabPagerIndicator;
import org.lasque.tusdkvideodemo.views.cosmetic.CosmeticPanelController;
import org.lasque.tusdkvideodemo.views.cosmetic.CosmeticTypes;
import org.lasque.tusdkvideodemo.views.cosmetic.panel.BasePanel;
import org.lasque.tusdkvideodemo.views.newFilterUI.FilterFragment;
import org.lasque.tusdkvideodemo.views.newFilterUI.FilterViewPagerAdapter;
import org.lasque.tusdkvideodemo.views.props.PropsItemMonsterPageFragment;
import org.lasque.tusdkvideodemo.views.props.PropsItemPageFragment;
import org.lasque.tusdkvideodemo.views.props.PropsItemPagerAdapter;
import org.lasque.tusdkvideodemo.views.props.StickerPropsItemPageFragment;
import org.lasque.tusdkvideodemo.views.props.model.PropsItem;
import org.lasque.tusdkvideodemo.views.props.model.PropsItemCategory;
import org.lasque.tusdkvideodemo.views.props.model.PropsItemMonsterCategory;
import org.lasque.tusdkvideodemo.views.props.model.PropsItemStickerCategory;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import static org.lasque.tusdk.video.editor.TuSdkMediaEffectData.TuSdkMediaEffectDataType.TuSdkMediaEffectDataTypeComic;
import static org.lasque.tusdk.video.editor.TuSdkMediaEffectData.TuSdkMediaEffectDataType.TuSdkMediaEffectDataTypeCosmetic;
import static org.lasque.tusdk.video.editor.TuSdkMediaEffectData.TuSdkMediaEffectDataType.TuSdkMediaEffectDataTypeFilter;
import static org.lasque.tusdk.video.editor.TuSdkMediaEffectData.TuSdkMediaEffectDataType.TuSdkMediaEffectDataTypePlasticFace;
import static org.lasque.tusdk.video.editor.TuSdkMediaEffectData.TuSdkMediaEffectDataType.TuSdkMediaEffectDataTypeSkinFace;

/**
 * Created by zuojindong on 2018/6/20.
 */

public class RecordView extends RelativeLayout {

    public final static String DEFAULT_FILTER_CODE = "default_filter_code";
    public final static String DEFAULT_FILTER_GROUP = "default_filter_group";

    /**
     * 录制类型状态
     */
    public interface RecordType {
        // 拍摄
        int CAPTURE = 0;
        // 长按拍摄
        int LONG_CLICK_RECORD = 1;
        // 单击拍摄
        int SHORT_CLICK_RECORD = 2;
        // 长按录制中
        int LONG_CLICK_RECORDING = 3;
        // 短按录制中
        int SHORT_CLICK_RECORDING = 4;
    }

    /**
     * 录制视频动作委托
     */
    public interface TuSDKMovieRecordDelegate {
        /**
         * 开始录制视频
         */
        void startRecording();

        /**
         * 是否正在录制
         *
         * @return
         */
        boolean isRecording();

        /**
         * 暂停录制视频
         */
        void pauseRecording();

        /**
         * 停止录制视频
         */
        void stopRecording();

        /**
         * 关闭录制界面
         */
        void finishRecordActivity();
    }

    public void setDelegate(TuSDKMovieRecordDelegate delegate) {
        mDelegate = delegate;
    }

    public TuSDKMovieRecordDelegate getDelegate() {
        return mDelegate;
    }

    private Context mContext;
    /**
     * 录制相机
     */
    protected TuSdkRecorderVideoCameraImpl mCamera;
    /**
     * 录制视频动作委托
     */
    private TuSDKMovieRecordDelegate mDelegate;
    /**
     * 拍照获得的Bitmap
     */
    private Bitmap mCaptureBitmap;

    private SharedPreferences mFilterValueMap;

    /******************************* View ********************************/
    /**
     * 顶部按键
     */
    private LinearLayout mTopBar;
    /**
     * 关闭按键
     */
    private TuSdkTextButton mCloseButton;
    /**
     * 切换摄像头按键
     */
    private TuSdkTextButton mSwitchButton;
    /**
     * 美颜按键
     */
    private TuSdkTextButton mBeautyButton;
    /**
     * 速度按键
     */
    private TuSdkTextButton mSpeedButton;
    /**
     * 更多设置
     */
    private TuSdkTextButton mMoreButton;

    /**
     * 美颜设置
     */
    private LinearLayout mSmartBeautyTabLayout;
    private RecyclerView mBeautyRecyclerView;

    /**
     * 录制进度
     **/
    private HorizontalProgressBar mRecordProgress;
    /**
     * 录制的视频之间的断点
     */
    private RelativeLayout interuptLayout;
    /**
     * 回退按钮
     */
    private TuSdkTextButton mRollBackButton;

    /**
     * 底部功能按键视图
     */
    private LinearLayout mBottomBarLayout;
    /**
     * 录制按键
     */
    private ImageView mRecordButton;
    /**
     * 确认保存视频
     **/
    private TuSdkTextButton mConfirmButton;
    /**
     * 贴纸
     */
    private TuSdkTextButton mStickerWrapButton;
    /**
     * 滤镜
     */
    private TuSdkTextButton mFilterButton;

    /**
     * 视频速度模式视图
     */
    private ViewGroup mSpeedModeBar;
    /**
     * 速度选项是否开启
     */
    private boolean isSpeedChecked = false;

    /**
     * 拍摄模式视图
     */
    private RelativeLayout mRecordModeBarLayout;
    /**
     * 拍照按键
     */
    private TuSdkTextButton mShootButton;
    /**
     * 长按录制
     */
    private TuSdkTextButton mLongButton;
    /**
     * 单击拍摄
     */
    private TuSdkTextButton mClickButton;

    /**
     * 更多设置视图
     */
    private LinearLayout mMoreConfigLayout;
    /**
     * 自动对焦开关
     */
    private TextView mFocusOpen;
    private TextView mFocusClose;
    /**
     * 闪关灯开关
     */
    private TextView mLightingOpen;
    private TextView mLightingClose;
    /**
     * Radio设置
     */
    private ImageView mRadioFull;
    private ImageView mRadio3_4;
    private ImageView mRadio1_1;
    /**
     * 变声
     */
    private RelativeLayout mChangeAudioLayout;
    private RadioGroup mChangeAudioGroup;


    // 道具布局 贴纸+哈哈镜

    /**
     * 道具布局
     */
    private LinearLayout mPropsItemLayout;
    /**
     * 取消道具
     */
    private ImageView mPropsItemCancel;
    /**
     * 道具 Layout
     */
    private ViewPager2 mPropsItemViewPager;
    /**
     * 道具  PropsItemPagerAdapter
     */
    private PropsItemPagerAdapter<PropsItemPageFragment> mPropsItemPagerAdapter;

    private TabPagerIndicator mPropsItemTabPagerIndicator;
    /**
     * 道具分类类别
     */
    private List<PropsItemCategory> mPropsItemCategories = new ArrayList<>();

    //曝光补偿
    private SeekBar mExposureSeekbar;


    /**
     * 图片预留视图
     **/
    private ImageView mPreViewImageView;
    /**
     * 返回拍照按钮
     **/
    private TuSdkTextButton mBackButton;
    /**
     * 保存按钮
     **/
    private TuSdkTextButton mSaveImageButton;

    private boolean isBeautyClose = false;


    private int mCameraMaxEV = 0;

    private int mCameraMinEV = 0;

    private int mCurrentCameraEV = 0;


    private ViewPager2 mFilterViewPager;
    private TabPagerIndicator mFilterTabIndicator;
    private FilterViewPagerAdapter mFilterViewPagerAdapter;
    private ImageView mFilterReset;
    private boolean isFilterReset = false;

    private List<FilterFragment> mFilterFragments;

    private List<FilterGroup> mFilterGroups;

    public RecordView(Context context) {
        super(context);
    }

    public RecordView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RecordView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    protected int getLayoutId() {
        return R.layout.record_view;
    }

    protected void init(Context context) {
        LayoutInflater.from(context).inflate(getLayoutId(), this,
                true);

        // TopLayout
        mTopBar = findViewById(R.id.lsq_topBar);
        mCloseButton = findViewById(R.id.lsq_closeButton);
        mSwitchButton = findViewById(R.id.lsq_switchButton);
        mBeautyButton = findViewById(R.id.lsq_beautyButton);
        mSpeedButton = findViewById(R.id.lsq_speedButton);
        mMoreButton = findViewById(R.id.lsq_moreButton);

        mCloseButton.setOnClickListener(onClickListener);
        mSwitchButton.setOnClickListener(onClickListener);
        mBeautyButton.setOnClickListener(onClickListener);
        mSpeedButton.setOnClickListener(onClickListener);
        mMoreButton.setOnClickListener(onClickListener);

        // more_config_layout
        mMoreConfigLayout = findViewById(R.id.lsq_more_config_layout);
        // 自动对焦
        mFocusOpen = findViewById(R.id.lsq_focus_open);
        mFocusClose = findViewById(R.id.lsq_focus_close);
        mFocusOpen.setOnClickListener(onClickListener);
        mFocusClose.setOnClickListener(onClickListener);
        // 闪光灯
        mLightingOpen = findViewById(R.id.lsq_lighting_open);
        mLightingClose = findViewById(R.id.lsq_lighting_close);
        mLightingOpen.setOnClickListener(onClickListener);
        mLightingClose.setOnClickListener(onClickListener);
        // 比例
        mRadioFull = findViewById(R.id.lsq_radio_full);
        mRadio3_4 = findViewById(R.id.lsq_radio_3_4);
        mRadio1_1 = findViewById(R.id.lsq_radio_1_1);
        mRadioFull.setOnClickListener(onClickListener);
        mRadio3_4.setOnClickListener(onClickListener);
        mRadio1_1.setOnClickListener(onClickListener);
        // 变声
        mChangeAudioLayout = findViewById(R.id.lsq_audio_layout);
        mChangeAudioGroup = findViewById(R.id.lsq_audio_group);
        mChangeAudioGroup.setOnCheckedChangeListener(mAudioOnCheckedChangeListener);

        // 底部功能按键视图
        mBottomBarLayout = findViewById(R.id.lsq_button_wrap_layout);
        // 贴纸按键
        mStickerWrapButton = findViewById(R.id.lsq_stickerWrap);
        mStickerWrapButton.setOnClickListener(onClickListener);
        // 滤镜按键
        mFilterButton = findViewById(R.id.lsq_tab_filter_btn);
        mFilterButton.setOnClickListener(onClickListener);
        // 保存视频
        mConfirmButton = findViewById(R.id.lsq_confirmWrap);
        mConfirmButton.setOnClickListener(onClickListener);
        // 录制按钮
        mRecordButton = findViewById(R.id.lsq_recordButton);
        mRecordButton.setOnTouchListener(onTouchListener);

        // 模式切换视图
        mRecordModeBarLayout = findViewById(R.id.lsq_record_mode_bar_layout);
        mRecordModeBarLayout.setOnTouchListener(onModeBarTouchListener);

        // 录制进度条
        mRecordProgress = findViewById(R.id.lsq_record_progressbar);
        Button minTimeButton = (Button) findViewById(R.id.lsq_minTimeBtn);
        RelativeLayout.LayoutParams minTimeLayoutParams = (LayoutParams) minTimeButton.getLayoutParams();
        minTimeLayoutParams.leftMargin = (int) (((float) Constants.MIN_RECORDING_TIME * TuSdkContext.getScreenSize().width) / Constants.MAX_RECORDING_TIME)
                - TuSdkContext.dip2px(minTimeButton.getWidth());
        // 一进入录制界面就显示最小时长标记
        interuptLayout = (RelativeLayout) findViewById(R.id.interuptLayout);
        // 回退按钮
        mRollBackButton = (TuSdkTextButton) findViewById(R.id.lsq_backWrap);
        mRollBackButton.setOnClickListener(onClickListener);

        // 模式切换
        mShootButton = findViewById(R.id.lsq_shootButton);
        mLongButton = findViewById(R.id.lsq_longButton);
        mClickButton = findViewById(R.id.lsq_clickButton);
        mShootButton.setOnTouchListener(onModeBarTouchListener);
        mLongButton.setOnTouchListener(onModeBarTouchListener);
        mClickButton.setOnTouchListener(onModeBarTouchListener);

        // PreviewLayout
        mBackButton = findViewById(R.id.lsq_backButton);
        mBackButton.setOnClickListener(onClickListener);
        mSaveImageButton = findViewById(R.id.lsq_saveImageButton);
        mSaveImageButton.setOnClickListener(onClickListener);
        mPreViewImageView = findViewById(R.id.lsq_cameraPreviewImageView);
        mPreViewImageView.setOnClickListener(onClickListener);

        // 速度控制条
        mSpeedModeBar = findViewById(R.id.lsq_movie_speed_bar);
        int childCount = mSpeedModeBar.getChildCount();
        for (int i = 0; i < childCount; i++) {
            mSpeedModeBar.getChildAt(i).setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    selectSpeedMode(Integer.parseInt((String) view.getTag()));
                }
            });
        }

        // 美颜Bar
        mSmartBeautyTabLayout = findViewById(R.id.lsq_smart_beauty_layout);
        setBeautyLayout(false);
        mBeautyRecyclerView = findViewById(R.id.lsq_beauty_recyclerView);
        mBeautyRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));

        // 美颜类型
        mBeautyRecyclerAdapter = new BeautyRecyclerAdapter(getContext());
        mBeautyRecyclerAdapter.setOnSkinItemClickListener(beautyItemClickListener);
        // 微整形
        mBeautyPlasticRecyclerAdapter = new BeautyPlasticRecyclerAdapter(getContext(), mBeautyPlastics);
        mBeautyPlasticRecyclerAdapter.setOnBeautyPlasticItemClickListener(beautyPlasticItemClickListener);

        // 美妆
        mController = new CosmeticPanelController(getContext());
        initCosmeticView();

        // 滤镜调节
        mFilterConfigView = findViewById(R.id.lsq_filter_config_view);
        mFilterConfigView.setSeekBarDelegate(mFilterConfigViewSeekBarDelegate);
        // 微整形调节
        mBeautyPlasticsConfigView = findViewById(R.id.lsq_beauty_plastics_config_view);
        mBeautyPlasticsConfigView.setPrefix("lsq_beauty_");
        mBeautyPlasticsConfigView.setSeekBarDelegate(mBeautyPlasticConfigViewSeekBarDelegate);

        //曝光补偿控制
        mExposureSeekbar = findViewById(R.id.lsq_exposure_compensation_seek);
        mExposureSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (mCamera == null) return;
                mCurrentCameraEV = progress - mCameraMaxEV;
                mCamera.setExposureCompensation(mCurrentCameraEV);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        mFilterValueMap = getContext().getSharedPreferences("TUTUFilter", Context.MODE_PRIVATE);

        initFilterRecyclerView();
        initStickerLayout();

    }

    private String mCurrentFilterCode = "";


    public void initFilterGroupsViews(FragmentManager fragmentManager, Lifecycle lifecycle, List<FilterGroup> filterGroups) {
        mFilterGroups = filterGroups;
        mFilterReset = findViewById(R.id.lsq_filter_reset);
        mFilterReset.setOnClickListener(new TuSdkViewHelper.OnSafeClickListener() {
            @Override
            public void onSafeClick(View view) {
                mCamera.removeMediaEffectsWithType(TuSdkMediaEffectDataTypeFilter);
                mCamera.removeMediaEffectsWithType(TuSdkMediaEffectDataTypeComic);
                mFilterFragments.get(mFilterTabIndicator.getCurrentPosition()).removeFilter();
                mFilterConfigView.setVisibility(View.GONE);
                mFilterViewPagerAdapter.notifyDataSetChanged();
                mCurrentFilterCode = "";
                mFilterValueMap.edit().remove(DEFAULT_FILTER_CODE).apply();
                mFilterValueMap.edit().remove(DEFAULT_FILTER_GROUP).apply();
                isFilterReset = true;

            }
        });

        mFilterTabIndicator = findViewById(R.id.lsq_filter_tabIndicator);

        mFilterViewPager = findViewById(R.id.lsq_filter_view_pager);
        mFilterViewPager.requestDisallowInterceptTouchEvent(true);
        List<String> tabTitles = new ArrayList<>();
        List<FilterFragment> fragments = new ArrayList<>();
        for (FilterGroup group : mFilterGroups) {
            FilterFragment fragment = FilterFragment.newInstance(group);
            if (group.groupId == 252) {
                fragment.setOnFilterItemClickListener(new FilterFragment.OnFilterItemClickListener() {
                    @Override
                    public void onFilterItemClick(String code, int position) {
                        mCurrentFilterCode = code;
                        mCurrentPosition = position;
                        //设置滤镜
                        changeVideoComicEffectCode(mCurrentFilterCode);
                    }
                });
            } else {
                fragment.setOnFilterItemClickListener(new FilterFragment.OnFilterItemClickListener() {
                    @Override
                    public void onFilterItemClick(String code, int position) {
                        if (TextUtils.equals(mCurrentFilterCode, code)) {
                            mFilterConfigView.setVisibility(mFilterConfigView.getVisibility() == View.GONE ? View.VISIBLE : View.GONE);
                        } else {
                            mCurrentFilterCode = code;
                            mCurrentPosition = position;
                            //设置滤镜
                            changeVideoFilterCode(mCurrentFilterCode);
                        }
                    }
                });
            }

            fragments.add(fragment);
            tabTitles.add(group.getName());
        }
        mFilterFragments = fragments;
        mFilterViewPagerAdapter = new FilterViewPagerAdapter(fragmentManager, lifecycle, fragments);
        mFilterViewPager.setAdapter(mFilterViewPagerAdapter);
        mFilterTabIndicator.setViewPager(mFilterViewPager, 0);
        mFilterTabIndicator.setDefaultVisibleCounts(tabTitles.size());
        mFilterTabIndicator.setTabItems(tabTitles);


    }

    /**
     * 初始化进度
     */
    public void initRecordProgress() {
        mRecordProgress.clearProgressList();
        interuptLayout.removeAllViews();
        if (mBottomBarLayout.getVisibility() == VISIBLE)
            setViewHideOrVisible(true);
    }

    /**
     * 传递录制相机对象
     *
     * @param camera
     */
    public void setUpCamera(Context context, TuSdkRecorderVideoCameraImpl camera) {
        this.mContext = context;
        this.mCamera = camera;

        mCamera.setCameraListener(mVideoCameraLinstener);
        mCamera.setMediaEffectChangeListener(mMediaEffectChangeListener);
        mCamera.getFocusTouchView().setGestureListener(gestureListener);


    }

    /**
     * 特效数据应用、移除回调
     */
    private TuSdkRecorderVideoCamera.TuSdkMediaEffectChangeListener mMediaEffectChangeListener = new TuSdkRecorderVideoCamera.TuSdkMediaEffectChangeListener() {
        @Override
        public void didApplyingMediaEffect(final TuSdkMediaEffectData mediaEffectData) {
            ThreadHelper.post(new Runnable() {
                @Override
                public void run() {
                    switch (mediaEffectData.getMediaEffectType()) {
                        case TuSdkMediaEffectDataTypeFilter: //滤镜效果
                            List<SelesParameters.FilterArg> filterArgs = new ArrayList<>();
                            SelesParameters.FilterArg filterArg = mediaEffectData.getFilterArg("mixied");// 获取效果参数
                            if (filterArg != null) filterArgs.add(filterArg);
                            SelesParameters.FilterArg saturationFilterArg = mediaEffectData.getFilterArg("saturation");
                            if (saturationFilterArg != null) filterArgs.add(saturationFilterArg);
                            mFilterConfigView.setFilterArgs(mediaEffectData, filterArgs);
                            break;

                    }
                }
            });
        }

        /**
         * 移除特效后回调
         * @param mediaEffects
         */
        @Override
        public void didRemoveMediaEffect(List<TuSdkMediaEffectData> mediaEffects) {

        }
    };

    /**
     * 录制按键
     */
    private OnTouchListener onTouchListener = new OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (getDelegate() == null) return false;
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    if (TuSdkViewHelper.isFastDoubleClick()) return false;
                    if (mRecordMode == RecordType.LONG_CLICK_RECORD) {
                        setViewHideOrVisible(false);
                        getDelegate().startRecording();
                        updateRecordButtonResource(RecordType.LONG_CLICK_RECORDING);
                    }
                    return true;
                case MotionEvent.ACTION_UP:
                    // 点击拍照
                    if (mRecordMode == RecordType.CAPTURE) {
                        mCamera.captureImage();
                    }
                    // 长按录制
                    else if (mRecordMode == RecordType.LONG_CLICK_RECORD) {
                        getDelegate().pauseRecording();
                        updateRecordButtonResource(RecordType.LONG_CLICK_RECORD);
                    }
                    // 点击录制
                    else if (mRecordMode == RecordType.SHORT_CLICK_RECORD) {
                        // 是否录制中
                        if (getDelegate().isRecording()) {
                            getDelegate().pauseRecording();
                            updateRecordButtonResource(RecordType.SHORT_CLICK_RECORD);
                        } else {
                            if (mCamera.getMovieDuration() >= Constants.MAX_RECORDING_TIME) {
                                String msg = getStringFromResource("max_recordTime") + Constants.MAX_RECORDING_TIME + "s";
                                TuSdk.messageHub().showToast(mContext, msg);
                                return false;
                            }
                            setViewHideOrVisible(false);
                            getDelegate().startRecording();
                            updateRecordButtonResource(RecordType.SHORT_CLICK_RECORDING);
                        }
                    }
                    return true;
                default:
                    return false;
            }
        }
    };

    /**
     * 变声切换
     */
    RadioGroup.OnCheckedChangeListener mAudioOnCheckedChangeListener = new RadioGroup.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            switch (checkedId) {
                case R.id.lsq_audio_normal:
                    // 正常
                    mCamera.setSoundPitchType(TuSdkAudioPitchEngine.TuSdkSoundPitchType.Normal);
                    break;
                case R.id.lsq_audio_monster:
                    // 怪兽
                    mCamera.setSoundPitchType(TuSdkAudioPitchEngine.TuSdkSoundPitchType.Monster);
                    break;
                case R.id.lsq_audio_uncle:
                    // 大叔
                    mCamera.setSoundPitchType(TuSdkAudioPitchEngine.TuSdkSoundPitchType.Uncle);
                    break;
                case R.id.lsq_audio_girl:
                    // 女生
                    mCamera.setSoundPitchType(TuSdkAudioPitchEngine.TuSdkSoundPitchType.Girl);
                    break;
                case R.id.lsq_audio_lolita:
                    // 萝莉
                    mCamera.setSoundPitchType(TuSdkAudioPitchEngine.TuSdkSoundPitchType.Lolita);
                    break;
            }
        }
    };

    /**
     * 属性动画监听事件
     */
    private ViewPropertyAnimatorListener mViewPropertyAnimatorListener = new ViewPropertyAnimatorListener() {

        @Override
        public void onAnimationCancel(View view) {
        }

        @Override
        public void onAnimationEnd(View view) {
            ViewCompat.animate(mPropsItemLayout).setListener(null);
            ViewCompat.animate(mFilterContent).setListener(null);
        }

        @Override
        public void onAnimationStart(View view) {
        }
    };

    /******************************** 滤镜 ********************************************/
    /**
     * 默认选中滤镜
     */
    private static final int DEFAULT_POSITION = 1;
    /**
     * 滤镜视图
     */
    private RelativeLayout mFilterContent;
    /**
     * 参数调节视图
     */
    protected ParamsConfigView mFilterConfigView;
    /**
     * 滤镜列表
     */
    private RecyclerView mFilterRecyclerView;
    /**
     * 滤镜列表Adapter
     */
    private FilterRecyclerAdapter mFilterAdapter;
    /**
     * 滤镜列表
     */
    private RecyclerView mComicsFilterRecyclerView;
    /**
     * 滤镜列表Adapter
     */
    private FilterRecyclerAdapter mComicsFilterAdapter;
    /**
     * 用于记录上一次位置
     */
    private int mCurrentPosition = DEFAULT_POSITION;
    /**
     * 用于记录上一次位置
     */
    private int mComicsCurrentPosition = 0;
    /**
     * 滤镜名称
     */
    private TextView mFilterNameTextView;
    /**
     * 是否切换漫画滤镜
     */
    private boolean isComicsFilterChecked = false;

    /**
     * 初始化滤镜
     */
    private void initFilterRecyclerView() {
        mFilterNameTextView = findViewById(R.id.lsq_filter_name);
        mFilterContent = findViewById(R.id.lsq_filter_content);
        /** 屏蔽在滤镜栏显示时 在滤镜栏上面的手势操作  如不需要 可删除*/
        mFilterContent.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });
        setFilterContentVisible(false);

    }

    /**
     * 显示滤镜列表
     */
    private void showFilterLayout() {
        // 滤镜栏向上动画并显示
        ViewCompat.setTranslationY(mFilterContent,
                mFilterContent.getHeight());
        ViewCompat.animate(mFilterContent).translationY(0).setDuration(200).setListener(mViewPropertyAnimatorListener);

        setFilterContentVisible(true);

        // 设置滤镜参数调节
        if (mCurrentPosition > 0 && mFilterConfigView != null) {
            mFilterConfigView.invalidate();
        }
    }

    /**
     * 滤镜调节栏
     */
    private ParamsConfigView.FilterConfigViewSeekBarDelegate mFilterConfigViewSeekBarDelegate = new ParamsConfigView.FilterConfigViewSeekBarDelegate() {
        @Override
        public void onSeekbarDataChanged(FilterConfigSeekbar seekbar, SelesParameters.FilterArg arg) {
            float progress = seekbar.getSeekbar().getProgress();
            mFilterValueMap.edit().putFloat(mCurrentFilterCode, progress).apply();
            if (mCurrentFilter != null) {
                mCurrentFilter.submitParameter(arg.getKey(), progress);
            }
        }
    };

    /**
     * 滤镜效果改变监听事件
     */
    protected TuSdkRecorderVideoCamera.TuSdkCameraListener mVideoCameraLinstener = new TuSdkRecorderVideoCamera.TuSdkCameraListener() {
        @Override
        public void onFilterChanged(FilterWrap selesOutInput) {
        }

        @Override
        public void onVideoCameraStateChanged(TuSdkStillCameraAdapter.CameraState newState) {
            if (newState.equals(TuSdkStillCameraAdapter.CameraState.StateUnknow)) return;
            ThreadHelper.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (!isBeautyClose)
                        // 调用极致美颜
                        switchConfigSkin(TuSdkMediaSkinFaceEffect.SkinFaceType.Beauty);
                }
            }, 500);
            ThreadHelper.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (mCamera.mediaEffectsWithType(TuSdkMediaEffectDataTypePlasticFace).size() == 0) {
                        // 添加一个默认微整形特效
                        TuSdkMediaPlasticFaceEffect plasticFaceEffect = new TuSdkMediaPlasticFaceEffect();
                        mCamera.addMediaEffectData(plasticFaceEffect);
//                        for (SelesParameters.FilterArg arg : plasticFaceEffect.getFilterArgs()) {
//                            if (arg.equalsKey("eyeSize")) {// 大眼
//                                arg.setMaxValueFactor(0.85f);// 最大值限制
//                            }
//                            if (arg.equalsKey("chinSize")) {// 瘦脸
//                                arg.setMaxValueFactor(0.9f);// 最大值限制
//                            }
//                            if (arg.equalsKey("noseSize")) {// 瘦鼻
//                                arg.setMaxValueFactor(0.6f);// 最大值限制
//                            }
//                        }
                        for (String key : mDefaultBeautyPercentParams.keySet()) {
                            TLog.e("key -- %s", mDefaultBeautyPercentParams.get(key));
                            submitPlasticFaceParamter(key, mDefaultBeautyPercentParams.get(key));
                            plasticFaceEffect.getFilterArg(key).setDefaultPercent(mDefaultBeautyPercentParams.get(key));
                        }
                        plasticFaceEffect.getFilterWrap().setFilterParameter(plasticFaceEffect.getFilterWrap().getFilterParameter());

                    }
                }
            }, 700);
            // 滤镜切换需要做延时
            if (!isFilterReset){
                setDefaultFilter();
            }
            if (newState.equals(TuSdkStillCameraAdapter.CameraState.StatePreview) && mCameraMaxEV == 0) {
                ThreadHelper.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mCameraMaxEV = mCamera.getMaxExposureCompensation();
                        mCameraMinEV = mCamera.getMinExposureCompensation();
                        mExposureSeekbar.setMax(mCameraMaxEV + Math.abs(mCameraMinEV));
                        mExposureSeekbar.setProgress(mCameraMaxEV);
                    }
                }, 1000);
            }


        }

        /**
         * 拍照数据回调
         * @param bitmap 图片
         */
        @Override
        public void onVideoCameraScreenShot(Bitmap bitmap) {
            presentPreviewLayout(bitmap);
        }


    };

    private void setDefaultFilter() {
        mFilterViewPager.setCurrentItem(0);
        int filterViewPagerPos = 0;
        mFilterGroups = Constants.getCameraFilters(true);
        String defaultCode = mFilterValueMap.getString(DEFAULT_FILTER_CODE, "");
        if (TextUtils.isEmpty(defaultCode)) {
            return;
        }
        long defaultFilterGroupId = mFilterValueMap.getLong(DEFAULT_FILTER_GROUP, -1);
        List<FilterOption> defaultFilters = mFilterGroups.get(0).filters;
        FilterGroup defaultGroup = mFilterGroups.get(0);
        if (defaultFilterGroupId != -1) {
            for (FilterGroup group : mFilterGroups) {
                if (group.groupId == defaultFilterGroupId) {
                    defaultFilters = group.filters;
                    defaultGroup = group;
                    filterViewPagerPos = mFilterGroups.indexOf(group);
                    break;
                }
            }
        }
        for (int i = 0; i < defaultFilters.size(); i++) {
            if (defaultFilters.get(i).code.equals(defaultCode)) {
                mCurrentPosition = i;
                break;
            }
        }
        mCurrentFilterCode = defaultCode;
        mFilterViewPager.setCurrentItem(filterViewPagerPos, false);
        final FilterGroup finalDefaultGroup = defaultGroup;
        ThreadHelper.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (finalDefaultGroup.groupId == 252) {
                    changeVideoComicEffectCode(mCurrentFilterCode);
                } else {
                    changeVideoFilterCode(mCurrentFilterCode);
                }
            }
        }, 1000);
    }

    /**
     * 滤镜组列表点击事件
     */
    private FilterRecyclerAdapter.ItemClickListener mFilterItemClickListener = new FilterRecyclerAdapter.ItemClickListener() {
        @Override
        public void onItemClick(int position) {
            mFilterConfigView.setVisibility((position == 0) ? INVISIBLE :
                    ((mCurrentPosition == position) ? (mFilterConfigView.getVisibility() == VISIBLE ? INVISIBLE : VISIBLE)
                            : INVISIBLE));
            if (mCurrentPosition == position) return;
            mCurrentPosition = position;
            changeVideoFilterCode(mFilterAdapter.getFilterList().get(position));
        }
    };

    /**
     * 漫画滤镜组列表点击事件
     */
    private FilterRecyclerAdapter.ItemClickListener mComicsFilterItemClickListener = new FilterRecyclerAdapter.ItemClickListener() {
        @Override
        public void onItemClick(int position) {
            mComicsCurrentPosition = position;
            changeVideoComicEffectCode(mComicsFilterAdapter.getFilterList().get(position));
        }
    };

    private TuSdkMediaEffectData mCurrentFilter = null;

    /**
     * 切换滤镜
     *
     * @param code
     */
    protected void changeVideoFilterCode(final String code) {
        if (mCamera.mediaEffectsWithType(TuSdkMediaEffectDataTypeFilter) != null && mCamera.mediaEffectsWithType(TuSdkMediaEffectDataTypeFilter).size() > 0 && mCamera.mediaEffectsWithType(TuSdkMediaEffectDataTypeFilter).get(0).getFilterWrap().getCode().equals(code))
            return;
        isFilterReset = false;
        TuSdkMediaFilterEffectData filterEffectData = new TuSdkMediaFilterEffectData(code);
        SelesParameters.FilterArg filterArg = filterEffectData.getFilterArg("mixied");// 效果
        mCamera.addMediaEffectData(filterEffectData);
        mFilterValueMap.edit().putString(DEFAULT_FILTER_CODE, code).apply();
        mFilterValueMap.edit().putLong(DEFAULT_FILTER_GROUP, mFilterGroups.get(mFilterViewPager.getCurrentItem()).groupId).apply();
        if (filterArg != null) {
            Float value = mFilterValueMap.getFloat(code, -1f) == -1f ? 0.75f : mFilterValueMap.getFloat(code, -1f);
            filterArg.setPrecentValue(value);
            filterEffectData.submitParameter(filterArg.getKey(), filterArg.getPrecentValue());
        }
        mCurrentFilter = filterEffectData;
        if (mFilterTabIndicator.getCurrentPosition() != -1) {
            for (int i = 0; i < mFilterFragments.size(); i++) {
                if (i == mFilterTabIndicator.getCurrentPosition()) {
                    mFilterFragments.get(i).setCurrentPosition(mCurrentPosition);
                } else {
                    mFilterFragments.get(i).setCurrentPosition(-1);
                }
            }
        }
        // 滤镜名显示
        showHitTitle(TuSdkContext.getString("lsq_filter_" + code));
    }


    /**
     * 显示提示文字
     *
     * @param title
     */
    private void showHitTitle(String title) {
        AlphaAnimation alphaAnimation = new AlphaAnimation(1.0f, 0.0f);
        alphaAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                mFilterNameTextView.setVisibility(VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mFilterNameTextView.setVisibility(GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        mFilterNameTextView.setText(title);
        mFilterNameTextView.setAnimation(alphaAnimation);
        alphaAnimation.setDuration(2000);
        alphaAnimation.start();
    }

    /**
     * TouchView滑动监听
     */
    private TuSdkVideoFocusTouchViewBase.GestureListener gestureListener = new TuSdkVideoFocusTouchViewBase.GestureListener() {
        @Override
        public void onLeftGesture() {
            // 美颜开启禁止滑动切换
            if (mSmartBeautyTabLayout.getVisibility() == VISIBLE) return;

            FilterGroup current = mFilterGroups.get(mFilterTabIndicator.getCurrentPosition());
            final String filterCode;
            if (mCurrentPosition == current.filters.size() - 1) {
                int targetViewPagerPos = mFilterTabIndicator.getCurrentPosition() + 1 == mFilterFragments.size() ? 0 : mFilterTabIndicator.getCurrentPosition() + 1;
                current = mFilterGroups.get(targetViewPagerPos);
                mFilterViewPager.setCurrentItem(targetViewPagerPos);
                mCurrentPosition = 0;
            } else {
                ++mCurrentPosition;
            }
            filterCode = current.filters.get(mCurrentPosition).code;
            mCurrentFilterCode = filterCode;
            final FilterGroup finalCurrent = current;
            ThreadHelper.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (finalCurrent.groupId == 252) {
                        changeVideoComicEffectCode(filterCode);
                    } else {
                        changeVideoFilterCode(filterCode);
                    }
                }
            }, 100);


        }

        @Override
        public void onRightGesture() {
            // 美颜开启禁止滑动切换
            if (mSmartBeautyTabLayout.getVisibility() == VISIBLE) return;

            FilterGroup current = mFilterGroups.get(mFilterTabIndicator.getCurrentPosition());
            final String filterCode;
            if (mCurrentPosition == 0) {
                int targetViewPagerPos = mFilterTabIndicator.getCurrentPosition() - 1 == -1 ? mFilterFragments.size() - 1 : mFilterTabIndicator.getCurrentPosition() - 1;
                current = mFilterGroups.get(targetViewPagerPos);
                mFilterViewPager.setCurrentItem(targetViewPagerPos);
                mCurrentPosition = current.filters.size() - 1;
            } else {
                --mCurrentPosition;
            }
            filterCode = current.filters.get(mCurrentPosition).code;
            mCurrentFilterCode = filterCode;
            final FilterGroup finalCurrent = current;
            ThreadHelper.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (finalCurrent.groupId == 252) {
                        changeVideoComicEffectCode(filterCode);
                    } else {
                        changeVideoFilterCode(filterCode);
                    }
                }
            }, 100);
        }

        @Override
        public void onClick() {
            if (!mCamera.isRecording()) {
                mExposureSeekbar.setProgress(mCameraMaxEV);
                setFilterContentVisible(false);
                setBeautyViewVisible(false);
                setBottomViewVisible(true);
                setStickerVisible(false);
                mMoreConfigLayout.setVisibility(GONE);
                setTextButtonDrawableTop(mMoreButton, R.drawable.video_nav_ic_more);
                mPropsItemViewPager.getAdapter().notifyDataSetChanged();
                mCamera.getFocusTouchView().isShowFoucusView(true);
            }
        }
    };

    /********************** 动漫 ****************************/

    /**
     * 切换漫画滤镜
     *
     * @param code
     */
    protected void changeVideoComicEffectCode(final String code) {
        isFilterReset = false;
        TuSdkMediaComicEffectData effectData = new TuSdkMediaComicEffectData(code);
        mCamera.addMediaEffectData(effectData);
        mFilterValueMap.edit().putString(DEFAULT_FILTER_CODE, code).apply();
        mFilterValueMap.edit().putLong(DEFAULT_FILTER_GROUP, mFilterGroups.get(mFilterViewPager.getCurrentItem()).groupId).apply();
        mCurrentFilter = effectData;
        if (mFilterTabIndicator.getCurrentPosition() != -1) {
            for (int i = 0; i < mFilterFragments.size(); i++) {
                if (i == mFilterTabIndicator.getCurrentPosition()) {
                    mFilterFragments.get(i).setCurrentPosition(mCurrentPosition);
                } else {
                    mFilterFragments.get(i).setCurrentPosition(-1);
                }
            }
        }
        mFilterConfigView.setVisibility(View.GONE);
        // 滤镜名显示
        showHitTitle(TuSdkContext.getString("lsq_filter_" + code));
    }

    /******************************* 贴纸 **************************/
    /**
     * 初始化贴纸
     */
    private void initStickerLayout() {
        mPropsItemViewPager = findViewById(R.id.lsq_viewPager);
        mPropsItemTabPagerIndicator = findViewById(R.id.lsq_TabIndicator);

        mPropsItemCancel = findViewById(R.id.lsq_cancel_button);
        mPropsItemCancel.setOnClickListener(onClickListener);

        // 贴纸视图
        mPropsItemLayout = findViewById(R.id.lsq_sticker_layout);
        setStickerVisible(false);
    }

    /**
     * 设置贴纸视图
     *
     * @param isVisible 是否可见
     */
    private void setStickerVisible(boolean isVisible) {
        mPropsItemLayout.setVisibility(isVisible ? VISIBLE : INVISIBLE);
    }

    /**
     * 显示贴纸视图
     */
    private void showStickerLayout() {
        setStickerVisible(true);
        // 滤镜栏向上动画并显示
        ViewCompat.setTranslationY(mPropsItemLayout,
                mPropsItemLayout.getHeight());
        ViewCompat.animate(mPropsItemLayout).translationY(0).setDuration(200).setListener(mViewPropertyAnimatorListener);
    }

    /**
     * 选择贴纸道具物品后回调
     */
    private StickerPropsItemPageFragment.StickerItemDelegate mStickerPropsItemDelegate = new StickerPropsItemPageFragment.StickerItemDelegate() {
        /**
         * 移除道具
         * @param propsItem
         */
        @Override
        public void removePropsItem(PropsItem propsItem) {
            if (propsItemUsed(propsItem))
                mCamera.removeMediaEffectsWithType(mPropsItemCategories.get(mPropsItemViewPager.getCurrentItem()).getMediaEffectType());
        }

        @Override
        public void didSelectPropsItem(PropsItem propsItem) {
            mCamera.addMediaEffectData(propsItem.effect());
            mPropsItemPagerAdapter.notifyAllPageData();

        }

        /**
         * 当前道具是否正在被使用
         *
         * @param propsItem 道具
         * @return
         */
        @Override
        public boolean propsItemUsed(PropsItem propsItem) {
            if (propsItem.effect() == null) return false;
            List<TuSdkMediaEffectData> mediaEffectDataList = mCamera.mediaEffectsWithType(propsItem.effect().getMediaEffectType());

            if (mediaEffectDataList == null || mediaEffectDataList.size() == 0) return false;

            return mediaEffectDataList.contains(propsItem.effect());
        }
    };

    /**
     * 选择道具物品后回调
     */
    private PropsItemPageFragment.ItemDelegate mPropsItemDelegate = new PropsItemPageFragment.ItemDelegate() {
        @Override
        public void didSelectPropsItem(PropsItem propsItem) {
            mCamera.addMediaEffectData(propsItem.effect());
            mPropsItemPagerAdapter.notifyAllPageData();

        }

        /**
         * 当前道具是否正在被使用
         *
         * @param propsItem 道具
         * @return
         */
        @Override
        public boolean propsItemUsed(PropsItem propsItem) {
            if (propsItem.effect() == null) return false;
            List<TuSdkMediaEffectData> mediaEffectDataList = mCamera.mediaEffectsWithType(propsItem.effect().getMediaEffectType());

            if (mediaEffectDataList == null || mediaEffectDataList.size() == 0) return false;

            return mediaEffectDataList.contains(propsItem.effect());
        }
    };

    /**
     * 设置贴纸适配器
     */
    public void init(final FragmentManager fm, final Lifecycle lifecycle) {

        // 添加贴纸道具分类数据
        mPropsItemCategories.addAll(PropsItemStickerCategory.allCategories());

        // 添加哈哈镜道具分类
        mPropsItemCategories.addAll(PropsItemMonsterCategory.allCategories());

        mPropsItemPagerAdapter = new PropsItemPagerAdapter(fm, lifecycle, new PropsItemPagerAdapter.DataSource() {
            @Override
            public Fragment frament(int pageIndex) {

                PropsItemCategory category = mPropsItemCategories.get(pageIndex);

                switch (category.getMediaEffectType()) {
                    case TuSdkMediaEffectDataTypeSticker: {
                        StickerPropsItemPageFragment fragment = new StickerPropsItemPageFragment(pageIndex, mPropsItemCategories.get(pageIndex).getItems());
                        fragment.setItemDelegate(mStickerPropsItemDelegate);
                        return fragment;
                    }
                    default: {
                        PropsItemMonsterPageFragment fragment = new PropsItemMonsterPageFragment(pageIndex, mPropsItemCategories.get(pageIndex).getItems());
                        fragment.setItemDelegate(mPropsItemDelegate);
                        return fragment;
                    }
                }

            }

            @Override
            public int pageCount() {
                return mPropsItemCategories.size();
            }
        });

        mPropsItemViewPager.setAdapter(mPropsItemPagerAdapter);

        mPropsItemTabPagerIndicator.setViewPager(mPropsItemViewPager, 0);
        mPropsItemTabPagerIndicator.setDefaultVisibleCounts(mPropsItemCategories.size());


        List<String> itemTitles = new ArrayList<>();
        for (PropsItemCategory category : mPropsItemCategories)
            itemTitles.add(category.getName());


        mPropsItemTabPagerIndicator.setTabItems(itemTitles);

//        initFilterGroupsViews(fm,lifecycle);
    }

    /*********************************** 美妆 ********************* */

    private RelativeLayout mCosmeticList;

    private LinearLayout mLipstick, mBlush, mEyebrow, mEyeshadow, mEyeliner, mEyelash, mCosmeticClear;
    private RelativeLayout mLipstickPanel, mBlushPanel, mEyebrowPanel, mEyeshadowPanel, mEyelinerPanel, mEyelashPanel;

    private CosmeticPanelController mController;

    private CosmeticTypes.Types mCurrentType;

    private HashSet<CosmeticTypes.Types> types = new HashSet<>();

    private int lastPos = -1;

    private BasePanel.OnPanelClickListener panelClickListener = new BasePanel.OnPanelClickListener() {
        @Override
        public void onClear(CosmeticTypes.Types type) {
            int viewID = -1;
            switch (type) {
                case Lipstick:
                    viewID = R.id.lsq_lipstick_add;
                    break;
                case Blush:
                    viewID = R.id.lsq_blush_add;
                    break;
                case Eyebrow:
                    viewID = R.id.lsq_eyebrow_add;
                    break;
                case Eyeshadow:
                    viewID = R.id.lsq_eyeshadow_add;
                    break;
                case Eyeliner:
                    viewID = R.id.lsq_eyeliner_add;
                    break;
                case Eyelash:
                    viewID = R.id.lsq_eyelash_add;
                    break;
            }
            findViewById(viewID).setVisibility(View.GONE);
            mBeautyPlasticsConfigView.setVisibility(View.GONE);
            types.remove(type);
        }

        @Override
        public void onClose(CosmeticTypes.Types type) {
            switch (type) {
                case Lipstick:
                    mLipstickPanel.setVisibility(View.GONE);
                    break;
                case Blush:
                    mBlushPanel.setVisibility(View.GONE);
                    break;
                case Eyebrow:
                    mEyebrowPanel.setVisibility(View.GONE);
                    break;
                case Eyeshadow:
                    mEyeshadowPanel.setVisibility(View.GONE);
                    break;
                case Eyeliner:
                    mEyelinerPanel.setVisibility(View.GONE);
                    break;
                case Eyelash:
                    mEyelashPanel.setVisibility(View.GONE);
                    break;
            }
            mCurrentType = null;
            mBeautyPlasticsConfigView.setVisibility(View.GONE);
            mPreButton.setVisibility(View.VISIBLE);
            mCosmeticScroll.scrollTo(lastPos, 0);
        }

        @Override
        public void onClick(CosmeticTypes.Types type) {
            int viewID = -1;
            switch (type) {
                case Lipstick:
                    viewID = R.id.lsq_lipstick_add;
                    break;
                case Blush:
                    viewID = R.id.lsq_blush_add;
                    break;
                case Eyebrow:
                    viewID = R.id.lsq_eyebrow_add;
                    break;
                case Eyeshadow:
                    viewID = R.id.lsq_eyeshadow_add;
                    break;
                case Eyeliner:
                    viewID = R.id.lsq_eyeliner_add;
                    break;
                case Eyelash:
                    viewID = R.id.lsq_eyelash_add;
                    break;
            }
            findViewById(viewID).setVisibility(View.VISIBLE);
            mBeautyPlasticsConfigView.setVisibility(View.VISIBLE);
            types.add(type);
        }
    };

    private View mPreButton;

    private OnClickListener mCosmeticClick = new OnClickListener() {
        @Override
        public void onClick(View v) {
            lastPos = mCosmeticScroll.getScrollX();
            if (v.getId() != R.id.lsq_cosmetic_item_clear)
                if (mPreButton != null) mPreButton.setVisibility(View.VISIBLE);
            switch (v.getId()) {
                case R.id.lsq_cosmetic_item_clear:
                    clearCosmetic();
                    break;
                case R.id.lsq_cosmetic_item_lipstick:
                    mCurrentType = CosmeticTypes.Types.Lipstick;
                    mLipstick.setVisibility(View.GONE);
                    mPreButton = mLipstick;
                    mLipstickPanel.setVisibility(mLipstickPanel.getVisibility() == View.GONE ? View.VISIBLE : View.GONE);
                    mBlushPanel.setVisibility(View.GONE);
                    mEyebrowPanel.setVisibility(View.GONE);
                    mEyeshadowPanel.setVisibility(View.GONE);
                    mEyelinerPanel.setVisibility(View.GONE);
                    mEyelashPanel.setVisibility(View.GONE);
                    if (mLipstickPanel.getVisibility() == View.VISIBLE) {
                        mBeautyPlasticsConfigView.setVisibility(findViewById(R.id.lsq_lipstick_add).getVisibility());
                        mBeautyPlasticsConfigView.setFilterArgs(mController.getEffect(), Arrays.asList(mController.getEffect().getFilterArg("lipAlpha")));
                        findViewById(R.id.list_panel).addOnLayoutChangeListener(new OnLayoutChangeListener() {
                            @Override
                            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                                mCosmeticScroll.scrollTo(mLipstickPanel.getLeft(), 0);
                                findViewById(R.id.list_panel).removeOnLayoutChangeListener(this);
                            }
                        });
                    } else {
                        mBeautyPlasticsConfigView.setVisibility(View.GONE);
                    }
                    break;
                case R.id.lsq_cosmetic_item_blush:
                    mCurrentType = CosmeticTypes.Types.Blush;
                    mBlush.setVisibility(View.GONE);
                    mPreButton = mBlush;
                    mLipstickPanel.setVisibility(View.GONE);
                    mBlushPanel.setVisibility(mBlushPanel.getVisibility() == View.GONE ? View.VISIBLE : View.GONE);
                    mEyebrowPanel.setVisibility(View.GONE);
                    mEyeshadowPanel.setVisibility(View.GONE);
                    mEyelinerPanel.setVisibility(View.GONE);
                    mEyelashPanel.setVisibility(View.GONE);
                    if (mBlushPanel.getVisibility() == View.VISIBLE) {
                        mBeautyPlasticsConfigView.setVisibility(findViewById(R.id.lsq_blush_add).getVisibility());
                        mBeautyPlasticsConfigView.setFilterArgs(mController.getEffect(), Arrays.asList(mController.getEffect().getFilterArg("blushAlpha")));
                        findViewById(R.id.list_panel).addOnLayoutChangeListener(new OnLayoutChangeListener() {
                            @Override
                            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                                mCosmeticScroll.scrollTo(mBlushPanel.getLeft(), 0);
                                findViewById(R.id.list_panel).removeOnLayoutChangeListener(this);
                            }
                        });
                    } else {
                        mBeautyPlasticsConfigView.setVisibility(View.GONE);
                    }
                    break;
                case R.id.lsq_cosmetic_item_eyebrow:
                    mCurrentType = CosmeticTypes.Types.Eyebrow;
                    mEyebrow.setVisibility(View.GONE);
                    mPreButton = mEyebrow;
                    mLipstickPanel.setVisibility(View.GONE);
                    mBlushPanel.setVisibility(View.GONE);
                    mEyebrowPanel.setVisibility(mEyebrowPanel.getVisibility() == View.GONE ? View.VISIBLE : View.GONE);
                    mEyeshadowPanel.setVisibility(View.GONE);
                    mEyelinerPanel.setVisibility(View.GONE);
                    mEyelashPanel.setVisibility(View.GONE);
                    if (mEyebrowPanel.getVisibility() == View.VISIBLE) {
                        mBeautyPlasticsConfigView.setVisibility(findViewById(R.id.lsq_eyebrow_add).getVisibility());
                        mBeautyPlasticsConfigView.setFilterArgs(mController.getEffect(), Arrays.asList(mController.getEffect().getFilterArg("eyebrowAlpha")));
                        findViewById(R.id.list_panel).addOnLayoutChangeListener(new OnLayoutChangeListener() {
                            @Override
                            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                                mCosmeticScroll.scrollTo(mEyebrowPanel.getLeft(), 0);
                                findViewById(R.id.list_panel).removeOnLayoutChangeListener(this);
                            }
                        });
                    } else {
                        mBeautyPlasticsConfigView.setVisibility(View.GONE);
                    }
                    break;
                case R.id.lsq_cosmetic_item_eyeshadow:
                    mCurrentType = CosmeticTypes.Types.Eyeshadow;
                    mEyeshadow.setVisibility(View.GONE);
                    mPreButton = mEyeshadow;
                    mLipstickPanel.setVisibility(View.GONE);
                    mBlushPanel.setVisibility(View.GONE);
                    mEyebrowPanel.setVisibility(View.GONE);
                    mEyeshadowPanel.setVisibility(mEyeshadowPanel.getVisibility() == View.GONE ? View.VISIBLE : View.GONE);
                    mEyelinerPanel.setVisibility(View.GONE);
                    mEyelashPanel.setVisibility(View.GONE);
                    if (mEyeshadowPanel.getVisibility() == View.VISIBLE) {
                        mBeautyPlasticsConfigView.setVisibility(findViewById(R.id.lsq_eyeshadow_add).getVisibility());
                        mBeautyPlasticsConfigView.setFilterArgs(mController.getEffect(), Arrays.asList(mController.getEffect().getFilterArg("eyeshadowAlpha")));
                        findViewById(R.id.list_panel).addOnLayoutChangeListener(new OnLayoutChangeListener() {
                            @Override
                            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                                mCosmeticScroll.scrollTo(mEyeshadowPanel.getLeft(), 0);
                                findViewById(R.id.list_panel).removeOnLayoutChangeListener(this);
                            }
                        });
                    } else {
                        mBeautyPlasticsConfigView.setVisibility(View.GONE);
                    }
                    break;
                case R.id.lsq_cosmetic_item_eyeliner:
                    mCurrentType = CosmeticTypes.Types.Eyeliner;
                    mEyeliner.setVisibility(View.GONE);
                    mPreButton = mEyeliner;
                    mLipstickPanel.setVisibility(View.GONE);
                    mBlushPanel.setVisibility(View.GONE);
                    mEyebrowPanel.setVisibility(View.GONE);
                    mEyeshadowPanel.setVisibility(View.GONE);
                    mEyelinerPanel.setVisibility(mEyelinerPanel.getVisibility() == View.GONE ? View.VISIBLE : View.GONE);
                    mEyelashPanel.setVisibility(View.GONE);
                    if (mEyelinerPanel.getVisibility() == View.VISIBLE) {
                        mBeautyPlasticsConfigView.setVisibility(findViewById(R.id.lsq_eyeliner_add).getVisibility());
                        mBeautyPlasticsConfigView.setFilterArgs(mController.getEffect(), Arrays.asList(mController.getEffect().getFilterArg("eyelineAlpha")));
                        findViewById(R.id.list_panel).addOnLayoutChangeListener(new OnLayoutChangeListener() {
                            @Override
                            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                                mCosmeticScroll.scrollTo(mEyelinerPanel.getLeft(), 0);
                                findViewById(R.id.list_panel).removeOnLayoutChangeListener(this);
                            }
                        });
                    } else {
                        mBeautyPlasticsConfigView.setVisibility(View.GONE);
                    }
                    break;
                case R.id.lsq_cosmetic_item_eyelash:
                    mCurrentType = CosmeticTypes.Types.Eyelash;
                    mEyelash.setVisibility(View.GONE);
                    mPreButton = mEyelash;
                    mLipstickPanel.setVisibility(View.GONE);
                    mBlushPanel.setVisibility(View.GONE);
                    mEyebrowPanel.setVisibility(View.GONE);
                    mEyeshadowPanel.setVisibility(View.GONE);
                    mEyelinerPanel.setVisibility(View.GONE);
                    mEyelashPanel.setVisibility(mEyelashPanel.getVisibility() == View.GONE ? View.VISIBLE : View.GONE);
                    if (mEyelashPanel.getVisibility() == View.VISIBLE) {
                        mBeautyPlasticsConfigView.setVisibility(findViewById(R.id.lsq_eyelash_add).getVisibility());
                        mBeautyPlasticsConfigView.setFilterArgs(mController.getEffect(), Arrays.asList(mController.getEffect().getFilterArg("eyelashAlpha")));
                        findViewById(R.id.list_panel).addOnLayoutChangeListener(new OnLayoutChangeListener() {
                            @Override
                            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                                mCosmeticScroll.scrollTo(mEyelashPanel.getLeft(), 0);
                                findViewById(R.id.list_panel).removeOnLayoutChangeListener(this);
                            }
                        });
                    } else {
                        mBeautyPlasticsConfigView.setVisibility(View.GONE);

                    }
                    break;
            }
        }
    };

    private boolean isFirstShow = true;

    private void clearCosmetic() {
        if (types.size() == 0) return;
        AlertDialog.Builder adBuilder = new AlertDialog.Builder(mContext, android.R.style.Theme_Material_Dialog_Alert);
        adBuilder.setTitle(R.string.lsq_text_cosmetic_type);
        adBuilder.setMessage(R.string.lsq_clear_beauty_cosmetic_hit);
        adBuilder.setNegativeButton(R.string.lsq_audioRecording_cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        adBuilder.setPositiveButton(R.string.lsq_audioRecording_next, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mController.clearAllCosmetic();
                types.removeAll(Arrays.asList(CosmeticTypes.Types.values()));
            }
        });
        adBuilder.show();
    }

    private HorizontalScrollView mCosmeticScroll;

    private void initCosmeticView() {
        mController.setPanelClickListener(panelClickListener);

        mCosmeticList = findViewById(R.id.lsq_cosmetic_list);

        mCosmeticClear = findViewById(R.id.lsq_cosmetic_item_clear);
        mCosmeticClear.setOnClickListener(mCosmeticClick);

        mLipstick = findViewById(R.id.lsq_cosmetic_item_lipstick);
        mLipstick.setOnClickListener(mCosmeticClick);
        mLipstickPanel = findViewById(R.id.lsq_lipstick_panel);
        mLipstickPanel.addView(mController.getLipstickPanel().getPanel());

        mBlush = findViewById(R.id.lsq_cosmetic_item_blush);
        mBlush.setOnClickListener(mCosmeticClick);
        mBlushPanel = findViewById(R.id.lsq_blush_panel);
        mBlushPanel.addView(mController.getBlushPanel().getPanel());

        mEyebrow = findViewById(R.id.lsq_cosmetic_item_eyebrow);
        mEyebrow.setOnClickListener(mCosmeticClick);
        mEyebrowPanel = findViewById(R.id.lsq_eyebrow_panel);
        mEyebrowPanel.addView(mController.getEyebrowPanel().getPanel());

        mEyeshadow = findViewById(R.id.lsq_cosmetic_item_eyeshadow);
        mEyeshadow.setOnClickListener(mCosmeticClick);
        mEyeshadowPanel = findViewById(R.id.lsq_eyeshadow_panel);
        mEyeshadowPanel.addView(mController.getEyeshadowPanel().getPanel());

        mEyeliner = findViewById(R.id.lsq_cosmetic_item_eyeliner);
        mEyeliner.setOnClickListener(mCosmeticClick);
        mEyelinerPanel = findViewById(R.id.lsq_eyeliner_panel);
        mEyelinerPanel.addView(mController.getEyelinerPanel().getPanel());

        mEyelash = findViewById(R.id.lsq_cosmetic_item_eyelash);
        mEyelash.setOnClickListener(mCosmeticClick);
        mEyelashPanel = findViewById(R.id.lsq_eyelash_panel);
        mEyelashPanel.addView(mController.getEyelashPanel().getPanel());
        mEyelashPanel.setVisibility(View.VISIBLE);
        mEyelashPanel.requestLayout();
        mEyelashPanel.setVisibility(View.GONE);

        mCosmeticScroll = findViewById(R.id.lsq_cosmetic_scroll_view);
    }

    /*********************************** 微整形 ********************/
    /**
     * 美颜微整形是否选中
     */
    private boolean isBeautyChecked = true;

    private boolean isCosmeticChecked = false;
    /**
     * 美颜适配器
     */
    private BeautyRecyclerAdapter mBeautyRecyclerAdapter;
    /**
     * 微整形适配器
     */
    private BeautyPlasticRecyclerAdapter mBeautyPlasticRecyclerAdapter;


    /**
     * 微整形调节栏
     */
    private ParamsConfigView mBeautyPlasticsConfigView;
    /**
     * 微整形默认值  Float 为进度值
     */
    private HashMap<String, Float> mDefaultBeautyPercentParams = new HashMap<String, Float>() {
        {
            put("eyeSize", 0.3f);
            put("chinSize", 0.5f);
            put("cheekNarrow", 0.0f);
            put("smallFace", 0.0f);
            put("noseSize", 0.2f);
            put("mouthWidth", 0.5f);
            put("lips", 0.5f);
            put("philterum", 0.5f);
            put("archEyebrow", 0.5f);
            put("browPosition", 0.5f);
            put("jawSize", 0.5f);
            put("cheekLowBoneNarrow", 0.0f);
            put("eyeAngle", 0.5f);
            put("eyeInnerConer", 0.0f);
            put("eyeOuterConer", 0.0f);
            put("eyeDis", 0.5f);
            put("eyeHeight", 0.5f);
            put("forehead", 0.5f);
            put("cheekBoneNarrow", 0.0f);
        }
    };

    /**
     * 微整形参数
     */
    private List<String> mBeautyPlastics = new ArrayList() {
        {
            add("reset");
            add("eyeSize");
            add("chinSize");
            add("cheekNarrow");
            add("smallFace");
            add("noseSize");
            add("noseHeight");
            add("mouthWidth");
            add("lips");
            add("philterum");
            add("archEyebrow");
            add("browPosition");
            add("jawSize");
            add("cheekLowBoneNarrow");
            add("eyeAngle");
            add("eyeInnerConer");
            add("eyeOuterConer");
            add("eyeDis");
            add("eyeHeight");
            add("forehead");
            add("cheekBoneNarrow");
        }
    };

    /**
     * 美型调节栏
     */
    private ParamsConfigView.FilterConfigViewSeekBarDelegate mBeautyPlasticConfigViewSeekBarDelegate =
            new ParamsConfigView.FilterConfigViewSeekBarDelegate() {
                @Override
                public void onSeekbarDataChanged(FilterConfigSeekbar seekbar, SelesParameters.FilterArg arg) {
                    if (isBeautyChecked)
                        submitSkinParamter(arg.getKey(), seekbar.getSeekbar().getProgress());
                    else
                        submitPlasticFaceParamter(arg.getKey(), seekbar.getSeekbar().getProgress());
                }
            };

    /**
     * 美颜Item点击事件
     */
    BeautyRecyclerAdapter.OnBeautyItemClickListener beautyItemClickListener =
            new BeautyRecyclerAdapter.OnBeautyItemClickListener() {
                @Override
                public void onChangeSkin(View v, String key, TuSdkMediaSkinFaceEffect.SkinFaceType skinMode) {
                    mBeautyPlasticsConfigView.setVisibility(VISIBLE);
                    switchConfigSkin(skinMode);

                    // 获取key值并显示到调节栏
                    TuSdkMediaEffectData mediaEffectData = mCamera.mediaEffectsWithType(TuSdkMediaEffectDataTypeSkinFace).get(0);
                    SelesParameters.FilterArg filterArg = mediaEffectData.getFilterArg(key);
                    mBeautyPlasticsConfigView.setFilterArgs(mediaEffectData, Arrays.asList(filterArg));
                }

                @Override
                public void onClear() {
                    hideBeautyBarLayout();

                    mCamera.removeMediaEffectsWithType(TuSdkMediaEffectDataTypeSkinFace);
                    isBeautyClose = true;
                }
            };

    /**
     * 微整形Item点击事件
     */
    BeautyPlasticRecyclerAdapter.OnBeautyPlasticItemClickListener beautyPlasticItemClickListener = new BeautyPlasticRecyclerAdapter.OnBeautyPlasticItemClickListener() {
        @Override
        public void onItemClick(View v, int position) {
            mBeautyPlasticsConfigView.setVisibility(VISIBLE);
            switchBeautyPlasticConfig(position);
        }

        @Override
        public void onClear() {

            hideBeautyBarLayout();

            AlertDialog.Builder adBuilder = new AlertDialog.Builder(mContext, android.R.style.Theme_Material_Dialog_Alert);
            adBuilder.setTitle(R.string.lsq_text_beauty_type);
            adBuilder.setMessage(R.string.lsq_clear_beauty_plastic_hit);
            adBuilder.setNegativeButton(R.string.lsq_audioRecording_cancel, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            adBuilder.setPositiveButton(R.string.lsq_audioRecording_next, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    for (String key : mDefaultBeautyPercentParams.keySet()) {
                        TLog.e("key -- %s", mDefaultBeautyPercentParams.get(key));
                        submitPlasticFaceParamter(key, mDefaultBeautyPercentParams.get(key));
                    }                    dialog.dismiss();
                }
            });
            adBuilder.show();
        }
    };

    /**
     * 隐藏美颜参数调节栏
     */
    private void hideBeautyBarLayout() {
        mBeautyPlasticsConfigView.setVisibility(GONE);

    }

    /**
     * 切换美颜、微整形Tab
     *
     * @param view
     */
    private void switchBeautyConfigTab(View view) {
        switch (view.getId()) {
            // 美颜
            case R.id.lsq_beauty_tab:
                isBeautyChecked = true;
                ((TextView) findViewById(R.id.lsq_beauty_tab)).setTextColor(getResources().getColor(R.color.lsq_color_white));
                ((TextView) findViewById(R.id.lsq_beauty_plastic_tab)).setTextColor(getResources().getColor(R.color.lsq_alpha_white_66));
                ((TextView) findViewById(R.id.lsq_cosmetic_tab)).setTextColor(getResources().getColor(R.color.lsq_alpha_white_66));
                findViewById(R.id.lsq_beauty_tab_line).setBackgroundResource(R.color.lsq_color_white);
                findViewById(R.id.lsq_beauty_plastic_tab_line).setBackgroundResource(R.color.lsq_alpha_white_00);
                findViewById(R.id.lsq_cosmetic_tab_line).setBackgroundResource(R.color.lsq_alpha_white_00);
                mCosmeticList.setVisibility(View.GONE);
                mBeautyRecyclerView.setVisibility(View.VISIBLE);
                mBeautyRecyclerView.setAdapter(mBeautyRecyclerAdapter);
                hideBeautyBarLayout();
                break;
            // 微整形
            case R.id.lsq_beauty_plastic_tab:
                isBeautyChecked = false;
                ((TextView) findViewById(R.id.lsq_beauty_tab)).setTextColor(getResources().getColor(R.color.lsq_alpha_white_66));
                ((TextView) findViewById(R.id.lsq_beauty_plastic_tab)).setTextColor(getResources().getColor(R.color.lsq_color_white));
                ((TextView) findViewById(R.id.lsq_cosmetic_tab)).setTextColor(getResources().getColor(R.color.lsq_alpha_white_66));

                findViewById(R.id.lsq_beauty_tab_line).setBackgroundResource(R.color.lsq_alpha_white_00);
                findViewById(R.id.lsq_beauty_plastic_tab_line).setBackgroundResource(R.color.lsq_color_white);
                findViewById(R.id.lsq_cosmetic_tab_line).setBackgroundResource(R.color.lsq_alpha_white_00);
                mCosmeticList.setVisibility(View.GONE);
                mBeautyRecyclerView.setVisibility(View.VISIBLE);
                mBeautyRecyclerView.setAdapter(mBeautyPlasticRecyclerAdapter);
                mBeautyRecyclerView.scrollToPosition(mBeautyPlasticRecyclerAdapter.getCurrentPos() - 1);
                int currentPos = mBeautyPlasticRecyclerAdapter.getCurrentPos();
                if (currentPos != -1){
                    switchBeautyPlasticConfig(currentPos);
                } else {
                    hideBeautyBarLayout();
                }
                break;
            //美妆
            case R.id.lsq_cosmetic_tab:
                isBeautyChecked = false;
                isCosmeticChecked = true;
                ((TextView) findViewById(R.id.lsq_cosmetic_tab)).setTextColor(getResources().getColor(R.color.lsq_color_white));
                findViewById(R.id.lsq_cosmetic_tab_line).setBackgroundResource(R.color.lsq_color_white);

                ((TextView) findViewById(R.id.lsq_beauty_tab)).setTextColor(getResources().getColor(R.color.lsq_alpha_white_66));
                ((TextView) findViewById(R.id.lsq_beauty_plastic_tab)).setTextColor(getResources().getColor(R.color.lsq_alpha_white_66));
                findViewById(R.id.lsq_beauty_tab_line).setBackgroundResource(R.color.lsq_alpha_white_00);
                findViewById(R.id.lsq_beauty_plastic_tab_line).setBackgroundResource(R.color.lsq_alpha_white_00);

                mCosmeticList.setVisibility(View.VISIBLE);
                mBeautyRecyclerView.setVisibility(View.GONE);
                if (mCurrentType != null) {
                    mBeautyPlasticsConfigView.setVisibility(View.VISIBLE);
                    switch (mCurrentType) {
                        case Lipstick:
                            mBeautyPlasticsConfigView.setFilterArgs(mController.getEffect(), Arrays.asList(mController.getEffect().getFilterArg("lipAlpha")));
                            break;
                        case Blush:
                            mBeautyPlasticsConfigView.setFilterArgs(mController.getEffect(), Arrays.asList(mController.getEffect().getFilterArg("blushAlpha")));
                            break;
                        case Eyebrow:
                            mBeautyPlasticsConfigView.setFilterArgs(mController.getEffect(), Arrays.asList(mController.getEffect().getFilterArg("eyebrowAlpha")));
                            break;
                        case Eyeshadow:
                            mBeautyPlasticsConfigView.setFilterArgs(mController.getEffect(), Arrays.asList(mController.getEffect().getFilterArg("eyeshadowAlpha")));
                            break;
                        case Eyeliner:
                            mBeautyPlasticsConfigView.setFilterArgs(mController.getEffect(), Arrays.asList(mController.getEffect().getFilterArg("eyelineAlpha")));
                            break;
                        case Eyelash:
                            mBeautyPlasticsConfigView.setFilterArgs(mController.getEffect(), Arrays.asList(mController.getEffect().getFilterArg("eyelashAlpha")));
                            break;
                    }
                } else {
                    hideBeautyBarLayout();
                }


                if (mCamera.mediaEffectsWithType(TuSdkMediaEffectDataTypeCosmetic).size() == 0) {
                    TuSdkMediaCosmeticEffectData data = mController.getEffect();
                    mCamera.addMediaEffectData(data);
                    for (String key : CosmeticPanelController.mDefaultCosmeticPercentParams.keySet()) {
                        data.submitParameter(key, CosmeticPanelController.mDefaultCosmeticPercentParams.get(key));
                        data.getFilterArg(key).setDefaultPercent(CosmeticPanelController.mDefaultCosmeticPercentParams.get(key));
                    }
                }
                break;
        }
    }

    /**
     * 设置美颜、微整形视图状态
     *
     * @param isVisible true显示false隐藏
     */
    private void setBeautyViewVisible(boolean isVisible) {

        if (isVisible) {
            setBeautyLayout(true);
            setTextButtonDrawableTop(mBeautyButton, R.drawable.video_nav_ic_beauty_selected);

            TextView lsq_beauty_tab = findViewById(R.id.lsq_beauty_tab);
            TextView lsq_beauty_shape_tab = findViewById(R.id.lsq_beauty_plastic_tab);
            TextView lsq_cosmetic_tab = findViewById(R.id.lsq_cosmetic_tab);

            lsq_beauty_tab.setTag(0);
            lsq_beauty_shape_tab.setTag(1);

            lsq_beauty_tab.setOnClickListener(onClickListener);
            lsq_beauty_shape_tab.setOnClickListener(onClickListener);
            lsq_cosmetic_tab.setOnClickListener(onClickListener);

            if (isCosmeticChecked) {
                switchBeautyConfigTab(lsq_cosmetic_tab);
            } else {
                switchBeautyConfigTab(isBeautyChecked ? lsq_beauty_tab : lsq_beauty_shape_tab);
            }
        } else {
            setBeautyLayout(false);
            setTextButtonDrawableTop(mBeautyButton, R.drawable.video_nav_ic_beauty);
        }
    }

    /**
     * 设置美颜视图
     *
     * @param isVisible 是否可见
     */
    private void setBeautyLayout(boolean isVisible) {
        mSmartBeautyTabLayout.setVisibility(isVisible ? VISIBLE : GONE);
    }

    /**
     * 切换美颜预设按键
     *
     * @param skinMode true 自然(精准)美颜 false 极致美颜
     */
    private void switchConfigSkin(TuSdkMediaSkinFaceEffect.SkinFaceType skinMode) {
        TuSdkMediaSkinFaceEffect skinFaceEffect = new TuSdkMediaSkinFaceEffect(skinMode);


        // 美白
        SelesParameters.FilterArg whiteningArgs = skinFaceEffect.getFilterArg("whitening");//whiten whitening
        whiteningArgs.setMaxValueFactor(1.0f);//设置最大值限制
        whiteningArgs.setDefaultPercent(0.3f);
        // 磨皮
        SelesParameters.FilterArg smoothingArgs = skinFaceEffect.getFilterArg("smoothing");//smooth smoothing
        smoothingArgs.setMaxValueFactor(1.0f);//设置最大值限制
        smoothingArgs.setDefaultPercent(0.8f);
        // 红润
        SelesParameters.FilterArg ruddyArgs = skinFaceEffect.getFilterArg(skinMode!= TuSdkMediaSkinFaceEffect.SkinFaceType.Beauty ? "ruddy" : "sharpen");//sharpen ruddy
        ruddyArgs.setMaxValueFactor(1.0f);//设置最大值限制
        ruddyArgs.setDefaultPercent(skinMode!= TuSdkMediaSkinFaceEffect.SkinFaceType.Beauty ? 0.4f : 0.6f);

        whiteningArgs.setPrecentValue(0.3f);//设置默认显示

        smoothingArgs.setPrecentValue(0.8f);//设置默认显示

        ruddyArgs.setPrecentValue(skinMode!= TuSdkMediaSkinFaceEffect.SkinFaceType.Beauty ? 0.4f : 0.6f);

        if (mCamera.mediaEffectsWithType(TuSdkMediaEffectDataTypeSkinFace) == null ||
                mCamera.mediaEffectsWithType(TuSdkMediaEffectDataTypeSkinFace).size() == 0) {

            mCamera.addMediaEffectData(skinFaceEffect);
        } else {
            TuSdkMediaSkinFaceEffect oldSkinFaceEffect = (TuSdkMediaSkinFaceEffect) mCamera.mediaEffectsWithType(TuSdkMediaEffectDataTypeSkinFace).get(0);
            mCamera.addMediaEffectData(skinFaceEffect);

            for (SelesParameters.FilterArg filterArg : oldSkinFaceEffect.getFilterArgs()) {
                SelesParameters.FilterArg arg = skinFaceEffect.getFilterArg(filterArg.getKey());
                if (arg != null)
                    arg.setPrecentValue(filterArg.getPrecentValue());
            }

            skinFaceEffect.submitParameters();

            if (!oldSkinFaceEffect.getFilterWrap().equals(skinFaceEffect.getFilterWrap())) {
                // 滤镜名显示
                showHitTitle(TuSdkContext.getString(getSkinModeTitle(skinMode)));
            }
        }
        isBeautyClose = false;
    }

    private String getSkinModeTitle(TuSdkMediaSkinFaceEffect.SkinFaceType skinMode){
        switch (skinMode){
            case SkinNatural:
                return "lsq_beauty_skin_precision";
            case SkinMoist:
                return "lsq_beauty_skin_extreme";
            case Beauty:
                return "lsq_beauty_skin_beauty";
        }
        return "";
    }

    /**
     * 应用美肤
     *
     * @param key
     * @param progress
     */
    private void submitSkinParamter(String key, float progress) {
        List<TuSdkMediaEffectData> filterEffects = mCamera.mediaEffectsWithType(TuSdkMediaEffectData.TuSdkMediaEffectDataType.TuSdkMediaEffectDataTypeSkinFace);

        if (filterEffects.size() == 0) return;

        // 只能添加一个滤镜特效
        TuSdkMediaSkinFaceEffect filterEffect = (TuSdkMediaSkinFaceEffect) filterEffects.get(0);
        filterEffect.submitParameter(key, progress);
    }

    /**
     * 切换微整形类型
     *
     * @param position
     */
    private void switchBeautyPlasticConfig(int position) {
        if (mCamera.mediaEffectsWithType(TuSdkMediaEffectDataTypePlasticFace).size() == 0) {
            // 添加一个默认微整形特效
            TuSdkMediaPlasticFaceEffect plasticFaceEffect = new TuSdkMediaPlasticFaceEffect();
            mCamera.addMediaEffectData(plasticFaceEffect);
//            for (SelesParameters.FilterArg arg : plasticFaceEffect.getFilterArgs()) {
//                if (arg.equalsKey("eyeSize")) {// 大眼
//                    arg.setMaxValueFactor(0.85f);// 最大值限制
//                }
//                if (arg.equalsKey("chinSize")) {// 瘦脸
//                    arg.setMaxValueFactor(0.9f);// 最大值限制
//                }
//                if (arg.equalsKey("noseSize")) {// 瘦鼻
//                    arg.setMaxValueFactor(0.6f);// 最大值限制
//                }
//
//            }
            for (String key : mDefaultBeautyPercentParams.keySet()) {
                TLog.e("key -- %s", mDefaultBeautyPercentParams.get(key));
                submitPlasticFaceParamter(key, mDefaultBeautyPercentParams.get(key));
            }

        }

        TuSdkMediaEffectData effectData = mCamera.mediaEffectsWithType(TuSdkMediaEffectDataTypePlasticFace).get(0);
        SelesParameters.FilterArg filterArg = effectData.getFilterArg(mBeautyPlastics.get(position));

//        TLog.e("filterArg -- %s",filterArg.getPrecentValue());

        mBeautyPlasticsConfigView.setFilterArgs(null, Arrays.asList(filterArg));
    }

    /**
     * 应用整形值
     *
     * @param key
     * @param progress
     */
    private void submitPlasticFaceParamter(String key, float progress) {
        List<TuSdkMediaEffectData> filterEffects = mCamera.mediaEffectsWithType(TuSdkMediaEffectData.TuSdkMediaEffectDataType.TuSdkMediaEffectDataTypePlasticFace);

        if (filterEffects.size() == 0) return;

        // 只能添加一个滤镜特效
        TuSdkMediaPlasticFaceEffect filterEffect = (TuSdkMediaPlasticFaceEffect) filterEffects.get(0);
        filterEffect.submitParameter(key, progress);
    }


    /******************************** 拍照 ************************/
    /**
     * 更新拍照预览界面
     *
     * @param isShow true显示false隐藏
     */
    private void updatePreviewImageLayoutStatus(boolean isShow) {
        findViewById(R.id.lsq_preview_image_layout).setVisibility(isShow ? VISIBLE : GONE);
    }

    /**
     * 显示拍照视图
     *
     * @param bitmap
     */
    private void presentPreviewLayout(Bitmap bitmap) {
        if (bitmap != null) {
            mCaptureBitmap = bitmap;
            updatePreviewImageLayoutStatus(true);
            mPreViewImageView.setImageBitmap(bitmap);
            // 暂停相机
            mCamera.pauseCameraCapture();
        }
    }

    /**
     * 保存拍照资源
     */
    public void saveResource() {
        updatePreviewImageLayoutStatus(false);
        File flie = AlbumHelper.getAlbumFile();
        ImageSqlHelper.saveJpgToAblum(mContext, mCaptureBitmap, 0, flie);
        refreshFile(flie);
        destroyBitmap();
        TuSdk.messageHub().showToast(mContext, R.string.lsq_image_save_ok);
        mCamera.resumeCameraCapture();
    }

    /**
     * 刷新相册
     *
     * @param file
     */
    public void refreshFile(File file) {
        if (file == null) {
            TLog.e("refreshFile file == null");
            return;
        }

        Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        Uri uri = Uri.fromFile(file);
        intent.setData(uri);
        mContext.sendBroadcast(intent);
    }

    /**
     * 删除拍照资源
     */
    public void deleteResource() {
        updatePreviewImageLayoutStatus(false);
        destroyBitmap();
        mCamera.resumeCameraCapture();
    }

    /**
     * 销毁拍照图片
     */
    private void destroyBitmap() {
        if (mCaptureBitmap == null) return;

        if (!mCaptureBitmap.isRecycled())
            mCaptureBitmap.recycle();

        mCaptureBitmap = null;
    }

    /********************************** 点击事件 ************************/
    private OnClickListener onClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                // 关闭
                case R.id.lsq_closeButton:
                    if (getDelegate() != null) getDelegate().finishRecordActivity();
                    break;
                // 切换摄像头
                case R.id.lsq_switchButton:
                    mCamera.rotateCamera();
                    mLightingOpen.setTextColor(getResources().getColor(R.color.lsq_color_white));
                    mLightingClose.setTextColor(getResources().getColor(R.color.lsq_widget_speedbar_button_bg));
                    if (mCamera.isFrontFacingCameraPresent()) {
                        mCamera.setFocusMode(CameraConfigs.CameraAutoFocus.Auto, new PointF(0.5f, 0.5f));
                    }
                    break;
                // 美颜按钮显示美颜布局
                case R.id.lsq_beautyButton:
                    setFilterContentVisible(false);
                    setBottomViewVisible(mSmartBeautyTabLayout.getVisibility() == VISIBLE);
                    setBeautyViewVisible(mSmartBeautyTabLayout.getVisibility() == GONE);
                    setStickerVisible(false);
                    setSpeedViewVisible(false);
                    mCamera.getFocusTouchView().isShowFoucusView(false);
                    break;
                // 速度
                case R.id.lsq_speedButton:
                    setFilterContentVisible(false);
                    setBottomViewVisible(true);
                    setStickerVisible(false);
                    setBeautyViewVisible(false);
                    setSpeedViewVisible(mSpeedModeBar.getVisibility() == GONE);
                    break;
                // 更多设置
                case R.id.lsq_moreButton:
                    mMoreConfigLayout.setVisibility(mMoreConfigLayout.getVisibility() == VISIBLE ? GONE : VISIBLE);
                    setTextButtonDrawableTop(mMoreButton, mMoreConfigLayout.getVisibility() == VISIBLE ? R.drawable.video_nav_ic_more_selected : R.drawable.video_nav_ic_more);
                    break;
                // 自动对焦开启
                case R.id.lsq_focus_open:
                    mFocusOpen.setTextColor(getResources().getColor(R.color.lsq_widget_speedbar_button_bg));
                    mFocusClose.setTextColor(getResources().getColor(R.color.lsq_color_white));

                    mCamera.setDisableContinueFocus(false);
                    break;
                // 自动对焦关闭
                case R.id.lsq_focus_close:
                    mFocusOpen.setTextColor(getResources().getColor(R.color.lsq_color_white));
                    mFocusClose.setTextColor(getResources().getColor(R.color.lsq_widget_speedbar_button_bg));

                    mCamera.setDisableContinueFocus(true);
                    break;
                // 闪光灯开启
                case R.id.lsq_lighting_open:
                    updateFlashMode(CameraConfigs.CameraFlash.Torch);
                    break;
                // 闪光灯关闭
                case R.id.lsq_lighting_close:
                    updateFlashMode(CameraConfigs.CameraFlash.Off);
                    break;
                // 美颜
                case R.id.lsq_beauty_tab:
                    isCosmeticChecked = false;
                    switchBeautyConfigTab(v);
                    break;
                // 微整形
                case R.id.lsq_beauty_plastic_tab:
                    isCosmeticChecked = false;
                    switchBeautyConfigTab(v);
                    break;
                case R.id.lsq_cosmetic_tab:
                    isCosmeticChecked = true;
                    switchBeautyConfigTab(v);
                    break;
                // 滤镜
                case R.id.lsq_tab_filter_btn:
                    setBeautyViewVisible(false);
                    setBottomViewVisible(false);
                    setSpeedViewVisible(false);
                    setStickerVisible(false);
                    showFilterLayout();
                    mCamera.getFocusTouchView().isShowFoucusView(false);
                    break;
                // 贴纸
                case R.id.lsq_stickerWrap:
                    setFilterContentVisible(false);
                    setBeautyViewVisible(false);
                    setSpeedViewVisible(false);
                    setBottomViewVisible(false);
                    showStickerLayout();
                    mCamera.getFocusTouchView().isShowFoucusView(false);
                    break;
                // 比例
                case R.id.lsq_radio_1_1:
                    updateCameraRatio(RatioType.ratio_1_1);
                    break;
                case R.id.lsq_radio_3_4:
                    updateCameraRatio(RatioType.ratio_3_4);
                    break;
                case R.id.lsq_radio_full:
                    updateCameraRatio(RatioType.ratio_orgin);
                    break;
                // 视频回退
                case R.id.lsq_backWrap:
                    // 点击后退按钮删除上一条视频
                    if (mCamera.getRecordingFragmentSize() > 0) {
                        mCamera.popVideoFragment();
                        mRecordProgress.removePreSegment();

                        if (interuptLayout.getChildCount() != 0) {
                            interuptLayout.removeViewAt(interuptLayout.getChildCount() - 1);
                        }
                        // 删除最后一段，重置录制状态
                        if (mCamera.getRecordingFragmentSize() == 0) {
                            mCamera.cancelRecording();
                        }
                    }
                    // 刷新按钮状态
                    setViewHideOrVisible(true);
                    break;
                // 保存录制视频
                case R.id.lsq_confirmWrap:
                    if (mCamera.getMovieDuration() < Constants.MIN_RECORDING_TIME) {
                        String msg = getStringFromResource("min_recordTime") + Constants.MIN_RECORDING_TIME + "s";
                        TuSdk.messageHub().showToast(mContext, msg);
                        return;
                    }
                    // 启动录制隐藏比例调节按钮
                    mCamera.stopRecording();
                    initRecordProgress();
                    setViewHideOrVisible(true);
                    break;
                // 取消拍摄
                case R.id.lsq_backButton:
                    deleteResource();
                    break;
                // 保存拍摄
                case R.id.lsq_saveImageButton:
                    saveResource();
                    break;
                // 取消贴纸
                case R.id.lsq_cancel_button:
                    mCamera.removeMediaEffectsWithType(mPropsItemCategories.get(mPropsItemViewPager.getCurrentItem()).getMediaEffectType());
                    mPropsItemPagerAdapter.notifyAllPageData();
                    break;
            }
        }
    };

    /**
     * 改变闪关灯状态
     *
     * @param cameraFlash
     */
    public void updateFlashMode(CameraConfigs.CameraFlash cameraFlash) {
        if (mCamera.isFrontFacingCameraPresent()) return;
        switch (cameraFlash) {
            case Off:
                mLightingOpen.setTextColor(getResources().getColor(R.color.lsq_color_white));
                mLightingClose.setTextColor(getResources().getColor(R.color.lsq_widget_speedbar_button_bg));

                mCamera.setFlashMode(cameraFlash);
                break;
            case Torch:
                mLightingOpen.setTextColor(getResources().getColor(R.color.lsq_widget_speedbar_button_bg));
                mLightingClose.setTextColor(getResources().getColor(R.color.lsq_color_white));

                mCamera.setFlashMode(cameraFlash);
                break;
        }
    }

    /**
     * 更新相机比例
     *
     * @param type
     */
    private void updateCameraRatio(int type) {
        // 只要开始录制就不可切换
        if (mCamera.getRecordingFragmentSize() > 0) return;
        switch (type) {
            case RatioType.ratio_1_1:
                mRadio1_1.setImageResource(R.drawable.lsq_video_popup_ic_scale_square_selected);
                mRadio3_4.setImageResource(R.drawable.lsq_video_popup_ic_scale_3_4);
                mRadioFull.setImageResource(R.drawable.lsq_video_popup_ic_scale_full);
                switchCameraRatio(RatioType.ratio_1_1);
                break;
            case RatioType.ratio_3_4:
                mRadio1_1.setImageResource(R.drawable.lsq_video_popup_ic_scale_square);
                mRadio3_4.setImageResource(R.drawable.lsq_video_popup_ic_scale_3_4_selected);
                mRadioFull.setImageResource(R.drawable.lsq_video_popup_ic_scale_full);
                switchCameraRatio(RatioType.ratio_3_4);
                break;
            case RatioType.ratio_orgin:
                mRadio1_1.setImageResource(R.drawable.lsq_video_popup_ic_scale_square);
                mRadio3_4.setImageResource(R.drawable.lsq_video_popup_ic_scale_3_4);
                mRadioFull.setImageResource(R.drawable.lsq_video_popup_ic_scale_full_selected);
                switchCameraRatio(RatioType.ratio_orgin);
                break;
        }
    }

    /**
     * 改变屏幕比例 录制状态不可改变
     *
     * @param type 参数类型 RatioType
     */
    private void switchCameraRatio(int type) {
        if (mCamera == null || !mCamera.canChangeRatio()) return;

        // 设置预览区域顶部偏移量 必须在 changeRegionRatio 之前设置
        mCamera.getRegionHandler().setOffsetTopPercent(getPreviewOffsetTopPercent(type));
        mCamera.changeRegionRatio(RatioType.ratio(type));
        mCamera.setRegionRatio(RatioType.ratio(type));

        // 计算保存比例
        mCamera.getVideoEncoderSetting().videoSize = TuSdkSize.create((int) (mCamera.getCameraPreviewSize().width * RatioType.ratio(type)), mCamera.getCameraPreviewSize().width);

    }

    /**
     * 获取当前 Ratio 预览画面顶部偏移百分比（默认：-1 居中显示 取值范围：0-1）
     *
     * @param ratioType
     * @return
     */
    protected float getPreviewOffsetTopPercent(int ratioType) {
        if (ratioType == RatioType.ratio_1_1) return 0.1f;
        // 置顶
        return 0.f;
    }

    /************************ 录制模式切换 **************************/
    /**
     * 模式按键切换动画
     */
    private ValueAnimator valueAnimator;
    /**
     * 录制按键模式
     */
    private int mRecordMode = RecordType.LONG_CLICK_RECORD;

    private float mPosX, mCurPosX;
    private static final int FLING_MIN_DISTANCE = 20;// 移动最小距离

    private OnTouchListener onModeBarTouchListener = new OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    mPosX = event.getX();
                    mCurPosX = 0;
                    return true;
                case MotionEvent.ACTION_MOVE:
                    mCurPosX = event.getX();
                    // 滑动效果处理
                    if (mCurPosX - mPosX > 0
                            && (Math.abs(mCurPosX - mPosX) > FLING_MIN_DISTANCE)) {
                        //向左滑动
                        if (mRecordMode == RecordType.LONG_CLICK_RECORD) {
                            switchCameraModeButton(RecordType.CAPTURE);
                        } else if (mRecordMode == RecordType.SHORT_CLICK_RECORD) {
                            switchCameraModeButton(RecordType.LONG_CLICK_RECORD);
                        }
                        return false;
                    } else if (mCurPosX - mPosX < 0
                            && (Math.abs(mCurPosX - mPosX) > FLING_MIN_DISTANCE)) {
                        //向右滑动
                        if (mRecordMode == RecordType.CAPTURE) {
                            switchCameraModeButton(RecordType.LONG_CLICK_RECORD);
                        } else if (mRecordMode == RecordType.LONG_CLICK_RECORD) {
                            switchCameraModeButton(RecordType.SHORT_CLICK_RECORD);
                        }
                        return false;
                    }
                    return true;
                case MotionEvent.ACTION_UP:
                    // 点击效果处理
                    if (Math.abs(mCurPosX - mPosX) < FLING_MIN_DISTANCE || mCurPosX == 0) {
                        switch (v.getId()) {
                            // 拍照模式
                            case R.id.lsq_shootButton:
                                switchCameraModeButton(RecordType.CAPTURE);
                                break;
                            // 长按录制模式
                            case R.id.lsq_longButton:
                                switchCameraModeButton(RecordType.LONG_CLICK_RECORD);
                                break;
                            // 点击录制模式
                            case R.id.lsq_clickButton:
                                switchCameraModeButton(RecordType.SHORT_CLICK_RECORD);
                                break;
                        }
                        return false;
                    }
            }
            return false;
        }
    };

    /**
     * 切换摄像模式按键
     *
     * @param index
     */
    private void switchCameraModeButton(int index) {
        if (valueAnimator != null && valueAnimator.isRunning() || mRecordMode == index) return;

        // 设置文字颜色
        mShootButton.setTextColor(index == 0 ? getResources().getColor(R.color.lsq_color_white) : getResources().getColor(R.color.lsq_alpha_white_66));
        mLongButton.setTextColor(index == 1 ? getResources().getColor(R.color.lsq_color_white) : getResources().getColor(R.color.lsq_alpha_white_66));
        mClickButton.setTextColor(index == 2 ? getResources().getColor(R.color.lsq_color_white) : getResources().getColor(R.color.lsq_alpha_white_66));

        // 设置偏移位置
        final float[] Xs = getModeButtonWidth();

        float offSet = 0;
        if (mRecordMode == 0 && index == 1)
            offSet = -(Xs[1] - Xs[0]) / 2 - (Xs[2] - Xs[1]) / 2;
        else if (mRecordMode == 0 && index == 2)
            offSet = -(Xs[1] - Xs[0]) / 2 - (Xs[3] - Xs[2]) / 2 - (Xs[2] - Xs[1]);
        else if (mRecordMode == 1 && index == 0)
            offSet = (Xs[1] - Xs[0]) / 2 + (Xs[2] - Xs[1]) / 2;
        else if (mRecordMode == 1 && index == 2)
            offSet = -(Xs[2] - Xs[1]) / 2 - (Xs[3] - Xs[2]) / 2;
        else if (mRecordMode == 2 && index == 0)
            offSet = (Xs[1] - Xs[0]) / 2 + (Xs[2] - Xs[1]) + (Xs[3] - Xs[2]) / 2;
        else if (mRecordMode == 2 && index == 1)
            offSet = (Xs[2] - Xs[1]) / 2 + (Xs[3] - Xs[2]) / 2;

        // 切换动画
        valueAnimator = ValueAnimator.ofFloat(0, offSet);
        valueAnimator.setDuration(300);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float offSet = (float) animation.getAnimatedValue();
                mShootButton.setX(Xs[0] + offSet);
                mLongButton.setX(Xs[1] + offSet);
                mClickButton.setX(Xs[2] + offSet);
            }
        });
        valueAnimator.start();

        // 录制按键背景
        if (index == RecordType.CAPTURE) {
            mSpeedButton.setVisibility(GONE);
            mSpeedModeBar.setVisibility(GONE);
            mChangeAudioLayout.setVisibility(GONE);
        } else if (index == RecordType.LONG_CLICK_RECORD) {
            mSpeedButton.setVisibility(VISIBLE);
            mSpeedModeBar.setVisibility(isSpeedChecked ? VISIBLE : GONE);
            mChangeAudioLayout.setVisibility(VISIBLE);
        } else if (index == RecordType.SHORT_CLICK_RECORD) {
            mSpeedButton.setVisibility(VISIBLE);
            mSpeedModeBar.setVisibility(isSpeedChecked ? VISIBLE : GONE);
            mChangeAudioLayout.setVisibility(VISIBLE);
        }
        updateRecordButtonResource(index);
        mRecordMode = index;
    }

    /**
     * 获取底部拍摄模式按键宽度
     */
    private float[] getModeButtonWidth() {
        float[] Xs = new float[4];
        Xs[0] = mShootButton.getX();
        Xs[1] = mLongButton.getX();
        Xs[2] = mClickButton.getX();
        Xs[3] = mClickButton.getX() + mClickButton.getWidth();
        return Xs;
    }

    /**
     * 切换速率
     *
     * @param selectedSpeedMode
     */
    private void selectSpeedMode(int selectedSpeedMode) {
        int childCount = mSpeedModeBar.getChildCount();

        for (int i = 0; i < childCount; i++) {
            Button btn = (Button) mSpeedModeBar.getChildAt(i);
            int speedMode = Integer.parseInt((String) btn.getTag());

            if (selectedSpeedMode == speedMode) {
                btn.setBackgroundResource(R.drawable.tusdk_view_widget_speed_button_bg);
            } else {
                btn.setBackgroundResource(0);
            }
        }

        // 切换相机速率
        TuSdkRecorderVideoCamera.SpeedMode speedMode = TuSdkRecorderVideoCamera.SpeedMode.values()[selectedSpeedMode];
        mCamera.setSpeedMode(speedMode);
    }

    /**
     * 设置显示隐藏控件（速度按键）
     *
     * @param isVisible 是否可见 true显示false隐藏
     */
    private void setSpeedViewVisible(boolean isVisible) {
        isSpeedChecked = isVisible;
        if (isVisible) {
            setTextButtonDrawableTop(mSpeedButton, R.drawable.video_nav_ic_speed_selected);
            mSpeedModeBar.setVisibility(VISIBLE);
        } else {
            setTextButtonDrawableTop(mSpeedButton, R.drawable.video_nav_ic_speed);
            mSpeedModeBar.setVisibility(GONE);
        }
    }

    /****************************** 视图控制 ****************************/

    /**
     * 设置按键图片
     *
     * @param textButton 按键
     * @param id         图片id
     */
    private void setTextButtonDrawableTop(TuSdkTextButton textButton, @DrawableRes int id) {
        Drawable top = getResources().getDrawable(id);
        top.setBounds(0, 0, top.getMinimumWidth(), top.getMinimumHeight());
        textButton.setCompoundDrawables(null, top, null, null);
    }

    /**
     * 底部控件是否可见 滤镜、美颜、贴纸切换时
     *
     * @param isVisible 是否可见
     */
    private void setBottomViewVisible(boolean isVisible) {
        mBottomBarLayout.setVisibility(isVisible ? VISIBLE : GONE);
        mRecordButton.setVisibility(isVisible ? VISIBLE : GONE);
        mRecordModeBarLayout.setVisibility(isVisible && mCamera.getRecordingFragmentSize() <= 0 ? VISIBLE : GONE);
        mRollBackButton.setVisibility(isVisible && mCamera.getRecordingFragmentSize() > 0 ? VISIBLE : GONE);
    }

    /**
     * 设置显示隐藏控件（录制、非录制状态下）
     *
     * @param isVisible 是否可见
     */
    private void setViewHideOrVisible(boolean isVisible) {
        int visibleState = isVisible ? VISIBLE : GONE;

        mTopBar.setVisibility(visibleState);
        mSpeedModeBar.setVisibility(isVisible && isSpeedChecked ? visibleState : GONE);
        mBottomBarLayout.setVisibility(visibleState);
        mRecordModeBarLayout.setVisibility(visibleState);
        mConfirmButton.setVisibility(GONE);
        mRollBackButton.setVisibility(GONE);

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 2);

        if (mCamera.getRecordingFragmentSize() > 0) {
            layoutParams = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1);
            mConfirmButton.setVisibility(visibleState);
            mRollBackButton.setVisibility(visibleState);
            mRecordModeBarLayout.setVisibility(GONE);
        }
        mFilterButton.setLayoutParams(layoutParams);
    }

    /**
     * 改变录制按钮视图
     *
     * @param type
     */
    private void updateRecordButtonResource(int type) {
        switch (type) {
            case RecordType.CAPTURE:
                mRecordButton.setBackgroundResource(R.drawable.tusdk_view_widget_shoot);
                mRecordButton.setImageResource(0);
                break;
            case RecordType.LONG_CLICK_RECORD:
                mRecordButton.setBackgroundResource(R.drawable.tusdk_view_widget_record_unpressed);
                mRecordButton.setImageResource(0);
                break;
            case RecordType.SHORT_CLICK_RECORD:
                mRecordButton.setBackgroundResource(R.drawable.tusdk_view_widget_record_unpressed);
                mRecordButton.setImageResource(R.drawable.video_ic_recording);
                break;
            case RecordType.LONG_CLICK_RECORDING:
                mRecordButton.setBackgroundResource(R.drawable.tusdk_view_widget_record_pressed);
                mRecordButton.setImageResource(0);
                break;
            case RecordType.SHORT_CLICK_RECORDING:
                mRecordButton.setBackgroundResource(R.drawable.tusdk_view_widget_record_pressed);
                mRecordButton.setImageResource(R.drawable.video_ic_recording);
                break;

        }
    }

    /**
     * 设置滤镜视图
     *
     * @param isVisible 是否可见
     */
    private void setFilterContentVisible(boolean isVisible) {
        mFilterContent.setVisibility(isVisible ? VISIBLE : INVISIBLE);
    }


    /********************************** 回调事件 ***********************/

    /**
     * 录制状态改变回调
     *
     * @param state     录制状态
     * @param recording 是否正在录制中
     */
    public void updateMovieRecordState(TuSdkRecorderVideoCamera.RecordState state, boolean recording) {

        if (state == TuSdkRecorderVideoCamera.RecordState.Recording) // 开始录制
        {
            if (mRecordMode == RecordType.LONG_CLICK_RECORD)
                updateRecordButtonResource(RecordType.LONG_CLICK_RECORDING);
            else
                updateRecordButtonResource(RecordType.SHORT_CLICK_RECORDING);
            setViewHideOrVisible(false);
            mMoreConfigLayout.setVisibility(GONE);
            setTextButtonDrawableTop(mMoreButton, false ? R.drawable.video_nav_ic_more_selected : R.drawable.video_nav_ic_more);

        } else if (state == TuSdkRecorderVideoCamera.RecordState.Paused) // 已暂停录制
        {
            if (mRecordProgress.getProgress() != 0) {
                addInteruptPoint(TuSdkContext.getDisplaySize().width * mRecordProgress.getProgress());
            }
            mRecordProgress.pauseRecord();
            setViewHideOrVisible(true);
            updateRecordButtonResource(mRecordMode);
        } else if (state == TuSdkRecorderVideoCamera.RecordState.RecordCompleted) //录制完成弹出提示（续拍模式下录过程中超过最大时间时调用）
        {
            String msg = getStringFromResource("lsq_record_completed");
            TuSdk.messageHub().showToast(mContext, msg);

            if (mRecordProgress.getProgress() != 0) {
                addInteruptPoint(TuSdkContext.getDisplaySize().width * 0.999f);
            }
            updateRecordButtonResource(mRecordMode);
            setViewHideOrVisible(true);

        } else if (state == TuSdkRecorderVideoCamera.RecordState.Saving) // 正在保存视频
        {
            String msg = getStringFromResource("new_movie_saving");
            TuSdk.messageHub().setStatus(mContext, msg);
        } else if (state == TuSdkRecorderVideoCamera.RecordState.SaveCompleted) {

            String msg = getStringFromResource("lsq_video_save_ok");
            TuSdk.messageHub().showToast(mContext, msg);

            updateRecordButtonResource(mRecordMode);
            setViewHideOrVisible(true);
        }
    }

    /**
     * 添加视频断点标记
     *
     * @param margingLeft
     */
    private void addInteruptPoint(float margingLeft) {
        // 添加断点标记
        Button interuptBtn = new Button(mContext);
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(2,
                LayoutParams.MATCH_PARENT);

        interuptBtn.setBackgroundColor(TuSdkContext.getColor("lsq_progress_interupt_color"));
        lp.setMargins((int) Math.ceil(margingLeft), 0, 0, 0);
        interuptBtn.setLayoutParams(lp);
        interuptLayout.addView(interuptBtn);
    }

    /**
     * 录制进度回调
     *
     * @param progress
     * @param durationTime
     */
    public void updateViewOnMovieRecordProgressChanged(float progress, float durationTime) {
        TLog.e("progress -- %s durationTime -- %s", progress, durationTime);
        mRecordProgress.setProgress(progress);
    }

    /**
     * 录制错误时更新视图显示
     *
     * @param error
     * @param isRecording
     */
    public void updateViewOnMovieRecordFailed(TuSdkRecorderVideoCamera.RecordError error, boolean isRecording) {
        if (error == TuSdkRecorderVideoCamera.RecordError.MoreMaxDuration) // 超过最大时间 （超过最大时间是再次调用startRecording时会调用）
        {
            String msg = getStringFromResource("max_recordTime") + Constants.MAX_RECORDING_TIME + "s";
            TuSdk.messageHub().showToast(mContext, msg);

        } else if (error == TuSdkRecorderVideoCamera.RecordError.SaveFailed) // 视频保存失败
        {
            String msg = getStringFromResource("new_movie_error_saving");
            TuSdk.messageHub().showError(mContext, msg);
        } else if (error == TuSdkRecorderVideoCamera.RecordError.InvalidRecordingTime) {
            TuSdk.messageHub().showError(mContext, R.string.lsq_record_time_invalid);
        }
        setViewHideOrVisible(true);
    }

    /**
     * 录制完成时更新视图显示
     *
     * @param isRecording
     */
    public void updateViewOnMovieRecordComplete(boolean isRecording) {
        TuSdk.messageHub().dismissRightNow();
        String msg = getStringFromResource("new_movie_saved");
        TuSdk.messageHub().showSuccess(mContext, msg);

        // 录制完进度清零(正常录制模式)
        mRecordProgress.clearProgressList();
        setViewHideOrVisible(true);
    }

    /**
     * 获取字符串资源
     *
     * @param fieldName
     * @return
     */
    protected String getStringFromResource(String fieldName) {
        int stringID = this.getResources().getIdentifier(fieldName, "string",
                this.mContext.getPackageName());

        return getResources().getString(stringID);
    }


    public void onResume() {

    }
}
