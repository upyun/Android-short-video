package com.upyun.shortvideo;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.MediaController;

import com.upyun.upplayer.widget.UpVideoView;


public class PlayVideoActivity extends Activity {

    private UpVideoView mVideoView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_video);
        mVideoView = (UpVideoView) findViewById(R.id.uv_demo);
//        mVideoView.setVideoPath("http://p.upyun.com/demo/short_video/UPYUN.mp4");
//        mVideoView.setVideoPath("http://p.upyun.com/demo/short_video/UPYUN.flv");
        mVideoView.setVideoPath("http://uprocess.b0.upaiyun.com/demo/short_video/UPYUN_0.flv");
        mVideoView.start();

        AndroidMediaController controller = new AndroidMediaController(this);
        mVideoView.setMediaController(controller);
        controller.setMediaPlayer(mVideoView);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mVideoView != null) {
            mVideoView.stopPlayback();
        }
    }
}
