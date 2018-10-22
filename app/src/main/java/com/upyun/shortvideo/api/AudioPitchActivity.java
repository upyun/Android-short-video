package com.upyun.shortvideo.api;

import android.app.Activity;
import android.media.MediaFormat;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.SeekBar;

import org.lasque.tusdk.api.audio.preproc.mixer.TuSDKAudioMixerRender;
import org.lasque.tusdk.api.audio.preproc.mixer.TuSDKAudioRenderEntry;
import org.lasque.tusdk.core.api.extend.TuSdkMediaPlayerListener;
import org.lasque.tusdk.core.api.extend.TuSdkMediaProgress;
import org.lasque.tusdk.core.media.codec.audio.TuSdkAudioInfo;
import org.lasque.tusdk.core.media.codec.extend.TuSdkMediaFormat;
import org.lasque.tusdk.core.media.codec.extend.TuSdkMediaTimeline;
import org.lasque.tusdk.core.media.codec.suit.TuSdkMediaFileDirectorPlayer;
import org.lasque.tusdk.core.media.codec.video.TuSdkVideoQuality;
import org.lasque.tusdk.core.media.suit.TuSdkMediaSuit;
import org.lasque.tusdk.core.seles.output.SelesView;
import org.lasque.tusdk.core.struct.TuSdkMediaDataSource;
import org.lasque.tusdk.core.struct.TuSdkSize;
import org.lasque.tusdk.core.utils.TLog;
import org.lasque.tusdk.core.view.TuSdkViewHelper;
import com.upyun.shortvideo.R;

import static android.media.MediaFormat.KEY_SAMPLE_RATE;

public class AudioPitchActivity extends Activity {

    private static final String TAG = "AudioPitchActivity";
    private static final String TEST_AUDIO_PATH = Environment.getExternalStorageDirectory().getPath()+"/test1.mp3";

    private String mInputPath;

    private TuSdkMediaFileDirectorPlayer mediaFilePlayer;
    /** 进度条 */
    private SeekBar mSeekBar;
    /** 播放按钮 */
    private Button mPlayBtn;
    /** 暂停按钮 */
    private Button mPauseBtn;
    /** 慢速按钮 */
    private Button mSlowBtn;
    /** 快速按钮 */
    private Button mFastBtn;
    /** 倒播按钮 */
    private Button mReverseBtn;

    private SelesView mVideoView;

    private RelativeLayout mPreviewWrap;

    private TuSDKAudioMixerRender mMixerRender;

    private TuSdkAudioInfo mTrunkAudioInfo ;
    private TuSDKAudioRenderEntry mMixerAudioInfo ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio_pitch);

        mInputPath = getIntent().getStringExtra("videoPath");
        mInputPath = Environment.getExternalStorageDirectory().getPath()+"/sssssssss.mp4";

        test3();

    }



    private void test3() {
        mSeekBar = this.findViewById(R.id.lsq_progress_bar);
        mSeekBar.setMax(100);

        mPreviewWrap = this.findViewById(R.id.priviewWrap);
        mPlayBtn = this.findViewById(R.id.lsq_play_btn);
        mPauseBtn = this.findViewById(R.id.lsq_pause_btn);
        mSlowBtn = this.findViewById(R.id.lsq_slow_btn);
        mFastBtn = this.findViewById(R.id.lsq_fast_btn);
        mReverseBtn = this.findViewById(R.id.lsq_reverse_btn);

        /** Step 1: 创建播放器对象 */

        mediaFilePlayer = TuSdkMediaSuit.directorPlayer(
                new TuSdkMediaDataSource(mInputPath),
                true,
                new TuSdkMediaPlayerListener() {
                    /**
                     * 执行进度 [主线程]
                     *
                     * @param playbackTimeUs 当前播放时间戳 [微秒]
                     * @param totalTimeUs    总时长
                     * @param mediaDataSource           当前处理的视频文件
                     */
                    @Override
                    public void onProgress(long playbackTimeUs, long totalTimeUs, TuSdkMediaDataSource mediaDataSource) {
                        TLog.d("%s onProgress[%f], %d/%d, %s", TAG, playbackTimeUs / (float) totalTimeUs, playbackTimeUs / 1000, totalTimeUs / 1000, mediaDataSource);

                        mSeekBar.setProgress((int) (playbackTimeUs * 100 / (float) totalTimeUs));
                    }

                    /***
                     * 完成转码 [主线程]
                     * @param e 如果成功则为Null
                     * @param mediaDataSource 文件路径
                     */
                    @Override
                    public void onCompleted(Exception e, TuSdkMediaDataSource mediaDataSource) {
                        if (e == null) TLog.d("%s onCompleted: %s", TAG, mediaDataSource);
                        else TLog.e(e, "%s onCompleted: %s", TAG, mediaDataSource);
                    }

                    /***
                     * 播放状态改变
                     * @param state 状态: -1停止, 0播放, 1暂停
                     */
                    @Override
                    public void onStateChanged(int state) {
                        TLog.d("%s onStateChanged: %d", TAG, state);
                    }

                    /*** 有新的画面帧需要刷新 */
                    @Override
                    public void onFrameAvailable() {
                        mVideoView.requestRender();
                    }
                }
        );

        if (mediaFilePlayer == null) {
            TLog.e("%s TuSdkMediaFilePlayerImpl create failed.", TAG);
            return;
        }

//        mediaFilePlayer.setAudioMixerRender(mMixerRender);


        /** Step 2: 创建预览视图 */
        mVideoView = new SelesView(this);
        mVideoView.setFillMode(SelesView.SelesFillModeType.PreserveAspectRatio);
        /** Step 2 - 1: 内部调用方法 */
        mVideoView.setRenderer(mediaFilePlayer.getExtenalRenderer());
        /** Step 2 - 2: 外部调用方法 */
//        mVideoView.setRenderer(new GLSurfaceView.Renderer() {
//            @Override
//            public void onSurfaceCreated(GL10 gl, javax.microedition.khronos.egl.EGLConfig config) {
//                GLES20.glDisable(GLES20.GL_DEPTH_TEST);
//                mediaFilePlayer.initInGLThread();
//            }
//
//            @Override
//            public void onSurfaceChanged(GL10 gl, int width, int height) {
//                GLES20.glViewport(0, 0, width, height);
//            }
//
//            @Override
//            public void onDrawFrame(GL10 gl) {
//                GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
//                mediaFilePlayer.newFrameReadyInGLThread();
//            }
//        });
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        mPreviewWrap.addView(mVideoView, 0, params);
        /** Step 3: 连接视图对象 */
        mediaFilePlayer.getFilterBridge().addTarget(mVideoView, 0);


        SeekBar.OnSeekBarChangeListener mSeekBarListener = new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (!fromUser) return;
                mediaFilePlayer.seekToPercentage(progress / (float) seekBar.getMax());
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                mediaFilePlayer.pause();
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mediaFilePlayer.resume();
            }
        };

        View.OnClickListener mClickListener = new TuSdkViewHelper.OnSafeClickListener() {
            @Override
            public void onSafeClick(View v) {
                switch (v.getId()) {
                    case R.id.lsq_play_btn:
                        mediaFilePlayer.resume();
                        break;
                    case R.id.lsq_pause_btn:
                        mediaFilePlayer.pause();
                        break;
                    case R.id.lsq_slow_btn:
//                        mediaFilePlayer.setSpeed(0.5f);
                        break;
                    case R.id.lsq_fast_btn:
//                        mediaFilePlayer.setSpeed(2.0f);
                        //mediaFilePlayer.preview(new TuSdkMediaTimeline(new TuSdkMediaTimeSlice(53 * 1000000, 65 * 1000000, 2f)));
                        break;
                    case R.id.lsq_reverse_btn:
                        TuSdkMediaTimeline mediaTimeline = new TuSdkMediaTimeline();
//                        mediaTimeline.append(6 * 1000000,0,1);
//                        mediaTimeline.append(2 * 1000000,6 * 1000000,1);

                        //反复
                        mediaTimeline.append(7 * 1000000,1 * 1000000,1);


                        mediaFilePlayer.preview(mediaTimeline);
//                        mediaFilePlayer.setReverse(!mediaFilePlayer.isReverse());
                        break;
                    default:
                        break;
                }
            }
        };

        mSeekBar.setOnSeekBarChangeListener(mSeekBarListener);
        mPlayBtn.setOnClickListener(mClickListener);
        mPauseBtn.setOnClickListener(mClickListener);
        mSlowBtn.setOnClickListener(mClickListener);
        mFastBtn.setOnClickListener(mClickListener);
        mReverseBtn.setOnClickListener(mClickListener);
    }


    public void test5(){
        TuSdkMediaDataSource mediaDataSource = new TuSdkMediaDataSource();
        mediaDataSource.setPath(mInputPath);
        MediaFormat videoFormat = TuSdkMediaFormat.buildSafeVideoSurfaceEncodecFormat(
                TuSdkSize.create(480, 720),
                TuSdkVideoQuality.RECORD_MEDIUM2, false
        );
        MediaFormat audioFormat = TuSdkMediaFormat.buildSafeAudioEncodecFormat();
        audioFormat.setInteger(KEY_SAMPLE_RATE, 48000);

        String mOutput = Environment.getExternalStorageDirectory().getAbsolutePath() + "/123456789.mp4";

        TuSdkMediaSuit.transcoding(mediaDataSource,mOutput,videoFormat,audioFormat,new TuSdkMediaProgress(){

            @Override
            public void onProgress(float progress, TuSdkMediaDataSource mediaDataSource, int index, int total) {

            }

            @Override
            public void onCompleted(Exception e, TuSdkMediaDataSource outputFile, int total) {
                TLog.e("onCompleted : %s",outputFile);
            }
        });
    }


}
