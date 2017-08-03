/**
 * TuSDKVideoDemo
 * DemoEntryActivity.java
 *
 * @author XiaShengCui
 * @Date Jun 1, 2017 7:34:44 PM
 * @Copyright: (c) 2017 tusdk.com. All rights reserved.
 */
package com.upyun.shortvideo;

import org.lasque.tusdk.core.TuSdk;
import org.lasque.tusdk.core.TuSdkContext;
import org.lasque.tusdk.core.secret.StatisticsManger;
import org.lasque.tusdk.core.seles.tusdk.FilterManager;
import org.lasque.tusdk.core.seles.tusdk.FilterManager.FilterManagerDelegate;
import org.lasque.tusdk.core.utils.StringHelper;
import org.lasque.tusdk.core.utils.hardware.CameraHelper;
import org.lasque.tusdk.impl.activity.TuFragmentActivity;
import org.lasque.tusdk.impl.view.widget.TuProgressHub;
import org.lasque.tusdk.modules.components.ComponentActType;
import com.upyun.shortvideo.suite.MoviePreviewAndCutActivity;
import com.upyun.shortvideo.utils.UriUtils;

import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.widget.RelativeLayout;

/**
 * 首页界面
 */
public class DemoEntryActivity extends TuFragmentActivity {
    private static final int IMAGE_PICKER_SELECT = 1;
    /**
     * 布局ID
     */
    public static final int layoutId = com.upyun.shortvideo.R.layout.demo_entry_activity;

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
                    handleImportButton();
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
     * 开启录制相机
     */
    private void handleRecordButton() {
        // 如果不支持摄像头显示警告信息
        if (CameraHelper.showAlertIfNotSupportCamera(this, true)) return;

        Intent intent = new Intent(this, MovieRecordAndImportEditorActivity.class);
        this.startActivity(intent);
    }

    /**
     * 打开示例列表界面
     */
    private void handleComponentButton() {
        Intent intent = new Intent(this, ComponentListActivity.class);
        startActivity(intent);
    }

    private void handleImportButton() {
        Intent pickIntent = new Intent(Intent.ACTION_GET_CONTENT);
        pickIntent.setType("video/*");
        pickIntent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(pickIntent, IMAGE_PICKER_SELECT);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if(resultCode != RESULT_OK) return;

        Uri selectedMediaUri = data.getData();

        String path = UriUtils.getFileAbsolutePath(getApplicationContext(),selectedMediaUri);

        if(!StringHelper.isEmpty(path))
        {
            Intent intent = new Intent(this, MoviePreviewAndCutActivity.class);
            intent.putExtra("videoPath", path);
            startActivity(intent);
        }
        else
        {
            TuSdk.messageHub().showToast(getApplicationContext(), com.upyun.shortvideo.R.string.lsq_video_empty_error);
        }
    }
}
