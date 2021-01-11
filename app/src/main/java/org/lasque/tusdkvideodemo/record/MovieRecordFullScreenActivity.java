package org.lasque.tusdkvideodemo.record;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;


import org.lasque.tusdk.core.TuSdk;
import org.lasque.tusdk.core.media.codec.extend.TuSdkMediaTimeSlice;
import org.lasque.tusdk.core.media.codec.video.TuSdkVideoQuality;
import org.lasque.tusdk.core.struct.TuSdkSize;
import org.lasque.tusdk.core.utils.TLog;
import org.lasque.tusdk.core.utils.ThreadHelper;
import org.lasque.tusdk.core.utils.hardware.CameraConfigs;
import org.lasque.tusdk.core.utils.hardware.TuSdkRecorderVideoCamera;
import org.lasque.tusdk.core.utils.hardware.TuSdkRecorderVideoEncoderSetting;
import org.lasque.tusdk.core.video.TuSDKVideoResult;

import com.upyun.library.common.Params;
import com.upyun.library.common.SerialUploader;
import com.upyun.library.common.UploadEngine;
import com.upyun.library.listener.UpCompleteListener;
import com.upyun.library.listener.UpProgressListener;
import com.upyun.library.utils.UpYunUtils;
import com.upyun.shortvideo.R;

import org.lasque.tusdkvideodemo.SimpleCameraActivity;
import org.lasque.tusdkvideodemo.editor.MovieEditorActivity;
import org.lasque.tusdkvideodemo.utils.Constants;
import org.lasque.tusdkvideodemo.views.record.RecordView;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Response;

/**
 *
 */
public class MovieRecordFullScreenActivity extends SimpleCameraActivity implements
        RecordView.TuSDKMovieRecordDelegate, TuSdkRecorderVideoCamera.TuSdkRecorderVideoCameraCallback {
    // 录制界面视图
    protected RecordView mRecordView;

    protected int getLayoutId() {
        return R.layout.activity_new_record_full_screen;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(getLayoutId());

        initCamera();

        getRecordView();

        // 设置录制界面背景为透明色
//        hideNavigationBar();
        TuSdk.messageHub().applyToViewWithNavigationBarHidden(true);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mRecordView.onResume();
//        setWindowBrightness(255);
    }

    @Override
    protected void onPause() {
        // 同步闪关灯状态
        getRecordView().updateFlashMode(CameraConfigs.CameraFlash.Off);
        mVideoCamera.pauseRecording();
        mVideoCamera.cancelRecording();
        super.onPause();
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                getRecordView().initRecordProgress();
            }
        });
    }

    /**
     * 录制界面视图
     */
    protected RecordView getRecordView() {
        if (mRecordView == null) {
            mRecordView = (RecordView) findViewById(R.id.lsq_movie_record_view);
            mRecordView.setDelegate(this);
            mRecordView.setUpCamera(this, mVideoCamera);
            mRecordView.init(getSupportFragmentManager(), getLifecycle());
            mRecordView.initFilterGroupsViews(getSupportFragmentManager(), getLifecycle(), Constants.getCameraFilters(true));
        }
        return mRecordView;
    }

    private void setWindowBrightness(int brightness) {
        Window window = getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.screenBrightness = brightness / 255.0f;
        window.setAttributes(lp);
    }

    protected void initCamera() {
        super.initCamera();
        if (getIntent() != null && getIntent().hasExtra("isDirectEdit")) {
            mVideoCamera.enableDirectEdit(getIntent().getBooleanExtra("isDirectEdit", false));
        }
        // 设置相机事件回调
        mVideoCamera.setRecorderVideoCameraCallback(this);
        mVideoCamera.setMinRecordingTime(Constants.MIN_RECORDING_TIME);
        mVideoCamera.setMaxRecordingTime(Constants.MAX_RECORDING_TIME);
        // 设置使用录制相机最小空间限制,开发者可根据需要自行设置（默认：50M）
        mVideoCamera.setMinAvailableSpaceBytes(1024 * 1024 * 50l);

        // 开启人脸检测 开启后方可使用人脸贴纸及微整形功能
        mVideoCamera.setEnableFaceDetection(true);

        // 编码配置
        TuSdkRecorderVideoEncoderSetting encoderSetting = TuSdkRecorderVideoEncoderSetting.getDefaultRecordSetting();
        // 输出全屏尺寸
        encoderSetting.videoSize = TuSdkSize.create(1080, 1920);
        // 这里可以修改帧率和码率; RECORD_MEDIUM2第一个参数代表帧率，第二参数代表码率;选择VideoQuality参数尽量选用RECORD开头(专门为视频录制设计)
        encoderSetting.videoQuality = TuSdkVideoQuality.RECORD_HIGH3;

        mVideoCamera.setVideoEncoderSetting(encoderSetting);

    }

    /**
     * ----------- 注意事项：如果视频录制完成后需要跳转到视频编辑页面,需要将录制视频页面销毁掉; 视频编辑跳转视频录制也是如此 ---------------------------
     */
    @Override
    public void onMovieRecordComplete(final TuSDKVideoResult result) {
        if (!mVideoCamera.isDirectEdit()) {
            mRecordView.updateViewOnMovieRecordComplete(isRecording());
            //上传至 upyun
//            formUpload(result.videoPath);
            resumeUpload(result.videoPath);
        } else {
            final ArrayList<TuSdkMediaTimeSlice> recordTimeSlices = mVideoCamera.getRecordTimeSlice();
            ThreadHelper.postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent intent = new Intent(MovieRecordFullScreenActivity.this, MovieEditorActivity.class);
                    intent.putExtra("isDirectEdit", mVideoCamera.isDirectEdit());
                    intent.putExtra("videoPath", result.videoPath.getAbsolutePath());
                    intent.putExtra("timeRange", recordTimeSlices);
                    startActivity(intent);
                    finishRecordActivity();
                }
            }, 500);
        }
    }

    //表单上传
    private void formUpload(File file) {

        final String TAG = "formUpload";

        //空间名
        String SPACE = "formtest";
        //操作员
        String OPERATER = "one";
        //密码
        String PASSWORD = "***";

        //上传路径
        String savePath = "/uploads/{year}{mon}{day}/{random32}{.suffix}";

        final Map<String, Object> paramsMap = new HashMap<>();
        //上传空间
        paramsMap.put(Params.BUCKET, SPACE);
        //保存路径
        paramsMap.put(Params.SAVE_KEY, savePath);
        //添加 CONTENT_LENGTH 参数使用大文件表单上传
        paramsMap.put(Params.CONTENT_LENGTH, file.length());

        //可选参数（详情见api文档介绍）
        paramsMap.put(Params.CONTENT_MD5, UpYunUtils.md5Hex(file));
        paramsMap.put(Params.RETURN_URL, "httpbin.org/post");

        //上传结果回调
        UpCompleteListener completeListener = new UpCompleteListener() {
            @Override
            public void onComplete(boolean isSuccess, Response response, Exception error) {
                Log.d(TAG, isSuccess + ":" + response + ":" + error);

            }
        };

        //进度条回调
        UpProgressListener progressListener = new UpProgressListener() {
            @Override
            public void onRequestProgress(final long bytesWrite, final long contentLength) {
                Log.d(TAG, (100 * bytesWrite) / contentLength + "%");
            }
        };

        UploadEngine.getInstance().formUpload(file, paramsMap, OPERATER, UpYunUtils.md5(PASSWORD), completeListener, progressListener);
    }


    //断点续传
    private void resumeUpload(File file) {

        final String TAG = "resumeUpload";

        //空间名
        String SPACE = "formtest";
        //操作员
        String OPERATER = "one";
        //密码
        String PASSWORD = "***";

        //上传路径
        String path = "/test.mp4";

        //初始化断点续传
        SerialUploader uploader = new SerialUploader(SPACE, OPERATER, UpYunUtils.md5(PASSWORD));

        //设置 MD5 校验
        uploader.setCheckMD5(true);

        //设置进度监听
        uploader.setOnProgressListener(new UpProgressListener() {
            @Override
            public void onRequestProgress(long bytesWrite, long contentLength) {
                Log.d(TAG, bytesWrite + ":" + contentLength);
            }
        });

        uploader.upload(file, path, null, new UpCompleteListener() {
            @Override
            public void onComplete(boolean isSuccess, Response response, Exception error) {
                Log.d(TAG, isSuccess + ":" + response + ":" + error);
            }
        });
    }


    @Override
    public void onMovieRecordProgressChanged(float progress,
                                             float durationTime) {
        mRecordView.updateViewOnMovieRecordProgressChanged(progress, durationTime);
    }

    @Override
    public void onMovieRecordStateChanged(TuSdkRecorderVideoCamera.RecordState state) {
        mRecordView.updateMovieRecordState(state, isRecording());
    }

    @Override
    public void onMovieRecordFailed(TuSdkRecorderVideoCamera.RecordError error) {
        TLog.e("RecordError : %s", error);
        mRecordView.updateViewOnMovieRecordFailed(error, isRecording());
    }


    @Override
    public void stopRecording() {
        if (mVideoCamera.isRecording()) {
            mVideoCamera.stopRecording();
        }
//        mRecordView.updateViewOnStopRecording(mVideoCamera.isRecording());
    }

    @Override
    public void pauseRecording() {
        mVideoCamera.pauseRecording();
    }

    @Override
    public void startRecording() {
        if (!mVideoCamera.isRecording()) {
            mVideoCamera.startRecording();
        }

//        mRecordView.updateViewOnStartRecording(mVideoCamera.isRecording());
    }

    @Override
    public boolean isRecording() {
        return mVideoCamera.isRecording();
    }

    @Override
    public void finishRecordActivity() {
        this.finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
