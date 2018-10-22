package com.upyun.shortvideo.api;

import android.app.Activity;
import android.graphics.Color;
import android.media.MediaCodecInfo;
import android.media.MediaFormat;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import org.lasque.tusdk.api.movie.player.TuSDKMoviePlayer;
import org.lasque.tusdk.core.TuSdk;
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
import com.upyun.shortvideo.R;

import java.io.File;

import at.grabner.circleprogress.CircleProgressView;

import static android.widget.Toast.LENGTH_SHORT;

/**
 * 视频转码
 *
 * @author zuojindong
 */
public class MovieTranscodeActivity extends Activity
{
    // 默认的视频输出路径
    private final String DEFAULT_OUTPUT_MOVIE_PATH = getCompressedVideoPath();

    // 输入视频路径
    private String mInputPath = "";

    // 开始转码按钮
    private Button mStartTranscodeBtn;

    // 输出视频路径
    private String mOutputPath;

    // 返回按钮
    private TextView mBackBtn;

    // 视频播放器
    private TuSDKMoviePlayer mMoviePlayer;

    // 上一次播放的位置
    private int mLastPlayPosition;

    // 压缩进度
    private CircleProgressView mCircleView;

    // 帧率组
    private RadioGroup fpsGroup;

    // 码率组
    private RadioGroup bitrateGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.movie_transcode_activity);

        initView();
    }

    private void initView()
    {
        mInputPath = getIntent().getStringExtra("videoPath");

        mBackBtn = (TextView) findViewById(R.id.lsq_back);
        mBackBtn.setOnClickListener(mOnClickListener);
        TextView titleView = (TextView) findViewById(R.id.lsq_title);
        titleView.setText(TuSdkContext.getString("lsq_transcode_transcode"));
        TextView nextBtn = (TextView) findViewById(R.id.lsq_next);
        nextBtn.setVisibility(View.GONE);
        mStartTranscodeBtn = (Button) findViewById(R.id.lsq_start_transcode_btn);
        mStartTranscodeBtn.setOnClickListener(mOnClickListener);

        fpsGroup = findViewById(R.id.lsq_fps_group);
        fpsGroup.setOnCheckedChangeListener(checkedChangeListener);
        bitrateGroup = findViewById(R.id.lsq_bitrate_group);
        bitrateGroup.setOnCheckedChangeListener(checkedChangeListener);
        RadioButton fpsNormal = findViewById(R.id.lsq_fps_normal);
        fpsNormal.setChecked(true);
        RadioButton bitNormal = findViewById(R.id.lsq_bit_normal);
        bitNormal.setChecked(true);

        mCircleView = findViewById(R.id.circleView);
        mCircleView.setTextSize(50);
        mCircleView.setAutoTextSize(true);
        mCircleView.setTextColor(Color.WHITE);
        mCircleView.setVisibility(View.GONE);


        if(!new TuSdkMediaDataSource(mInputPath).isValid()) {
            TuSdk.messageHub().showToast(this, R.string.lsq_video_empty_error);
            return;
        }

        SurfaceView preview = (SurfaceView) findViewById(R.id.lsq_preview);
        fixMovieplayerSize(preview);
        iniMoviePlayer(preview);
    }

    private int mFps = 0;
    private int mBitrate = 0;
    /**
     * 视频格式单选框
     */
    private RadioGroup.OnCheckedChangeListener checkedChangeListener = new RadioGroup.OnCheckedChangeListener()
    {

        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId)
        {
            switch (checkedId)
            {
                case R.id.lsq_fps_normal:
                    mFps = 0;
                    break;
                case R.id.lsq_fps_10:
                    mFps = 10;
                    break;
                case R.id.lsq_fps_20:
                    mFps = 20;
                    break;
                case R.id.lsq_fps_30:
                    mFps = 30;
                    break;
                case R.id.lsq_bit_normal:
                    mBitrate = 0;
                    break;
                case R.id.lsq_bit_1000:
                    mBitrate = 1000*1000;
                    break;
                case R.id.lsq_bit_2000:
                    mBitrate = 2*1000*1000;
                    break;
                case R.id.lsq_bit_3000:
                    mBitrate = 3*1000*1000;
                    break;
            }
        }
    };

    /**
     * 动态设置播放器宽高
     * @param preview
     */
    private void fixMovieplayerSize(SurfaceView preview)
    {
        MediaMetadataRetriever retr = new MediaMetadataRetriever();
        retr.setDataSource(mInputPath);
        // 视频高度
        int height = Integer.valueOf(retr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT));
        // 视频宽度
        int width = Integer.valueOf(retr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH));
        int rotation = Integer.valueOf(retr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_ROTATION));
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) preview.getLayoutParams();
        if(rotation == 0 || rotation == 180)
        {
            params.width = params.height*width/height;
        }
        else
        {
            params.width = params.height*height/width;
        }

        preview.setLayoutParams(params);
    }

    private void iniMoviePlayer(SurfaceView surfaceView)
    {
        mMoviePlayer = TuSDKMoviePlayer.createMoviePlayer();
        mMoviePlayer.setLooping(true);
        mMoviePlayer.initVideoPlayer(this, Uri.parse(mInputPath), surfaceView);
        mMoviePlayer.setDelegate(mMoviePlayerDelegate);
    }

    private TuSDKMoviePlayer.TuSDKMoviePlayerDelegate mMoviePlayerDelegate = new TuSDKMoviePlayer.TuSDKMoviePlayerDelegate()
    {
        @Override
        public void onStateChanged(TuSDKMoviePlayer.PlayerState state)
        {
            if (state == TuSDKMoviePlayer.PlayerState.INITIALIZED)
            {
                mMoviePlayer.seekTo(mLastPlayPosition);
            }
        }

        @Override
        public void onVideSizeChanged(MediaPlayer mp, int width, int height)
        {
        }

        @Override
        public void onProgress(int progress)
        {
        }

        @Override
        public void onSeekComplete()
        {
        }

        @Override
        public void onCompletion()
        {
        }
    };

    /**
     * 转码后输出地址
     * @return
     */
    private String getCompressedVideoPath()
    {
        String mCompressdedVideoPath = new File(AlbumHelper.getAblumPath(),String.format("lsq_转码_%s.mp4",
                StringHelper.timeStampString())).getPath();
        return mCompressdedVideoPath;
    }

    private View.OnClickListener mOnClickListener = new View.OnClickListener()
    {

        @Override
        public void onClick(View v)
        {
            if (v == mBackBtn)
            {
                finish();
            }
            else if (v == mStartTranscodeBtn)
            {
                startTranscode();
            }
        }
    };

    /**
     * 开始转码
     *
     */
    private void startTranscode()
    {
        if(mInputPath == null || mInputPath.equals("")) return;

        TuSDKVideoInfo videoInfo = TuSDKMediaUtils.getVideoInfo(mInputPath);
        MediaFormat ouputVideoFormat = getOutputVideoFormat(videoInfo);
        MediaFormat ouputAudioFormat = getOutputAudioFormat();

        TuSdkMediaSuit.transcoding(new TuSdkMediaDataSource(mInputPath),mOutputPath == null ? DEFAULT_OUTPUT_MOVIE_PATH : mOutputPath,ouputVideoFormat,ouputAudioFormat,mediaProgress);
        mCircleView.setVisibility(View.VISIBLE);
        Toast.makeText(MovieTranscodeActivity.this, R.string.lsq_transcode_start, LENGTH_SHORT).show();
    }

    /**
     * 视频处理结果
     */
    private TuSdkMediaProgress mediaProgress = new TuSdkMediaProgress()
    {
        @Override
        public void onProgress(float progress, TuSdkMediaDataSource mediaDataSource, int index,
                               int total) {
            TLog.i("onProgressChanged: " + progress);
            mCircleView.setText((progress * 100)+"%");
            mCircleView.setValue(progress);
        }

        @Override
        public void onCompleted(Exception e, TuSdkMediaDataSource outputFile, int total)
        {
            if(e == null)
            {
                Toast.makeText(MovieTranscodeActivity.this, R.string.lsq_transcode_complete, LENGTH_SHORT).show();
            }
            else
            {
                Toast.makeText(MovieTranscodeActivity.this, getResources().getString(R.string.lsq_compress_failed), LENGTH_SHORT).show();
            }
            hideCircleView();
        }
    };

    /**
     * 获取输出文件的视频格式信息
     *
     * @return MediaFormat
     */
    protected MediaFormat getOutputVideoFormat(TuSDKVideoInfo videoInfo)
    {
        int fps = mFps==0 ? videoInfo.fps : mFps;
        int bitrate = mBitrate==0 ? videoInfo.bitrate : mBitrate;
        TuSdkSize videoSize = TuSdkSize.create(videoInfo.width,videoInfo.height);

        if (videoInfo.videoOrientation == ImageOrientation.Right || videoInfo.videoOrientation == ImageOrientation.Left)
            videoSize = TuSdkSize.create(videoSize.height,videoSize.width);

        //是否输出关键帧
        CheckBox keyFrame = findViewById(R.id.lsq_keyFrame_cb);

        MediaFormat mediaFormat = TuSdkMediaFormat.buildSafeVideoEncodecFormat(videoSize.width, videoSize.height,
                fps, bitrate, MediaCodecInfo.CodecCapabilities.COLOR_FormatSurface, 0,keyFrame.isChecked() ? 0 : 1);

        return mediaFormat;
    }

    /**
     * 获取输出的音频格式信息
     * @return MediaFormat
     */
    protected MediaFormat getOutputAudioFormat()
    {
        MediaFormat audioFormat = TuSdkMediaFormat.buildSafeAudioEncodecFormat();
        return audioFormat;
    }


    /**
     * 隐藏进度
     */
    private void hideCircleView()
    {
        mCircleView.setVisibility(View.GONE);
        mCircleView.setText("0%");
        mCircleView.setValue(0);
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        if (mMoviePlayer != null)
            mMoviePlayer.start();
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        if (mMoviePlayer != null)
        {
            mMoviePlayer.pause();
            mLastPlayPosition = mMoviePlayer.getCurrentPosition();
        }
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        if (mMoviePlayer != null)
        {
            mMoviePlayer.destory();
            mMoviePlayer = null;
        }
    }
}
