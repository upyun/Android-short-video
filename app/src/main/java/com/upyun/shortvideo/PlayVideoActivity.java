package com.upyun.shortvideo;

import android.app.Activity;
import android.app.Service;
import android.media.AudioManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.MediaController;
import android.widget.TableLayout;

import com.upyun.upplayer.widget.UpVideoView;
import com.upyun.upplayer.widget.UpVideoView2;


public class PlayVideoActivity extends Activity {

    private UpVideoView2 mVideoView;
    private TableLayout mHudView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_video);
        mVideoView = (UpVideoView2) findViewById(R.id.uv_demo);
        //设置播放地址
        mVideoView.setVideoPath("http://uprocess.b0.upaiyun.com/demo/short_video/UPYUN_0.flv");
        //开始播放
        mVideoView.start();

        //关联 MediaController
        MediaController controller = new MediaController(this);
        mVideoView.setMediaController(controller);
        controller.setMediaPlayer(mVideoView);

        mHudView = (TableLayout) findViewById(R.id.hud_view);
        mVideoView.setHudView(mHudView);
        mHudView.setVisibility(View.GONE);
    }


    //横竖屏
    public void fullScreen(View view) {
        if (mVideoView.isFullState()) {
            mVideoView.exitFullScreen(this);
        } else {
            mVideoView.fullScreen(this);
        }
    }

    //视频信息显示
    public void info(View view) {
        if (mHudView.getVisibility() == View.VISIBLE) {
            mHudView.setVisibility(View.GONE);
        } else {
            mHudView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        //释放播放器
        mVideoView.release(true);
    }
}
