/**
 * TuSDK
 * TuSDKVideoDemo
 * MovieAlbumFragment.java
 *
 * @author H.ys
 * @Date 2019/5/31 17:05
 * @Copyright (c) 2019 tusdk.com. All rights reserved.
 */
package org.lasque.tusdkvideodemo.album;

import android.content.Intent;
import android.database.Cursor;
import android.media.MediaFormat;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.lasque.tusdk.core.TuSdk;
import org.lasque.tusdk.core.common.TuSDKMediaDataSource;
import org.lasque.tusdk.core.common.TuSDKMediaUtils;
import org.lasque.tusdk.core.decoder.TuSDKVideoInfo;
import org.lasque.tusdk.core.media.codec.video.TuSdkVideoInfo;
import org.lasque.tusdk.core.view.TuSdkViewHelper;
import org.lasque.tusdk.impl.view.widget.TuProgressHub;
import com.upyun.shortvideo.R;
import org.lasque.tusdkvideodemo.editor.MovieEditorPreviewActivity;
import org.lasque.tusdkvideodemo.utils.MD5Util;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


public class MovieAlbumFragment extends Fragment {

    /* 最小视频时长(单位：ms) */
    private static int MIN_VIDEO_DURATION = 3000;
    /* 最大视频时长(单位：ms) */
    private static int MAX_VIDEO_DURATION = 60000 * 6;
    /** 最大边长限制 **/
    private static final int MAX_SIZE = 4096;

    /* 确定按钮 */
    protected TextView mConfirmButton;
    /* 返回按钮 */
    protected TextView mBackButton;
    /* 最大选择数量 */
    protected int mSelectMax = 1;

    private RecyclerView mRecyclerView;

    private MovieAlbumAdapter mVideoAlbumAdapter;

    private int mCurrentPos = -1;

    private LoadVideoTask mLoadVideoTask;

    private boolean isEnable = true;

    public void setIsEnable(boolean isEnable){
        this.isEnable = isEnable;
        mRecyclerView.setEnabled(isEnable);
    }

    public boolean isEnable(){
        return isEnable;
    }

    private View.OnClickListener mNextStepClickListener = new TuSdkViewHelper.OnSafeClickListener() {
        @Override
        public void onSafeClick(View v) {
            if (mVideoAlbumAdapter == null || mVideoAlbumAdapter.getSelectedVideoInfo().size() <= 0)
                TuSdk.messageHub().showToast(getActivity(), R.string.lsq_select_video_hint);
            else
                handleIntentAction();
        }
    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View baseView = inflater.inflate(R.layout.movie_album_fragment, container, false);
        mRecyclerView = (RecyclerView) baseView.findViewById(R.id.lsq_movie_selector_recyclerView);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 4);
        mRecyclerView.setLayoutManager(gridLayoutManager);
        mSelectMax = getActivity().getIntent().getIntExtra("selectMax", 1);
        return baseView;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && resultCode == 100) {
            MovieInfo info = (MovieInfo) data.getSerializableExtra("videoInfo");
            if (info != null && !contains(mVideoAlbumAdapter.getSelectedVideoInfo(), info))
                mVideoAlbumAdapter.updateSelectedVideoPosition(mCurrentPos);
            else if (info == null && mVideoAlbumAdapter.getVideoInfoList().size() > 0 && mCurrentPos != -1)
                // 取消选中
                if (contains(mVideoAlbumAdapter.getSelectedVideoInfo(), mVideoAlbumAdapter.getVideoInfoList().get(mCurrentPos)))
                    mVideoAlbumAdapter.updateSelectedVideoPosition(mCurrentPos);
        }
    }

    private boolean contains(List<MovieInfo> movieInfos, MovieInfo movieInfo) {
        for (MovieInfo info : movieInfos) {
            if (info.getPath().equals(movieInfo.getPath())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void onResume() {
        super.onResume();
        mLoadVideoTask = new LoadVideoTask();
        mLoadVideoTask.execute();
    }

    /**
     * 将扫描的视频添加到集合中
     */
    public List<MovieInfo> getVideoList() {
        List<MovieInfo> videoInfo = new ArrayList<>();
        Cursor cursor = getActivity().getContentResolver().query(
                MediaStore.Video.Media.EXTERNAL_CONTENT_URI, null, null,
                null, "date_added desc");
        while (cursor.moveToNext()) {
            String path = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA));
            int duration = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DURATION));

            //根据时间长短加入显示列表
            if (duration > 0 && duration < MAX_VIDEO_DURATION) {
                videoInfo.add(new MovieInfo(path, duration));
            }
            if (duration == 0) {
                TuSDKVideoInfo vInfo = TuSDKMediaUtils.getVideoInfo(path);
                if (vInfo == null) continue;
                if (vInfo.durationTimeUs > 0) {
                    videoInfo.add(new MovieInfo(path, (int) (vInfo.durationTimeUs / 1000)));
                }
            }
        }
        cursor.close();
        return videoInfo;
    }

    /**
     * 检测是否4K视频
     * @param position
     * @return
     */
    private boolean check4K(int position) {
        MovieInfo info = mVideoAlbumAdapter.getVideoInfoList().get(position);
        MediaFormat mediaFormat = TuSDKMediaUtils.getVideoFormat(new TuSDKMediaDataSource(info.getPath()));
        TuSdkVideoInfo videoInfo = new TuSdkVideoInfo(mediaFormat);
        if (!TuSDKMediaUtils.isVideoSizeSupported(videoInfo.size,mediaFormat.getString(MediaFormat.KEY_MIME)) || videoInfo.size.maxSide() > MAX_SIZE) {
            TuSdkViewHelper.toast(getActivity(), R.string.lsq_loadvideo_failed);
            return true;
        }
        return false;
    }

    /**
     *  RecyclerView中item的点击事件，得到点击item的视频信息
     */
    private MovieAlbumAdapter.OnItemClickListener mOnItemClickListener = new MovieAlbumAdapter.OnItemClickListener() {
        @Override
        public void onSelectClick(View view, int position) {
            if (!isEnable) return;
            if (check4K(position)) return;

            mVideoAlbumAdapter.updateSelectedVideoPosition(position);
        }

        @Override
        public void onClick(View view, int position) {
            if (!isEnable) return;
            if (check4K(position)) return;

            MovieInfo info = mVideoAlbumAdapter.getVideoInfoList().get(position);
            if (info.getDuration() < MIN_VIDEO_DURATION) {
                TuSdk.messageHub().showToast(getActivity(), R.string.lsq_album_select_min_time);
                return;
            }

            mCurrentPos = position;
            // 视频路径
            List<MovieInfo> videoPath = mVideoAlbumAdapter.getSelectedVideoInfo();
            Intent intent = new Intent(getActivity(), MovieEditorPreviewActivity.class);
            // 要跳转的视频裁剪类名
            intent.putExtra("cutClassName", getActivity().getIntent().getStringExtra("cutClassName"));
            intent.putExtra("selectMax", mSelectMax);
            intent.putExtra("currentVideoPath", mVideoAlbumAdapter.getVideoInfoList().get(position));
            intent.putExtra("videoPaths", (Serializable) videoPath);
            startActivityForResult(intent, 100);
        }
    };

    /**
     *  处理跳转事件
     */
    public void handleIntentAction() {
        if (mVideoAlbumAdapter == null || mVideoAlbumAdapter.getSelectedVideoInfo().size() <= 0)
            return;

        // 要跳转的视频裁剪类名
        String className = getActivity().getIntent().getStringExtra("cutClassName");
        // 视频路径
        List<MovieInfo> videoPath = mVideoAlbumAdapter.getSelectedVideoInfo();

        long totalTime = 0;
        for (MovieInfo info : videoPath) {
            totalTime += info.getDuration();
        }

        if (totalTime < MIN_VIDEO_DURATION) {
            TuSdk.messageHub().showToast(getActivity(), R.string.lsq_album_select_min_time);
            return;
        }

        Intent intent = null;
        try {
            intent = new Intent(getActivity(), Class.forName(className));
            intent.putExtra("videoPaths", (Serializable) videoPath);
            startActivity(intent);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

    }

    public View.OnClickListener getNextStepClickListener() {
        return mNextStepClickListener;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mLoadVideoTask.cancel(false);
    }

    /**
     * 相册加载
     */
    class LoadVideoTask extends AsyncTask<Void, Integer, List<MovieInfo>> {

        @Override
        protected List<MovieInfo> doInBackground(Void... voids) {
            return getVideoList();
        }

        @Override
        protected void onPreExecute() {
            TuProgressHub.showToast(getActivity(), "数据加载中...");
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(List<MovieInfo> movieInfos) {
            TuProgressHub.dismiss();
            if (movieInfos == null) movieInfos = new ArrayList<>();
            if (mVideoAlbumAdapter == null) {
                mVideoAlbumAdapter = new MovieAlbumAdapter(getActivity(), movieInfos, mSelectMax);
                mRecyclerView.setAdapter(mVideoAlbumAdapter);
                mVideoAlbumAdapter.setOnItemClickListener(mOnItemClickListener);
            }
            if (mVideoAlbumAdapter.getVideoInfoList().size() != movieInfos.size() || !(MD5Util.crypt(mVideoAlbumAdapter.getVideoInfoList().toString()).equals(MD5Util.crypt(movieInfos.toString())))) {
                mVideoAlbumAdapter.setVideoInfoList(movieInfos);
            }
        }
    }
}
