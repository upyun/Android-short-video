/**
 * TuSDKVideoDemo
 * MixingListView.java
 *
 * @author  LiuHang
 * @Date  Jul 5, 2017 6:05:15 PM
 * @Copright (c) 2017 tusdk.com. All rights reserved.
 *
 */
package com.upyun.shortvideo.views;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import com.upyun.shortvideo.views.AudioEffectCellView.MixingCellViewDelegate;

import org.lasque.tusdk.core.TuSdkContext;
import org.lasque.tusdk.core.view.recyclerview.TuSdkTableView;
import com.upyun.shortvideo.R;

/**
 * 自定义的混音列表
 * 
 * @author LiuHang
 *
 */
public class AudioEffectListView extends TuSdkTableView<AudioEffectCellView.AudioEffectEntity, AudioEffectCellView>
{
	/** 行视图宽度 */
	private int mCellWidth;
	
	private MixingCellViewDelegate mDelegate;
	
	public AudioEffectListView(Context context, AttributeSet attrs, int defStyle)
	{
		super(context, attrs, defStyle);
	}

	public AudioEffectListView(Context context, AttributeSet attrs)
	{
		super(context, attrs);
	}

	public AudioEffectListView(Context context)
	{
		super(context);
	}
	
	public void setLongClickDelegate(MixingCellViewDelegate delegate)
	{
		this.mDelegate = delegate;
	}

	public MixingCellViewDelegate getLongClickDelegate()
	{
		return mDelegate;
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
	protected void onViewCreated(AudioEffectCellView view, ViewGroup parent, int viewType)
	{
		if (this.getCellWidth() > 0)
		{
			view.setWidth(this.getCellWidth());
		}
		view.setLongClickDelegate(mDelegate);
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
	protected void onViewBinded(AudioEffectCellView view, int position)
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
	
	@SuppressLint("DefaultLocale") 
	private void updateFilterCellViewStatus(AudioEffectCellView view, Boolean isSelected)
	{
		AudioEffectCellView.AudioEffectEntity model = view.getModel();
			if (isSelected)
			{
				view.getBorderView().setVisibility(View.VISIBLE);
				
				if (model.mTypeId == 1)
				{
					view.getTitleView().setBackground(TuSdkContext.getDrawable(R.drawable.tusdk_view_filter_selected_text_roundcorner));
				}
				else if (model.mTypeId == 0) 
				{
					view.getTitleView().setTextColor(TuSdkContext.getColor("lsq_filter_title_color"));
					view.getImageView().setImageDrawable(TuSdkContext.getDrawable("lsq_mixing_thumb_"+model.mName.toLowerCase()+"_selected"));
				}
				
			}
			else
			{
				view.getBorderView().setVisibility(View.GONE);
				if (model.mTypeId == 1)
				{
					view.getTitleView().setBackground(TuSdkContext.getDrawable(R.drawable.tusdk_view_filter_unselected_text_roundcorner));
				}
				else if (model.mTypeId == 0)
				{
					view.getTitleView().setTextColor(TuSdkContext.getColor("lsq_dubbing_unselected_color"));
					view.getImageView().setImageDrawable(TuSdkContext.getDrawable("lsq_mixing_thumb_"+model.mName.toLowerCase()));
				}
				
			}
	}
}
