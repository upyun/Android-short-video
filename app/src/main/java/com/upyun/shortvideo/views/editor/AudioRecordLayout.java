package com.upyun.shortvideo.views.editor;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.media.MediaFormat;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import org.lasque.tusdk.api.audio.preproc.processor.TuSdkAudioPitchEngine;
import org.lasque.tusdk.core.TuSdk;
import org.lasque.tusdk.core.TuSdkContext;
import org.lasque.tusdk.core.audio.TuSdkAudioRecordCuter;
import org.lasque.tusdk.core.audio.TuSdkAudioRecorder;
import org.lasque.tusdk.core.decoder.TuSDKAudioInfo;
import org.lasque.tusdk.core.media.codec.extend.TuSdkMediaFormat;
import org.lasque.tusdk.core.seles.sources.TuSdkMovieEditor;
import org.lasque.tusdk.core.utils.FileHelper;
import org.lasque.tusdk.core.utils.TLog;
import org.lasque.tusdk.core.utils.ThreadHelper;
import org.lasque.tusdk.core.view.TuSdkViewHelper;

import com.upyun.shortvideo.views.HorizontalProgressBar;
import com.upyun.shortvideo.R;

import static org.lasque.tusdk.api.audio.preproc.processor.TuSdkAudioPitchEngine.TuSdkSoundPitchType.Girl;
import static org.lasque.tusdk.api.audio.preproc.processor.TuSdkAudioPitchEngine.TuSdkSoundPitchType.Lolita;
import static org.lasque.tusdk.api.audio.preproc.processor.TuSdkAudioPitchEngine.TuSdkSoundPitchType.Monster;
import static org.lasque.tusdk.api.audio.preproc.processor.TuSdkAudioPitchEngine.TuSdkSoundPitchType.Normal;
import static org.lasque.tusdk.api.audio.preproc.processor.TuSdkAudioPitchEngine.TuSdkSoundPitchType.Uncle;


/**
 * 录音视图
 *
 * @author MirsFang
 */
public class AudioRecordLayout extends RelativeLayout implements View.OnClickListener {
    private static final String TAG = "AudioRecordLayout";
    private HorizontalProgressBar mRecordProgressBar;
    //默认录制高度为20dp
    private int mRecordBarHeight = 5;
    private RecordBottomView mRecordBottomView;

    /** 音频文件录制实例 */
    private TuSdkAudioRecorder mAudioRecorder;
    private TuSdkMovieEditor mMovieEditor;
    private TuSdkAudioRecordCuter mMovieCuter;
    private TuSdkAudioRecordCuter.OnAudioRecordCuterListener mOnRecordCutLisener;
    private OnRecordOperationListener mOperationListener;

    /** 音效的顺序 **/
    private TuSdkAudioPitchEngine.TuSdkSoundPitchType[] mSoundTypes =
            new TuSdkAudioPitchEngine.TuSdkSoundPitchType[]{Monster, Uncle, Normal, Girl, Lolita};

    /** 默认选择 */
    private int mCurrentPos = 2;

    /** 操作的事件回调 **/
    public interface OnRecordOperationListener {
        /** 取消事件 */
        void onCancel();
    }

    /**
     * 录制按钮点击事件
     */
    public interface OnRecordTouchListener {
        /** 开始录制音频 */
        void onStartRecordAudio();

        /** 暂停录制音频 */
        void onPauseRecordAudio();

        /** 删除上段音频 */
        void onDeletedSegment();

        /** 下一步 **/
        void onNextStep();
    }

    /** 录制按钮 **/
    private OnRecordTouchListener mOnRecordTouchListener = new OnRecordTouchListener() {
        @Override
        public void onStartRecordAudio() {

            if(mAudioRecorder.getValidTimeUs() >=
                    mMovieEditor.getEditorPlayer().getInputTotalTimeUs()){
                TuSdkViewHelper.toast(getContext(), R.string.lsq_max_audio_record_time);
                return;
            }

            // 如果是未开启的状态  开启录音
            if (!mAudioRecorder.isStart()) {
                mAudioRecorder.start();
            }
            //如果是暂停状态  回复录音
            if (mAudioRecorder.isPause()) {
                mAudioRecorder.resume();
            }

            mMovieEditor.getEditorPlayer().startPreview();
        }

        @Override
        public void onPauseRecordAudio() {
            if (!mAudioRecorder.isPause()) {
                mAudioRecorder.pause();
                mRecordProgressBar.pauseRecord();
            }

            mMovieEditor.getEditorPlayer().pausePreview();
        }

        @Override
        public void onDeletedSegment() {
            mAudioRecorder.removeLastRecordRange();
            mRecordProgressBar.removePreSegment();
            mMovieEditor.getEditorPlayer().seekInputTimeUs(mAudioRecorder.getValidTimeUs());
        }

        @Override
        public void onNextStep() {
            mAudioRecorder.stop();
            mMovieCuter = new TuSdkAudioRecordCuter();
            mMovieCuter.setInputPath(mAudioRecorder.getOutputFileTemp().getPath());
            mMovieCuter.setOutputTimeRangeList(mAudioRecorder.getRecordingTimeRangeList());
            if (mOnRecordCutLisener != null)
                mMovieCuter.setOnAudioRecordCuterListener(mOnRecordCutLisener);
            mMovieCuter.start();
        }
    };

    public AudioRecordLayout(Context context) {
        super(context);
        initView();
    }

    public AudioRecordLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    private void initView() {

        mRecordProgressBar = getRecordProgressBar();
        LayoutParams progressLayoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, TuSdkContext.dip2px(mRecordBarHeight));
        progressLayoutParams.addRule(ALIGN_PARENT_TOP);
        addView(mRecordProgressBar, progressLayoutParams);

        mRecordBottomView = new RecordBottomView();
        LayoutParams bottomLayoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, TuSdkContext.dip2px(170));
        bottomLayoutParams.addRule(ALIGN_PARENT_BOTTOM);
        addView(mRecordBottomView.mRecordBottomView, bottomLayoutParams);

        setOnClickListener(this);
        setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getY() >= mRecordBottomView.mRecordBottomView.getTop()){
                    return true;
                }
                return false;
            }
        });

        setVisibility(GONE);
    }


    @Override
    public void onClick(View v) {
        AlertDialog.Builder adBuilder = new AlertDialog.Builder(getContext(), android.R.style.Theme_Material_Dialog_Alert);
        adBuilder.setTitle(R.string.lsq_audioRecording_cancelrecording);
        adBuilder.setNegativeButton(R.string.lsq_audioRecording_cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        adBuilder.setPositiveButton(R.string.lsq_audioRecording_next, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mOperationListener.onCancel();
                mMovieEditor.getEditorPlayer().setVideoSoundVolume(1);
                FileHelper.delete(mAudioRecorder.getOutputFileTemp());
                gone();
                dialog.dismiss();
            }
        });
        adBuilder.show();
    }

    /** 初始化录音 **/
    public void initAudioRecord(TuSdkMovieEditor movieEditor) {
        this.mMovieEditor = movieEditor;

    }

    /** 显示当前录音视图  需要重新实例化一个录音器 **/
    public void show() {
        TuSDKAudioInfo audioInfo = TuSDKAudioInfo.createWithMediaFormat(getOutputAudioFormat());
        TuSdkAudioRecorder.TuSdkAudioRecorderSetting setting = new TuSdkAudioRecorder.TuSdkAudioRecorderSetting();
        setting.bitRate = audioInfo.sampleRate;
        setting.channelCount = audioInfo.channel;
        setting.sampleRate = audioInfo.sampleRate;
        mAudioRecorder = new TuSdkAudioRecorder(setting,mAudioRecorderListener);
        mAudioRecorder.setMaxRecordTime(mMovieEditor.getEditorPlayer().getOutputTotalTimeUS());
        setVisibility(VISIBLE);
        mAudioRecorder.setSoundPitchType(mSoundTypes[mCurrentPos]);
        mRecordProgressBar.clearProgressList();
    }

    /** 隐藏当期那视图  把录音器和裁剪器销毁  进度条归0 **/
    public void gone() {
        mCurrentPos = 2;
        if (mAudioRecorder != null) mAudioRecorder.releas();
        if (mMovieCuter != null) mMovieCuter.releas();
        mAudioRecorder = null;
        mMovieCuter = null;
        //重置播放进度
        mMovieEditor.getEditorPlayer().seekOutputTimeUs(0);
        mRecordProgressBar.setProgress(0);
        mRecordBottomView.mDeletedBtn.setVisibility(GONE);
        mRecordBottomView.mNextBtn.setVisibility(GONE);
        setVisibility(GONE);
    }

    /** 录制监听 **/
    private TuSdkAudioRecorder.TuSdkAudioRecorderListener mAudioRecorderListener = new TuSdkAudioRecorder.TuSdkAudioRecorderListener() {
        @Override
        public void onRecordProgress(long durationTimeUS, float percent) {
            if(percent >= 1.0){
                mRecordProgressBar.pauseRecord();
            }
            mRecordProgressBar.setProgress(percent);
            updateBtnState(percent);
        }

        @Override
        public void onStateChanged(int state) {
        }

        @Override
        public void onRecordError(int code) {
            switch (code){
                case PERMISSION_ERROR:
                    TuSdk.messageHub().showError(getContext(), R.string.lsq_record_dialog_message);
                    break;
                case PARAMETRTS_ERROR:
                    TLog.e("%s record parameter invalid ！",TAG);
                    break;
            }
        }
    };


    /** 获取Progress */
    private HorizontalProgressBar getRecordProgressBar() {

        if (mRecordProgressBar == null) {
            View prgressView = LayoutInflater.from(getContext()).inflate(R.layout.lsq_audio_recoder_progress, null);
            mRecordProgressBar = prgressView.findViewById(R.id.lsq_record_progressbar);
        }

        return mRecordProgressBar;
    }


    private MediaFormat getOutputAudioFormat() {
       return TuSdkMediaFormat.buildSafeAudioEncodecFormat();
    }

    public void setOnRecordCutListener(TuSdkAudioRecordCuter.OnAudioRecordCuterListener onRecordCutLisener) {
        this.mOnRecordCutLisener = onRecordCutLisener;
    }


    /**
     * 设置当前操作的事件回调
     *
     * @param operationListener
     */
    public void setOperationListener(OnRecordOperationListener operationListener) {
        this.mOperationListener = operationListener;
    }


    /** 更新按钮显示状态 **/
    private void updateBtnState(final float percent) {
        ThreadHelper.post(new Runnable() {
            @Override
            public void run() {
                boolean isVisible = percent > 0;
                mRecordBottomView.mDeletedBtn.setVisibility(isVisible ? VISIBLE : INVISIBLE);
                mRecordBottomView.mNextBtn.setVisibility(isVisible ? VISIBLE : INVISIBLE);
            }
        });
    }

    /** 底部录制的视图 **/
    class RecordBottomView {
        private static final int RECORDING = 1;
        private static final int LONG_CLICK_RECORD = 2;
        private View mRecordBottomView;
        private LinearLayout mSoundTypeBar;
        private ImageButton mRecordBtn;
        private ImageView mDeletedBtn;
        private ImageView mNextBtn;

        public RecordBottomView() {
            mRecordBottomView = LayoutInflater.from(getContext()).inflate(R.layout.record_bottom_view, null);
            mRecordBtn = mRecordBottomView.findViewById(R.id.lsq_recordButton);
            mRecordBtn.setOnTouchListener(mOnTouchListener);

            mSoundTypeBar = mRecordBottomView.findViewById(R.id.lsq_editor_audio_record_type_bar);
            int childCount = mSoundTypeBar.getChildCount();
            for (int i = 0; i < childCount; i++) {
                mSoundTypeBar.getChildAt(i).setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        selectSoundType(Integer.parseInt((String) view.getTag()));
                    }
                });
            }

            mDeletedBtn = mRecordBottomView.findViewById(R.id.lsq_record_deleted);
            mNextBtn = mRecordBottomView.findViewById(R.id.lsq_record_sure);

            mDeletedBtn.setOnClickListener(mOnClickListener);
            mNextBtn.setOnClickListener(mOnClickListener);

            mRecordBottomView.setClickable(false);
        }

        private OnTouchListener mOnTouchListener = new OnTouchListener() {
            //开始录制时间
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        if (mOnRecordTouchListener != null)
                            mOnRecordTouchListener.onStartRecordAudio();
                        updateRecordButtonResource(RECORDING);
                        break;
                    case MotionEvent.ACTION_MOVE:
                        if(mAudioRecorder.isPause()) updateRecordButtonResource(LONG_CLICK_RECORD);return false;
                    case MotionEvent.ACTION_UP:
                        if (mOnRecordTouchListener != null)
                            mOnRecordTouchListener.onPauseRecordAudio();
                        updateRecordButtonResource(LONG_CLICK_RECORD);
                        break;
                    default:
                        return false;
                }
                return false;
            }
        };

        //点击回调
        private OnClickListener mOnClickListener = new OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.lsq_record_deleted:
                        if (mOnRecordTouchListener != null)
                            mOnRecordTouchListener.onDeletedSegment();
                        break;
                    case R.id.lsq_record_sure:
                        if (mOnRecordTouchListener != null) mOnRecordTouchListener.onNextStep();
                        break;

                }
            }
        };

        /**
         * 切换录音的类型
         *
         * @param index
         */
        private void selectSoundType(int index) {

            int childCount = mSoundTypeBar.getChildCount();

            for (int i = 0; i < childCount; i++) {
                Button btn = (Button) mSoundTypeBar.getChildAt(i);
                int currentIndex = Integer.parseInt((String) btn.getTag());
                if (index == currentIndex) {
                    btn.setBackgroundResource(R.drawable.tusdk_edite_cut_speed_button_bg);
                    btn.setTextColor(getContext().getResources().getColor(R.color.lsq_editor_cut_select_font_color));
                } else {
                    btn.setBackgroundResource(0);
                    btn.setTextColor(getContext().getResources().getColor(R.color.lsq_color_white));
                }
            }
            mCurrentPos = index;
            mAudioRecorder.setSoundPitchType(mSoundTypes[index]);
        }

        /**
         * 改变录制按钮视图
         *
         * @param type
         */
        private void updateRecordButtonResource(int type) {
            switch (type) {
                case LONG_CLICK_RECORD:
                    mRecordBtn.setBackgroundResource(R.drawable.tusdk_view_widget_record_audio_unpressed);
                    mRecordBtn.setImageResource(0);
                    break;
                case RECORDING:
                    mRecordBtn.setBackgroundResource(R.drawable.tusdk_view_widget_record_pressed);
                    mRecordBtn.setImageResource(0);
                    break;

            }
        }

    }


}
