package org.lasque.tusdkvideodemo.views;

import android.content.Context;
import android.graphics.Rect;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.FrameLayout;

import org.lasque.tusdk.core.TuSdkContext;
import org.lasque.tusdk.core.struct.TuSdkSize;
import org.lasque.tusdk.core.utils.RectHelper;

import java.io.IOException;

/**
 * @author xujie
 * @Date 2018/11/8
 */

public class TuSDKMediaPlayer extends FrameLayout {

    private Context mContext;
    // 播放器
    private MediaPlayer mMediaPlayer;
    // 播放回调
    private PlayerCallback mPlayerCallback;
    // 播放视图
    private SurfaceView mSurfaceView;
    // holder
    private SurfaceHolder mSurfaceholder;
    /** 视频总时长 */
    private int mVideoTotalTime;
    // 播放地址
    private String url;
    // 记录结束点
    private int position;

    Handler mHandler = new Handler(Looper.getMainLooper())
    {
        public void handleMessage(Message msg)
        {
            switch (msg.what)
            {
                case 0:
                    if (mMediaPlayer != null)
                    {
                        float progressTime = mMediaPlayer.getCurrentPosition();
                        float progress = progressTime / mVideoTotalTime;
                        if (mPlayerCallback != null) {
                            mPlayerCallback.getProgress(progress);
                        }
                    }

                    mHandler.removeMessages(0);
                    mHandler.sendEmptyMessageDelayed(0, 500L);
            }
        }
    };

    public void setPlayerCallback(PlayerCallback mCallback)
    {
        this.mPlayerCallback = mCallback;
    }

    public interface PlayerCallback{

        public void setStartPlayer(int paramInt);

        public void setEndPlayer();

        public void getProgress(float paramFloat);
    }

    public TuSDKMediaPlayer(@NonNull Context context) {
        this(context,null);
    }

    public TuSDKMediaPlayer(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public TuSDKMediaPlayer(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context){
        mContext = context;
        mSurfaceView = new SurfaceView(context);
        addView(this.mSurfaceView);
        mSurfaceholder = mSurfaceView.getHolder();
        mSurfaceholder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        mSurfaceholder.setKeepScreenOn(true);
    }

    /**
     * 初始播放
     * @param url
     */
    public void startPlay(String url)
    {
        if(TextUtils.isEmpty(url)) return;

        this.url = url;
        if (this.mMediaPlayer == null) {
            this.mSurfaceholder.addCallback(callback);
        } else {
            initMediaPlayer();
        }
    }

    /**
     *  播放
     */
    public void start()
    {
        if (this.mMediaPlayer != null && !mMediaPlayer.isPlaying()) {
            this.mMediaPlayer.start();
        }
    }

    /**
     * 是否正在播放
     * @return
     */
    public boolean isPlaying(){
        if (this.mMediaPlayer != null) {
            return mMediaPlayer.isPlaying();
        }
        return false;
    }

    /**
     * 暂停播放
     */
    public void pause()
    {
        if (this.mMediaPlayer != null && mMediaPlayer.isPlaying()) {
            this.mMediaPlayer.pause();
        }
    }

    /**
     * 获取当前时间
     * @return
     *      秒 s
     */
    public int getTime()
    {
        if (this.mMediaPlayer != null)
        {
            int time = this.mMediaPlayer.getCurrentPosition() / 1000;
            return time;
        }
        return 0;
    }

    /**
     * 重置
     */
    public void reset()
    {
        if (this.mMediaPlayer != null)
        {
            this.mHandler.removeMessages(0);
            this.mMediaPlayer.release();
        }
    }

    /**
     * seek指定时间
     * @param seekTime
     *          秒 s
     */
    public void seekTo(int seekTime)
    {
        if ((this.mMediaPlayer != null) && (this.mMediaPlayer.getDuration() > seekTime * 1000)) {
            this.mMediaPlayer.seekTo(seekTime * 1000);
        }
    }

    private SurfaceHolder.Callback callback = new SurfaceHolder.Callback() {
        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            initMediaPlayer();
            if(position > 0) {
                seekTo(position);
            }
        }

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
            if(mMediaPlayer != null){
                position = 0;
                mMediaPlayer.stop();
                if(mPlayerCallback != null) {
                    mHandler.removeMessages(0);
                    mPlayerCallback.setEndPlayer();
                }
            }
        }
    };

    /**
     * 初始化播放器
     */
    private void initMediaPlayer(){
        mMediaPlayer = new MediaPlayer();
        mMediaPlayer.reset();
        mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

        // 将视频画面输出到SurfaceView
        mMediaPlayer.setDisplay(mSurfaceholder);

        // 设置需要播放的视频
        try {
            setDataSource(url);
            mMediaPlayer.prepareAsync();
        } catch (Exception e) {
            e.printStackTrace();
        }

        mMediaPlayer.setOnPreparedListener(mOnPreparedListener);
        mMediaPlayer.setOnVideoSizeChangedListener(mOnVideoSizeChangedListener);
        mMediaPlayer.setOnCompletionListener(mOnCompletionListener);
    }

    /**
     * 设置数据源
     * @param mInputPath
     */
    private void setDataSource(String mInputPath) {
        if (mMediaPlayer == null) mMediaPlayer = new MediaPlayer();
        try {
            mMediaPlayer.setDataSource(mInputPath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private MediaPlayer.OnPreparedListener mOnPreparedListener = new MediaPlayer.OnPreparedListener() {
        @Override
        public void onPrepared(MediaPlayer mp) {
            mp.start();
            mVideoTotalTime = mp.getDuration();
            if (mPlayerCallback != null) {
                mPlayerCallback.setStartPlayer(position);
            }
            mp.pause();
            mHandler.sendEmptyMessage(0);
        }
    };

    private MediaPlayer.OnCompletionListener mOnCompletionListener = new MediaPlayer.OnCompletionListener() {
        @Override
        public void onCompletion(MediaPlayer mp) {
            if (mPlayerCallback != null)
            {
                mHandler.removeMessages(0);
                mPlayerCallback.setEndPlayer();
            }
        }
    };

    private MediaPlayer.OnVideoSizeChangedListener mOnVideoSizeChangedListener = new MediaPlayer.OnVideoSizeChangedListener() {

        @Override
        public void onVideoSizeChanged(MediaPlayer mp, int width, int height) {
            // 将视频进行等比例显示处理
            setVideoSize(mSurfaceView, width, height);
        }
    };

    public void setVideoSize(SurfaceView surfaceView, int width, int height) {
        if (surfaceView != null) {
            TuSdkSize tuSdkSize = TuSdkContext.getDisplaySize();
            int screenWidth = (int) tuSdkSize.width;
            int screenHeight = (int) (tuSdkSize.height);

            Rect boundingRect = new Rect();
            boundingRect.left = 0;
            boundingRect.right = screenWidth;
            boundingRect.top = 0;
            boundingRect.bottom = screenHeight;
            Rect rect = RectHelper.makeRectWithAspectRatioInsideRect(new TuSdkSize(width, height), boundingRect);

            int w = rect.right - rect.left;
            int h = rect.bottom - rect.top;
            FrameLayout.LayoutParams lp = new FrameLayout
                    .LayoutParams(w, h);
            lp.setMargins(rect.left, rect.top, 0, 0);
            surfaceView.setLayoutParams(lp);
        }
    }
}
