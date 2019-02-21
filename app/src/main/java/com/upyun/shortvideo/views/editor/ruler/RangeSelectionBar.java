package com.upyun.shortvideo.views.editor.ruler;

/**
 * Created by tutu-penggao on 2018/9/11.
 */

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Region;
import android.view.MotionEvent;
import android.view.View;

import com.upyun.shortvideo.views.editor.CUtils;
import com.upyun.shortvideo.R;


/**
 * 绘制Bar
 */
public class RangeSelectionBar extends View {

    /**
     * 缩略图画笔
     */
    private Paint mBitmapPaint;
    /**
     * 光标画笔
     */
    private Paint mPlayCursorPaint;
    /**
     * Bar画笔
     */
    private Paint mOutPaint;
    /**
     * 阴影画笔
     */
    private Paint mShadowPaint;
    /**
     * 左bar移动量
     */
    private int leftOffsetX = 0;
    /**
     * 右bar移动量
     */
    private int rightOffsetX = 0;
    /**
     * 左bar移动距离
     */
    public int leftBarX = 0;
    /**
     * 右bar移动距离
     */
    public int rightBarX = 0;
    /**
     * 阴影颜色
     */
    private int shadowColor = 0xAAAAAAAA;
    /**
     * 外框颜色
     */
    private int outColor = 0xFFFFFFFF;

    /**
     * View宽度
     */
    private int mRangeSelectionBarWidthMeasure;
    /**
     * View高度
     */
    private int mRangeSelectionBarHeightMeasure;
    /**
     * 播放光标矩形框
     */
    private RectF mPlayCursorRect = new RectF();
    /**
     * 播放光标矩形宽度
     */
    private int mPlayCursorOffsetW;

    RectF centerRectF = new RectF();
    // 绘制圆角矩形
    RectF roundRectF = new RectF();

    /**
     * 时间轴总长
     */
    private int mTotalWidth = 0;
    /**
     * Bar宽度
     */
    private int mBarWidth = CUtils.dip2px(12);
    /**
     * Bar高度
     */
    private int selectionBarHeight = 0;
    /**
     * 缩略图列表滑动距离 X方向
     */
    private int mScrollDX = 0;
    /**
     * 外边框高度
     */
    private int mOutLineWidth = CUtils.dip2px(2);

    private Context mContext;

    public int leftMargin;
    RectF mClipRectF = new RectF();
    RectF mShadowRectF = new RectF();
    Bitmap mBitmapLeft;
    Bitmap mBitmapRight;
    private int movedDistancePx = 0;
    private int mDownX = 0;
    private boolean mIsLeftBarClick = false;
    private boolean mIsRightBarClick = false;
    /** 是否显示 **/
    private boolean isShowBar = true;
    /** 最长绘制宽度 */
    private long mMaxDrawWidth = -1;
    /** 最短绘制宽度 */
    private long mMinDrawWidth = -1;


    private int mStartLineX = CUtils.dip2px(20);

    //2个bar之间的最小距离
    private int mTwoBarsMinDistance = 0;

    public RangeSelectionBar(Context context) {
        super(context);
        this.mContext = context;
        mOutPaint = new Paint();
        mOutPaint.setColor(outColor);

        mShadowPaint = new Paint();
        mShadowPaint.setColor(shadowColor);

        mBitmapPaint = new Paint();
        leftMargin = 0;
        mBitmapLeft = BitmapFactory.decodeResource(mContext.getResources(), R.drawable
                .edit_ic_arrow_left);
        mBitmapRight = BitmapFactory.decodeResource(mContext.getResources(), R.drawable
                .edit_ic_arrow_right);

    }

    public void setValue(int totalWidth, int height, int startLineX) {
        mTotalWidth = totalWidth;
        selectionBarHeight = height;
        this.mStartLineX = startLineX;
        postInvalidate();
    }

    /** 设置最大绘制宽度 **/
    public void setMaxDrawWidth(long maxDrawWidth){
        this.mMaxDrawWidth = maxDrawWidth;
    }

    /** 设置最小绘制宽度 **/
    public void setMinDrawWidth(long minDrawWidth){
        this.mMinDrawWidth = minDrawWidth;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        mRangeSelectionBarWidthMeasure = MeasureSpec.getSize(widthMeasureSpec);
        mRangeSelectionBarHeightMeasure = MeasureSpec.getSize(heightMeasureSpec);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    public void setLeftMargin(int leftMargin){
        this.leftMargin = leftMargin;
        postInvalidate();
    }

    public void setStartLineX(int startLineX){
        this.mStartLineX = startLineX - mBarWidth;
        postInvalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.save();
        if(!isShowBar)return;
        // 镂空中间视图
        centerRectF.left = mScrollDX + mBarWidth + leftOffsetX + leftBarX + leftMargin +
                mStartLineX;
        centerRectF.top = mOutLineWidth;
        centerRectF.right = mScrollDX + mTotalWidth + mBarWidth - rightOffsetX - rightBarX +
                leftMargin + mStartLineX;
        centerRectF.bottom = selectionBarHeight + mOutLineWidth;
        canvas.clipRect(centerRectF, Region.Op.DIFFERENCE);
        // 绘制圆角矩形
        roundRectF.left = mScrollDX + leftOffsetX + leftBarX + leftMargin + mStartLineX;
        roundRectF.top = 0;
        roundRectF.right = mScrollDX + mTotalWidth + mBarWidth * 2.0f - rightOffsetX - rightBarX
                + leftMargin + mStartLineX;
        roundRectF.bottom = selectionBarHeight + mOutLineWidth* 2.0f;
        canvas.drawRoundRect(roundRectF, 10, 10, mOutPaint);
        // 裁切
        mClipRectF.left = mScrollDX + leftOffsetX + leftBarX + leftMargin + mStartLineX;
        mClipRectF.top = 0;
        mClipRectF.right = mScrollDX + mTotalWidth + mBarWidth * 2.0f - rightOffsetX - rightBarX
                + leftMargin + mStartLineX;
        mClipRectF.bottom = selectionBarHeight+mOutLineWidth* 2.0f;
        canvas.clipRect(mClipRectF, Region.Op.DIFFERENCE);
        // 绘制阴影
        mShadowRectF.left = mScrollDX + mBarWidth + leftMargin + mStartLineX;
        mShadowRectF.top = mOutLineWidth;
        mShadowRectF.right = mScrollDX + mTotalWidth + mBarWidth + leftMargin + mStartLineX;
        mShadowRectF.bottom = selectionBarHeight+mOutLineWidth;
        canvas.drawRect(mShadowRectF, mShadowPaint);
        canvas.restore();

        // 绘制图片
        canvas.save();
        canvas.drawBitmap(mBitmapLeft,
                mScrollDX + leftOffsetX + leftBarX + mBarWidth / 2.0f - mBitmapLeft.getWidth() /
                        2.0f
                        + leftMargin + mStartLineX,
                selectionBarHeight / 2.0f - mBitmapLeft.getHeight() / 2.0f + mOutLineWidth,
                mBitmapPaint);
        canvas.drawBitmap(mBitmapRight,
                mScrollDX + mTotalWidth + mBarWidth - rightOffsetX - rightBarX + mBarWidth / 2.0f
                        - mBitmapRight.getWidth() / 2.0f + leftMargin + mStartLineX,
                selectionBarHeight / 2.0f - mBitmapRight.getHeight() / 2.0f + mOutLineWidth,
                mBitmapPaint);
        canvas.restore();

        if(isShowCursor)
            onDrawPlayPointer(canvas, mOutPaint);
    }

    private int cursorPos;
    private boolean isShowCursor = false;
    public void setCursorPos(int pos){
        this.cursorPos = pos;
        isShowCursor = true;
        invalidate();
    }

    /**
     * 画播放指针
     *
     * @param canvas
     * @param paint
     */
    private void onDrawPlayPointer(Canvas canvas, Paint paint) {
        canvas.save();
        paint.setStrokeWidth(6);
        canvas.drawLine(mBarWidth + cursorPos, 0,
                mBarWidth + cursorPos, CUtils.dip2px(32) + mOutLineWidth * 2.0f, paint);
        canvas.restore();
    }


    public void setDisatnce(int movedDistancePx) {
        this.movedDistancePx = movedDistancePx;
    }



    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int currentX = (int) event.getX();
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                if (currentX + movedDistancePx - mStartLineX > mScrollDX + leftBarX && currentX +
                        movedDistancePx - mStartLineX < mScrollDX + leftBarX + mBarWidth) {
                    mDownX = (int) event.getX();
                    mIsLeftBarClick = true;

                    return true;
                } else if (currentX + movedDistancePx - mStartLineX > mScrollDX + mTotalWidth +
                        mBarWidth -
                        rightBarX && currentX + movedDistancePx - mStartLineX < mScrollDX +
                        mTotalWidth +
                        mBarWidth * 2.0 - rightBarX) {
                    mDownX = (int) event.getX();
                    mIsRightBarClick = true;
                    return true;
                } else {
                    return false;
                }
            case MotionEvent.ACTION_MOVE:
                int leftOffset = 0,rightOffset = 0;
                if (mIsLeftBarClick)
                    leftOffset = currentX - mDownX;
                if (mIsRightBarClick)
                    rightOffset = mDownX - currentX;


                float left = mScrollDX + mBarWidth + leftOffset + leftBarX + leftMargin +
                        mStartLineX;
                float right = mScrollDX + mTotalWidth + mBarWidth - rightOffset - rightBarX +
                        leftMargin + mStartLineX;

                if(right- left >mMaxDrawWidth && mMaxDrawWidth != -1){
                    moOnBarMoveListener.onMaxValue();
                    invalidate();
                    return true;
                }

                if(right - left < mMinDrawWidth && mMinDrawWidth!= -1){
                    moOnBarMoveListener.onMinValue();
                    invalidate();
                    return true;
                }

                leftOffsetX = leftOffset;
                rightOffsetX = rightOffset;

//                TLog.e("leftOffsetX :%s    rightOffsetX :%s  centRect ：%s",leftOffsetX,rightOffsetX,centerRectF);
                // 判断是否超出界限
                if (leftBarX + leftOffsetX < 0) {
                    if (Math.abs(leftBarX) < Math.abs(leftOffsetX)) {
                        leftOffsetX = -leftBarX;
                    }
                }
                if (rightBarX + rightOffsetX < 0) {
                    if (Math.abs(rightBarX) < Math.abs(rightOffsetX)) {
                        rightOffsetX = -rightBarX;
                    }
                }
                if (leftBarX + leftOffsetX + rightBarX + rightOffsetX+mTwoBarsMinDistance > mTotalWidth) {
                    if (mIsLeftBarClick) {
                        leftOffsetX = mTotalWidth - (leftBarX + rightBarX + rightOffsetX+mTwoBarsMinDistance);
                    }
                    if (mIsRightBarClick) {
                        rightOffsetX = mTotalWidth - (leftBarX + rightBarX + leftOffsetX+mTwoBarsMinDistance);
                    }
                }
                if (moOnBarMoveListener != null) {
                    if (mIsLeftBarClick) {
                        moOnBarMoveListener.onMoveLeftBar(leftBarX + leftOffsetX);
                    }
                    if (mIsRightBarClick) {
                        moOnBarMoveListener.onMoveRightBar(rightBarX + rightOffsetX);
                    }
                    moOnBarMoveListener.onBarMove(leftBarX + leftOffsetX, rightBarX + rightOffsetX);
                }
                invalidate();

                return true;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                if (mIsLeftBarClick) {
                    if (leftBarX + leftOffsetX > 0) {
                        leftBarX = leftBarX + leftOffsetX;
                    } else {
                        leftBarX = 0;
                    }
                    if (mOnBarTouchUpListener != null){
                        mOnBarTouchUpListener.onLeftBarTouchUp(leftBarX);
                    }
                }
                if (mIsRightBarClick){
                    rightBarX = rightBarX + rightOffsetX;
                    if (mOnBarTouchUpListener != null){
                        mOnBarTouchUpListener.onRightBarTouchUp(rightBarX);
                    }
                }
                leftOffsetX = 0;
                rightOffsetX = 0;
                mIsLeftBarClick = false;
                mIsRightBarClick = false;
                return true;
        }
        return true;
    }


    private OnBarMoveListener moOnBarMoveListener;

    public void setOnBarMoveListener(OnBarMoveListener onBarMoveListener) {
        this.moOnBarMoveListener = onBarMoveListener;
    }

    public interface OnBarMoveListener {
        void onBarMove(int leftBarX, int rightBarX);
        void onMoveLeftBar(int leftBarX);
        void onMoveRightBar(int rightBarX);
        void onMaxValue();
        void onMinValue();
    }

    private OnBarTouchUpListener mOnBarTouchUpListener;
    public void setOnBarTouchUpListener(OnBarTouchUpListener onBarTouchUpListener) {
        this.mOnBarTouchUpListener = onBarTouchUpListener;
    }
    public interface OnBarTouchUpListener {
        void onLeftBarTouchUp(int leftBarX);
        void onRightBarTouchUp(int rightBarX);
    }


    public void setLeftBarPosition(float percent) {
        leftBarX = (int) (mTotalWidth * percent);
        invalidate();
    }

    public void setRightBarPosition(float percent) {
        rightBarX = (int) (mTotalWidth * (1 - percent));
        invalidate();
    }

    public void setShowBar(boolean isShow){
        this.isShowBar = isShow;
        postInvalidate();
    }

    /**
     * 设置左右bar的最小间距 默认0
     * @param twoBarsDistance
     */
    public void setTwoBarsMinDistance(int twoBarsDistance){
        mTwoBarsMinDistance = twoBarsDistance;
    }
}