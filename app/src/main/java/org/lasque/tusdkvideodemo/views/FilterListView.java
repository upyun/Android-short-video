/**
 * TuSDKVideoDemo
 * FilterListView.java
 *
 * @author  Yanlin
 * @Date  Jan 5, 2017 6:05:15 PM
 * @Copright (c) 2016 tusdk.com. All rights reserved.
 *
 */
package org.lasque.tusdkvideodemo.views;

import org.lasque.tusdk.core.TuSdkContext;
import org.lasque.tusdk.core.view.recyclerview.TuSdkTableView;
import org.lasque.tusdkvideodemo.R;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

/**
 * @author Yanlin
 *
 */
public class FilterListView extends TuSdkTableView<String, FilterCellView> 
{
	/** 行视图宽度 */
	private int mCellWidth;
	
	public FilterListView(Context context, AttributeSet attrs, int defStyle)
	{
		super(context, attrs, defStyle);
	}

	public FilterListView(Context context, AttributeSet attrs)
	{
		super(context, attrs);
	}

	public FilterListView(Context context)
	{
		super(context);
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
	protected void onViewCreated(FilterCellView view, ViewGroup parent, int viewType) 
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
	protected void onViewBinded(FilterCellView view, int position)
	{
		if(view.getFlag() != position)
		{
			// 取消当前滤镜选中状态
			updateFilterCellViewStatus(view, false);
		}
		else
		{
			// 选中当前滤镜
			updateFilterCellViewStatus(view, true);
		}
	}
	
	/**
	 * 更新滤镜选中状态
	 * 
	 * @param view
	 * @param isSelected
	 */
	
	private void updateFilterCellViewStatus(FilterCellView view, Boolean isSelected)
	{
		if(isSelected)
		{
			view.getBorderView().setVisibility(View.VISIBLE);
			view.getTitleView().setBackground(TuSdkContext.getDrawable(R.drawable.tusdk_view_filter_selected_text_roundcorner));
		}
		else
		{
			view.getBorderView().setVisibility(View.GONE);
			view.getTitleView().setBackground(TuSdkContext.getDrawable(R.drawable.tusdk_view_filter_unselected_text_roundcorner));
		}
	}
}
