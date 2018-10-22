package com.upyun.shortvideo.views;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.DecelerateInterpolator;

import java.util.ArrayList;
import java.util.List;

/**
 * MovieRangeScrollerBar
 *
 * @author MirsFang
 * @Date 2018/8/14 16:23
 * @Copright (c) 2018 tusdk.com. All rights reserved.
 */
public class MovieRangScrollerBar extends View {
    private static final String TAG = "MovieRangScrollerBar";
    //小块的画笔
    private Paint mBlockPaint;
    //游标的画笔
    private Paint mCursorPaint;
    //当前View的宽高
    private int mViewWidth, mViewHeight;
    //当前块的长度 px
    private float mBlockWidth = 80;
    //当前块的 offset;
    private float mBlockOffset = 0;
    private RectF mBlockRectF;
    //差值动画
    private ValueAnimator mAnimator;
    //选取改动回调
    private OnTimeRangChangedListener mTimeRangChangedListener;
    //封面图
    private List<Bitmap> mBitmapList;
    // 设置滑块的宽度百分比
    private float diffValue;
    //游标的宽度
    private float mCursorWidth = 20;
    private RectF mCursorRectF = new RectF();
    //最后点击的点
    private float mLastX, mLastY;
    //是否滑块移动
    private boolean mBlockingMoving;


    /** 选区改动回调 **/
    public interface OnTimeRangChangedListener {
        /**
         * 选块改动回调
         *
         * @param leftPercent
         * @param rightPercent
         */
        void onTimeRangChanged(float leftPercent, float rightPercent);
    }

    public MovieRangScrollerBar(Context context) {
        super(context);
        _init(context);
    }

    public MovieRangScrollerBar(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        _init(context);
    }

    //初始化
    private void _init(Context context) {
        mBlockPaint = new Paint();
        mBlockPaint.setAntiAlias(true);
        mBlockPaint.setColor(Color.WHITE);
        mBlockPaint.setAlpha(180);

        mCursorPaint = new Paint();
        mCursorPaint.setAntiAlias(true);
        mCursorPaint.setColor(Color.YELLOW);

        setClickable(true);
    }


    /** 设置滑块时长占总时长的百分比 */
    public void setBlockRang(float blockPercent) {
        diffValue = blockPercent;
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        mViewWidth = MeasureSpec.getSize(widthMeasureSpec);
        mViewHeight = MeasureSpec.getSize(heightMeasureSpec);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //绘制封面
        drawCover(canvas);
        //绘制滑块
        drawBlock(canvas);
        //绘制游标
        drawCursor(canvas);

    }

    /** 绘制游标 */

    private void drawCursor(Canvas canvas) {
        canvas.drawRoundRect(mCursorRectF, 5f, 5f, mCursorPaint);
    }

    /** 同步播放Seek  percent 当前播放百分比 */

    public void seek(float percent) {
        mCursorRectF.top = 0;
        mCursorRectF.bottom = mViewHeight;
        mCursorRectF.left = (mViewWidth * percent) - (mCursorWidth / 2);
        mCursorRectF.right = mCursorRectF.left + mCursorWidth;
        postInvalidate();
    }

    private void drawCover(Canvas canvas) {
        if (mBitmapList != null) {
            int size = mBitmapList.size();
            for (int i = 0; i < size; i++) {
                Bitmap bitmap = mBitmapList.get(i);
                if (bitmap == null) continue;
                Rect rect = new Rect();
                rect.left = i * mViewWidth / size;
                rect.right = rect.left + mViewWidth / size;
                rect.top = 0;
                rect.bottom = mViewHeight;
                canvas.drawBitmap(bitmap, null, rect, null);
            }
        }
    }

    /**
     * 绘制选块
     *
     * @param canvas
     */
    private void drawBlock(Canvas canvas) {
        mBlockWidth = mViewWidth * diffValue;
        mBlockRectF = new RectF(mBlockOffset, 0, mBlockWidth + mBlockOffset, mViewHeight);
        canvas.drawRoundRect(mBlockRectF, 10f, 10f, mBlockPaint);
    }

    /**
     * 设置选取改动回调
     *
     * @param mTimeRangChangedListener
     */
    public void setTimeRangChangedListener(OnTimeRangChangedListener mTimeRangChangedListener) {
        this.mTimeRangChangedListener = mTimeRangChangedListener;
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                updateLastPoint(event);
                mBlockingMoving = containBlockRange(mLastX, mLastY);
                if (!mBlockingMoving) scrollerToPoint(mLastX);
                break;
            case MotionEvent.ACTION_MOVE:
                if (mBlockingMoving) {
                    float offset = event.getX() - (mBlockWidth / 2);
                    if ((offset + mBlockWidth) > mViewWidth)
                        mBlockOffset = mViewWidth - mBlockWidth;
                    else if (offset < 0) {
                        mBlockOffset = 0;
                    } else {
                        mBlockOffset = offset;
                    }
                    postInvalidate();
                }
                break;
            case MotionEvent.ACTION_UP:
                if (mBlockingMoving && mTimeRangChangedListener != null) {
                    float left = (float) (Math.round((mBlockRectF.left / mViewWidth) * 100)) / 100;
                    float right = (float) (Math.round((mBlockRectF.right / mViewWidth) * 100)) / 100;
                    mTimeRangChangedListener.onTimeRangChanged(left, right);
                }

                mBlockingMoving = false;
                break;
        }

        return super.onTouchEvent(event);
    }

    /** 平滑滑动到 */
    private void scrollerToPoint(float pointX) {
        if (mAnimator == null) {
            mAnimator = new ValueAnimator();
            mAnimator.setDuration(500);
            mAnimator.setInterpolator(new DecelerateInterpolator());
            mAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    float offset = (float) animation.getAnimatedValue();
                    mBlockOffset = offset;
                    postInvalidate();
                    if (animation.getAnimatedFraction() == 1 && mTimeRangChangedListener != null) {
                        float left = (float) (Math.round((mBlockRectF.left / mViewWidth) * 100)) / 100;
                        float right = (float) (Math.round((mBlockRectF.right / mViewWidth) * 100)) / 100;
                        mTimeRangChangedListener.onTimeRangChanged(left, right);
                    }
                }
            });
        }
        if (mAnimator.isRunning()) mAnimator.end();
        if (pointX + (mBlockWidth / 2) > mViewWidth) {
            pointX = mViewWidth - (mBlockWidth / 2);
        } else if (pointX - (mBlockWidth / 2) < 0) {
            pointX = (mBlockWidth / 2);
        }
        mAnimator.setFloatValues((mBlockOffset + (mBlockWidth / 2)), pointX - (mBlockWidth / 2));
        mAnimator.start();
    }

    /**
     * 是否在Block内
     *
     * @param mLastX
     * @param mLastY
     * @return
     */
    private boolean containBlockRange(float mLastX, float mLastY) {
        if (mBlockRectF == null) return false;
        return mBlockRectF.contains(mLastX, mLastY);
    }

    private void updateLastPoint(MotionEvent event) {
        mLastX = event.getX();
        mLastY = event.getY();
    }

    /** 获取封面图片列表 */
    public List<Bitmap> getVideoThumbList() {
        if (this.mBitmapList == null)
            this.mBitmapList = new ArrayList<>(5);

        return mBitmapList;
    }

    /**
     * 绘制
     *
     * @param list
     */
    public void drawVideoThumbList(List<Bitmap> list) {
        getVideoThumbList().clear();
        if (list != null && list.size() > 0) {
            getVideoThumbList().addAll(list);
        }
        invalidate();
    }

    public void drawVideoThumb(Bitmap bitmap) {
        this.getVideoThumbList().add(bitmap);
        invalidate();
    }

    /**
     * 回收Bitmap
     */
    public void clearVideoThumbList() {
        if (mBitmapList != null) {
            for (Bitmap bitmap : mBitmapList) {
                if (bitmap == null || bitmap.isRecycled()) return;
                bitmap.recycle();
            }
            mBitmapList.clear();
            mBitmapList = null;
        }
    }


}
