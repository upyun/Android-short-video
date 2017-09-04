/**
 * TuSDKVideoDemo
 * StickerListView.java
 *
 * @author  Yanlin
 * @Date  Jan 5, 2017 6:05:15 PM
 * @Copright (c) 2016 tusdk.com. All rights reserved.
 *
 */
package com.upyun.shortvideo.views;

import java.util.List;

import org.lasque.tusdk.core.view.recyclerview.TuSdkTableView;
import org.lasque.tusdk.modules.view.widget.sticker.StickerGroup;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

/**
 * @author Yanlin
 *
 */
public class StickerListView extends TuSdkTableView<StickerGroup, StickerCellView> 
{
	/** 行视图宽度 */
	private int mCellWidth;
	
	public StickerListView(Context context, AttributeSet attrs, int defStyle)
	{
		super(context, attrs, defStyle);
	}

	public StickerListView(Context context, AttributeSet attrs)
	{
		super(context, attrs);
	}

	public StickerListView(Context context)
	{
		super(context);
	}
	
	/**
	 * 根据groupId获取对应的贴纸视图选项
	 * @param groupId
	 * @return
	 */
	public View getStickCellView(long groupId)
	{
		List<StickerGroup> groups = this.getModeList();
		
		if (groups == null) return null;
		
		for (int i = 0 ; i < groups.size() ; i++)
		{
			StickerGroup group = groups.get(i);
			if (group.groupId == groupId)
				return this.findViewWithTag(Integer.valueOf(i));	
		}
		
		return null;
	}
	
	/** 行视图宽度 */
	public int getCellWidth()
	{
		return mCellWidth;
	}

	/** 行视图宽度 */
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
	protected void onViewCreated(StickerCellView view, ViewGroup parent, int viewType) 
	{
		if (this.getCellWidth() > 0)
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
	protected void onViewBinded(StickerCellView view, int position) 
	{
		view.setTag(position);
	}
}
