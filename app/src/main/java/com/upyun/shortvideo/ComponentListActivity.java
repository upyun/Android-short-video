/**
 * TuSDKVideoDemo 
 * ComponentListActivity.java
 * 
 * @author Bonan
 * @Date 8:50:13 AM
 * @Copright (c) 2017 tusdk.com. All rights reserved.
 * 
 */
package com.upyun.shortvideo;


import android.Manifest;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.ExpandableListActivity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnGroupClickListener;

import com.upyun.shortvideo.utils.AlbumUtils;
import com.upyun.shortvideo.utils.PermissionUtils;

import org.lasque.tusdk.core.TuSdkContext;
import org.lasque.tusdk.core.utils.ContextUtils;
import org.lasque.tusdk.core.view.TuSdkViewHelper;
import org.lasque.tusdk.core.view.widget.TuSdkNavigatorBar.NavigatorBarButtonInterface;
import org.lasque.tusdk.core.view.widget.TuSdkNavigatorBar.NavigatorBarButtonType;
import org.lasque.tusdk.core.view.widget.TuSdkNavigatorBar.TuSdkNavigatorBarDelegate;
import org.lasque.tusdk.impl.view.widget.TuNavigatorBar;
import org.lasque.tusdk.video.TuSDKVideo;

/**
 * 功能列表界面
 * 
 * @author Bonan
 */
public class ComponentListActivity extends ExpandableListActivity implements TuSdkNavigatorBarDelegate 
{
	/** 最大滑动速度 */
	public static final int MAX_SLIDE_SPEED = 1000;

	/** 最大滑动距离 */
	public static final float MAX_SLIDE_DISTANCE = 0.3f;

	private static final int IMAGE_PICKER_SELECT = 1;

	/** ListView Adapter */
    private ExpandableSamplesListAdapter mSamplesListAdapter;

    /** 导航栏 实现类 */
	private TuNavigatorBar mNavigatorBar;
	
	/** 滑动后退手势 */
	private GestureDetector gdDetector;
	
	private String mClassName;

	/** 需要开启相机的类名 */
	private String mNeedCameraClassName;

	@Override
    public void onCreate(Bundle icicle) 
    {
        super.onCreate(icicle);

        setContentView(R.layout.main_layout);
        
		// 导航栏 实现类
		mNavigatorBar = (TuNavigatorBar) findViewById(R.id.lsq_navigatorBar);
		TuSdkViewHelper.loadView(mNavigatorBar);
		mNavigatorBar.setTitle(String.format("%s %s", TuSdkContext.getString(R.string.app_name), TuSDKVideo.VIDEO_VERSION));
		mNavigatorBar.setBackButtonId(R.id.lsq_backButton);
		mNavigatorBar.showBackButton(true);
		mNavigatorBar.delegate = this;

		// 滑动后退手势
		gdDetector = new GestureDetector(this, gestureListener);

		// 设置 ListView
		initExpandableListView();
    }
    
    /**
     * 初始化 ListView
     */
	private void initExpandableListView() 
	{
        getExpandableListView().setGroupIndicator(null);
        mSamplesListAdapter = new ExpandableSamplesListAdapter(this);
        setListAdapter(mSamplesListAdapter);
        
		int groupCount = getExpandableListView().getCount();
		
		// 默认展开子视图
		for (int i = 0; i < groupCount; i++) 
		{
			getExpandableListView().expandGroup(i);
		}

		// 设置点击组视图时无动作
		getExpandableListView().setOnGroupClickListener(new OnGroupClickListener() {

			@Override
			public boolean onGroupClick(ExpandableListView parent, View v,
					int groupPosition, long id) {

				return true;
			}
		});
	}

	/**
	 * 后退按钮
	 */
    @Override
	public void onNavigatorBarButtonClicked(NavigatorBarButtonInterface button)
	{
		if (button.getType() == NavigatorBarButtonType.back)
		{
			this.finish();
		}
	}

	/** 滑动后退手势监听 */
	GestureDetector.SimpleOnGestureListener gestureListener = new GestureDetector.SimpleOnGestureListener()
	{
		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY)
		{
			// 移动距离
			if (e2.getRawX() - e1.getRawX() < ContextUtils.getScreenSize(ComponentListActivity.this).width * MAX_SLIDE_DISTANCE) return false;

			// 滑动位置
			if (e1.getRawX() > ContextUtils.getScreenSize(ComponentListActivity.this).width * 0.2) return false;

			// 滑动速度
			if (Math.abs(velocityX) < Math.abs(velocityY) || velocityX < MAX_SLIDE_SPEED) return false;

			// 关闭界面
			ComponentListActivity.this.finish();

			return true;
		}
	};

    @Override
    public boolean onChildClick(ExpandableListView parent, View view, int group, int child, long id) 
    {
        ExpandableSamplesListAdapter.SampleItem sample = (ExpandableSamplesListAdapter.SampleItem) mSamplesListAdapter.getChild(group, child);

        if(sample.className == null) return super.onChildClick(parent, view, group, child, id);
        
        mClassName = sample.className;
        
       // 需要先打开相册选取
        if(sample.needOpenAlbum)
        {
			AlbumUtils.openVideoAlbum(mClassName);
            return super.onChildClick(parent, view, group, child, id);
        }
        
        // 需要先判断是否有相机权限
        if(sample.needOpenCamera)
        {
			if (!PermissionUtils.hasRequiredPermissions(this, getRequiredPermissions()))
			{
				PermissionUtils.requestRequiredPermissions(this, getRequiredPermissions());
				mNeedCameraClassName = sample.className;
				return super.onChildClick(parent, view, group, child, id);
			}
        }
        
        startActivityWithClassName(sample.className, null);

        return super.onChildClick(parent, view, group, child, id);
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
				Manifest.permission.CAMERA,
				Manifest.permission.RECORD_AUDIO
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
				startActivityWithClassName(mNeedCameraClassName, null);
			}
			else
			{
				String msg = TuSdkContext.getString("lsq_camera_no_access", ContextUtils.getAppName(ComponentListActivity.this));

				TuSdkViewHelper.alert(permissionAlertDelegate, ComponentListActivity.this, TuSdkContext.getString("lsq_camera_alert_title"),
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
					Uri.fromParts("package", ComponentListActivity.this.getPackageName(), null));
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(intent);
		}

		@Override
		public void onAlertCancel(AlertDialog dialog)
		{

		}
	};

	/**
	 * 根据 className 打开对应 Activity
	 * 
	 * @param className
	 * @param path
	 */
	private void startActivityWithClassName(String className, String path) 
	{
        Intent intent = null;

        try {
            intent = new Intent(ComponentListActivity.this, Class.forName(className));
            intent.putExtra("videoPath", path);
        } catch (ClassNotFoundException e) {
        	e.printStackTrace();
        }

        startActivity(intent); 
	}

	/**
	 * 分发触摸事件
	 */
	@Override
	public boolean dispatchTouchEvent(MotionEvent ev)
	{
		gdDetector.onTouchEvent(ev);
		return super.dispatchTouchEvent(ev);
	}
}
