package com.upyun.shortvideo.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RadialGradient;
import android.graphics.RectF;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.SeekBar;

import org.lasque.tusdk.core.TuSdkContext;

/**
 * Created by LiuHang on 7/27/2017.
 */

public class ColorSelector extends SeekBar
{
    /** 颜色集合 */
    private String[] mColorSeeds = new String[]{"#FFFFFF", "#CCCCCC", "#808080", "#404040", "#362F2D", "#000000",
            "#BE8145", "#800000", "#CC0000", "#FF0000", "#FF5500", "#FF8000", "#FFBF00", "#A8E000", "#BCBF00",
            "#008C00", "#80D4FF", "#0095FF", "#0066CC", "#001A66", "#3C0066", "#75008C", "#FF338F", "#FFBFD4"};
    private Context mContext;

    /** 获取View在屏幕中的坐标位置 */
    private RectF mLocationRect;
    // 标记颜色选择列表是否被选中，默认 ： false
    private boolean mIsSelected = false;
    private float pointDownX;
    private float pointDownY;
    /** 显示选中颜色方块宽度 */
    private int colorIndicatorWidth = 50;
    /** 颜色列表的高度 */
    private float mColorListHeight = 0;
    /** 颜色列表的宽度 */
    private float mColorListWidth = 0;
    /** 拖动块半径 */
    private float mDragBarRadius = 0;
    /** 颜色指示器宽度 */
    private float mColorIndicatorWidth = 0;
    /** 颜色指示器高度 */
    private float getmColorIndicatorHeight = 0;
    /** 颜色列表在屏幕中的坐标 */
    private RectF mColorListRect;
    /** 标记View是否第一次绘制,默认 : true */
    private boolean isFirstOnDraw = true;
    /** 颜色列表顶部拍Paddding */
    private float mColorListPaddingTop;

    private OnColorChangeListener mOnColorChangeListener;

    public void setColorChangeListener(OnColorChangeListener onColorChangeListener)
    {
        this.mOnColorChangeListener = onColorChangeListener;
    }

    public interface OnColorChangeListener
    {
        /**
         * @param color
         *          返回选择的颜色
         */
        void onColorChangeListener(int color);
    }
    public ColorSelector(Context context)
    {
        this(context, null, 0, 0);
    }

    public ColorSelector(Context context, AttributeSet attrs)
    {
        this(context, attrs, 0, 0);
    }

    public ColorSelector(Context context, AttributeSet attrs, int defStyleAttr)
    {
        this(context, attrs, defStyleAttr, 0);
    }

    public ColorSelector(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes)
    {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs, defStyleAttr, defStyleRes);
    }

    protected void init(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes)
    {
        applyStyle(context, attrs, defStyleAttr, defStyleRes);
    }

    protected void applyStyle(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes)
    {
        mContext = context;
        TypedArray typedArray = context.obtainStyledAttributes(attrs, com.upyun.shortvideo.R.styleable.ColorSelector, defStyleAttr, defStyleRes);
        mColorListHeight = typedArray.getDimension(com.upyun.shortvideo.R.styleable.ColorSelector_colorListHeight, TuSdkContext.dip2px(10));
        mColorListWidth = typedArray.getDimension(com.upyun.shortvideo.R.styleable.ColorSelector_colorListWidth, TuSdkContext.dip2px(290));
        mDragBarRadius = typedArray.getDimension(com.upyun.shortvideo.R.styleable.ColorSelector_dragBarRadius, TuSdkContext.dip2px(10));
        getmColorIndicatorHeight = typedArray.getDimension(com.upyun.shortvideo.R.styleable.ColorSelector_colorIndicatorHeight, TuSdkContext.dip2px(20));
        mColorIndicatorWidth = typedArray.getDimension(com.upyun.shortvideo.R.styleable.ColorSelector_colorIndicatorWidth, TuSdkContext.dip2px(20));
        mColorListPaddingTop = typedArray.getDimension(com.upyun.shortvideo.R.styleable.ColorSelector_colorListPaddingTop, TuSdkContext.dip2px(5));
        typedArray.recycle();
    }

    @Override
    protected void onDraw(Canvas canvas)
    {
        super.onDraw(canvas);
        Paint colorPaint = new Paint();
        colorPaint.setAntiAlias(true);
        colorPaint.setColor(Color.BLACK);

        // 获取View在屏幕中的坐标
        mLocationRect = new RectF(getPaddingLeft(),getTop(),getRight(),getBottom());
        int startPointX = getPaddingLeft();
        int startPointY = (int) (getmColorIndicatorHeight + mDragBarRadius-mColorListHeight/2+mColorListPaddingTop);
        // 绘制颜色列表
        for (int i =1; i <= mColorSeeds.length; i++)
        {
            colorPaint.setColor(Color.parseColor(mColorSeeds[i-1]));
            canvas.drawRect(startPointX,startPointY,getColorCellWidth() * i + startPointX, mColorListHeight + startPointY,colorPaint);
            startPointX = (int) (getPaddingLeft() + getColorCellWidth() * i);
        }
        // 颜色选择列表在屏幕中的坐标位置
        mColorListRect = new RectF(mLocationRect.left+getPaddingLeft(), mLocationRect.top + startPointY,
                mLocationRect.right, mLocationRect.top + mColorListHeight + startPointY);

        // 颜色列表选中时，列表上方显示一个矩形方块，显示当前选中颜色
        if (mIsSelected)
        {
            // 回调选中的颜色
            mOnColorChangeListener.onColorChangeListener(Color.parseColor(getColor(pointDownX)));

            colorPaint.setColor(Color.parseColor(getColor(pointDownX)));
            float colorIndicatorStartX = getColorCellWidth()*getSelectedPosition(pointDownX) + getPaddingLeft();
            float colorIndicatorEndX = colorIndicatorStartX + mColorIndicatorWidth;
            float colorIndicatorStartY = 0;
            float coloIndicatorEndY = getmColorIndicatorHeight;
            RectF drawRect = new RectF(colorIndicatorStartX, colorIndicatorStartY , colorIndicatorEndX, coloIndicatorEndY);
            // 绘制颜色指示器矩形方块
            canvas.drawRect(drawRect,colorPaint);

            // 绘制圆形拖动块
            colorPaint.setColor(Color.BLUE);
            colorPaint.setStrokeWidth(2);
            float dragCircleCenterX = (float) (getColorCellWidth()*(getSelectedPosition(pointDownX) + 0.5)  + mLocationRect.left);
            float dragCircleCenterY = startPointY + mColorListHeight/2;
            RadialGradient gradient = new RadialGradient(dragCircleCenterX, dragCircleCenterY, mDragBarRadius,
                    new int[] {Color.WHITE, Color.BLUE}, new float[] { 0.8f, 1.0f }, Shader.TileMode.CLAMP);
            colorPaint.setShader(gradient);

            canvas.drawCircle(dragCircleCenterX, dragCircleCenterY, mDragBarRadius, colorPaint);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
    {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int viewHeight = (int) (mDragBarRadius*2+getmColorIndicatorHeight+mColorListPaddingTop);
        int viewWidth = (int) mColorListWidth+getPaddingLeft()+getPaddingRight();
        setMeasuredDimension(viewWidth, viewHeight);
    }

    /**
     * 获取每个颜色单元格的宽度
     *
     * @return
     */
    private float getColorCellWidth()
    {
        if (mColorSeeds.length == 0) return 0;
        return mColorListWidth / mColorSeeds.length;
    }

    /**
     * 获取被选中的颜色
     *
     * @param pointX
     * @return
     */
    protected String getColor(float pointX)
    {
        return mColorSeeds[getSelectedPosition(pointX)];
    }

    /**
     * 获取选中颜色单元格的位置
     *
     * @param pointX
     * @return
     */
    private int getSelectedPosition(float pointX)
    {
        // 点击位置距离Vie左侧距离
        float distancX = pointX-mLocationRect.left;
        if (distancX < 0) return 0;
        int colorPosition = (int) (distancX / getColorCellWidth());
        if (colorPosition < 0) return 0;
        if (colorPosition >= mColorSeeds.length) return mColorSeeds.length-1;
        return colorPosition;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        switch (event.getAction())
        {
            case MotionEvent.ACTION_DOWN:
                pointDownX = event.getX();
                pointDownY = event.getY();
                if (mLocationRect.contains((int) pointDownX, (int) pointDownY))
                {
                    updateSeletedState(true);
                }
                break;
            case MotionEvent.ACTION_MOVE:
                pointDownX = event.getX();
                pointDownY = event.getY();
                updateSeletedState(true);
                break;
            case MotionEvent.ACTION_UP:
                updateSeletedState(false);
                break;
        }
        return true;
    }

    private void updateSeletedState(boolean isSelected)
    {
        if (isSelected)
            mIsSelected = true;
        else
            mIsSelected = false;

        postInvalidate();
    }
}