/**
 * TuSDKVideoDemo 
 * MovieRecordView.java
 * 
 * @author Bonan
 * @Date: 2017-5-8 上午10:42:48
 * @Copyright: (c) 2017 tusdk.com. All rights reserved.
 * 
 */
package com.upyun.shortvideo.views.record;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.lasque.tusdk.core.TuSdk;
import org.lasque.tusdk.core.TuSdkContext;
import org.lasque.tusdk.core.seles.sources.SelesOutInput;
import org.lasque.tusdk.core.seles.sources.SelesVideoCameraInterface;
import org.lasque.tusdk.core.seles.tusdk.FilterManager;
import org.lasque.tusdk.core.utils.ContextUtils;
import org.lasque.tusdk.core.utils.hardware.CameraConfigs.CameraFlash;
import org.lasque.tusdk.core.utils.hardware.TuSDKRecordVideoCamera;
import org.lasque.tusdk.core.utils.hardware.TuSDKRecordVideoCamera.RecordError;
import org.lasque.tusdk.core.utils.hardware.TuSDKRecordVideoCamera.RecordMode;
import org.lasque.tusdk.core.utils.hardware.TuSDKRecordVideoCamera.RecordState;
import org.lasque.tusdk.core.utils.hardware.TuSDKVideoCamera.TuSDKVideoCameraDelegate;
import org.lasque.tusdk.core.utils.hardware.TuSdkStillCameraAdapter.CameraState;
import org.lasque.tusdk.core.view.recyclerview.TuSdkTableView;
import org.lasque.tusdk.core.view.recyclerview.TuSdkTableView.TuSdkTableViewItemClickDelegate;
import org.lasque.tusdk.core.view.widget.button.TuSdkTextButton;
import org.lasque.tusdk.impl.view.widget.TuSeekBar;
import org.lasque.tusdk.modules.view.widget.sticker.StickerGroup;
import org.lasque.tusdk.modules.view.widget.sticker.StickerLocalPackage;
import com.upyun.shortvideo.R;
import com.upyun.shortvideo.utils.Constants;
import com.upyun.shortvideo.views.CompoundDrawableTextView;
import com.upyun.shortvideo.views.FilterCellView;
import com.upyun.shortvideo.views.FilterConfigView;
import com.upyun.shortvideo.views.FilterListView;
import com.upyun.shortvideo.views.StickerCellView;
import com.upyun.shortvideo.views.StickerListView;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPropertyAnimatorListener;
import android.support.v7.widget.GridLayoutManager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * 断点续拍 + 正常模式录制相机界面视图
 */
public class MovieRecordView extends RelativeLayout
{
	private static final int IMAGE_PICKER_SELECT = 1;

	/**
	 * 关闭按钮
	 */
	protected TuSdkTextButton mCloseView;

	/**
	 * 闪光灯按钮
	 */
	protected ImageView mFlashButton;

	/**
	 * 切换摄像头
	 */
	protected ImageView mToggleButton;

	/**
	 * 开始录制按钮
	 */
	private ImageButton mRecordButton;

	/**
	 * 动态贴纸按钮
	 */
	private TuSdkTextButton mStickerButton;

	/**
	 * 智能美化按钮
	 */
	private TuSdkTextButton mFilterButton;

	/**
	 * 相机底部按钮栏
	 */
	private RelativeLayout mBottomBar;

    // 闪光灯状态
    private boolean mFlashEnabled = false;

    // 确认完成按钮
	private TuSdkTextButton mConfirmButton;
	
	// 回退按钮
	private TuSdkTextButton mRollBackButton;
	
	private int lastProgress;
	
	// 录制的视频之间的断点
	private RelativeLayout interuptLayout;
	
	private ArrayList<Integer> progressList = new ArrayList<Integer>();

	/**
	 * 上方的触摸视图
	 */
	private FrameLayout mTouchView;

	/**
	 * 进度条
	 */
	private ProgressBar mProgressBar;

	/**
	 * 参数调节视图
	 */
	protected FilterConfigView mConfigView;

	/**
	 * 滤镜栏视图
	 */
	protected FilterListView mFilterListView;

	/**
	 * 贴纸栏视图
	 */
	protected StickerListView mStickerListView;

	/**
	 * 滤镜底部栏
	 */
	private RelativeLayout mFilterBottomView;

	/**
	 * 贴纸底部栏
	 */
	private RelativeLayout mStickerBottomView;

	// 上一次选中的贴纸项
	private RelativeLayout mLastStickerView;
	
	// 上一个选中的滤镜
	private FilterCellView lastSelectedCellView;

    // 在第一次运行的时候选中无效果滤镜
    private boolean isFirstTimeRun;

	// 美颜按钮
	private TuSdkTextButton mBeautyBtn;
	// 美颜布局
	private RelativeLayout mBeautyLayout;

	// 美颜磨皮强度调节栏布局
	private RelativeLayout mBeautyBarWrap;

	// 美颜磨皮强度调节栏
	private TuSeekBar mBeautyBar;

	// 磨皮强度值
	private TextView mBeautyLevel;

	private Context mContext;
	private TuSDKRecordVideoCamera mCamera;
	
	// 录制视频动作委托
	private TuSDKMovieRecordDelegate mDelegate;
	
	// 跳转视频编辑按钮
	private CompoundDrawableTextView mMovieImportButton;
	
	// 顶部栏
	private RelativeLayout mTopBarLayout;
	
	// 记录页面状态
	protected boolean mActived = false;
	
	/**
	 * 录制视频动作委托
	 */
	public interface TuSDKMovieRecordDelegate
	{
		/**
		 * 开始录制视频
		 */
		void startRecording();
		
		/**
		 * 是否正在录制
		 * 
		 * @return
		 */
		boolean isRecording();
		
		/**
		 * 暂停录制视频
		 */
		void pauseRecording();
		
		/**
		 * 停止录制视频
		 */
		void stopRecording();
		
		/**
		 * 关闭录制界面
		 */
		void finishRecordActivity();
	}

	public MovieRecordView(Context context, AttributeSet attrs)
	{
		this(context, attrs, 0);
	}

	public MovieRecordView(Context context, AttributeSet attrs, int defStyleAttr)
	{
		super(context, attrs, defStyleAttr);
		init(context);
	}
	
	public void setDelegate(TuSDKMovieRecordDelegate delegate)
	{
		mDelegate = delegate;
	}
	
	public TuSDKMovieRecordDelegate getDelegate()
	{
		return mDelegate;
	}
	
	private void init(Context context)
	{
		LayoutInflater.from(context).inflate(R.layout.movie_record_view, this,
				true);
		mMovieImportButton = (CompoundDrawableTextView) findViewById(R.id.lsq_movieEditorButton);
		mMovieImportButton.setOnClickListener(mButtonClickListener);
		mCloseView = (TuSdkTextButton) findViewById(R.id.lsq_closeButton);
		mCloseView.setOnClickListener(mButtonClickListener);

		mFlashButton = (ImageView) findViewById(R.id.lsq_flashButton);
		mFlashButton.setOnClickListener(mButtonClickListener);

		mToggleButton = (ImageView) findViewById(R.id.lsq_toggleButton);
		mToggleButton.setOnClickListener(mButtonClickListener);

		mRecordButton = (ImageButton) findViewById(R.id.lsq_recordButton);
		
		mFilterButton = (TuSdkTextButton) findViewById(R.id.lsq_filterWrap);
		mFilterButton.setOnClickListener(mButtonClickListener);
		
		mStickerButton = (TuSdkTextButton) findViewById(R.id.lsq_stickerWrap);
		mStickerButton.setOnClickListener(mButtonClickListener);
		
		//确认按钮
		mConfirmButton = (TuSdkTextButton) findViewById(R.id.lsq_confirmWrap);
		mConfirmButton.setOnClickListener(mButtonClickListener);
		mConfirmButton.setClickable(false);

		//回退按钮
		mRollBackButton = (TuSdkTextButton) findViewById(R.id.lsq_backWrap);
		mRollBackButton.setOnClickListener(mButtonClickListener);
		mRollBackButton.setClickable(false);
		
		mBottomBar = (RelativeLayout) findViewById(R.id.lsq_bottomBar);
		android.view.ViewGroup.LayoutParams params = mBottomBar.getLayoutParams();
		
		int remainHeight = TuSdkContext.getScreenSize().height - TuSdkContext.getScreenSize().width- ContextUtils.dip2px(context, 60);
		params.height = remainHeight;
		mBottomBar.setLayoutParams(params);
		
		mTouchView = (FrameLayout) findViewById(R.id.lsq_touch_view);
		mTouchView.setOnClickListener(mButtonClickListener);
		mTouchView.setVisibility(View.INVISIBLE);

		mProgressBar = (ProgressBar) findViewById(R.id.lsq_record_progressbar);
		mProgressBar.setProgress(0);
		
		Button minTimeButton = (Button) findViewById(R.id.lsq_minTimeBtn);
		RelativeLayout.LayoutParams minTimeLayoutParams = (LayoutParams) minTimeButton.getLayoutParams();
		minTimeLayoutParams.leftMargin =(int)(((float)Constants.MIN_RECORDING_TIME * TuSdkContext.getScreenSize().width) / Constants.MAX_RECORDING_TIME)
				 -TuSdkContext.dip2px(minTimeButton.getWidth());
		
		//一进入录制界面就显示最小时长标记
		interuptLayout = (RelativeLayout) findViewById(R.id.interuptLayout);

		// 开启美颜按钮
		mBeautyBtn = (TuSdkTextButton) findViewById(R.id.lsq_beautyBtn);
		mBeautyLayout = (RelativeLayout) findViewById(R.id.lsq_beautyWrap);
		mBeautyLayout.setOnClickListener(mButtonClickListener);
		
		mBeautyBar = (TuSeekBar) findViewById(R.id.lsq_seekBar);
		mBeautyBar.setDelegate(mTuSeekBarDelegate);
		
		getBeautyBarWrap().setVisibility(View.INVISIBLE);
		mBeautyLevel = (TextView) findViewById(R.id.lsq_level_View);
		
		mFilterBottomView = (RelativeLayout) findViewById(R.id.lsq_filter_group_bottom_view);
		mFilterBottomView.setVisibility(View.INVISIBLE);

		mStickerBottomView = (RelativeLayout) findViewById(R.id.lsq_sticker_group_bottom_view);
		mStickerBottomView.setVisibility(View.INVISIBLE);
		
		mTopBarLayout = (RelativeLayout) findViewById(R.id.lsq_topBar);

		android.view.ViewGroup.LayoutParams param = mStickerBottomView
				.getLayoutParams();
		param.height = remainHeight;
		mStickerBottomView.setLayoutParams(param);
		
		initFilterListView();
		initStickerListView();
		isFirstTimeRun = false;
		
		updateFlashButtonStatus();
		updateShowStatus(false);
			
		updateViewStatus(false);
		updateTopBarStatus(false);
		
		//为进度添加初始值
		progressList.add(0);
	}

	public void setActived(boolean mActived) 
	{
		this.mActived = mActived;
	}
	
	public RelativeLayout getFilterBottomView()
	{
		return mFilterBottomView;
	}
	
	public CompoundDrawableTextView getMovieImportButton()
	{
		return mMovieImportButton;
	}
	
	public RelativeLayout getStickerBottomView() 
	{
		return mStickerBottomView;
	}
	
	public RelativeLayout getTopBarLayout()
	{
		return mTopBarLayout;
	}
	
	public RelativeLayout getBottomBarLayout()
	{
		return mBottomBar;
	}
	
	/** 录制按钮触摸事件处理 */
	private View.OnTouchListener mOnTouchListener = new View.OnTouchListener() 
	{
		@SuppressLint("ClickableViewAccessibility")
		@Override
		public boolean onTouch(View v, MotionEvent event)
		{
			if(getDelegate() == null || mCamera.getRecordMode() != RecordMode.Keep) return false;
			
			switch (event.getAction())
			{
				case MotionEvent.ACTION_DOWN:
					
					if (!getDelegate().isRecording())
		            {
		                getDelegate().startRecording();
		            }
					break;
					
				case MotionEvent.ACTION_UP:
				case MotionEvent.ACTION_CANCEL:
					
					updateTopBarStatus(false);
					if (getDelegate() != null && getDelegate().isRecording())
					{
						getDelegate().pauseRecording();
					}
					break;   
	        }
			return false;
		}
	};

	/** 按钮点击事件处理 */
	private View.OnClickListener mButtonClickListener = new View.OnClickListener()
	{
		public void onClick(View v)
		{
			dispatchClickEvent(v);
		}
	};

	protected void dispatchClickEvent(View v)
	{
		if (v == mCloseView)
		{
			if(getDelegate() != null) getDelegate().finishRecordActivity();
		}
		else if (v == mFlashButton)
		{
			mFlashEnabled = !mFlashEnabled;

			mCamera.setFlashMode(mFlashEnabled ? CameraFlash.Torch
					: CameraFlash.Off);

			// 闪光灯
			updateFlashButtonStatus();
		}
		else if (v == mToggleButton)
		{
			mCamera.rotateCamera();
		}
		else if (v == mStickerButton)
		{
			handleStickerButton();
		}
		else if (v == mFilterButton)
		{
			handleFilterButton();
		}
		else if (v == mTouchView)
		{
			hideStickerStaff();
			hideFilterStaff();
		}
		else if (v == mBeautyLayout)
		{
			getBeautyBarWrap().setVisibility(View.VISIBLE);
			getBeautyBar().setProgress(mCamera.getBeautyLevel());
			mBeautyLevel.setText((int)(mCamera.getBeautyLevel()*100) + "%");
			updateBeautyStatus(true);
			updateButtonStatus(mBeautyBtn, mCamera.getBeautyLevel() > (float)0);

			// 隐藏滤镜调节栏
			getFilterConfigView().setVisibility(View.INVISIBLE);
			// 隐藏滤镜选中边框
			updateFilterBorderView(lastSelectedCellView,true);
		}
		else if (v == mConfirmButton)
		{
			updateButtonStatus(mConfirmButton, false);
			updateButtonStatus(mRollBackButton, false);
			initProgressList();
			mCamera.finishRecording();
		}
		else if (v == mRollBackButton)
		{
			// 点击后退按钮删除上一条视频
			if (progressList.size() > 1)
			{
				mCamera.popVideoFragment();
				
				// 上一条视频所在的 position
				int positon = progressList.size() - 2;
				int progress = progressList.get(positon);
				
				// 删除最后一条视频
				progressList.remove(positon + 1);
				
				// 设置进度条显示上一条视频的进度
				mProgressBar.setProgress(progress);
				lastProgress = mProgressBar.getProgress();
				
				if (mProgressBar.getProgress() == 0)
				{
					updateButtonStatus(mRollBackButton, false);
					updateButtonStatus(mConfirmButton, false);
				}
				
				if (interuptLayout.getChildCount() != 0) 
				{
					interuptLayout.removeViewAt(interuptLayout.getChildCount() - 1);
				}

				updateViewStatus(false);
				updateButtonStatus(mConfirmButton, mCamera.getMovieDuration() >= mCamera.getMinRecordingTime());
			}
        }
		else if (v == mRecordButton)
        {
        	if(getDelegate() == null) return;
        	
            if (!getDelegate().isRecording())
            {
                getDelegate().startRecording();
                updateShowStatus(true);
            }
            else
            {
            	if (mCamera.getMovieDuration() >= Constants.MIN_RECORDING_TIME) 
            	{
            		getDelegate().stopRecording();
            		updateShowStatus(false);
				}
            	else 
            	{
					String msg = getStringFromResource("min_recordTime") + Constants.MIN_RECORDING_TIME + "s";
		        	if (mActived)
		        		TuSdk.messageHub().showToast(mContext, msg);
				}
            }
        }
		else if(v == mMovieImportButton)
		{
			handleImportButton();
		}
	}
	
	/**
	 * 初始化存储进度集合
	 */
	public void initProgressList()
	{
		lastProgress = 0;
		//清除所有存储进度并设置初始值
		progressList.clear();
		progressList.add(0);
		// 录制完成进度清零(断点续拍模式)
		mProgressBar.setProgress(0);
		interuptLayout.removeAllViews();
	}
	
	private void handleImportButton()
	{
		Intent pickIntent = new Intent(Intent.ACTION_GET_CONTENT);
		pickIntent.setType("video/*");
		pickIntent.addCategory(Intent.CATEGORY_OPENABLE);
		((Activity) mContext).startActivityForResult(pickIntent, IMAGE_PICKER_SELECT);
	}
	
	/**
	 * 更新美颜按钮、拖动条显示状态
	 * 
	 * @param clickable
	 */
	private void updateBeautyStatus(boolean clickable)
	{
		int imgId = clickable ? R.drawable.tusdk_view_beauty_roundcorner_selected_bg
				: R.drawable.tusdk_view_beauty_roundcorner_unselected_bg;
		mBeautyLayout.setBackgroundResource(imgId);
		getBeautyBarWrap().setVisibility(clickable ? View.VISIBLE : View.INVISIBLE);
	}

	/**
	 * 更新录制按钮显示状态
	 *
	 * @param isRunning 是否录制中
	 */
	protected void updateShowStatus(boolean isRunning)
	{
		int imgID = isRunning ? R.drawable.lsq_style_default_record_btn_record_selected : R.drawable.lsq_style_default_record_btn_record_unselected;

		if (mRecordButton != null)
			mRecordButton.setBackgroundResource(imgID);
	}

	/**
	 * 更新闪光灯按钮显示状态
	 */
	protected void updateFlashButtonStatus()
    {
        if (mFlashButton != null)
        {
            int imgID = mFlashEnabled ? R.drawable.lsq_style_default_btn_flash_on : R.drawable.lsq_style_default_btn_flash_off;

            mFlashButton.setImageResource(imgID);
        }
    }
	
	/**
	 * 开始录制，更新视图显示状态
	 */
	private void updateViewStatus(boolean isRecording)
	{
		updateButtonStatus(mStickerButton, !isRecording);
		updateButtonStatus(mFilterButton, !isRecording);
	}
	
	/**
	 * 更新顶部栏视图显示状态
	 * 
	 * @param isRecording
	 */
	private void updateTopBarStatus(boolean isRecording)
	{
		mCloseView.setVisibility(isRecording? View.GONE : View.VISIBLE);
		mFlashButton.setVisibility(isRecording? View.GONE : View.VISIBLE);
		mToggleButton.setVisibility(isRecording? View.GONE : View.VISIBLE);
	}

	/**
	 * 更新按钮显示状态
	 * 
	 * @param button
	 * @param clickable
	 */
	private void updateButtonStatus(TuSdkTextButton button, boolean clickable)
	{
		int imgId = 0, colorId = 0;
		
		switch (button.getId())
		{
		case R.id.lsq_confirmWrap:
			imgId = clickable? R.drawable.lsq_style_default_btn_finish_selected 
					: R.drawable.lsq_style_default_btn_finish_unselected;
			colorId = clickable? R.color.lsq_filter_title_color : R.color.lsq_filter_title_unselected_color;
			button.setClickable(clickable);
			break;
			
		case R.id.lsq_backWrap:
			imgId = clickable? R.drawable.lsq_style_default_btn_back_selected 
					: R.drawable.lsq_style_default_btn_back_unselected;
			colorId = clickable? R.color.lsq_filter_title_color : R.color.lsq_filter_title_unselected_color;
			button.setClickable(clickable);
			break;
			
		case R.id.lsq_stickerWrap:
			imgId = clickable? R.drawable.lsq_style_default_btn_sticker 
					: R.drawable.lsq_style_default_btn_sticker_unselected;
			colorId = clickable? R.color.lsq_filter_title_color : R.color.lsq_filter_title_unselected_color;
			button.setClickable(clickable);
			break;
			
		case R.id.lsq_filterWrap:
			imgId = clickable? R.drawable.lsq_style_default_btn_filter_selected
					: R.drawable.lsq_style_default_btn_filter_unselected;
			colorId = clickable? R.color.lsq_filter_title_color : R.color.lsq_filter_title_unselected_color;
			button.setClickable(clickable);
			break;
			
		case R.id.lsq_beautyBtn:
			imgId = clickable ? R.drawable.lsq_style_default_btn_beauty_selected
					: R.drawable.lsq_style_default_btn_beauty_unselected;
			colorId = clickable ? R.color.lsq_filter_title_color
					: R.color.lsq_filter_title_default_color;
			break;

		default:
			break;
		}
		
		button.setCompoundDrawables(null, TuSdkContext.getDrawable(imgId), null, null);
		button.setTextColor(TuSdkContext.getColor(colorId));
	}

	// 添加视频断点标记
	private void addInteruptPoint(float margingLeft)
	{
		// 添加断点标记
		Button interuptBtn = new Button(mContext);
		RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(2,
				LayoutParams.MATCH_PARENT);
		
		interuptBtn.setBackgroundColor(TuSdkContext.getColor("lsq_progress_interupt_color"));
		lp.setMargins((int) Math.ceil(margingLeft), 0, 0, 0);
		interuptBtn.setLayoutParams(lp);
		interuptLayout.addView(interuptBtn);
	}
 	
	/**
	 * 设置当前录制的时间及进度条进度
	 */
	private void setTimeProgress(float currentDuration, float progress)
	{
		mProgressBar.setProgress((int) Math.ceil(progress * 100));
	}
	
	/**
	 * 开始录制时更新视图显示
	 * 
	 * @param isRecording
	 */
	public void updateViewOnStartRecording(boolean isRecording)
	{
    	updateViewStatus(isRecording);
    	updateTopBarStatus(isRecording);
    	updateButtonStatus(mConfirmButton, false);
	}
	
	/**
	 * 暂停录制时更新视图显示
	 * 
	 * @param isClickable
	 */
	public void updateViewOnPauseRecording(boolean isClickable)
	{
		updateShowStatus(false);
    	updateButtonStatus(mStickerButton, true);
    	updateButtonStatus(mFilterButton, true);
    	//录制时长大于最小限制时长且非录制状态下确认按钮才可以点击
    	updateButtonStatus(mConfirmButton, isClickable);
	}
	
	/**
	 * 停止录制时更新视图显示
	 * 
	 * @param isRecording
	 */
	public void updateViewOnStopRecording(boolean isRecording)
	{
		updateTopBarStatus(isRecording);
	}
	
	/**
	 * 录制完成时更新视图显示
	 * 
	 * @param isRecording
	 */
	public void updateViewOnMovieRecordComplete(boolean isRecording)
	{
    	TuSdk.messageHub().dismissRightNow();
		String msg = getStringFromResource("new_movie_saved");
		if (mActived)
			TuSdk.messageHub().showSuccess(mContext, msg);
    	
    	updateShowStatus(isRecording);
    	updateViewStatus(isRecording);
    	updateTopBarStatus(isRecording);
    	
    	// 录制完进度清零(正常录制模式)
    	mProgressBar.setProgress(0);
	}
	
	/**
	 * 录制错误时更新视图显示
	 * 
	 * @param error
	 * @param isRecording
	 */
	public void updateViewOnMovieRecordFailed(RecordError error, boolean isRecording)
	{
        if (error==RecordError.MoreMaxDuration) // 超过最大时间 （超过最大时间是再次调用startRecording时会调用） 
        {
        	 String msg = getStringFromResource("over_max_recordTime");
     		 if (mActived)
     			 TuSdk.messageHub().showError(mContext, msg);
        	 updateButtonStatus(mConfirmButton, true);
        	 
		}else if (error==RecordError.SaveFailed) // 视频保存失败
        {
        	String msg = getStringFromResource("new_movie_error_saving");
    		if (mActived)
    			TuSdk.messageHub().showError(mContext, msg);
     		updateButtonStatus(mConfirmButton, true);
		}
		else if (error == RecordError.InvalidRecordingTime)
		{
    		if (mActived)
    			TuSdk.messageHub().showError(mContext, R.string.lsq_record_time_invalid);
		}


    	updateShowStatus(isRecording);
    	updateViewStatus(isRecording);
	}
	
	/**
	 * 录制状态发生改变时更新视图显示
	 * 
	 * @param state
	 * @param isRecording
	 */
	public void updateViewOnMovieRecordStateChanged(RecordState state, boolean isRecording)
	{
        if(state == RecordState.Recording) // 开始录制
        {
            updateShowStatus(isRecording);
            updateButtonStatus(mRollBackButton, false);
            
        }else if(state == RecordState.Paused) // 已暂停录制
        {
         	if (mProgressBar.getProgress() != 0)
            {
                addInteruptPoint(TuSdkContext.getDisplaySize().width * mProgressBar.getProgress() / 100);
            }
         	
        	// 存储每段视频结束时进度条的进度
			lastProgress = mProgressBar.getProgress();
			progressList.add(lastProgress);
			updateButtonStatus(mRollBackButton, true);
			updateShowStatus(false);
        }else if(state == RecordState.RecordCompleted) //录制完成弹出提示（续拍模式下录过程中超过最大时间时调用）
        {
        	String msg = getStringFromResource("max_recordTime") + Constants.MAX_RECORDING_TIME + "s";
        	if (mActived)
	    	   TuSdk.messageHub().showToast(mContext, msg);
	    	
			updateShowStatus(false);
			updateButtonStatus(mConfirmButton, true);
			
        }else if(state == RecordState.Saving) // 正在保存视频
        {
        	String msg = getStringFromResource("new_movie_saving");
        	if (mActived)
 	    	   TuSdk.messageHub().setStatus(mContext, msg);
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
		setTimeProgress(durationTime, progress);
	}
	
	/** 显示贴纸视图 */
	protected void handleStickerButton()
	{
		showStickerLayout();
	}
	
	/** 更新底部栏的显示状态 */
	private void updateBottomBar(boolean isHidden)
	{
		mBottomBar.setVisibility(isHidden ? View.INVISIBLE : View.VISIBLE);
	}

	/** 显示滤镜视图 */
	protected void handleFilterButton()
	{
		showFilterLayout();
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
		ViewCompat.animate(mStickerBottomView).translationY(0).setDuration(200).setListener(mViewPropertyAnimatorListener);
	}
	
	/** 属性动画监听事件 */
	private ViewPropertyAnimatorListener mViewPropertyAnimatorListener = new ViewPropertyAnimatorListener()
	{

		@Override
		public void onAnimationCancel(View view)
		{
		}

		@Override
		public void onAnimationEnd(View view) 
		{
			updateBottomBar(true);
			ViewCompat.animate(mStickerBottomView).setListener(null);
			ViewCompat.animate(mFilterBottomView).setListener(null);
		}

		@Override
		public void onAnimationStart(View view) 
		{
		}
	};
	
	/**
	 * 更新贴纸栏相关视图的显示状态
	 * 
	 * @param isShow
	 */
	private void updateStickerViewStaff(boolean isShow)
	{
		mStickerBottomView.setVisibility(isShow? View.VISIBLE: View.INVISIBLE);
		mTouchView.setVisibility(isShow? View.VISIBLE: View.INVISIBLE);
	}

	/**
	 * 点击贴纸栏上方的空白区域隐藏贴纸栏
	 */
	public void hideStickerStaff()
	{
		if(mStickerBottomView.getVisibility() == View.INVISIBLE) return;
		
		updateStickerViewStaff(false);
		
		// 滤镜栏向下动画并隐藏
		ViewCompat.animate(mStickerBottomView)
		.translationY(mStickerBottomView.getHeight()).setDuration(200);
		
		updateBottomBar(false);
	}

	/**
	 * 显示滤镜底部栏
	 */
	public void showFilterLayout()
	{
		// 第一次进入滤镜页面默认选中无效果滤镜
		if (!isFirstTimeRun)
		{
			FilterCellView firstItem= (FilterCellView) mFilterListView.getChildAt(0);
			selectFilter(firstItem, 0);
			lastSelectedCellView=firstItem;
			isFirstTimeRun=true;
		}
		
		updateFilterViewStaff(true);
		
		// 滤镜栏向上动画并显示
		ViewCompat.setTranslationY(mFilterBottomView,
				mFilterBottomView.getHeight());
		ViewCompat.animate(mFilterBottomView).translationY(0).setDuration(200).setListener(mViewPropertyAnimatorListener);
	}

	/**
	 * 点击滤镜栏上方的空白区域隐藏滤镜栏
	 */
	public void hideFilterStaff()
	{
		if(mFilterBottomView.getVisibility() == View.INVISIBLE) return;

		updateFilterViewStaff(false);
		
		// 滤镜栏向下动画并隐藏
		ViewCompat.animate(mFilterBottomView)
				.translationY(mFilterBottomView.getHeight()).setDuration(200);
		updateBottomBar(false);
	}
	
	/**
	 * 更新滤镜栏相关视图的显示状态
	 * 
	 * @param isShow
	 */
	private void updateFilterViewStaff(boolean isShow)
	{
		mRecordButton.setVisibility(isShow? View.INVISIBLE: View.VISIBLE);
		mFilterButton.setVisibility(isShow? View.INVISIBLE: View.VISIBLE);
		mStickerButton.setVisibility(isShow? View.INVISIBLE: View.VISIBLE);
		 
		mFilterBottomView.setVisibility(isShow? View.VISIBLE: View.INVISIBLE);
		mTouchView.setVisibility(isShow? View.VISIBLE: View.INVISIBLE);
	}

	/**
	 * 初始化滤镜栏视图
	 */
	protected void initFilterListView()
	{
	    getFilterConfigView().setVisibility(View.INVISIBLE);
		getFilterListView();

		if (mFilterListView == null)
			return;

		this.mFilterListView.setModeList(Arrays.asList(Constants.VIDEOFILTERS));
	}

	/**
	 * 滤镜栏视图
	 * 
	 * @return
	 */
	public FilterListView getFilterListView()
	{
		if (mFilterListView == null)
		{
			mFilterListView = (FilterListView) findViewById(R.id.lsq_filter_list_view);
			mFilterListView.loadView();
			mFilterListView.setCellLayoutId(R.layout.filter_list_cell_view);
			mFilterListView.setCellWidth(TuSdkContext.dip2px(80));
			mFilterListView.setItemClickDelegate(mFilterTableItemClickDelegate);
			mFilterListView.reloadData();
		}
		return mFilterListView;
	}

	/**
	 * 初始化贴纸组视图
	 */
	protected void initStickerListView()
	{
		getStickerListView();

		if (mStickerListView == null)
			return;

		List<StickerGroup> groups = new ArrayList<StickerGroup>();
		groups.addAll(StickerLocalPackage.shared().getSmartStickerGroups());

		groups.add(0, new StickerGroup());
		this.mStickerListView.setModeList(groups);
	}

	/**
	 * 贴纸组视图
	 */
	public StickerListView getStickerListView()
	{
		if (mStickerListView == null)
		{
			mStickerListView = (StickerListView) findViewById(R.id.lsq_sticker_list_view);
			mStickerListView.loadView();
			mStickerListView.setCellLayoutId(R.layout.sticker_list_cell_view);
			GridLayoutManager grid = new GridLayoutManager(mContext, 5);
			mStickerListView.setLayoutManager(grid);
			mStickerListView.setCellWidth(TuSdkContext.dip2px(80));
			mStickerListView.setItemClickDelegate(mStickerTableItemClickDelegate);
			mStickerListView.reloadData();
		}
		return mStickerListView;
	}

	/** 滤镜组列表点击事件 */
	private TuSdkTableViewItemClickDelegate<String, FilterCellView> mFilterTableItemClickDelegate = new TuSdkTableViewItemClickDelegate<String, FilterCellView>()
	{
		@Override
		public void onTableViewItemClick(String itemData,
				FilterCellView itemView, int position)
		{
			onFilterGroupSelected(itemData, itemView, position);
		}
	};

	/**
	 * 贴纸组列表点击事件
	 */
	private TuSdkTableView.TuSdkTableViewItemClickDelegate<StickerGroup, StickerCellView> mStickerTableItemClickDelegate = new TuSdkTableView.TuSdkTableViewItemClickDelegate<StickerGroup, StickerCellView>()
	{
		@Override
		public void onTableViewItemClick(StickerGroup itemData,
				StickerCellView itemView, int position)
		{
			onStickerGroupSelected(itemData, itemView, position);
		}
	};

	/**
	 * 滤镜配置视图
	 * 
	 * @return
	 */
	private FilterConfigView getFilterConfigView()
	{
       if (mConfigView == null)
       {
           mConfigView = (FilterConfigView) findViewById(R.id.lsq_filter_config_view);
       }
    
       return mConfigView;
	}
	
	/**
	 * 美颜调节SeekBar
	 * @return
	 */
	private TuSeekBar getBeautyBar()
	{
		if(mBeautyBar == null)
		{
			mBeautyBar = (TuSeekBar) findViewById(R.id.lsq_seekBar);
			mBeautyBar.setDelegate(mTuSeekBarDelegate);
		}
		return mBeautyBar;
	}
	
	/**
	 * 美颜调节栏配置视图
	 * @return
	 */
	private RelativeLayout getBeautyBarWrap()
	{
		if(mBeautyBarWrap == null)
		{
			mBeautyBarWrap = (RelativeLayout) findViewById(R.id.lsq_beauty_config_view);
		}
		return mBeautyBarWrap;
	}

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
		changeVideoFilterCode(itemData);

		deSelectLastFilter(lastSelectedCellView, position);

		selectFilter(itemView, position);
		
        getFilterConfigView().setVisibility(
                FilterManager.shared().isNormalFilter(itemData) || (getFilterConfigView().getVisibility() == View.INVISIBLE
                && getBeautyBarWrap().getVisibility() == View.INVISIBLE) ? View.INVISIBLE : View.VISIBLE);
        
		// 更改美颜状态
		updateBeautyStatus(false);
	}

	/**
	 * 滤镜选中状态
	 * 
	 * @param itemView
	 * @param position
	 */
	private void selectFilter(FilterCellView itemView, int position)
	{
		updateFilterBorderView(itemView, false);
		itemView.setFlag(position);
		TextView titleView = itemView.getTitleView();
		titleView.setBackground(TuSdkContext.getDrawable(R.drawable.tusdk_view_filter_selected_text_roundcorner));
		// 记录本次选中的滤镜
		lastSelectedCellView = itemView;
	}

	/**
	 * 取消上一个滤镜的选中状态
	 * 
	 * @param lastFilter
	 * @param position
	 */
	private void deSelectLastFilter(FilterCellView lastFilter, int position)
	{
		if (lastFilter == null)
			return;

		lastFilter.setFlag(-1);
		updateFilterBorderView(lastFilter,true);
		lastFilter.getTitleView().setBackground(TuSdkContext.getDrawable(R.drawable.tusdk_view_filter_unselected_text_roundcorner));
		lastFilter.getImageView().invalidate();
	}
	
	/**
	 * 设置滤镜单元边框是否可见
	 * @param lastFilter
	 * @param isHidden
	 */
	private void updateFilterBorderView(FilterCellView lastFilter,boolean isHidden)
	{
		RelativeLayout filterBorderView = lastFilter.getBorderView();
		filterBorderView.setVisibility(isHidden ? View.GONE : View.VISIBLE);
	}
	
	/**
	 * 获取字符串资源
	 * 
	 * @param fieldName
	 * @return
	 */
	protected String getStringFromResource(String fieldName)
	{
		int stringID = this.getResources().getIdentifier(fieldName, "string",
				this.mContext.getPackageName());

		return getResources().getString(stringID);
	}

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
		// 清除上一个选中贴纸的背景
		if (mLastStickerView != null)
		{
			mLastStickerView.setBackground(null);
		}

		RelativeLayout stickerLayout = (RelativeLayout) itemView
				.findViewById(R.id.lsq_item_wrap);
		stickerLayout.setBackground(TuSdkContext
				.getDrawable(R.drawable.sticker_cell_background));

		// 记录下选中的贴纸
		mLastStickerView = stickerLayout;

		// 设置点击贴纸时呈现或是隐藏贴纸
		if (position == 0)
		{
			mCamera.removeAllLiveSticker();
		}
		else
		{
			mCamera.showGroupSticker(itemData);
		}
	}

	/**
	 * 传递录制相机对象
	 * 
	 * @param camera
	 */
	public void setUpCamera(Context context, TuSDKRecordVideoCamera camera)
	{
		this.mContext = context;
		this.mCamera = camera;
		
		mCamera.setDelegate(mVideoCameraDelegate);
		
		// 根据录制模式不同，给开始录制按钮加上不同的监听事件
	    if (mCamera.getRecordMode()==RecordMode.Normal)
	    {
	       mRecordButton.setOnClickListener(mButtonClickListener);
	       
	    } else if(mCamera.getRecordMode()==RecordMode.Keep){
	    	
		    mRecordButton.setOnTouchListener(mOnTouchListener);
	    }
	}
	
	/**
	 * 切换滤镜
	 * @param code
	 */
	protected void changeVideoFilterCode(String code)
    {
    	mCamera.switchFilter(code);
    }

	/**
	 * 滤镜效果改变监听事件
	 */
	protected TuSDKVideoCameraDelegate mVideoCameraDelegate = new TuSDKVideoCameraDelegate() 
    {
        @Override
        public void onFilterChanged(SelesOutInput selesOutInput)
        {
            if (selesOutInput != null && getFilterConfigView() != null) {
                getFilterConfigView().setSelesFilter(selesOutInput);
            }
        }

		@Override
		public void onVideoCameraStateChanged(SelesVideoCameraInterface camera, CameraState newState){
		}

		@Override
		public void onVideoCameraScreenShot(SelesVideoCameraInterface camera, Bitmap bitmap) {
			
		}
    };

	/** 美颜拖动条监听事件 */
    private TuSeekBar.TuSeekBarDelegate mTuSeekBarDelegate = new TuSeekBar.TuSeekBarDelegate()
    {
        @Override
        public void onTuSeekBarChanged(TuSeekBar seekBar, float progress) 
        {
          mBeautyLevel.setText((int)(progress*100) + "%");
          mCamera.setBeautyLevel(progress);

          if (progress <= 0)
          {
              updateButtonStatus(mBeautyBtn, false);
          }
          else
          {
              updateButtonStatus(mBeautyBtn, true);
          }
        }
    };

}