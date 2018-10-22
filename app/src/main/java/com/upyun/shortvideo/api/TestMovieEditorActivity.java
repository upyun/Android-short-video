package com.upyun.shortvideo.api;

import android.app.Activity;
import android.media.MediaCodec;
import android.media.MediaFormat;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;

import org.lasque.tusdk.core.api.extend.TuSdkAudioRender;
import org.lasque.tusdk.core.api.extend.TuSdkMediaProgress;
import org.lasque.tusdk.core.common.TuSDKMediaUtils;
import org.lasque.tusdk.core.decoder.TuSDKAudioDecoderTaskManager;
import org.lasque.tusdk.core.decoder.TuSDKVideoInfo;
import org.lasque.tusdk.core.media.codec.extend.TuSdkMediaFormat;
import org.lasque.tusdk.core.media.codec.extend.TuSdkMediaTimeline;
import org.lasque.tusdk.core.media.codec.suit.TuSdkMediaFileCuterImpl;
import org.lasque.tusdk.core.media.codec.sync.TuSdkMediaFileDirectorSync;
import org.lasque.tusdk.core.media.codec.video.TuSdkVideoQuality;
import org.lasque.tusdk.core.seles.sources.SelesWatermark;
import org.lasque.tusdk.core.seles.sources.SelesWatermarkImpl;
import org.lasque.tusdk.core.seles.sources.TuSdkEditorAudioMixer;
import org.lasque.tusdk.core.seles.sources.TuSdkEditorAudioMixerImpl;
import org.lasque.tusdk.core.seles.sources.TuSdkEditorEffector;
import org.lasque.tusdk.core.seles.sources.TuSdkEditorEffectorImpl;
import org.lasque.tusdk.core.seles.sources.TuSdkEditorPlayer;
import org.lasque.tusdk.core.seles.sources.TuSdkEditorPlayerImpl;
import org.lasque.tusdk.core.seles.sources.TuSdkEditorTranscoder;
import org.lasque.tusdk.core.seles.sources.TuSdkEditorTranscoderImpl;
import org.lasque.tusdk.core.seles.sources.TuSdkMovieEditorImpl;
import org.lasque.tusdk.core.struct.TuSdkMediaDataSource;
import org.lasque.tusdk.core.struct.TuSdkSize;
import org.lasque.tusdk.core.utils.TLog;
import org.lasque.tusdk.core.utils.TuSdkWaterMarkOption;
import org.lasque.tusdk.core.utils.image.BitmapHelper;
import org.lasque.tusdk.modules.view.widget.sticker.StickerGroup;
import org.lasque.tusdk.modules.view.widget.sticker.StickerLocalPackage;
import org.lasque.tusdk.video.editor.TuSDKMediaEffectData;
import org.lasque.tusdk.video.editor.TuSDKMediaFilterEffectData;
import org.lasque.tusdk.video.editor.TuSDKMediaRepeatTimeEffect;
import org.lasque.tusdk.video.editor.TuSDKMediaReversalTimeEffect;
import org.lasque.tusdk.video.editor.TuSDKMediaSceneEffectData;
import org.lasque.tusdk.video.editor.TuSDKMediaStickerAudioEffectData;
import org.lasque.tusdk.video.editor.TuSDKTimeRange;
import com.upyun.shortvideo.R;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TestMovieEditorActivity extends Activity {
    private static final String TAG = "TestMovieEditorActivity";
    private RelativeLayout mContent;
    // 转码按钮
    private Button mTransCodeBtn;
    // 播放按钮
    private Button mPlayerBtn;

    // 转码好的视频
    private String mTestVideoPath = Environment.getExternalStorageDirectory().getPath() + "/sssssssss.mp4";

    private String mInputPath;

    private TuSdkMediaDataSource mMediaDataSource;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_movie_editor);

        mContent = findViewById(R.id.lsq_content);
        mTransCodeBtn = findViewById(R.id.lsq_btn_transCode);
        lsq_edit_seek = findViewById(R.id.lsq_edit_seek);

        mInputPath = getIntent().getStringExtra("videoPath");
        mMediaDataSource = new TuSdkMediaDataSource(mInputPath);

        mTransCodeBtn.setOnClickListener(mClickListener);

        testEditorPlayer();

//        TuSDKMediaUtils.testMediaCodecInfo("video/avc");
    }

    /** 测试编辑器 */
    public void testMovieEditor() {
        movieEditor = new TuSdkMovieEditorImpl(this, mContent, null);
        movieEditor.setDataSource(mMediaDataSource);
        movieEditor.loadVideo();
    }

    TuSdkMovieEditorImpl movieEditor;
    private View.OnClickListener mClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.lsq_btn_transCode:
                    //转码
                    TuSDKMediaFilterEffectData filterEffectData = new TuSDKMediaFilterEffectData("Gold_1");
                    filterEffectData.setAtTimeRange(TuSDKTimeRange.makeRange(0, Float.MAX_VALUE));
                    movieEditor.getEditorEffector().addMediaEffectData(filterEffectData);
                    movieEditor.getEditorPlayer().startPreview();
                    break;
            }
        }
    };

    /********************                   播放器测试                       *****************************/
    TuSdkEditorPlayerImpl mPlayer;
    private long mSeekTime;
    private EditText lsq_edit_seek;
    TuSdkEditorPlayer.TuSdkProgressListener mPlayProgressListener = new TuSdkEditorPlayer.TuSdkProgressListener() {
        @Override
        public void onStateChanged(int state) {

        }

        @Override
        public void onProgress(long playbackTimeUs, long totalTimeUs, float percentage) {
        }
    };

    /** 测试播放器 */
    public void testEditorPlayer() {
        mPlayer = new TuSdkEditorPlayerImpl(this);
        mPlayer.setPreViewContent(mContent);
        mPlayer.setDataSource(mMediaDataSource);
        mPlayer.addProgressListener(mPlayProgressListener);

        TuSDKVideoInfo videoInfo = TuSDKMediaUtils.getVideoInfo(mMediaDataSource);
        TLog.e("MirsFang : %s",videoInfo);

        //测试效果器
        testEffector();

        mPlayer.loadVideo();
    }

    /** 开始播放 */
    public void onStartPlay(View view) {
        if (mPlayer == null) return;
        TLog.e("%s onStartPlay()  before : %s", TAG, mPlayer.getCurrentSampleTimeUs());
        mPlayer.startPreview();
        TLog.e("%s onStartPlay()  after : %s", TAG, mPlayer.getCurrentSampleTimeUs());
    }

    /** 暂停 */
    public void onPausePlay(View view) {
        if (mPlayer == null) return;
        TLog.e("%s onPausePlay()  before : %s", TAG, mPlayer.getCurrentSampleTimeUs());
        mPlayer.pausePreview();
        TLog.e("%s onPausePlay()  after : %s", TAG, mPlayer.getCurrentSampleTimeUs());
    }

    /** seek */
    public void onSeek(View view) {
        mSeekTime = Long.valueOf(lsq_edit_seek.getText().toString());
        if (mPlayer == null) return;
        mPlayer.pausePreview();
        mPlayer.seekTimeUs(mSeekTime * 1000000);
        mPlayer.startPreview();
        TLog.e("%s onSeek()  seek : %s", TAG, mSeekTime * 1000000);
    }


    /****************        测试特效器           *******************/

    private TuSdkEditorEffector mEffector;
    private TuSdkEditorAudioMixer mMixer;
    private TuSDKAudioDecoderTaskManager.TuSDKAudioDecoderTaskStateListener mStateListener = new TuSDKAudioDecoderTaskManager.TuSDKAudioDecoderTaskStateListener() {
        @Override
        public void onStateChanged(TuSDKAudioDecoderTaskManager.State state) {
            mPlayer.pausePreview();
            if(state == TuSDKAudioDecoderTaskManager.State.Complete){
                mMixer.notifyLoadCompleted();
                mPlayer.startPreview();
            }
        }
    };

    private void testEffector() {
        mMixer = new TuSdkEditorAudioMixerImpl();
        mEffector = new TuSdkEditorEffectorImpl();

        mMixer.setDataSource(mMediaDataSource);
        mMixer.addTaskStateListener(mStateListener);


        mPlayer.setEffector(mEffector);

        mEffector.setAudioMixer(mMixer);
        mPlayer.setAudioMixer(mMixer);
        mPlayer.setAudioRender(mMixer.getMixerAudioRender());
//        mPlayer.setAudioMixerRender(mAudioRender);
    }

    private TuSdkAudioRender mAudioRender = new TuSdkAudioRender() {
        @Override
        public boolean onAudioSliceRender(ByteBuffer buffer, MediaCodec.BufferInfo bufferInfo, TuSdkAudioRenderCallback callback) {
            return false;
        }
    };

    /** 添加一个场景特效 **/
    public void addSceneEffect(View view) {
//        int length = Constants.SCENE_EFFECT_CODES.length;
//        int position = new Random(System.currentTimeMillis()).nextInt(length);
//        String code = Constants.SCENE_EFFECT_CODES[position];
//        TLog.e("%s addSceneEffect : %s ", TAG, code);
//        TuSDKMediaSceneEffectData sceneEffectData = new TuSDKMediaSceneEffectData(code);
//        sceneEffectData.setAtTimeRange(TuSDKTimeRange.makeRange(2, 6));
//        if (mEffector != null) mEffector.addMediaEffectData(sceneEffectData);

        TuSDKMediaFilterEffectData filter = new TuSDKMediaFilterEffectData("Olympus_1");
        filter.setAtTimeRange(TuSDKTimeRange.makeRange(0, Integer.MAX_VALUE));
        mEffector.addMediaEffectData(filter);
    }

    /** 移除一个场景特效 */
    public void removeSceneEffect(View view) {
        if (mEffector == null) return;
        List<TuSDKMediaSceneEffectData> effectList = mEffector.mediaEffectsWithType(TuSDKMediaEffectData.TuSDKMediaEffectDataType.TuSDKMediaEffectDataTypeScene);
        if (effectList == null || effectList.size() == 0) return;
        mEffector.removeMediaEffectData(effectList.get(0));
    }

    /** 添加一个MV特效 */
    public void addMVEffect(View view) {
        Map<Integer, Integer> musicMap = new HashMap<Integer, Integer>();

        musicMap.put(1420, R.raw.lsq_audio_cat);
        musicMap.put(1427, R.raw.lsq_audio_crow);
        musicMap.put(1432, R.raw.lsq_audio_tangyuan);
        musicMap.put(1446, R.raw.lsq_audio_children);

        List<StickerGroup> groups = new ArrayList<StickerGroup>();
        List<StickerGroup> smartStickerGroups = StickerLocalPackage.shared().getSmartStickerGroups(false);

        for (StickerGroup smartStickerGroup : smartStickerGroups) {
            if (musicMap.containsKey((int) smartStickerGroup.groupId))
                groups.add(smartStickerGroup);
        }

        Uri uri = Uri.parse("android.resource://org.lasque.tusdkvideodemo/2130968577");
        TuSDKMediaStickerAudioEffectData stickerAudioEffectDat = new TuSDKMediaStickerAudioEffectData(new TuSdkMediaDataSource(this, uri), groups.get(0));
        stickerAudioEffectDat.setAtTimeRange(TuSDKTimeRange.makeRange(0,Float.MAX_VALUE));
        stickerAudioEffectDat.getMediaAudioEffectData().getAudioEntry().setLooping(true);
        mEffector.addMediaEffectData(stickerAudioEffectDat);
    }

    /** 删除一个MV特效 */
    public void removeMVEffect(View view) {
        if(mEffector == null) return;
        mEffector.removeMediaEffectsWithType(TuSDKMediaEffectData.TuSDKMediaEffectDataType.TuSDKMediaEffectDataTypeSticker);
        mEffector.removeMediaEffectsWithType(TuSDKMediaEffectData.TuSDKMediaEffectDataType.TuSDKMediaEffectDataTypeAudio);
    }

    /** 倒序 */
    public void onRever(View view) {
        if(mEffector == null) return;

        TuSDKMediaReversalTimeEffect reversalTimeEffect = new TuSDKMediaReversalTimeEffect();
        reversalTimeEffect.setTimeRange(mPlayer.getTotalTimeUS(),0);
        mPlayer.setTimeEffect(reversalTimeEffect);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPlayer.destroy();
    }

    /** 反复 */
    public void onRepeatedly(View view)
    {
        if(mEffector == null) return;
        TLog.e("MirsFang debug onRepeatedly() ----------------------------------");

        TuSDKMediaRepeatTimeEffect repeatTimeEffect = new TuSDKMediaRepeatTimeEffect();
        repeatTimeEffect.setRepeatCount(2);
        repeatTimeEffect.setTimeRange(1000000,3000000);
        mPlayer.setTimeEffect(repeatTimeEffect);
//        mPlayer.setTimeLine(timeline);

        TuSdkMediaTimeline timeline = new TuSdkMediaTimeline();
        timeline.append(0,1000000);
//        timeline.append(1000000,3000000);
//        timeline.append(1000000,3000000);
//        timeline.append(1000000,3000000);

        timeline.append(1000000,3000000);
        timeline.append(1000000,3000000);
        timeline.append(1000000,3000000);

        timeline.append(2000000,7000000);
        mPlayer.preview(timeline);
    }

    /** 慢动作 */
    public void onSlow(View view)
    {
        TLog.e("MirsFang debug onSlow() ----------------------------------");
        if(mEffector == null) return;
        TuSdkMediaTimeline timeline = new TuSdkMediaTimeline();
        timeline.append(0,1000000);
        timeline.append(1000000,1500000,0.6f);
        timeline.append(1500000,5000000);
//        mPlayer.setTimeLine(timeline);
    }


    /** 快动作 */
    public void onQuickly(View view)
    {
        if(mEffector == null) return;
        TuSdkMediaTimeline timeline = new TuSdkMediaTimeline();
        timeline.append(0,2000000);
        timeline.append(2000000,4000000,1.5f);
        timeline.append(4000000,mPlayer.getTotalTimeUS());
//        mPlayer.setTimeLine(timeline);
    }


    /************       测试保存        ************/
    public void onSave(View view)
    {
        String outputPath = Environment.getExternalStorageDirectory().getAbsolutePath()+"/test001.mp4";

        TuSDKVideoInfo mVideoInfo = TuSDKMediaUtils.getVideoInfo(mMediaDataSource);

        MediaFormat videoFormat = TuSdkMediaFormat.buildSafeVideoSurfaceEncodecFormat(
                TuSdkSize.create(mVideoInfo.width, mVideoInfo.height),
                TuSdkVideoQuality.RECORD_MEDIUM2, false
        );
        MediaFormat audioFormat = TuSdkMediaFormat.buildSafeAudioEncodecFormat();

        TuSdkMediaFileCuterImpl cuter = new TuSdkMediaFileCuterImpl(new TuSdkMediaFileDirectorSync());
        cuter.setMediaDataSource(mMediaDataSource);
        cuter.setOutputVideoFormat(videoFormat);
        cuter.setOutputAudioFormat(audioFormat);

        SelesWatermark selesWatermark = new SelesWatermarkImpl(true);
        selesWatermark.setImage(BitmapHelper.getBitmapFormRaw(this, R.raw.sample_watermark),false);
        selesWatermark.setWaterPostion(TuSdkWaterMarkOption.WaterMarkPosition.Center);
        cuter.setWatermark(selesWatermark);

        TuSdkMediaTimeline timeSlice = new TuSdkMediaTimeline();
        timeSlice.append(1000000,6000000);

        cuter.setTimeline(timeSlice);

        cuter.setOutputFilePath(outputPath);
        cuter.run(new TuSdkMediaProgress() {
            @Override
            public void onProgress(float progress, TuSdkMediaDataSource mediaDataSource, int index, int total) {
                TLog.e("%s  onProgress() :%s",TAG,progress);
            }

            @Override
            public void onCompleted(Exception e, TuSdkMediaDataSource outputFile, int total) {
                TLog.e("%s  onCompleted() :%s",TAG,outputFile);
            }
        });
    }

    /** 倒序慢动作 */
    public void onReverseSlow(View view) {
        if(mEffector == null) return;
        TuSdkMediaTimeline timeline = new TuSdkMediaTimeline();
        timeline.append(0,1000000);
        timeline.append(2000000,1000000,0.6f);
        timeline.append(1000000,5000000);
//        mPlayer.setTimeLine(timeline);
    }

    public void onReverseQuick(View view) {
        if(mEffector == null) return;
        TuSdkMediaTimeline timeline = new TuSdkMediaTimeline();
        timeline.append(0,1000000);
        timeline.append(2000000,1000000,1.5f);
        timeline.append(1000000,5000000);
//        mPlayer.setTimeLine(timeline);
    }

    /** 转码 */
    public void onTransCoder(View view) {
        TuSdkEditorTranscoder transcoder = new TuSdkEditorTranscoderImpl();
        transcoder.setVideoDataSource(mMediaDataSource);
        transcoder.addTransCoderProgressListener(new TuSdkEditorTranscoder.TuSdkTranscoderProgressListener() {
            @Override
            public void onProgressChanged(float percentage) {
                TLog.e("MirsFang percentage:%s",percentage);
            }

            @Override
            public void onLoadComplete(TuSDKVideoInfo videoInfo, TuSdkMediaDataSource videoSource) {
                //测试播放器
                mMediaDataSource = videoSource;
                testEditorPlayer();
            }

            @Override
            public void onError(Exception e) {

            }
        });
        transcoder.startTransCoder();
    }
}
