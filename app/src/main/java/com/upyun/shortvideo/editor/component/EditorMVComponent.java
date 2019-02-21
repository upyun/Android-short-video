package com.upyun.shortvideo.editor.component;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;

import org.lasque.tusdk.core.TuSdkContext;
import org.lasque.tusdk.core.decoder.TuSDKAudioDecoderTaskManager;
import org.lasque.tusdk.core.decoder.TuSDKVideoInfo;
import org.lasque.tusdk.core.seles.sources.TuSdkEditorPlayer;
import org.lasque.tusdk.core.struct.TuSdkMediaDataSource;
import org.lasque.tusdk.core.utils.ThreadHelper;
import org.lasque.tusdk.core.view.TuSdkViewHelper;
import org.lasque.tusdk.modules.view.widget.sticker.StickerGroup;
import org.lasque.tusdk.modules.view.widget.sticker.StickerLocalPackage;
import org.lasque.tusdk.video.editor.TuSdkMediaEffectData;
import org.lasque.tusdk.video.editor.TuSdkMediaStickerAudioEffectData;
import org.lasque.tusdk.video.editor.TuSdkMediaStickerEffectData;
import org.lasque.tusdk.video.editor.TuSdkTimeRange;

import com.upyun.shortvideo.editor.MovieEditorController;
import com.upyun.shortvideo.views.CompoundConfigView;
import com.upyun.shortvideo.views.ConfigViewSeekBar;
import com.upyun.shortvideo.views.editor.playview.TuSdkMovieScrollView;
import com.upyun.shortvideo.views.editor.playview.TuSdkRangeSelectionBar;
import com.upyun.shortvideo.R;
import com.upyun.shortvideo.views.ConfigViewParams;
import com.upyun.shortvideo.views.MvRecyclerAdapter;
import com.upyun.shortvideo.views.editor.TuSdkMovieScrollPlayLineView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.lasque.tusdk.core.TuSdkContext.getString;
import static org.lasque.tusdk.video.editor.TuSdkMediaEffectData.TuSdkMediaEffectDataType.TuSdkMediaEffectDataTypeAudio;
import static org.lasque.tusdk.video.editor.TuSdkMediaEffectData.TuSdkMediaEffectDataType.TuSdKMediaEffectDataTypeSticker;

/**
 * droid-sdk-video
 *
 * @author MirsFang
 * @Date 2018/9/25 15:50
 * @Copright (c) 2018 tusdk.com. All rights reserved.
 * <p>
 * MV组件
 */
public class EditorMVComponent extends EditorComponent {
    /** 主音轨音量 **/
    private float mMasterVolume;
    /** 其他音轨音量 **/
    private float mOtherVolume;

    /** BottomView */
    private View mBottomView;
    /** Mv */
    private RecyclerView mMvRecyclerView;
    /** Mv适配器 */
    private MvRecyclerAdapter mMvRecyclerAdapter;
    /** 时间轴 */
//    private LineView mTimeLineView;
    private TuSdkMovieScrollPlayLineView mPlayLineView;
    /** 播放暂停按钮 */
    private ImageView ivMvPlayBtn;

    /** MV音效资源 */
    @SuppressLint("UseSparseArrays")
    private Map<Integer, Integer> mMusicMap = new HashMap<Integer, Integer>();
    /** 声音强度调节栏 */
    private CompoundConfigView mVoiceVolumeConfigView;
    /** MV最短选择时间 **/
    private final int mMinSelectTimeUs = 1 * 1000000;
    private boolean isSelect = false;

    /**
     * 创建当前组件
     *
     * @param editorController
     */
    public EditorMVComponent(MovieEditorController editorController) {
        super(editorController);
        mComponentType = EditorComponentType.MV;

        getEditorAudioMixer().addTaskStateListener(mAudioDecoderTask);
        getVoiceVolumeConfigView();
    }

    //音频混合之后的回调
    private TuSDKAudioDecoderTaskManager.TuSDKAudioDecoderTaskStateListener mAudioDecoderTask = new TuSDKAudioDecoderTaskManager.TuSDKAudioDecoderTaskStateListener() {
        @Override
        public void onStateChanged(TuSDKAudioDecoderTaskManager.State state) {
            if (state == TuSDKAudioDecoderTaskManager.State.Complete) {
                // 启动视频预览
                startPreview();
            }
        }
    };

    @Override
    public void attach() {
        getEditorController().getVideoContentView().setClickable(false);
        // 添加底部布局
        getEditorController().getBottomView().addView(getBottomView());

        // 隐藏主界面播放按钮
        getEditorController().getPlayBtn().setVisibility(View.GONE);

        // 暂停
        pausePreview();


        // 设置播放回调
        getEditorController().getMovieEditor().getEditorPlayer().addProgressListener(mPlayerProgressListener);
        isSelect = true;
        // 应用备份数据
        if (getEditorController().getMVEffectData() != null) {
            float leftPercent = getEditorController().getMVEffectData().getAtTimeRange().getStartTimeUS() / getMovieEditor().getEditorPlayer().getTotalTimeUs();
            mPlayLineView.setLeftBarPosition(leftPercent);

            float rightPercent = getEditorController().getMVEffectData().getAtTimeRange().getEndTimeUS() / getMovieEditor().getEditorPlayer().getTotalTimeUs();
            mPlayLineView.setRightBarPosition(rightPercent);

            mSelectEffectData = getEditorController().getMVEffectData();
            mMvRecyclerAdapter.setCurrentPosition(mMementoEffectIndex);
        }else{
            mMvRecyclerAdapter.setCurrentPosition(0);
        }

        mMasterVolume = getEditorController().getMasterVolume();
        mOtherVolume = mMementoOtherVolume;
        setSeekBarProgress(0,mMasterVolume);
        setSeekBarProgress(1,mOtherVolume);
    }

    @Override
    public void onAnimationEnd() {
        super.onAnimationEnd();
        // seek放到起始位置
        getEditorPlayer().seekOutputTimeUs(0);
        if(getEditorPlayer().isReversing()) {
            mPlayLineView.seekTo(1f);
        }else {
            mPlayLineView.seekTo(0f);
        }
    }

    @Override
    public void detach() {
        pausePreview();
        isSelect = false;
        getEditorPlayer().removeProgressListener(mPlayerProgressListener);
        getEditorPlayer().seekOutputTimeUs(0);
        getEditorController().getPlayBtn().setVisibility(View.VISIBLE);
        getEditorController().getVideoContentView().setClickable(true);
    }

    @Override
    public View getHeaderView() {
        return null;
    }

    @Override
    public View getBottomView() {
        if (mBottomView == null) {
            mBottomView = initBottomView();
        }
        return mBottomView;
    }

    @Override
    public void addCoverBitmap(Bitmap bitmap) {
        getBottomView();
        mPlayLineView.addBitmap(bitmap);
    }

    /**
     * 初始化BottomView
     *
     * @return
     */
    private View initBottomView() {
        if (mBottomView == null) {
            View bottomView = LayoutInflater.from(getEditorController().getActivity()).inflate(R.layout.lsq_editor_component_mv_bottom, null);
            ImageButton ibCoverBack = bottomView.findViewById(R.id.lsq_mv_close);
            ImageButton ibCoverSure = bottomView.findViewById(R.id.lsq_mv_sure);
            ibCoverBack.setOnClickListener(onClickListener);
            ibCoverSure.setOnClickListener(onClickListener);
            mBottomView = bottomView;
            geMvPlayBtn();
            initMvRecyclerView();
            initTimeLineView();
        }
        return mBottomView;
    }

    private ImageView geMvPlayBtn() {
        if (ivMvPlayBtn == null) {
            ivMvPlayBtn = mBottomView.findViewById(R.id.lsq_mv_btn);
            ivMvPlayBtn.setOnClickListener(playOnClickListener);
        }
        return ivMvPlayBtn;
    }

    /**
     * 初始化时间轴
     */
    private void initTimeLineView() {
        if(mPlayLineView == null) {
            mPlayLineView = mBottomView.findViewById(R.id.lsq_mv_lineView);
            mPlayLineView.setType(1);
            if(getEditorPlayer().getOutputTotalTimeUS() > 0) {
                float minPercent = mMinSelectTimeUs / (float)getEditorPlayer().getOutputTotalTimeUS();
                mPlayLineView.setMinWidth(minPercent);
            }
            mPlayLineView.setSelectRangeChangedListener(new TuSdkRangeSelectionBar.OnSelectRangeChangedListener() {
                @Override
                public void onSelectRangeChanged(float leftPercent, float rightPerchent, int type) {
                    updateMediaEffectsTimeRange();
                    getEditorPlayer().seekOutputTimeUs((long) (type == 0?leftPercent:rightPerchent * getEditorPlayer().getInputTotalTimeUs()));
                }
            });

            mPlayLineView.setOnProgressChangedListener(new TuSdkMovieScrollView.OnProgressChangedListener() {
                @Override
                public void onProgressChanged(float progress, boolean isTouching) {
                    if(!isTouching)return;
                    if(isTouching){
                        getEditorPlayer().pausePreview();
                    }

                    if (getEditorPlayer().isPause()) {
                        long seekUs = (long) (getEditorPlayer().getInputTotalTimeUs() * progress);
                        getEditorPlayer().seekOutputTimeUs(seekUs);
                    }
                }

                @Override
                public void onCancelSeek() {

                }
            });
        }
    }

    /**
     * 初始化Mv
     */
    private void initMvRecyclerView() {
        mMvRecyclerView = mBottomView.findViewById(R.id.lsq_mv_recyclerView);
        mMvRecyclerView.setLayoutManager(new LinearLayoutManager(getEditorController().getActivity(), LinearLayoutManager.HORIZONTAL, false));
        mMvRecyclerAdapter = new MvRecyclerAdapter();
        mMvRecyclerAdapter.setItemClickListener(mvItemClickListener);
        mMvRecyclerView.setAdapter(mMvRecyclerAdapter);
        mMvRecyclerAdapter.setMvModeList(getMvModeList());
    }

    /**
     * 获取配音原音调节栏
     *
     * @return
     */
    private CompoundConfigView getVoiceVolumeConfigView() {
        if (mVoiceVolumeConfigView == null) {
            mVoiceVolumeConfigView = (CompoundConfigView) getEditorController().getActivity().findViewById(R.id.lsq_voice_volume_config_view);
            mVoiceVolumeConfigView.setDelegate(mMvVolumeConfigSeekbarDelegate);
            ConfigViewParams params = new ConfigViewParams();
            params.appendFloatArg(getString("originIntensity"), mMasterVolume);
            params.appendFloatArg(getString("dubbingIntensity"), mOtherVolume);
            mVoiceVolumeConfigView.setCompoundConfigView(params);
            mVoiceVolumeConfigView.showView(false);
        }

        return mVoiceVolumeConfigView;
    }

    /**
     * 获取Mv列表
     *
     * @return
     */
    private List<StickerGroup> getMvModeList() {
        /** 当前资源内的Id **/
        mMusicMap.put(1420, R.raw.lsq_audio_cat);
        mMusicMap.put(1427, R.raw.lsq_audio_crow);
        mMusicMap.put(1432, R.raw.lsq_audio_tangyuan);
        mMusicMap.put(1446, R.raw.lsq_audio_children);
        mMusicMap.put(1470, R.raw.lsq_audio_oldmovie);
        mMusicMap.put(1469, R.raw.lsq_audio_relieve);

        List<StickerGroup> groups = new ArrayList<StickerGroup>();
        List<StickerGroup> smartStickerGroups = StickerLocalPackage.shared().getSmartStickerGroups(false);

        for (StickerGroup smartStickerGroup : smartStickerGroups) {
            if (mMusicMap.containsKey((int) smartStickerGroup.groupId))
                groups.add(smartStickerGroup);
        }

        groups.add(0, new StickerGroup());
        return groups;
    }

    /** 原音配音调节栏委托事件 */
    private ConfigViewSeekBar.ConfigSeekbarDelegate mMvVolumeConfigSeekbarDelegate = new ConfigViewSeekBar.ConfigSeekbarDelegate() {

        @Override
        public void onSeekbarDataChanged(ConfigViewSeekBar seekbar, ConfigViewParams.ConfigViewArg arg) {
            if (arg.getKey().equals("originIntensity")) {
                getEditorController().getMovieEditor().getEditorMixer().setMasterAudioTrack(arg.getPercentValue());
                mMasterVolume = arg.getPercentValue();
            } else if (arg.getKey().equals("dubbingIntensity")) {
                getEditorController().getMovieEditor().getEditorMixer().setSecondAudioTrack(arg.getPercentValue());
                mOtherVolume = arg.getPercentValue();
            }
        }
    };

    /** MV列表Item点击 */
    private MvRecyclerAdapter.ItemClickListener mvItemClickListener = new MvRecyclerAdapter.ItemClickListener() {
        @Override
        public void onItemClick(final int position) {
            isSelect = false;
            if (TuSdkViewHelper.isFastDoubleClick()) return;
            getVoiceVolumeConfigView().setVisibility((position == 0) ? View.GONE : View.VISIBLE);
            mPlayLineView.setShowSelectBar(position > 0);
            ThreadHelper.post(new Runnable() {
                @Override
                public void run() {
                    // 应用 MV 特效
                    changeMvEffect(position, mMvRecyclerAdapter.getMvModeList().get(position));
                }
            });
        }
    };

    /** 点击事件 */
    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            getVoiceVolumeConfigView().setVisibility(View.GONE);
            switch (v.getId()) {
                case R.id.lsq_mv_close:
                    if (mSelectEffectData != null) {
                        getEditorEffector().removeMediaEffectsWithType(TuSdkMediaEffectDataTypeAudio);
                        getEditorEffector().removeMediaEffectsWithType(TuSdKMediaEffectDataTypeSticker);
                        mMvRecyclerAdapter.setCurrentPosition(0);
                        if (getEditorController().getMediaEffectData() != null) {
                           getEditorEffector().addMediaEffectData(getEditorController().getMediaEffectData());
                        } else {
                            mPlayLineView.setShowSelectBar(false);
                        }
                    }else{
                        if(getEditorController().getMediaEffectData() != null) {
                            getEditorEffector().removeMediaEffectsWithType(TuSdkMediaEffectDataTypeAudio);
                            getEditorEffector().removeMediaEffectsWithType(TuSdKMediaEffectDataTypeSticker);
                            getEditorEffector().addMediaEffectData(getEditorController().getMediaEffectData());
                        }
                    }

                    mSelectIndex = -1;
                    mSelectEffectData = null;
                    getEditorController().onBackEvent();
                    break;
                case R.id.lsq_mv_sure:
                    if (mSelectEffectData != null) {
                        mMementoOtherVolume = mOtherVolume;
                    }else {
                        mMementoOtherVolume = 0.5f;
                    }

                    mMementoEffectIndex = mSelectIndex;
                    getEditorController().setMusicEffectData(null);
                    getEditorController().setMVEffectData((TuSdkMediaStickerAudioEffectData) mSelectEffectData);
                    getEditorController().setMasterVolume(mMasterVolume);
                    mSelectIndex = -1;
                    mSelectEffectData = null;
                    getEditorController().onBackEvent();
                    break;
            }
        }
    };

    // 播放暂停事件
    private View.OnClickListener playOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            isSelect = false;
            if (getEditorController().getMovieEditor().getEditorPlayer().isPause()) {
                startPreview();
            } else {
                pausePreview();
            }
        }
    };

    /** 播放器进度回调 */
    private TuSdkEditorPlayer.TuSdkProgressListener mPlayerProgressListener = new TuSdkEditorPlayer.TuSdkProgressListener() {
        /**
         *
         * @param state 状态: 0播放, 1暂停
         */
        @Override
        public void onStateChanged(int state) {
            geMvPlayBtn().setImageDrawable(TuSdkContext.getDrawable(state == 0 ? R.drawable.edit_ic_pause : R.drawable.edit_ic_play));
        }

        /**
         *
         * @param playbackTimeUs 当前播放时间
         * @param totalTimeUs    总时间
         * @param percentage     百分比
         */
        @Override
        public void onProgress(long playbackTimeUs, long totalTimeUs, float percentage) {
            if(isSelect) return;
            mPlayLineView.seekTo(percentage);
        }
    };

    /**
     * 应用MV特效
     *
     * @param position
     * @param itemData
     */
    protected void changeMvEffect(int position, StickerGroup itemData) {
        if (position < 0 || mSelectIndex == position) return;

        mSelectIndex = position;


        if (position >= 0) {
            int groupId = (int) itemData.groupId;
            if (position == 0) {
                mSelectEffectData = null;
                getEditorController().getMovieEditor().getEditorEffector().removeMediaEffectsWithType(TuSdkMediaEffectDataTypeAudio);
                getEditorController().getMovieEditor().getEditorEffector().removeMediaEffectsWithType(TuSdKMediaEffectDataTypeSticker);
                getEditorController().getMovieEditor().getEditorMixer().clearAllAudioData();
            }
            if (mMusicMap != null && mMusicMap.containsKey(groupId)) {
                //带音效的MV
                Uri uri = Uri.parse("android.resource://" + getEditorController().getActivity().getPackageName() + "/" + mMusicMap.get(groupId));
                TuSdkMediaStickerAudioEffectData stickerAudioEffectDat = new TuSdkMediaStickerAudioEffectData(new TuSdkMediaDataSource(getEditorController().getActivity(), uri), itemData);
                stickerAudioEffectDat.setAtTimeRange(TuSdkTimeRange.makeRange(0, Float.MAX_VALUE));
                stickerAudioEffectDat.getMediaAudioEffectData().getAudioEntry().setLooping(true);
                getEditorController().getMovieEditor().getEditorEffector().addMediaEffectData(stickerAudioEffectDat);
                mSelectEffectData = stickerAudioEffectDat;
            } else {
                //纯贴纸的MV
                TuSdkMediaStickerEffectData stickerEffectData = new TuSdkMediaStickerEffectData(itemData);
                stickerEffectData.setAtTimeRange(TuSdkTimeRange.makeRange(0, Float.MAX_VALUE));
                getEditorController().getMovieEditor().getEditorEffector().addMediaEffectData(stickerEffectData);
                mSelectEffectData = stickerEffectData;
            }
        }
    }

    /**
     * 开始播放视频
     */
    private void startPreview() {
        //循环播放视频
        if (getEditorController().getMovieEditor() == null) return;
        // 添加设置的特效信息
        updateMediaEffectsTimeRange();
        getEditorController().getMovieEditor().getEditorPlayer().startPreview();
        geMvPlayBtn().setImageBitmap(BitmapFactory.decodeResource(getEditorController().getActivity().getResources(), R.drawable.edit_ic_pause));

        // 隐藏主界面播放按钮
        getEditorController().getPlayBtn().setVisibility(View.GONE);
    }

    /**
     * 暂停播放视频
     */
    private void pausePreview() {
        //循环播放视频
        if (getEditorController().getMovieEditor() == null) return;
        getEditorController().getMovieEditor().getEditorPlayer().pausePreview();
        geMvPlayBtn().setImageBitmap(BitmapFactory.decodeResource(getEditorController().getActivity().getResources(), R.drawable.edit_ic_play));
    }

    /**
     * 更新特效播放时间
     */
    private void updateMediaEffectsTimeRange() {
        TuSDKVideoInfo videoInfo = getEditorController().getMovieEditor().getEditorTransCoder().getOutputVideoInfo();
        if (videoInfo == null) return;

        long startTimeUs = (long) (mPlayLineView.getLeftBarPercent() * videoInfo.durationTimeUs);
        long endTimeUs = (long) (mPlayLineView.getRightBarPercent() * videoInfo.durationTimeUs);

        if(endTimeUs <= startTimeUs)return;

        TuSdkTimeRange timeRange = TuSdkTimeRange.makeTimeUsRange(startTimeUs, endTimeUs);

        // 设置音频特效播放区间
        if (getEditorController().getMovieEditor().getEditorEffector().mediaEffectsWithType(TuSdkMediaEffectDataTypeAudio) != null) {
            for (TuSdkMediaEffectData mediaEffectData : getEditorController().getMovieEditor().getEditorEffector().mediaEffectsWithType(TuSdkMediaEffectDataTypeAudio)) {
                mediaEffectData.setAtTimeRange(timeRange);
                if (mSelectEffectData != null && mSelectEffectData instanceof TuSdkMediaStickerAudioEffectData && ((TuSdkMediaStickerAudioEffectData) mSelectEffectData).getMediaAudioEffectData() == mediaEffectData) {
                    mSelectEffectData.setAtTimeRange(timeRange);
                }
            }
        }
        // 设置贴纸特效播放区间
        if (getEditorController().getMovieEditor().getEditorEffector().mediaEffectsWithType(TuSdKMediaEffectDataTypeSticker) != null) {
            for (TuSdkMediaEffectData mediaEffectData : getEditorController().getMovieEditor().getEditorEffector().mediaEffectsWithType(TuSdKMediaEffectDataTypeSticker)) {
                mediaEffectData.setAtTimeRange(timeRange);
                if (mSelectEffectData != null && mSelectEffectData instanceof TuSdkMediaStickerAudioEffectData && ((TuSdkMediaStickerAudioEffectData) mSelectEffectData).getMediaStickerEffectData() == mediaEffectData) {
                    mSelectEffectData.setAtTimeRange(timeRange);
                }
            }
        }
    }

    private void setSeekBarProgress(int index, float progress) {
        mVoiceVolumeConfigView.getSeekBarList().get(index).setProgress(progress);
    }
}
