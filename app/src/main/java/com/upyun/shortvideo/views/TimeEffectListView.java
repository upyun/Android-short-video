/**
 * TuSDKVideoDemo
 * FilterListView.java
 *
 * @author  Yanlin
 * @Date  Jan 5, 2017 6:05:15 PM
 * @Copright (c) 2016 tusdk.com. All rights reserved.
 *
 */
package com.upyun.shortvideo.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import org.lasque.tusdk.core.TuSdkContext;
import org.lasque.tusdk.core.view.recyclerview.TuSdkTableView;
import com.upyun.shortvideo.R;

/**
 * 时间特效
 * @author xujie
 *
 */
public class TimeEffectListView extends TuSdkTableView<String, TimeEffectCellView>
{
	/** 行视图宽度 */
	private int mCellWidth;

	/** 默认选择第几项 */
	private int mSelectedPosition = 0;


	public TimeEffectListView(Context context, AttributeSet attrs, int defStyle)
	{
		super(context, attrs, defStyle);
	}

	public TimeEffectListView(Context context, AttributeSet attrs)
	{
		super(context, attrs);
	}

	public TimeEffectListView(Context context)
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
	
    public void selectPosition(int position) {
        mSelectedPosition = position;
        getSdkAdapter().notifyDataSetChanged();
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
	protected void onViewCreated(TimeEffectCellView view, ViewGroup parent, int viewType)
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
	protected void onViewBinded(TimeEffectCellView view, int position)
	{
		view.setTag(position);
		
		if(mSelectedPosition != position)
		{
			// 取消当前特效选中状态
			updateFilterCellViewStatus(view, false);
		}
		else
		{
			// 选中当前特效
			updateFilterCellViewStatus(view, true);
		}
	}
	
	/**
	 * 更新特效选中状态
	 * 
	 * @param view
	 * @param isSelected
	 */
	
	private void updateFilterCellViewStatus(TimeEffectCellView view, Boolean isSelected)
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
