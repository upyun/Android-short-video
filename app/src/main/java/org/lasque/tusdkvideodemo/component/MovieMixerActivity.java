package org.lasque.tusdkvideodemo.component;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.lasque.tusdk.api.audio.player.TuSDKMutiAudioPlayer;
import org.lasque.tusdk.api.audio.preproc.mixer.TuSDKAudioEntry;
import org.lasque.tusdk.api.movie.player.TuSDKMoviePlayer;
import org.lasque.tusdk.api.movie.preproc.mixer.TuSDKMP4MovieMixer;
import org.lasque.tusdk.core.TuSdk;
import org.lasque.tusdk.core.TuSdkContext;
import org.lasque.tusdk.core.common.TuSDKMediaDataSource;
import org.lasque.tusdk.core.utils.StringHelper;
import org.lasque.tusdk.core.utils.TLog;
import org.lasque.tusdk.core.utils.image.AlbumHelper;
import org.lasque.tusdk.core.video.TuSDKVideoResult;
import org.lasque.tusdk.core.view.widget.button.TuSdkNavigatorBackButton;
import com.upyun.shortvideo.R;
import org.lasque.tusdkvideodemo.ScreenAdapterActivity;
import org.lasque.tusdkvideodemo.views.CompoundConfigView;
import org.lasque.tusdkvideodemo.views.ConfigViewParams;
import org.lasque.tusdkvideodemo.views.ConfigViewSeekBar;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * droid-sdk-video
 *
 * @author MirsFang
 * @Date 2018/9/13 10:51
 * @Copright (c) 2018 tusdk.com. All rights reserved.
 * <p>
 * 音视频混合
 */
public class MovieMixerActivity extends ScreenAdapterActivity implements TuSDKMP4MovieMixer.OnMP4MovieMixerDelegate {
    //资源数组
    private final static int[] AUIDOENTRY_RESIDARRAY = new int[]{R.raw.lsq_audio_cat, R.raw.lsq_audio_children};

    //进度控制视图
    private CompoundConfigView mVoiceConfigView;
    //返回按钮
    private TuSdkNavigatorBackButton mBackBtn;
    /** 视频预览界面 */
    private SurfaceView mMoviePreviewLayout;
    /** 视频合成按钮 */
    private Button mMovieMixerButton;

    /** MP4视频格式混合 */
    private TuSDKMP4MovieMixer mMP4MovieMixer;
    /** 视频播放器 */
    private TuSDKMoviePlayer mMoviePlayer;
    /** 多音轨播放器 */
    private TuSDKMutiAudioPlayer mMutiAudioPlayer;

    /** 混合的音频数据 */
    private List<TuSDKAudioEntry> mAudioEntryList = new ArrayList<TuSDKAudioEntry>();
    /** 输入视频路径 */
    private String mInputPath = "";
    /** 混合后的视频地址 */
    private String mMixedVideoPath;

    private boolean isFirst = true;

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

    /** 视频播放器回调 */
    private TuSDKMoviePlayer.TuSDKMoviePlayerDelegate mMoviePlayerDelegate = new TuSDKMoviePlayer.TuSDKMoviePlayerDelegate() {
        @Override
        public void onStateChanged(TuSDKMoviePlayer.PlayerState state) {

            if (state == TuSDKMoviePlayer.PlayerState.INITIALIZED) {
                mMoviePlayer.setVolume(0.0f);
                prepareAudio();
            }
        }

        @Override
        public void onVideSizeChanged(MediaPlayer mp, int width, int height) {
        }

        @Override
        public void onProgress(int progress) {
        }

        @Override
        public void onSeekComplete() {
        }

        @Override
        public void onCompletion() {

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
            if (state == TuSDKMutiAudioPlayer.State.PrePared) {
                startMutiAudioPlayer();
                startMoviePlayer();
            }

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_mixer);
        mInputPath = "android.resource://" + getPackageName() + "/" + R.raw.tusdk_sample_video;
        initView();
        getAudioEntryList();
        initMediaPlayer();
        initVoiceConfigView();

    }

    /** 初始化视频播放器 */
    private void initMediaPlayer() {
        mMoviePlayer = TuSDKMoviePlayer.createMoviePlayer();
        mMoviePlayer.setLooping(true);
        mMoviePlayer.initVideoPlayer(this, getVideoPath(), getPreviewLayout());
        mMoviePlayer.setDelegate(mMoviePlayerDelegate);
    }

    /** 获取视频地址 */
    private Uri getVideoPath() {
        Uri videoPathUri = Uri.parse(mInputPath);
        return videoPathUri;
    }

    /** 获取预览视图 */
    private SurfaceView getPreviewLayout() {
        if (mMoviePreviewLayout == null) {
            mMoviePreviewLayout = findViewById(R.id.lsq_movie_mixer_preview);
            int movieWidth = TuSdkContext.getScreenSize().width;
            int movieHeight = movieWidth * 9 / 16;
            RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) mMoviePreviewLayout.getLayoutParams();
            lp.width = movieWidth;
            lp.height = movieHeight;
        }
        return mMoviePreviewLayout;
    }

    /**
     * 初始化View
     */
    private void initView() {
        mBackBtn = findViewById(R.id.lsq_backButton);
        mBackBtn.setOnClickListener(mOnClickListener);

        TextView titleView = findViewById(R.id.lsq_titleView);
        titleView.setText(TuSdkContext.getString("lsq_movie_mixer_text"));

        mMovieMixerButton = findViewById(R.id.lsq_movie_mixer_start);
        mMovieMixerButton.setOnClickListener(mOnClickListener);
        TuSdk.messageHub().applyToViewWithNavigationBarHidden(false);
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
     * 准备音频播放器
     */
    private void prepareAudio() {
        if (mMutiAudioPlayer == null) initMutiAudioPlayer();
        mMutiAudioPlayer.setLooping(true);
        mMutiAudioPlayer.asyncPrepare(getAudioEntryList());
    }

    /**
     * 初始化多音轨播放器
     */
    private void initMutiAudioPlayer() {
        mMutiAudioPlayer = new TuSDKMutiAudioPlayer();
        mMutiAudioPlayer.setDelegate(mMutiAudioPlayerDelegate);
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
                this.setSeekBarProgress(i, 0.5f);
            }
        }
    }

    /**
     * 设置进度以及音量
     *
     * @param index
     * @param progress
     */
    private void setSeekBarProgress(int index, float progress) {
        getVoiceConfigView().getSeekBarList().get(index).setProgress(progress);

        // 设置音频音量
        getAudioEntryList().get(index).setVolume(progress);
    }

    //输出路径
    private String getMixedVideoPath() {
        mMixedVideoPath = new File(AlbumHelper.getAblumPath(), String.format("lsq_%s.mp4",
                StringHelper.timeStampString())).getPath();
        return mMixedVideoPath;
    }

    /**
     * 准备音频数据
     */
    private List<TuSDKAudioEntry> getAudioEntryList() {
        if (mAudioEntryList != null && mAudioEntryList.size() > 0) return mAudioEntryList;

        mAudioEntryList = new ArrayList<>(2);

        TuSDKAudioEntry audioEntry = new TuSDKAudioEntry(mInputPath);
        audioEntry.setTrunk(true);
        mAudioEntryList.add(audioEntry);

        for (int i = 0; i < AUIDOENTRY_RESIDARRAY.length; i++) {
            Uri uri = Uri.parse("android.resource://" + getPackageName() + "/" + AUIDOENTRY_RESIDARRAY[i]);
            TuSDKAudioEntry audio = new TuSDKAudioEntry(uri);
            audio.setTrunk(false);
            mAudioEntryList.add(audio);
        }

        return mAudioEntryList;
    }


    /**
     * 开始播放音频数据
     */
    private void startMutiAudioPlayer() {
        if (mMutiAudioPlayer == null) return;
        mMutiAudioPlayer.start();
    }

    /**
     * 停止音频播放器
     */
    private void stopMutiAudioPlayer() {
        if (mMutiAudioPlayer == null) return;
        mMutiAudioPlayer.stop();
    }

    /** 开始播放视频 **/
    private void startMoviePlayer() {
        if (mMoviePlayer == null) return;
        mMoviePlayer.start();
    }

    /** 停止播放视频 **/
    private void stopMoviePlayer() {
        if (mMoviePlayer == null) return;
        mMoviePlayer.stop();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!isFirst) {
            startMutiAudioPlayer();
            startMoviePlayer();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopMutiAudioPlayer();
        stopMoviePlayer();
        isFirst = false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopMutiAudioPlayer();
        stopMoviePlayer();
        mMoviePlayer = null;
        mMutiAudioPlayer = null;
    }

    /**
     * 混合音视频
     */
    private void startMovieMixer() {
        // 混合音视频前需将音频、视频暂停
        stopMoviePlayer();
        stopMutiAudioPlayer();

        mMP4MovieMixer = new TuSDKMP4MovieMixer();
        mMP4MovieMixer.setDelegate(this)
                .setOutputFilePath(getMixedVideoPath()) // 设置输出路径
                .setVideoSoundVolume(1.f) // 设置视频原音音量
                .setClearAudioDecodeCacheInfoOnCompleted(true) // 设置音视频混合完成后是否清除缓存信息 默认：true （false:再次混合时可加快混合速度）
                .mix(TuSDKMediaDataSource.create(getVideoPath()), mAudioEntryList, false); //  mVideoDataSource : 视频路径 mAudioTracks : 待混合的音频数据 true ： 是否混合视频原音
    }

    // 刷新文件
    public void refreshFile(File file) {
        if (file == null) {
            TLog.e("refreshFile file == null");
            return;
        }
        Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        Uri uri = Uri.fromFile(file);
        intent.setData(uri);
        this.sendBroadcast(intent);
    }

    /**
     * 点击事件
     */
    private View.OnClickListener mOnClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.lsq_movie_mixer_start:
                    startMovieMixer();
                    break;
                case R.id.lsq_backButton:
                    finish();
                    break;
            }
        }
    };

    @Override
    public void onStateChanged(TuSDKMP4MovieMixer.State state) {
        if (state == TuSDKMP4MovieMixer.State.Decoding) {
            TuSdk.messageHub().setStatus(this, "正在解码...");

        } else if (state == TuSDKMP4MovieMixer.State.Mixing) {
            TuSdk.messageHub().setStatus(this, "正在混合...");

        } else if (state == TuSDKMP4MovieMixer.State.Failed) {
            TuSdk.messageHub().setStatus(this, "混合失败");
        } else {
            TuSdk.messageHub().dismissRightNow();
        }
    }

    @Override
    public void onErrrCode(TuSDKMP4MovieMixer.ErrorCode code) {
        if (code == TuSDKMP4MovieMixer.ErrorCode.UnsupportedVideoFormat) {
            TuSdk.messageHub().showError(this, "不支持的视频格式");
        }
    }

    @Override
    public void onMixerComplete(TuSDKVideoResult result) {
        if (result != null) {
            TuSdk.messageHub().showSuccess(this, "混合完成,请到 DCIM 目录下查看");
            refreshFile(new File(mMixedVideoPath));
            TLog.d("result： %s", result.videoInfo);
        }
    }
}
