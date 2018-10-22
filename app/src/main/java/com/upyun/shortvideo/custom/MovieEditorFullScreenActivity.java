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
import com.upyun.shortvideo.views.EffectsTimelineView;
import com.upyun.shortvideo.views.MagicEditorLayout;
import com.upyun.shortvideo.views.MagicEffectCellView;
import com.upyun.shortvideo.views.MagicEffectLayout;
import com.upyun.shortvideo.views.MovieEditorTabBar;

import org.lasque.tusdk.core.TuSdkContext;
import org.lasque.tusdk.core.decoder.TuSDKVideoInfo;
import org.lasque.tusdk.core.seles.sources.TuSdkEditorTranscoder;
import org.lasque.tusdk.core.struct.TuSdkSize;
import org.lasque.tusdk.core.utils.TLog;
import org.lasque.tusdk.core.view.recyclerview.TuSdkTableView;
import org.lasque.tusdk.video.editor.TuSDKMediaParticleEffectData;
import org.lasque.tusdk.video.editor.TuSDKTimeRange;
import com.upyun.shortvideo.R;

import static com.upyun.shortvideo.views.MovieEditorTabBar.TabType.ParticleEffectTabType;

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
	private TuSDKMediaParticleEffectData mCurrentMagicEffectModel;

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
		return R.layout.movie_editor_full_screen_activity;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		// 背景设置为透明效果
		mTopBarLayout = (RelativeLayout) findViewById(R.id.lsq_topBar);
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
		((TextView)findViewById(R.id.lsq_title)).setTextColor(getResources().getColor(R.color.lsq_color_white));

		getMVLayout().setBackgroundColor(TuSdkContext.getColor("lsq_color_semitransparent"));
		mFilterLayout.setBackgroundColor(TuSdkContext.getColor("lsq_color_semitransparent"));
		mRangeSelectionBar.setBackgroundColor(TuSdkContext.getColor("lsq_color_semitransparent"));
		getMagicEffectLayout().setBackgroundColor(TuSdkContext.getColor("lsq_color_semitransparent"));
	}

	@Override
	protected void updateSelectedTabType()
	{
		initParticleEffectEditLayout();

		initMagicEffectEditorLayout();

		// 底部栏默认选中魔法特效
		getTabBar().updateButtonStatus(getTabBar().getMagicTab(), true);
		getTabBar().getDelegate().onSelectedTabType(ParticleEffectTabType);
	}

	/**
	 * 魔法特效编辑
	 */
	private View.OnTouchListener mMagicEditorLayoutTouchListener = new View.OnTouchListener() {
		private boolean isEnd;
	    @Override
		public boolean onTouch(View view, MotionEvent event)
		{
			if (mCurrentMagicCode == null) return false;

			final PointF pointF = getConvertedPoint(event.getX(),event.getY());

			Rect rect = new Rect();
			mActionButton.getHitRect(rect);
			if(mActionButton.getVisibility() == View.VISIBLE && rect.contains((int)event.getX(),(int)event.getY())){
				return false;
			}

			switch (event.getAction())
			{
				case MotionEvent.ACTION_DOWN:

					mHandler.postDelayed(new Runnable() {
						@Override
						public void run() {

							// 构建魔法特效
							mCurrentMagicEffectModel = new TuSDKMediaParticleEffectData(mCurrentMagicCode);

							mCurrentMagicEffectModel.setSize(mMagicEffectEditorLayout.getSize());
							mCurrentMagicEffectModel.setColor(mMagicEffectEditorLayout.getColor());
							mCurrentMagicEffectModel.putPoint(mMovieEditor.getEditorPlayer().getCurrentSampleTimeUs(),pointF);

							// 预览魔法特效
							mMovieEditor.getEditorEffector().addMediaEffectData(mCurrentMagicEffectModel);

							long startTimeUs = mMovieEditor.getEditorPlayer().getCurrentSampleTimeUs();
							if (mMovieEditor.getEditorPlayer().getCurrentSampleTimeUs() == mMovieEditor.getEditorPlayer().getTotalTimeUS()) {
								startTimeUs = 0;
                                isEnd = true;
							}

							//构建TimeLineViewModel
							mCurrentMagicEffectModel.setAtTimeRange(TuSDKTimeRange.makeTimeUsRange(startTimeUs,Long.MAX_VALUE));
							EffectsTimelineView.EffectsTimelineSegmentViewModel magicEffectSegment = new EffectsTimelineView.EffectsTimelineSegmentViewModel("lsq_margic_effect_color_"+mCurrentMagicCode);
							magicEffectSegment.setMediaEffectData(mCurrentMagicEffectModel);
							float startProgress = (float)startTimeUs/(float)mMovieEditor.getEditorPlayer().getTotalTimeUS();
							float endProgress = (float) startTimeUs/(float)mMovieEditor.getEditorPlayer().getTotalTimeUS();
							magicEffectSegment.makeProgressRange(startProgress,endProgress);


							mMagicEffectEditorLayout.getTimelineView().setEditable(true);
							mMagicEffectEditorLayout.getTimelineView().addEffectMode(magicEffectSegment);

							mMagicEffectLayout.getTimelineView().setEditable(true);
							mMagicEffectLayout.getTimelineView().addEffectMode(magicEffectSegment);

							startEffectsPreview();

							mCurrentMagicEffectModel.getFilterWrap().updateParticleEmitPosition(pointF);

						}
					},MIN_PRESS_DURATION_MILLIS);

					break;
				case MotionEvent.ACTION_UP:
				case MotionEvent.ACTION_CANCEL:

					mHandler.removeCallbacksAndMessages(null);

					if (mCurrentMagicEffectModel == null) return false;

					// 取消预览魔法特效
					mCurrentMagicEffectModel.getAtTimeRange().setEndTimeUs(mMovieEditor.getEditorPlayer().getCurrentSampleTimeUs());

					EffectsTimelineView.EffectsTimelineSegmentViewModel model = mMagicEffectLayout.getTimelineView().lastEffectMode();
					float endProgress = (float) mMovieEditor.getEditorPlayer().getCurrentSampleTimeUs() / (float) mMovieEditor.getEditorPlayer().getTotalTimeUS();
                    if (endProgress > model.getProgressRange().getStartProgress() && !( isEnd && endProgress == 1)) {
                        model.makeProgressRange(model.getProgressRange().getStartProgress(), endProgress);
                        isEnd = false;
                    }

					mMagicEffectEditorLayout.getTimelineView().setEditable(false);
					mMagicEffectLayout.getTimelineView().setEditable(false);

					mCurrentMagicEffectModel = null;

					pausePreview();
					break;
				case MotionEvent.ACTION_MOVE:

					if (mCurrentMagicEffectModel == null) return false;

					// 更新魔法特效触发位置（预览）
					mCurrentMagicEffectModel.getFilterWrap().updateParticleEmitPosition(pointF);
					// 记录魔法特效触发位置
					mCurrentMagicEffectModel.putPoint(mMovieEditor.getEditorPlayer().getCurrentSampleTimeUs(),pointF);

					break;
			}

			return true;
		}
	};


	/**
	 * 获取到视频信息
	 *
	 * @param videoInfo
	 */
	@Override
	protected void onVideoInfoReady(TuSDKVideoInfo videoInfo)
	{
		super.onVideoInfoReady(videoInfo);

		mMagicEffectEditorLayout.getTimelineView().setDurationTimueUs(videoInfo.durationTimeUs);
		getMagicEffectLayout().getTimelineView().setDurationTimueUs(videoInfo.durationTimeUs);
	}


	@Override
	protected void loadComplete(TuSDKVideoInfo videoInfo) {
		super.loadComplete(videoInfo);
		setPreviewSize(getCameraView(),mMovieEditor.getEditorTransCoder().getVideoInfo().width,mMovieEditor.getEditorTransCoder().getVideoInfo().height);
	}

	/**
	 * 视频播放进度改变通知
	 *
	 * @param duratioimTimeUs
	 * 			持续时间
	 * @param progress
	 *  		当前进度
	 */
	public void onPlayerProgressChanged(long duratioimTimeUs, float progress)
	{
		super.onPlayerProgressChanged(duratioimTimeUs, progress);

		mMagicEffectEditorLayout.getTimelineView().setProgress(progress);
			mMagicEffectEditorLayout.getTimelineView().updateLastEffectModelEndTime(progress);
		mMagicEffectLayout.getTimelineView().setProgress(progress);
			mMagicEffectLayout.getTimelineView().updateLastEffectModelEndTime(progress);

	}

	@Override
	protected void onPlayStateChanged(int state) {
		super.onPlayStateChanged(state);
		changeMagicPlayBtn(state == 1);
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
	private void initMagicEffectEditorLayout()
	{
		mMagicEffectEditorLayout = (MagicEditorLayout) findViewById(R.id.lsq_magic_preview_layout);
		mMagicEffectEditorLayout.setMovieEditor(mMovieEditor);
		mMagicEffectEditorLayout.loadView();
		mMagicEffectEditorLayout.setDelegate(mMagicPreviewLayoutDelegate);
		mMagicEffectEditorLayout.setOnTouchListener(mMagicEditorLayoutTouchListener);

		mEditorMainLayout = (RelativeLayout) findViewById(R.id.lsq_editor_main_layout);
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

			TLog.e("onSizeSeekBarProgressChanged:%s",seekbar.getSeekbar().getProgress());
		}

		@Override
		public void onColorSeekBarProgressChanged(int color) {
			TLog.e("onColorSeekBarProgressChanged:%s",color);
		}

		@Override
		public void onMagicPreviewPlay()
		{
			changeMagicPlayBtn(mMovieEditor.getEditorPlayer().isPause());
			if(!mMovieEditor.getEditorPlayer().isPause()) pausePreview();
			else startEffectsPreview();
		}
	};

	private void changeMagicPlayBtn(boolean isPause) {
		int playBtnDrawableId;

		if (isPause)
        {
            playBtnDrawableId = R.drawable.lsq_edit_play;
        }
        else
        {
            playBtnDrawableId = R.drawable.lsq_edit_ic_pause;
        }

		mMagicEffectEditorLayout.getMagicPlayBtn().setImageDrawable(getResources().getDrawable(playBtnDrawableId));
	}

	/**
	 * 加载魔法特效视图
	 */
	private void initParticleEffectEditLayout()
	{
		getMagicEffectLayout().loadView();
		getMagicEffectLayout().setDelegate(mMagicTableItemClickDelegate);
	}

	/** 魔法特效列表点击事件 */
	private TuSdkTableView.TuSdkTableViewItemClickDelegate<String, MagicEffectCellView> mMagicTableItemClickDelegate = new TuSdkTableView.TuSdkTableViewItemClickDelegate<String, MagicEffectCellView>()
	{
		@Override
		public void onTableViewItemClick(String code, MagicEffectCellView itemView, int position)
		{
			// 撤销
			if(mMovieEditor.getEditorTransCoder().getStatus() != TuSdkEditorTranscoder.TuSdkTranscoderStatus.Loaded){return;}


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
		if(mMagicEffectEditorLayout.getTimelineView().lastEffectMode() != null)
			mMovieEditor.getEditorEffector().removeMediaEffectData(mMagicEffectEditorLayout.getTimelineView().lastEffectMode().getCurrentMediaEffectData());
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
		setActionButtonStatus(isShown == true ? false : (mMovieEditor.getEditorPlayer().isPause() == true ? true : false));
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
			mMagicEffectLayout = findViewById(R.id.lsq_editor_magic_layout);
			mMagicEffectLayout.setMovieEditor(mMovieEditor);
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

		if (tabType == ParticleEffectTabType)
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
		mTimeEffectsLayout.setVisibility(View.GONE);
		mTextEffectsLayout.setVisibility(View.GONE);
		getVoiceVolumeConfigView().setVisibility(View.INVISIBLE);
		getMVLayout().setVisibility(View.GONE);
		getFilterConfigView().setVisibility(View.INVISIBLE);

		getMagicEffectLayout().setVisibility(View.VISIBLE);

		/**  设置场景特效 */
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
		TuSdkSize videoSize = TuSdkSize.create(mMovieEditor.getEditorTransCoder().getVideoInfo().width,mMovieEditor.getEditorTransCoder().getVideoInfo().height);

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