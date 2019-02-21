package com.upyun.shortvideo.editor.component;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.PointF;
import android.graphics.RectF;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import org.lasque.tusdk.core.TuSdk;
import org.lasque.tusdk.core.TuSdkContext;
import org.lasque.tusdk.core.seles.sources.TuSdkEditorEffector;
import org.lasque.tusdk.core.seles.sources.TuSdkEditorPlayer;
import org.lasque.tusdk.core.seles.sources.TuSdkMovieEditor;
import org.lasque.tusdk.core.struct.TuSdkSize;
import org.lasque.tusdk.core.utils.TLog;
import org.lasque.tusdk.core.utils.ThreadHelper;
import org.lasque.tusdk.core.view.TuSdkViewHelper;
import org.lasque.tusdk.core.view.recyclerview.TuSdkLinearLayoutManager;
import org.lasque.tusdk.video.editor.TuSdkMediaEffectData;
import org.lasque.tusdk.video.editor.TuSdkMediaParticleEffectData;
import org.lasque.tusdk.video.editor.TuSdkMediaRepeatTimeEffect;
import org.lasque.tusdk.video.editor.TuSdkMediaReversalTimeEffect;
import org.lasque.tusdk.video.editor.TuSdkMediaSceneEffectData;
import org.lasque.tusdk.video.editor.TuSdkMediaSlowTimeEffect;
import org.lasque.tusdk.video.editor.TuSdkMediaTimeEffect;
import org.lasque.tusdk.video.editor.TuSdkTimeRange;

import com.upyun.shortvideo.editor.MovieEditorController;
import com.upyun.shortvideo.views.ConfigViewSeekBar;
import com.upyun.shortvideo.views.editor.playview.TuSdkMovieScrollView;
import com.upyun.shortvideo.views.editor.playview.TuSdkRangeSelectionBar;
import com.upyun.shortvideo.R;
import com.upyun.shortvideo.utils.Constants;
import com.upyun.shortvideo.views.ConfigViewParams;
import com.upyun.shortvideo.views.EffectComponetAdapter;
import com.upyun.shortvideo.views.MagicRecyclerAdapter;
import com.upyun.shortvideo.views.SceneRecyclerAdapter;
import com.upyun.shortvideo.views.TabPagerIndicator;
import com.upyun.shortvideo.views.TimeRecyclerAdapter;
import com.upyun.shortvideo.views.editor.TuSdkMovieScrollPlayLineView;
import com.upyun.shortvideo.views.editor.color.ColorView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * droid-sdk-video
 *
 * @author MirsFang
 * @Date 2018/9/25 15:54
 * @Copright (c) 2018 tusdk.com. All rights reserved.
 * <p>
 * 特效组件
 */
public class EditorEffectComponent extends EditorComponent {
    /** 判断为按压的最短触摸时间 **/
    private static final int MIN_PRESS_DURATION_MILLIS = 150;
    /** 当前组件的底部控件 **/
    private View mBottomView;

    /** 三种特效的 ViewPager **/
    private ViewPager mViewPager;
    /** 指示器 **/
    private TabPagerIndicator mIndicator;
    /** 特效 ViewPager 适配器 **/
    private EffectComponetAdapter mAdapter;
    /** 关闭按钮 **/
    private ImageButton mBackBtn;
    /** 应用按钮 **/
    private ImageButton mNextBtn;
    /** 粒子设置控件 (大小  颜色) **/
    private ParticleConfigView mMagicConfig;

    /** 场景特效 Fragment **/
    private SceneEffectFragment mScreenFragment;
    /** 时间特效 Fragment **/
    private TimeEffectFragment mTimeFragment;
    /** 魔法特效 Fragment **/
    private ParticleEffectFragment mMagicFragment;

    /** 特效 Frgament 列表 **/
    private List<EffectFragment> mFragmentList;
    /** 视频封面图片列表 **/
    private List<Bitmap> mBitmapList = new ArrayList<>();

    /** 默认特效持续时间 **/
    private static long mEffectDurationUs = 2 * 1000000;


    /**
     * 显示区域改变回调
     *
     * @since V3.0.0
     */
    private TuSdkEditorPlayer.TuSdkPreviewSizeChangeListener mOnDisplayChangeListener = new TuSdkEditorPlayer.TuSdkPreviewSizeChangeListener() {
        @Override
        public void onPreviewSizeChanged(final TuSdkSize previewSize) {
            if (getEditorController().getActivity().getMagicContent() == null) return;
            ThreadHelper.post(new Runnable() {
                @Override
                public void run() {
                    RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) getEditorController().getActivity().getMagicContent().getLayoutParams();
                    layoutParams.width = previewSize.width;
                    layoutParams.height = previewSize.height;
                    layoutParams.leftMargin = (getEditorController().getVideoContentView().getWidth() - layoutParams.width) / 2;
                    layoutParams.topMargin = (getEditorController().getVideoContentView().getHeight() - layoutParams.height) / 2;
                    getEditorController().getActivity().getMagicContent().setLayoutParams(layoutParams);
                }
            });

        }
    };

    /** 播放进度回调 **/
    private TuSdkEditorPlayer.TuSdkProgressListener mProgressLisntener = new TuSdkEditorPlayer.TuSdkProgressListener() {
        @Override
        public void onStateChanged(int state) {
            if (mScreenFragment != null) mScreenFragment.setPlayState(state);
            if (mScreenFragment != null) mScreenFragment.onPlayerStateChanged(state);
            if (mTimeFragment != null) mTimeFragment.setPlayState(state);
            if (mMagicFragment != null) mMagicFragment.setPlayState(state);
            if (mMagicFragment != null) mMagicFragment.onPlayerStateChanged(state);
        }

        @Override
        public void onProgress(long playbackTimeUs, long totalTimeUs, float percentage) {
            if(isAnimationStaring)return;
            if (mScreenFragment != null) mScreenFragment.moveToPercent(percentage, playbackTimeUs);
            if (mMagicFragment != null) mMagicFragment.moveToPercent(percentage, playbackTimeUs);
            if (mTimeFragment != null) mTimeFragment.moveToPercent(percentage);
        }
    };


    /**
     * 创建当前组件
     *
     * @param editorController
     */
    public EditorEffectComponent(MovieEditorController editorController) {
        super(editorController);
        mComponentType = EditorComponentType.Effect;
        getEditorPlayer().addPreviewSizeChangeListener(mOnDisplayChangeListener);
    }

    @Override
    public void attach() {
        getEditorController().getBottomView().addView(getBottomView());
        getEditorPlayer().pausePreview();
        getEditorPlayer().seekOutputTimeUs(0);

        if (mScreenFragment != null) mScreenFragment.attach();
        if (mTimeFragment != null) mTimeFragment.attach();
        if (mMagicFragment != null) mMagicFragment.attach();
        if (mViewPager != null) mViewPager.setCurrentItem(0);

        getEditorController().getVideoContentView().setClickable(false);
        getEditorController().getPlayBtn().setVisibility(View.GONE);
    }

    @Override
    public void onAnimationStart() {
        super.onAnimationStart();

        if (mScreenFragment != null) mScreenFragment.onAnimationStart();
        if (mTimeFragment != null) mTimeFragment.onAnimationStart();
        if (mMagicFragment != null) mMagicFragment.onAnimationStart();

    }

    @Override
    public void onAnimationEnd() {
        super.onAnimationEnd();
        if (mScreenFragment != null) mScreenFragment.onAnimationEnd();
        if (mTimeFragment != null) mTimeFragment.onAnimationEnd();
        if (mMagicFragment != null) mMagicFragment.onAnimationEnd();
    }

    @Override
    public void detach() {
        if (mScreenFragment != null) mScreenFragment.detach();
        if (mTimeFragment != null) mTimeFragment.detach();
        if (mMagicFragment != null) mMagicFragment.detach();

        getEditorPlayer().pausePreview();
        getEditorPlayer().seekOutputTimeUs(0);
        getEditorController().getVideoContentView().setClickable(true);
        getEditorController().getPlayBtn().setVisibility(View.VISIBLE);

    }

    @Override
    public View getHeaderView() {
        return null;
    }

    @Override
    public View getBottomView() {
        if (mBottomView == null) {
            initBottomView();
        }
        return mBottomView;
    }

    @Override
    public void addCoverBitmap(Bitmap bitmap) {
        getBottomView();
        mBitmapList.add(bitmap);
        if (mScreenFragment != null) mScreenFragment.addCoverBitmap(bitmap);
        if (mTimeFragment != null) mTimeFragment.addCoverBitmap(bitmap);
        if (mMagicFragment != null) mMagicFragment.addCoverBitmap(bitmap);
    }

    /** 初始化BottomView **/
    private void initBottomView() {
        mBottomView = LayoutInflater.from(getEditorController().getActivity()).inflate(R.layout.lsq_editor_component_effect_bottom, null);
        mViewPager = findViewById(R.id.lsq_editor_effect_content);
        mBackBtn = findViewById(R.id.lsq_effect_close);
        mNextBtn = findViewById(R.id.lsq_effect_sure);
        mIndicator = findViewById(R.id.lsq_effect_indicator);

        mBackBtn.setOnClickListener(mOnClickListener);
        mNextBtn.setOnClickListener(mOnClickListener);

        mMagicConfig = new ParticleConfigView();

        mFragmentList = new ArrayList<>();
        mScreenFragment = new SceneEffectFragment(getEditorController().getMovieEditor(), mBitmapList);
        mTimeFragment = new TimeEffectFragment(getEditorController().getMovieEditor(), mBitmapList);
        mMagicFragment = new ParticleEffectFragment(getEditorController().getMovieEditor(), getEditorController().getActivity().getMagicContent(), mMagicConfig, mBitmapList);

        mFragmentList.add(mScreenFragment);
        mFragmentList.add(mTimeFragment);
        mFragmentList.add(mMagicFragment);


        mAdapter = new EffectComponetAdapter(getEditorController().getActivity().getSupportFragmentManager(), mFragmentList);
        mViewPager.setOffscreenPageLimit(3);
        mViewPager.setAdapter(mAdapter);
        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int i) {
                if (mFragmentList.get(i) != mMagicFragment) {
                    mMagicFragment.mParticleConfig.setVisible(false);
                    mMagicFragment.clearSelect();
                }

                if(mFragmentList.get(i) != mTimeFragment){
                    mTimeFragment.updataApplayTimeEffect();
                }

                mFragmentList.get(i).onSelected();
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });

        mIndicator.setViewPager(mViewPager, 0);
        mIndicator.setTabItems(Arrays.asList(
                getEditorController().getActivity().getResources().getString(R.string.lsq_screen),
                getEditorController().getActivity().getResources().getString(R.string.lsq_time),
                getEditorController().getActivity().getResources().getString(R.string.lsq_magic)
        ));
        mViewPager.setCurrentItem(0);

        getEditorController().getMovieEditor().getEditorPlayer().addProgressListener(mProgressLisntener);
    }

    /** 当期组件底部点击事件 **/
    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.lsq_effect_close:
                    mScreenFragment.back();
                    mTimeFragment.back();
                    mMagicFragment.back();
                    getEditorController().onBackEvent();
                    break;
                case R.id.lsq_effect_sure:
                    mScreenFragment.next();
                    mTimeFragment.next();
                    mMagicFragment.next();
                    getEditorController().onBackEvent();
                    break;
            }
        }
    };


    private <T extends View> T findViewById(@IdRes int id) {
        return mBottomView.findViewById(id);
    }


    /** 场景特效Fragment */
    @SuppressLint("ValidFragment")
    public static class SceneEffectFragment extends EffectFragment {
        /** 视频编辑器 **/
        private TuSdkMovieEditor mMovieEditor;
        /** 当前正在应用的场景特效数据 **/
        private TuSdkMediaSceneEffectData mediaSceneEffectData;
        /** 当前应用的场景数据列表 **/
        private LinkedList<TuSdkMediaSceneEffectData> mDataList;
        /** 当前应用的场景数据备忘列表 **/
        private LinkedList<TuSdkMediaSceneEffectData> mMementoList;
        private Handler mHandler = new Handler();
        /** 场景特效Framgent的视图 **/
        private View mSceneView;
        /** 当前场景特效组件的播放按钮 **/
        private ImageView mPlayBtn;
        /** 场景特效列表 **/
        private RecyclerView mSceneRecycle;
        /** 场景特效列表适配器 **/
        private SceneRecyclerAdapter mSceneAdapter;
        /** 播放控件 **/
        private TuSdkMovieScrollPlayLineView mLineView;
        /** 视频封面Bitmap列表 **/
        private List<Bitmap> mBitmapList;
        /** 当前正在应用的场景特效Code **/
        public volatile String mSceneCode;
        /** 当前应用场景特效的开始时间 **/
        public long mStartTimeUs;
        /** 当前是否允许绘制特效色块 **/
        public boolean mDrawColorState = false;
        /** 当前组件是否被选择 **/
        boolean isOnSelect = false;
        private boolean isContinue = true;

        private float prePercent = 0;


        private SceneRecyclerAdapter.OnItemTouchListener mOnItemTouchListener = new SceneRecyclerAdapter.OnItemTouchListener() {
            /** 当前触摸的持续时间 **/
            long duration = 0;
            boolean isTouching = false;
            @Override
            public void onItemTouch(MotionEvent event, final int position, final SceneRecyclerAdapter.SceneViewHolder sceneViewHolder) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        //按下场景特效Item
                        if(isTouching )return;
                        isTouching = true;
                        mHandler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                onPressSceneEffect(sceneViewHolder, position);
                            }
                        },MIN_PRESS_DURATION_MILLIS);
                        break;
                    case MotionEvent.ACTION_MOVE:
                        break;
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL:
                        //手抬起来之后
                        isTouching = false;
                        mHandler.removeMessages(0);
                        onReleaseSceneEffect(sceneViewHolder, position);
                        break;
                }
            }


            private void onPressSceneEffect(SceneRecyclerAdapter.SceneViewHolder sceneViewHolder, int position) {
                if(position == 0) return;

                /** 开始播放视频并预览已设置的特效 */
                if (getEditorPlayer().getCurrentOutputTimeUs() >= getEditorPlayer().getOutputTotalTimeUS()) {
                    sceneViewHolder.mSelectLayout.setImageResource(TuSdkContext.getColorResId("lsq_color_transparent"));
                    return;
                }

                /** 倒序情况下特效添加到头则返回 **/
                if(getEditorPlayer().isReversing() && (0 == currentPercent) ){
                    sceneViewHolder.mSelectLayout.setImageResource(TuSdkContext.getColorResId("lsq_color_transparent"));
                    return;
                }

                if(mLineView.getCurrentPercent() == 1 && !getEditorPlayer().isReversing()){
                    sceneViewHolder.mSelectLayout.setImageResource(TuSdkContext.getColorResId("lsq_color_transparent"));
                    return;
                }

                if(getEditorPlayer().getCurrentTimeUs() >= getEditorPlayer().getTotalTimeUs() && !getEditorPlayer().isReversing()){
                    sceneViewHolder.mSelectLayout.setImageResource(TuSdkContext.getColorResId("lsq_color_transparent"));
                    return;
                }

                if (!mMovieEditor.getEditorPlayer().isPause()) {
                    mMovieEditor.getEditorPlayer().pausePreview();
                    return;
                }

                mSceneCode = mSceneAdapter.getSceneCode(position);


                long totalUs = getEditorPlayer().getTotalTimeUs();
                float percent = mLineView.getCurrentPercent();
                mStartTimeUs = (long) (totalUs*percent);
                mMovieEditor.getEditorPlayer().startPreview();
                mediaSceneEffectData = new TuSdkMediaSceneEffectData(mSceneCode);
                //设置ViewModel
                if (mMovieEditor.getEditorPlayer().isReversing()) {
                    mediaSceneEffectData.setAtTimeRange(TuSdkTimeRange.makeTimeUsRange(0, mStartTimeUs));
                } else {
                    mediaSceneEffectData.setAtTimeRange(TuSdkTimeRange.makeTimeUsRange(mStartTimeUs, Long.MAX_VALUE));
                }
                // 预览场景特效
                mMovieEditor.getEditorEffector().addMediaEffectData(mediaSceneEffectData);
                mLineView.endAddColorRect();
                mLineView.addColorRect(TuSdkContext.getColor("lsq_scence_effect_color_" + mSceneCode));
                mDataList.add(mediaSceneEffectData);
                isOnSelect = false;
                sceneViewHolder.mSelectLayout.setImageResource(TuSdkContext.getColorResId("lsq_scence_effect_color_" + mSceneCode));
                if (!mSceneAdapter.isCanDeleted()) {
                    mSceneAdapter.setCanDeleted(true);
                    mSceneAdapter.notifyItemChanged(0);
                }
            }

            private void onReleaseSceneEffect(SceneRecyclerAdapter.SceneViewHolder sceneViewHolder, int position) {
                mMovieEditor.getEditorPlayer().pausePreview();
                sceneViewHolder.mSelectLayout.setImageResource(TuSdkContext.getColorResId("lsq_color_transparent"));
            }

        };


        public SceneEffectFragment(TuSdkMovieEditor movieEditor, final List<Bitmap> bitmaps) {
            super(movieEditor);
            this.mMovieEditor = movieEditor;
            this.mBitmapList = bitmaps;
            mDataList = new LinkedList<>();
            mMementoList = new LinkedList<>();
            mSceneAdapter = new SceneRecyclerAdapter();
            mSceneAdapter.setOnItemTouchListener(mOnItemTouchListener);
            mSceneAdapter.setItemCilckListener(new SceneRecyclerAdapter.ItemClickListener() {
                @Override
                public void onItemClick(int position) {
                    if (position != 0 || mDataList.size() == 0 || mMovieEditor == null || !mSceneAdapter.isCanDeleted())
                        return;
                    mLineView.endAddColorRect();
                    mLineView.deletedColorRect();
                    mMovieEditor.getEditorPlayer().pausePreview();
                    TuSdkMediaEffectData mediaEffectData = mDataList.removeLast();
                    mMovieEditor.getEditorEffector().removeMediaEffectData(mediaEffectData);
                     if (mMovieEditor.getEditorPlayer().isReversing()) {
                         mMovieEditor.getEditorPlayer().seekInputTimeUs(mediaEffectData.getAtTimeRange().getEndTimeUS());
                        mLineView.seekTo(mediaEffectData.getAtTimeRange().getEndTimeUS()/(float)getEditorPlayer().getTotalTimeUs());
                    } else {
                         mMovieEditor.getEditorPlayer().seekInputTimeUs(mediaEffectData.getAtTimeRange().getStartTimeUS());
                         mLineView.seekTo(mediaEffectData.getAtTimeRange().getStartTimeUS()/(float)getEditorPlayer().getTotalTimeUs());
                    }

                    boolean isCanDeleted = mDataList.size() > 0;
                    mSceneAdapter.setCanDeleted(isCanDeleted);
                    mSceneAdapter.notifyItemChanged(0);
                    setPlayState(1);
                }
            });
        }

        @Nullable
        @Override
        public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            mSceneView = inflater.inflate(R.layout.lsq_editor_component_effect_bottom_scene, null);
            mSceneRecycle = mSceneView.findViewById(R.id.lsq_editor_effect_scene_list);
            mSceneRecycle.setLayoutManager(new TuSdkLinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
            mSceneRecycle.setAdapter(mSceneAdapter);
            mSceneAdapter.setSceneList(Arrays.asList(Constants.SCENE_EFFECT_CODES));

            mLineView = mSceneView.findViewById(R.id.lsq_editor_scene_play_range);
            mLineView.setType(0);
            mLineView.setOnProgressChangedListener(mOnScrollingPlayListener);
            mLineView.setOnBackListener(new TuSdkMovieScrollView.OnColorGotoBackListener() {
                @Override
                public void onGotoBack(float percent) {
                    prePercent = percent;
                }
            });
            if (mBitmapList != null) {
                for (Bitmap bp : mBitmapList)
                    mLineView.addBitmap(bp);
            }

            mPlayBtn = mSceneView.findViewById(R.id.lsq_editor_scene_play);
            mPlayBtn.setOnClickListener(mOnClickListener);

            return mSceneView;
        }

        public void addCoverBitmap(Bitmap bitmap) {
            if (mLineView != null)
                mLineView.addBitmap(bitmap);
        }

        private View.OnClickListener mOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.lsq_editor_scene_play:
                        isOnSelect = false;
                        if (mMovieEditor.getEditorPlayer().isPause()) {
                            mMovieEditor.getEditorPlayer().startPreview();
                            isContinue = true;
                        } else
                            mMovieEditor.getEditorPlayer().pausePreview();
                        break;
                }
            }
        };

        /**
         * 设置播放状态
         *
         * @param state 0 播放  1 暂停
         * @since V3.0.0
         */
        public void setPlayState(int state) {
            if (mPlayBtn == null) return;
            mPlayBtn.setImageDrawable(TuSdkContext.getDrawable(state == 0 ? R.drawable.edit_ic_pause : R.drawable.edit_ic_play));
        }

        private TuSdkMovieScrollView.OnProgressChangedListener mOnScrollingPlayListener = new TuSdkMovieScrollView.OnProgressChangedListener() {
            @Override
            public void onProgressChanged(float progress, boolean isTouching) {
                currentPercent  = progress;
                if(!isTouching)return;
                if(isTouching)getEditorPlayer().pausePreview();
                long current = (long) (progress * getEditorPlayer().getTotalTimeUs());

                if (getEditorPlayer().isPause()) {
                    long seekUs;
                    if (getEditorPlayer().isReversing()) {
                        seekUs = (long) ((1 - progress) * getEditorPlayer().getInputTotalTimeUs());
                        getEditorPlayer().seekOutputTimeUs(seekUs);
                    } else {
                        seekUs = (long) (getEditorPlayer().getInputTotalTimeUs() * progress);
                        getEditorPlayer().seekInputTimeUs(seekUs);

                    }
                }

            }

    @Override
    public void onCancelSeek() {
        //纠偏
        float progress_1 = getEditorPlayer().getCurrentTimeUs() / (float)getEditorPlayer().getTotalTimeUs();
        mLineView.seekTo(progress_1);
    }
};

        @Override
        public void attach() {
            super.attach();
            //恢复之前的情况
            if (mMementoList.size() == 0) {
                mSceneAdapter.setCanDeleted(false);
                mSceneAdapter.notifyItemChanged(0);
            }else {
                mSceneAdapter.setCanDeleted(true);
                mSceneAdapter.notifyItemChanged(0);
            }

            for (TuSdkMediaSceneEffectData item : mMementoList) {
                recoveryEffect(item);
            }
            mMovieEditor.getEditorPlayer().seekOutputTimeUs(0);
            if (mLineView != null)
                mLineView.seekTo(mMovieEditor.getEditorPlayer().isReversing() ? 1 : 0);
            if (mLineView != null)
                mLineView.setTimeEffectType(mMovieEditor.getEditorPlayer().isReversing()? 1 : 0);
            setPlayState(1);
        }


        @Override
        public void onSelected() {
            super.onSelected();
            isOnSelect = true;
            boolean isReverse = mMovieEditor.getEditorPlayer().isReversing();
            mMovieEditor.getEditorPlayer().pausePreview();
            mMovieEditor.getEditorPlayer().seekOutputTimeUs(0);
            mLineView.seekTo(isReverse ? 1f : 0f);
            if (mLineView != null)
                mLineView.setTimeEffectType(mMovieEditor.getEditorPlayer().isReversing()?1:0);
            mPlayBtn.setImageDrawable(TuSdkContext.getDrawable(R.drawable.edit_ic_play));

        }

        /** 恢复之前应用的特效 **/
        private void recoveryEffect(TuSdkMediaSceneEffectData mediaEffectData) {
            String sceneCode = mediaEffectData.getEffectCode();
            float startPercent = mediaEffectData.getAtTimeRange().getStartTimeUS()/(float)getEditorPlayer().getTotalTimeUs();
            float endPercent = mediaEffectData.getAtTimeRange().getEndTimeUS()/(float)getEditorPlayer().getTotalTimeUs();
            mLineView.recoverColorRect(TuSdkContext.getColor("lsq_scence_effect_color_" + sceneCode),startPercent,endPercent);
            mDataList.add(mediaEffectData);
        }

        @Override
        public void detach() {
            super.detach();
        }

        @Override
        public void back() {
            super.back();
            for (TuSdkMediaEffectData item : mDataList) {
                mMovieEditor.getEditorEffector().removeMediaEffectData(item);
                mLineView.deletedColorRect();
            }

            for (TuSdkMediaEffectData item : mMementoList) {
                mMovieEditor.getEditorEffector().addMediaEffectData(item);
            }

            mDataList.clear();
        }

        @Override
        public void next() {
            super.next();
            mMementoList.clear();
            mMementoList.addAll(mDataList);
            mDataList.clear();
            mLineView.clearAllColorRect();
        }


        /** 同步播放器的播放状态 **/
        public void onPlayerStateChanged(int state) {

            if (mediaSceneEffectData != null) {
                //暂停
                if (state == 1) {
                    if (mediaSceneEffectData != null) {
                        TuSdkTimeRange timeRange = mediaSceneEffectData.getAtTimeRange();
                        if (mMovieEditor.getEditorPlayer().isReversing()) {
                            timeRange.setStartTimeUs((long) (mLineView.getCurrentPercent() * getEditorPlayer().getTotalTimeUs()));
                        }
                        else {
                            timeRange.setEndTimeUs(getEditorPlayer().getCurrentTimeUs());
                            mLineView.seekTo(mMovieEditor.getEditorPlayer().getCurrentInputTimeUs()/(float)getEditorPlayer().getTotalTimeUs());
                            if(timeRange.getEndTimeUS() < prePercent * getEditorPlayer().getTotalTimeUs()){
                                timeRange.setEndTimeUs((long) (prePercent * getEditorPlayer().getTotalTimeUs()));
                            }
                        }
                        mediaSceneEffectData.setAtTimeRange(timeRange);
                    }
                    mDrawColorState = false;
                    mLineView.endAddColorRect();
                    mediaSceneEffectData = null;

                } else {
                    mDrawColorState = true;
                    isContinue = false;
                }
            }


        }

        /** 更新进度 **/
        private boolean isPreState = true;
        private float currentPercent;
        /** 移动进度到播放视图 **/
        public void moveToPercent(float percentage, long playbackTimeUs) {
            currentPercent = percentage;
            if (mLineView != null) {
                if (mDrawColorState && mediaSceneEffectData != null) {
                    // mStartTimeUs, playbackTimeUs

                }
                if (isOnSelect) return;
                if (mDrawColorState) {
                    isPreState = mDrawColorState;
                    mLineView.seekTo(percentage);
                } else {
                    if (isPreState) {
                        isPreState = false;
                        return;
                    }
                    if (isContinue && !mMovieEditor.getEditorPlayer().isPause()) {
                        mLineView.seekTo(percentage);
                    }
                }
            }
        }
    }

    /** 时间特效Fragment */
    @SuppressLint("ValidFragment")
    public static class TimeEffectFragment extends EffectFragment {
        /** 视频编辑器 **/
        private TuSdkMovieEditor mMovieEditor;
        /** 正在使用的时间特效 **/
        private TuSdkMediaTimeEffect mCurrentEffectData;
        /** 备忘时间特效 **/
        private TuSdkMediaTimeEffect mMementoEffectData;

        /** 当前时间特效的视图 **/
        private View mTimeView;
        /** 时间特效列表 **/
        private RecyclerView mTimeRecycle;
        /** 时间特效列表适配器 **/
        private TimeRecyclerAdapter mTimeAdapter;
        /** 播放进度控件 **/
        private TuSdkMovieScrollPlayLineView mLineView;
        /** 封面图片列表 **/
        private List<Bitmap> mBitmapList;
        /** 播放按钮 **/
        private ImageView mPlayBtn;
        /** 当前正在使用的时间特效下标 **/
        private int mCurrentIndex;
        /** 当前备忘上一个时间特效的下标 **/
        private int mMementoIndex;
        /** 当前视图的输出总时长 **/
        private long mVideoOutputTotalTimeUs;
        /** 当前时间特效是否正在改变(包括时间范围) **/
        private boolean isTimeChanged;

        /** 反复和慢动作最长持续时间 **/
        private long mMaxEffectTimeUs = 3 * 1000000;
        /** 反复和慢动作最短持续时间 **/
        private long mMinEffectTimeUs = 1 * 1000000;
        boolean isOnSelect = false;

        /** 是否在快速切换中 **/
        boolean isFastSwitch = false;

        @SuppressLint("ValidFragment")
        public TimeEffectFragment(TuSdkMovieEditor movieEditor, List<Bitmap> bitmapList) {
            super(movieEditor);
            this.mMovieEditor = movieEditor;
            this.mBitmapList = bitmapList;
        }

        @Nullable
        @Override
        public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            mTimeView = inflater.inflate(R.layout.lsq_editor_component_effect_bottom_time, null);
            mTimeRecycle = mTimeView.findViewById(R.id.lsq_editor_effect_time_list);
            mTimeRecycle.setLayoutManager(new TuSdkLinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
            mTimeAdapter = new TimeRecyclerAdapter();
            mTimeAdapter.setTimeList(Arrays.asList(Constants.TIME_EFFECT_CODES));
            mTimeRecycle.setAdapter(mTimeAdapter);
            mVideoOutputTotalTimeUs = mMovieEditor.getEditorPlayer().getOutputTotalTimeUS();
            mTimeAdapter.setItemCilckListener(new TimeRecyclerAdapter.ItemClickListener() {
                @Override
                public void onItemClick(int position) {
                    if (TuSdkViewHelper.isFastDoubleClick()) return;

                    if(isFastSwitch) return;
                    isFastSwitch = true;
                    setTimeEffect(position);
                    ThreadHelper.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            isFastSwitch = false;
                        }
                    },500);
                }

                private void setTimeEffect(int position) {
                    mTimeAdapter.setCurrentPosition(position);
                    mCurrentIndex = position;
                    isTimeChanged = true;

                    if(!getEditorPlayer().isPause()) getEditorPlayer().pausePreview();
                    if (position == 0) {
                        mLineView.setTimeEffectType(1);
                        mLineView.setShowSelectBar(false);
                        mMovieEditor.getEditorPlayer().clearTimeEffect();
                        mCurrentEffectData = null;
                    } else if (position == 1) {
                        //反复
                        mLineView.setTimeEffectType(1);
                        mLineView.setShowSelectBar(true);
                        TuSdkMediaRepeatTimeEffect repeatTimeEffect = new TuSdkMediaRepeatTimeEffect();
                        TuSdkTimeRange applyTime = getApplyTimeRang();
                        repeatTimeEffect.setTimeRange(applyTime.getStartTimeUS(), applyTime.getEndTimeUS());
                        repeatTimeEffect.setRepeatCount(2);
                        getEditorPlayer().setTimeEffect(repeatTimeEffect);
                        mCurrentEffectData = repeatTimeEffect;
                        getEditorPlayer().seekOutputTimeUs(0);
                    } else if (position == 2) {
                        //慢动作
                        mLineView.setTimeEffectType(1);
                        mLineView.setShowSelectBar(true);
                        TuSdkMediaSlowTimeEffect slowTimeEffect = new TuSdkMediaSlowTimeEffect();
                        TuSdkTimeRange applyTime = getApplyTimeRang();
                        slowTimeEffect.setTimeRange(applyTime.getStartTimeUS(), applyTime.getEndTimeUS());
                        slowTimeEffect.setSpeed(0.5f);
                        getEditorPlayer().setTimeEffect(slowTimeEffect);
                        mCurrentEffectData = slowTimeEffect;
                        getEditorPlayer().seekOutputTimeUs(0);
                    } else if (position == 3) {
                        //时光倒流
                        mLineView.setTimeEffectType(1);
                        mLineView.seekTo(1f);
                        getEditorPlayer().seekOutputTimeUs(getEditorPlayer().getOutputTotalTimeUS());
                        TuSdkMediaReversalTimeEffect reversalTimeEffect = new TuSdkMediaReversalTimeEffect();
                        getEditorPlayer().setTimeEffect(reversalTimeEffect);
                        mLineView.setShowSelectBar(false);
                        mCurrentEffectData = reversalTimeEffect;
                    }

                    setPlayState(1);
                }
            });

            mLineView = mTimeView.findViewById(R.id.lsq_editor_time_play_range);
            mLineView.setType(1);
            mLineView.setShowSelectBar(false);
            mLineView.setOnProgressChangedListener(mOnScrollingPlayListener);
            mLineView.setMaxWidth(mMaxEffectTimeUs/(float)getEditorPlayer().getTotalTimeUs());
            mLineView.setMinWidth(mMinEffectTimeUs/(float)getEditorPlayer().getTotalTimeUs());
            if (mBitmapList != null) {
                for (Bitmap bp : mBitmapList)
                    mLineView.addBitmap(bp);
            }
            mLineView.setSelectRangeChangedListener(new TuSdkRangeSelectionBar.OnSelectRangeChangedListener() {
                @Override
                public void onSelectRangeChanged(float leftPercent, float rightPerchent, int type) {
                    if (!getEditorPlayer().isPause()) {
                        getEditorPlayer().pausePreview();
                        getEditorPlayer().seekOutputTimeUs(0);
                    }

                    if (mCurrentEffectData == null) return;
                    isTimeChanged = true;
                    if(type == 0){
                        mCurrentEffectData.getTimeRange().setStartTimeUs((long) (getEditorPlayer().getInputTotalTimeUs() * leftPercent));
                    }else {
                        mCurrentEffectData.getTimeRange().setEndTimeUs((long) (getEditorPlayer().getInputTotalTimeUs() * rightPerchent));
                    }

                }
            });
            mLineView.setExceedCriticalValueListener(new TuSdkRangeSelectionBar.OnExceedCriticalValueListener() {
                @Override
                public void onMaxValueExceed() {
                   ThreadHelper.postDelayed(new Runnable() {
                       @Override
                       public void run() {
                           Integer maxTime = (int) (mMaxEffectTimeUs / 1000000);
                           String tips = String.format(getString(R.string.lsq_max_time_effect_tips), maxTime);
                           TuSdk.messageHub().showToast(getContext(), tips);
                       }
                   },100);
                }

                @Override
                public void onMinValueExceed() {
                    ThreadHelper.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Integer minTime = (int) (mMinEffectTimeUs / 1000000);
                            String tips = String.format(getString(R.string.lsq_min_time_effect_tips), minTime);
                            TuSdk.messageHub().showToast(getContext(), tips);
                        }
                    },100);
                }
            });

            mPlayBtn = mTimeView.findViewById(R.id.lsq_editor_time_play);
            mPlayBtn.setOnClickListener(mOnClickListener);

            return mTimeView;
        }

        public void addCoverBitmap(Bitmap bitmap) {
            if (mLineView == null) return;
            mLineView.addBitmap(bitmap);
        }

        /** 滚动时 播放位置的回调 **/
        private TuSdkMovieScrollView.OnProgressChangedListener mOnScrollingPlayListener = new TuSdkMovieScrollView.OnProgressChangedListener() {
            @Override
            public void onProgressChanged(float progress, boolean isTouching) {
                if(!isTouching)return;
                if(isTouching) getEditorPlayer().pausePreview();
                if (getEditorPlayer().isPause()) {
                    long seekUs;
                    if (getEditorPlayer().isReversing()) {
                        seekUs = (long) ((1 - progress) * getEditorPlayer().getInputTotalTimeUs());
                        getEditorPlayer().seekOutputTimeUs(seekUs);
                    } else {
                        seekUs = (long) (getEditorPlayer().getInputTotalTimeUs() * progress);
                        getEditorPlayer().seekInputTimeUs(seekUs);
                    }

                }
            }

            @Override
            public void onCancelSeek() {

            }
        };

        private View.OnClickListener mOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.lsq_editor_time_play:
                        //应用时间特效并且备忘
                        isOnSelect = false;
                        if (mMovieEditor.getEditorPlayer().isPause()) {
                            if (isTimeChanged) {
                                if (mCurrentEffectData != null)
                                    mMovieEditor.getEditorPlayer().setTimeEffect(mCurrentEffectData);
                                isTimeChanged = false;
                            }
                            mMovieEditor.getEditorPlayer().startPreview();
                        } else {
                            mMovieEditor.getEditorPlayer().pausePreview();
                        }
                        break;
                }
            }
        };

        /**
         * 获取应用特效的时间区间
         * @return 时间特效应用的时间区间
         */
        public TuSdkTimeRange getApplyTimeRang() {
            TuSdkTimeRange timeRange = new TuSdkTimeRange();
//            long starTimeUs = mMovieEditor.getEditorPlayer().getCurrentInputTimeUs();
            long starTimeUs = (long) (mLineView.getCurrentPercent() * getEditorPlayer().getInputTotalTimeUs());
            if (starTimeUs > (mMovieEditor.getEditorPlayer().getInputTotalTimeUs() - mEffectDurationUs)) {
                starTimeUs = mMovieEditor.getEditorPlayer().getInputTotalTimeUs() - mEffectDurationUs;
            }
            getEditorPlayer().seekInputTimeUs(0);
//            mLineView.seekTo(starTimeUs/(float)getEditorPlayer().getTotalTimeUs());
//            mLineView.seekTo(0f);
            timeRange.setStartTimeUs(starTimeUs);
            long endTimeUs = timeRange.getStartTimeUS() + mEffectDurationUs;
            if (endTimeUs > mVideoOutputTotalTimeUs) {
                endTimeUs = mVideoOutputTotalTimeUs;
            }
            timeRange.setEndTimeUs(endTimeUs);
            mLineView.setLeftBarPosition(timeRange.getStartTimeUS()/(float)getEditorPlayer().getTotalTimeUs());
            mLineView.setRightBarPosition(timeRange.getEndTimeUS()/(float)getEditorPlayer().getTotalTimeUs());
            return timeRange;
        }

        /**
         * 设置播放状态
         *
         * @param state 0 播放  1 暂停
         * @since V3.0.0
         */
        public void setPlayState(int state) {
            if (mPlayBtn == null) return;
            mPlayBtn.setImageDrawable(TuSdkContext.getDrawable(state == 0 ? R.drawable.edit_ic_pause : R.drawable.edit_ic_play));
        }

        @Override
        public void attach() {
            super.attach();
            mMovieEditor.getEditorPlayer().seekOutputTimeUs(0);
            if (mLineView != null)
                mLineView.seekTo(mMovieEditor.getEditorPlayer().isReversing() ? 1 : 0);
            if (mTimeAdapter != null)
                mTimeAdapter.setCurrentPosition(mMementoIndex);
        }

        @Override
        public void onSelected() {
            super.onSelected();
            isOnSelect = true;
            boolean isReverse = mMovieEditor.getEditorPlayer().isReversing();
            mMovieEditor.getEditorPlayer().pausePreview();
            mMovieEditor.getEditorPlayer().seekOutputTimeUs(0);
            mLineView.seekTo(isReverse ? 1f : 0f);
            mPlayBtn.setImageDrawable(TuSdkContext.getDrawable(R.drawable.edit_ic_play));
        }

        @Override
        public void detach() {
            super.detach();
        }

        @Override
        public void back() {
            super.back();
            if (mMementoEffectData == null) {
                mLineView.setTimeEffectType(0);
                mMovieEditor.getEditorPlayer().clearTimeEffect();
                mLineView.setShowSelectBar(false);
            } else {
                mMovieEditor.getEditorPlayer().setTimeEffect(mMementoEffectData);
                mTimeAdapter.setCurrentPosition(mMementoIndex);
                mLineView.setShowSelectBar(true);
                mLineView.setLeftBarPosition(mMementoEffectData.getTimeRange().getStartTimeUS()/(float)getEditorPlayer().getTotalTimeUs());
                mLineView.setRightBarPosition(mMementoEffectData.getTimeRange().getEndTimeUS()/(float)getEditorPlayer().getTotalTimeUs());
            }
        }

        @Override
        public void next() {
            super.next();
            mMementoEffectData = mCurrentEffectData;
            mMementoIndex = mCurrentIndex;
            getEditorPlayer().setTimeEffect(mCurrentEffectData);
        }

        /** 播放器状态改变 **/
        public void onStateChanged(int state) {
            if (state != 0) getEditorPlayer().setTimeEffect(mCurrentEffectData);
        }

        /** 播放控件移动到指定的进度 **/
        public void moveToPercent(float percent) {
            if (mLineView != null && !isOnSelect && !isTimeChanged && !mMovieEditor.getEditorPlayer().isPause()) {
                mLineView.seekTo(percent);
            }
        }

        public void updataApplayTimeEffect() {
            if(mCurrentEffectData == null)return;
            mMovieEditor.getEditorPlayer().setTimeEffect(mCurrentEffectData);
        }

        public void showTips(String tips){
            Toast toast = Toast.makeText(getContext(),tips,Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER,0,0);
            toast.show();
        }
    }

    /** 魔法特效 */
    @SuppressLint("ValidFragment")
    public static class ParticleEffectFragment extends EffectFragment {
        /** 当前正在使用的魔法效果 **/
        private volatile TuSdkMediaParticleEffectData mCurrentParticleEffectModel;
        /** 当前应用的魔法效果列表 **/
        private LinkedList<TuSdkMediaParticleEffectData> mDataList;
        /** 上次应用过后的魔法效果备忘列表 **/
        private LinkedList<TuSdkMediaParticleEffectData> mMementoList;
        /** 魔法特效Framgent视图 **/
        private View mParticleView;
        /** 魔法特效列表 **/
        private RecyclerView mParticleRecycle;
        /** 魔法特效列表适配器 **/
        private MagicRecyclerAdapter mParticleAdapter;
        /** 播放进度视图 **/
//        private LineView mLineView;
        private TuSdkMovieScrollPlayLineView mLineView;
        /** 播放按钮 **/
        private ImageView mPlayBtn;
        /** 魔法效果触摸视图 **/
        private RelativeLayout mParticleContent;
        /** 魔法效果设置视图 ( 大小、颜色 ) **/
        private ParticleConfigView mParticleConfig;
        private boolean isContinue = true;
        /** 当前使用的魔法效果下标 **/
        private int mCurrentIndex;
        /** 上次应用的魔法效果下标 **/
        private int mMementoIndex;
        /** 当前魔法效果的Code **/
        private String mCurrentParticleCode;
        private boolean mDrawColorState;
        /** 应用特效的开始时间 **/
        private long mStartTimeUs;
        /** 视频封面列表 **/
        private List<Bitmap> mBitmapList;
        /** 是否正在切换过程中 **/
        boolean isFirstSelect = false;
        /** 上一个进度 **/
        private float prePercent;

        public ParticleEffectFragment(TuSdkMovieEditor movieEditor, RelativeLayout magicContent, ParticleConfigView magicConfig, List<Bitmap> bitmaps) {
            super(movieEditor);
            this.mBitmapList = bitmaps;
            this.mParticleContent = magicContent;
            mDataList = new LinkedList<>();
            mMementoList = new LinkedList<>();
            mParticleAdapter = new MagicRecyclerAdapter();
            mParticleContent.setOnTouchListener(mOnParticleTouchListener);
            mParticleConfig = magicConfig;
        }

        @Nullable
        @Override
        public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            mParticleView = inflater.inflate(R.layout.lsq_editor_component_effect_bottom_particle, null);
            mParticleRecycle = mParticleView.findViewById(R.id.lsq_editor_effect_time_list);
            mParticleRecycle.setLayoutManager(new TuSdkLinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
            mParticleAdapter.setMagicList(Arrays.asList(Constants.PARTICLE_CODES));
            mParticleRecycle.setAdapter(mParticleAdapter);
            mParticleAdapter.setItemCilckListener(new MagicRecyclerAdapter.ItemClickListener() {
                @Override
                public void onItemClick(int position, MagicRecyclerAdapter.MagicViewHolder MagicViewHolder) {
                    if(mCurrentIndex != position)
                    mParticleConfig.setVisible(false);
                    if (position == 0) {
                        if (mDataList.size() == 0) return;
                        mLineView.endAddColorRect();
                        mLineView.deletedColorRect();
                        getEditorPlayer().pausePreview();
                        TuSdkMediaParticleEffectData effectData = mDataList.removeLast();
                        getEditorEffector().removeMediaEffectData(effectData);
                        getEditorPlayer().seekOutputTimeUs(effectData.getAtTimeRange().getStartTimeUS());

                        if (mMovieEditor.getEditorPlayer().isReversing()) {
                            mMovieEditor.getEditorPlayer().seekInputTimeUs(effectData.getAtTimeRange().getEndTimeUS());
                            mLineView.seekTo(effectData.getAtTimeRange().getEndTimeUS()/(float)getEditorPlayer().getTotalTimeUs());
                        } else {
                            mMovieEditor.getEditorPlayer().seekInputTimeUs(effectData.getAtTimeRange().getStartTimeUS());
                            mLineView.seekTo(effectData.getAtTimeRange().getStartTimeUS()/(float)getEditorPlayer().getTotalTimeUs());
                        }

                        boolean isCanDeleted = mDataList.size() > 0;
                        mParticleAdapter.setCanDeleted(isCanDeleted);
                        mParticleAdapter.notifyDataSetChanged();
                        setPlayState(1);
                        return;
                    }

//                    MagicViewHolder.mSelectLayout.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//                            mParticleConfig.setVisible(true);
//                        }
//                    });
//
                    if(mCurrentIndex == position && !mParticleConfig.isVisible()){
                        mParticleConfig.setVisible(true);
                    }

                    mCurrentIndex = position;
                    mCurrentParticleCode = Constants.PARTICLE_CODES[position];
                    mParticleAdapter.setCurrentPosition(mCurrentIndex);
                    if (position != 0 || mDataList.size() == 0) return;
                    TuSdkMediaParticleEffectData effectData = mDataList.removeLast();
                    getEditorEffector().removeMediaEffectData(effectData);
                    if (mDataList.size() != 0) return;
                    mParticleAdapter.setCanDeleted(false);
                    mParticleAdapter.notifyDataSetChanged();

                }
            });

            mLineView = mParticleView.findViewById(R.id.lsq_editor_time_play_range);
            mLineView.setType(0);
            mLineView.setOnProgressChangedListener(mOnScrollingPlayListener);
            mLineView.setOnBackListener(new TuSdkMovieScrollView.OnColorGotoBackListener() {
                @Override
                public void onGotoBack(float percent) {
                    prePercent = percent;
                }
            });
            if (mBitmapList != null) {
                if (mBitmapList != null) {
                    for (Bitmap bp : mBitmapList)
                        mLineView.addBitmap(bp);
                }
            }

            mPlayBtn = mParticleView.findViewById(R.id.lsq_editor_time_play);
            mPlayBtn.setOnClickListener(mOnClickListener);

            return mParticleView;
        }

        public void addCoverBitmap(Bitmap bitmap) {
            if (mLineView == null) return;
            mLineView.addBitmap(bitmap);
        }

        private View.OnClickListener mOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.lsq_editor_time_play:
                        //点击播放
                        isFirstSelect = false;
                        if (getEditorPlayer().isPause()) {
                            getEditorPlayer().startPreview();
                            isContinue = true;
                        } else
                            getEditorPlayer().pausePreview();
                        break;
                }
            }
        };

        @Override
        public void attach() {
            super.attach();
            //恢复之前的情况
            if (mMementoList.size() == 0) {
                mParticleAdapter.setCanDeleted(false);
                mParticleAdapter.notifyItemChanged(0);
            }else {
                mParticleAdapter.setCanDeleted(true);
                mParticleAdapter.notifyItemChanged(0);
            }

            for (TuSdkMediaParticleEffectData item : mMementoList) {
                recoveryEffect(item);
            }
//            mParticleAdapter.setCurrentPosition(mMementoIndex);
            mParticleContent.setOnTouchListener(mOnParticleTouchListener);

            getEditorPlayer().seekOutputTimeUs(0);
            if (mLineView != null)
                mLineView.seekTo(getEditorPlayer().isReversing() ? 1 : 0);
            if (mLineView != null)
                mLineView.setTimeEffectType(getEditorPlayer().isReversing()? 1 : 0);
        }


        @Override
        public void onSelected() {
            super.onSelected();
            isFirstSelect = true;
            boolean isReverse = getEditorPlayer().isReversing();
            getEditorPlayer().pausePreview();
            getEditorPlayer().seekOutputTimeUs(0);
            mLineView.seekTo(isReverse ? 1f : 0f);
            if (mLineView != null)
                mLineView.setTimeEffectType(getEditorPlayer().isReversing()?1:0);
            mPlayBtn.setImageDrawable(TuSdkContext.getDrawable(R.drawable.edit_ic_play));
            mParticleContent.setOnTouchListener(mOnParticleTouchListener);
        }

        /** 恢复之前应用的特效 **/
        private void recoveryEffect(TuSdkMediaParticleEffectData mediaEffectData) {
            mDataList.add(mediaEffectData);
            String screenCode = mediaEffectData.getParticleCode();
            float startPercent = mediaEffectData.getAtTimeRange().getStartTimeUS()/(float)getEditorPlayer().getTotalTimeUs();
            float endPercent = mediaEffectData.getAtTimeRange().getEndTimeUS()/(float)getEditorPlayer().getTotalTimeUs();
            mLineView.recoverColorRect(TuSdkContext.getColorResId("lsq_margic_effect_color_" + screenCode),startPercent,endPercent);
        }

        public void clearSelect(){
            mParticleContent.setOnTouchListener(null);
        }

        @Override
        public void detach() {
            super.detach();
            mParticleAdapter.setCurrentPosition(0);
            mParticleContent.setOnTouchListener(null);
            mParticleConfig.setVisible(false);
            mCurrentParticleCode =  null;
        }

        @Override
        public void back() {
            super.back();
            for (TuSdkMediaEffectData item : mDataList) {
                getEditorEffector().removeMediaEffectData(item);
                mLineView.deletedColorRect();
            }

            for (TuSdkMediaEffectData item : mMementoList) {
                getEditorEffector().addMediaEffectData(item);
            }

            mDataList.clear();
            mParticleConfig.setVisible(false);
        }

        @Override
        public void next() {
            super.next();

            mMementoList.clear();
            mMementoList.addAll(mDataList);
            mMementoIndex = mCurrentIndex;
            mDataList.clear();
            mParticleConfig.setVisible(false);
            mLineView.clearAllColorRect();
        }

        @Override
        public void onPause() {
            super.onPause();
            mParticleConfig.setVisible(false);
        }

        /**
         * 设置播放状态
         *
         * @param state 0 播放  1 暂停
         * @since V3.0.0
         */
        public void setPlayState(int state) {
            if (mPlayBtn == null) return;
            mPlayBtn.setImageDrawable(TuSdkContext.getDrawable(state == 0 ? R.drawable.edit_ic_pause : R.drawable.edit_ic_play));
        }

        /** 播放空间滚动时的回调 **/
        private TuSdkMovieScrollView.OnProgressChangedListener mOnScrollingPlayListener = new TuSdkMovieScrollView.OnProgressChangedListener() {
            @Override
            public void onProgressChanged(float progress, boolean isTouching) {
                currentPercent  = progress;
                if(!isTouching)return;
                if(isTouching)getEditorPlayer().pausePreview();

                if (getEditorPlayer().isPause()) {
                    long seekUs;
                    if (getEditorPlayer().isReversing()) {
                        seekUs = (long) ((1 - progress) * getEditorPlayer().getInputTotalTimeUs());
                        getEditorPlayer().seekOutputTimeUs(seekUs);
                    } else {
                        seekUs = (long) (getEditorPlayer().getInputTotalTimeUs() * progress);
                        getEditorPlayer().seekInputTimeUs(seekUs);
                    }
                }
            }

            @Override
            public void onCancelSeek() {

            }
        };

        private View.OnTouchListener mOnParticleTouchListener = new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                String particleCode = mCurrentParticleCode;
                TuSdkMediaParticleEffectData currentEffectModel = mCurrentParticleEffectModel;

                if (particleCode == null ) return false;

                /** 开始播放视频并预览已设置的特效 */
                if (getEditorPlayer().getCurrentOutputTimeUs() >= getEditorPlayer().getOutputTotalTimeUS() && getEditorPlayer().getOutputTotalTimeUS() > 0) {
                    return false;
                }

                /** 倒序情况下特效添加到头则返回 **/
                if(getEditorPlayer().isReversing() && (0 == currentPercent) ){
                    return false;
                }

                if(mLineView.getCurrentPercent() == 1 && !getEditorPlayer().isReversing()){
                    return false;
                }

                if(getEditorPlayer().getCurrentTimeUs() >= getEditorPlayer().getTotalTimeUs() && !getEditorPlayer().isReversing()){
                    return false;
                }


                final PointF pointF = getConvertedPoint(event.getX(), event.getY());


                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        isFirstSelect = false;
                            isContinue = false;
                            mParticleConfig.setVisible(false);

                            // 构建魔法特效
                            mCurrentParticleEffectModel = new TuSdkMediaParticleEffectData(particleCode);
                            mCurrentParticleEffectModel.setSize(mParticleConfig.getSize());
                            mCurrentParticleEffectModel.setColor(mParticleConfig.getColor());
                            mCurrentParticleEffectModel.putPoint(getEditorPlayer().getCurrentTimeUs(), pointF);
                            currentEffectModel = mCurrentParticleEffectModel;
                            // 预览魔法特效
                            getEditorEffector().addMediaEffectData(currentEffectModel);
                            mDataList.addLast(currentEffectModel);
                            mLineView.addColorRect(TuSdkContext.getColor("lsq_margic_effect_color_" + particleCode));
                            if (!mParticleAdapter.isCanDeleted()) {
                                mParticleAdapter.setCanDeleted(true);
                                mParticleAdapter.notifyItemChanged(0);
                            }

                        long totalUs = getEditorPlayer().getTotalTimeUs();
                        float percent = mLineView.getCurrentPercent();
                        mStartTimeUs = (long) (totalUs*percent);
                        getEditorPlayer().startPreview();
                        if (getEditorPlayer().isReversing()) {
                            currentEffectModel.setAtTimeRange(TuSdkTimeRange.makeTimeUsRange(0, mStartTimeUs));
                        } else {
                            currentEffectModel.setAtTimeRange(TuSdkTimeRange.makeTimeUsRange(mStartTimeUs, Long.MAX_VALUE));
                        }
                        mDrawColorState = true;
                        currentEffectModel.getFilterWrap().updateParticleEmitPosition(pointF);

                        break;
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL:

                        if (currentEffectModel == null) return true;
                        if (presscancel()) return false;

                        break;
                    case MotionEvent.ACTION_MOVE:

                            if (currentEffectModel == null) return false;
                            // 更新魔法特效触发位置（预览）
                            currentEffectModel.getFilterWrap().updateParticleEmitPosition(pointF);
                            // 记录魔法特效触发位置
                            currentEffectModel.putPoint(getEditorPlayer().getCurrentInputTimeUs(), pointF);

                            if (!getEditorPlayer().isReversing() && getEditorPlayer().getCurrentTimeUs() >= getEditorPlayer().getTotalTimeUs() && getEditorPlayer().getTotalTimeUs() != 0) {
                                presscancel();
                            }

                            /** 倒序情况下特效添加到头则返回 **/
                            if (getEditorPlayer().isReversing() && (0 == mLineView.getCurrentPercent())) {
                                presscancel();
                            }

                            if (mLineView.getCurrentPercent() == 1 && !getEditorPlayer().isReversing()) {
                                presscancel();
                            }
                        break;
                }

                return true;
            }

            private boolean presscancel() {
                // 取消预览魔法特效
                TuSdkTimeRange timeRange = mCurrentParticleEffectModel.getAtTimeRange();

                if (getEditorPlayer().isReversing()) {
                    timeRange.setStartTimeUs((long) (mLineView.getCurrentPercent() * getEditorPlayer().getTotalTimeUs()));
                } else {
                    timeRange.setEndTimeUs((long) (mLineView.getCurrentPercent() * getEditorPlayer().getTotalTimeUs()));
                }
                mCurrentParticleEffectModel.setAtTimeRange(timeRange);

                mCurrentParticleEffectModel = null;
                getEditorPlayer().pausePreview();
                mLineView.endAddColorRect();
                mDrawColorState = false;
                return false;
            }
        };

        /**
         * 点击坐标系和绘制动画坐标系不同，需要转换坐标
         *
         * @return
         */
        public PointF getConvertedPoint(float x, float y) {
            // 获取视频大小
            TuSdkSize videoSize = TuSdkSize.create(mMovieEditor.getEditorTransCoder().getOutputVideoInfo().width, mMovieEditor.getEditorTransCoder().getOutputVideoInfo().height);

            TuSdkSize previewSize = new TuSdkSize(mParticleContent.getMeasuredWidth(), mParticleContent.getMeasuredHeight());

            TuSdkSize screenSize = previewSize;

            RectF previewRectF = new RectF(0, (screenSize.height - previewSize.height) / (float) 2,
                    previewSize.width, (screenSize.height + previewSize.height) / (float) 2);

            if (!previewRectF.contains(x, y))
                return new PointF(-1, -1);

            // 将基于屏幕的坐标转换成基于预览区域的坐标
            y -= previewRectF.top;

            // 将预览区域的坐标转换成基于视频实际大小的坐标点
            float videoX = x / (float) previewSize.width * videoSize.minSide();
            float videoY = y / (float) previewSize.height * videoSize.maxSide();

            PointF convertedPoint = new PointF(videoX, videoSize.maxSide() - videoY);
            return convertedPoint;
        }

        private float currentPercent;
        /** 更新进度 **/
        public void moveToPercent(float percentage, long playbackTimeUs) {
            if (mLineView == null) return;
            currentPercent = percentage;
            if (mDrawColorState) {
//                mLineView.starDrawColor(TuSdkContext.getColor("lsq_margic_effect_color_" + mCurrentParticleCode), mStartTimeUs, playbackTimeUs);
            }
            if (isFirstSelect) return;
            if (mDrawColorState) {
                mLineView.seekTo(percentage);
            }

            if (isContinue) {
                mLineView.seekTo(percentage);
            }
        }

        public void onPlayerStateChanged(int state) {
            if (mCurrentParticleEffectModel != null) {
                //暂停
                if (state == 1) {
                    if (mCurrentParticleEffectModel != null) {
                        TuSdkTimeRange timeRange = mCurrentParticleEffectModel.getAtTimeRange();
                        if (mMovieEditor.getEditorPlayer().isReversing()) {
                            timeRange.setStartTimeUs((long) (mLineView.getCurrentPercent() * getEditorPlayer().getTotalTimeUs()));
                        }
                        else {
                            timeRange.setEndTimeUs(getEditorPlayer().getCurrentTimeUs());
                            mLineView.seekTo(mMovieEditor.getEditorPlayer().getCurrentInputTimeUs()/(float)getEditorPlayer().getTotalTimeUs());
                            if(timeRange.getEndTimeUS() < prePercent * getEditorPlayer().getTotalTimeUs()){
                                timeRange.setEndTimeUs((long) (prePercent * getEditorPlayer().getTotalTimeUs()));
                            }
                        }
                        mCurrentParticleEffectModel.setAtTimeRange(timeRange);
                    }
                    mDrawColorState = false;
                    mLineView.endAddColorRect();
                    mCurrentParticleEffectModel = null;

                } else {
                    mDrawColorState = true;
                    isContinue = false;
                }
            }
        }
    }

    /** 魔法效果设置 **/
    private class ParticleConfigView {
        private LinearLayout mContent;
        private ConfigViewSeekBar mSizeSeekBar;
        private ColorView mColorSeekBar;

        public ParticleConfigView() {
            mContent = getEditorController().getActivity().findViewById(R.id.lsq_magic_config);
            mSizeSeekBar = getEditorController().getActivity().findViewById(R.id.lsq_magic_size_seekbar);
            ConfigViewParams params = new ConfigViewParams();
            params.appendFloatArg("size", 0f);
            mSizeSeekBar.setConfigViewArg(params.getArgs().get(0));

            mColorSeekBar = getEditorController().getActivity().findViewById(R.id.lsq_magic_color_seekView);
            mColorSeekBar.setCircleRadius(10);
        }

        public void setVisible(boolean visible) {
            mContent.setVisibility(visible ? View.VISIBLE : View.GONE);
        }

        public boolean isVisible(){
            return mContent.getVisibility() == View.VISIBLE;
        }

        /** 获取大小 **/
        public float getSize() {
            return mSizeSeekBar.getSeekbar().getProgress();
        }

        /** 获取颜色 **/
        public int getColor() {
            return mColorSeekBar.getSelectColor();
        }
    }

    @SuppressLint("ValidFragment")
    protected static class EffectFragment extends Fragment {
        protected TuSdkMovieEditor mMovieEditor;
        protected boolean isAnimationStarting = false;

        public EffectFragment(TuSdkMovieEditor mMovieEditor) {
            this.mMovieEditor = mMovieEditor;
        }

        @Nullable
        @Override
        public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            return super.onCreateView(inflater, container, savedInstanceState);
        }

        /**
         * 获取编辑器
         * @return
         */
        protected TuSdkMovieEditor getMovieEditor(){
            if(mMovieEditor == null){
                TLog.e("EffectFragment is not init");
                return null;
            }
            return mMovieEditor;
        }

        /**
         * 获取编辑播放器
         * @return
         */
        protected TuSdkEditorPlayer getEditorPlayer(){
            return getMovieEditor().getEditorPlayer();
        }

        /**
         * 获取编辑特效器
         * @return
         */
        protected TuSdkEditorEffector getEditorEffector(){
            return getMovieEditor().getEditorEffector();
        }

        public void onAnimationStart() {
            isAnimationStarting = true;
        }

        public void onAnimationEnd() {
            isAnimationStarting = false;
        }



        /** 同步组件的attach方法 **/
        public void attach() {
        }

        /** 同步组件的detach方法 */
        public void detach() {
        }

        /** 同步组件的返回事件 */
        public void back() {
        }


        /** 同步组件的确认事件 */
        public void next() {
        }

        /** 被选中 **/
        public void onSelected() {
        }

    }

}
