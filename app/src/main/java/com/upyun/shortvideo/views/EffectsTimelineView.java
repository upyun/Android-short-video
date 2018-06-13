package com.upyun.shortvideo.views;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

import org.lasque.tusdk.core.TuSdkContext;
import org.lasque.tusdk.core.utils.image.BitmapHelper;
import org.lasque.tusdk.video.editor.TuSDKMediaEffectData;
import org.lasque.tusdk.video.editor.TuSDKTimeRange;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sprint on 26/12/2017.
 */

public class EffectsTimelineView<T extends TuSDKMediaEffectData & EffectsTimelineView.EffectModelInterface> extends FrameLayout
{
    // 离顶部的间距
    private final int PADDING_TOP = 5;

    // 离顶部的间距
    private final int PADDING_BOTTOM = 5;

    // 当前是否可以编辑
    protected boolean mEditable = true;

    /** 视频封面视图 */
    private CoverBar mCoverBar;
    /** 光标视图 */
    private CursorBar mCursorBar;

    // 持续时间 单位：秒
    protected float mDuration = 10;

    // 委托事件
    private EffectsTimelineViewDelegate mDelegate;

    // 特效分布集合
    private ArrayList<T> mEffectModeList = new ArrayList<T>(5);

    /**
     * EffectsTimelineViewDelegate 委托对象
     */
    public interface EffectsTimelineViewDelegate
    {
        public void onProgressCursorWillChaned();
        public void onProgressChaned(float progress);

        /**
         * 特效个数变化
         */
        public void onEffectNumChanged(int effectNum);
    }

    /** 颜色条 */
    private ColorBar mColorBar;

    /**
     * 颜色条
     */
    private class ColorBar extends View
    {

        Paint mPaint = new Paint();

        public ColorBar(Context context)
        {
            super(context);
        }

        @Override
        public void draw(Canvas canvas)
        {
            super.draw(canvas);

            // 设置背景颜色
            mPaint.setColor(TuSdkContext.getColor("lsq_alpha_white_99"));
            canvas.drawRect(0, PADDING_TOP, getWidth(), getHeight()-PADDING_BOTTOM , mPaint);

            if (mDuration == 0) return;

            for (int i = 0; i< mEffectModeList.size() ; i ++)
            {
                T magicModel = mEffectModeList.get(i);

                float left = (magicModel.getAtTimeRange().start / mDuration) * getWidth();
                float width = (magicModel.getAtTimeRange().end / mDuration) * getWidth();

                mPaint.setColor(magicModel.getLabelColor());

                canvas.drawRect(left, 0, width, getHeight(), mPaint);
            }

        }
    }


    /**
     * 封面视图
     */
    private class CoverBar extends View
    {
        /** 缩略图列表  */
        private List<Bitmap> mVideoThumbList;

        public CoverBar(Context context)
        {
            super(context);
        }

        /**
         * 设置显示的视频缩略图列表
         *
         * @param thumbList
         */
        public void drawThumbList(List<Bitmap> thumbList)
        {
            this.mVideoThumbList = new ArrayList<>(thumbList);
            invalidate();
        }

        /**
         * 绘制一张视频缩略图
         * @param bitmap
         */
        public void drawVideoThumb(Bitmap bitmap)
        {
            if (this.mVideoThumbList == null)
                this.mVideoThumbList = new ArrayList<>(5);

            this.mVideoThumbList.add(bitmap);
            invalidate();
        }

        /**
         * 清空视频缩略图
         */
        public void clearVideoThumbList()
        {
            if(mVideoThumbList != null)
            {
                for (Bitmap bitmap : mVideoThumbList)
                    BitmapHelper.recycled(bitmap);

                mVideoThumbList.clear();
                mVideoThumbList = null;
            }
        }

        @Override
        public void draw(Canvas canvas) {
            super.draw(canvas);


            /**Draw Bitmap */
            if(mVideoThumbList!=null)
            {
                int size = mVideoThumbList.size();
                for (int i = 0 ; i< size ; i++)
                {
                    Bitmap bitmap = mVideoThumbList.get(i);
                    if (bitmap == null) continue;

                    Rect rect = new Rect();
                    rect.left = i * getWidth() / size;
                    rect.right = rect.left + bitmap.getWidth();
                    rect.top = PADDING_TOP;
                    rect.bottom = rect.top + bitmap.getHeight() -PADDING_BOTTOM;

                    canvas.drawBitmap(bitmap, null, rect, null);
                }


            }
        }
    }

    /**
     * 光标视图
     */
    private class CursorBar extends View
    {
        private Paint mPaint = new Paint();
        // 光标宽度
        private float mBarWidth = TuSdkContext.dip2px(4);
        // 光标颜色
        private int mBarColor = TuSdkContext.getColor("lsq_seekbar_drag_color");
        // 当前进度
        private float mProgress = 0.0f;

        public CursorBar(Context context)
        {
            super(context);
            mPaint.setColor(mBarColor);
        }

        /**
         * 设置当前进度
         *
         * @param progress
         */
        public void setProgress(float progress)
        {
            this.mProgress = progress;

            this.invalidate();
        }

        @Override
        public void draw(Canvas canvas)
        {
            super.draw(canvas);

            float left = mProgress * this.getWidth();

            // int left, int top, int right, int bottom
            RectF rectF = new RectF(left, 0, left + mBarWidth, getHeight());
            canvas.drawRoundRect(rectF,5,5,mPaint);

        }

        @Override
        public boolean onTouchEvent(MotionEvent event)
        {

            switch (event.getAction())
            {
                case MotionEvent.ACTION_DOWN:
                {
                    if (mDelegate != null)
                        mDelegate.onProgressCursorWillChaned();

                }
                    break;
                case MotionEvent.ACTION_MOVE:
                {
                    float progerss = event.getX() / getWidth();
                    this.setProgress(progerss);

                }
                    break;
                case MotionEvent.ACTION_UP:
                {
                    if (mDelegate != null)
                        mDelegate.onProgressChaned(this.mProgress);
                }
                    break;
            }

            return true;
        }
    }

    /**
     * 初始化 SceneEffectsTimelineView
     * @param context
     * @param attrs
     */
    public EffectsTimelineView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);


        // 视频封面
        LayoutParams lp =  new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        lp.gravity = Gravity.CENTER_VERTICAL;
        lp.topMargin = TuSdkContext.dip2px(6);
        lp.bottomMargin = TuSdkContext.dip2px(6);

        mCoverBar = new CoverBar(context);
        mCoverBar.setLayoutParams(lp);
        this.addView(mCoverBar);

        // 颜色条
        lp =  new SceneEffectsTimelineView.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        lp.gravity = Gravity.CENTER_VERTICAL;
        lp.topMargin = TuSdkContext.dip2px(4);
        lp.bottomMargin = TuSdkContext.dip2px(4);

        mColorBar = new ColorBar(context);
        mColorBar.setLayoutParams(lp);
        this.addView(mColorBar);


        // 光标
        lp =  new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        lp.gravity = Gravity.CENTER_VERTICAL;
        lp.topMargin = TuSdkContext.dip2px(2);
        lp.bottomMargin = TuSdkContext.dip2px(2);

        mCursorBar = new CursorBar(context);
        mCursorBar.setLayoutParams(lp);
        this.addView(mCursorBar);

    }

    /**
     * 设置显示的视频缩略图列表
     *
     * @param thumbList
     */
    public void drawVideoThumbList(List<Bitmap> thumbList)
    {
        mCoverBar.drawThumbList(thumbList);
    }

    /**
     * 绘制一张视频缩略图
     *
     * @param bitmap
     */
    public void drawVideoThumb(Bitmap bitmap)
    {
        mCoverBar.drawVideoThumb(bitmap);
    }

    /**
     * 清空视频缩略图
     */
    public void clearVideoThumbList()
    {
        mCoverBar.clearVideoThumbList();
    }

    /**
     * 设置委托对象
     *
     * @param delegate
     */
    public void setDelegate(EffectsTimelineViewDelegate delegate)
    {
        this.mDelegate = delegate;
    }

    /**
     * 设置当前是否可以编辑
     *
     * @param editable
     */
    public void setEditable(boolean editable)
    {
        this.mEditable = editable;
    }

    /**
     * 设置进度
     *
     * @param progress (0.f - 1.f)
     */
    public void setProgress(float progress)
    {
        mCursorBar.setProgress(progress);
    }

    public void postColorBarInvalidate()
    {
        mColorBar.postInvalidate();
    }

    /**
     * 重置场景特效时间轴
     */
    public void clearSceneEffect()
    {
        this.mEffectModeList.clear();

        this.postColorBarInvalidate();
    }

    /**
     * 获取设置的最后一个场景特效信息
     *
     * @return MagicModel
     *
     */
    public T lastEffectMode()
    {
        if (this.mEffectModeList.size() == 0) return null;

        return this.mEffectModeList.get(this.mEffectModeList.size() - 1);
    }

    /**
     * 获取所有特效数据
     *
     * @return
     */
    public List<T> getAllMediaEffectData()
    {
        return mEffectModeList;
    }

    /**
     * 添加一个场景特效信息
     *
     * @param effectModel
     */
    public void addEffectMode(T effectModel)
    {
        if (!mEditable) return;

        this.mEffectModeList.add(effectModel);

        if (mDelegate != null)
            mDelegate.onEffectNumChanged(this.mEffectModeList.size());

        this.postColorBarInvalidate();
    }

    /**
     * 移除最后一个特效
     */
    public void removeLastEffectMode()
    {
        if (this.mEffectModeList.size() == 0) return ;

        this.mEffectModeList.remove(lastEffectMode());

        if (mDelegate != null)
            mDelegate.onEffectNumChanged(this.mEffectModeList.size());

        this.postColorBarInvalidate();
    }

    /**
     * 更新最后一个场景特效结束时间
     *
     * @param endTime
     */
    public void updateLastEffectModelEndTime(float endTime)
    {
        if (!mEditable) return;

        T sceneEffectModel = lastEffectMode();

        if (sceneEffectModel == null || endTime < sceneEffectModel.getAtTimeRange().end) return;

        sceneEffectModel.getAtTimeRange().end = endTime;

        this.postColorBarInvalidate();

    }

    /**
     * 设置持续时间 单位：秒
     *
     * @param duration
     */
    public void setDuration(float duration)
    {
        this.mDuration = duration;
        mColorBar.postInvalidate();
    }

    public static interface EffectModelInterface
    {
        public int getLabelColor();

        public TuSDKTimeRange getAtTimeRange();
    }
}
