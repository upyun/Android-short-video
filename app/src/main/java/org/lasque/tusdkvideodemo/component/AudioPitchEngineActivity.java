package org.lasque.tusdkvideodemo.component;

import android.graphics.Color;
import android.media.MediaCodec;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.lasque.tusdk.api.audio.preproc.processor.TuSdkAudioEngine;
import org.lasque.tusdk.api.audio.preproc.processor.TuSdkAudioPitchEngine;
import org.lasque.tusdk.core.TuSdkContext;
import org.lasque.tusdk.core.media.codec.audio.TuSdkAudioInfo;
import org.lasque.tusdk.core.media.codec.audio.TuSdkAudioRecord;
import org.lasque.tusdk.core.media.codec.audio.TuSdkAudioTrackImpl;
import org.lasque.tusdk.core.media.codec.audio.TuSdkMicRecord;
import org.lasque.tusdk.core.media.codec.extend.TuSdkMediaFormat;
import org.lasque.tusdk.core.utils.ThreadHelper;
import com.upyun.shortvideo.R;
import org.lasque.tusdkvideodemo.ScreenAdapterActivity;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.List;

import static org.lasque.tusdk.api.audio.preproc.processor.TuSdkAudioPitchEngine.TuSdkSoundPitchType.Girl;
import static org.lasque.tusdk.api.audio.preproc.processor.TuSdkAudioPitchEngine.TuSdkSoundPitchType.Lolita;
import static org.lasque.tusdk.api.audio.preproc.processor.TuSdkAudioPitchEngine.TuSdkSoundPitchType.Monster;
import static org.lasque.tusdk.api.audio.preproc.processor.TuSdkAudioPitchEngine.TuSdkSoundPitchType.Normal;
import static org.lasque.tusdk.api.audio.preproc.processor.TuSdkAudioPitchEngine.TuSdkSoundPitchType.Uncle;

/**
 * droid-sdk-video
 *
 * @author sprint
 * @Date 2018/11/28 02:45
 * @Copright (c) 2018 tusdk.com. All rights reserved.
 * <p>
 * 音频录制
 */
public class AudioPitchEngineActivity extends ScreenAdapterActivity {

    //返回按钮
    private Button mBackBtn;

    /** 开始录音按钮 */
    private Button mStartRecordButton;

    /** 结束录音按钮 */
    private Button mStopRecordButton;
    /** 音效视图 */
    private LinearLayout mSoundPitchTypeBar;
    /** 音效的顺序 **/
    private TuSdkAudioPitchEngine.TuSdkSoundPitchType[] mSoundTypes =
            new TuSdkAudioPitchEngine.TuSdkSoundPitchType[]{Monster, Uncle, Normal, Girl, Lolita};


    /** 生成的录音文件 */
    private File mOutputFile;
    private FileOutputStream mFileOutputStream;

    /** 录制的音频信息 */
    private TuSdkAudioInfo mInputAudioInfo;
    /** 麦克风录制 API */
    private TuSdkMicRecord mMicRecord;
    /** 音频 PCM 数据变调 API  */
    private TuSdkAudioPitchEngine mAudioPitchEngine;
    /** PCM 裸流播放 API */
    private TuSdkAudioTrackImpl mAudioTrack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio_engine_pitch_record);
        initView();
    }

    /** 初始化视图 */
    private void initView() {
        mBackBtn = findViewById(R.id.lsq_backButton);
        mBackBtn.setOnClickListener(mOnClickListener);

        TextView titleView = findViewById(R.id.lsq_titleView);
        titleView.setText(TuSdkContext.getString("lsq_audio_engine_pitch"));

        mSoundPitchTypeBar = findViewById(R.id.lsq_editor_audio_record_type_bar);
        for (int i = 0;i < mSoundPitchTypeBar.getChildCount(); i++) {
            View soudPitchBar = mSoundPitchTypeBar.getChildAt(i);
            soudPitchBar.setOnClickListener(mOnSoundPitchBarClickListener);
        }

        mStartRecordButton = (Button) findViewById(R.id.lsq_audio_record_btn);
        mStopRecordButton = (Button) findViewById(R.id.lsq_audio_stop_btn);
        mStartRecordButton.setOnClickListener(mOnClickListener);
        mStopRecordButton.setOnClickListener(mOnClickListener);

        disableBtns(Arrays.asList(mStopRecordButton));


        /** step1: 初始化 TuSdkMicRecord ，用以采集音频数据 */
        mInputAudioInfo = new TuSdkAudioInfo(TuSdkMediaFormat.buildSafeAudioEncodecFormat());
        mMicRecord = new TuSdkMicRecord(mInputAudioInfo);
        mMicRecord.setListener(mAudioRecordListener);

        /**  step2: 初始化 TuSdkAudioPitchEngine 用以实现音频变调 */
        mAudioPitchEngine = new TuSdkAudioPitchEngine(mInputAudioInfo);
        mAudioPitchEngine.setOutputBufferDelegate(mAudioPitchEngineOutputBufferDelegate);
        mAudioPitchEngine.setSoundPitchType(TuSdkAudioPitchEngine.TuSdkSoundPitchType.Normal);
        /**  step3: 初始化 AudioTrack 用以播放处理后的音频数据 */
        mAudioTrack = new TuSdkAudioTrackImpl(mInputAudioInfo);
        mAudioTrack.play();
    }

    /**
     *  TuSdkMicRecord 录制回调
      */
   private TuSdkAudioRecord.TuSdkAudioRecordListener mAudioRecordListener = new TuSdkAudioRecord.TuSdkAudioRecordListener() {

        /**
         * 麦克风采集输出的 PCM 数据
         * @param outputByteBuffer    PCM 数据
         * @param bufferInfo 数据信息
         */
       @Override
       public void onAudioRecordOutputBuffer(ByteBuffer outputByteBuffer, MediaCodec.BufferInfo bufferInfo) {

            mAudioPitchEngine.processInputBuffer(outputByteBuffer,bufferInfo);
       }

       @Override
       public void onAudioRecordError(int code) {
           enableBtns(Arrays.asList(mStartRecordButton));
       }
   };

   /** TuSdkAudioEngine 数据处理完成 */
   private TuSdkAudioEngine.TuSdKAudioEngineOutputBufferDelegate mAudioPitchEngineOutputBufferDelegate = new TuSdkAudioEngine.TuSdKAudioEngineOutputBufferDelegate() {

       /**
        * 处理后的音频数据  格式为：PCM
        * @param outputByteBuffer PCM 数据
        * @param bufferInfo 音频数据信息
        */
       @Override
       public void onProcess(ByteBuffer outputByteBuffer, MediaCodec.BufferInfo bufferInfo) {
           try {


               /** outputByteBuffer 为 JNI 层抛出，在这里必须使用 get 方式获取数据。不能直接使用 outputByteBuffer.array() */
               byte[] data = new byte[bufferInfo.size];
               outputByteBuffer.get(data);

               // 将处理后的音频数据写入文件
               // Demo 为了演示主要功能，未对 PCM 数据进行编码.
               mFileOutputStream.write(data);


           } catch (Exception e) {
               e.printStackTrace();
           }
       }
   };

    /**
     * 播放录制的音频文件
     */
    private void playAudio() {

        if(mOutputFile == null) return;

        try {

            /**  step3: 初始化 AudioTrack 用以播放处理后的音频数据 */
            mAudioTrack = new TuSdkAudioTrackImpl(mInputAudioInfo);
            /** 播放处理后的音频数据 */
            mAudioTrack.play();

            InputStream is = new FileInputStream(mOutputFile);
            BufferedInputStream bis = new BufferedInputStream(is);

            byte[] bytes = new byte[mAudioTrack.getBufferSize()];
            int readSize = 0;
            while ((readSize = bis.read(bytes, 0, bytes.length)) > 0) {
                mAudioTrack.write(ByteBuffer.wrap(bytes));
            }

            mAudioTrack.pause();

        } catch (Throwable t) {

        } finally {

            ThreadHelper.post(new Runnable() {
                @Override
                public void run() {
                    enableBtns(Arrays.asList(mStartRecordButton));
                }
            });
        }
    }

    /**
     * 切换音效类型
     *
     * @param index
     */
    private void selectSoundType(int index) {

        int childCount = mSoundPitchTypeBar.getChildCount();

        for (int i = 0; i < childCount; i++) {
            Button btn = (Button) mSoundPitchTypeBar.getChildAt(i);
            int currentIndex = Integer.parseInt((String) btn.getTag());
            if (index == currentIndex) {
                btn.setBackgroundResource(R.drawable.tusdk_edite_cut_speed_button_bg);
                btn.setTextColor(this.getResources().getColor(R.color.lsq_editor_cut_select_font_color));
            } else {
                btn.setBackgroundResource(0);
                btn.setTextColor(this.getResources().getColor(R.color.lsq_color_white));
            }
        }

        mAudioPitchEngine.setSoundPitchType(mSoundTypes[index]);
    }

    /** 切换声调事件 */
    private View.OnClickListener mOnSoundPitchBarClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            selectSoundType(Integer.parseInt((String) v.getTag()));
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

                    try {

                        if (mOutputFile != null)
                            mOutputFile.delete();

                        mOutputFile = new File(Environment.getExternalStorageDirectory(),"tusdkAudioEnginePitch.pcm");
                        mOutputFile.createNewFile();
                        mFileOutputStream = new FileOutputStream(mOutputFile);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    disableBtns(Arrays.asList(mStartRecordButton));
                    enableBtns(Arrays.asList(mStopRecordButton));

                    mMicRecord.startRecording();

                    break;
                case R.id.lsq_audio_stop_btn:

                    enableBtns(Arrays.asList(mStartRecordButton));
                    disableBtns(Arrays.asList(mStopRecordButton));

                    try {
                        if (mFileOutputStream != null) {
                            mFileOutputStream.flush();
                            mFileOutputStream.close();
                            mFileOutputStream = null;
                        }

                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    mMicRecord.stop();
                    mAudioPitchEngine.flush();

                    disableBtns(Arrays.asList(mStartRecordButton,mStopRecordButton));

                    ThreadHelper.runThread(new Runnable() {
                        @Override
                        public void run() {
                            playAudio();
                        }
                    });

                    break;
            }
        }
    };



    /** 将指定的按钮置位不可用  */
    private void disableBtns(List<Button> btns) {

        if (btns == null) return;

        for (Button button : btns) {
            button.setClickable(false);
            button.setBackgroundColor(Color.GRAY);
        }

    }

    /** 将指定的按钮置位可用  */
    private void enableBtns(List<Button> btns) {
        if (btns == null)return;
        for (Button button : btns) {
            button.setClickable(true);
            button.setBackgroundResource(R.drawable.tusdk_view_api_button_roundcorner);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mAudioTrack != null)
            mAudioTrack.pause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mMicRecord != null){
            mMicRecord.stop();
            mMicRecord.release();
            mMicRecord = null;
        }

        if (mAudioTrack != null)
            mAudioTrack.release();
    }
}
