/**
 * TuSDKVideoDemo
 * MovieRangeSelectionBar.java
 *
 * @author  leone.xia
 * @Date  Mar 8, 2017 6:05:15 PM
 * @Copyright: (c) 2017 tusdk.com. All rights reserved.
 *
 */
package org.lasque.tusdkvideodemo.views;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import org.lasque.tusdk.core.TuSdkContext;
import org.lasque.tusdk.core.utils.image.BitmapHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * 裁剪进度条控件
 * 
 * 说明：
 *     此控件是通过重写View来实现，控件显示的内容都是通过
 *     View类中的onDraw()方法来绘制的，特别注意内部坐标宽
 *     高等单位是像素(px),修改控件相关属性单位若是dp时需
 *     要将dp转换成px，变量seekbarHeight控制此控件的高
 *     度，相关坐标位置都是基于变量seekbarHeight来定位
 *     initUpadate()函数为初始化控件的相关属性，事件响应
 *     在onTouchEvent()函数中处理，以及控制是否需要刷新
 *     等操作。
 * 
 * @author leone.xia
 */

public class MovieRangeSelectionBar extends View 
{
	/** 左右光标最小间隔距离 */
    private final int MIN_LEFT_RIGHT_SPACE = 80;
	/** 设置左右光标间隔距离,默认设置为最小间隔距离，可在外部修改 */
	private int mCursorSpace = MIN_LEFT_RIGHT_SPACE;
	/** 设置最小开始滑动的滑动距离 */
    private final int MIN_SLIDE_SPACE = 6;
    /** 点击索引:1--点击左光标;2--点击右光标; 3--点击播放光标 */
    private final int CLICK_LEFT_CURSOR = 1;
    private final int CLICK_RIGHT_CURSOR = 2;
    private final int CLICK_PLAY_CURSOR = 3;

    /** 定义滑动方向  */
    private enum DIRECTION 
    {
        LEFT , RIGHT;
    }
    
    /** 分为剪辑和MV页面两种使用场合 */
    public enum Type
    {
    	Clip , MV
    }
    
    /** 缩略图列表  */
    private List<Bitmap> mVideoThumbList;
    /** 最新滑动的X坐标 */
	private float mLastX = 0;
	/** 记录左光标滑动的百分数 */
    private int mLeftPercent = 0;
    /** 记录右光标滑动的百分数 */
    private int mRightPercent = 100;
    /** 记录播放光标滑动的百分数 */
    private int mPlayPercent = 50;
    /** View宽度 */
    private int mRangeSelectionBarWidthMeasure;
    /** View高度 */
    private int mRangeSelectionBarHeightMeasure;
	/** MovieRangeSelectionBar WIDTH */
    private int mRangeSelectionBarWidth;
	/** MovieRangeSelectionBar HEIGHT */
    private int mRangeSelectionBarHeight;
	/** 进度条背景颜色 */
    private String mSeekBarColorBg = "#9fa0a0";
    /**当前进度条选择范围的颜色 */
	private String mSeekBarSelectColorBg = "#f4a11a";
	/** 当前播放点位置的填充色 */
	private String mPlayCursorColorBg = "#f4a11a";
    /** 蒙层的颜色**/
    private String mShadowColor = "#231815";
    /** 左光标移动指针ID */
    private int mLeftPointerID = -1;
    /** 右光标移动指针ID */
    private int mRightPointerID = -1;
    /** 播放光标移动指针ID */
    private int mPlayPointerID = -1;
    /** 最新左光标X坐标 */
    private float mLeftPointerLastX;
    /** 最新右光标X坐标 */
    private float mRightPointerLastX;
    /** 最新播放光标X坐标 */
    private float mPlayPointerLastX;
    /** 最新X坐标 */
    private float mPointerLastX;
    /** 最新X滑动距离 */
    private float mDeltaX;
    /** 裁剪进度条控件高度 */
    private int mSelectionBarHeight;
    /** 控件背景矩形框 */
    private RectF mSelectionBarRect;
    /** 控件选择矩形框 */
    private RectF mSelectionBarSelectedRect;
    /** 控件填充矩形框 */
    private RectF mSelectionBarFillRect;
    /** 左光标矩形框 */
    private RectF mLeftCursorRect;
    /** 右光标矩形框 */
    private RectF mRightCursorRect;
    /** 播放光标矩形框 */
    private RectF mPlayCursorRect;
    /** 左光标偏移量X,Y */
    private float mLeftCursorOffsetX = 0;
    /** 右光标偏移量X,Y */
    private float mRightCursorOffsetX = 0;
    /** 播放光标偏移量X,Y */
    private float mPlayCursorOffsetX = 0;
    /** 左光标矩形左边坐标 */
    private float mLeftCursorX = 0;
    /** 右光标矩形左边坐标 */
    private float mRightCursorX = 0;
    /** 播放光标矩形左边坐标 */
    private float mPlayCursoX = 0;
    /** 三角形坐标变量与大小属性 */
    private int mTglSize= 15;
    /** 左三角形顶点参考坐标 */
    private float mLeftTglVertexX= 0;
    private float mLeftTglVertexY= 0;
    /** 右三角形顶点参考坐标 */
    private float mRightTglVertexX= 0;
    private float mRightTglVertexY= 0;
    /** 设置光标的pod，用于扩大手势触碰光标的范围 */
    private final int LEFT_CURSOR_PODX = 80;
    private final int RIGHT_CURSOR_PODX = 80;
    private final int PLAY_CURSOR_PODX = 40;
    /** 填充偏移量X,Y */
    private int mFillOffsetY = 10;
    /** 播放光标矩形宽度 */
    private  int mPlayCursorOffsetW = 10;
    /** 左光标矩形宽度 */
    private  int mLeftCursorOffsetW = 40;
    /** 右光标矩形宽度 */
    private  int mRightCursorOffsetW = 40;
    /** 是否显示播放光标  */
    private boolean isShowPlayCursor;
    /** View绘制画笔 */
    private Paint mPaint;
    /** Bar背景绘制画笔 */
    private Paint mBarPaint;
    /** 左右光标监听接口 */
    private OnCursorChangeListener mListener;

    /** 缩略图的使用场合 */
    private Type mType;
    /** 绘制阴影蒙层效果 */
    private Paint mShadowPaint = new Paint();
    
    public MovieRangeSelectionBar(Context context)
    {
        this(context, null, 0);
    }

    public MovieRangeSelectionBar(Context context, AttributeSet attrs)
    {
        this(context, attrs, 0);
    }

    public MovieRangeSelectionBar(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
        initConfig();
    }
    
    private void initConfig()
    {
        setWillNotDraw(false);
        setFocusable(true);
        setClickable(true);
        
        initPaint();
        
        mLeftCursorRect = new RectF();
        mRightCursorRect = new RectF();

        mSelectionBarRect = new RectF();
        mSelectionBarSelectedRect = new RectF();
        mSelectionBarFillRect = new RectF();
        mPlayCursorRect = new RectF();
        
    }
    
    private void initPaint()
    {
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Style.FILL);

        mBarPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mBarPaint.setAntiAlias(true);
        mBarPaint.setStyle(Style.FILL);
        
        mShadowPaint.setAntiAlias(true);
        mShadowPaint.setColor(Color.parseColor(getShadowColor()));
        mShadowPaint.setAlpha(153);
    }

    public void setShadowColor(String colorId){
        mShadowColor = colorId;
        if(mShadowPaint !=null)
        {
            mShadowPaint.setColor(Color.parseColor(mShadowColor));
            postInvalidate();
        }
    }

    public String getShadowColor(){
        return mShadowColor;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        if (getParent() != null)
        {
            getParent().requestDisallowInterceptTouchEvent(true);
        }
        // For multiple touch
        final int action = event.getActionMasked();
        switch (action)
        {
        case MotionEvent.ACTION_DOWN:
        case MotionEvent.ACTION_POINTER_DOWN:        	
            handleTouchDown(event);
            break;

        case MotionEvent.ACTION_MOVE:
            handleTouchMove(event);

            break;
        case MotionEvent.ACTION_POINTER_UP:
        case MotionEvent.ACTION_CANCEL:
        case MotionEvent.ACTION_UP:
            handleTouchUp(event);
            break;
        }

        return super.onTouchEvent(event);
    }
    
    private void handleTouchDown(MotionEvent event)
    {
        final int actionIndex = (event.getAction() & MotionEvent.ACTION_POINTER_INDEX_MASK) >> MotionEvent.ACTION_POINTER_INDEX_SHIFT;
        final int downX = (int) event.getX(actionIndex);
        
    	mPointerLastX = downX;
        if (downX+LEFT_CURSOR_PODX >= mLeftCursorRect.left
        		&&downX-LEFT_CURSOR_PODX <= mLeftCursorRect.right)
        {
            mLeftPointerLastX = mPointerLastX;
            mLeftPointerID = event.getPointerId(actionIndex);
        }
        
        if (downX+RIGHT_CURSOR_PODX >= mRightCursorRect.left
        		&&downX-RIGHT_CURSOR_PODX <= mRightCursorRect.right)
        {
            mRightPointerLastX = mPointerLastX;
            mRightPointerID = event.getPointerId(actionIndex);

        } 
        
        if (downX+PLAY_CURSOR_PODX >= mPlayCursorRect.left
        		&&downX-PLAY_CURSOR_PODX <= mPlayCursorRect.right)
        {
            mPlayPointerLastX = mPointerLastX;
            mPlayPointerID = event.getPointerId(actionIndex);

        }

    }

    private void handleTouchMove(MotionEvent event)
    {
    	
        final int actionIndex = (event.getAction() & MotionEvent.ACTION_POINTER_INDEX_MASK) >> MotionEvent.ACTION_POINTER_INDEX_SHIFT;
        final float x = event.getX(actionIndex);
        mDeltaX = x - mPointerLastX;
        /** 
         * 设置只有滑动距离大于MIN_SLIDE_SPACE时，才开始更新ui坐标，
         * 这样做的目的时减少在滑动 光标的过程中，过多的刷新此控件的
         * UI画面
         **/
        int distanceX = (int) Math.abs(mDeltaX);
        if (distanceX < MIN_SLIDE_SPACE)return;
        
        mPointerLastX = (int) x;
        
        if (mLeftPointerID != -1)
        {
            DIRECTION direction = (mDeltaX < 0 ? DIRECTION.LEFT
                    : DIRECTION.RIGHT);
            if (mLeftCursorRect.left < mSelectionBarRect.left
            		&& mLeftCursorRect.right > mSelectionBarRect.right)
            {
                return;
            }
            mCursorSpace = (mCursorSpace > MIN_LEFT_RIGHT_SPACE) ? mCursorSpace : MIN_LEFT_RIGHT_SPACE;
            if ((direction == DIRECTION.RIGHT)
            		&& mLeftCursorRect.right +mCursorSpace > mRightCursorRect.left)
            {
                return;
            }
            mLeftPointerLastX = mPointerLastX;
            if (mLeftPointerLastX+LEFT_CURSOR_PODX > mLeftCursorRect.left
            		&& mLeftPointerLastX < mLeftCursorRect.right + LEFT_CURSOR_PODX)
            {
            	mLeftCursorOffsetX = mDeltaX ;
            	mLeftCursorX = mLeftCursorRect.left;
            	mLastX = mLeftCursorX+mLeftCursorOffsetX;
            	if (mLeftCursorRect.left+mLeftCursorOffsetX <= mSelectionBarRect.left)
            	{
            		mLeftCursorOffsetX =0;
            		mLeftCursorX  = mSelectionBarRect.left;
            		mLastX = 0;
            	}
              	if (mLeftCursorRect.left+mLeftCursorOffsetX >= mSelectionBarRect.right - mLeftCursorOffsetW)
              	{
              		mLeftCursorOffsetX = 0;
              		mLeftCursorX  = mSelectionBarRect.right - mLeftCursorOffsetW;
              		mLastX = mRangeSelectionBarWidth;
            	}
              	mLeftPercent = (int) (100 * mLastX / mRangeSelectionBarWidth);
              	trggleLeftCallback(mLeftPercent);
            }        	
        }

        if (mRightPointerID != -1) {
            DIRECTION direction = (mDeltaX < 0 ? DIRECTION.LEFT
                    : DIRECTION.RIGHT);
            if (mRightCursorRect.left < mLeftCursorRect.right
            		||mRightCursorRect.right > mSelectionBarRect.right)
            {
            	return;
            }
            mCursorSpace = (mCursorSpace > MIN_LEFT_RIGHT_SPACE) ? mCursorSpace : MIN_LEFT_RIGHT_SPACE;
            if ((direction == DIRECTION.LEFT) 
            		&& mRightCursorRect.left < mLeftCursorRect.right + mCursorSpace)
            {
                return;
            }
            mRightPointerLastX = mPointerLastX;
            if (mRightPointerLastX+RIGHT_CURSOR_PODX > mRightCursorRect.left
            		&& mRightPointerLastX < mRightCursorRect.right + RIGHT_CURSOR_PODX)
            {
            	mRightCursorOffsetX = mDeltaX ;
            	mRightCursorX = mRightCursorRect.left;
            	mLastX = mRightCursorX + mRightCursorOffsetX + mRightCursorRect.right - mRightCursorRect.left;
            	if (mRightCursorRect.left + mRightCursorOffsetX <= mLeftCursorRect.right)
            	{
            		mRightCursorOffsetX = 0;
            		mRightCursorX  = mLeftCursorRect.right;
            		mLastX = 0;
            	}
              	if (mRightCursorRect.left + mRightCursorOffsetX >= mSelectionBarRect.right - mRightCursorOffsetW)
              	{
              		mRightCursorOffsetX = 0;
              		mRightCursorX  = mSelectionBarRect.right - mRightCursorOffsetW;
              		mLastX = mRangeSelectionBarWidth;
            	}
              	mRightPercent = (int) (100 * mLastX  / mRangeSelectionBarWidth);
              	trggleRightCallback(mRightPercent);
            }
       	
        }

        if (mPlayPointerID != -1) {
            if (mPlayCursorRect.left < mSelectionBarRect.left
            		&&mPlayCursorRect.right > mSelectionBarRect.right)
            {
                return;
            }
            mPlayPointerLastX = mPointerLastX;
            if (mPlayPointerLastX + PLAY_CURSOR_PODX > mPlayCursorRect.left
            		&& mPlayPointerLastX < mPlayCursorRect.right + PLAY_CURSOR_PODX)
            {
            	mPlayCursorOffsetX = mDeltaX ;
            	mPlayCursoX = mPlayCursorRect.left;
            	mLastX = mPlayCursoX+mPlayCursorOffsetX;
            	if (mPlayCursorRect.left+mPlayCursorOffsetX <= mSelectionBarRect.left)
            	{
            		mPlayCursorOffsetX = 0;
            		mPlayCursoX  = mSelectionBarRect.left;
            		mLastX = 0;
            	}
              	if (mPlayCursorRect.left+mPlayCursorOffsetX >= mSelectionBarRect.right - mPlayCursorOffsetW)
              	{
            		mPlayCursorOffsetX = 0;
            		mPlayCursoX  = mSelectionBarRect.right - mPlayCursorOffsetW;
            		mLastX = mRangeSelectionBarWidth;
            	}

              	mPlayPercent = (int) (100 * mLastX / mRangeSelectionBarWidth);
              	trgglePlayCallback(mPlayPercent);
            }        	
        }
        
        update();
    }
    
    private void handleTouchUp(MotionEvent event)
    {
        final int actionIndex = (event.getAction() & MotionEvent.ACTION_POINTER_INDEX_MASK) >> MotionEvent.ACTION_POINTER_INDEX_SHIFT;
        final int actionID = event.getPointerId(actionIndex);
        if (actionID == mLeftPointerID) 
        {
        	update();
            mLeftPointerID = -1;
            if (mListener == null) return;
            mListener.onLeftCursorUp(); 
        }
        
        if (actionID == mRightPointerID)
        {
        	update();
            mRightPointerID = -1;
            if (mListener == null) return;
            mListener.onRightCursorUp(); 
        }
        
        if (actionID == mPlayPointerID)
        {
            mPlayPointerID = -1;
        }
  
    }
    
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
    {
        mRangeSelectionBarWidthMeasure = MeasureSpec.getSize(widthMeasureSpec);
        mRangeSelectionBarHeightMeasure = MeasureSpec.getSize(heightMeasureSpec);


        mRangeSelectionBarWidth = ((int) (mSelectionBarRect.right - mSelectionBarRect.left));
        mRangeSelectionBarHeight = ((int) (mSelectionBarRect.top - mSelectionBarRect.bottom)); 
        if (mListener != null)
        {
        	mListener.onSeeekBarChanged(mRangeSelectionBarWidth, mRangeSelectionBarHeight);
        }
        

        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        initUpdate();
    }
    
    private void trggleLeftCallback(int percent)
    {
    	if (mListener == null) return;

        mListener.onLeftCursorChanged(percent); 
    }
    
    private void trggleRightCallback(int percent)
    {
        if (mListener == null) return;
        
        mListener.onRightCursorChanged(percent); 
    }
    
    private void trgglePlayCallback(int percent)
    {
        if (mListener == null) return;

        mListener.onPlayCursorChanged(percent);
    }

    public void update()
    {
    	if(mDeltaX == 0) return;

        if (mPlayPointerID != -1)
        {
        	mPlayCursorRect.left = mPlayCursoX + mPlayCursorOffsetX;
        	mPlayCursorRect.right = mPlayCursorRect.left + mPlayCursorOffsetW;    
        }
        if (mLeftPointerID != -1) 
        {
    		mLeftCursorRect.left = mLeftCursorX + mLeftCursorOffsetX;
        	mLeftCursorRect.right = mLeftCursorRect.left + mLeftCursorOffsetW ;
        	
            mLeftTglVertexX = mLeftCursorRect.left + mTglSize;
            mLeftTglVertexY = mSelectionBarRect.top + mSelectionBarHeight / 2 - mTglSize;
        }
        if (mRightPointerID != -1)
        {
        	if (mRightCursorX == 0)
        	{
        		mRightCursorX = mRightCursorRect.left;
        	}
    		mRightCursorRect.left = mRightCursorX + mRightCursorOffsetX;
    		mRightCursorRect.right = mRightCursorRect.left + mRightCursorOffsetW;     
    		
            mRightTglVertexX = mRightCursorRect.right - mTglSize;
            mRightTglVertexY = mSelectionBarRect.top + mSelectionBarHeight / 2 - mTglSize;
        }

    	
        mSelectionBarRect.left = 0;
        mSelectionBarRect.right = mRangeSelectionBarWidthMeasure + mSelectionBarRect.left;
        mSelectionBarRect.top = 0;
        mSelectionBarRect.bottom = mSelectionBarRect.top + mSelectionBarHeight;
        
        mSelectionBarSelectedRect.left = 0;
        mSelectionBarSelectedRect.right = getWidth();
        mSelectionBarSelectedRect.top = mSelectionBarRect.top;
        mSelectionBarSelectedRect.bottom = mSelectionBarRect.bottom;
        
        mSelectionBarFillRect.left = mLeftCursorRect.right;
        mSelectionBarFillRect.right = mRightCursorRect.left;
        mSelectionBarFillRect.top = mSelectionBarRect.top+mFillOffsetY;
        mSelectionBarFillRect.bottom = mSelectionBarFillRect.top + mSelectionBarHeight-2*mFillOffsetY;
        
        postInvalidate();
    }
    
    private void initUpdate()
    {
    	
    	mSelectionBarHeight = getHeight() > 0 ? getHeight() : TuSdkContext.dip2px(65);
    	mSelectionBarHeight = mRangeSelectionBarHeightMeasure;
    	mPlayCursorOffsetW = TuSdkContext.dip2px(3);
    	mLeftCursorOffsetW = TuSdkContext.dip2px(20);
    	mRightCursorOffsetW = TuSdkContext.dip2px(20);
    	mTglSize = TuSdkContext.dip2px(7);
    	mFillOffsetY = TuSdkContext.dip2px(4);
    	
    	mPlayCursorRect.left = mPlayPercent*mRangeSelectionBarWidth/100;
    	mPlayCursorRect.right = mPlayCursorRect.left + mPlayCursorOffsetW; 
        mPlayCursorRect.top = mSelectionBarRect.top;
        mPlayCursorRect.bottom = mSelectionBarRect.bottom;
      
		mLeftCursorRect.left = mLeftPercent*mRangeSelectionBarWidth/100;
    	mLeftCursorRect.right = mLeftCursorRect.left + mLeftCursorOffsetW ;
        mLeftCursorRect.top = mSelectionBarRect.top;
        mLeftCursorRect.bottom = mSelectionBarRect.bottom;   
        
        mLeftTglVertexX = mLeftCursorRect.left+mTglSize;
        mLeftTglVertexY = mSelectionBarRect.top+mSelectionBarHeight/2-mTglSize;
        
    	mRightCursorRect.right = mRightPercent*mRangeSelectionBarWidth/100;     
		mRightCursorRect.left = mRightCursorRect.right - mRightCursorOffsetW;
        mRightCursorRect.top = mSelectionBarRect.top;
        mRightCursorRect.bottom = mSelectionBarRect.bottom;
        
        mRightTglVertexX = mRightCursorRect.right-mTglSize;
        mRightTglVertexY = mSelectionBarRect.top+mSelectionBarHeight/2-mTglSize;
        
        mSelectionBarRect.left = 0;
        mSelectionBarRect.right = mRangeSelectionBarWidthMeasure +mSelectionBarRect.left;
        mSelectionBarRect.top = 0;
        mSelectionBarRect.bottom = mSelectionBarRect.top + mSelectionBarHeight;
        
        mSelectionBarSelectedRect.left = mLeftCursorRect.left;
        mSelectionBarSelectedRect.right = mRightCursorRect.right;
        mSelectionBarSelectedRect.top = mSelectionBarRect.top;
        mSelectionBarSelectedRect.bottom = mSelectionBarRect.bottom;
        
        mSelectionBarFillRect.left = mLeftCursorRect.right;
        mSelectionBarFillRect.right = mRightCursorRect.left;
        mSelectionBarFillRect.top = 0+mFillOffsetY;
        mSelectionBarFillRect.bottom = mSelectionBarFillRect.top + mSelectionBarHeight-2*mFillOffsetY;
        invalidate();
    }

    public void delegateUpdate(int index,int percent)
    {
        if (index == CLICK_PLAY_CURSOR)
        {
        	mPlayPercent = percent;
        	if (mType == Type.Clip)
        	{
        		mPlayCursorRect.left = mPlayPercent*(mRangeSelectionBarWidth) / 100 + mLeftCursorOffsetW;
        		if (mPlayCursorRect.left > mRightCursorRect.left - mPlayCursorOffsetW)
            	{
            		mPlayCursorRect.left= mRightCursorRect.left - mPlayCursorOffsetW;
            	}
        	}
        	else if (mType == Type.MV)
        	{
    			mPlayCursorRect.left = mPlayPercent*(mRangeSelectionBarWidth) / 100;
        	}
    			
        	mPlayCursorRect.right = mPlayCursorRect.left + mPlayCursorOffsetW;    
        }
        
        if (index == CLICK_LEFT_CURSOR)
        {
        	mLeftPercent = percent;
    		mLeftCursorRect.left = mLeftPercent * mRangeSelectionBarWidth / 100;
        	mLeftCursorRect.right = mLeftCursorRect.left + mLeftCursorOffsetW ;
        	
            mLeftTglVertexX = mLeftCursorRect.left + mTglSize;
            mLeftTglVertexY = mSelectionBarHeight / 2 + mSelectionBarRect.top - mTglSize;
        }
        
        if (index == CLICK_RIGHT_CURSOR)
        {
        	mRightPercent = percent;
        	mRightCursorRect.right = mRightPercent*mRangeSelectionBarWidth / 100;     
    		mRightCursorRect.left = mRightCursorRect.right - mRightCursorOffsetW;
    		
            mRightTglVertexX = mRightCursorRect.right - mTglSize;
            mRightTglVertexY = mSelectionBarHeight / 2 + mSelectionBarRect.top - mTglSize;
        }
        
        mSelectionBarRect.left = 0;
        mSelectionBarRect.right = mRangeSelectionBarWidthMeasure + mSelectionBarRect.left;
        mSelectionBarRect.top = 0;
        mSelectionBarRect.bottom = mSelectionBarRect.top + mSelectionBarHeight;
        
        mSelectionBarSelectedRect.left = mLeftCursorRect.left;
        mSelectionBarSelectedRect.right = mRightCursorRect.right;
        mSelectionBarSelectedRect.top = mSelectionBarRect.top;
        mSelectionBarSelectedRect.bottom = mSelectionBarRect.bottom;
        
        mSelectionBarFillRect.left = mLeftCursorRect.right;
        mSelectionBarFillRect.right = mRightCursorRect.left;
        mSelectionBarFillRect.top = 0 + mFillOffsetY;
        mSelectionBarFillRect.bottom = mSelectionBarFillRect.top + mSelectionBarHeight - 2*mFillOffsetY;
        
        invalidate();
    }

    public int getLeftCursorPercent()
    {
        return mLeftPercent;
    }

    public int getRightCursorPercent()
    {
        return mRightPercent;
    }

    /**
     * 
     * 设置MovieRangeSelectionBar的使用情况
     */
    public void setType(Type type)
    {
    	this.mType = type;
    }
    @SuppressLint("DrawAllocation")
    @Override
    protected void onDraw(Canvas canvas)
    {
    	super.onDraw(canvas);

    	/*** Draw ClipSeekBar Background***/
    	final float radius = 0;
        mBarPaint.setColor(Color.parseColor(mSeekBarColorBg));
        canvas.drawRoundRect(mSelectionBarRect, radius, radius, mBarPaint);

        /*** Draw ClipSeekBar Select Background*/
        mBarPaint.setColor(Color.parseColor(mSeekBarSelectColorBg));
        canvas.drawRoundRect(mSelectionBarSelectedRect, radius, radius, mBarPaint);

        /**Draw ClipSeekBar Select Fill Background*/
        mBarPaint.setColor(Color.parseColor(mSeekBarColorBg));
        canvas.drawRoundRect(mSelectionBarFillRect, radius, radius, mBarPaint);

        mBarPaint.setColor(Color.parseColor(mSeekBarColorBg));
        canvas.drawRoundRect(new RectF(0,mSelectionBarRect.top,mSelectionBarFillRect.left,mSelectionBarRect.bottom), radius, radius, mBarPaint);

        mBarPaint.setColor(Color.parseColor(mSeekBarColorBg));
        canvas.drawRoundRect(new RectF(mSelectionBarFillRect.right,mSelectionBarRect.top,mSelectionBarRect.right,mSelectionBarSelectedRect.bottom), radius, radius, mBarPaint);
        
        /**Draw Bitmap */
        if(mVideoThumbList != null)
        {
            int size = mVideoThumbList.size();
        	for (int i = 0 ; i< size ; i++)
        	{
        		Bitmap bitmap = mVideoThumbList.get(i);
        		if (bitmap == null) continue;
        		
        		Rect rect = new Rect();
        		rect.left = i * mRangeSelectionBarWidth / size;
        		rect.right = rect.left + bitmap.getWidth();
           		rect.top = (int) mSelectionBarFillRect.top;
           		rect.bottom = (int)mSelectionBarFillRect.bottom;

                canvas.drawBitmap(bitmap, null, rect, null);
        	}        	
        }
    	
        /**Draw Left Cursor*/
        final float left_radius = 0;
        mPaint.setColor(Color.parseColor(mSeekBarSelectColorBg));
        canvas.drawRoundRect(mLeftCursorRect, left_radius, left_radius, mPaint);
        
        /**Draw Right Cursor*/
        final float right_radius = 0;
        mPaint.setColor(Color.parseColor(mSeekBarSelectColorBg));
        canvas.drawRoundRect(mRightCursorRect, right_radius, right_radius, mPaint);

        /**Draw Left Triangle*/
        mPaint.setColor(Color.WHITE);
        Path path = new Path();
        path.moveTo(mLeftTglVertexX, mLeftTglVertexY);
        path.lineTo(mLeftTglVertexX, mLeftTglVertexY + mTglSize * 2);
        path.lineTo(mLeftTglVertexX + mTglSize, mLeftTglVertexY + mTglSize);
        path.close();
        canvas.drawPath(path, mPaint);

        /**Draw Right Triangle*/
        mPaint.setColor(Color.WHITE);
        path = new Path();
        path.moveTo(mRightTglVertexX, mRightTglVertexY);
        path.lineTo(mRightTglVertexX, mRightTglVertexY + mTglSize * 2);
        path.lineTo(mRightTglVertexX - mTglSize, mRightTglVertexY + mTglSize);
        path.close();
        canvas.drawPath(path, mPaint);
        
        if (mType == Type.MV)
        drawShadow(canvas);
        /**Draw Play Cursor*/
        if (isShowPlayCursor)
        {
            final float play_radius = 0;
            mPaint.setColor(Color.parseColor(mPlayCursorColorBg));
            canvas.drawRoundRect(mPlayCursorRect, play_radius, play_radius, mPaint);        	
        }
        
    }
    private void drawShadow(Canvas canvas)
    {
    	Rect mRect = new Rect((int)mLeftCursorRect.right,(int)mSelectionBarFillRect.top,
    			(int)mRightCursorRect.left,(int)mSelectionBarFillRect.bottom);
    	canvas.drawRect(mRect, mShadowPaint);
    }
    
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
    
    public void setLeftSelection(int percent)
    {
    	delegateUpdate(CLICK_LEFT_CURSOR,percent);
    }
    
    public void setPlaySelection(int percent)
    {
    	delegateUpdate(CLICK_PLAY_CURSOR,percent);
    }
    
    public void setRightSelection(int percent)
    {
    	delegateUpdate(CLICK_RIGHT_CURSOR,percent);
    }
    
    public void setOnCursorChangeListener(OnCursorChangeListener l)
    {
        mListener = l;
    }

    public static interface OnCursorChangeListener 
    {
    	
    	void onSeeekBarChanged(int width, int height);
    	
        void onLeftCursorChanged(int percent);
        
        void onPlayCursorChanged(int percent);
        
        void onRightCursorChanged(int percent);
        
        void onLeftCursorUp();
        
        void onRightCursorUp();
    }

	public boolean isShowPlayCursor()
	{
		return isShowPlayCursor;
	}

	public void setShowPlayCursor(boolean isShowPlayCursor)
	{
		this.isShowPlayCursor = isShowPlayCursor;
		invalidate();
	}
	
	public List<Bitmap> getVideoThumbList()
	{
        if (this.mVideoThumbList == null)
                this.mVideoThumbList = new ArrayList<>(5);

		return mVideoThumbList;
	}

	public void drawVideoThumbList(List<Bitmap> list)
	{
        this.mVideoThumbList = list;
		invalidate();
	}

	public void drawVideoThumb(Bitmap bitmap)
    {
        this.getVideoThumbList().add(bitmap);
        invalidate();
    }

	public String getSeekBarColorBg()
	{
		return mSeekBarColorBg;
	}

	public void setSeekBarColorBg(String seekBarColorBg)
	{
		this.mSeekBarColorBg = seekBarColorBg;
		invalidate();
	}

	public String getSeekBarSelectColorBg()
	{
		return mSeekBarSelectColorBg;
	}

	public void setSeekBarSelectColorBg(String seekBarSelectColorBg)
	{
		this.mSeekBarSelectColorBg = seekBarSelectColorBg;
		invalidate();
	}

	public String getPlayCursorColorBg()
	{
		return mPlayCursorColorBg;
	}

	public void setPlayCursorColorBg(String playCursorColorBg)
	{
		this.mPlayCursorColorBg = playCursorColorBg;
		invalidate();
	}

	public int getCursorSpace() {
		return mCursorSpace;
	}

	public void setCursorSpace(int mCursorSpace) {
		this.mCursorSpace = mCursorSpace;
	}
	
}
