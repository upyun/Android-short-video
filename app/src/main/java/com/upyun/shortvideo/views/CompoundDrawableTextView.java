/**
 * TuSDKVideoDemo
 * CompoundDrawableTextView.java
 *
 * @author     LiuHang
 * @Date:      June 1, 2017 7:28:12 PM
 * @Copyright: (c) 2017 tusdk.com. All rights reserved.
 *
 */
package com.upyun.shortvideo.views;

import com.upyun.shortvideo.R;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * 自定义TextView，可以设置CompoundDrawable大小
 * @author LiuHang
 */
public class CompoundDrawableTextView extends TextView
{
	/** 属性集合 */
	private TypedArray mTypedArray;
	/** 图片高度 */
	private float mDrawableTopHeight;
	/** 图片宽度 */
	private float mDrawableTopWidth;
	/** 顶部图片 */
	private Drawable mDrawableTop;
	
	public CompoundDrawableTextView(Context context) {
		super(context);
	}
	
	@SuppressLint("Recycle")
	public CompoundDrawableTextView(Context context, AttributeSet attrs) 
	{
		super(context, attrs);
		
		mTypedArray=context.obtainStyledAttributes(attrs, R.styleable.CompoundDrawableTextView);
		mDrawableTopHeight=mTypedArray.getDimension(R.styleable.CompoundDrawableTextView_drawableTopHeight, 10);
		mDrawableTopWidth=mTypedArray.getDimension(R.styleable.CompoundDrawableTextView_drawableTopWidth, 10);
		mDrawableTop=mTypedArray.getDrawable(R.styleable.CompoundDrawableTextView_drawableTop);
		
		// 自定义设置图片的宽高
		if(mDrawableTop != null) mDrawableTop.setBounds(0, 0, (int)mDrawableTopWidth, (int)mDrawableTopHeight);
		
		setCompoundDrawables(null, mDrawableTop, null, null);
	}
}
