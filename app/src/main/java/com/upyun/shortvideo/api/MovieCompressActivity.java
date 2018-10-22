package com.upyun.shortvideo.api;

import android.app.Activity;
import android.graphics.Color;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import org.lasque.tusdk.api.movie.compresser.TuSDKMovieCompresser;
import org.lasque.tusdk.api.movie.player.TuSDKMoviePlayer;
import org.lasque.tusdk.core.TuSdkContext;
import org.lasque.tusdk.core.common.TuSDKMediaDataSource;
import org.lasque.tusdk.core.delegate.TuSDKVideoSaveDelegate;
import org.lasque.tusdk.core.utils.StringHelper;
import org.lasque.tusdk.core.utils.TLog;
import org.lasque.tusdk.core.utils.image.AlbumHelper;
import org.lasque.tusdk.core.video.TuSDKVideoResult;
import com.upyun.shortvideo.R;

import java.io.File;
import java.text.DecimalFormat;

import at.grabner.circleprogress.CircleProgressView;

import static android.widget.Toast.LENGTH_SHORT;

/**
 * 视频压缩API使用示例
 *
 * @author zuojindong
 */

public class MovieCompressActivity extends Activity
{
    // 默认的视频输出路径
    private final String DEFAULT_OUTPUT_MOVIE_PATH = getCompressedVideoPath();

    // 视频压缩类实例
    private TuSDKMovieCompresser mMovieCompresser;

    // 输入视频路径
    private String mInputPath = "";

    // 开始压缩按钮
    private Button mStartCompressBtn;

    // 输出视频路径
    private String mOutputPath;

    // 视频压缩比例, 默认: 1.0f,不压缩, 取值范围：0 ~ 2.0f
    private float mScaleValue = 1.0f;

    // 返回按钮
    private TextView mBackBtn;

    // 视频播放器
    private TuSDKMoviePlayer mMoviePlayer;

    // 上一次播放的位置
    private int mLastPlayPosition;

    // 压缩进度
    private CircleProgressView mCircleView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.movie_compress_layout);
        initView();
    }

    private void initView()
    {
        mInputPath = getIntent().getStringExtra("videoPath");

        mBackBtn = (TextView) findViewById(R.id.lsq_back);
        mBackBtn.setOnClickListener(mOnClickListener);
        TextView titleView = (TextView) findViewById(R.id.lsq_title);
        titleView.setText(TuSdkContext.getString("lsq_compresser_compress"));
        TextView nextBtn = (TextView) findViewById(R.id.lsq_next);
        nextBtn.setVisibility(View.GONE);
        mStartCompressBtn = (Button) findViewById(R.id.lsq_start_compress_btn);
        mStartCompressBtn.setOnClickListener(mOnClickListener);

        TextView lsq_before_compress_textview = (TextView) findViewById(R.id.lsq_before_compress_textview);
        lsq_before_compress_textview.setText(TuSdkContext.getString("lsq_compresser_before_compress")+FormetFileSize(new File(mInputPath).length()));

        mCircleView = findViewById(R.id.circleView);
        mCircleView.setTextSize(50);
        mCircleView.setAutoTextSize(true);
        mCircleView.setTextColor(Color.WHITE);
        mCircleView.setVisibility(View.GONE);

        SurfaceView preview = (SurfaceView) findViewById(R.id.lsq_preview);
        fixMovieplayerSize(preview);
        iniMoviePlayer(preview);

        SeekBar scaleSeekBar = findViewById(R.id.lsq_scale_seekBar);
        scaleSeekBar.setOnSeekBarChangeListener(seekBarChangeListener);
    }

    /**
     * 压缩比滚动条监听
     */
    private SeekBar.OnSeekBarChangeListener seekBarChangeListener = new SeekBar.OnSeekBarChangeListener()
    {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser)
        {
            TextView scale = findViewById(R.id.lsq_scale);
            scale.setText(seekBar.getProgress()/100f+"");
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar)
        {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar)
        {
            mScaleValue = seekBar.getProgress()/100f;
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

    private void iniMoviePlayer(SurfaceView surfaceView)
    {
        mMoviePlayer = TuSDKMoviePlayer.createMoviePlayer();
        mMoviePlayer.setLooping(true);
        mMoviePlayer.initVideoPlayer(this, Uri.parse(mInputPath), surfaceView);
        mMoviePlayer.setDelegate(mMoviePlayerDelegate);
    }

    /**
     * 压缩后输出地址
     * @return
     */
    private String getCompressedVideoPath()
    {
        String mCompressdedVideoPath = new File(AlbumHelper.getAblumPath(),String.format("lsq_压缩_%s.mp4",
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
            else if (v == mStartCompressBtn)
            {
                startCompresser();
            }
        }
    };

    /**
     * 开始压缩
     *
     */
    private void startCompresser()
    {
        if(mInputPath == null || mInputPath.equals("")) return;

        mMovieCompresser = new TuSDKMovieCompresser(TuSDKMediaDataSource.create(mInputPath));
        mMovieCompresser.setOutputFilePath(mOutputPath == null ? DEFAULT_OUTPUT_MOVIE_PATH : mOutputPath);
        mMovieCompresser.setDelegate(delegate);
        TuSDKMovieCompresser.TuSDKMovieCompresserSetting compresserSetting = mMovieCompresser.getCompresserSetting();
        compresserSetting.setScale(mScaleValue);
        mMovieCompresser.start();
        mCircleView.setVisibility(View.VISIBLE);
        Toast.makeText(MovieCompressActivity.this, R.string.lsq_compresser_compressing, LENGTH_SHORT).show();
    }

    private TuSDKVideoSaveDelegate delegate = new TuSDKVideoSaveDelegate()
    {

        @Override
        public void onProgressChaned(float percentage)
        {
            TLog.i("onProgressChanged: " + percentage);
            mCircleView.setText((percentage * 100)+"%");
            mCircleView.setValue(percentage);
        }

        @Override
        public void onSaveResult(TuSDKVideoResult result)
        {
            hideCircleView();

            TLog.i("onCompressComplete:" + result.videoPath.getPath());
            Toast.makeText(MovieCompressActivity.this, R.string.lsq_compresser_compress_complete, LENGTH_SHORT).show();
            TextView lsq_after_compress_textview = findViewById(R.id.lsq_after_compress_textview);
            lsq_after_compress_textview.setText(TuSdkContext.getString("lsq_compresser_after_compress")+ FormetFileSize(result.videoPath.length()));
        }

        @Override
        public void onResultFail(Exception e)
        {
            hideCircleView();
            Toast.makeText(MovieCompressActivity.this, getResources().getString(R.string.lsq_compress_failed), LENGTH_SHORT).show();
        }
    };

    /**
     * 隐藏进度
     */
    private void hideCircleView() {
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

    /**
     * 格式化文件大小（MB）
     * @param fileS
     * @return
     */
    private static String FormetFileSize(long fileS)
    {
        DecimalFormat df = new DecimalFormat("#0.00");
        String fileSizeString = "";
        fileSizeString = df.format((double) fileS / 1048576) + "MB";
        return fileSizeString;
    }
}
