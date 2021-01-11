package org.lasque.tusdkvideodemo.component;

import android.content.Intent;
import android.media.MediaCodecInfo;
import android.media.MediaFormat;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.lasque.tusdk.api.movie.player.TuSDKMoviePlayer;
import org.lasque.tusdk.core.TuSdkContext;
import org.lasque.tusdk.core.api.extend.TuSdkMediaProgress;
import org.lasque.tusdk.core.common.TuSDKMediaUtils;
import org.lasque.tusdk.core.decoder.TuSDKVideoInfo;
import org.lasque.tusdk.core.media.codec.extend.TuSdkMediaFormat;
import org.lasque.tusdk.core.media.suit.TuSdkMediaSuit;
import org.lasque.tusdk.core.struct.TuSdkMediaDataSource;
import org.lasque.tusdk.core.struct.TuSdkSize;
import org.lasque.tusdk.core.utils.StringHelper;
import org.lasque.tusdk.core.utils.TLog;
import org.lasque.tusdk.core.utils.image.AlbumHelper;
import org.lasque.tusdk.core.utils.image.ImageOrientation;
import org.lasque.tusdk.core.view.widget.button.TuSdkNavigatorBackButton;
import com.upyun.shortvideo.R;
import org.lasque.tusdkvideodemo.ScreenAdapterActivity;
import org.lasque.tusdkvideodemo.album.MovieInfo;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import at.grabner.circleprogress.CircleProgressView;

import static android.widget.Toast.LENGTH_SHORT;

/**
 * droid-sdk-video
 *
 * @author MirsFang
 * @Date 2018/9/13 10:51
 * @Copright (c) 2018 tusdk.com. All rights reserved.
 * <p>
 * 视频拼接
 */
public class MovieSplicerActivity extends ScreenAdapterActivity {

    /** 用于记录片段路径信息 */
    private List<TuSdkMediaDataSource> mMoviePathList = new ArrayList<>();
    /** 第一个视频播放器 */
    private TuSDKMoviePlayer mMediaPlayerOne;
    /** 第二个视频播放器 */
    private TuSDKMoviePlayer mMediaPlayerTwo;
    /** 输入视频路径 */
    private String mInputPath;
    private List<MovieInfo> movieInfos;
    /** 保存路径 */
    private String mMuxerResultPath;

    /** 返回按钮 */
    private TuSdkNavigatorBackButton mBackBtn;
    /** 合成视频 Button */
    private Button mMuxerButton;
    /** 第一个视频预览界面 */
    private SurfaceView mPreViewOne;
    /** 第二个视频预览界面 */
    private SurfaceView mPreViewTwo;
    /** 进度的Content **/
    private FrameLayout mProgressContent;
    /** 拼接进度 */
    private CircleProgressView mCircleView;

    /** 第一个视频的回调方法 **/
    private TuSDKMoviePlayer.TuSDKMoviePlayerDelegate mFirstPlayerDelegate = new TuSDKMoviePlayer.TuSDKMoviePlayerDelegate() {
        @Override
        public void onStateChanged(TuSDKMoviePlayer.PlayerState state) {

        }

        @Override
        public void onVideSizeChanged(MediaPlayer mp, int width, int height) {
            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) mPreViewOne.getLayoutParams();
            float proportion = (float) width / (float) height;
            layoutParams.leftMargin = (TuSdkContext.getScreenSize().width - layoutParams.width) / 2;
            layoutParams.width = (int) (layoutParams.height * proportion);
            mPreViewOne.setLayoutParams(layoutParams);
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
    /** 第二个视频的回调方法 **/
    private TuSDKMoviePlayer.TuSDKMoviePlayerDelegate mSecondPlayerDelegate = new TuSDKMoviePlayer.TuSDKMoviePlayerDelegate() {
        @Override
        public void onStateChanged(TuSDKMoviePlayer.PlayerState state) {

        }

        @Override
        public void onVideSizeChanged(MediaPlayer mp, int width, int height) {
            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) mPreViewTwo.getLayoutParams();
            float proportion = (float) width / (float) height;
            layoutParams.leftMargin = (TuSdkContext.getScreenSize().width - layoutParams.width) / 2;
            layoutParams.width = (int) (layoutParams.height * proportion);
            mPreViewTwo.setLayoutParams(layoutParams);
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_splicer);
        initView();

    }

    public void initView() {
        Intent intent = getIntent();
        if (intent.hasExtra("videoPaths")) {
            movieInfos = (List<MovieInfo>) getIntent().getSerializableExtra("videoPaths");
            mInputPath = movieInfos.get(0).getPath();
        }

        mBackBtn = findViewById(R.id.lsq_backButton);
        mBackBtn.setOnClickListener(mOnClickListener);
        TextView titleView = findViewById(R.id.lsq_titleView);
        titleView.setText(TuSdkContext.getString("lsq_movie_splicer_text"));

        mProgressContent = findViewById(R.id.lsq_movie_splicer_prgress_content);

        mCircleView = findViewById(R.id.lsq_movie_splicer_progress);
        mProgressContent.setVisibility(View.GONE);

        mMuxerButton = this.findViewById(R.id.lsq_movie_mixer_btn);
        mMuxerButton.setOnClickListener(mOnClickListener);
        mPreViewOne = findViewById(R.id.lsq_movie_preview_one);
        mPreViewTwo = findViewById(R.id.lsq_movie_preview_two);
        initMediaPlayer();
    }

    private void initMediaPlayer() {
        mMediaPlayerOne = TuSDKMoviePlayer.createMoviePlayer();
        mMediaPlayerOne.setDelegate(mFirstPlayerDelegate);
        mMediaPlayerOne.setLooping(true);
        String firstPath = mInputPath;
        Uri firstVideoUri = Uri.parse(firstPath);
        mMoviePathList.add(new TuSdkMediaDataSource(firstPath));
        mMediaPlayerOne.initVideoPlayer(this, firstVideoUri, mPreViewOne);

        mMediaPlayerTwo = TuSDKMoviePlayer.createMoviePlayer();
        mMediaPlayerTwo.setLooping(true);
        mMediaPlayerTwo.setDelegate(mSecondPlayerDelegate);
        String secondPath = movieInfos.size() <= 1 ? mInputPath : movieInfos.get(1).getPath();
        Uri secondVideoUri = Uri.parse(secondPath);
        mMoviePathList.add(new TuSdkMediaDataSource(secondPath));
        mMediaPlayerTwo.initVideoPlayer(this, secondVideoUri, mPreViewTwo);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mMediaPlayerOne.start();
        mMediaPlayerTwo.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mMediaPlayerOne.stop();
        mMediaPlayerTwo.stop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mMediaPlayerOne.destory();
        mMediaPlayerTwo.destory();
        mMediaPlayerOne = null;
        mMediaPlayerTwo = null;
    }

    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.lsq_backButton:
                    finish();
                    break;
                case R.id.lsq_movie_mixer_btn:
                    handleMuxerMovieFragmentData(getMovieResultPath());
                    break;
            }
        }
    };

    /**
     * 获取视频保存的路径
     *
     * @return
     */
    private String getMovieResultPath() {
        // 每次点击保存视频时，根据时间戳生成路径
        mMuxerResultPath = new File(AlbumHelper.getAblumPath(),
                String.format("lsq_%s.mp4", StringHelper.timeStampString())).toString();
        return mMuxerResultPath;
    }

    /**
     * 音视频合成处理
     *
     * @param muxerPath
     */
    private void handleMuxerMovieFragmentData(String muxerPath) {
        TuSDKVideoInfo videoInfo = TuSDKMediaUtils.getVideoInfo(mInputPath);
        MediaFormat ouputVideoFormat = getOutputVideoFormat(videoInfo);
        MediaFormat ouputAudioFormat = getOutputAudioFormat();

        TuSdkMediaSuit.merge(mMoviePathList, muxerPath, ouputVideoFormat, ouputAudioFormat, mediaProgress);
        mProgressContent.setVisibility(View.VISIBLE);
        Toast.makeText(MovieSplicerActivity.this, R.string.lsq_movie_splicer_processing, LENGTH_SHORT).show();
    }

    /**
     * 视频拼接状态通知
     */
    private TuSdkMediaProgress mediaProgress = new TuSdkMediaProgress() {
        @Override
        public void onProgress(float progress, TuSdkMediaDataSource mediaDataSource, int index,
                               int total) {
            TLog.i("onProgressChanged: " + progress);
            mCircleView.setValue((progress * 100));
        }

        @Override
        public void onCompleted(Exception e, TuSdkMediaDataSource outputFile, int total) {
            if (e == null) {
                Toast.makeText(MovieSplicerActivity.this, R.string.lsq_movie_splicer_success, LENGTH_SHORT).show();
            } else {
                Toast.makeText(MovieSplicerActivity.this, R.string.lsq_movie_splicer_error, LENGTH_SHORT).show();
            }
            hideCircleView();
        }
    };

    /**
     * 隐藏进度
     */
    private void hideCircleView() {
        mProgressContent.setVisibility(View.GONE);
        mCircleView.setText("0%");
        mCircleView.setValue(0);
    }

    private int mFps = 0;
    private int mBitrate = 0;

    /**
     * 获取输出文件的视频格式信息
     *
     * @return MediaFormat
     */
    protected MediaFormat getOutputVideoFormat(TuSDKVideoInfo videoInfo) {
        int fps = mFps == 0 ? videoInfo.fps : mFps;
        int bitrate = mBitrate == 0 ? videoInfo.bitrate : mBitrate;
        TuSdkSize videoSize = TuSdkSize.create(videoInfo.width, videoInfo.height);

        if (videoInfo.videoOrientation == ImageOrientation.Right || videoInfo.videoOrientation == ImageOrientation.Left)
            videoSize = TuSdkSize.create(videoSize.height, videoSize.width);

        MediaFormat mediaFormat = TuSdkMediaFormat.buildSafeVideoEncodecFormat(videoSize.width, videoSize.height,
                fps, bitrate, MediaCodecInfo.CodecCapabilities.COLOR_FormatSurface, 0, 1);

        return mediaFormat;
    }

    /**
     * 获取输出的音频格式信息
     *
     * @return MediaFormat
     */
    protected MediaFormat getOutputAudioFormat() {
        MediaFormat audioFormat = TuSdkMediaFormat.buildSafeAudioEncodecFormat();
        return audioFormat;
    }
}
