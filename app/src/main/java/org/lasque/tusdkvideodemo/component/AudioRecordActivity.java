package org.lasque.tusdkvideodemo.component;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.lasque.tusdk.core.TuSdk;
import org.lasque.tusdk.core.TuSdkContext;
import org.lasque.tusdk.core.audio.TuSdkAudioRecorder;
import org.lasque.tusdk.core.utils.StringHelper;
import org.lasque.tusdk.core.utils.image.AlbumHelper;
import com.upyun.shortvideo.R;
import org.lasque.tusdkvideodemo.ScreenAdapterActivity;

import java.io.File;

/**
 * droid-sdk-video
 *
 * @author MirsFang
 * @Date 2018/9/17 16:45
 * @Copright (c) 2018 tusdk.com. All rights reserved.
 * <p>
 * 音频录制
 */
public class AudioRecordActivity extends ScreenAdapterActivity {

    //返回按钮
    private Button mBackBtn;

    /** 开始录音按钮 */
    private Button mStartRecordButton;

    /** 播放录音按钮 */
    private Button mPlayAudioButton;

    /** 结束录音按钮 */
    private Button mStopRecordButton;

    /** 生成的录音文件 */
    private File mAudioFile;

    private TuSdkAudioRecorder mAudioRecorder;
    /** 音频文件录制实例 */
//    private TuSDKAudioFileRecorder mAudioRecorder;

    /** 音视频播放器 */
    private MediaPlayer mMediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio_record);
        initView();
    }

    /** 初始化视图 */
    private void initView() {
        mBackBtn = findViewById(R.id.lsq_backButton);
        mBackBtn.setOnClickListener(mOnClickListener);

        TextView titleView = findViewById(R.id.lsq_titleView);
        titleView.setText(TuSdkContext.getString("lsq_audio_file_recorder"));

        mStartRecordButton = (Button) findViewById(R.id.lsq_audio_record_btn);
        mStopRecordButton = (Button) findViewById(R.id.lsq_audio_stop_btn);
        mPlayAudioButton = (Button) findViewById(R.id.lsq_audio_play_btn);
        mStartRecordButton.setOnClickListener(mOnClickListener);
        mPlayAudioButton.setOnClickListener(mOnClickListener);
        mStopRecordButton.setOnClickListener(mOnClickListener);
        initMediaPlayer();

    }

    private TuSdkAudioRecorder getAudioFileRecorder() {
        if(mAudioRecorder == null) {
            //支持断点录制
            TuSdkAudioRecorder.TuSdkAudioRecorderSetting setting = new TuSdkAudioRecorder.TuSdkAudioRecorderSetting();
            mAudioRecorder = new TuSdkAudioRecorder(setting,mRecordListener);
            mAudioRecorder.setMaxRecordTime(10 * 1000000);
            mAudioRecorder.setOutputFile(getOutputFile());
        }
        return mAudioRecorder;
    }

    public File getOutputFile(){
        mAudioFile = new File(AlbumHelper.getAblumPath(), String.format("lsq_%s.aac", StringHelper.timeStampString()));
        return mAudioFile;
    }

    /**
     * 录音委托事件
     */
    private TuSdkAudioRecorder.TuSdkAudioRecorderListener mRecordListener = new TuSdkAudioRecorder.TuSdkAudioRecorderListener() {
        @Override
        public void onRecordProgress(long durationTimeUS, float percent) {

        }

        @Override
        public void onStateChanged(int state) {
            if(state == TuSdkAudioRecorder.START_RECORD){
                String hintMsg = getResources().getString(R.string.lsq_audio_record_recording);
                TuSdk.messageHub().showToast(AudioRecordActivity.this, hintMsg);
            }else if(state == TuSdkAudioRecorder.STOP_RECORD){
                String hintMsg = getResources().getString(R.string.lsq_audio_record_stopped);
                TuSdk.messageHub().showToast(AudioRecordActivity.this, hintMsg);
                mAudioRecorder = null;
            }
        }

        @Override
        public void onRecordError(int code) {
            if (code == PERMISSION_ERROR) {
                String hintMsg = getResources().getString(R.string.lsq_audio_initialization_failed_hint);
                TuSdk.messageHub().showError(AudioRecordActivity.this, hintMsg);
            }
        }
    };




    private void initMediaPlayer() {
        mMediaPlayer = new MediaPlayer();
        mMediaPlayer.setOnCompletionListener(mOnCompletionListener);
    }

    /**
     * 音频播放结束监听事件
     */
    private MediaPlayer.OnCompletionListener mOnCompletionListener = new MediaPlayer.OnCompletionListener() {

        @Override
        public void onCompletion(MediaPlayer mp) {
            String hintMsg = getResources().getString(R.string.lsq_audio_record_played);
            TuSdk.messageHub().showToast(AudioRecordActivity.this, hintMsg);
            mMediaPlayer.reset();
        }
    };


    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.lsq_backButton:
                    finish();
                    break;
                case R.id.lsq_audio_record_btn:
                    getAudioFileRecorder().start();
                    break;
                case R.id.lsq_audio_stop_btn:
                    getAudioFileRecorder().stop();
                    break;
                case R.id.lsq_audio_play_btn:
                    try {
                        mMediaPlayer.setDataSource(mAudioFile.toString());
                        mMediaPlayer.prepare();
                        mMediaPlayer.setOnPreparedListener(mOnPreparedListener);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
            }
        }
    };

    private MediaPlayer.OnPreparedListener mOnPreparedListener = new MediaPlayer.OnPreparedListener() {

        @Override
        public void onPrepared(MediaPlayer mp) {
            mMediaPlayer.start();
            String hintMsg = getResources().getString(R.string.lsq_audio_record_playing);
            if (AudioRecordActivity.this != null)
                TuSdk.messageHub().showToast(AudioRecordActivity.this, hintMsg);
        }
    };

    @Override
    protected void onPause() {
        super.onPause();
        if (mMediaPlayer != null && mMediaPlayer.isPlaying())
            mMediaPlayer.pause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mAudioRecorder != null){
            mAudioRecorder.stop();
            mAudioRecorder = null;
        }
        if (mMediaPlayer != null)
            mMediaPlayer.release();

    }
}
