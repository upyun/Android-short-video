package org.lasque.tusdkvideodemo.editor.component;

import android.graphics.Bitmap;
import android.net.Uri;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;

import org.lasque.tusdk.core.TuSdkContext;
import org.lasque.tusdk.core.audio.TuSdkAudioRecordCuter;
import org.lasque.tusdk.core.decoder.TuSDKAudioDecoderTaskManager;
import org.lasque.tusdk.core.seles.sources.TuSdkEditorPlayer;
import org.lasque.tusdk.core.struct.TuSdkMediaDataSource;
import org.lasque.tusdk.core.utils.ThreadHelper;
import org.lasque.tusdk.core.view.TuSdkViewHelper;
import org.lasque.tusdk.video.editor.TuSdkMediaAudioEffectData;
import org.lasque.tusdk.video.editor.TuSdkMediaEffectData;
import org.lasque.tusdk.video.editor.TuSdkTimeRange;
import com.upyun.shortvideo.R;
import org.lasque.tusdkvideodemo.editor.MovieEditorController;
import org.lasque.tusdkvideodemo.utils.Constants;
import org.lasque.tusdkvideodemo.views.CompoundConfigView;
import org.lasque.tusdkvideodemo.views.ConfigViewParams;
import org.lasque.tusdkvideodemo.views.ConfigViewSeekBar;
import org.lasque.tusdkvideodemo.views.MusicRecyclerAdapter;
import org.lasque.tusdkvideodemo.views.editor.AudioRecordLayout;

import java.io.File;
import java.util.Arrays;

import static android.view.View.VISIBLE;
import static org.lasque.tusdk.core.TuSdkContext.getPackageName;
import static org.lasque.tusdk.video.editor.TuSdkMediaEffectData.TuSdkMediaEffectDataType.TuSdkMediaEffectDataTypeAudio;
import static org.lasque.tusdk.video.editor.TuSdkMediaEffectData.TuSdkMediaEffectDataType.TuSdkMediaEffectDataTypeSticker;

/**
 * droid-sdk-video
 *
 * @author MirsFang
 * @Date 2018/9/25 15:52
 * @Copright (c) 2018 tusdk.com. All rights reserved.
 * <p>
 * 音乐组件
 */
public class EditorMusicComponent extends EditorComponent {
    /** 主音轨音量 */
    private float mMasterVolume;
    /** 次音轨音量 */
    private float mOtherVolume;

    /** 底部布局 */
    private View mBottomView;
    /** 音乐列表 */
    private RecyclerView mMusicRecycle;
    /** 返回 */
    private ImageButton mBackBtn;
    /** 播放 */
    private ImageButton mPlayBtn;
    /** 音乐适配器 */
    private MusicRecyclerAdapter mMusicAdapter;
    /** 录音布局 */
    private AudioRecordLayout mAudioRecordView;
    /** 音量调节器 */
    private CompoundConfigView mVolumeConfigView;


    /** 音频混合之后的回调 **/
    private TuSDKAudioDecoderTaskManager.TuSDKAudioDecoderTaskStateListener mAudioDecoderTask = new TuSDKAudioDecoderTaskManager.TuSDKAudioDecoderTaskStateListener() {
        @Override
        public void onStateChanged(TuSDKAudioDecoderTaskManager.State state) {
            if (state == TuSDKAudioDecoderTaskManager.State.Complete) {
                // 启动视频预览
                if(!getEditorPlayer().isPause()) getEditorPlayer().pausePreview();
                getEditorPlayer().seekOutputTimeUs(0);
                startPreview();
            }
        }
    };

    /** 保存录音回调 **/
    private TuSdkAudioRecordCuter.OnAudioRecordCuterListener mCuterListener = new TuSdkAudioRecordCuter.OnAudioRecordCuterListener() {
        @Override
        public void onProgressChanged(float percent, long currentTimeUS, long totalTimeUS) {

        }

        @Override
        public void onComplete(final File outputFile) {
            ThreadHelper.post(new Runnable() {
                @Override
                public void run() {
                    getEditorPlayer().setVideoSoundVolume(1);
                    getVolumeConfigView().showView(true);
                    getAudioRecordView().gone();
                    applyAudioEffect(Uri.fromFile(outputFile),false);
                }
            });
        }
    };

    /** 音量调节 */
    private ConfigViewSeekBar.ConfigSeekbarDelegate mSeekBarChangeDelegateLisntener = new ConfigViewSeekBar.ConfigSeekbarDelegate() {
        @Override
        public void onSeekbarDataChanged(ConfigViewSeekBar seekbar, ConfigViewParams.ConfigViewArg arg) {
            if (arg.getKey().equals("origin")) {
                getEditorAudioMixer().setMasterAudioTrack(arg.getPercentValue());
                mMasterVolume = arg.getPercentValue();
            } else if (arg.getKey().equals("dubbing")) {
                getEditorAudioMixer().setSecondAudioTrack(arg.getPercentValue());
                mOtherVolume = arg.getPercentValue();
            }
        }
    };

    private TuSdkEditorPlayer.TuSdkProgressListener mPlayProgress = new TuSdkEditorPlayer.TuSdkProgressListener() {
        @Override
        public void onStateChanged(int state) {
            // 0为播放 1为暂停
        }

        @Override
        public void onProgress(long playbackTimeUs, long totalTimeUs, float percentage) {
            if (!getEditorPlayer().isReversing()){
                if(percentage == 1 && getAudioRecordView().getVisibility() == View.GONE){
                    getEditorController().getPlayBtn().setVisibility(VISIBLE);
                }
            } else{
                if (percentage == 0 && getAudioRecordView().getVisibility() == View.GONE){
                    getEditorController().getPlayBtn().setVisibility(VISIBLE);
                }
            }
        }
    };

    /**
     * 创建当前组件
     *
     * @param editorController
     */
    public EditorMusicComponent(MovieEditorController editorController) {
        super(editorController);
        mComponentType = EditorComponentType.Music;
        mMusicAdapter = new MusicRecyclerAdapter();
        getEditorAudioMixer().addTaskStateListener(mAudioDecoderTask);
        getVolumeConfigView();
    }

    /**
     * 获取音量调节栏
     */
    public CompoundConfigView getVolumeConfigView(){
        if(mVolumeConfigView == null) {
            mVolumeConfigView = getEditorController().getActivity().findViewById(R.id.lsq_voice_volume_config_view);
            mVolumeConfigView.setDelegate(mSeekBarChangeDelegateLisntener);

            ConfigViewParams params = new ConfigViewParams();
            params.appendFloatArg(TuSdkContext.getString("origin"), mMasterVolume);
            params.appendFloatArg(TuSdkContext.getString("dubbing"), mOtherVolume);
            mVolumeConfigView.setCompoundConfigView(params);
            mVolumeConfigView.showView(false);
        }
        return mVolumeConfigView;
    }


    @Override
    public void attach() {
        getEditorController().getBottomView().addView(getBottomView());
        getEditorPlayer().addProgressListener(mPlayProgress);
        startPreview();
        getEditorController().getVideoContentView().setClickable(false);
        getEditorController().getPlayBtn().setClickable(true);
        getEditorController().getPlayBtn().setOnClickListener(mOnClickListener);

        if(getEditorController().getMusicEffectData() != null){
            mSelectEffectData = getEditorController().getMusicEffectData();
            mVolumeConfigView.showView(true);
            mMusicAdapter.setCurrentPosition(mMementoEffectIndex);
        }else{
            mMusicAdapter.setCurrentPosition(0);
        }

        mMasterVolume = getEditorController().getMasterVolume();
        mOtherVolume = mMementoOtherVolume;
        setSeekBarProgress(0,mMasterVolume);
        setSeekBarProgress(1,mOtherVolume);
    }

    @Override
    public void detach() {
        mVolumeConfigView.showView(false);
        getEditorPlayer().removeProgressListener(mPlayProgress);
        getEditorController().getVideoContentView().setClickable(true);
        getEditorController().getPlayBtn().setClickable(false);
        getAudioRecordView().gone();
    }

    @Override
    public View getHeaderView() {
        return null;
    }

    @Override
    public View getBottomView() {
        if (mBottomView == null) {
            mBottomView = LayoutInflater.from(getEditorController().getActivity()).inflate(R.layout.lsq_editor_component_music_bottom, null);
            mMusicRecycle = mBottomView.findViewById(R.id.lsq_music_list_view);
            mMusicRecycle.setLayoutManager(new LinearLayoutManager(getEditorController().getActivity(), LinearLayoutManager.HORIZONTAL, false));
            mMusicAdapter.setMusicList(Arrays.asList(Constants.AUDIO_EFFECTS_CODES));
            mMusicAdapter.setItemCilckListener(mOnItemClickListener);
            mMusicRecycle.setAdapter(mMusicAdapter);

            mBackBtn = mBottomView.findViewById(R.id.lsq_music_close);
            mBackBtn.setOnClickListener(mOnClickListener);

            mPlayBtn = mBottomView.findViewById(R.id.lsq_music_sure);
            mPlayBtn.setOnClickListener(mOnClickListener);
        }
        return mBottomView;
    }

    @Override
    public void addCoverBitmap(Bitmap bitmap) {

    }

    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.lsq_music_close:
                    //点击关闭按钮 回复上次一次应用的特效
                    if(mSelectEffectData == null){
                        if(getEditorController().getMediaEffectData() != null) {
                            applyAudioEffect(getEditorController().getMediaEffectData());
                        }
                    } else {
                        mMusicAdapter.setCurrentPosition(0);
                        if(getEditorController().getMediaEffectData() != null){
                            getEditorEffector().removeMediaEffectData(mSelectEffectData);
                            applyAudioEffect(getEditorController().getMediaEffectData());
                        }else
                            getEditorEffector().removeMediaEffectData(mSelectEffectData);
                    }
                    mSelectIndex = 0;
                    mSelectEffectData = null;
                    getEditorController().onBackEvent();
                    break;
                case R.id.lsq_music_sure:
                    //应用特效，添加到备忘中
                    if(mSelectEffectData != null) {
                        mMementoOtherVolume = mOtherVolume;
                    }else {
                        mMementoOtherVolume = 0.5f;
                    }

                    mMementoEffectIndex = mSelectIndex;
                    getEditorController().setMusicEffectData((TuSdkMediaAudioEffectData) mSelectEffectData);
                    getEditorController().setMVEffectData(null);
                    //这是主音轨的音量
                    getEditorController().setMasterVolume(mMasterVolume);
                    mSelectIndex = 0;
                    mSelectEffectData = null;
                    getEditorController().onBackEvent();
                    break;
                case R.id.lsq_play_btn:
                    if(getEditorPlayer().isPause()){
                        startPreview();
                    }
                    break;
            }
        }
    };

    private MusicRecyclerAdapter.ItemClickListener mOnItemClickListener = new MusicRecyclerAdapter.ItemClickListener() {
        @Override
        public void onItemClick(final String musicCode, final int position) {
            if (TuSdkViewHelper.isFastDoubleClick()) return;
            // 停止预览
            if(position != 0)
            getEditorPlayer().seekInputTimeUs(0);

            //进入录音页面
            if (position == 1) {
                getEditorPlayer().setVideoSoundVolume(0);
                getAudioRecordView().show();
                getVolumeConfigView().setVisibility(View.GONE);
                getEditorController().getPlayBtn().setVisibility(View.GONE);
                return;
            }

            ThreadHelper.post(new Runnable() {
                @Override
                public void run() {
                    // 应用配音特效
                    changeAudioEffect(musicCode, position);
                }
            });
        }
    };


    /**
     * 应用背景音乐特效
     *
     * @param position
     */
    protected void changeAudioEffect(String musicCode, int position) {
        mSelectIndex = position;
        if (position == 0) {
            // 取消所有音特效
            getEditorEffector().removeMediaEffectsWithType(TuSdkMediaEffectDataTypeSticker);
            getEditorEffector().removeMediaEffectsWithType(TuSdkMediaEffectDataTypeAudio);
            getEditorAudioMixer().clearAllAudioData();
            mSelectEffectData = null;
            mVolumeConfigView.showView(false);
        } else if (position > 0) {
            Uri uri = Uri.parse("android.resource://" + getPackageName() + "/" + TuSdkContext.getRawResId(musicCode));
            applyAudioEffect(uri,true);
            setSeekBarProgress(0,mMasterVolume);
            setSeekBarProgress(1,mOtherVolume);
            mVolumeConfigView.showView(true);
        }
    }

    /**
     * 设置配音音效
     *
     * @param audioPathUri
     */
    private void applyAudioEffect(Uri audioPathUri,boolean isLooping) {
        if (audioPathUri == null) return;

        TuSdkMediaAudioEffectData audioEffectData = new TuSdkMediaAudioEffectData(new TuSdkMediaDataSource(getEditorController().getActivity(), audioPathUri));
        audioEffectData.setAtTimeRange(TuSdkTimeRange.makeTimeUsRange(0, Long.MAX_VALUE));
        audioEffectData.getAudioEntry().setLooping(isLooping);
        //添加音效
        getEditorEffector().addMediaEffectData(audioEffectData);
        mSelectEffectData = audioEffectData;
    }

    /**
     * 应用已有数据
     * @param audioEffectData
     */
    private void applyAudioEffect(TuSdkMediaEffectData audioEffectData) {
        getEditorEffector().addMediaEffectData(audioEffectData);
    }


    /** 获取录音View **/
    public AudioRecordLayout getAudioRecordView() {
        if (mAudioRecordView == null) {
            mAudioRecordView = getEditorController().getActivity().findViewById(R.id.lsq_audio_record);
            mAudioRecordView.initAudioRecord(getEditorController().getMovieEditor());
            mAudioRecordView.setOnRecordCutListener(mCuterListener);
            mAudioRecordView.setOperationListener(new AudioRecordLayout.OnRecordOperationListener() {
                @Override
                public void onCancel() {
                    if(getEditorPlayer().isPause()){
                        getEditorController().getPlayBtn().setVisibility(VISIBLE);
                    }
                    mMusicAdapter.setCurrentPosition(0);
                    ThreadHelper.post(new Runnable() {
                        @Override
                        public void run() {
                            // 应用配音特效
                            changeAudioEffect("none", 0);
                        }
                    });
                }
            });
        }
        return mAudioRecordView;
    }

    /**
     * 设置进度
     * @param index
     * @param progress
     */
    private void setSeekBarProgress(int index, float progress) {
        mVolumeConfigView.getSeekBarList().get(index).setProgress(progress);
    }

    /**
     * 开始预览
     */
    private void startPreview(){
        getEditorPlayer().startPreview();
        getEditorController().getPlayBtn().setVisibility(View.GONE);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getAudioRecordView().gone();
    }
}
