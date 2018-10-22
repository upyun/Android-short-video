/**
 * TuSDKVideoDemo
 * MVListView.java
 *
 * @author  LiuHang
 * @Date  May 5, 2017 6:05:15 PM
 * @Copright (c) 2017 tusdk.com. All rights reserved.
 *
 */
package com.upyun.shortvideo.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;

import org.lasque.tusdk.core.view.recyclerview.TuSdkTableView;
import org.lasque.tusdk.modules.view.widget.sticker.StickerGroup;

/**
 * 自定义的MV列表
 * @author LiuHang
 *
 */
public class StickerAudioEffectListView extends TuSdkTableView<StickerGroup, StickerAudioEffectCellView>
{
	/** 行视图宽度  */
	private int mCellWidth;
	
	public StickerAudioEffectListView(Context context, AttributeSet attrs, int defStyle)
	{
		super(context, attrs, defStyle);
	}

	public StickerAudioEffectListView(Context context, AttributeSet attrs)
	{
		super(context, attrs);
	}

	public StickerAudioEffectListView(Context context)
	{
		super(context);
	}
	
	/** 行视图宽度  */
	public int getCellWidth()
	{
		return mCellWidth;
	}

	/** 行视图宽度  */
	public void setCellWidth(int mCellWidth)
	{
		this.mCellWidth = mCellWidth;
	}
	
	@Override
	public void loadView()
	{
		super.loadView();
		this.setHasFixedSize(true);
	}
	
	/**
	 * 视图创建
	 * 
	 * @param view
	 *            创建的视图
	 * @param parent
	 *            父对象
	 * @param viewType
	 *            视图类型
	 */
	@Override
	protected void onViewCreated(StickerAudioEffectCellView view, ViewGroup parent, int viewType)
	{
		if ( this.getCellWidth() > 0 )
		{
			view.setWidth(this.getCellWidth());
		}
	}

	/**
	 * 绑定视图数据
	 * 
	 * @param view
	 *            创建的视图
	 * @param position
	 *            索引位置
	 */
	@Override
	protected void onViewBinded(StickerAudioEffectCellView view, int position)
	{
		view.setTag(position);
	}
}
