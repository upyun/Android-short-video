package com.upyun.shortvideo.api;

import android.app.Activity;
import android.media.MediaCodec;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import org.lasque.tusdk.core.TuSdkContext;
import org.lasque.tusdk.core.api.extend.TuSdkAudioRender;
import org.lasque.tusdk.core.api.extend.TuSdkMediaPlayerListener;
import org.lasque.tusdk.core.media.codec.audio.TuSdkAudioPitch;
import org.lasque.tusdk.core.media.codec.audio.TuSdkAudioPitchSoftImpl;
import org.lasque.tusdk.core.media.codec.suit.TuSdkMediaFilePlayer;
import org.lasque.tusdk.core.media.codec.sync.TuSdkAudioPitchSync;
import org.lasque.tusdk.core.media.suit.TuSdkMediaSuit;
import org.lasque.tusdk.core.seles.output.SelesView;
import org.lasque.tusdk.core.struct.TuSdkMediaDataSource;
import com.upyun.shortvideo.R;

import java.nio.ByteBuffer;

/**
 * 媒体播放器
 *
 * @author zuojindong
 */
public class MediaPlayerActivity extends Activity {

    private TuSdkMediaFilePlayer mediaFilePlayer;
    /** 返回按钮 */
    private TextView mBackBtn;
    /** 输入视频路径 */
    private String mInputPath = "";
    /** 播放按键 */
    private ImageView play;
    /** 滚动条 */
    private SeekBar mSeekBar;
    /** 调节数值 */
    private float mValue = 1;
    /** 数值显示 */
    private TextView valueText;
    /** 单选框 */
    private RadioButton mPitchRb;
    /** 播放进度条 */
    private ProgressBar playProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_media_player);

        initView();
        initPlayer();
    }

    /**
     * 初始化视图
     */
    private void initView() {
        mInputPath = getIntent().getStringExtra("videoPath");

        mBackBtn = (TextView) findViewById(R.id.lsq_back);
        mBackBtn.setOnClickListener(mOnClickListener);
        TextView titleView = (TextView) findViewById(R.id.lsq_title);
        titleView.setText(TuSdkContext.getString("lsq_media_player"));
        TextView nextBtn = (TextView) findViewById(R.id.lsq_next);
        nextBtn.setVisibility(View.GONE);

        play = findViewById(R.id.lsq_play);

        mSeekBar = findViewById(R.id.lsq_audio_seek);
        mSeekBar.setOnSeekBarChangeListener(seekBarChangeListener);
        valueText = findViewById(R.id.lsq_num);

        playProgress = findViewById(R.id.lsq_play_progress);

        mPitchRb = findViewById(R.id.lsq_audio_pitch);
        mPitchRb.setChecked(true);
    }

    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.lsq_back:
                    finish();
                    break;
            }
        }
    };

    private SeekBar.OnSeekBarChangeListener seekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            mValue = 2f * seekBar.getProgress() / seekBar.getMax();

            if (mValue == 0)
                mValue = 0.01f;

            valueText.setText(mValue + "");
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

        }
    };

    private long mTotalTimeUs = 0;

    /**
     * 初始化播放器
     */
    private void initPlayer() {
        final SelesView mVideoView = new SelesView(this);

        mediaFilePlayer = TuSdkMediaSuit.playMedia(new TuSdkMediaDataSource(mInputPath), true, new TuSdkMediaPlayerListener() {

            @Override
            public void onStateChanged(int state) {

            }

            @Override
            public void onFrameAvailable() {
                mVideoView.requestRender();
            }

            @Override
            public void onProgress(long playbackTimeUs, long totalTimeUs, TuSdkMediaDataSource mediaDataSource) {
                if (mTotalTimeUs == 0) mTotalTimeUs = totalTimeUs;
                int progress = (int) (100 * playbackTimeUs / mTotalTimeUs);
                playProgress.setProgress(progress);
                if (progress == 100) play.setVisibility(View.VISIBLE);
            }

            @Override
            public void onCompleted(Exception e, TuSdkMediaDataSource mediaDataSource) {

            }
        });

        setAudioRender(mediaFilePlayer);

        mVideoView.setRenderer(mediaFilePlayer.getExtenalRenderer());
        RelativeLayout audioView = findViewById(R.id.audioView);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout
                .LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        audioView.addView(mVideoView, 0, params);
        mVideoView.setFillMode(SelesView.SelesFillModeType.PreserveAspectRatio);

        mediaFilePlayer.getFilterBridge().addTarget(mVideoView, 0);
    }

    /**
     * 播放按键
     *
     * @param view
     */
    public void playMedia(View view) {
        if (play.getVisibility() == View.VISIBLE) {
            mediaFilePlayer.resume();
            play.setVisibility(View.GONE);
        } else {
            mediaFilePlayer.pause();
            play.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mediaFilePlayer != null) {
            play.setVisibility(View.VISIBLE);
            mediaFilePlayer.pause();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaFilePlayer != null)
            mediaFilePlayer.release();
    }

    /**
     * 设置外部音频渲染
     */
    private void setAudioRender(TuSdkMediaFilePlayer mediaFilePlayer) {

        /** 可选: 外部音频渲染 */
        mediaFilePlayer.setAudioRender(new TuSdkAudioRender() {
            /** 音频变调接口 */
            TuSdkAudioPitch mAudioPitch;
            /** 音频渲染处理回调 */
            TuSdkAudioRender.TuSdkAudioRenderCallback mCallback;

            /**
             * 渲染一帧音频
             * @param buffer 解码音频数据
             * @param bufferInfo 解码音频数据信息
             * @param callback 音频渲染处理回调
             * @return 是否接过处理流程 [true: 按自定义流程, false: 按系统原始流程]
             */
            @Override
            public boolean onAudioSliceRender(ByteBuffer
                                                      buffer, MediaCodec.BufferInfo bufferInfo, TuSdkAudioRender.TuSdkAudioRenderCallback callback) {
                if (callback.isEncodec()) return false;
                mCallback = callback;
                if (mAudioPitch == null) {
                    mAudioPitch = new TuSdkAudioPitchSoftImpl(callback.getAudioInfo());
                    // 设置处理委托
                    mAudioPitch.setMediaSync(this.mPitchSync);
                }
                // 变速和变调只会生效一个
                if (mPitchRb.isChecked())
                    mAudioPitch.changePitch(mValue);
                else
                    mAudioPitch.changeSpeed(mValue);
                // 处理数据
                mAudioPitch.queueInputBuffer(buffer, bufferInfo);
                return true;
            }

            /** 变调同步器 */
            private TuSdkAudioPitchSync mPitchSync = new TuSdkAudioPitchSync() {
                @Override
                public void syncAudioPitchOutputBuffer(ByteBuffer byteBuf, MediaCodec.BufferInfo bufferInfo) {
                    mCallback.returnRenderBuffer(byteBuf, bufferInfo);
                }

                @Override
                public void release() {
                }
            };
        });
    }
}
