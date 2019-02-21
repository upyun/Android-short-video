/**
 * TuSDKVideoDemo
 * DemoEntryActivity.java
 *
 * @author XiaShengCui
 * @Date Jun 1, 2017 7:34:44 PM
 * @Copyright: (c) 2017 tusdk.com. All rights reserved.
 */
package com.upyun.shortvideo;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.RelativeLayout;

import com.upyun.shortvideo.editor.MovieEditorCutActivity;
import com.upyun.shortvideo.record.MovieRecordFullScreenActivity;
import com.upyun.shortvideo.utils.Constants;
import com.upyun.shortvideo.utils.PermissionUtils;

import org.lasque.tusdk.core.TuSdk;
import org.lasque.tusdk.core.TuSdkContext;
import org.lasque.tusdk.core.secret.StatisticsManger;
import org.lasque.tusdk.core.seles.tusdk.FilterManager;
import org.lasque.tusdk.core.seles.tusdk.FilterManager.FilterManagerDelegate;
import org.lasque.tusdk.core.utils.ContextUtils;
import org.lasque.tusdk.core.view.TuSdkViewHelper;
import org.lasque.tusdk.impl.activity.TuFragmentActivity;
import org.lasque.tusdk.impl.view.widget.TuProgressHub;
import org.lasque.tusdk.modules.components.ComponentActType;

import static com.upyun.shortvideo.album.AlbumUtils.openVideoAlbum;

/**
 * 首页界面
 */
public class DemoEntryActivity extends TuFragmentActivity {
    private static final int IMAGE_PICKER_SELECT = 1;

    private int mRequestCode = -1;
    /**
     * 布局ID
     */
    public static final int layoutId = com.upyun.shortvideo.R.layout.entry_activity;

    /**
     * 编辑类ClassName
     */
    public static final String EDITOR_CLASS = MovieEditorCutActivity.class.getName();


    public DemoEntryActivity() {

    }

    /**
     * 初始化控制器
     */
    @Override
    protected void initActivity() {
        super.initActivity();
        this.setRootView(layoutId, 0);

        // 设置应用退出信息ID 一旦设置将触发连续点击两次退出应用事件
        this.setAppExitInfoId(com.upyun.shortvideo.R.string.lsq_exit_info);
    }

    /**
     * 初始化视图
     */
    @Override
    protected void initView() {
        super.initView();

        // sdk统计代码，请不要加入您的应用
        StatisticsManger.appendComponent(ComponentActType.sdkComponent);

        // 异步方式初始化滤镜管理器 (注意：如果需要一开启应用马上执行SDK组件，需要做该检测，反之可选)
        // 需要等待滤镜管理器初始化完成，才能使用所有功能
        TuProgressHub.setStatus(this, TuSdkContext.getString("lsq_initing"));
        TuSdk.checkFilterManager(mFilterManagerDelegate);

        RelativeLayout recordLayout = (RelativeLayout) findViewById(com.upyun.shortvideo.R.id.lsq_record_layout);
        recordLayout.setOnClickListener(mClickListener);

        RelativeLayout editorLayout = (RelativeLayout) findViewById(com.upyun.shortvideo.R.id.lsq_editor_layout);
        editorLayout
                .setOnClickListener(mClickListener);

        RelativeLayout playLayout = (RelativeLayout) findViewById(com.upyun.shortvideo.R.id.lsq_play_layout);
        playLayout
                .setOnClickListener(mClickListener);

//		RelativeLayout componentLayout= (RelativeLayout) findViewById(R.id.lsq_component_layout);
//		componentLayout.setOnClickListener(mClickListener);
    }

    /**
     * 滤镜管理器委托
     */
    private FilterManagerDelegate mFilterManagerDelegate = new FilterManagerDelegate() {
        @Override
        public void onFilterManagerInited(FilterManager manager) {
            TuProgressHub.showSuccess(DemoEntryActivity.this, TuSdkContext.getString("lsq_inited"));
        }
    };

    /**
     * 点击事件监听
     */
    private View.OnClickListener mClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case com.upyun.shortvideo.R.id.lsq_record_layout:
                    handleRecordButton();
                    break;

                case com.upyun.shortvideo.R.id.lsq_editor_layout:
                    handleEditorButton();
                    break;

                case R.id.lsq_play_layout:
                    handlePlayButton();
                    break;
            }
        }
    };

    private void handlePlayButton() {
        Intent intent = new Intent(this, PlayVideoActivity.class);
        startActivity(intent);
    }

    /**
     * 处理编辑视频按钮操作
     */
    private void handleEditorButton() {
        mRequestCode = 2;

        if (PermissionUtils.hasRequiredPermissions(this, getRequiredPermissions())) {
            openVideoAlbum(EDITOR_CLASS, Constants.MAX_EDITOR_SELECT_MUN);
        } else {
            PermissionUtils.requestRequiredPermissions(this, getRequiredPermissions());
        }

    }

    /**
     * 开启录制相机
     */
    private void handleRecordButton() {

        mRequestCode = 1;
        if (PermissionUtils.hasRequiredPermissions(this, getRequiredPermissions())) {
            Intent intent = new Intent(this, MovieRecordFullScreenActivity.class);
            this.startActivity(intent);
        } else {
            PermissionUtils.requestRequiredPermissions(this, getRequiredPermissions());
        }
    }


//    @Override
//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        if (resultCode != RESULT_OK) return;
//
//        Uri selectedMediaUri = data.getData();
//
//        String path = UriUtils.getFileAbsolutePath(getApplicationContext(), selectedMediaUri);
//
//        if (!StringHelper.isEmpty(path)) {
//            Intent intent = new Intent(this, MovieEditorCutActivity.class);
////            Intent intent = new Intent(this, EditorSettingActivity.class);
//            intent.putExtra("videoPath", path);
//            startActivity(intent);
//        } else {
//            TuSdk.messageHub().showToast(getApplicationContext(), com.upyun.shortvideo.R.string.lsq_video_empty_error);
//        }
//    }

    /**
     * 组件运行需要的权限列表
     *
     * @return 列表数组
     */
    @TargetApi(Build.VERSION_CODES.M)
    protected String[] getRequiredPermissions() {
        String[] permissions = new String[]{
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA,
                Manifest.permission.RECORD_AUDIO,
                Manifest.permission.READ_PHONE_STATE,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_WIFI_STATE
        };

        return permissions;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        PermissionUtils.handleRequestPermissionsResult(requestCode, permissions, grantResults, this, mGrantedResultDelgate);
    }

    /**
     * 授予权限的结果，在对话结束后调用
     *
     * @param permissionGranted
     * true or false, 用户是否授予相应权限
     */
    protected PermissionUtils.GrantedResultDelgate mGrantedResultDelgate = new PermissionUtils.GrantedResultDelgate() {
        @Override
        public void onPermissionGrantedResult(boolean permissionGranted) {
            if (permissionGranted) {
                if (mRequestCode == 1) {
                    Intent intent = new Intent(DemoEntryActivity.this, MovieRecordFullScreenActivity.class);
                    DemoEntryActivity.this.startActivity(intent);
                } else if (mRequestCode == 2) {
                    openVideoAlbum(EDITOR_CLASS, Constants.MAX_EDITOR_SELECT_MUN);
                }
            } else {
                String msg = TuSdkContext.getString("lsq_camera_no_access", ContextUtils.getAppName(DemoEntryActivity.this));

                TuSdkViewHelper.alert(permissionAlertDelegate, DemoEntryActivity.this, TuSdkContext.getString("lsq_camera_alert_title"),
                        msg, TuSdkContext.getString("lsq_button_close"), TuSdkContext.getString("lsq_button_setting")
                );
            }
        }
    };

    /**
     * 权限警告提示框点击事件回调
     */
    protected TuSdkViewHelper.AlertDelegate permissionAlertDelegate = new TuSdkViewHelper.AlertDelegate() {
        @Override
        public void onAlertConfirm(AlertDialog dialog) {
            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                    Uri.fromParts("package", DemoEntryActivity.this.getPackageName(), null));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }

        @Override
        public void onAlertCancel(AlertDialog dialog) {

        }
    };
}
