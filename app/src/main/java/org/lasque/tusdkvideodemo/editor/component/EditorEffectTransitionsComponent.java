/**
 * TuSDK
 * TuSDKVideoDemo3
 * EditorEffectTransitionsComponent.java
 *
 * @author H.ys
 * @Date 2019/6/13 17:57
 * @Copyright (c) 2019 tusdk.com. All rights reserved.
 */
package org.lasque.tusdkvideodemo.editor.component;


import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import androidx.annotation.IdRes;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import org.lasque.tusdk.core.TuSdkContext;
import org.lasque.tusdk.core.seles.sources.TuSdkEditorEffector;
import org.lasque.tusdk.core.seles.sources.TuSdkEditorPlayer;
import org.lasque.tusdk.core.seles.sources.TuSdkMovieEditor;
import org.lasque.tusdk.core.seles.tusdk.TuSDKMediaTransitionWrap;
import org.lasque.tusdk.core.struct.TuSdkSize;
import org.lasque.tusdk.core.utils.TLog;
import org.lasque.tusdk.core.utils.ThreadHelper;
import org.lasque.tusdk.core.view.recyclerview.TuSdkLinearLayoutManager;
import org.lasque.tusdk.video.editor.TuSdkMediaEffectData;
import org.lasque.tusdk.video.editor.TuSdkMediaTransitionEffectData;
import org.lasque.tusdk.video.editor.TuSdkTimeRange;
import com.upyun.shortvideo.R;
import org.lasque.tusdkvideodemo.editor.MovieEditorController;
import org.lasque.tusdkvideodemo.views.TransitionsRecyclerAdapter;
import org.lasque.tusdkvideodemo.views.editor.TuSdkMovieScrollPlayLineView;
import org.lasque.tusdkvideodemo.views.editor.playview.TuSdkMovieScrollView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 *
 */
public class EditorEffectTransitionsComponent extends EditorComponent {

    /**
     * 判断为按压的最短触摸时间
     **/
    private static final int MIN_PRESS_DURATION_MILLIS = 150;

    /**
     * 当前组件的底部控件
     **/
    private View mBottomView;


    /**
     *
     */
    private FrameLayout mContentLayout;
    /**
     * 关闭按钮
     **/
    private ImageButton mBackBtn;
    /**
     * 应用按钮
     **/
    private ImageButton mNextBtn;

    private EditorEffectTransitionsComponent.TransitionsEffectFragment mTransitionsEffectFragment;

    /**
     * 特效 Frgament 列表
     **/
    private List<EditorEffectTransitionsComponent.EffectFragment> mFragmentList;

    /**
     * 视频封面图片列表
     **/
    private List<Bitmap> mBitmapList = new ArrayList<>();

    /**
     * 默认特效持续时间
     **/
    private static long mEffectDurationUs = 1 * 1000000;

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

    /**
     * 播放进度回调
     **/
    private TuSdkEditorPlayer.TuSdkProgressListener mProgressLisntener = new TuSdkEditorPlayer.TuSdkProgressListener() {
        @Override
        public void onStateChanged(int state) {
            if (mTransitionsEffectFragment != null) mTransitionsEffectFragment.setPlayState(state);
            if (mTransitionsEffectFragment != null)
                mTransitionsEffectFragment.onPlayerStateChanged(state);
        }

        @Override
        public void onProgress(long playbackTimeUs, long totalTimeUs, float percentage) {
            if (isAnimationStaring) return;
            if (mTransitionsEffectFragment != null)
                mTransitionsEffectFragment.moveToPercent(percentage, playbackTimeUs);
        }
    };

    /**
     * 当期组件底部点击事件
     **/
    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.lsq_effect_close:
                    mTransitionsEffectFragment.back();
                    getEditorController().onBackEvent();
                    break;
                case R.id.lsq_effect_sure:
                    mTransitionsEffectFragment.next();
                    getEditorController().onBackEvent();
                    break;
            }
        }
    };

    /**
     * 创建当前组件
     *
     * @param editorController
     */
    public EditorEffectTransitionsComponent(MovieEditorController editorController) {
        super(editorController);
        mComponentType = EditorComponentType.TransitionsEffect;
        getEditorPlayer().addPreviewSizeChangeListener(mOnDisplayChangeListener);
    }

    @Override
    public void attach() {
        getEditorController().getBottomView().addView(getBottomView());
        if (getEditorController().getActivity().getSupportFragmentManager().getFragments() == null || !getEditorController().getActivity().getSupportFragmentManager().getFragments().contains(mTransitionsEffectFragment)) {
            getEditorController().getActivity().getSupportFragmentManager().beginTransaction().add(R.id.lsq_editor_effect_content, mTransitionsEffectFragment).commit();
        }

        getEditorPlayer().pausePreview();
        getEditorPlayer().seekOutputTimeUs(0);

        if (mTransitionsEffectFragment != null) mTransitionsEffectFragment.attach();

        getEditorController().getVideoContentView().setClickable(false);
        getEditorController().getPlayBtn().setVisibility(View.GONE);
    }

    @Override
    public void onAnimationStart() {
        super.onAnimationStart();
        if (mTransitionsEffectFragment != null) mTransitionsEffectFragment.onAnimationStart();
    }

    @Override
    public void onAnimationEnd() {
        super.onAnimationEnd();
        if (mTransitionsEffectFragment != null) mTransitionsEffectFragment.onAnimationEnd();
    }

    @Override
    public void detach() {
        if (mTransitionsEffectFragment != null) mTransitionsEffectFragment.detach();
//        getEditorController().getActivity().getSupportFragmentManager().beginTransaction().remove(mTransitionsEffectFragment).commit();

        getEditorPlayer().seekTimeUs(0);
        getEditorPlayer().pausePreview();
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

    /**
     * 初始化BottomView
     **/
    private void initBottomView() {
        mBottomView = LayoutInflater.from(getEditorController().getActivity()).inflate(R.layout.lsq_editor_component_transition_bottom, null);

        mBackBtn = findViewById(R.id.lsq_effect_close);
        mNextBtn = findViewById(R.id.lsq_effect_sure);


        mBackBtn.setOnClickListener(mOnClickListener);
        mNextBtn.setOnClickListener(mOnClickListener);

        mTransitionsEffectFragment = new EditorEffectTransitionsComponent.TransitionsEffectFragment(getEditorController().getMovieEditor(), mBitmapList);


        getEditorController().getMovieEditor().getEditorPlayer().addProgressListener(mProgressLisntener);
    }

    private <T extends View> T findViewById(@IdRes int id) {
        return mBottomView.findViewById(id);
    }

    @Override
    public void addCoverBitmap(Bitmap bitmap) {
        getBottomView();
        mBitmapList.add(bitmap);
        if (mTransitionsEffectFragment != null) mTransitionsEffectFragment.addCoverBitmap(bitmap);
    }

    /**
     * 转场特效Fragment
     */
    @SuppressLint("ValidFragment")
    public static class TransitionsEffectFragment extends EditorEffectTransitionsComponent.EffectFragment {
        /**
         * 视频编辑器
         **/
        private TuSdkMovieEditor mMovieEditor;
        /**
         * 当前正在应用的场景特效数据
         **/
        private TuSdkMediaTransitionEffectData mediaSceneEffectData;
        /**
         * 当前应用的场景数据列表
         **/
        private LinkedList<TuSdkMediaTransitionEffectData> mDataList;
        /**
         * 当前应用的场景数据备忘列表
         **/
        private LinkedList<TuSdkMediaTransitionEffectData> mMementoList;
        private Handler mHandler = new Handler();
        /**
         * 场景特效Framgent的视图
         **/
        private View mSceneView;
        /**
         * 当前场景特效组件的播放按钮
         **/
        private ImageView mPlayBtn;
        /**
         * 场景特效列表
         **/
        private RecyclerView mSceneRecycle;
        /**
         * 场景特效列表适配器
         **/
        private TransitionsRecyclerAdapter mSceneAdapter;
        /**
         * 播放控件
         **/
        private TuSdkMovieScrollPlayLineView mLineView;
        /**
         * 视频封面Bitmap列表
         **/
        private List<Bitmap> mBitmapList;
        /**
         * 当前正在应用的场景特效Code
         **/
        public volatile TuSDKMediaTransitionWrap.TuSDKMediaTransitionType mTransitionType;
        /**
         * 当前应用场景特效的开始时间
         **/
        public long mStartTimeUs;
        /**
         * 当前是否允许绘制特效色块
         **/
        public boolean mDrawColorState = false;
        /**
         * 当前组件是否被选择
         **/
        boolean isOnSelect = false;
        private boolean isContinue = true;

        private float prePercent = 0;


        private TransitionsRecyclerAdapter.OnItemTouchListener mOnItemTouchListener = new TransitionsRecyclerAdapter.OnItemTouchListener() {
            /** 当前触摸的持续时间 **/
            long duration = 0;
            boolean isTouching = false;

            /**
             * @param event
             * @param position
             * @param sceneViewHolder
             */
            @Override
            public void onItemTouch(MotionEvent event, final int position, final TransitionsRecyclerAdapter.TransitionsViewHolder sceneViewHolder) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        //按下场景特效Item
                        if (isTouching) return;
                        isTouching = true;
                        mHandler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                onPressSceneEffect(sceneViewHolder, position);
                            }
                        }, MIN_PRESS_DURATION_MILLIS);
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


        };


        public TransitionsEffectFragment(TuSdkMovieEditor movieEditor, final List<Bitmap> bitmaps) {
            super(movieEditor);
            this.mMovieEditor = movieEditor;
            this.mBitmapList = bitmaps;
            mDataList = new LinkedList<>();
            mMementoList = new LinkedList<>();
            mSceneAdapter = new TransitionsRecyclerAdapter();
//            mSceneAdapter.setOnItemTouchListener(mOnItemTouchListener);
            mSceneAdapter.setItemCilckListener(new TransitionsRecyclerAdapter.ItemClickListener() {
                @Override
                public void onItemClick(final int position, final TransitionsRecyclerAdapter.TransitionsViewHolder ScreenViewHolder) {
                    if (mMovieEditor == null) return;
                    if (position == 0 && mDataList.size() != 0) {
                        if (!mSceneAdapter.isCanDeleted()) return;
                        mLineView.deletedColorRect();
                        mMovieEditor.getEditorPlayer().pausePreview();
                        TuSdkMediaEffectData mediaEffectData = mDataList.removeLast();
                        mMovieEditor.getEditorEffector().removeMediaEffectData(mediaEffectData);
                        if (mMovieEditor.getEditorPlayer().isReversing()) {
                            mMovieEditor.getEditorPlayer().seekInputTimeUs(mediaEffectData.getAtTimeRange().getEndTimeUS());
                            mLineView.seekTo(mediaEffectData.getAtTimeRange().getEndTimeUS() / (float) getEditorPlayer().getTotalTimeUs());
                        } else {
                            mMovieEditor.getEditorPlayer().seekInputTimeUs(mediaEffectData.getAtTimeRange().getStartTimeUS());
                            mLineView.seekTo(mediaEffectData.getAtTimeRange().getStartTimeUS() / (float) getEditorPlayer().getTotalTimeUs());
                        }
                        boolean isCanDeleted = mDataList.size() > 0;
                        mSceneAdapter.setCanDeleted(isCanDeleted);
                        mSceneAdapter.notifyItemChanged(0);
                        setPlayState(1);
                        return;
                    }
                    mHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            onPressSceneEffect(ScreenViewHolder, position);
                        }
                    }, MIN_PRESS_DURATION_MILLIS);

//                    if (position != 0 || mDataList.size() == 0 || mMovieEditor == null || !mSceneAdapter.isCanDeleted())
//                        return;
                    ThreadHelper.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mHandler.removeMessages(0);
                            onReleaseSceneEffect(ScreenViewHolder, position);
                            mLineView.endAddColorRect();

                        }
                    }, MIN_PRESS_DURATION_MILLIS + 200 + (mEffectDurationUs / 1000));

                }
            });
        }

        /**
         * @param sceneViewHolder
         * @param position
         */
        private void onPressSceneEffect(TransitionsRecyclerAdapter.TransitionsViewHolder sceneViewHolder, int position) {
            if (position == 0) return;

            /** 开始播放视频并预览已设置的特效 */
            if (getEditorPlayer().getCurrentOutputTimeUs() >= getEditorPlayer().getOutputTotalTimeUS()) {
                sceneViewHolder.mSelectLayout.setImageResource(TuSdkContext.getColorResId("lsq_color_transparent"));
                return;
            }

            /** 倒序情况下特效添加到头则返回 **/
            if (getEditorPlayer().isReversing() && (0 == currentPercent)) {
                sceneViewHolder.mSelectLayout.setImageResource(TuSdkContext.getColorResId("lsq_color_transparent"));
                return;
            }

            if (mLineView.getCurrentPercent() == 1 && !getEditorPlayer().isReversing()) {
                sceneViewHolder.mSelectLayout.setImageResource(TuSdkContext.getColorResId("lsq_color_transparent"));
                return;
            }

            if (getEditorPlayer().getCurrentTimeUs() >= getEditorPlayer().getTotalTimeUs() && !getEditorPlayer().isReversing()) {
                sceneViewHolder.mSelectLayout.setImageResource(TuSdkContext.getColorResId("lsq_color_transparent"));
                return;
            }

//            if (!mMovieEditor.getEditorPlayer().isPause()) {
//                mMovieEditor.getEditorPlayer().pausePreview();
//                return;
//            }

            mTransitionType = mSceneAdapter.getTransitionType(position);


            long totalUs = getEditorPlayer().getTotalTimeUs();
            float percent = mLineView.getCurrentPercent();
            mStartTimeUs = (long) (totalUs * percent);
            mMovieEditor.getEditorPlayer().startPreview();
            mediaSceneEffectData = new TuSdkMediaTransitionEffectData(mTransitionType);
            //设置ViewModel
            if (mMovieEditor.getEditorPlayer().isReversing()) {
                mediaSceneEffectData.setAtTimeRange(TuSdkTimeRange.makeTimeUsRange(mStartTimeUs - mEffectDurationUs < 0 ? 0 : mStartTimeUs - mEffectDurationUs,mStartTimeUs));
            } else {
                mediaSceneEffectData.setAtTimeRange(TuSdkTimeRange.makeTimeUsRange(mStartTimeUs, mStartTimeUs + (mEffectDurationUs)));
            }
            if (mediaSceneEffectData.getFilterArg("duration") != null) {
                mediaSceneEffectData.getFilterArg("duration").setValue(mEffectDurationUs * 1000);
                mediaSceneEffectData.submitParameters();
            }

            // 预览转场特效
            mMovieEditor.getEditorEffector().addMediaEffectData(mediaSceneEffectData);

            mLineView.endAddColorRect();
            mLineView.addColorRect(TuSdkContext.getColor("lsq_scence_effect_color_" + mTransitionType));
            mDataList.add(mediaSceneEffectData);
            mediaSceneEffectData = null;
            isOnSelect = false;
//            sceneViewHolder.mSelectLayout.setImageResource(TuSdkContext.getColorResId("lsq_scence_effect_color_" + mTransitionType));
            if (!mSceneAdapter.isCanDeleted()) {
                mSceneAdapter.setCanDeleted(true);
                mSceneAdapter.notifyItemChanged(0);
            }
        }

        private void onReleaseSceneEffect(TransitionsRecyclerAdapter.TransitionsViewHolder sceneViewHolder, int position) {
            mMovieEditor.getEditorPlayer().pausePreview();
            sceneViewHolder.mSelectLayout.setImageResource(TuSdkContext.getColorResId("lsq_color_transparent"));
        }

        @Nullable
        @Override
        public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            mSceneView = inflater.inflate(R.layout.lsq_editor_component_effect_bottom_transition, null);
            mSceneRecycle = mSceneView.findViewById(R.id.lsq_editor_effect_scene_list);
            mSceneRecycle.setLayoutManager(new TuSdkLinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
            mSceneRecycle.setAdapter(mSceneAdapter);
            List<TuSDKMediaTransitionWrap.TuSDKMediaTransitionType> lists = Arrays.asList(TuSDKMediaTransitionWrap.TuSDKMediaTransitionType.values());
            mSceneAdapter.setSceneList(lists);
            mLineView = mSceneView.findViewById(R.id.lsq_editor_scene_play_range);
            mLineView.setType(0);
            if (mMovieEditor.getEditorPlayer() != null)
                mLineView.seekTo(mMovieEditor.getEditorPlayer().isReversing() ? 1 : 0);
            if (mMovieEditor.getEditorPlayer() != null)
                mLineView.setTimeEffectType(mMovieEditor.getEditorPlayer().isReversing() ? 1 : 0);
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
                currentPercent = progress;
                if (!isTouching) return;
                if (isTouching) getEditorPlayer().pausePreview();
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
                if (!getEditorPlayer().isReversing()){
                    float progress_1 = getEditorPlayer().getCurrentTimeUs() / (float) getEditorPlayer().getTotalTimeUs();
                    mLineView.seekTo(progress_1);
                }
            }
        };

        @Override
        public void attach() {
            super.attach();
            //恢复之前的情况
            if (mMementoList.size() == 0) {
                mSceneAdapter.setCanDeleted(false);
                mSceneAdapter.notifyItemChanged(0);
            } else {
                mSceneAdapter.setCanDeleted(true);
                mSceneAdapter.notifyItemChanged(0);
            }

            for (TuSdkMediaTransitionEffectData item : mMementoList) {
                recoveryEffect(item);
            }
            mMovieEditor.getEditorPlayer().seekOutputTimeUs(0);
            if (mLineView != null)
                mLineView.setTimeEffectType(mMovieEditor.getEditorPlayer().isReversing() ? 1 : 0);
            if (mLineView != null)
                mLineView.seekTo(mMovieEditor.getEditorPlayer().isReversing() ? 1 : 0);
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
                mLineView.setTimeEffectType(mMovieEditor.getEditorPlayer().isReversing() ? 1 : 0);
            mPlayBtn.setImageDrawable(TuSdkContext.getDrawable(R.drawable.edit_ic_play));

        }

        /**
         * 恢复之前应用的特效
         **/
        private void recoveryEffect(TuSdkMediaTransitionEffectData mediaEffectData) {
            TuSDKMediaTransitionWrap.TuSDKMediaTransitionType sceneCode = mediaEffectData.getEffectCode();
            float startPercent = mediaEffectData.getAtTimeRange().getStartTimeUS() / (float) getEditorPlayer().getTotalTimeUs();
            float endPercent = mediaEffectData.getAtTimeRange().getEndTimeUS() / (float) getEditorPlayer().getTotalTimeUs();
            mLineView.recoverColorRect(TuSdkContext.getColor("lsq_scence_effect_color_" + sceneCode), startPercent, endPercent);
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
            getEditorPlayer().seekTimeUs(0);
        }

        @Override
        public void next() {
            super.next();
            mMementoList.clear();
            mMementoList.addAll(mDataList);
            mDataList.clear();
            mLineView.clearAllColorRect();
            getEditorPlayer().seekTimeUs(0);
        }


        /**
         * 同步播放器的播放状态
         *
         * @param state*/
        public void onPlayerStateChanged(int state) {

            if (mediaSceneEffectData != null) {
                //暂停
                if (state == 1) {
//                    if (mediaSceneEffectData != null) {
//                        TuSdkTimeRange timeRange = mediaSceneEffectData.getAtTimeRange();
//                        if (mMovieEditor.getEditorPlayer().isReversing()) {
//                            timeRange.setStartTimeUs((long) (mLineView.getCurrentPercent() * getEditorPlayer().getTotalTimeUs()));
//                        } else {
//                            timeRange.setEndTimeUs(getEditorPlayer().getCurrentTimeUs());
//                            mLineView.seekTo(mMovieEditor.getEditorPlayer().getCurrentInputTimeUs() / (float) getEditorPlayer().getTotalTimeUs());
//                            if (prePercent < 1 && timeRange.getEndTimeUS() < prePercent * getEditorPlayer().getTotalTimeUs()) {
//                                timeRange.setEndTimeUs((long) (prePercent * getEditorPlayer().getTotalTimeUs()));
//                            }
//                        }
//                        mediaSceneEffectData.setAtTimeRange(timeRange);
//                    }
                    mDrawColorState = false;
                    mLineView.endAddColorRect();
//                    mediaSceneEffectData = null;

                } else {
                    mDrawColorState = true;
                    isContinue = false;
                }
            }


        }

        /**
         * 更新进度
         **/
        private boolean isPreState = true;
        private float currentPercent;

        /**
         * 移动进度到播放视图
         **/
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
         *
         * @return
         */
        protected TuSdkMovieEditor getMovieEditor() {
            if (mMovieEditor == null) {
                TLog.e("EffectFragment is not init");
                return null;
            }
            return mMovieEditor;
        }

        /**
         * 获取编辑播放器
         *
         * @return
         */
        protected TuSdkEditorPlayer getEditorPlayer() {
            return getMovieEditor().getEditorPlayer();
        }

        /**
         * 获取编辑特效器
         *
         * @return
         */
        protected TuSdkEditorEffector getEditorEffector() {
            return getMovieEditor().getEditorEffector();
        }

        public void onAnimationStart() {
            isAnimationStarting = true;
        }

        public void onAnimationEnd() {
            isAnimationStarting = false;
        }


        /**
         * 同步组件的attach方法
         **/
        public void attach() {
        }

        /**
         * 同步组件的detach方法
         */
        public void detach() {
        }

        /**
         * 同步组件的返回事件
         */
        public void back() {
        }


        /**
         * 同步组件的确认事件
         */
        public void next() {
        }

        /**
         * 被选中
         **/
        public void onSelected() {
        }

    }
}
