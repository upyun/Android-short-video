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

import org.lasque.tusdk.core.TuSdk;
import org.lasque.tusdk.core.TuSdkContext;
import org.lasque.tusdk.core.utils.ContextUtils;
import org.lasque.tusdk.core.utils.StringHelper;
import org.lasque.tusdk.core.utils.hardware.CameraHelper;
import org.lasque.tusdk.core.view.TuSdkViewHelper;
import org.lasque.tusdk.core.view.widget.TuSdkNavigatorBar.NavigatorBarButtonInterface;
import org.lasque.tusdk.core.view.widget.TuSdkNavigatorBar.NavigatorBarButtonType;
import org.lasque.tusdk.core.view.widget.TuSdkNavigatorBar.TuSdkNavigatorBarDelegate;
import org.lasque.tusdk.impl.view.widget.TuNavigatorBar;
import org.lasque.tusdk.video.TuSDKVideo;
import com.upyun.shortvideo.utils.UriUtils;

import android.app.ExpandableListActivity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnGroupClickListener;

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
        	openSystemAlbum();
            return super.onChildClick(parent, view, group, child, id);
        }
        
        // 需要先判断是否有相机权限
        if(sample.needOpenCamera)
        {
        	if(CameraHelper.showAlertIfNotSupportCamera(this, true)) 
        		return super.onChildClick(parent, view, group, child, id);
        }
        
        startActivityWithClassName(sample.className, null);

        return super.onChildClick(parent, view, group, child, id);
    }

    /**
     * 打开系统相册选取视频
     */
	private void openSystemAlbum()
	{
		Intent pickIntent = new Intent(Intent.ACTION_GET_CONTENT);
		pickIntent.setType("video/*");
		pickIntent.addCategory(Intent.CATEGORY_OPENABLE);
		startActivityForResult(pickIntent, IMAGE_PICKER_SELECT);
	}
	
	public void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		if (resultCode == RESULT_OK) 
		{
		    Uri selectedMediaUri = data.getData();
		    
		    String path = UriUtils.getFileAbsolutePath(getApplicationContext(), selectedMediaUri);
		    
		    if(!StringHelper.isEmpty(path) && mClassName != null)
		    {
			    startActivityWithClassName(mClassName, path);
		    }
		    else
		    {
		    	TuSdk.messageHub().showToast(getApplicationContext(), R.string.lsq_video_empty_error);
		    }
		}
	}

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
