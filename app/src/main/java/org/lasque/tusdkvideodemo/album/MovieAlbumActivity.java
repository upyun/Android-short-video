/**
 * TuSDKVideoDemo
 * MovieAlbumActivity.java
 *
 * @author  loukang
 * @Date  Oct 9, 2017 10:43:14 AM
 * @Copright (c) 2017 tusdk.com. All rights reserved.
 *
 */
package org.lasque.tusdkvideodemo.album;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.Intent;
import android.database.Cursor;
import android.media.MediaFormat;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import org.lasque.tusdk.core.TuSdk;
import org.lasque.tusdk.core.TuSdkContext;
import org.lasque.tusdk.core.common.TuSDKMediaDataSource;
import org.lasque.tusdk.core.common.TuSDKMediaUtils;
import org.lasque.tusdk.core.decoder.TuSDKVideoInfo;
import org.lasque.tusdk.core.media.codec.video.TuSdkVideoInfo;
import org.lasque.tusdk.core.utils.ContextUtils;
import org.lasque.tusdk.core.view.TuSdkViewHelper;
import org.lasque.tusdk.impl.view.widget.TuProgressHub;
import com.upyun.shortvideo.R;
import org.lasque.tusdkvideodemo.ScreenAdapterActivity;
import org.lasque.tusdkvideodemo.editor.MovieEditorPreviewActivity;
import org.lasque.tusdkvideodemo.utils.PermissionUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 视频选择相册
 */
public class MovieAlbumActivity extends ScreenAdapterActivity
{
    /* 最小视频时长(单位：ms) */
    private static int MIN_VIDEO_DURATION = 3000;
    /* 最大视频时长(单位：ms) */
    private static int MAX_VIDEO_DURATION = 60000;
    /** 最大边长限制 **/
    private static final int MAX_SIZE = 3840;

    /* 确定按钮 */
    protected TextView mConfirmButton;
   /* 返回按钮 */
    protected TextView mBackButton;
   /* 最大选择数量 */
    protected int mSelectMax = 1;

    private RecyclerView mRecyclerView;

    private MovieAlbumAdapter mVideoAlbumAdapter;

    private int mCurrentPos = -1;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        if (PermissionUtils.hasRequiredPermissions(this, getRequiredPermissions()))
        {
            setContentView(R.layout.movie_album_activity);
            initView();
        }
        else
        {
            PermissionUtils.requestRequiredPermissions(this, getRequiredPermissions());
        }
    }

    /**
     * 组件运行需要的权限列表
     *
     * @return
     *            列表数组
     */
    @TargetApi(Build.VERSION_CODES.M)
    protected String[] getRequiredPermissions()
    {
        String[] permissions = new String[]{
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
        };

        return permissions;
    }

    /**
     * 处理用户的许可结果
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        PermissionUtils.handleRequestPermissionsResult(requestCode, permissions, grantResults, this, mGrantedResultDelgate);
    }

    protected PermissionUtils.GrantedResultDelgate mGrantedResultDelgate = new PermissionUtils.GrantedResultDelgate()
    {
        @Override
        public void onPermissionGrantedResult(boolean permissionGranted)
        {
            if (permissionGranted)
            {
                setContentView(R.layout.movie_album_activity);
                initView();
            }
            else
            {
                String msg = TuSdkContext.getString("lsq_album_no_access", ContextUtils.getAppName(MovieAlbumActivity.this));

                TuSdkViewHelper.alert(permissionAlertDelegate, MovieAlbumActivity.this, TuSdkContext.getString("lsq_album_alert_title"),
                        msg, TuSdkContext.getString("lsq_button_close"), TuSdkContext.getString("lsq_button_setting")
                );
            }
        }
    };

    /**
     * 权限警告提示框点击事件回调
     */
    protected TuSdkViewHelper.AlertDelegate permissionAlertDelegate = new TuSdkViewHelper.AlertDelegate()
    {
        @Override
        public void onAlertConfirm(AlertDialog dialog)
        {
            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                    Uri.fromParts("package", MovieAlbumActivity.this.getPackageName(), null));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        }

        @Override
        public void onAlertCancel(AlertDialog dialog)
        {
            finish();
        }
    };

    private void initView()
    {
        mSelectMax = getIntent().getIntExtra("selectMax",1);

        mConfirmButton = (TextView) findViewById(R.id.lsq_next);
        mConfirmButton.setText(R.string.lsq_next);
        mConfirmButton.setOnClickListener(mButtonSafeClickListener);

        mBackButton = (TextView) findViewById(R.id.lsq_back);
        mBackButton.setOnClickListener(mButtonSafeClickListener);

        mRecyclerView = (RecyclerView) findViewById(R.id.lsq_movie_selector_recyclerView);
        GridLayoutManager gridLayoutManager =  new GridLayoutManager(MovieAlbumActivity.this , 4);
        mRecyclerView.setLayoutManager(gridLayoutManager);

        LoadVideoTask loadVideoTask = new LoadVideoTask();
        loadVideoTask.execute();
    }

    /**
     * 检测是否4K视频
     * @param position
     * @return
     */
     private boolean check4K(int position){
         MovieInfo info = mVideoAlbumAdapter.getVideoInfoList().get(position);
         MediaFormat mediaFormat = TuSDKMediaUtils.getVideoFormat(new TuSDKMediaDataSource(info.getPath()));
         TuSdkVideoInfo videoInfo = new TuSdkVideoInfo(mediaFormat);
         if(videoInfo.size.maxSide() >= MAX_SIZE){
             TuSdkViewHelper.toast(MovieAlbumActivity.this,R.string.lsq_loadvideo_failed);
             return true;
         }
         return false;
     }

    /**
     *  RecyclerView中item的点击事件，得到点击item的视频信息
     */
    private MovieAlbumAdapter.OnItemClickListener mOnItemClickListener = new MovieAlbumAdapter.OnItemClickListener()
    {
        @Override
        public void onSelectClick(View view, int position) {
            if(check4K(position)) return;

            mVideoAlbumAdapter.updateSelectedVideoPosition(position);
        }

        @Override
        public void onClick(View view, int position)
        {
            if(check4K(position)) return;

            MovieInfo info = mVideoAlbumAdapter.getVideoInfoList().get(position);
            if(info.getDuration() < MIN_VIDEO_DURATION){
                TuSdk.messageHub().showToast(MovieAlbumActivity.this, R.string.lsq_album_select_min_time);
                return;
            }

            mCurrentPos = position;
            // 视频路径
            List<MovieInfo> videoPath = mVideoAlbumAdapter.getSelectedVideoInfo();
            Intent intent = new Intent(MovieAlbumActivity.this, MovieEditorPreviewActivity.class);
            // 要跳转的视频裁剪类名
            intent.putExtra("cutClassName",getIntent().getStringExtra("cutClassName"));
            intent.putExtra("selectMax", mSelectMax);
            intent.putExtra("currentVideoPath", mVideoAlbumAdapter.getVideoInfoList().get(position));
            intent.putExtra("videoPaths", (Serializable) videoPath);
            startActivityForResult(intent,100);
        }
    };

    /* 按钮点击事件处理 */
    private View.OnClickListener mButtonSafeClickListener = new TuSdkViewHelper.OnSafeClickListener()
    {
        public void onSafeClick(View v)
        {
            if (v == mConfirmButton)
            {
                if (mVideoAlbumAdapter == null || mVideoAlbumAdapter.getSelectedVideoInfo().size() <= 0)
                    TuSdk.messageHub().showToast(MovieAlbumActivity.this, R.string.lsq_select_video_hint);
               else
                    handleIntentAction();
            }
            else if (v == mBackButton)
            {
                finish();
            }
        }
    };

    /**
     * 将扫描的视频添加到集合中
     */
    public List<MovieInfo> getVideoList()
    {
        List<MovieInfo> videoInfo = new ArrayList<>();
        Cursor cursor = getContentResolver().query(
                MediaStore.Video.Media.EXTERNAL_CONTENT_URI, null, null,
                null, "date_added desc");
        while (cursor.moveToNext())
        {
            String path = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA));
            int duration = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DURATION));

            //根据时间长短加入显示列表
            if (duration > 0 && duration < MAX_VIDEO_DURATION)
            {
                videoInfo.add(new MovieInfo(path, duration));
            }
            if(duration == 0){
                TuSDKVideoInfo vInfo = TuSDKMediaUtils.getVideoInfo(path);
                if( vInfo != null && vInfo.durationTimeUs > 0){
                    videoInfo.add(new MovieInfo(path, (int) (vInfo.durationTimeUs / 1000)));
                }
            }
        }
        cursor.close();
        return videoInfo;
    }

    /**
     *  处理跳转事件
     */
    public void handleIntentAction()
    {
        if (mVideoAlbumAdapter == null || mVideoAlbumAdapter.getSelectedVideoInfo().size() <= 0) return;

        // 要跳转的视频裁剪类名
        String className = getIntent().getStringExtra("cutClassName");
        // 视频路径
        List<MovieInfo> videoPath = mVideoAlbumAdapter.getSelectedVideoInfo();

        long totalTime = 0;
        for (MovieInfo info : videoPath){
            totalTime += info.getDuration();
        }

        if(totalTime < MIN_VIDEO_DURATION){
            TuSdk.messageHub().showToast(this, R.string.lsq_album_select_min_time);
            return;
        }

        Intent intent = null;
        try
        {
            intent = new Intent(MovieAlbumActivity.this, Class.forName(className));
            intent.putExtra("videoPaths", (Serializable) videoPath);
            startActivity(intent);
        }
        catch (ClassNotFoundException e)
        {
            e.printStackTrace();
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 100 && resultCode == 100){
            MovieInfo info = (MovieInfo) data.getSerializableExtra("videoInfo");
            if(info != null && !contains(mVideoAlbumAdapter.getSelectedVideoInfo(),info))
                mVideoAlbumAdapter.updateSelectedVideoPosition(mCurrentPos);
            else if(info == null && mVideoAlbumAdapter.getVideoInfoList().size() > 0 && mCurrentPos != -1)
                // 取消选中
                if(contains(mVideoAlbumAdapter.getSelectedVideoInfo(),mVideoAlbumAdapter.getVideoInfoList().get(mCurrentPos)))
                mVideoAlbumAdapter.updateSelectedVideoPosition(mCurrentPos);
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

    /**
     * 相册加载
     */
    class LoadVideoTask extends AsyncTask<Void,Integer,List<MovieInfo>>{

        @Override
        protected List<MovieInfo> doInBackground(Void... voids) {
            return getVideoList();
        }

        @Override
        protected void onPreExecute() {
            TuProgressHub.showToast(MovieAlbumActivity.this,"数据加载中...");
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(List<MovieInfo> movieInfos) {
            TuProgressHub.dismiss();
            mVideoAlbumAdapter = new MovieAlbumAdapter(MovieAlbumActivity.this, movieInfos,mSelectMax);
            mRecyclerView.setAdapter(mVideoAlbumAdapter);
            mVideoAlbumAdapter.setOnItemClickListener(mOnItemClickListener);
        }
    }
}
