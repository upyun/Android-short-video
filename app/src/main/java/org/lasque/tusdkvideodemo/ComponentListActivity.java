/**
 * TuSDKVideoDemo 
 * ComponentListActivity.java
 * 
 * @author Bonan
 * @Date 8:50:13 AM
 * @Copright (c) 2017 tusdk.com. All rights reserved.
 * 
 */
package org.lasque.tusdkvideodemo;


import android.Manifest;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.upyun.shortvideo.R;
import org.lasque.tusdk.core.TuSdkContext;
import org.lasque.tusdk.core.utils.ContextUtils;
import org.lasque.tusdk.core.view.TuSdkViewHelper;
import org.lasque.tusdk.core.view.widget.TuSdkNavigatorBar.NavigatorBarButtonInterface;
import org.lasque.tusdk.core.view.widget.TuSdkNavigatorBar.NavigatorBarButtonType;
import org.lasque.tusdk.core.view.widget.TuSdkNavigatorBar.TuSdkNavigatorBarDelegate;
import org.lasque.tusdk.impl.TuSpecialScreenHelper;
import org.lasque.tusdk.impl.view.widget.TuNavigatorBar;
import org.lasque.tusdk.video.TuSDKVideo;
import org.lasque.tusdkvideodemo.album.AlbumUtils;
import org.lasque.tusdkvideodemo.utils.PermissionUtils;

/**
 * 功能列表界面
 * 
 * @author xujie
 */
public class ComponentListActivity extends ListActivity implements TuSdkNavigatorBarDelegate
{
	/** 最大滑动速度 */
	public static final int MAX_SLIDE_SPEED = 1000;

	/** 最大滑动距离 */
	public static final float MAX_SLIDE_DISTANCE = 0.3f;

	/** ListView Adapter */
    private ListAdapter mSamplesListAdapter;

    /** 导航栏 实现类 */
	private TuNavigatorBar mNavigatorBar;
	
	/** 滑动后退手势 */
	private GestureDetector gdDetector;

	private String mClassName;

	/** 需要开启相机的类名 */
	private String mNeedCameraClassName;

	private TextView tvCopyrightInfo;

	@Override
    public void onCreate(Bundle icicle) 
    {
		if(TuSpecialScreenHelper.isNotchScreen())
		{
			setTheme(android.R.style.Theme_NoTitleBar);
		}
        super.onCreate(icicle);

        setContentView(R.layout.more_layout);
        
		// 导航栏 实现类
		mNavigatorBar = (TuNavigatorBar) findViewById(R.id.lsq_navigatorBar);
		TuSdkViewHelper.loadView(mNavigatorBar);
		mNavigatorBar.setBackButtonId(R.id.lsq_backButton);
		mNavigatorBar.showBackButton(true);
		mNavigatorBar.delegate = this;
		tvCopyrightInfo = findViewById(R.id.tv_copyright_info);
		tvCopyrightInfo.setText(String.format("TuSDK Video %s \n © 2020 TUTUCLOUD.COM", TuSDKVideo.VIDEO_VERSION));
		// 滑动后退手势
		gdDetector = new GestureDetector(this, gestureListener);

		// 设置 ListView
		initListView();
    }
    
    /**
     * 初始化 ListView
     */
	private void initListView()
	{
        mSamplesListAdapter = new SamplesListAdapter(this);
        setListAdapter(mSamplesListAdapter);
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
	protected void onListItemClick(ListView parent, View view, int position, long id) {
		SamplesListAdapter.SampleItem sample = (SamplesListAdapter.SampleItem) mSamplesListAdapter.getItem(position);

		if(sample.className == null) return ;

		mClassName = sample.className;

		// 需要先打开相册选取
		if(sample.OpenAlbumForPicNum >= 1)
		{
			AlbumUtils.openVideoAlbum(mClassName,sample.OpenAlbumForPicNum);
			return ;
		}

		// 需要先判断是否有相机权限
		if(sample.needOpenCamera)
		{
			if (!PermissionUtils.hasRequiredPermissions(this, getRequiredPermissions()))
			{
				PermissionUtils.requestRequiredPermissions(this, getRequiredPermissions());
				mNeedCameraClassName = sample.className;
				return ;
			}
		}
		//录制跳编辑
		if(position == mSamplesListAdapter.getCount() - 1){
			try {
				Intent intent = new Intent(ComponentListActivity.this, Class.forName(sample.className));
				intent.putExtra("isDirectEdit",true);
				startActivity(intent);
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		}else {
			startActivityWithClassName(sample.className, null);
		}
		super.onListItemClick(parent, view, position, id);
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
