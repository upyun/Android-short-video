package com.upyun.shortvideo.views;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.upyun.shortvideo.utils.AudioTimingRunnable;

import org.lasque.tusdk.api.audio.preproc.processor.TuSDKAudioProcessor;
import org.lasque.tusdk.core.TuSdk;
import org.lasque.tusdk.core.TuSdkContext;
import org.lasque.tusdk.core.audio.TuSDKAudioFileRecorder;
import org.lasque.tusdk.core.media.codec.audio.TuSdkAudioInfo;
import org.lasque.tusdk.core.utils.FileHelper;
import org.lasque.tusdk.core.utils.TLog;
import org.lasque.tusdk.core.utils.ThreadHelper;
import org.lasque.tusdk.core.view.TuSdkImageView;
import org.lasque.tusdk.core.view.TuSdkRelativeLayout;
import com.upyun.shortvideo.R;

import java.io.File;

/**
 * @author sprint
 * @Date: 25/02/2018
 * @Copyright: (c) 2018 tusdk.com. All rights reserved.
 * @Description 配音录制视图
 */
public class DubbingRecodLayout extends TuSdkRelativeLayout
{
    /** 最小时间,单位s*/
    private static final int MIN_AUDIO_RECORD_TIME = 1;

    /** 音频文件录制实例 */
    private TuSDKAudioFileRecorder mAudioRecorder;

    /** 录音界面关闭按钮 */
    private TuSdkImageView mAudioRecordCloseButton;

    /** 录音录制按钮 */
    private TuSdkImageView mAudioRecordButton;

    /** 录音保存按钮 */
    private TuSdkImageView mAudioRecordSaveButton;

    /** 录音取消按钮 */
    private TuSdkImageView mAudioRecordCancelButton;

    /** 录音进度条 */
    private ProgressBar mAudioRecordProgressBar;

    /** 录音界面剩余时间文本 */
    private TextView mAudioTimeRemainingText;

    /** 录音计时线程 */
    private AudioTimingRunnable mAudioTimingRunnable;

    /** 保存的录音文件 */
    private File mAudioFile;

    /** 录音开始时间 */
    private long mAudioRecordStartTime;

    /** 最大录制时长 单位：秒 */
    private float mMaxRecordTime = 0;

    /** 记录开启录音的次数 */
    private int mStartAudioTimes = 0;

    private DubbingRecodLayoutDelegate mDelegate;

    /** 委托对象 */
    public interface DubbingRecodLayoutDelegate
    {
        public void onRecordCompleted(File file);

        public void onRecordStarted();
        public void onRecordStoped();
    }

    public DubbingRecodLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void loadView()
    {
        super.loadView();

        initAudioFileRecorder();

        mAudioTimingRunnable = new AudioTimingRunnable();
        mAudioTimingRunnable.setDelegate(mAudioRecordProgressDelegate);

        initAudioRecordingView();
    }


    public void setDelegate(DubbingRecodLayoutDelegate delegate)
    {
        this.mDelegate = delegate;
    }


    public void setMaxRecordTime(float maxRecordTime)
    {
        this.mMaxRecordTime = maxRecordTime;
    }

    private AudioTimingRunnable.AudioRecordProgressDelegate mAudioRecordProgressDelegate = new AudioTimingRunnable.AudioRecordProgressDelegate()
    {

        @Override
        public void onAudioRecordPogressChanged(final float duration)
        {
            ThreadHelper.post(new Runnable()
            {
                @Override
                public void run()
                {
                    if (duration > mMaxRecordTime)
                    {
                        stopAudioFileRecorder();
                        updateVoiceRecordButton(false);
                    }
                    updateAudioProgressBar(duration);
                    updateAudioTimeRemaining(duration);
                }
            });
        }
    };



    @SuppressLint("ClickableViewAccessibility")
    private void initAudioRecordingView()
    {
        mAudioRecordCloseButton = (TuSdkImageView) findViewById(R.id.lsq_voice_close_button);
        mAudioRecordButton = (TuSdkImageView) findViewById(R.id.lsq_voice_record_button);
        mAudioRecordCancelButton = (TuSdkImageView) findViewById(R.id.lsq_voice_cancel_button);
        mAudioRecordCancelButton.setOnClickListener(mClickListener);
        mAudioRecordSaveButton = (TuSdkImageView) findViewById(R.id.lsq_voice_record_save_button);
        mAudioRecordCloseButton.setOnClickListener(mClickListener);
        mAudioRecordButton.setOnTouchListener(mAudioRecordButtonOnTouchListener);
        mAudioRecordSaveButton.setOnClickListener(mClickListener);

        Button minTimeButton = (Button) findViewById(R.id.lsq_minTimeBtn);
        LayoutParams minTimeLayoutParams = (LayoutParams) minTimeButton.getLayoutParams();
        minTimeLayoutParams.leftMargin =(int)(((float) MIN_AUDIO_RECORD_TIME * TuSdkContext.getScreenSize().width) / mMaxRecordTime)
                - TuSdkContext.dip2px(minTimeButton.getWidth());

        mAudioTimeRemainingText = (TextView) findViewById(R.id.lsq_voiceRrecord_timeRemaining_text);
        updateAudioTimeRemaining(0.0f);

        mAudioRecordProgressBar = (ProgressBar) findViewById(R.id.lsq_record_progressbar);
        updateAudioProgressBar(0.0f);

    }

    /**
     * 更新录音界面剩余时间文本
     *
     * @param duration
     */
    public void updateAudioTimeRemaining(float duration)
    {
        int timeRemaining = (int) Math.abs(mMaxRecordTime - duration);
        mAudioTimeRemainingText.setText("剩余"+timeRemaining+"秒");
    }

    /**
     * 更新录音进度条
     *
     * @param audioRecordDuration
     */
    public void updateAudioProgressBar(float audioRecordDuration)
    {
        float videoDuration = mMaxRecordTime;

        if(videoDuration == 0) return;

        int progress = (int) (audioRecordDuration *100 / videoDuration);
        if (progress > 100) progress = 100;
        mAudioRecordProgressBar.setProgress(progress);
    }

    //-----------------------  TuSDKAudioFileRecorder -----------------------//
    /**
     * 初始化 TuSDKAudioFileRecorder
     */
    private void initAudioFileRecorder()
    {
        mAudioRecorder = new TuSDKAudioFileRecorder();
        mAudioRecorder.setOutputFormat(TuSDKAudioFileRecorder.OutputFormat.AAC);
        mAudioRecorder.setAudioRecordDelegate(mRecordAudioDelegate);
    }

    /**
     * 设置输入音频信息
     * @param audioInfo
     */
    public void setInputAudioInfo(TuSdkAudioInfo audioInfo)
    {
        if(mAudioRecorder == null || audioInfo == null) return;
        mAudioRecorder.setInputAudioInfo(audioInfo);
    }

    /**
     * 设置要处理的音效类型
     * @param soundType
     */
    public void setSoundType(TuSDKAudioProcessor.TuSDKSoundType soundType)
    {
        if(mAudioRecorder == null || soundType == null) return;
        mAudioRecorder.setSoundType(soundType);
    }

    /**
     * 设置音效类型改变回调
     * @param soundTypeChangeListener 音效类型改变回调
     */
    public void setSoundTypeChangeListener(TuSDKAudioProcessor.TuSDKSoundTypeChangeListener soundTypeChangeListener) {
        if(mAudioRecorder == null || soundTypeChangeListener == null) return;
        this.mAudioRecorder.setSoundTypeChangeListener(soundTypeChangeListener);
    }

    /** 停止录音 */
    private void stopAudioFileRecorder()
    {
        // 关闭录音放在子线程,避免录音时更新进度延迟严重(30ms)
        ThreadHelper.runThread(new Runnable()
        {
            @Override
            public void run()
            {
                mAudioRecorder.stop();
            }
        });
    }

    /**
     * 是否正在录制音效
     *
     * @return
     */
    private boolean isAudioRecording()
    {
        return mAudioRecorder.isRecording();
    }


    private OnClickListener mClickListener = new OnClickListener()
    {
        @Override
        public void onClick(View view)
        {

            if (view == mAudioRecordCloseButton)
            {
                setVisibility(View.GONE);

                if (mDelegate != null)
                    mDelegate.onRecordCompleted(null);

            } else if (view == mAudioRecordSaveButton)
            {
                if (mAudioRecordProgressBar.getProgress() == 0) return;

                setVisibility(View.GONE);

                updateAudioProgressBar(0);

                TLog.i("mAudioFile === "+ mAudioFile);

                if (mDelegate != null)
                    mDelegate.onRecordCompleted(mAudioFile);


            } else if (view == mAudioRecordCancelButton)
            {
                // 清空录音临时文件
                deleteFile(mAudioFile);
                mAudioFile = null;
                updateAudioProgressBar(0);

                toggleCloseCancleButton(true);

                updateAudioTimeRemaining(0.0f);


            }
        }
    };


    /** 录音按钮触摸事件处理 */
    private OnTouchListener mAudioRecordButtonOnTouchListener = new OnTouchListener()
    {
        long startTime;
        long recordTime;

        @SuppressLint("ClickableViewAccessibility")
        @Override
        public boolean onTouch(View v, MotionEvent event)
        {
            switch (event.getAction())
            {
                case MotionEvent.ACTION_DOWN:
                    startTime = System.currentTimeMillis();
                    if (mAudioFile == null && mStartAudioTimes < 1)
                    {
                        mAudioRecorder.start();
                    }
                    if (!isAudioRecording())
                    {
                        String hintMsg = getStringFromResource("lsq_audio_delete_hint");
                        TuSdk.messageHub().showToast(getContext(), hintMsg);
                    }
                    break;

                case MotionEvent.ACTION_UP:

                case MotionEvent.ACTION_CANCEL:
                    if (mStartAudioTimes >1) break;
                    recordTime = System.currentTimeMillis()-startTime;
                    // 避免录音时间过短导致奔溃
                    if (recordTime < 500)
                    {
                        ThreadHelper.postDelayed(new Runnable()
                        {

                            @Override
                            public void run()
                            {
                                // 调用stop前先判断
                                // 避免录音状态不对造成异常
                                if (!isAudioRecording()) return;
                                mAudioRecorder.stop();

                            }
                        }, 500-recordTime);
                    }
                    else
                    {
                        stopAudioFileRecorder();
                    }


                    break;
            }
            return true;
        }
    };

    /**
     * 录音界面关闭和撤销按钮切换
     */
    private void toggleCloseCancleButton(boolean isCloseButton)
    {
        mAudioRecordCloseButton.setVisibility(isCloseButton ? View.VISIBLE : View.INVISIBLE);
        mAudioRecordCancelButton.setVisibility(isCloseButton ? View.INVISIBLE : View.VISIBLE);
    }

    private void updateVoiceRecordButton(boolean isRecording)
    {
        int imgId = 0;
        imgId = isRecording ? R.drawable.tusdk_view_dubbing_record_selected_button : R.drawable.tusdk_view_dubbing_record_unselected_button;
        mAudioRecordButton.setImageResource(imgId);
    }


    /**
     * 录音委托事件
     */
    private TuSDKAudioFileRecorder.TuSDKRecordAudioDelegate mRecordAudioDelegate = new TuSDKAudioFileRecorder.TuSDKRecordAudioDelegate(){

        @Override
        public void onAudioRecordComplete(File file)
        {
            mAudioFile = file;

        }

        @Override
        public void onAudioRecordStateChanged(TuSDKAudioFileRecorder.RecordState state)
        {
            if (state == TuSDKAudioFileRecorder.RecordState.Recording)
            {
                mAudioRecordStartTime = System.currentTimeMillis();
                mAudioTimingRunnable.setAudioRecordStartTime(mAudioRecordStartTime);
                mAudioTimingRunnable.start();
                ThreadHelper.runThread(mAudioTimingRunnable);

                updateVoiceRecordButton(true);

                if (mDelegate != null)
                    mDelegate.onRecordStarted();

                mStartAudioTimes++;

            }
            else if (state == TuSDKAudioFileRecorder.RecordState.Stoped)
            {
                mStartAudioTimes = 0;
                mAudioTimingRunnable.stop();

                if (mDelegate != null)
                    mDelegate.onRecordStoped();

                updateVoiceRecordButton(false);
                toggleCloseCancleButton(false);

                // 录音时间小于最小时间不保留录音
                long audioRecordStopTime = System.currentTimeMillis();
                if((audioRecordStopTime - mAudioRecordStartTime)/(float)1000 < (float) MIN_AUDIO_RECORD_TIME)
                {
                    deleteFile(mAudioFile);
                    mAudioFile = null;
                    ThreadHelper.post(new Runnable()
                    {

                        @Override
                        public void run()
                        {
                            updateAudioProgressBar(0.0f);
                            toggleCloseCancleButton(true);
                        }
                    });

                    String messageId = getStringFromResource("lsq_audio_record_mintime");
                    TuSdk.messageHub().showToast(getContext(), messageId);
                }
            }
        }

        @Override
        public void onAudioRecordError(TuSDKAudioFileRecorder.RecordError error)
        {
            if(error == TuSDKAudioFileRecorder.RecordError.InitializationFailed)
            {
                String messageId = getStringFromResource("lsq_audio_initialization_failed_hint");
                TuSdk.messageHub().showError(getContext(),messageId);
            }
        }

    };

    protected String getStringFromResource(String fieldName)
    {
        int stringID = this.getResources().getIdentifier(fieldName, "string", this.getContext().getApplicationContext().getPackageName());

        return getResources().getString(stringID);
    }

    private void deleteFile(File file)
    {
        if (file == null) return;

        FileHelper.delete(file);
        refreshFile(file);
    }

    public void refreshFile(File file)
    {
        if (file == null) {
            TLog.e("refreshFile file == null");
            return;
        }

        Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        Uri uri = Uri.fromFile(file);
        intent.setData(uri);
        this.getContext().sendBroadcast(intent);
    }

}
