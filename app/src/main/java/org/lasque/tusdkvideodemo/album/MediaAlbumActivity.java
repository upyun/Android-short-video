/**
 *  TuSDK
 *  TuSDKVideoDemo3
 *  MediaAlbumActivity.java
 *  @author  H.ys
 *  @Date    2019/6/3 15:23
 *  @Copyright 	(c) 2019 tusdk.com. All rights reserved.
 *
 *
 */

package org.lasque.tusdkvideodemo.album;


import android.Manifest;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;
import android.view.View;
import android.widget.TextView;

import org.lasque.tusdk.core.TuSdkContext;
import org.lasque.tusdk.core.utils.ContextUtils;
import org.lasque.tusdk.core.view.TuSdkViewHelper;
import org.lasque.tusdkvideodemo.MediaAlbumAdapter;
import com.upyun.shortvideo.R;
import org.lasque.tusdkvideodemo.ScreenAdapterActivity;
import org.lasque.tusdkvideodemo.utils.PermissionUtils;
import org.lasque.tusdkvideodemo.views.TabPagerIndicator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MediaAlbumActivity extends ScreenAdapterActivity {

    private TabPagerIndicator mMediaTabPagerIndicator;

    private ViewPager mMediaViewPager;

    private MediaAlbumAdapter mMediaAlbumAdapter;

    private MovieAlbumFragment mMovieAlbumFragment;

    private ImageAlbumFragment mImageAlbumFragment;

    /* 确定按钮 */
    protected TextView mConfirmButton;
    /* 返回按钮 */
    protected TextView mBackButton;

    private View.OnClickListener mConfirmClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Fragment currentFragment = mMediaAlbumAdapter.getFragmentList().get(mMediaViewPager.getCurrentItem());
            if (currentFragment instanceof ImageAlbumFragment){
                mImageAlbumFragment.getNextStepClickListener().onClick(v);
            } else if (currentFragment instanceof MovieAlbumFragment){
                mMovieAlbumFragment.getNextStepClickListener().onClick(v);
            }
        }
    };

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
                setContentView(R.layout.media_album_activity);
                initViews();
            }
            else
            {
                String msg = TuSdkContext.getString("lsq_album_no_access", ContextUtils.getAppName(MediaAlbumActivity.this));

                TuSdkViewHelper.alert(permissionAlertDelegate, MediaAlbumActivity.this, TuSdkContext.getString("lsq_album_alert_title"),
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
                    Uri.fromParts("package", MediaAlbumActivity.this.getPackageName(), null));
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


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (PermissionUtils.hasRequiredPermissions(this, getRequiredPermissions()))
        {
            setContentView(R.layout.media_album_activity);
            initViews();
        }
        else
        {
            PermissionUtils.requestRequiredPermissions(this, getRequiredPermissions());
        }
    }

    private void initViews() {

        mConfirmButton = (TextView) findViewById(R.id.lsq_next);
        mConfirmButton.setText(R.string.lsq_next);
        mConfirmButton.setOnClickListener(mConfirmClickListener);

        mBackButton = (TextView) findViewById(R.id.lsq_back);
        mBackButton.setOnClickListener(new TuSdkViewHelper.OnSafeClickListener() {
            @Override
            public void onSafeClick(View v) {
                finish();
            }
        });

        mMediaTabPagerIndicator = findViewById(R.id.lsq_media_album_tab);
        mMediaViewPager = findViewById(R.id.lsq_media_view_pager);
        mMovieAlbumFragment = new MovieAlbumFragment();
        mImageAlbumFragment = new ImageAlbumFragment();
        List<Fragment> fragments = new ArrayList<>();
        fragments.add(mMovieAlbumFragment);
        fragments.add(mImageAlbumFragment);
        mMediaAlbumAdapter = new MediaAlbumAdapter(getSupportFragmentManager(),fragments);
        mMediaViewPager.setAdapter(mMediaAlbumAdapter);
        mMediaViewPager.setOffscreenPageLimit(1);
        mMediaTabPagerIndicator.setViewPager(mMediaViewPager,0);
        mMediaTabPagerIndicator.setTabItems(Arrays.asList("视频","照片"));
    }

    public void setEnable(boolean enable){
        mMediaTabPagerIndicator.setEnabled(enable);
        mMediaViewPager.setEnabled(enable);
        mConfirmButton.setEnabled(enable);
        mBackButton.setEnabled(enable);
        mImageAlbumFragment.setIsEnable(enable);
        mMovieAlbumFragment.setIsEnable(enable);

    }
}
