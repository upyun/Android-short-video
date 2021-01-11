package org.lasque.tusdkvideodemo.album;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import android.view.KeyEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import org.lasque.tusdk.api.image.TuSDKMediaMovieCompositionComposer;
import org.lasque.tusdk.core.TuSdk;
import org.lasque.tusdk.core.api.extend.TuSdkMediaProgress;
import org.lasque.tusdk.core.media.codec.extend.TuSdkMediaFormat;
import org.lasque.tusdk.core.media.codec.video.TuSdkVideoQuality;
import org.lasque.tusdk.core.seles.tusdk.TuSDKMediaTransitionWrap;
import org.lasque.tusdk.core.struct.TuSdkMediaDataSource;
import org.lasque.tusdk.core.struct.TuSdkSize;
import org.lasque.tusdk.core.utils.ThreadHelper;
import org.lasque.tusdk.core.utils.sqllite.ImageSqlInfo;
import org.lasque.tusdk.video.editor.TuSdkMediaTransitionEffectData;
import org.lasque.tusdk.video.editor.TuSdkTimeRange;
import com.upyun.shortvideo.R;
import org.lasque.tusdkvideodemo.ScreenAdapterActivity;
import org.lasque.tusdkvideodemo.editor.MovieEditorActivity;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import at.grabner.circleprogress.CircleProgressView;

import static android.media.MediaCodecInfo.CodecCapabilities.COLOR_FormatSurface;
import static org.lasque.tusdkvideodemo.album.ImageAlbumFragment.DEFAULT_IMAGE_SHOW_DURATION;

/**
 * TuSDK
 * $desc$
 *
 * @author H.ys
 * @Date $data$ $time$
 * @Copyright (c) 2019 tusdk.com. All rights reserved.
 */
public class ImagePreviewActivity extends ScreenAdapterActivity {

    private ImageView mImagePreviewView;

    //返回按钮
    private TextView mBackBtn;
    //下一步按钮
    private TextView mNextBtn;
    //视频添加
    private TextView mVideoAddBtn;

    private ImageSqlInfo mCurrentImage;

    private List<ImageSqlInfo> mImagePath;

    //转码进度视图
    private FrameLayout mLoadContent;
    private CircleProgressView mLoadProgress;

    private int mSelectMax;

    private boolean isEnable = true;

    private TuSDKMediaMovieCompositionComposer tuSDKMediaMovieCompositionComposer;

    private void startImageToVideo() {
        tuSDKMediaMovieCompositionComposer = new TuSDKMediaMovieCompositionComposer();
        tuSDKMediaMovieCompositionComposer.setImageSource(mImagePath);
        tuSDKMediaMovieCompositionComposer.setVideoFormat(TuSdkMediaFormat.buildSafeVideoEncodecFormat(TuSdkSize.create(1080, 1920), TuSdkVideoQuality.RECORD_HIGH1, COLOR_FormatSurface));
        tuSDKMediaMovieCompositionComposer.setMediaProgress(mMediaProgress);
        tuSDKMediaMovieCompositionComposer.setDuration(DEFAULT_IMAGE_SHOW_DURATION);
        addEffectData(mImagePath);
//        设置是否保存到相册,与设置OutputFilePath相冲突
//        tuSDKMediaMovieCompositionComposer.saveToAlbum(true);
//        tuSDKMediaMovieCompositionComposer.setOutpuFilePath(TuSdk.getAppTempPath().getPath() + "/LSQ_" + System.currentTimeMillis() + ".mp4");
        tuSDKMediaMovieCompositionComposer.startExport();


    }

    private void addEffectData(List<ImageSqlInfo> selectedVideoInfo) {
        for (int i = 1; i < selectedVideoInfo.size(); i++) {
            TuSdkMediaTransitionEffectData data = new TuSdkMediaTransitionEffectData(TuSDKMediaTransitionWrap.TuSDKMediaTransitionType.TuSDKMediaTransitionTypePullInLeft);
            long startTime = (long) ((i * DEFAULT_IMAGE_SHOW_DURATION * TuSdkTimeRange.BASE_TIME_US) - (TuSdkTimeRange.BASE_TIME_US / 5));
            long endTime = (long) (((i + 1) * DEFAULT_IMAGE_SHOW_DURATION * TuSdkTimeRange.BASE_TIME_US) + (TuSdkTimeRange.BASE_TIME_US / 5 * 4));
            data.setAtTimeRange(TuSdkTimeRange.makeTimeUsRange(startTime, endTime));
            tuSDKMediaMovieCompositionComposer.addMediaEffect(data);
        }
    }

    private TuSdkMediaProgress mMediaProgress = new TuSdkMediaProgress() {
        @Override
        public void onProgress(final float progress, TuSdkMediaDataSource mediaDataSource, int index, int total) {
            ThreadHelper.post(new Runnable() {
                @Override
                public void run() {
                    mLoadContent.setVisibility(View.VISIBLE);
                    mLoadProgress.setValue(progress * 100);
                    setEnable(false);
                    if (progress >= 1) {
                        mLoadContent.setVisibility(View.GONE);
                        mLoadProgress.setValue(0);
                        setEnable(true);
                    }
                }
            });
        }

        @Override
        public void onCompleted(Exception e, TuSdkMediaDataSource outputFile, int total) {
            ThreadHelper.post(new Runnable() {
                @Override
                public void run() {
                    setEnable(true);
                    mLoadContent.setVisibility(View.GONE);
                    mLoadProgress.setValue(0);
                }
            });
            if (!new File(outputFile.getPath()).exists()) {
                TuSdk.messageHub().showToast(ImagePreviewActivity.this, R.string.lsq_not_file);
                return;
            }
            Intent intent = new Intent(ImagePreviewActivity.this, MovieEditorActivity.class);
            intent.putExtra("videoPath", outputFile.getPath());
            startActivity(intent);
            tuSDKMediaMovieCompositionComposer.cancelExport();
        }
    };

    private void setEnable(boolean b) {
        isEnable = b;
        mNextBtn.setEnabled(b);
        mVideoAddBtn.setEnabled(b);
        mBackBtn.setEnabled(b);
    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_preview);
        initView();
    }

    private void initView() {
        mBackBtn = findViewById(R.id.lsq_back);
        mBackBtn.setOnClickListener(mOnClickListener);
        mNextBtn = findViewById(R.id.lsq_next);
        mNextBtn.setOnClickListener(mOnClickListener);
        mVideoAddBtn = findViewById(R.id.lsq_video_add);
        mVideoAddBtn.setOnClickListener(mOnClickListener);
        mImagePreviewView = findViewById(R.id.lsq_image_preview);
        mCurrentImage = (ImageSqlInfo) getIntent().getSerializableExtra("currentImage");
        mImagePath = (ArrayList<ImageSqlInfo>) getIntent().getSerializableExtra("imagePath");
        mSelectMax = getIntent().getIntExtra("selectMax", 1);
        Glide.with(this).load(mCurrentImage.path).into(mImagePreviewView);
        mLoadContent = findViewById(R.id.lsq_editor_cut_load);
        mLoadProgress = findViewById(R.id.lsq_editor_cut_load_parogress);
        if (mImagePath.size() >= mSelectMax && !contains(mImagePath, mCurrentImage)) {
            mVideoAddBtn.setVisibility(View.GONE);
        } else {
            mVideoAddBtn.setVisibility(View.VISIBLE);
            if (contains(mImagePath, mCurrentImage)) {
                mVideoAddBtn.setText(String.valueOf(mImagePath.indexOf(mCurrentImage) + 1));
                mVideoAddBtn.setBackground(getResources().getDrawable(R.drawable.edit_heckbox_sel));
            }
        }
    }

    /**
     * 返回上级
     */
    private void onBack() {
        Intent intentBack = getIntent();
        intentBack.putExtra("videoInfo", mImagePath.contains(mCurrentImage) ? mCurrentImage : null);
        setResult(101, intentBack);
        finish();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (!isEnable) {
            return false;
        }
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            onBack();
            return false;
        } else {
            return super.onKeyDown(keyCode, event);
        }
    }

    private boolean contains(List<ImageSqlInfo> imageInfos, ImageSqlInfo imageInfo) {
        for (ImageSqlInfo info : imageInfos) {
            if (info.path.equals(imageInfo.path)) {
                return true;
            }
        }
        return false;
    }


    /**
     * 点击事件监听
     */
    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.lsq_back:
                    //返回按钮
                    onBack();
                    break;
                case R.id.lsq_next:
                    if (mImagePath.size() < 3)
                        TuSdk.messageHub().showToast(ImagePreviewActivity.this, R.string.lsq_select_image_hint);
                    else {
                        startImageToVideo();
                    }
                    break;
                case R.id.lsq_video_add:
                    if (mImagePath.contains(mCurrentImage)) {
                        mImagePath.remove(mCurrentImage);
                        mVideoAddBtn.setText("");
                        mVideoAddBtn.setBackground(getResources().getDrawable(R.drawable.edit_heckbox_unsel_max));
                    } else {
                        mVideoAddBtn.setText(String.valueOf(mImagePath.size() + 1));
                        mVideoAddBtn.setBackground(getResources().getDrawable(R.drawable.edit_heckbox_sel));
                        mImagePath.add(mCurrentImage);
                    }
                    break;
            }
        }
    };
}
