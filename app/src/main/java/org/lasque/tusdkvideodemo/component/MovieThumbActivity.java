package org.lasque.tusdkvideodemo.component;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import org.lasque.tusdk.api.movie.player.TuSDKMoviePlayer;
import org.lasque.tusdk.api.video.retriever.TuSDKVideoImageExtractor;
import org.lasque.tusdk.core.TuSdk;
import org.lasque.tusdk.core.TuSdkContext;
import org.lasque.tusdk.core.common.TuSDKMediaDataSource;
import org.lasque.tusdk.core.struct.TuSdkSize;
import org.lasque.tusdk.core.view.widget.button.TuSdkNavigatorBackButton;
import com.upyun.shortvideo.R;
import org.lasque.tusdkvideodemo.ScreenAdapterActivity;

import java.util.List;

/**
 * droid-sdk-video
 *
 * @author MirsFang
 * @Date 2018/9/13 18:04
 * @Copright (c) 2018 tusdk.com. All rights reserved.
 * <p>
 * 获取视频封面
 */
public class MovieThumbActivity extends ScreenAdapterActivity {

    /** 返回按钮 */
    private TuSdkNavigatorBackButton mBackBtn;

    /** 视频播放器 */
    private TuSDKMoviePlayer mMoviePlayer;

    private GridView mThumbList;

    /** 加载缩略图按钮 */
    private Button mLoadThumbButton;

    /** 上一次播放的位置 */
    private int mLastPlayPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_thumb);
        initView();
    }

    private void initView() {
        mBackBtn = findViewById(R.id.lsq_backButton);
        mBackBtn.setOnClickListener(mOnClickListener);

        TextView titleView = findViewById(R.id.lsq_titleView);
        titleView.setText(TuSdkContext.getString("lsq_movie_thumb"));

        SurfaceView preview = findViewById(R.id.lsq_preview);
        iniMoviePlayer(preview);
        mLoadThumbButton = findViewById(R.id.lsq_load_thumb_btn);
        mLoadThumbButton.setOnClickListener(mOnClickListener);
        mThumbList = findViewById(R.id.lsq_movie_thumb_list);
        mThumbList.setNumColumns(5);
        mThumbList.setColumnWidth((TuSdkContext.getScreenSize().width - 32*2 -38*4)/5);
        mThumbList.setHorizontalSpacing(TuSdkContext.dip2px(20));
        mThumbList.setVerticalSpacing(TuSdkContext.dip2px(20));
    }

    private TuSDKMoviePlayer.TuSDKMoviePlayerDelegate mMoviePlayerDelegate = new TuSDKMoviePlayer.TuSDKMoviePlayerDelegate() {
        @Override
        public void onStateChanged(TuSDKMoviePlayer.PlayerState state) {
            if (state == TuSDKMoviePlayer.PlayerState.INITIALIZED) {
                mMoviePlayer.seekTo(mLastPlayPosition);
            }
        }

        @Override
        public void onVideSizeChanged(MediaPlayer mp, int width, int height) {
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

    protected void iniMoviePlayer(SurfaceView surfaceView) {
        mMoviePlayer = TuSDKMoviePlayer.createMoviePlayer();
        mMoviePlayer.setLooping(true);

        mMoviePlayer.initVideoPlayer(this, getVideoPath(), surfaceView);
        mMoviePlayer.setDelegate(mMoviePlayerDelegate);
    }

    /** 加载视频缩略图 */
    public void loadVideoThumbList(String videoPath) {
        TuSdkSize tuSdkSize = TuSdkSize.create(TuSdkContext.dip2px(56), TuSdkContext.dip2px(30));
        TuSDKVideoImageExtractor extractor = TuSDKVideoImageExtractor.createExtractor();
        extractor.setOutputImageSize(tuSdkSize)
                .setVideoDataSource(TuSDKMediaDataSource.create(getVideoPath()))
                .setExtractFrameCount(15);
        extractor.asyncExtractImageList(new TuSDKVideoImageExtractor.TuSDKVideoImageExtractorDelegate() {
            @Override
            public void onVideoImageListDidLoaded(List<Bitmap> images) {
                mThumbList.setAdapter(new MovieThumbAdapter(MovieThumbActivity.this, images));
                String hintMsg = getResources().getString(R.string.lsq_refresh_list_view_state_hidden);
                TuSdk.messageHub().showToast(MovieThumbActivity.this, hintMsg);
            }

            @Override
            public void onVideoNewImageLoaded(Bitmap bitmap) {
            }
        });
    }

    private Uri getVideoPath() {
        Uri videoPathUri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.tusdk_sample_video);
        return videoPathUri;
    }

    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v == mBackBtn) {
                finish();
            } else if (v == mLoadThumbButton) {
                if (mThumbList.getAdapter() != null) return;

                loadVideoThumbList(getVideoPath().toString());
                String hintMsg = getResources().getString(R.string.lsq_movie_thumb_loading);
                TuSdk.messageHub().setStatus(MovieThumbActivity.this, hintMsg);
            }
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        if (mMoviePlayer != null)
            mMoviePlayer.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mMoviePlayer.pause();
        mLastPlayPosition = mMoviePlayer.getCurrentPosition();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mMoviePlayer.destory();
        mMoviePlayer = null;
    }

    /** 自定义适配器 */
    public static class MovieThumbAdapter extends BaseAdapter {
        private Context context;

        private List<Bitmap> mImageList;

        public MovieThumbAdapter(Context context, List<Bitmap> imageList) {
            this.context = context;
            this.mImageList = imageList;
        }

        @Override
        public int getCount() {
            return mImageList.size();
        }

        @Override
        public Object getItem(int position) {
            return mImageList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        //给每一个item填充图片
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ImageView imageView = new ImageView(context);

            //每一张图片
            imageView.setImageBitmap(mImageList.get(position));

            return imageView;
        }
    }
}
