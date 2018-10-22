/**
 * TuSDKVideoDemo
 * RoundProgressBar.java
 *
 * @author  LiuHang
 * @Date  May 20, 2017 6:05:15 PM
 * @Copright (c) 2017 tusdk.com. All rights reserved.
 *
 */
package com.upyun.shortvideo.views;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import org.lasque.tusdk.core.utils.TLog;
import com.upyun.shortvideo.R;

/**
 * 圆形进度条
 * @author LiuHang
 *
 */
public class RoundProgressBar extends View
{	
	public static final int STROKE = 0;
	public static final int FILL = 1;
	
	/** 画笔对象的引 */
	private Paint mPaint;
	
	/** 圆环的颜色 */
	private int mRingColor;
	
	/** 圆环进度的颜色 */
	private int mRingProgressColor;
	
	/** 圆环的宽度 */
	private float mRingWidth;
	
	/** 最大进度 */
	private int mMax;
	
	/** 当前进度 */
	private int mProgress;
	/** 进度的风格，实心或者空心 */
	private int mStyle;
	/** 内部圆*/
	private int mInnerRoundColor;
	/** 内部圆半径 */
	private float mInnerRoundRadius;
	/** 圆形进度条最外围的宽度 */
	private float mRingProgressWidth;
	
	public RoundProgressBar(Context context) {
		this(context, null);
	}

	public RoundProgressBar(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}
	
	public RoundProgressBar(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		
		mPaint = new Paint();
		TypedArray mTypedArray = context.obtainStyledAttributes(attrs,
				R.styleable.RoundProgressBar);
		
		// 获取自定义属性和默认值
		mRingColor = mTypedArray.getColor(R.styleable.RoundProgressBar_ringColor, Color.WHITE);
		mInnerRoundColor= mTypedArray.getColor(R.styleable.RoundProgressBar_innerRoundColor, Color.WHITE);
		mRingProgressColor = mTypedArray.getColor(R.styleable.RoundProgressBar_ringProgressColor, Color.GREEN);
		mRingWidth = mTypedArray.getDimension(R.styleable.RoundProgressBar_ringWidth, 7);
		mInnerRoundRadius= mTypedArray.getDimension(R.styleable.RoundProgressBar_innerRoundRadius, 35);
		mMax = mTypedArray.getInteger(R.styleable.RoundProgressBar_max, 100);
		mStyle = mTypedArray.getInt(R.styleable.RoundProgressBar_style, 0);
		mRingProgressWidth = mTypedArray.getDimension(R.styleable.RoundProgressBar_ringProgressWidth, 85);
		
		mTypedArray.recycle();
	}
	

	@SuppressLint("DrawAllocation")
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		
		/**
		 * 画最外层的大圆环
		 */
		// 获取圆心的x坐标
		float centre = getWidth()/2; 
		// 圆环的半径

		float radius =(mRingProgressWidth - mRingWidth)/2;
		// 设置圆环的颜色
		mPaint.setColor(mRingColor); 
		// 设置空心
		mPaint.setStyle(Paint.Style.STROKE); 
		// 设置圆环的宽度
		mPaint.setStrokeWidth(mRingWidth); 
		 // 消除锯齿 
		mPaint.setAntiAlias(true); 
		// 画出圆环
		canvas.drawCircle(centre, centre, radius, mPaint); 
		/**
		 * 画内部圆
		 */
		mPaint.setAntiAlias(true);
		mPaint.setColor(mInnerRoundColor);
		mPaint.setStrokeWidth(0); 
		mPaint.setStyle(Paint.Style.FILL);
		canvas.drawCircle(centre, centre,mInnerRoundRadius, mPaint);
		/**
		 * 画圆弧 ，画圆环的进度
		 */
		 // 设置圆环的宽度
		mPaint.setStrokeWidth(mRingWidth);
		 // 设置进度的颜色
		mPaint.setColor(mRingProgressColor); 
		// 用于定义的圆弧的形状和大小的界限
		RectF oval = new RectF(centre - radius, centre - radius, centre
				+ radius, centre + radius);  

		switch (mStyle) {
		case STROKE:{
			mPaint.setStyle(Paint.Style.STROKE);
			// 根据进度画圆弧
			canvas.drawArc(oval, -90, 360 * mProgress / mMax, false, mPaint);  
			break;
		}
		case FILL:{
			mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
			if( mProgress != 0)
			{
				 // 根据进度画圆弧
				canvas.drawArc(oval, -90, 360 * mProgress / mMax, true, mPaint); 
			}
				
			break;
		}
		}
	}
	public void setRingProgresswidth(float RingProgressWidth) {
		this.mRingProgressWidth = RingProgressWidth;
	}

	public float getInnerRoundRadius() {
		return mInnerRoundRadius;
	}

	public void setInnerRoundRadius(float innerRoundRadius) {
		this.mInnerRoundRadius = innerRoundRadius;
	}

	public float getRoundProgressWidth()
	{
		return mRingProgressWidth;
	}
	
	
	public  int getMax() {
		return mMax;
	}
	

	/**
	 * 设置进度的最大值
	 * @param max
	 */
	public  void setMax(int max) {
		if( max < 0){
			TLog.i("max: %s", "max < 0 not allowed");
		}
		this.mMax = max;
	}

	/**
	 * 获取进度.需要同步
	 * @return
	 */
	public  int getProgress() {
		return mProgress;
	}

	/**
	 * 刷新界面调用postInvalidate()能在非UI线程刷新
	 * @param progress
	 */
	public  void setProgress(int progress) {
		if( progress < 0 ){
			return;
		}
		if( progress > mMax ){
			progress = mMax;
		}
		if( progress <= mMax ){
			this.mProgress = progress;
			postInvalidate();
		}
		
	}
	
	public void setInnerRoundColor(int innerRoundColor)
	{
		this.mInnerRoundColor=innerRoundColor;
	}
	public int getCricleColor() {
		return mRingColor;
	}

	public void setCricleColor(int cricleColor) {
		this.mRingColor = cricleColor;
	}

	public int getRingProgressColor() {
		return mRingProgressColor;
	}

	public void setRingProgressColor(int ringProgressColor) {
		this.mRingProgressColor = ringProgressColor;
	}

	public float getRingWidth() {
		return mRingWidth;
	}

	public void setRingWidth(float ringWidth) {
		this.mRingWidth = ringWidth;
	}



}
