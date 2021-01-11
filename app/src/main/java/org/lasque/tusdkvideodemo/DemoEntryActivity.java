package org.lasque.tusdkvideodemo;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import androidx.annotation.NonNull;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RadioButton;

import com.upyun.shortvideo.R;
import org.lasque.tusdk.core.TuSdk;
import org.lasque.tusdk.core.TuSdkContext;
import org.lasque.tusdk.core.secret.StatisticsManger;
import org.lasque.tusdk.core.seles.tusdk.FilterManager;
import org.lasque.tusdk.core.utils.ContextUtils;
import org.lasque.tusdk.core.view.TuSdkViewHelper;
import org.lasque.tusdk.impl.activity.TuFragmentActivity;
import org.lasque.tusdk.impl.view.widget.TuProgressHub;
import org.lasque.tusdk.modules.components.ComponentActType;
import org.lasque.tusdkvideodemo.album.AlbumUtils;
import org.lasque.tusdkvideodemo.editor.MovieEditorCutActivity;
import org.lasque.tusdkvideodemo.record.MovieRecordFullScreenActivity;
import org.lasque.tusdkvideodemo.utils.Constants;
import org.lasque.tusdkvideodemo.utils.PermissionUtils;

/**
 * 首页界面
 */
public class DemoEntryActivity extends TuFragmentActivity {

    /** 布局ID */
    public static final int layoutId = R.layout.demo_entry_activity;

    /** 编辑类ClassName */
    public static final String EDITOR_CLASS = MovieEditorCutActivity.class.getName();

    /** 1为录制  2为编辑  3功能列表 **/
    private int mRequestCode = -1;

    public DemoEntryActivity()
    {

    }

    @Override
    protected void initActivity() {
        super.initActivity();
        this.setRootView(layoutId, 0);

        // 设置应用退出信息ID 一旦设置将触发连续点击两次退出应用事件
        this.setAppExitInfoId(R.string.lsq_exit_info);
    }

    @Override
    protected void initView() {
        super.initView();

        // sdk统计代码，请不要加入您的应用
        StatisticsManger.appendComponent(ComponentActType.sdkComponent);

        // 异步方式初始化滤镜管理器 (注意：如果需要一开启应用马上执行SDK组件，需要做该检测，反之可选)
        // 需要等待滤镜管理器初始化完成，才能使用所有功能
        TuProgressHub.setStatus(this, TuSdkContext.getString("lsq_initing"));
        TuSdk.checkFilterManager(mFilterManagerDelegate);

        ImageButton menuButton = (ImageButton)findViewById(R.id.lsq_app_menu);
        menuButton.setOnClickListener(mClickListener);

        RadioButton recordButton = (RadioButton)findViewById(R.id.lsq_app_record);
        recordButton.setOnClickListener(mClickListener);

        RadioButton clipButton = (RadioButton)findViewById(R.id.lsq_app_clip);
        clipButton.setOnClickListener(mClickListener);


    }


    /**
     * 点击事件监听
     */
    private View.OnClickListener mClickListener = new TuSdkViewHelper.OnSafeClickListener() {
        @Override
        public void onSafeClick(View v) {
            switch (v.getId()){
                case R.id.lsq_app_menu:
                    handleComponentButton();
                    break;
                case R.id.lsq_app_record:
                    handleRecordButton();
                    break;
                case R.id.lsq_app_clip:
                    handleEditorButton();
                    break;
            }
        }
    };

    /**
     * 处理编辑视频按钮操作
     */
    private void handleEditorButton()
    {
        mRequestCode = 2;

        if (PermissionUtils.hasRequiredPermissions(this, getRequiredPermissions()))
        {
            AlbumUtils.openMediaAlbum(EDITOR_CLASS, Constants.MAX_EDITOR_SELECT_MUN);
        }
        else
        {
            PermissionUtils.requestRequiredPermissions(this, getRequiredPermissions());
        }

    }

    /**
     * 开启录制相机
     */
    private void handleRecordButton()
    {

        mRequestCode = 1;
        if (PermissionUtils.hasRequiredPermissions(this, getRequiredPermissions()))
        {
            Intent intent = new Intent(this, MovieRecordFullScreenActivity.class);
            this.startActivity(intent);
        }
        else
        {
            PermissionUtils.requestRequiredPermissions(this, getRequiredPermissions());
        }
    }

    /**
     * 打开示例列表界面
     */
    private void handleComponentButton()
    {

        mRequestCode = 3;
        if (PermissionUtils.hasRequiredPermissions(this, getRequiredPermissions()))
        {
            Intent intent = new Intent(this, ComponentListActivity.class);
            startActivity(intent);
        }
        else
        {
            PermissionUtils.requestRequiredPermissions(this, getRequiredPermissions());
        }
    }

    /**
     * 滤镜管理器委托
     */
    private FilterManager.FilterManagerDelegate mFilterManagerDelegate = new FilterManager.FilterManagerDelegate()
    {
        @Override
        public void onFilterManagerInited(FilterManager manager)
        {
            //使用TuProgressHub或者TuSdk.messageHub()需要tusdk_view_widget_progress_hud_view布局
            TuProgressHub.showSuccess(DemoEntryActivity.this, TuSdkContext.getString("lsq_inited"));
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
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        PermissionUtils.handleRequestPermissionsResult(requestCode, permissions, grantResults, this, mGrantedResultDelgate);
    }

    /**
     * 授予权限的结果，在对话结束后调用
     *
     * @param permissionGranted
     *            true or false, 用户是否授予相应权限
     */
    protected PermissionUtils.GrantedResultDelgate mGrantedResultDelgate = new PermissionUtils.GrantedResultDelgate()
    {
        @Override
        public void onPermissionGrantedResult(boolean permissionGranted)
        {
            if (permissionGranted)
            {
                if(mRequestCode == 1) {
                    Intent intent = new Intent(DemoEntryActivity.this, MovieRecordFullScreenActivity.class);
                    DemoEntryActivity.this.startActivity(intent);
                }else if(mRequestCode == 2) {
                    AlbumUtils.openMediaAlbum(EDITOR_CLASS,Constants.MAX_EDITOR_SELECT_MUN);
                }else{
                    Intent intent = new Intent(DemoEntryActivity.this, ComponentListActivity.class);
                    startActivity(intent);
                }
            }
            else
            {
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
    protected TuSdkViewHelper.AlertDelegate permissionAlertDelegate = new TuSdkViewHelper.AlertDelegate()
    {
        @Override
        public void onAlertConfirm(AlertDialog dialog)
        {
            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                    Uri.fromParts("package", DemoEntryActivity.this.getPackageName(), null));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }

        @Override
        public void onAlertCancel(AlertDialog dialog)
        {

        }
    };
}
