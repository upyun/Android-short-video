package com.upyun.shortvideo.editor;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import org.lasque.tusdk.core.TuSdk;

import com.upyun.shortvideo.ScreenAdapterActivity;
import com.upyun.shortvideo.views.TuSDKMediaPlayer;
import com.upyun.shortvideo.R;
import com.upyun.shortvideo.album.MovieInfo;

import java.io.Serializable;
import java.util.Iterator;
import java.util.List;

/**
 * 视频预览
 * @author xujie
 * @Date 2018/11/1
 */

public class MovieEditorPreviewActivity extends ScreenAdapterActivity {
    private static final String TAG = "MovieEditorPreviewActiv";
    //播放按钮
    private ImageView mPlayBtn;
    //返回按钮
    private TextView mBackBtn;
    //下一步按钮
    private TextView mNextBtn;
    //视频添加
    private TextView mVideoAddBtn;
    //视频路径
    private List<MovieInfo> mVideoPaths;
    //视频路径
    private MovieInfo mCurrentVideoPath;
    private int mSelectMax;

    private TuSDKMediaPlayer mMediaPlayer;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_video_preview);
        initView();
        initData();
        initPlayer();
    }

    private void initView(){
        mPlayBtn = findViewById(R.id.lsq_play_btn);
        mBackBtn = findViewById(R.id.lsq_back);
        mBackBtn.setOnClickListener(mOnClickListener);
        mNextBtn = findViewById(R.id.lsq_next);
        mNextBtn.setOnClickListener(mOnClickListener);
        mVideoAddBtn = findViewById(R.id.lsq_video_add);
        mVideoAddBtn.setOnClickListener(mOnClickListener);
    }

    /**
     * 初始化数据
     */
    private void initData(){
        mVideoPaths = (List<MovieInfo>) getIntent().getSerializableExtra("videoPaths");
        mCurrentVideoPath = (MovieInfo) getIntent().getSerializableExtra("currentVideoPath");
        mSelectMax = getIntent().getIntExtra("selectMax",1);
        if(mVideoPaths.size() >= mSelectMax && !contains(mVideoPaths,mCurrentVideoPath)){
            mVideoAddBtn.setVisibility(View.GONE);
        }else {
            mVideoAddBtn.setVisibility(View.VISIBLE);
            if(contains(mVideoPaths,mCurrentVideoPath)){
                mVideoAddBtn.setText(String.valueOf(indexOf(mVideoPaths,mCurrentVideoPath)));
                mVideoAddBtn.setBackground(getResources().getDrawable(R.drawable.edit_heckbox_sel));
            }
        }
    }

    private boolean contains(List<MovieInfo> movieInfos,MovieInfo movieInfo){
        for (MovieInfo info : movieInfos){
            if(info.getPath().equals(movieInfo.getPath())){
                return true;
            }
        }
        return false;
    }

    private void remove(List<MovieInfo> movieInfos,MovieInfo movieInfo){
        Iterator<MovieInfo> iterator = movieInfos.iterator();
        while (iterator.hasNext()){
            MovieInfo info = iterator.next();
            if(info.getPath().equals(movieInfo.getPath())){
                iterator.remove();
            }
        }
    }

    private int indexOf(List<MovieInfo> movieInfos,MovieInfo movieInfo){
        for (int i = 0;i < movieInfos.size(); i++){
            if(movieInfos.get(i).getPath().equals(movieInfo.getPath())){
                return i + 1;
            }
        }
        return -1;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            onBack();
            return false;
        }else {
            return super.onKeyDown(keyCode, event);
        }
    }

    /**
     * 返回上级
     */
    private void onBack(){
        Intent intentBack = getIntent();
        intentBack.putExtra("videoInfo",contains(mVideoPaths,mCurrentVideoPath) ? mCurrentVideoPath : null);
        setResult(100,intentBack);
        finish();
    }

    /**
     * 点击事件监听
     */
    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.lsq_back:
                    //返回按钮
                    onBack();
                    break;
                case R.id.lsq_next:
                    //下一步
                    if(mVideoPaths.size() <= 0){
                        TuSdk.messageHub().showToast(MovieEditorPreviewActivity.this, R.string.lsq_select_video_hint);
                        return;
                    }
                    String className = getIntent().getStringExtra("cutClassName");
                    Intent intent = null;
                    try {
                        intent = new Intent(MovieEditorPreviewActivity.this,Class.forName(className));
                        intent.putExtra("videoPaths",(Serializable)mVideoPaths);
                        startActivity(intent);
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                    break;
                case R.id.lsq_video_add:
                    if(contains(mVideoPaths,mCurrentVideoPath)){
                        remove(mVideoPaths,mCurrentVideoPath);
                        mVideoAddBtn.setText("");
                        mVideoAddBtn.setBackground(getResources().getDrawable(R.drawable.edit_heckbox_unsel_max));
                    }else {
                        mVideoAddBtn.setText(String.valueOf(mVideoPaths.size() + 1));
                        mVideoAddBtn.setBackground(getResources().getDrawable(R.drawable.edit_heckbox_sel));
                        mVideoPaths.add(mCurrentVideoPath);
                    }
                    break;
                case R.id.lsq_media_player:
                    if(mMediaPlayer.isPlaying()){
                        mMediaPlayer.pause();
                        mPlayBtn.setVisibility(View.VISIBLE);
                    }else {
                        mMediaPlayer.start();
                        mPlayBtn.setVisibility(View.GONE);
                    }
                    break;
            }
        }
    };

    /** 初始化播放器 **/
    public void initPlayer(){
        mMediaPlayer = findViewById(R.id.lsq_media_player);
        mMediaPlayer.setOnClickListener(mOnClickListener);
        mMediaPlayer.startPlay(mCurrentVideoPath.getPath());
        mMediaPlayer.setPlayerCallback(playerCallback);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mMediaPlayer.reset();
    }

    TuSDKMediaPlayer.PlayerCallback playerCallback = new TuSDKMediaPlayer.PlayerCallback() {
        @Override
        public void setStartPlayer(int paramInt) {
            mMediaPlayer.seekTo(paramInt);
        }

        @Override
        public void setEndPlayer() {
            mMediaPlayer.pause();
            mPlayBtn.setVisibility(View.VISIBLE);
        }

        @Override
        public void getProgress(float paramFloat) {
        }
    };
}
