/**
 * TuSDKVideoDemo
 * MultipleCameraView.java
 *
 * @author     LiuHang
 * @Date:      Jun 3, 2017 3:48:19 PM
 * @Copyright: (c) 2017 tusdk.com. All rights reserved.
 *
 */
package com.upyun.shortvideo.views.record;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.GridLayoutManager;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.upyun.shortvideo.utils.ClickAndLongPressedInterface;
import com.upyun.shortvideo.utils.ClickAndLongPressedListener;
import com.upyun.shortvideo.utils.Constants;
import com.upyun.shortvideo.views.CompoundDrawableTextView;
import com.upyun.shortvideo.views.ConfigViewSeekBar;
import com.upyun.shortvideo.views.FilterCellView;
import com.upyun.shortvideo.views.FilterConfigSeekbar;
import com.upyun.shortvideo.views.FilterConfigView;
import com.upyun.shortvideo.views.FilterListView;
import com.upyun.shortvideo.views.RoundProgressBar;
import com.upyun.shortvideo.views.StickerCellView;
import com.upyun.shortvideo.views.StickerListView;

import org.json.JSONArray;
import org.json.JSONObject;
import org.lasque.tusdk.core.TuSdk;
import org.lasque.tusdk.core.TuSdkContext;
import org.lasque.tusdk.core.seles.SelesParameters;
import org.lasque.tusdk.core.seles.SelesParameters.FilterArg;
import org.lasque.tusdk.core.seles.sources.SelesVideoCameraInterface;
import org.lasque.tusdk.core.seles.tusdk.FilterWrap;
import org.lasque.tusdk.core.struct.TuSdkSize;
import org.lasque.tusdk.core.utils.FileHelper;
import org.lasque.tusdk.core.utils.RectHelper;
import org.lasque.tusdk.core.utils.TLog;
import org.lasque.tusdk.core.utils.ThreadHelper;
import org.lasque.tusdk.core.utils.hardware.CameraConfigs.CameraFlash;
import org.lasque.tusdk.core.utils.hardware.TuSDKRecordVideoCamera;
import org.lasque.tusdk.core.utils.hardware.TuSDKRecordVideoCamera.RecordError;
import org.lasque.tusdk.core.utils.hardware.TuSDKRecordVideoCamera.RecordState;
import org.lasque.tusdk.core.utils.hardware.TuSdkStillCameraAdapter.CameraState;
import org.lasque.tusdk.core.utils.image.AlbumHelper;
import org.lasque.tusdk.core.utils.json.JsonHelper;
import org.lasque.tusdk.core.utils.sqllite.ImageSqlHelper;
import org.lasque.tusdk.core.video.TuSDKVideoResult;
import org.lasque.tusdk.core.view.recyclerview.TuSdkTableView.TuSdkTableViewItemClickDelegate;
import org.lasque.tusdk.core.view.widget.button.TuSdkTextButton;
import org.lasque.tusdk.impl.view.widget.TuSeekBar;
import org.lasque.tusdk.modules.view.widget.sticker.StickerGroup;
import org.lasque.tusdk.modules.view.widget.sticker.StickerLocalPackage;
import org.lasque.tusdk.api.movie.player.TuSDKMoviePlayer;
import org.lasque.tusdk.api.movie.player.TuSDKMoviePlayer.PlayerState;
import org.lasque.tusdk.api.movie.player.TuSDKMoviePlayer.TuSDKMoviePlayerDelegate;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 多功能相机界面视图
 */
public class MultipleCameraView extends FrameLayout
{
	/** 资源类型--图片类型 */
	private final int RES_TYPE_IMAGE = 0;
	/** 资源类型--视频类型 */
	private final int RES_TYPE_VIDEO = 1;
	
	/**相机 */
    protected TuSDKRecordVideoCamera mTuSDKVideoCamera;
    
	/** 自定义的圆形进度条*/
	private RoundProgressBar mRoundProgressBar;
	/**返回按钮 */
	private ImageButton mBackButton;
	/**相机镜头切换按钮 */
	private ImageButton mToggleButton;
	/**闪光灯按钮 */
	private ImageButton mFlashButton;
	/**滤镜按钮 */
	private CompoundDrawableTextView mSmartBeautyButton;
	/**动态贴纸按钮 */
	private CompoundDrawableTextView mStickerButton;
	/**闪光灯状态 */
	private boolean mFlashEnabled = false;

	/** 上方的触摸视图 */
	private FrameLayout mTouchView;
	/** 参数调节视图 */
	protected FilterConfigView mConfigView;
	/** 滤镜栏视图 */
	protected FilterListView mFilterListView;
	/** 贴纸栏视图 */
	protected StickerListView mStickerListView;
	/** 滤镜底部栏 */
	private RelativeLayout mFilterBottomView;
	/** 贴纸底部栏 */
	private RelativeLayout mStickerBottomView;

	// 记录是否是首次进入录制页面
	private boolean mIsFirstEntry = true;
	
	// 用于记录焦点位置
	private int mFocusPostion = 1;

	// 记录当前滤镜
    private FilterWrap mSelesOutInput;

	// 滤镜Tab
	private TuSdkTextButton mFilterTab;
	// 美颜布局
	private RelativeLayout mFilterLayout;
	// 美颜Tab
	private TuSdkTextButton mBeautyTab;
	// 美颜布局
	private LinearLayout mBeautyLayout;
	// 磨皮调节栏 
	private ConfigViewSeekBar mSmoothingBarLayout;
	// 大眼调节栏
	private ConfigViewSeekBar mEyeSizeBarLayout;
	// 瘦脸调节栏 
	private ConfigViewSeekBar mChinSizeBarLayout;
	// 用于记录当前调节栏效果系数
	private float mMixiedProgress = -1.0f;
	// 用于记录当前调节栏磨皮系数
	private float mSmoothingProgress = -1.0f;
	// 用于记录当前调节栏大眼系数
	private float mEyeSizeProgress = -1.0f;
	// 用于记录当前调节栏瘦脸系数
	private float mChinSizeProgress = -1.0f;

	/** 底部按钮布局 */
	private RelativeLayout mBottomBtnLayout;
	/** 预览图片控件 */
	private ImageView mPreviewImg;
	/** 播放视频SurfaceView */
	private SurfaceView mSurfaceView;
	/** 预览界面删除按钮 */
	private CompoundDrawableTextView mDeltButton;
	/** 预览界面保存按钮 */
	private CompoundDrawableTextView mSaveButton;
	/** 预览布局 */
	private RelativeLayout mPreviewLayout;
	/** 预览视频布局 */
	private RelativeLayout mPreviewVideoLayout;
	/** 处理的资源类型：默认图片类型 */
	private int mResType = RES_TYPE_IMAGE;
	/** 视频资源路径 */ 
	private String mVideoPath;
	/** 视频播放器 */
	private TuSDKMoviePlayer mMoviePlayer;
	/** 拍照获得的Bitmap */
	private Bitmap mCaptureBitmap;
	private Activity mActivity;
	private ClickAndLongPressedListener mClickAndLongPressListener = new ClickAndLongPressedListener();
	/** 录制视频动作委托 */
	private TuSDKMultipleCameraDelegate mDelegate;

	/**
	 * 录制视频动作委托
	 */
	public interface TuSDKMultipleCameraDelegate
	{
		/**
		 * 暂停相机
		 */
		void pauseCameraCapture();
		
		/**
		 * 恢复相机
		 */
		void resumeCameraCapture();
		
		/**
		 * 视频成功通知
		 */
		void onMovieSaveSucceed(String videoPath);
	}
	
	public MultipleCameraView(Context context) 
	{
		super(context);
	}

	public MultipleCameraView(Context context, AttributeSet attrs) 
	{
		super(context, attrs);
		initView(context);
	}
	
	public void setDelegate(TuSDKMultipleCameraDelegate delegate)
	{
		mDelegate = delegate;
	}
	
	public TuSDKMultipleCameraDelegate getDelegate()
	{
		return mDelegate;
	}
	
	@SuppressLint( "ClickableViewAccessibility") 
	private void initView(Context context)
	{
		mIsFirstEntry = true;
		//圆形进度条
		LayoutInflater.from(context).inflate(com.upyun.shortvideo.R.layout.multiple_camera_view, this,true);
		mRoundProgressBar = (RoundProgressBar) findViewById(com.upyun.shortvideo.R.id.roundProgressBar);
		mClickAndLongPressListener.setLongPressedAndClickInterface(mClickAndLongPressedInterface);
		mRoundProgressBar.setOnTouchListener(mClickAndLongPressListener);
		
		//顶部按钮
		mBackButton = (ImageButton) findViewById(com.upyun.shortvideo.R.id.lsq_back_btn);
		mToggleButton = (ImageButton) findViewById(com.upyun.shortvideo.R.id.lsq_toggle_btn);
		mFlashButton = (ImageButton) findViewById(com.upyun.shortvideo.R.id.lsq_flash_btn);
		mBackButton.setOnClickListener(mButtonListener);
		mToggleButton.setOnClickListener(mButtonListener);
		mFlashButton.setOnClickListener(mButtonListener);

		mBeautyTab = (TuSdkTextButton) findViewById(com.upyun.shortvideo.R.id.lsq_beauty_btn);
		mBeautyTab.setOnClickListener(mButtonListener);
		mBeautyLayout = (LinearLayout) findViewById(com.upyun.shortvideo.R.id.lsq_beauty_content);
		
		mFilterTab = (TuSdkTextButton) findViewById(com.upyun.shortvideo.R.id.lsq_filter_btn);
		mFilterTab.setOnClickListener(mButtonListener);
		mFilterLayout = (RelativeLayout) findViewById(com.upyun.shortvideo.R.id.lsq_filter_content);

		mSmoothingBarLayout = (ConfigViewSeekBar) mBeautyLayout.findViewById(com.upyun.shortvideo.R.id.lsq_dermabrasion_bar);
		mSmoothingBarLayout.getTitleView().setText(com.upyun.shortvideo.R.string.lsq_dermabrasion);
		mSmoothingBarLayout.getSeekbar().setDelegate(mTuSeekBarDelegate);
		
		mEyeSizeBarLayout = (ConfigViewSeekBar) mBeautyLayout.findViewById(com.upyun.shortvideo.R.id.lsq_big_eyes_bar);
		mEyeSizeBarLayout.getTitleView().setText(com.upyun.shortvideo.R.string.lsq_big_eyes);
		mEyeSizeBarLayout.getSeekbar().setDelegate(mTuSeekBarDelegate);
		
		mChinSizeBarLayout = (ConfigViewSeekBar) mBeautyLayout.findViewById(com.upyun.shortvideo.R.id.lsq_thin_face_bar);
		mChinSizeBarLayout.getTitleView().setText(com.upyun.shortvideo.R.string.lsq_thin_face);
		mChinSizeBarLayout.getSeekbar().setDelegate(mTuSeekBarDelegate);
		
		initFilterListView();
		initStickerListView();
		
		//贴纸视图
		mStickerBottomView = (RelativeLayout) findViewById(com.upyun.shortvideo.R.id.lsq_sticker_group_bottom_view);
		mStickerBottomView.setVisibility(View.INVISIBLE);
		//点击切换贴纸、滤镜视图
		mTouchView = (FrameLayout) findViewById(com.upyun.shortvideo.R.id.lsq_touch_view);
		mTouchView.setOnClickListener(mButtonListener);
		//滤镜视图
		mFilterBottomView = (RelativeLayout) findViewById(com.upyun.shortvideo.R.id.lsq_filter_group_bottom_view);
		mFilterBottomView.setVisibility(View.INVISIBLE);

		//底部按钮
		mBottomBtnLayout = (RelativeLayout) findViewById(com.upyun.shortvideo.R.id.lsq_btn_layout);
		mSmartBeautyButton = (CompoundDrawableTextView) findViewById(com.upyun.shortvideo.R.id.lsq_smart_beauty_btn);
		mStickerButton = (CompoundDrawableTextView) findViewById(com.upyun.shortvideo.R.id.lsq_sticker_btn);
		mStickerButton.setOnTouchListener(mOnTouchListener);
		mSmartBeautyButton.setOnTouchListener(mOnTouchListener);
		
		//预览图片控件
		mPreviewImg = (ImageView) findViewById(com.upyun.shortvideo.R.id.lsq_preview_imageview);
		mPreviewImg.setVisibility(View.GONE);
		
		mPreviewLayout = (RelativeLayout) findViewById(com.upyun.shortvideo.R.id.lsq_preview);
		mPreviewLayout.setVisibility(View.INVISIBLE);
		mPreviewVideoLayout = (RelativeLayout) findViewById(com.upyun.shortvideo.R.id.lsq_preview_video);
		mDeltButton = (CompoundDrawableTextView) findViewById(com.upyun.shortvideo.R.id.lsq_delet_btn);
		mSaveButton = (CompoundDrawableTextView) findViewById(com.upyun.shortvideo.R.id.lsq_save_btn);
		mDeltButton.setOnTouchListener(mOnTouchListener);
		mSaveButton.setOnTouchListener(mOnTouchListener);
		
		// 设置弹窗提示是否在隐藏虚拟键的情况下使用
		TuSdk.messageHub().applyToViewWithNavigationBarHidden(true);
	}
	
	/**
	 * 初始化滤镜栏视图
	 */
	protected void initFilterListView() 
	{
		getFilterListView();

		this.mFilterListView.setModeList(Arrays.asList(Constants.VIDEOFILTERS));
	    ThreadHelper.postDelayed(new Runnable(){

			@Override
			public void run()
			{
				if (!mIsFirstEntry) return;
				
				mIsFirstEntry = false;
				changeVideoFilterCode(Arrays.asList(Constants.VIDEOFILTERS).get(mFocusPostion));
			}
			
	    }, 1000);
	}
	
	/**
	 * 滤镜栏视图
	 * 
	 * @return
	 */
	private FilterListView getFilterListView() 
	{
		if (mFilterListView == null) {
			mFilterListView = (FilterListView) findViewById(com.upyun.shortvideo.R.id.lsq_filter_list_view);
			mFilterListView.loadView();
			mFilterListView.setCellLayoutId(com.upyun.shortvideo.R.layout.filter_list_cell_view);
			mFilterListView.setCellWidth(TuSdkContext.dip2px(80));
			mFilterListView.setItemClickDelegate(mFilterTableItemClickDelegate);
			mFilterListView.reloadData();
			mFilterListView.selectPosition(mFocusPostion);
		}
	
		return mFilterListView;
	}
	
	/** 滤镜组列表点击事件 */
	private TuSdkTableViewItemClickDelegate<String, FilterCellView> mFilterTableItemClickDelegate = new TuSdkTableViewItemClickDelegate<String, FilterCellView>() {
		@Override
		public void onTableViewItemClick(String itemData,
				FilterCellView itemView, int position) {
			onFilterGroupSelected(itemData, itemView, position);
		}
	};
	
	/**
	 * 滤镜组选择事件
	 * 
	 * @param itemData
	 * @param itemView
	 * @param position
	 */
	protected void onFilterGroupSelected(String itemData,
			FilterCellView itemView, int position) 
	{
		FilterCellView prevCellView = (FilterCellView) mFilterListView.findViewWithTag(mFocusPostion);
		mFocusPostion = position;
		changeVideoFilterCode(itemData);
		mFilterListView.selectPosition(mFocusPostion);
		deSelectLastFilter(prevCellView);
		selectFilter(itemView, position);
		getFilterConfigView().setVisibility((position == 0)?View.INVISIBLE:View.VISIBLE);
    }
	
	/**
	 * 切换滤镜
	 * 
	 * @param code
	 */
	protected void changeVideoFilterCode(final String code) 
	{
		if (mTuSDKVideoCamera != null && mTuSDKVideoCamera.getState() != CameraState.StateUnknow
				&& mTuSDKVideoCamera.getState() != CameraState.StateStarting)
		{
			ThreadHelper.runThread(new Runnable() {

				@Override
				public void run() {
					mTuSDKVideoCamera.switchFilter(code);
				}
			});
		}
		else
		{
			ThreadHelper.postDelayed(new Runnable() {

				@Override
				public void run() {

					ThreadHelper.runThread(new Runnable() {

						@Override
						public void run() {
							if (mTuSDKVideoCamera != null
									&& mTuSDKVideoCamera.getState() != CameraState.StateUnknow
									&& mTuSDKVideoCamera.getState() != CameraState.StateStarting)
								mTuSDKVideoCamera.switchFilter(code);
						}
					});
				}

			}, 800);
		}
	}
	
	/**
	 * 取消上一个滤镜的选中状态
	 * 
	 * @param lastFilter
	 */
	private void deSelectLastFilter(FilterCellView lastFilter)
	{
		if (lastFilter == null) return;

		updateFilterBorderView(lastFilter,true);
		lastFilter.getTitleView().setBackground(TuSdkContext.getDrawable("tusdk_view_filter_unselected_text_roundcorner"));
		lastFilter.getImageView().invalidate();
	}
	
	/**
	 * 初始化贴纸组视图
	 */
	protected void initStickerListView() 
	{
		getStickerListView();

		refetchStickerList();
	}
	
	/**
	 * 刷新本地贴纸列表
	 */
	public void refetchStickerList()
	{
		if (mStickerListView == null) return;

        List<StickerGroup> groups = new ArrayList<StickerGroup>();
        groups.addAll(getRawStickGroupList());
        groups.add(0, new StickerGroup());

		mStickerListView.setModeList(groups);
		mStickerListView.reloadData();
	}
	
	public List<StickerGroup> getLocalStickerList()
	{
		List<StickerGroup> localList = StickerLocalPackage.shared().getSmartStickerGroups();
		List<StickerGroup> list = new ArrayList<StickerGroup>();
		// 添加本地贴纸到容器
		for(StickerGroup group : localList)
		{
			list.add(group);
		}
		
		return list;
	}

	/**
	 * 贴纸组视图
	 */
	private StickerListView getStickerListView() 
	{
		if (mStickerListView == null) {
			mStickerListView = (StickerListView) findViewById(com.upyun.shortvideo.R.id.lsq_sticker_list_view);
			mStickerListView.loadView();
			mStickerListView.setCellLayoutId(com.upyun.shortvideo.R.layout.sticker_list_cell_view);
			GridLayoutManager grid = new GridLayoutManager(mActivity, 5);
			mStickerListView.setLayoutManager(grid);
			mStickerListView.setCellWidth(TuSdkContext.dip2px(80));
			mStickerListView
					.setItemClickDelegate(mStickerTableItemClickDelegate);
		}
		return mStickerListView;
	}
	
	/**
	 * 获取本地贴纸列表
	 * @return
	 */
	public List<StickerGroup> getRawStickGroupList()
	{
		List<StickerGroup> list = new ArrayList<StickerGroup>();
	    try {  
	        InputStream stream = getResources().openRawResource(com.upyun.shortvideo.R.raw.full_screen_sticker);

	        if (stream == null) return null;
	        
			byte buffer[] = new byte[stream.available()];
			stream.read(buffer);
			String json = new String(buffer, "UTF-8");
			
			JSONObject jsonObject = JsonHelper.json(json);
			JSONArray jsonArray = jsonObject.getJSONArray("stickerGroups");
			
			for(int i = 0; i < jsonArray.length();i++)
			{
				JSONObject item = jsonArray.getJSONObject(i);
		        StickerGroup group = new StickerGroup();
		        group.groupId = item.optLong("id");
		        group.previewName = item.optString("previewImage");
		        group.name = item.optString("name");
		        list.add(group);
			}
			return list;
	    } catch (Exception e) {  
	        e.printStackTrace();  
	    }  
		return null;
	}

	/**
	 * 贴纸组列表点击事件
	 */
	private TuSdkTableViewItemClickDelegate<StickerGroup, StickerCellView>
	             mStickerTableItemClickDelegate = new TuSdkTableViewItemClickDelegate<StickerGroup, StickerCellView>() {
		@Override
		public void onTableViewItemClick(StickerGroup itemData,
				StickerCellView itemView, int position) {
			onStickerGroupSelected(itemData, itemView, position);
		}
	};
	
	/**
	 * 贴纸组选择事件
	 * 
	 * @param itemData
	 * @param itemView
	 * @param position
	 */
	protected void onStickerGroupSelected(StickerGroup itemData,
			StickerCellView itemView, int position) 
	{
		// 设置点击贴纸时呈现或是隐藏贴纸
		if (position == 0)
		{
			mTuSDKVideoCamera.removeAllLiveSticker();
			mStickerListView.setSelectedPosition(position);
			return;
		}

		// 如果贴纸已被下载到本地
		if (mStickerListView.isDownloaded(itemData))
		{
			mStickerListView.setSelectedPosition(position);
			itemData = StickerLocalPackage.shared().getStickerGroup(itemData.groupId);
			mTuSDKVideoCamera.showGroupSticker(itemData);
		}else
		{
			mStickerListView.downloadStickerGroup(itemData);
		}
	}

	/**
	 * 传递录制相机对象
	 * 
	 */
	public void setUpCamera(Activity activity,TuSDKRecordVideoCamera tuSDKRecordVideoCamera)
	{
		this.mActivity = activity;
		this.mTuSDKVideoCamera = tuSDKRecordVideoCamera;
	    
	    ThreadHelper.postDelayed(new Runnable(){

			@Override
			public void run()
			{
				if (!mIsFirstEntry) return;

				mIsFirstEntry = false;
				changeVideoFilterCode(Arrays.asList(Constants.VIDEOFILTERS).get(mFocusPostion));
			}
			
	    }, 1000);
	}
	
	private OnClickListener mButtonListener=new OnClickListener()
	{
		@Override
		public void onClick(View v)
		{
			if (v == mBackButton)
			{
				mActivity.finish();
			}
			else if (v == mFlashButton)
			{
				mFlashEnabled = !mFlashEnabled;
	
				mTuSDKVideoCamera.setFlashMode(mFlashEnabled ? CameraFlash.Torch
						: CameraFlash.Off);
	
				// 闪光灯
				updateButtonStatus(mFlashButton, mFlashEnabled);
			}
			else if (v == mToggleButton)
			{
				mTuSDKVideoCamera.rotateCamera();
			}
			else if (v == mTouchView)
			{
				hideStickerStaff();
				hideFilterStaff();
				handleBottomBtnLayout(false);
			}
			else if (v == mBeautyTab)
			{
				showBeautySeekBar();
			}
			else if (v == mFilterTab)
			{
				showFilterLayout();
			}
		  }
	 };
		
	/**
	 * 更新按钮显示状态
	 * 
	 * @param button
	 * @param clickable
	 */
	private void updateButtonStatus(ImageButton button, boolean clickable)
	{
		int imgId = 0;

		switch (button.getId()) {

		case com.upyun.shortvideo.R.id.lsq_flash_btn:
			imgId = clickable ? com.upyun.shortvideo.R.drawable.lsq_flash_open
					: com.upyun.shortvideo.R.drawable.lsq_flash_closed;
			break;

		default:
			break;
		}
		button.setImageResource(imgId);
	}
	
	/**
	 * 点击贴纸栏上方的空白区域隐藏贴纸栏
	 */
	public void hideStickerStaff() 
	{
		if (mStickerBottomView.getVisibility() == View.INVISIBLE)
			return;

		updateStickerViewStaff(false);

		// 滤镜栏向下动画并隐藏
		ViewCompat.animate(mStickerBottomView)
				.translationY(mStickerBottomView.getHeight()).setDuration(200);
	}
	
	/**
	 * 点击滤镜栏上方的空白区域隐藏滤镜栏
	 */
	public void hideFilterStaff() 
	{
		if (mFilterBottomView.getVisibility() == View.INVISIBLE) return;

		updateFilterViewStaff(false);

		// 滤镜栏向下动画并隐藏
		ViewCompat.animate(mFilterBottomView)
				.translationY(mFilterBottomView.getHeight()).setDuration(200);
	}
	
	/**
	 * 处理底部按钮的显示状态
	 * 
	 * @param isHidden
	 */
	private void handleBottomBtnLayout(boolean isHidden) 
	{
		mBottomBtnLayout.setVisibility(isHidden ? View.INVISIBLE : View.VISIBLE);
	}
	
	private ClickAndLongPressedInterface mClickAndLongPressedInterface = new ClickAndLongPressedInterface()
	{
		@Override
		public void onLongPressedDown() 
		{
			if (mTuSDKVideoCamera == null) return;
			
			mTuSDKVideoCamera.startRecording();
			setBackButtonHided(true);
		}

		@Override
		public void onLongPressedUp() 
		{
			mResType = RES_TYPE_VIDEO;
			if (mTuSDKVideoCamera != null)
			{
				mTuSDKVideoCamera.stopRecording();
				setBackButtonHided(false);
			}
		}
		
		@Override
		public void onClick() 
		{
			updateCaptureState();
			hideFilterStickerBtn(true);
			if (mTuSDKVideoCamera != null)
			{
				mTuSDKVideoCamera.captureImage();
			}
		}
	};
	
	@SuppressLint("ClickableViewAccessibility") 
	private OnTouchListener mOnTouchListener = new OnTouchListener()
	{
		@Override
		public boolean onTouch(View v, MotionEvent event) {
		
			// 只响应ACTION_DOWN事件
			if (event.getAction() != MotionEvent.ACTION_DOWN) return false;

			if (v == mStickerButton)
			{
				handleStickerButton();
			}
			else if (v == mSmartBeautyButton)
			{
				handleFilterButton();
			}
			else if (v == mDeltButton) 
			{
				deleteResource();
			}
			else if (v == mSaveButton)
			{
				saveResource();
			}
			return true;
		}
	};

	/** 滤镜拖动条监听事件 */
	private FilterConfigView.FilterConfigViewSeekBarDelegate mConfigSeekBarDelegate = new FilterConfigView.FilterConfigViewSeekBarDelegate()
	{

		@Override
		public void onSeekbarDataChanged(FilterConfigSeekbar seekbar, FilterArg arg)
		{
			if (arg == null) return;

			if (arg.equalsKey("smoothing"))
				mSmoothingProgress = arg.getPrecentValue();
			else if (arg.equalsKey("eyeSize"))
				mEyeSizeProgress = arg.getPrecentValue();
			else if (arg.equalsKey("chinSize"))
				mChinSizeProgress = arg.getPrecentValue();
			else if (arg.equalsKey("mixied"))
				mMixiedProgress = arg.getPrecentValue();
		}

	};

	/** 美颜拖动条监听事件 */
	private TuSeekBar.TuSeekBarDelegate mTuSeekBarDelegate = new TuSeekBar.TuSeekBarDelegate()
	{

		@Override
		public void onTuSeekBarChanged(TuSeekBar seekBar, float progress)
		{
			if (seekBar == mSmoothingBarLayout.getSeekbar())
			{
				mSmoothingProgress = progress;
				applyFilter(mSmoothingBarLayout,"smoothing",progress);
			}
			else if (seekBar == mEyeSizeBarLayout.getSeekbar())
			{
				mEyeSizeProgress = progress;
				applyFilter(mEyeSizeBarLayout,"eyeSize",progress);
			}
			else if (seekBar == mChinSizeBarLayout.getSeekbar())
			{
				mChinSizeProgress = progress;
				applyFilter(mChinSizeBarLayout,"chinSize",progress);
			}
		}
	};

	private void applyFilter(ConfigViewSeekBar viewSeekBar,String key,float progress)
	{
		if (viewSeekBar == null || mSelesOutInput == null) return;

		viewSeekBar.getConfigValueView().setText((int)(progress*100) + "%");
		SelesParameters params = mSelesOutInput.getFilterParameter();
		params.setFilterArg(key, progress);
		mSelesOutInput.submitFilterParameter();
	}
    
	/**
	 * 更新贴纸栏相关视图的显示状态
	 * 
	 * @param isShow
	 */
	private void updateStickerViewStaff(boolean isShow)
	{
		mRoundProgressBar.setVisibility(isShow? View.INVISIBLE: View.VISIBLE);
		mSmartBeautyButton.setVisibility(isShow? View.INVISIBLE: View.VISIBLE);
		mStickerButton.setVisibility(isShow? View.INVISIBLE: View.VISIBLE);
		
		mStickerBottomView
				.setVisibility(isShow ? View.VISIBLE : View.INVISIBLE);
		mTouchView.setVisibility(isShow ? View.VISIBLE : View.INVISIBLE);
	}
	
	/**
	 * 更新滤镜栏相关视图的显示状态
	 * 
	 * @param isShow
	 */
	private void updateFilterViewStaff(boolean isShow)
	{
		
		mRoundProgressBar.setVisibility(isShow? View.INVISIBLE: View.VISIBLE);
		mSmartBeautyButton.setVisibility(isShow? View.INVISIBLE: View.VISIBLE);
		mStickerButton.setVisibility(isShow? View.INVISIBLE: View.VISIBLE);
		
		mFilterBottomView.setVisibility(isShow ? View.VISIBLE : View.INVISIBLE);
		mTouchView.setVisibility(isShow ? View.VISIBLE : View.INVISIBLE);
	}
	
	/**
	 * 滤镜配置视图
	 * 
	 * @return
	 */
	private FilterConfigView getFilterConfigView() 
	{
	    if (mConfigView == null)
        {
            mConfigView = (FilterConfigView) findViewById(com.upyun.shortvideo.R.id.lsq_filter_config_view);
        }

        return mConfigView;
	} 
	
	/**
	 * 设置返回按钮是否隐藏
	 * @param isHidden
	 */
    private void setBackButtonHided(boolean isHidden)
    {
		if (mBackButton == null){
			TLog.e("mBackButton == null");
			return;
		}
    	mBackButton.setVisibility(isHidden?View.INVISIBLE:View.VISIBLE);
    }
    
	/**
	 * 更新拍照进度条状态
	 */
	private void updateCaptureState()
	{
		int innerRoundColor = TuSdkContext.getColor("lsq_capture_selected");
	    mRoundProgressBar.setInnerRoundColor(innerRoundColor);
	    mRoundProgressBar.invalidate();
	}
	
	/** 显示贴纸视图 */
	protected void handleStickerButton() 
	{
		showStickerLayout();
		handleBottomBtnLayout(true);
	}
	 
	private void hideFilterStickerBtn(boolean isHidden)
	{
		mStickerButton.setVisibility(isHidden ? View.INVISIBLE : View.VISIBLE);
		mSmartBeautyButton.setVisibility(isHidden ? View.INVISIBLE : View.VISIBLE);
	}
	
	/** 显示滤镜视图 */
	protected void handleFilterButton()
	{
		showSmartBeautyLayout();
		handleBottomBtnLayout(true);
	}
	
    /**
     * 销毁图片
     */
    private void destroyBitmap()
    {
    	if (mCaptureBitmap == null) return;
    	
    	if (!mCaptureBitmap.isRecycled())
    		mCaptureBitmap.recycle();
    	
    	mCaptureBitmap = null;
    }
    
    /**
     * 删除资源
     */
    public void deleteResource()
    {
    	
		if (mPreviewLayout == null|| mPreviewVideoLayout == null)
		{
			TLog.e("mPreviewLayout == null || mPreviewVideoLayout == null");
			return;
		}		 
		
		if(mResType == RES_TYPE_IMAGE)
		{
			updateImagePreviewStatus(false);
			destroyBitmap();
            TuSdk.messageHub().showToast(mActivity, com.upyun.shortvideo.R.string.lsq_image_del_ok);
		}
		
		if(mResType == RES_TYPE_VIDEO && mMoviePlayer != null && mSurfaceView != null)
		{
			// 返回录制界面需先调用removeAllViews清除预览画面
			// 不然无法显示相机预览界面
			mPreviewVideoLayout.removeAllViews();
			
			updateVideoPreviewStatus(false);
			mMoviePlayer.stop();
			File file = new File(mVideoPath);
			FileHelper.delete(file);
			refreshFile(file);
            TuSdk.messageHub().showToast(mActivity, com.upyun.shortvideo.R.string.lsq_video_del_ok);

		}
		hideNavigationBar();
		resetProgess();
		showStickerAndFilter();
		getDelegate().resumeCameraCapture();
		updateProgressBarState(false);
	}

	/**
     * 隐藏虚拟按键，并且全屏
     */
    @TargetApi(Build.VERSION_CODES.KITKAT)
	protected void hideNavigationBar() 
    {
        if (Build.VERSION.SDK_INT > 11 && Build.VERSION.SDK_INT < 19) 
        { 
            View decorview = mActivity.getWindow().getDecorView();
            decorview.setSystemUiVisibility(View.GONE);
        } 
        else if (Build.VERSION.SDK_INT >= 19)
        {
            View decorView = mActivity.getWindow().getDecorView();
            int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY | View.SYSTEM_UI_FLAG_FULLSCREEN;
            decorView.setSystemUiVisibility(uiOptions);
        }
    }
    
    /**
     * 保存资源
     */
	public void saveResource()
	{
		if (mPreviewLayout == null|| mPreviewVideoLayout == null)
		{
			TLog.e("mPreviewLayout == null || mPreviewVideoLayout == null");
			return;
		}

		if(mResType == RES_TYPE_IMAGE)
		{
            File flie = AlbumHelper.getAlbumFile();
            ImageSqlHelper.saveJpgToAblum(mActivity, mCaptureBitmap, 0, flie);
            refreshFile(flie);
			updateImagePreviewStatus(false);
            destroyBitmap();
            TuSdk.messageHub().showToast(mActivity, com.upyun.shortvideo.R.string.lsq_image_save_ok);
		}
	
		if(mResType == RES_TYPE_VIDEO && mMoviePlayer != null && mSurfaceView != null)
		{
			// 返回录制界面需先调用removeAllViews清除预览画面
			// 不然无法显示相机预览界面
			mPreviewVideoLayout.removeAllViews();
			
			updateVideoPreviewStatus(false);
			mMoviePlayer.stop();
			ImageSqlHelper.saveMp4ToAlbum(mActivity,new File(mVideoPath));
			refreshFile(new File(mVideoPath));
			getDelegate().onMovieSaveSucceed(mVideoPath);
            TuSdk.messageHub().showToast(mActivity, com.upyun.shortvideo.R.string.lsq_video_save_ok);
		}
		hideNavigationBar();
		resetProgess();
		showStickerAndFilter();
		getDelegate().resumeCameraCapture();
		updateProgressBarState(false);
	 }
	
	private void updateSmartBeautyTab(TuSdkTextButton button, boolean clickable)
	{
		int imgId = 0, colorId = 0;
		
		switch (button.getId())
		{
		case com.upyun.shortvideo.R.id.lsq_filter_btn:
			imgId = clickable? com.upyun.shortvideo.R.drawable.lsq_style_default_btn_filter_selected
					: com.upyun.shortvideo.R.drawable.lsq_style_default_btn_filter_unselected;
			colorId = clickable? com.upyun.shortvideo.R.color.lsq_filter_title_color : com.upyun.shortvideo.R.color.lsq_filter_title_default_color;
			break;
		case com.upyun.shortvideo.R.id.lsq_beauty_btn:
			imgId = clickable? com.upyun.shortvideo.R.drawable.lsq_style_default_btn_beauty_selected
					: com.upyun.shortvideo.R.drawable.lsq_style_default_btn_beauty_unselected;
			colorId = clickable? com.upyun.shortvideo.R.color.lsq_filter_title_color : com.upyun.shortvideo.R.color.lsq_filter_title_default_color;
			break;
		}
		
		button.setCompoundDrawables(null, TuSdkContext.getDrawable(imgId), null, null);
		button.setTextColor(TuSdkContext.getColor(colorId));
	}
	
	private void setEnableAllSeekBar(boolean enable)
	{
		setEnableSeekBar(mSmoothingBarLayout,enable,0, com.upyun.shortvideo.R.drawable.tusdk_view_widget_seekbar_none_drag);
		setEnableSeekBar(mEyeSizeBarLayout,enable,0, com.upyun.shortvideo.R.drawable.tusdk_view_widget_seekbar_none_drag);
		setEnableSeekBar(mChinSizeBarLayout,enable,0, com.upyun.shortvideo.R.drawable.tusdk_view_widget_seekbar_none_drag);
	}

	/** 设置调节栏是否有效 */
	private void setEnableSeekBar(ConfigViewSeekBar viewSeekBar,boolean enable,float progress,int id)
	{
		if (viewSeekBar == null) return; 
		
		viewSeekBar.setProgress(progress);
		viewSeekBar.getSeekbar().setEnabled(enable);
		viewSeekBar.getSeekbar().getDragView().setBackgroundResource(id);
	}

	/** 显示美颜调节栏 */
	private void showBeautySeekBar()
	{
		if (mIsFirstEntry)
		{
		    changeVideoFilterCode(Arrays.asList(Constants.VIDEOFILTERS).get(mFocusPostion));
		}
	    
		if (mBeautyLayout == null || mFilterLayout == null)
			return;
		
		mBeautyLayout.setVisibility(View.VISIBLE);
		mFilterLayout.setVisibility(View.GONE);
		updateSmartBeautyTab(mBeautyTab,true);
		updateSmartBeautyTab(mFilterTab,false);
		
		if (mSelesOutInput == null)
		{
			setEnableAllSeekBar(false);
			return;
		}
		
		SelesParameters params = mSelesOutInput.getFilterParameter();
		if (params == null)
		{
			setEnableAllSeekBar(false);
			return;
		}
		
		List<FilterArg> list = params.getArgs();
		if (list == null || list.size() == 0)
		{
			setEnableAllSeekBar(false);
			return;
		}
		
		for(FilterArg arg : list)
		{
			if (arg.equalsKey("smoothing"))
			{
				setEnableSeekBar(mSmoothingBarLayout,true,arg.getPrecentValue(),
						com.upyun.shortvideo.R.drawable.tusdk_view_widget_seekbar_drag);
			}
			else if (arg.equalsKey("eyeSize"))
			{
				setEnableSeekBar(mEyeSizeBarLayout,true,arg.getPrecentValue(),
						com.upyun.shortvideo.R.drawable.tusdk_view_widget_seekbar_drag);
			}
			else if (arg.equalsKey("chinSize"))
			{
				setEnableSeekBar(mChinSizeBarLayout,true,arg.getPrecentValue(),
						com.upyun.shortvideo.R.drawable.tusdk_view_widget_seekbar_drag);
			}
		}
	}

	/** 显示滤镜列表 */
	private void showFilterLayout()
	{
		if (mBeautyLayout == null || mFilterLayout == null)
			return;
		
		mFilterLayout.setVisibility(View.VISIBLE);
		mBeautyLayout.setVisibility(View.GONE);
		updateSmartBeautyTab(mBeautyTab,false);
		updateSmartBeautyTab(mFilterTab,true);
		
		if (mFocusPostion>0 && getFilterConfigView() != null && mSelesOutInput != null)
		{
			getFilterConfigView().post(new Runnable()
			{

				@Override
				public void run() {
					getFilterConfigView().setSelesFilter(mSelesOutInput.getFilter());
					getFilterConfigView().setVisibility(View.VISIBLE);
				}});
			
			getFilterConfigView().setSeekBarDelegate(mConfigSeekBarDelegate);
			getFilterConfigView().invalidate();
		}
	}
	 
		/**
		 * 显示贴纸底部栏
		 */
		public void showStickerLayout() 
		{
			updateStickerViewStaff(true);
	
			// 滤镜栏向上动画并显示
			ViewCompat.setTranslationY(mStickerBottomView,
					mStickerBottomView.getHeight());
			ViewCompat.animate(mStickerBottomView).translationY(0).setDuration(200);
		}
		
		/**
		 * 显示智能美颜底部栏
		 */
		public void showSmartBeautyLayout()
		{
			updateFilterViewStaff(true);
			
			// 滤镜栏向上动画并显示
			ViewCompat.setTranslationY(mFilterBottomView,
					mFilterBottomView.getHeight());
            ViewCompat.animate(mFilterBottomView).translationY(0).setDuration(200);

			showBeautySeekBar();
			
		}
		
		/**
		 * 更新图片预览界面的状态
		 * @param isShowed
		 */
		private void updateImagePreviewStatus(boolean isShowed)
		{
			mPreviewLayout.setVisibility(isShowed ? View.VISIBLE : View.GONE);
			mPreviewImg.setVisibility(isShowed ?View.VISIBLE :View.GONE);
		}
		/**
		 * 更新视频预览界面的状态
		 * @param isShowed
		 */
		private void updateVideoPreviewStatus(boolean isShowed)
		{
			mPreviewLayout.setVisibility(isShowed ? View.VISIBLE :View.GONE);
			mPreviewVideoLayout.setVisibility(isShowed ? View.VISIBLE :View.GONE);
			mSurfaceView.setZOrderMediaOverlay(isShowed ? true :false);
			mPreviewImg.setVisibility(View.GONE);
		}
		
		 public void refreshFile(File file) 
		 {
			if (file == null) {
				TLog.e("refreshFile file == null");
				return;
			}
			
			Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
			Uri uri = Uri.fromFile(file);
			intent.setData(uri);
			mActivity.sendBroadcast(intent);
		 }
		 
	/**
	 * 录制结束重置进度条状态
	 */
	private void resetProgess()
	{
		if (mRoundProgressBar == null)
		{
			TLog.e("mRoundProgressBar == null");
			return;
		}
		
	    int innerRoundColor= TuSdkContext.getColor("lsq_innerRound_unselected_color");
        mRoundProgressBar.setInnerRoundColor(innerRoundColor);
        mRoundProgressBar.setRingWidth(TuSdkContext.dip2px(6));
        mRoundProgressBar.setInnerRoundRadius(TuSdkContext.dip2px(30));
        mRoundProgressBar.setRingProgresswidth(TuSdkContext.dip2px(85));
        mRoundProgressBar.setProgress(0);
        mRoundProgressBar.invalidate();
	}
	
	/**
	 * 显示贴纸和滤镜
	 */
	private void showStickerAndFilter() 
	{
		if (mStickerButton == null || mSmartBeautyButton == null)
		{
			TLog.e("mStickerButton == null || mFilterButton == null");
			return;
		}
		mStickerButton.setVisibility(View.VISIBLE);
		mSmartBeautyButton.setVisibility(View.VISIBLE);
	}
	
	/**
	 * 滤镜选中状态
	 * 
	 * @param itemView
	 * @param position
	 */
	private void selectFilter(FilterCellView itemView, int position) 
	{
		updateFilterBorderView(itemView,false);
		itemView.setFlag(position);

		TextView titleView = itemView.getTitleView();
		titleView.setBackground(TuSdkContext.getDrawable("tusdk_view_filter_selected_text_roundcorner"));
	}
	
	/**
	 * 设置滤镜单元边框是否可见
	 * @param lastFilter
	 * @param isHidden
	 */
	private void updateFilterBorderView(FilterCellView lastFilter,boolean isHidden)
	{
		View filterBorderView = lastFilter.getBorderView();
		filterBorderView.setVisibility(isHidden ? View.GONE : View.VISIBLE);
	}
	
	/**
	 * 录制错误时更新视图显示
	 * 
	 * @param error
	 */
	public void updateViewOnMovieRecordFailed(RecordError error)
	{
        TLog.e("RecordError : %s",error);
        if (error == RecordError.InvalidRecordingTime)
            TuSdk.messageHub().showToast(mActivity, com.upyun.shortvideo.R.string.lsq_record_time_invalid);
        else
            TuSdk.messageHub().showToast(mActivity, com.upyun.shortvideo.R.string.lsq_record_movie_error);
        
		resetProgess();
		showStickerAndFilter();
		getDelegate().resumeCameraCapture();
	}
	
	/**
	 * 录制状态发生改变时更新视图显示
	 * 
	 * @param state
 	 */
	public void updateViewOnMovieRecordStateChanged(RecordState state)
	{
        if (state == RecordState.Recording)
        {
            hideFilterStickerBtn(true);
        }
	}
	
	/**
	 * 录制进度改变时更新视图显示
	 * 
	 * @param progress
	 * @param durationTime
	 */
	public void updateViewOnMovieRecordProgressChanged(float progress, float durationTime) 
	{
		updateProgressBar((int)(progress*100));
	}
	
	/**
	 * 录制完成时更新视图显示
	 * 
	 */
	public void updateViewOnMovieRecordComplete(TuSDKVideoResult result)
	{
		previewCarmeraVideo(result.videoPath.getPath());
		// 暂停相机
		getDelegate().pauseCameraCapture();
	}
	
	/**
	 * 滤镜变化时改变滤镜调节栏
	 * @param selesOutInput
	 */
	public void updateViewOnFilterChanged(FilterWrap selesOutInput)
	{
    	if (selesOutInput == null) return;

    	// 设置滤镜参数
    	SelesParameters params = selesOutInput.getFilterParameter();
    	List<FilterArg> list = params.getArgs();
    	for (FilterArg arg : list)
    	{
    		if (arg.equalsKey("smoothing") && mSmoothingProgress != -1.0f)
    			arg.setPrecentValue(mSmoothingProgress);
    		else if (arg.equalsKey("smoothing") && mSmoothingProgress == -1.0f)
    			mSmoothingProgress = arg.getPrecentValue();
    		else if (arg.equalsKey("mixied") && mMixiedProgress !=  -1.0f)
    			arg.setPrecentValue(mMixiedProgress);
    		else if (arg.equalsKey("mixied") && mMixiedProgress == -1.0f)
    			mMixiedProgress = arg.getPrecentValue();
			else if (arg.equalsKey("eyeSize")&& mEyeSizeProgress != -1.0f)
				arg.setPrecentValue(mEyeSizeProgress);
			else if (arg.equalsKey("chinSize")&& mChinSizeProgress != -1.0f)
				arg.setPrecentValue(mChinSizeProgress);
			else if (arg.equalsKey("eyeSize") && mEyeSizeProgress == -1.0f)
				mEyeSizeProgress = arg.getPrecentValue();
			else if (arg.equalsKey("chinSize") && mChinSizeProgress == -1.0f)
				mChinSizeProgress = arg.getPrecentValue();
    	}
    	selesOutInput.setFilterParameter(params);
    	
        mSelesOutInput = selesOutInput;
        
        if (getFilterConfigView() != null)
            getFilterConfigView().setSelesFilter(mSelesOutInput.getFilter());

        if (mIsFirstEntry || (mBeautyLayout!=null && mBeautyLayout.getVisibility() == View.VISIBLE))
        {
        	mIsFirstEntry = false;
        	showBeautySeekBar();
        }
	}
	
	/**
	 * 拍照
	 * @param camera
	 * @param bitmap
	 */
	public void updateViewOnVideoCameraScreenShot(SelesVideoCameraInterface camera, Bitmap bitmap) 
	{
		if(bitmap != null) 
		{
			displayCapturedImage(bitmap);
			// 暂停相机
			camera.pauseCameraCapture();
			
			updateProgressBarState(true);
		}
	}
	
	public void displayCapturedImage(final Bitmap bitmap)
	{
		mResType = RES_TYPE_IMAGE;
		mCaptureBitmap = bitmap;
		
		if (mPreviewLayout == null || mPreviewImg == null) return;
		
		updateImagePreviewStatus(true);
		mPreviewImg.setImageBitmap(bitmap)	;			
	}
	
	/**
	 * 更新进度条进度
	 * @param progress
	 */
	private void updateProgressBar(int progress)
	{
        int innerRoundColor = TuSdkContext.getColor("lsq_innerRound_selected_color");
        mRoundProgressBar.setInnerRoundColor(innerRoundColor);
        mRoundProgressBar.setRingWidth(TuSdkContext.dip2px(8));
        mRoundProgressBar.setInnerRoundRadius(TuSdkContext.dip2px(38));
        mRoundProgressBar.setRingProgresswidth(TuSdkContext.dip2px(102));
        mRoundProgressBar.setProgress(progress);
        mRoundProgressBar.invalidate();
	}
	
	/**
	 * 创建SurfaceView播放视频
	 */
	private void createSurfaceViewOnPreviewLayout()
	{
		mSurfaceView = new SurfaceView(mActivity);
		LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		mPreviewVideoLayout.setLayoutParams(params);	
		mPreviewVideoLayout.addView(mSurfaceView);
	}
	
	public void previewCarmeraVideo(final String videoPath)
	{
		if (mPreviewLayout == null || mPreviewVideoLayout == null || mPreviewImg == null){
			TLog.e("mPreviewLayout == null || mPreviewVideoLayout == null || mPreviewImg == null");
			return;
		}
	    this.mVideoPath = videoPath;
		mResType = RES_TYPE_VIDEO;
		createSurfaceViewOnPreviewLayout();
		updateVideoPreviewStatus(true);
		updateProgressBarState(true);
		playMovie(videoPath);
	}
	
	/**
	 * 更新RoundProgressBar的状态
	 * @param isHidden
	 */
	private void updateProgressBarState(boolean isHidden)
	{
		mRoundProgressBar.setVisibility(isHidden ? View.INVISIBLE : View.VISIBLE);
	}
	
	private void playMovie(String videoPath) 
	{
		if (mMoviePlayer == null) mMoviePlayer = TuSDKMoviePlayer.createMoviePlayer();
		
		mMoviePlayer.setLooping(true);
		mMoviePlayer.initVideoPlayer(mActivity, Uri.fromFile(new File(mVideoPath)), mSurfaceView);
		mMoviePlayer.setDelegate(mMoviePlayerDelegate);
		mMoviePlayer.start();
	}
	
	public void pausePlayer()
	{
		if (mMoviePlayer != null) mMoviePlayer.pause();
	}
	
	public void resumePlayer()
	{
		if (mMoviePlayer != null) mMoviePlayer.resume();
	}
	
	public void stopPlayer()
	{
		if (mMoviePlayer != null) mMoviePlayer.stop();
	}
	
	public void videoSizeChanged(int width, int height)
	{
        final DisplayMetrics dm = new DisplayMetrics();
        mActivity.getWindowManager().getDefaultDisplay().getRealMetrics(dm);
        final int screenWidth= (int) dm.widthPixels;
        final int screenHeight= (int) dm.heightPixels;
        Rect boundingRect = new Rect();
        boundingRect.left = 0;
        boundingRect.right = screenWidth;
        boundingRect.top = 0;
        boundingRect.bottom = screenHeight;
        Rect rect = RectHelper.makeRectWithAspectRatioInsideRect(new TuSdkSize(width, height), boundingRect);
        int w = rect.right- rect.left;
        int h = rect.bottom - rect.top;
        RelativeLayout.LayoutParams params = new RelativeLayout
                .LayoutParams(w,h);
        params.addRule(RelativeLayout.CENTER_IN_PARENT);
        mSurfaceView.setLayoutParams(params);	
	}

	
	/** 播放器委托 */
	private TuSDKMoviePlayerDelegate mMoviePlayerDelegate = new TuSDKMoviePlayerDelegate()
	{

		@Override
		public void onStateChanged(PlayerState state)
		{
		}

		@Override
		public void onVideSizeChanged(MediaPlayer mp,int width, int height) 
		{
			videoSizeChanged(width,height);
		}

		@Override
		public void onSeekComplete()
		{
		}

		@Override
		public void onProgress(int progress)
		{
		}

		@Override
		public void onCompletion()
		{
		}
	};
}
