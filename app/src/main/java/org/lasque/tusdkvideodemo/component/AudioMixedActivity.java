package org.lasque.tusdkvideodemo.component;

import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.lasque.tusdk.api.audio.player.TuSDKMutiAudioPlayer;
import org.lasque.tusdk.api.audio.preproc.mixer.TuSDKAudioEntry;
import org.lasque.tusdk.api.audio.preproc.mixer.TuSDKAudioMixer;
import org.lasque.tusdk.api.audio.preproc.mixer.TuSDKAverageAudioMixer;
import org.lasque.tusdk.core.TuSdk;
import org.lasque.tusdk.core.TuSdkContext;
import org.lasque.tusdk.core.decoder.TuSDKAudioInfo;
import org.lasque.tusdk.core.encoder.audio.TuSDKAACAudioFileEncoder;
import org.lasque.tusdk.core.encoder.audio.TuSDKAudioEncoderSetting;
import org.lasque.tusdk.core.utils.StringHelper;
import org.lasque.tusdk.core.utils.image.AlbumHelper;
import org.lasque.tusdk.core.view.widget.button.TuSdkNavigatorBackButton;
import com.upyun.shortvideo.R;
import org.lasque.tusdkvideodemo.ScreenAdapterActivity;
import org.lasque.tusdkvideodemo.views.CompoundConfigView;
import org.lasque.tusdkvideodemo.views.ConfigViewParams;
import org.lasque.tusdkvideodemo.views.ConfigViewSeekBar;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static org.lasque.tusdk.api.audio.preproc.mixer.TuSDKAudioMixer.State.Cancelled;
import static org.lasque.tusdk.api.audio.preproc.mixer.TuSDKAudioMixer.State.Complete;
import static org.lasque.tusdk.api.audio.preproc.mixer.TuSDKAudioMixer.State.Decoding;
import static org.lasque.tusdk.api.audio.preproc.mixer.TuSDKAudioMixer.State.Idle;
import static org.lasque.tusdk.api.audio.preproc.mixer.TuSDKAudioMixer.State.Mixing;

/**
 * droid-sdk-video
 *
 * @author MirsFang
 * @Date 2018/9/12 16:45
 * @Copright (c) 2018 tusdk.com. All rights reserved.
 * <p>
 * 多音轨混合
 */
public class AudioMixedActivity extends ScreenAdapterActivity {
    //音频文件列表
    private int[] mAudioEntries = new int[]{R.raw.lsq_audio_cat, R.raw.lsq_audio_children, R.raw.lsq_audio_tangyuan};

    //进度控制视图
    private CompoundConfigView mVoiceConfigView;
    //返回按钮
    private TuSdkNavigatorBackButton mBackBtn;
    //开始混音按钮
    private Button mAudioMixerButton;
    //删除按钮
    private Button mDeleteMixingButton;
    //播放按钮
    private Button mPlayMixingButton;
    //暂停按钮
    private Button mPauseMixingButton;


    /** 音频混合对象 */
    private TuSDKAverageAudioMixer mAudioMixer;
    /** AAC音频文件编码器 */
    private TuSDKAACAudioFileEncoder mAACFileEncoder;
    /** 多音轨播放器 */
    private TuSDKMutiAudioPlayer mMutiAudioPlayer;
    /** 混合的音频数据 */
    private List<TuSDKAudioEntry> mAudioEntryList;
    /** 混合后输出的文件地址 */
    private String mMixedAudioPath;

    /**
     * 原音配音调节栏委托事件
     */
    private ConfigViewSeekBar.ConfigSeekbarDelegate mFilterConfigSeekbarDelegate = new ConfigViewSeekBar.ConfigSeekbarDelegate() {

        @Override
        public void onSeekbarDataChanged(ConfigViewSeekBar seekbar, ConfigViewParams.ConfigViewArg arg) {
            if (arg.getKey().equals("origin"))
                setSeekBarProgress(0, arg.getPercentValue());
            else if (arg.getKey().equals("dubbingone"))
                setSeekBarProgress(1, arg.getPercentValue());
            else if (arg.getKey().equals("dubbingtwo"))
                setSeekBarProgress(2, arg.getPercentValue());
        }
    };


    /**
     * 多音轨混合播放器Delegate
     */
    private TuSDKMutiAudioPlayer.TuSDKMutiAudioPlayerDelegate mMutiAudioPlayerDelegate = new TuSDKMutiAudioPlayer.TuSDKMutiAudioPlayerDelegate() {
        /**
         * 播放器状态改变事件
         */
        @Override
        public void onStateChanged(TuSDKMutiAudioPlayer.State state) {

            if (state == TuSDKMutiAudioPlayer.State.PrePared)
                startMutiAudioPlayer();
        }
    };


    /**
     * 音频混合Delegate
     */
    private TuSDKAudioMixer.OnAudioMixerDelegate mAudioMixerDelegate = new TuSDKAudioMixer.OnAudioMixerDelegate() {
        /**
         * 混合状态改变事件
         */
        @Override
        public void onStateChanged(TuSDKAudioMixer.State state) {
            if (state == TuSDKAudioMixer.State.Complete) {
                // 停止AAC编码器
                mAACFileEncoder.stop();

                TuSdk.messageHub().showSuccess(AudioMixedActivity.this, "混合完成");

            } else if (state == Decoding || state == Mixing) {
                TuSdk.messageHub().setStatus(AudioMixedActivity.this, "混合中");

            } else if (state == TuSDKAudioMixer.State.Cancelled) {
                delMixedFile();
            }
        }

        /**
         * 当前解析到主背景音乐信息时回调该方法，其他音乐将参考该信息进行混合
         */
        @Override
        public void onReayTrunkTrackInfo(TuSDKAudioInfo rawInfo) {
        }

        @Override
        public void onMixingError(int errorCode) {
            TuSdk.messageHub().showError(AudioMixedActivity.this, "混合失败");
        }

        /**
         * 混合后的音频数据（未经编码）
         */
        @Override
        public void onMixed(byte[] mixedBytes) {
            // 编码音频数据
            mAACFileEncoder.queueAudio(mixedBytes);
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio_mixed);
        initView();
    }

    private void initView() {
        mBackBtn = findViewById(R.id.lsq_backButton);
        mBackBtn.setOnClickListener(mOnClickListener);

        TextView titleView = findViewById(R.id.lsq_titleView);
        titleView.setText(TuSdkContext.getString("lsq_audio_mixer_text"));

        initMutiAudioPlayer();
        initAudioMixer();
        initVoiceConfigView();


        mAudioMixerButton = (Button) findViewById(R.id.lsq_audio_mixer_btn);
        mDeleteMixingButton = (Button) findViewById(R.id.lsq_delete_mixing_btn);
        mPlayMixingButton = (Button) findViewById(R.id.lsq_play_mixing_btn);
        mPauseMixingButton = (Button) findViewById(R.id.lsq_pause_mixing_btn);
        mAudioMixerButton.setOnClickListener(mOnClickListener);
        mDeleteMixingButton.setOnClickListener(mOnClickListener);
        mPlayMixingButton.setOnClickListener(mOnClickListener);
        mPauseMixingButton.setOnClickListener(mOnClickListener);
    }

    /**
     * 初始化多音轨播放器
     */
    private void initMutiAudioPlayer() {
        mMutiAudioPlayer = new TuSDKMutiAudioPlayer();
        mMutiAudioPlayer.setDelegate(mMutiAudioPlayerDelegate);
        mMutiAudioPlayer.setLooping(true);
        mMutiAudioPlayer.asyncPrepare(getAudioEntryList());
    }

    /**
     * 初始化多音轨混合对象
     */
    private void initAudioMixer() {
        mAudioMixer = new TuSDKAverageAudioMixer();
        mAudioMixer.setOnAudioMixDelegate(mAudioMixerDelegate);
    }

    /**
     * 初始化控制拦
     */
    private void initVoiceConfigView() {
        if (getVoiceConfigView() != null) {
            ConfigViewParams params = new ConfigViewParams();
            params.appendFloatArg(TuSdkContext.getString("origin"), 1.0f);
            params.appendFloatArg(TuSdkContext.getString("dubbingone"), 1.0f);
            params.appendFloatArg(TuSdkContext.getString("dubbingtwo"), 1.0f);
            getVoiceConfigView().setSeekBarHeight(TuSdkContext.dip2px(50));
            getVoiceConfigView().setSeekBarTitleWidh(TuSdkContext.dip2px(50));
            getVoiceConfigView().setDelegate(mFilterConfigSeekbarDelegate);
            getVoiceConfigView().setCompoundConfigView(params);

            for (int i = 0; i < getVoiceConfigView().getSeekBarList().size(); i++) {
                // 初始化音量调节SeeKBar
                this.setSeekBarProgress(i, 0.0f);
            }
        }
    }

    /**
     * 获取控制器
     *
     * @return
     */
    private CompoundConfigView getVoiceConfigView() {
        if (mVoiceConfigView == null) {
            mVoiceConfigView = findViewById(R.id.lsq_voice_volume_config_view);
        }

        return mVoiceConfigView;
    }

    /**
     * 点击事件
     */
    private View.OnClickListener mOnClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            if (v == mBackBtn) {
                finish();
            } else if (v == mAudioMixerButton) {
                startAudioMixer();
            } else if (v == mDeleteMixingButton) {
                delMixedFile();
            } else if (v == mPlayMixingButton) {
                if (mMixedAudioPath == null || getMixedAudioEntry(mMixedAudioPath) == null) return;
                mMutiAudioPlayer.asyncPrepare(getMixedAudioEntry(mMixedAudioPath));
            } else if (v == mPauseMixingButton) {
                cancelAudioMixer();
                stopMutiAudioPlayer();
            }
        }
    };


    private void setSeekBarProgress(int index, float progress) {
        getVoiceConfigView().getSeekBarList().get(index).setProgress(progress);

        /**
         * 设置音频音量
         */
        getAudioEntryList().get(index).setVolume(progress);
        if (mMutiAudioPlayer.getState() == TuSDKMutiAudioPlayer.State.Idle)
            mMutiAudioPlayer.asyncPrepare(getAudioEntryList());
    }

    /**
     * 准备音频数据
     */
    private List<TuSDKAudioEntry> getAudioEntryList() {
        if (mAudioEntryList != null && mAudioEntryList.size() > 0) return mAudioEntryList;

        mAudioEntryList = new ArrayList<TuSDKAudioEntry>(3);

        for (int i = 0; i < mAudioEntries.length; i++) {
            Uri uri = Uri.parse("android.resource://" + getPackageName() + "/" + mAudioEntries[i]);

            TuSDKAudioEntry audioEntry = new TuSDKAudioEntry(uri);
            audioEntry.setTrunk(i == 0);

            mAudioEntryList.add(audioEntry);
        }

        return mAudioEntryList;
    }


    /**
     * 开始播放音频数据
     */
    private void startMutiAudioPlayer() {
        mMutiAudioPlayer.start();
    }

    /**
     * 停止音频播放器
     */
    private void stopMutiAudioPlayer() {
        mMutiAudioPlayer.stop();
    }

    /**
     * 启动音频混合
     */
    private void startAudioMixer() {
        if(!(mAudioMixer.getState() == Idle || mAudioMixer.getState() == Complete || mAudioMixer.getState() == Cancelled)) return;

        stopMutiAudioPlayer();
        /** AAC 音频文件编码器，可将混合的音频数据编码为AAC文件 */
        mAACFileEncoder = new TuSDKAACAudioFileEncoder();

        TuSDKAudioEntry audioEntry = getAudioEntryList().get(0);
        TuSDKAudioEncoderSetting audioEncoderSetting = TuSDKAudioEncoderSetting.defaultEncoderSetting();
        audioEncoderSetting.mediacodecAACChannelCount = audioEntry.getRawInfo().channel;
        audioEncoderSetting.channelConfig = audioEntry.getRawInfo().channelConfig;
        audioEncoderSetting.audioFormat = audioEntry.getRawInfo().audioFormat;
        audioEncoderSetting.sampleRate = audioEntry.getRawInfo().sampleRate;

        // 初始化音频编码器
        mAACFileEncoder.initEncoder(audioEncoderSetting);
        mAACFileEncoder.setOutputFilePath(getMixedAudioPath());
        mAACFileEncoder.start();

        mAudioMixer.mixAudios(getAudioEntryList());
    }

    /**
     * 获取混合路径
     *
     * @return
     */
    private String getMixedAudioPath() {
        mMixedAudioPath = new File(AlbumHelper.getAblumPath(), String.format("lsq_%s.aac", StringHelper.timeStampString())).getPath();
        return mMixedAudioPath;
    }


    /**
     * 删除混合的音频
     */
    private void delMixedFile() {
        if (mMixedAudioPath == null) return;
        stopMutiAudioPlayer();
        new File(mMixedAudioPath).delete();
        if (!new File(mMixedAudioPath).exists()) {
            String hintMsg = getResources().getString(R.string.lsq_audio_mixer_delete_success);
            TuSdk.messageHub().showToast(this, hintMsg);
            for (int i = 0; i < getVoiceConfigView().getSeekBarList().size(); i++) {
                this.setSeekBarProgress(i, 0.0f);
            }
        } else {
            String hintMsg = getResources().getString(R.string.lsq_audio_mixer_delete_failed);
            TuSdk.messageHub().showToast(this, hintMsg);
        }
    }

    /**
     * 取消音频混合
     */
    private void cancelAudioMixer() {
        mAudioMixer.cancel();
    }


    private List<TuSDKAudioEntry> getMixedAudioEntry(String mixedAudioPath) {
        if (!new File(mixedAudioPath).exists()) return null;
        List<TuSDKAudioEntry> audioEntryList = new ArrayList<TuSDKAudioEntry>();
        TuSDKAudioEntry audioEntry = new TuSDKAudioEntry(mixedAudioPath);
        audioEntryList.add(audioEntry);
        return audioEntryList;
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (mMutiAudioPlayer != null)
            mMutiAudioPlayer.stop();
    }


}
