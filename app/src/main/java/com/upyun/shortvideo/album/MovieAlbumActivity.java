/**
 * TuSDKVideoDemo
 * MovieAlbumActivity.java
 *
 * @author  loukang
 * @Date  Oct 9, 2017 10:43:14 AM
 * @Copright (c) 2017 tusdk.com. All rights reserved.
 *
 */
package com.upyun.shortvideo.album;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.upyun.shortvideo.utils.PermissionUtils;

import org.lasque.tusdk.core.TuSdk;
import org.lasque.tusdk.core.TuSdkContext;
import org.lasque.tusdk.core.utils.ContextUtils;
import org.lasque.tusdk.core.view.TuSdkViewHelper;
import com.upyun.shortvideo.R;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 视频选择相册
 */
public class MovieAlbumActivity extends Activity
{
    private static final String MOVIE_EDIT = "MovieEditorActivity";
    /* 最小视频时长(单位：ms) */
    private static int MIN_VIDEO_DURATION = 3000;
    /* 最大视频时长(单位：ms) */
    private static int MAX_VIDEO_DURATION = 60000;
    /* 确定按钮 */
    protected TextView mConfirmButton;
    /* 标题 */
    private TextView mTitleTextView;
   /* 返回按钮 */
    protected TextView mBackButton;
   /* 最大选择数量 */
    protected int mSelectMax = 1;

    private RecyclerView mRecyclerView;

    private MovieAlbumAdapter mVideoAlbumAdapter;

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
        mConfirmButton.setText(R.string.lsq_text_confirm);
        mConfirmButton.setOnClickListener(mButtonSafeClickListener);

        mTitleTextView = (TextView) findViewById(R.id.lsq_title);
        mTitleTextView.setText(R.string.lsq_video_selected);
        mBackButton = (TextView) findViewById(R.id.lsq_back);
        mBackButton.setOnClickListener(mButtonSafeClickListener);

        mRecyclerView = (RecyclerView) findViewById(R.id.lsq_movie_selector_recyclerView);
        GridLayoutManager gridLayoutManager =  new GridLayoutManager(MovieAlbumActivity.this , 3);
        mRecyclerView.setLayoutManager(gridLayoutManager);

        mVideoAlbumAdapter = new MovieAlbumAdapter(MovieAlbumActivity.this,getVideoList(),mSelectMax);
        mRecyclerView.setAdapter(mVideoAlbumAdapter);
        mVideoAlbumAdapter.setOnItemClickListener(mOnItemClickListener);
    }

    /**
     *  RecyclerView中item的点击事件，得到点击item的视频信息
     */
    private MovieAlbumAdapter.OnItemClickListener mOnItemClickListener = new MovieAlbumAdapter.OnItemClickListener()
    {
        @Override
        public void onClick(View view, int position)
        {
            mVideoAlbumAdapter.updateSelectedVideoPosition(position);
        }
    };

    /* 按钮点击事件处理 */
    private View.OnClickListener mButtonSafeClickListener = new TuSdkViewHelper.OnSafeClickListener()
    {
        public void onSafeClick(View v)
        {
            if (v == mConfirmButton)
            {
                if (mVideoAlbumAdapter == null || mVideoAlbumAdapter.getSelectedVideoInfo() == null)
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
                null, null);
        while (cursor.moveToNext())
        {
            String path = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA));
            int duration = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DURATION));

            if (duration > MIN_VIDEO_DURATION && duration < MAX_VIDEO_DURATION)
            {
                videoInfo.add(new MovieInfo(path, duration));
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
        if (mVideoAlbumAdapter == null || mVideoAlbumAdapter.getSelectedVideoInfo() == null) return;

        // 要跳转的视频裁剪类名
        String className = getIntent().getStringExtra("cutClassName");
        // 视频路径
        List<MovieInfo> videoPath = mVideoAlbumAdapter.getSelectedVideoInfo();

        Intent intent = null;
        try
        {
            intent = new Intent(MovieAlbumActivity.this, Class.forName(className));
            if(videoPath.size() == 1)
            {
                intent.putExtra("videoPath", videoPath.get(0).getPath());
            }
            else
            {
                intent.putExtra("videoPaths", (Serializable) videoPath);
            }

            if(MOVIE_EDIT.equals(className))
            {
                intent.putExtra("ratioAdaption", false);
            }
            startActivity(intent);
            finish();
        }
        catch (ClassNotFoundException e)
        {
            e.printStackTrace();
        }

    }
}
