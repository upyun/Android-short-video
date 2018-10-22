/**
 * TuSdkColorSelectorBar
 *
 * @author LiuHang
 * @Date 7/27/2017 9:50 AM
 * @Copright (c) 2017 tusdk.com. All rights reserved.
 */

package com.upyun.shortvideo.views;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by LiuHang on 7/27/2017.
 */

public class TuSdkColorSelectorBar extends View
{
    /**
     * 拖动改变颜色监听事件
     */
    public interface OnColorChangeListener
    {
        /**
         * @param color
         *          返回选择的颜色
         */
        void onSelectedColorChanged(String color);
    }

    /** 颜色集合 */
    private String[] mColorSeeds = new String[]{"#00000000", "#FFFFFF", "#CCCCCC", "#808080", "#404040", "#362F2D", "#000000",
            "#BE8145", "#800000", "#CC0000", "#FF0000", "#FF5500", "#FF8000", "#FFBF00", "#A8E000", "#BCBF00",
            "#008C00", "#80D4FF", "#0095FF", "#0066CC", "#001A66", "#3C0066", "#75008C", "#FF338F", "#FFBFD4"};
    
    // 标记颜色选择列表是否被选中，默认 ： false
    private boolean mIsSelected = false;
    private float pointDownX;
    private float pointDownY;
    
    /** 颜色列表的高度 */
    private float mColorBarHeight;
    /** 颜色指示器宽度 */
    private float mColorIndicatorWidth;
    /** 颜色指示器高度 */
    private float mColorIndicatorHeight;
    /** 颜色列表顶部 paddding */
    private float mColorBarPaddingTop;
    
    /** 颜色列表区域范围 */
    private RectF mColorListRectF;
    
    /** 拖动改变颜色监听事件 */
    private OnColorChangeListener mOnColorChangeListener;

    /** 拖动改变颜色监听事件 */
    public void setColorChangeListener(OnColorChangeListener onColorChangeListener)
    {
        this.mOnColorChangeListener = onColorChangeListener;
    }
    
    public TuSdkColorSelectorBar(Context context)
    {
        this(context, null);
    }

    public TuSdkColorSelectorBar(Context context, AttributeSet attrs)
    {
        this(context, attrs, 0);
    }

    public TuSdkColorSelectorBar(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
    }

    public void setColorBarHeight(float height)
    {
    	this.mColorBarHeight = height;
    }
    
    public void setColorIndicatorWidth(float width)
    {
    	this.mColorIndicatorWidth = width;
    }
    
    public void setColorIndicatorHeight(float height)
    {
    	this.mColorIndicatorHeight = height;
    }
    
    public void setColorBarPaddingTop(float padding)
    {
    	this.mColorBarPaddingTop = padding;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
    {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int viewHeight = (int) (getMeasuredHeight() + mColorIndicatorHeight + mColorBarPaddingTop);
        int viewWidth = getMeasuredWidth() + getPaddingLeft() + getPaddingRight();
        setMeasuredDimension(viewWidth, viewHeight);
    }

    @SuppressLint("DrawAllocation")
	@Override
    protected void onDraw(Canvas canvas)
    {
        super.onDraw(canvas);
        /** 画笔 */
        Paint mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setColor(Color.BLACK);
        // 绘制颜色列表
        drawColorListRect(mPaint,canvas);

        if (mIsSelected)
        {
            // 回调选中的颜色
            if(mOnColorChangeListener != null) mOnColorChangeListener.onSelectedColorChanged(getColor(pointDownX));
            // 绘制选中颜色指示器
            drawColorIndicatorRect(mPaint,canvas);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        switch (event.getAction())
        {
            case MotionEvent.ACTION_DOWN:
                pointDownX = event.getX();
                pointDownY = event.getY();
                validateTouchPoint(pointDownX, pointDownY);
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

    /**
     * 绘制颜色列表区域
     *
     * @param paint
     * @param canvas
     */
    private void drawColorListRect(Paint paint, Canvas canvas)
    {
        // 颜色列表绘制区域
        float startPointX = getPaddingLeft();
        float endPointX = getWidth() - getPaddingRight();
        float startPointY = mColorIndicatorHeight + mColorBarPaddingTop;
        float endPointY = mColorBarHeight + startPointY;

        mColorListRectF = new RectF(startPointX, startPointY, endPointX, endPointY);
        float colorCellWidth = mColorListRectF.width() / mColorSeeds.length;
        
        // 绘制颜色列表
        for (int i =1; i <= mColorSeeds.length; i++)
        {
        	if (i == 1)
            {
        		paint.setColor(Color.RED);
        		paint.setStrokeWidth(5);
        		paint.setStyle(Style.STROKE);
        		canvas.drawRect(startPointX + colorCellWidth * (i - 1), startPointY, startPointX + colorCellWidth * i, endPointY, paint);
        		canvas.drawLine(startPointX + colorCellWidth * (i - 1), startPointY, startPointX + colorCellWidth * i, endPointY, paint);
        		paint.setStyle(Style.FILL);
                continue;
            }
        	
            paint.setColor(Color.parseColor(mColorSeeds[i-1]));
            canvas.drawRect(startPointX + colorCellWidth * (i - 1), startPointY, startPointX + colorCellWidth * i, endPointY, paint);
        }
    }

    /**
     * 绘制颜色指示器
     *
     * @param paint
     * @param canvas
     */
    private void drawColorIndicatorRect(Paint paint, Canvas canvas)
    {
        paint.setColor(Color.parseColor(getColor(pointDownX)));
        float colorIndicatorStartX = getColorCellWidth() * getSelectedPosition(pointDownX) + getPaddingLeft() - getColorCellWidth() / 4f;
        float colorIndicatorEndX = colorIndicatorStartX + mColorIndicatorWidth;
        float colorIndicatorStartY = 0;
        float coloIndicatorEndY = mColorIndicatorHeight;
        RectF drawRect = new RectF(colorIndicatorStartX, colorIndicatorStartY , colorIndicatorEndX, coloIndicatorEndY);
        // 绘制颜色指示器矩形方块
        canvas.drawRect(drawRect,paint);
    }

    /**
     * 获取每个颜色单元格的宽度
     *
     * @return
     */
    private float getColorCellWidth()
    {
        if (mColorSeeds.length == 0) return 0;
        
        return mColorListRectF.width() / mColorSeeds.length;
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
        float distancX = pointX-getPaddingLeft();
        if (distancX < 0) return 0;
        int colorPosition = (int) (distancX / getColorCellWidth());
        if (colorPosition < 0) return 0;
        if (colorPosition >= mColorSeeds.length) return mColorSeeds.length-1;
        return colorPosition;
    }

    private void updateSeletedState(boolean isSelected)
    {
        if (isSelected)
            mIsSelected = true;
        else
            mIsSelected = false;

        postInvalidate();
    }

    /**
     * 验证触摸区域是否在有效区域（颜色列表范围内）
     * 如果在有效区域即重新绘制
     *
     * @param pointDownX
     * @param pointDownY
     */
    private void validateTouchPoint(float pointDownX, float pointDownY)
    {
        if (mColorListRectF.contains(pointDownX, pointDownY))
        {
            updateSeletedState(true);
        }
    }
}