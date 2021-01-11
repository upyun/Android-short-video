package org.lasque.tusdkvideodemo.views.editor.ruler;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Region;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Scroller;


import org.lasque.tusdkvideodemo.utils.NumberUtils;
import org.lasque.tusdkvideodemo.views.editor.CUtils;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * 视频缩略图显示控件
 */
public class HorizontalPlayViewOne extends View {

    /**
     * 所有图片的宽度
     */
    protected int mTotalBitmapWidth;
    /**
     * 总宽度
     */
    protected int mRectWidth;
    /**
     * 高度
     */
    protected int mRectHeight = CUtils.dip2px(32);

    public Scroller mScroller;
    /**
     * 图片集合
     */
    private volatile List<Bitmap> bitmapList = new ArrayList<>();
    //    private int subValue = CUtils.dip2px(40+10+10);
    private int subValue = CUtils.dip2px(40);

    //    private int startLineX = CUtils.dip2px(20 + 20);
    private int startLineX = CUtils.dip2px(12 + 12);
    public int mScreenWidth;

    /** 是否是倒序状态 **/
    public boolean isReversePlay = false;

    /**
     * 每一次位移的像素
     */
    private int mEachMovePx = 30;

    /**
     * 总共移动的距离 像素
     */
    public int movedDistancePx;

    /**
     * 画颜色矩形的paint
     */
    private Paint mColorRectPaint;
    /**
     * 阴影画笔
     */
    private Paint mShadowPaint;
    /**
     * 开始画颜色矩形的时候所处的位置X坐标
     */
    private int drawCurrentX;

    /**
     * 所画的颜色矩形的集合
     */
    private List<ColorRect> rectList = new ArrayList<>();

    /**
     * 颜色矩形 画笔集合
     */
    private LinkedList<Paint> colorPaintList = new LinkedList<>();


    /**
     * 初始化 是否需要 画颜色矩形
     */
    public boolean mNeedDrawColorRect = false;

    /**
     * 是否 开始 画颜色矩形
     */
    public boolean mBeginDrawColorRect = false;

    /**
     * 上次滚动后的  X坐标
     */
    protected int mScrollLastX;
    /**
     * 光标在最左边
     */
    private boolean isInLeft = true;

    /**
     * 光标在最右边
     */
    private boolean isInRight = false;
    /**
     * 控件移动的距离
     */
    private int selfMoveX = 0;

    /**
     * 控件自身上次移动的距离
     */
    private int lastSelfMoveX = 0;
    /**
     * 正在播放状态
     */
    private boolean isPlaying = false;

    public int leftMargin;

    /**
     * 画颜色矩形时 所需的颜色
     */
    public int mColorId = 0;

    /**
     * 控件是否可以移动
     */
    private boolean mCanMoved = false;
    /**
     * 是否需要画播放光标
     */
    private boolean mNeedDrawPointer = false;

    /**
     * 阴影颜色
     */
    private int shadowColor = 0xAAAAAAAA;


    /**
     * 封面选择外边框框宽度
     */
    private int mCoverBorderWidth = CUtils.dip2px(2);

    /**
     * 封面选择框宽度
     */
    private int mCoverRectWidth = 30;

    /**
     * 处于封面选择框状态
     */
    private boolean mIsCoverRect = false;

    /**
     * 封面矩形移动距离
     */
    private int mCoverRectMoveX = 0;

    private int mSelfPerMoveX = 0;
    private Paint mBitmapPaint;
    private Paint mPaintCoverOutBorder;
    private RectF mShadowRectF = new RectF();
    private RectF mCoverRect = new RectF();
    private RectF mCoverBorderRectF = new RectF();
    private long mTotalTimeUs;
    private long mTimerEachTime;

    private OnSizeChanedListener onSizeChanedListener;

    public void setOnSizeChanedListener(OnSizeChanedListener onSizeChanedListener){
        this.onSizeChanedListener = onSizeChanedListener;
    }

    /** 大小改变回调 **/
    public interface OnSizeChanedListener{
        void onSizeChanged(int totalWidth,int height);
        void onBitmapListSizeChanged(int count);
    }

    public HorizontalPlayViewOne(Context context) {
        super(context);
        getScreenWidth(context);
        init(null);
    }

    public HorizontalPlayViewOne(Context context, AttributeSet attrs) {
        super(context, attrs);
        getScreenWidth(context);
        init(attrs);
    }

    public HorizontalPlayViewOne(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        getScreenWidth(context);
        init(attrs);
    }

    protected void init(AttributeSet attrs) {
        // 获取自定义属性
        mScroller = new Scroller(getContext());
        // 缩略图画笔
        mBitmapPaint = new Paint();
        mBitmapPaint.setColor(Color.GRAY);
        // 抗锯齿
        mBitmapPaint.setAntiAlias(true);
        // 设定是否使用图像抖动处理，会使绘制出来的图片颜色更加平滑和饱满，图像更加清晰
        mBitmapPaint.setDither(true);
        // 空心
        mBitmapPaint.setStyle(Paint.Style.STROKE);
        // 文字居中
        mBitmapPaint.setTextAlign(Paint.Align.CENTER);
        mShadowPaint = new Paint();
        mPaintCoverOutBorder = new Paint();


    }

    protected void initVar() {
//        if(bitmapList.size() == 0)return;
        mPaintCoverOutBorder.setColor(mCoverOutBorderColor);
        if (mIsCoverRect) {
            subValue = 0;
            leftMargin = mCoverBorderWidth;
            mTotalBitmapWidth = mRectWidth  = mScreenWidth - subValue - mCoverBorderWidth * 2;
            if(onSizeChanedListener!=null)onSizeChanedListener.onSizeChanged(mTotalBitmapWidth,mRectHeight);
        } else {
            mTotalBitmapWidth = mRectWidth  = mScreenWidth - subValue;
            leftMargin = startLineX;
            if(onSizeChanedListener!=null)onSizeChanedListener.onSizeChanged(mTotalBitmapWidth,mRectHeight);
        }
        float eachTime = (mTotalTimeUs * mEachMovePx) / mTotalBitmapWidth;
        mTimerEachTime = Float.valueOf(eachTime).longValue();
    }

    private void getScreenWidth(Context context) {
        WindowManager windowManager = (WindowManager) context.getSystemService(Context
                .WINDOW_SERVICE);
        DisplayMetrics displaymetrics = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(displaymetrics);
        mScreenWidth = displaymetrics.widthPixels;
    }

    public void setInitValue(List<Bitmap> list, long totalTimeUs, int startLineX, int
            pointerColor, int coverOutBorder) {
        bitmapList = list;
        mTotalTimeUs = totalTimeUs;
        this.startLineX = startLineX;
        this.mPointerColor = pointerColor;
        this.mCoverOutBorderColor = coverOutBorder;
        initVar();
    }

    /**
     * 初始化控件显示内容
     *
     * @param needDrawColorRect 画颜色矩形
     * @param needDrawPointer   画播放指针
     * @param isCoverRect       封面选择框矩形
     * @param canMoved          控件可以移动
     */
    public void setDrawWhat(boolean needDrawColorRect, boolean needDrawPointer, boolean
            isCoverRect, boolean canMoved) {
        mNeedDrawColorRect = needDrawColorRect;
        mNeedDrawPointer = needDrawPointer;
        mIsCoverRect = isCoverRect;
        mCanMoved = canMoved;
        initVar();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int height = MeasureSpec.makeMeasureSpec(mRectHeight + mCoverBorderWidth * 2, MeasureSpec
                .AT_MOST);
        super.onMeasure(widthMeasureSpec, height);
    }

    public int getTotalBitmapWidth() {
        return mTotalBitmapWidth;
    }

    public int getBitmapHeight() {
        return mRectHeight;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //画缩略图
        onDrawBtimap(canvas, mBitmapPaint);

        if (mNeedDrawColorRect) {
            mColorRectPaint = new Paint();
            // 抗锯齿
            mColorRectPaint.setAntiAlias(true);
            // 设定是否使用图像抖动处理，会使绘制出来的图片颜色更加平滑和饱满，图像更加清晰
            mColorRectPaint.setDither(true);
            mColorRectPaint.setColor(Color.GRAY);
            drawColorRect(canvas);
        }

        if (mIsCoverRect) {
            mShadowPaint.setColor(shadowColor);
            drawCoverRect(canvas);
            //绘制阴影
            drawShadow(canvas, mShadowPaint);
            drawCoverRectOutBorder(canvas, mPaintCoverOutBorder);
        }
    }


    private void drawColorRect(Canvas canvas) {
        if (rectList.size() > 0) {
            for (int i = 0; i < rectList.size(); i++) {
                canvas.drawRect(rectList.get(i).rect, colorPaintList.get(i));
            }
        }
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
    }
    private int mBitmapCount = 20;
    public void setBitmapCount(int bitmapCount){
        mBitmapCount = bitmapCount;
    }

    /** 是否是倒序播放 **/
    public void setReversePlay(boolean isReversePlay){
        this.isReversePlay = isReversePlay;
    }

    /**
     * 画缩略图
     *
     * @param canvas
     * @param paint
     */
    private void onDrawBtimap(Canvas canvas, Paint paint) {
        if(bitmapList.size() == 0) return;
        RectF rect = new RectF();
        float bitmapWidth = mTotalBitmapWidth / mBitmapCount;
        for (int i = 0; i < (bitmapList.size() > mBitmapCount ? mBitmapCount : bitmapList.size()); i++) {
            rect.left = bitmapWidth * i + leftMargin;
            rect.top = CUtils.dip2px(2);
            rect.right = bitmapWidth * (i + 1) + leftMargin;
            rect.bottom = mRectHeight + mCoverBorderWidth;
            if(bitmapList.get(i) != null)
                canvas.drawBitmap(bitmapList.get(i), null, rect, paint);
        }
//        TLog.e("Draw Bitmap :%s",length);
    }


    /**
     * 绘制阴影
     *
     * @param canvas
     * @param paint
     */
    private void drawShadow(Canvas canvas, Paint paint) {
        mShadowRectF.left = leftMargin;
        mShadowRectF.top = 0;
        mShadowRectF.right = leftMargin + mRectWidth;
        mShadowRectF.bottom = mRectHeight+mCoverBorderWidth;
        canvas.drawRect(mShadowRectF, paint);
    }

    /**
     * 绘制封面矩形内部
     *
     * @param canvas
     */
    private void drawCoverRect(Canvas canvas) {
        mCoverRect.left = leftMargin + mCoverRectMoveX;
        mCoverRect.top = mCoverBorderWidth;
        mCoverRect.right = leftMargin + mCoverRectWidth + mCoverRectMoveX;
        mCoverRect.bottom = mRectHeight;
        canvas.clipRect(mCoverRect, Region.Op.DIFFERENCE);
    }

    public void setLeftMargin(int leftMargin){
        this.leftMargin = leftMargin;
        postInvalidate();
    }

    /**
     * 绘制封面矩形边框
     *
     * @param canvas
     * @param paintCover
     */
    private void drawCoverRectOutBorder(Canvas canvas, Paint paintCover) {
        mCoverBorderRectF.left = leftMargin - mCoverBorderWidth + mCoverRectMoveX;
        mCoverBorderRectF.top = 0;
        mCoverBorderRectF.right = leftMargin + mCoverRectWidth + mCoverBorderWidth +
                mCoverRectMoveX;
        mCoverBorderRectF.bottom = mRectHeight + mCoverBorderWidth;
        canvas.drawRect(mCoverBorderRectF, paintCover);
    }

    private int mPointerColor = Color.RED;
    private int mCoverOutBorderColor = Color.GREEN;


    private float mLastEndPercent = -1;
    /**
     * 开始画颜色矩形
     * @param colorId
     */
    public void startDrawColorRect(int colorId, float startPercent, float endPercent) {
        mColorId = colorId;
        mBeginDrawColorRect = true;
        drawCurrentX = movedDistancePx;
        if (drawCurrentX == mTotalBitmapWidth) {
            drawCurrentX = 0;
        }
        RectF rect = new RectF();
        if(NumberUtils.formatFloat2f(movedDistancePx/(float)mTotalBitmapWidth) > NumberUtils.formatFloat2f(endPercent)){
            rect.right = leftMargin + (int) (mTotalBitmapWidth * startPercent);
            rect.left = leftMargin  +  (int) (mTotalBitmapWidth * endPercent);
        }else {
            rect.left = leftMargin + (int) (mTotalBitmapWidth * startPercent);
            rect.right = leftMargin + (int) (mTotalBitmapWidth * endPercent);
        }
        rect.top = 0;
        rect.bottom = mRectHeight+mCoverBorderWidth;
        if (mLastEndPercent == -1){
            if(endPercent >= 1)return;
            mColorRectPaint.setColor(mColorId);
            colorPaintList.add(mColorRectPaint);
            rectList.add(new ColorRect(rectList.size() -1,rect));
            mLastEndPercent = endPercent;
        }else {
            if (rectList.size() <1) return;
            if(movedDistancePx/(float)mTotalBitmapWidth >= endPercent){
                if(isReversePlay) {
                    rectList.get(rectList.size() - 1).rect.left = leftMargin + (int) (mTotalBitmapWidth * endPercent);
                }else {
                    rectList.get(rectList.size() - 1).rect.right = leftMargin + (int) (mTotalBitmapWidth * endPercent);
                }
            }else {
                ColorRect rect1 = rectList.get(rectList.size()-1);
                float before = rect1.rect.right;
                float after = leftMargin + (int )(mTotalBitmapWidth * endPercent);
                if(isReversePlay){
                    if (after > before) {
                        rectList.get(rectList.size() - 1).rect.right = leftMargin +(int)(mTotalBitmapWidth * endPercent);
                    }
                }else {
                    if (after <= before) {
                        mColorRectPaint.setColor(mColorId);
                        colorPaintList.add(mColorRectPaint);
                        rectList.add(new ColorRect(rect1.index, rect));
                    } else {
                        rectList.get(rectList.size() - 1).rect.right = leftMargin + (int) (mTotalBitmapWidth * endPercent);
                    }
                }
            }
        }
        if (mOnColorRectListChangeListener != null) {
            mOnColorRectListChangeListener.OnColorRectListChange(rectList.size());
        }
        invalidate();
    }


    /**
     * 停止画颜色矩形
     */
    public void stopDrawColorRect() {
        mLastEndPercent = -1;
    }

    /**
     * 删除 最近一次画的颜色矩形
     */
    public void removeColorRect() {
        stopPlay();
        if (rectList.size() > 0) {
            int size = rectList.size() - 1;
            RectF rectRemove = rectList.get(size).rect;
            List<ColorRect> removeList = new ArrayList<>();
            removeList.add(rectList.get(size));

            for (ColorRect colorRect : rectList) {
                if(colorRect.index == rectList.get(size).index){
                    removeList.add(colorRect);
                }
            }

            for (ColorRect item : removeList) {
                scrollBy((int) (-movedDistancePx + (item.rect.left - leftMargin)), 0);
                if (mScrollListener != null) {
                    mScrollListener.onScaleScroll((int) (-movedDistancePx + (item.rect.left - leftMargin)));
                }
                movedDistancePx = (int) (movedDistancePx + (-movedDistancePx + (item.rect.left - leftMargin)));
                selfMoveX = movedDistancePx;
                if (selfMoveX > 0) {
                    isInLeft = false;
                }

                rectList.remove(item);

                if (mOnSelfScrollingListener != null) {
                    mOnSelfScrollingListener.onSelfScrolling(movedDistancePx,false);
                }
                if (mOnColorRectListChangeListener != null) {
                    mOnColorRectListChangeListener.OnColorRectListChange(rectList.size());
                }
                postInvalidate();
            }
            if(colorPaintList.size() > 0)
            colorPaintList.removeLast();

        }

    }

    private OnColorRectListChangeListener mOnColorRectListChangeListener;

    public interface OnColorRectListChangeListener {
        void OnColorRectListChange(int num);
    }

    /**
     * 设置颜色方块矩形个数改变监听
     *
     * @param onColorRectListChangeListener
     */
    public void setOnColorRectListChangeListener(OnColorRectListChangeListener
                                                         onColorRectListChangeListener) {
        this.mOnColorRectListChangeListener = onColorRectListChangeListener;
    }

    private OnIsTouchingListener mOnIsTouchingListener;

    public interface OnIsTouchingListener {
        void isTouching(boolean isTouching);
    }

    /**
     * 设置正在在触摸监听
     *
     * @param onIsTouchingListener
     */
    public void setOnIsTouchingListener(OnIsTouchingListener
                                               onIsTouchingListener) {
        this.mOnIsTouchingListener = onIsTouchingListener;
    }



    private OnDistanceChangeListener mScrollListener;

    public interface OnDistanceChangeListener {
        void onScaleScroll(int scale);
    }

    /**
     * 设置滑动距离改变监听接口
     *
     * @param onSlideListener
     */
    public void setOnDistanceChangeListener(OnDistanceChangeListener onSlideListener) {
        this.mScrollListener = onSlideListener;
    }


    private OnSelfScrollingListener mOnSelfScrollingListener;

    public interface OnSelfScrollingListener {
        void onSelfScrolling(int moveDistance,boolean isTouch);
    }

    /**
     * 设置控件滚动距离监听
     *
     * @param onSelfScrollingListener
     */
    public void setOnSelfScrollingListener(OnSelfScrollingListener onSelfScrollingListener) {
        this.mOnSelfScrollingListener = onSelfScrollingListener;

    }


    private OnCoverRectMoveListener mOnCoverRectMoveListener;

    public interface OnCoverRectMoveListener {
        void onCoverRectMove(int movex, int coverRectWidth);
    }

    /**
     * 设置封面选择视图 矩形移动监听
     *
     * @param onCoverRectMoveListener
     */
    public void setOnCoverRectMoveListener(OnCoverRectMoveListener onCoverRectMoveListener) {
        this.mOnCoverRectMoveListener = onCoverRectMoveListener;
    }


    private OnPlayPointerChangeListener mOnPlayPointerChangeListener;

    public interface OnPlayPointerChangeListener {
        void onPlayPointerChange(int moveDistance);
    }

    /**
     * 设置播放指针 位置改变监听
     *
     * @param onPlayPointerChangeListener
     */
    public void setOnPlayPointerChangeListener(OnPlayPointerChangeListener
                                                       onPlayPointerChangeListener) {
        this.mOnPlayPointerChangeListener = onPlayPointerChangeListener;
    }

    private OnStopPlayListener mOnStopPlayListener;

    public interface OnStopPlayListener {
        void stopPlay();
    }

    /**
     * 设置停止播放监听
     *
     * @param onStopPlayListener
     */
    public void setOnStopPlayListener(OnStopPlayListener onStopPlayListener) {
        this.mOnStopPlayListener = onStopPlayListener;
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int x = (int) event.getX();//获取触摸位置
        if (isPlaying) stopPlay();
        if(bitmapList.size() < mBitmapCount) return false;
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                if (mOnIsTouchingListener !=null){
                    mOnIsTouchingListener.isTouching(true);
                }
                if (mNeedDrawPointer) {
                    //有播放指针时
                    needDrawPointerDown(event);
                    return true;
                } else if (mIsCoverRect) {
                    isCoverRectDown(event);
                    return true;
                } else {
                    if (mCanMoved) {
                        if (mScroller != null && !mScroller.isFinished()) {
                            mScroller.abortAnimation();
                        }
                        mScrollLastX = x;
                        return true;
                    }
                }
            case MotionEvent.ACTION_MOVE:
                if (mNeedDrawPointer) {
                    needDrawPointerMove(event);
                    return false;
                } else if (mIsCoverRect) {
                    isCoverRectMove(event);
                    return true;
                } else {
                    if (mCanMoved) {
                        int dataX = mScrollLastX - x;
                        if (isInLeft) {
                            moveWhenInLeft(x, dataX);
                        } else {
                            if (isInRight) {
                                moveWhenInRight(x, dataX);
                            } else {
                                if (dataX <= 0) {
                                    lastSelfMoveX = selfMoveX;
                                    movedDistancePx = selfMoveX = lastSelfMoveX + dataX;
                                    if (selfMoveX < 0) {
                                        scrollBy(-lastSelfMoveX, 0);
                                        if (mScrollListener != null) {
                                            mScrollListener.onScaleScroll(-lastSelfMoveX);
                                        }
                                        movedDistancePx = selfMoveX = 0;
                                        isInLeft = true;
                                        isInRight = false;
                                        mScrollLastX = x;
                                    } else {
                                        scrollBy(dataX, 0);
                                        if (mScrollListener != null) {
                                            mScrollListener.onScaleScroll(dataX);
                                        }
                                        mScrollLastX = x;
                                    }
                                } else {
                                    movedDistancePx = selfMoveX = selfMoveX + dataX;
                                    if (selfMoveX > mTotalBitmapWidth) {
                                        scrollBy(dataX - (selfMoveX - mTotalBitmapWidth), 0);
                                        if (mScrollListener != null) {
                                            mScrollListener.onScaleScroll(dataX - (selfMoveX -
                                                    mTotalBitmapWidth));
                                        }
                                        movedDistancePx = selfMoveX = mTotalBitmapWidth;
                                        isInRight = true;
                                        isInLeft = false;
                                        mScrollLastX = x;
                                    } else {
                                        scrollBy(dataX, 0);
                                        if (mScrollListener != null) {
                                            mScrollListener.onScaleScroll(dataX);
                                        }
                                        mScrollLastX = x;
                                    }
                                }
                            }
                        }
                        if (mOnSelfScrollingListener != null) {
                            mOnSelfScrollingListener.onSelfScrolling(movedDistancePx,true);
                        }
                        invalidate();
                        return true;
                    }
                }
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                if (mOnIsTouchingListener !=null){
                    mOnIsTouchingListener.isTouching(false);
                }
                return false;
        }
        return false;
    }

    /**
     * inRight情况下的move事件
     * @param x
     * @param dataX
     */
    private void moveWhenInRight(int x, int dataX) {
        if (dataX < 0) {
            movedDistancePx = selfMoveX = selfMoveX + dataX;
            if (selfMoveX < 0) {
                scrollBy(dataX - selfMoveX, 0);
                if (mScrollListener != null) {
                    mScrollListener.onScaleScroll(dataX - selfMoveX);
                }
                isInLeft = true;
                isInRight = false;
                mScrollLastX = x;
            } else {
                isInRight = false;
                scrollBy(dataX, 0);
                if (mScrollListener != null) {
                    mScrollListener.onScaleScroll(dataX);
                }
                mScrollLastX = x;
            }
        }
    }

    /**
     * inleft情况下的move事件
     * @param x
     * @param dataX
     */
    private void moveWhenInLeft(int x, int dataX) {
        if (dataX <= 0) {
            movedDistancePx = 0;
        } else {
            movedDistancePx = selfMoveX = selfMoveX + dataX;
            scrollBy(dataX, 0);
            if (mScrollListener != null) {
                mScrollListener.onScaleScroll(dataX);
            }
            isInLeft = false;
            mScrollLastX = x;
        }
    }
    /**
     * 封面矩形状态的Move事件中调用
     * @param event
     */
    private void isCoverRectMove(MotionEvent event) {
        if ((int) event.getX() < leftMargin) {
            mCoverRectMoveX = 0;
        } else if ((int) event.getX() > mTotalBitmapWidth - mCoverRectWidth) {
            mCoverRectMoveX = mTotalBitmapWidth - mCoverRectWidth;
        } else {
            mCoverRectMoveX = (int) event.getX();
        }
        if (mOnCoverRectMoveListener != null) {
            mOnCoverRectMoveListener.onCoverRectMove(mCoverRectMoveX, mCoverRectWidth);
        }
        invalidate();
    }

    /**
     * 封面矩形状态的down事件中调用
     * @param event
     */
    private void isCoverRectDown(MotionEvent event) {
        if ((int) event.getX() < leftMargin) {
            movedDistancePx = 0;
        } else if ((int) event.getX() > mTotalBitmapWidth - mCoverRectWidth) {
            mCoverRectMoveX = mTotalBitmapWidth - mCoverRectWidth;
        } else {
            mCoverRectMoveX = (int) event.getX();
        }
        if (mOnCoverRectMoveListener != null) {
            mOnCoverRectMoveListener.onCoverRectMove(mCoverRectMoveX, mCoverRectWidth);
        }
        invalidate();
    }

    /**
     * 播放指针状态的Move事件中调用
     * @param event
     */
    private void needDrawPointerMove(MotionEvent event) {
        if ((int) event.getX() < leftMargin) {
            movedDistancePx = 0;
        } else if ((int) event.getX() > mTotalBitmapWidth) {
            movedDistancePx = mTotalBitmapWidth;
        } else {
            movedDistancePx = (int) event.getX();
        }
        if (mOnPlayPointerChangeListener != null) {
            mOnPlayPointerChangeListener.onPlayPointerChange(movedDistancePx);
        }
        invalidate();
    }



    /**
     * 播放指针状态的down事件中调用
     * @param event
     */
    private void needDrawPointerDown(MotionEvent event) {
        if ((int) event.getX() < leftMargin) {
            movedDistancePx = 0;
        } else if ((int) event.getX() > mTotalBitmapWidth) {
            movedDistancePx = mTotalBitmapWidth;
        } else {
            movedDistancePx = (int) event.getX();
        }
        if (mOnPlayPointerChangeListener != null) {
            mOnPlayPointerChangeListener.onPlayPointerChange(movedDistancePx);
        }
    }


    /**
     * 用户点击停止播放时，停止滚动
     */
    public void stopPlay() {
        isPlaying = false;
        mBeginDrawColorRect = false;
        if (mOnStopPlayListener != null) {
            mOnStopPlayListener.stopPlay();
        }
    }


    /**
     * 使用Scroller时需重写
     */
    @Override
    public void computeScroll() {
        super.computeScroll();
        // 判断Scroller是否执行完毕
        if (mScroller.computeScrollOffset()) {
            scrollTo(mScroller.getCurrX(), mScroller.getCurrY());
            // 通过重绘来不断调用computeScroll
            invalidate();
        }
    }

    /**
     * 移动到moveToTimeUs所在的位置
     * @param moveToTimeUs
     */
    public void moveToPercent(long moveToTimeUs) {
        float toPercent = (float) moveToTimeUs / mTotalTimeUs;
        moveToPosition(toPercent);
    }

    /**
     * 移动到percent位置
     * @param percent
     */
    public void moveToPercent(float percent) {
        moveToPosition(percent);
    }

    /**
     * 移动播放指针的方法
     * @param percent
     */
    public void pointerMoveToPercent(float percent) {
        movedDistancePx = (int) (percent * mTotalBitmapWidth);
        if (mOnPlayPointerChangeListener != null) {
            mOnPlayPointerChangeListener.onPlayPointerChange(movedDistancePx);
        }
        invalidate();
    }

    private void moveToPosition(float percent) {
        // 未加载完成限制seek
        if(bitmapList.size() < mBitmapCount) return;

        mSelfPerMoveX = (int) (percent * mTotalBitmapWidth) - movedDistancePx;
        selfMoveX = movedDistancePx = mSelfPerMoveX + movedDistancePx;
        isInLeft = false;
        isInRight = false;
        scrollBy(mSelfPerMoveX, 0);
        if (mScrollListener != null) {
            mScrollListener.onScaleScroll(mSelfPerMoveX);
        }
        if (mOnSelfScrollingListener != null) {
            mOnSelfScrollingListener.onSelfScrolling(movedDistancePx,false);
        }
        invalidate();
    }

    private class ColorRect{
        //下标(重复绘制颜色 根据下标删除)
        private int index;
        //当前的绘制的颜色区域
        private RectF rect;

        public ColorRect(int index, RectF rect) {
            this.index = index;
            this.rect = rect;
        }
    }

    public void addBitmap(Bitmap bitmap){
        if(bitmapList == null)return;
        if(bitmapList.size() >= mBitmapCount)return;
        bitmapList.add(bitmap);
        initVar();
        postInvalidate();
        if(onSizeChanedListener != null){
            onSizeChanedListener.onBitmapListSizeChanged(bitmapList.size());
        }
    }

    public void setTotalTimeUs(long totalTimeUs){
        this.mTotalTimeUs = totalTimeUs;
        initVar();
        postInvalidate();
    }
}
