/**
 * TuSDKVideoDemo
 * MovieEditorFullScreenActivity.java
 *
 * @author     LiuHang
 * @Date:      May 22, 2017 7:28:12 PM
 * @Copyright: (c) 2017 tusdk.com. All rights reserved.
 *
 */

package com.upyun.shortvideo.custom;

import android.graphics.Bitmap;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.upyun.shortvideo.component.MovieEditorActivity;
import com.upyun.shortvideo.views.ConfigViewSeekBar;
import com.upyun.shortvideo.views.MagicEditorLayout;
import com.upyun.shortvideo.views.MagicEffectCellView;
import com.upyun.shortvideo.views.MagicEffectLayout;
import com.upyun.shortvideo.views.MagicEffectModel;
import com.upyun.shortvideo.views.MovieEditorTabBar;

import org.lasque.tusdk.core.TuSdkContext;
import org.lasque.tusdk.core.struct.TuSdkSize;
import org.lasque.tusdk.core.utils.TLog;
import org.lasque.tusdk.core.view.recyclerview.TuSdkTableView;
import org.lasque.tusdk.video.editor.TuSDKMovieEditor;
import org.lasque.tusdk.video.editor.TuSDKTimeRange;

import com.upyun.shortvideo.component.MediaEffectsManager;

/**
 * 视频编辑全屏示例
 * 
 * 功能：
 * 1. 预览视频，添加滤镜，魔法特效查看效果
 * 2. 导出新的视频
 *
 * @author LiuHang
 */
public class MovieEditorFullScreenActivity extends MovieEditorActivity
{
	private static final int MIN_PRESS_DURATION_MILLIS = 200;

	private Handler mHandler = new Handler();

	/** 当前正在编辑的魔法特效code */
	private String mCurrentMagicCode;
	/** 当前正在编辑的魔法特效数据 */
	private MagicEffectModel mCurrentMagicEffectModel;

	// 编辑页主要UI视图
	private RelativeLayout mEditorMainLayout;

	// 视频编辑的标题栏
	private RelativeLayout mTopBarLayout;

	// 魔法特效布局文件
	private MagicEffectLayout mMagicEffectLayout;

	// 魔法效果预览页面
	private MagicEditorLayout mMagicEffectEditorLayout;

	/** 全屏布局 */
	protected int getLayoutId()
	{
		return com.upyun.shortvideo.R.layout.movie_editor_full_screen_activity;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		
		// 背景设置为透明效果
		mTopBarLayout = (RelativeLayout) findViewById(com.upyun.shortvideo.R.id.lsq_topBar);
		getTabBar().getMagicTab().setVisibility(View.VISIBLE);
		updateLayoutBackgroundColor();
	}

	private void updateLayoutBackgroundColor()
	{
		mTopBarLayout.setBackgroundColor(TuSdkContext.getColor("lsq_color_semitransparent"));
		getTabBar().setBackgroundColor(TuSdkContext.getColor("lsq_color_semitransparent"));
		mDubbingLayout.getDubbingListView().setBackgroundColor(TuSdkContext.getColor("lsq_color_semitransparent"));

		getMVLayout().setBackgroundColor(TuSdkContext.getColor("lsq_color_semitransparent"));
		getScenceEffectLayout().setBackgroundColor(TuSdkContext.getColor("lsq_color_semitransparent"));
		// 修改标题颜色
		((TextView)findViewById(com.upyun.shortvideo.R.id.lsq_title)).setTextColor(getResources().getColor(com.upyun.shortvideo.R.color.lsq_color_white));

		getMVLayout().setBackgroundColor(TuSdkContext.getColor("lsq_color_semitransparent"));
		mFilterLayout.setBackgroundColor(TuSdkContext.getColor("lsq_color_semitransparent"));
		mRangeSelectionBar.setBackgroundColor(TuSdkContext.getColor("lsq_color_semitransparent"));
		getMagicEffectLayout().setBackgroundColor(TuSdkContext.getColor("lsq_color_semitransparent"));
	}

	@Override
	protected void updateSelectedTabType()
	{
		initParticleEffectEditLayout();

		initParticlePreviewLayout();

		// 底部栏默认选中魔法特效
		getTabBar().updateButtonStatus(getTabBar().getMagicTab(), true);
		getTabBar().getDelegate().onSelectedTabType(MovieEditorTabBar.TabType.ParticleEffectTabType);
	}

	/**
	 *
	 */
	private View.OnTouchListener mMagicEditorLayoutTouchListener = new View.OnTouchListener() {
		@Override
		public boolean onTouch(View view, MotionEvent event)
		{
			if (mCurrentMagicCode == null) return true;

			final PointF pointF = getConvertedPoint(event.getX(),event.getY());

			switch (event.getAction())
			{
				case MotionEvent.ACTION_DOWN:

					mHandler.postDelayed(new Runnable() {
						@Override
						public void run() {

							startPreView();

							// 编辑魔法特效时禁用循环播放功能
							mMovieEditor.setLooping(false);

							// 构建魔法特效
							mCurrentMagicEffectModel = new MagicEffectModel(mCurrentMagicCode);
							mCurrentMagicEffectModel.setAtTimeRange(TuSDKTimeRange.makeTimeUsRange(mMovieEditor.getCurrentSampleTimeUs(),0l));

							mCurrentMagicEffectModel.setSize(mMagicEffectEditorLayout.getSize());
							mCurrentMagicEffectModel.setColor(mMagicEffectEditorLayout.getColor());
							mCurrentMagicEffectModel.putPoint(mMovieEditor.getCurrentSampleTimeUs(),pointF);

							// 预览魔法特效
							mMovieEditor.applyMediaEffect(mCurrentMagicEffectModel);

							mMagicEffectEditorLayout.getTimelineView().setEditable(true);
							mMagicEffectEditorLayout.getTimelineView().addEffectMode(mCurrentMagicEffectModel);

							mMagicEffectLayout.getTimelineView().setEditable(true);
							mMagicEffectLayout.getTimelineView().addEffectMode(mCurrentMagicEffectModel);



						}
					},MIN_PRESS_DURATION_MILLIS);

					break;
				case MotionEvent.ACTION_UP:
				case MotionEvent.ACTION_CANCEL:

					mHandler.removeCallbacksAndMessages(null);

					if (mCurrentMagicEffectModel == null) return true;

					pausePreview();

					// 编辑完成后启用循环播放功能
					mMovieEditor.setLooping(true);

					// 取消预览魔法特效
                    mMovieEditor.unApplyMediaEffect(mCurrentMagicEffectModel);

					mMagicEffectEditorLayout.getTimelineView().setEditable(false);
					mMagicEffectLayout.getTimelineView().setEditable(false);

					mCurrentMagicEffectModel = null;

					break;
				case MotionEvent.ACTION_MOVE:

					if (mCurrentMagicEffectModel == null) return true;

					// 更新魔法特效触发位置（预览）
					mMovieEditor.updateParticleEmitPosition(pointF);

					// 记录魔法特效触发位置
					mCurrentMagicEffectModel.putPoint(mMovieEditor.getCurrentSampleTimeUs(),pointF);

					break;
			}

			return true;
		}
	};

	@Override
	public void onMovieEditorStatusChanged(TuSDKMovieEditor.TuSDKMovieEditorStatus status)
	{
		super.onMovieEditorStatusChanged(status);

		if (status == TuSDKMovieEditor.TuSDKMovieEditorStatus.Loaded)
		{
			setPreviewSize(getCameraView(),mMovieEditor.getVideoInfo().width,mMovieEditor.getVideoInfo().height);
		}
	}

	/**
	 * 视频播放进度改变通知
	 *
	 * @param durationTime
	 * 			持续时间
	 * @param progress
	 *  		当前进度
	 */
	@Override
	public void onMovieEditProgressChanged(float durationTime, float progress)
	{
		super.onMovieEditProgressChanged(durationTime, progress);

		mMagicEffectEditorLayout.getTimelineView().setProgress(progress);
		mMagicEffectEditorLayout.getTimelineView().updateLastEffectModelEndTime(durationTime);

		mMagicEffectLayout.getTimelineView().setProgress(progress);
		mMagicEffectLayout.getTimelineView().updateLastEffectModelEndTime(durationTime);

	}

	/**
	 * 根据视频大小计算预览区域
	 * 
	 * @param cameraView
	 */
	private void setPreviewSize(RelativeLayout cameraView,int width,int height)
	{
		if(cameraView == null) return;

         int screenWidth= TuSdkContext.getScreenSize().width;
         int screenHeight= TuSdkContext.getScreenSize().height;
         
         Rect boundingRect = new Rect();
         boundingRect.left = 0;
         boundingRect.right = screenWidth;
         boundingRect.top = 0;
         boundingRect.bottom = screenHeight;
         
         Rect rect = makeRectWithAspectRatioInsideRect(new TuSdkSize(width, height), boundingRect);
         int w = rect.right- rect.left;
         int h = rect.bottom - rect.top;
         TLog.i("w: "+ w + ", h: " + h);
         RelativeLayout.LayoutParams lp = new RelativeLayout
                 .LayoutParams(w,h);
         cameraView.setLayoutParams(lp);
	}
	
	/**
	 * 计算在Rect内按比例缩放Size后新的Rect
	 * 
	 * @param aspectRatio
	 * @param boundingRect
	 * @return
	 */
	public  Rect makeRectWithAspectRatioInsideRect(TuSdkSize aspectRatio, Rect boundingRect)
	{
		if (aspectRatio == null || boundingRect == null) return null;

		TuSdkSize cacheSize = new TuSdkSize();
		float ratio = boundingRect.height() /(float) boundingRect.width();
		cacheSize.height = boundingRect.height();
		cacheSize.width = (int) Math.floor(cacheSize.height / ratio);
		if (cacheSize.width > boundingRect.width())
		{
			cacheSize.width = boundingRect.height();
			cacheSize.height = (int) Math.floor(cacheSize.width * ratio);			
		}

		Rect rect = new Rect(boundingRect);
		rect.left = boundingRect.left + (boundingRect.width() - cacheSize.width) / 2;
		rect.right = rect.left + cacheSize.width;
		rect.top = boundingRect.top + (boundingRect.height() - cacheSize.height) / 2;
		rect.bottom = rect.top + cacheSize.height;
		return rect;
	}

	@Override
	protected RelativeLayout getCameraView()
	{
		RelativeLayout cameraView = super.getCameraView();// 播放视图设置触摸事件
		return cameraView;
	}


	@Override
	protected void onDestroy()
	{
		if (mMagicEffectLayout != null)
			mMagicEffectLayout.getTimelineView().clearVideoThumbList();

		super.onDestroy();

		if (mMagicEffectEditorLayout != null)
			mMagicEffectEditorLayout.release();
	}
	/** ----------------------------------------- 魔法效果逻辑 -----------------------------------------  **/

	/**
	 * 初始化魔法预览界面
	 */
	private void initParticlePreviewLayout()
	{
		mMagicEffectEditorLayout = (MagicEditorLayout) findViewById(com.upyun.shortvideo.R.id.lsq_magic_preview_layout);
		mMagicEffectEditorLayout.loadView();
		mMagicEffectEditorLayout.setDelegate(mMagicPreviewLayoutDelegate);
		mMagicEffectEditorLayout.setOnTouchListener(mMagicEditorLayoutTouchListener);
		mMagicEffectEditorLayout.getTimelineView().setDuration(mCutTimeRange.duration());

		mEditorMainLayout = (RelativeLayout) findViewById(com.upyun.shortvideo.R.id.lsq_editor_main_layout);
	}

	private MagicEditorLayout.MagicPreviewLayoutDelegate mMagicPreviewLayoutDelegate = new MagicEditorLayout.MagicPreviewLayoutDelegate()
	{
		@Override
		public void onUndo()
		{
			removeLastParticleEffect();
		}

		@Override
		public void onBackAction()
		{
			toggleMagicPreview(false);
		}

		@Override
		public void onSizeSeekBarProgressChanged(ConfigViewSeekBar seekbar)
		{


		}

		@Override
		public void onColorSeekBarProgressChanged(int color) {

		}

		@Override
		public void onMagicPreviewPlay()
		{
			int playBtnDrawableId;

			if (mMovieEditor.isPreviewing())
			{
				pausePreview();
				playBtnDrawableId = com.upyun.shortvideo.R.drawable.lsq_edit_play;
			}
			else
			{
				startEffectsPreview();
				playBtnDrawableId = com.upyun.shortvideo.R.drawable.lsq_edit_ic_pause;
			}

			mMagicEffectEditorLayout.getMagicPlayBtn().setImageDrawable(getResources().getDrawable(playBtnDrawableId));
		}
	};

	/**
	 * 加载魔法特效视图
	 */
	private void initParticleEffectEditLayout()
	{
		getMagicEffectLayout().loadView();
		getMagicEffectLayout().getTimelineView().setDuration(mCutTimeRange.duration());
		getMagicEffectLayout().setDelegate(mMagicTableItemClickDelegate);
	}

	/** 魔法特效列表点击事件 */
	private TuSdkTableView.TuSdkTableViewItemClickDelegate<String, MagicEffectCellView> mMagicTableItemClickDelegate = new TuSdkTableView.TuSdkTableViewItemClickDelegate<String, MagicEffectCellView>()
	{
		@Override
		public void onTableViewItemClick(String code,MagicEffectCellView itemView, int position)
		{
			// 撤销
			if (position == 0)
			{
				removeLastParticleEffect();
				return;
			}

			mCurrentMagicCode = code;

			mMagicEffectLayout.getMagicEffectListView().setSelectedPosition(position);
			
			toggleMagicPreview(true);
		}
	};

	/**
	 * 移除最后一个魔法特效
	 */
	private void removeLastParticleEffect()
	{
		mMovieEditor.removeMediaEffect(mMagicEffectEditorLayout.getTimelineView().lastEffectMode());
		mMagicEffectLayout.getTimelineView().removeLastEffectMode();
		mMagicEffectEditorLayout.getTimelineView().removeLastEffectMode();
	}

	/**
	 * 切换魔法预览页面
	 *
	 * @param isShown
	 */
	private void toggleMagicPreview(boolean isShown)
	{
		mMagicEffectEditorLayout.setVisibility(isShown ? View.VISIBLE : View.GONE);
		mEditorMainLayout.setVisibility(isShown ? View.GONE : View.VISIBLE);
		mTopBarLayout.setVisibility(isShown ? View.GONE : View.VISIBLE);
		mActionButton.setOnClickListener(isShown ? null : this);
	}

	/**
	 * 初始化魔法效果布局
	 *
	 * @return
	 */
	private MagicEffectLayout getMagicEffectLayout()
	{
		if (mMagicEffectLayout == null)
		{
			mMagicEffectLayout = findViewById(com.upyun.shortvideo.R.id.lsq_editor_magic_layout);
		}

		return mMagicEffectLayout;
	}

	/**
	 * 切换Tab事件
	 *
	 * @param tabType
	 */
	@Override
	public void onSelectedTabType(MovieEditorTabBar.TabType tabType)
	{
		super.onSelectedTabType(tabType);

		getMagicEffectLayout().setVisibility(View.GONE);

		if (tabType == MovieEditorTabBar.TabType.ParticleEffectTabType)
			toggleMagicMode();
	}

	/**
	 * 显示魔法效果布局
	 */
	private void toggleMagicMode()
	{
		// 隐藏其他功能布局
		mRangeSelectionBar.setVisibility(View.GONE);
		mScenceEffectLayout.setVisibility(View.GONE);
		mFilterLayout.setVisibility(View.GONE);
		mDubbingLayout.setVisibility(View.GONE);
		getVoiceVolumeConfigView().setVisibility(View.INVISIBLE);
		getMVLayout().setVisibility(View.GONE);
		getFilterConfigView().setVisibility(View.INVISIBLE);

		getMagicEffectLayout().setVisibility(View.VISIBLE);

		/**  设置场景特效 */
		MediaEffectsManager.getMediaEffectManager().setMagicEffectDataList(mMagicEffectEditorLayout.getTimelineView().getAllMediaEffectData());

		applyMediaEffects();
	}

	/**
	 * 点击坐标系和绘制动画坐标系不同，需要转换坐标
	 *
	 * @return
	 */
	public PointF getConvertedPoint(float x, float y)
	{
		// 获取视频大小
		TuSdkSize videoSize = TuSdkSize.create(mMovieEditor.getVideoInfo().width,mMovieEditor.getVideoInfo().height);

		TuSdkSize previewSize = new TuSdkSize(getCameraView().getMeasuredWidth(), getCameraView().getMeasuredHeight());

		TuSdkSize screenSize = TuSdkContext.getScreenSize();

		RectF previewRectF = new RectF(0, (screenSize.height - previewSize.height) / (float) 2,
				previewSize.width, (screenSize.height + previewSize.height) / (float) 2);

		if (!previewRectF.contains(x, y))
			return new PointF(-1, -1);

		// 将基于屏幕的坐标转换成基于预览区域的坐标
		y -= previewRectF.top;

		// 将预览区域的坐标转换成基于视频实际大小的坐标点
		float videoX = x / (float) previewSize.width * videoSize.minSide();
		float videoY = y / (float) previewSize.height * videoSize.maxSide();

		PointF convertedPoint = new PointF(videoX, videoSize.maxSide() - videoY );
		return convertedPoint;
	}

	@Override
	public void onVideoNewImageLoaded(Bitmap bitmap)
	{
		super.onVideoNewImageLoaded(bitmap);

		// 加载魔法效果页进度条的缩略图
		mMagicEffectLayout.getTimelineView().drawVideoThumb(bitmap);

		// 加载魔法预览页进度条的缩略图
		mMagicEffectEditorLayout.getTimelineView().drawVideoThumb(bitmap);
	}
}