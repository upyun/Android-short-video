/**
 * TuSDKVideoDemo
 * FilterCellView.java
 *
 * @author  Yanlin
 * @Date  Jan 5, 2017 6:06:07 PM
 * @Copright (c) 2016 tusdk.com. All rights reserved.
 *
 */
package com.upyun.shortvideo.views;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.lasque.tusdk.core.TuSdkContext;
import org.lasque.tusdk.core.seles.tusdk.FilterLocalPackage;
import org.lasque.tusdk.core.view.TuSdkImageView;
import org.lasque.tusdk.core.view.listview.TuSdkCellRelativeLayout;
import org.lasque.tusdk.core.view.listview.TuSdkListSelectableCellViewInterface;
import com.upyun.shortvideo.R;

/**
 * @author Yanlin
 *
 */
public class TimeEffectCellView extends TuSdkCellRelativeLayout<String> implements TuSdkListSelectableCellViewInterface
{
	/** 缩略图 */
	private TuSdkImageView mThumbView;

	/** 特效名称*/
	private TextView mTitlebView;

	/** 特效边框*/
	private View mFilterBorderView ;

	// 标记该滤镜项是否被选中
	private int flag = -1;

	public TimeEffectCellView(Context context, AttributeSet attrs, int defStyle)
	{
		super(context, attrs, defStyle);
	}

	public TimeEffectCellView(Context context, AttributeSet attrs)
	{
		super(context, attrs);
	}

	public TimeEffectCellView(Context context)
	{
		super(context);
	}
	
	@Override
	protected void bindModel() 
	{
		bindModelTypeOne();

		bindModelTypeTwo();
	}

	/**
	 * 绑定第一种类型的数据
	 */
	protected void bindModelTypeOne()
	{
		String filterCode = this.getModel();

		if (filterCode == null) return;

		filterCode = filterCode.toLowerCase();

		String filterImageName = getThumbPrefix() + filterCode;

		Bitmap filterImage = TuSdkContext.getRawBitmap(filterImageName);

		if (this.getImageView() != null && filterImage != null)
		{
			getImageView().setImageBitmap(filterImage);
		}

		if (this.getTitleView() != null)
		{
			getTitleView().setText(TuSdkContext.getString(getTextPrefix()+ filterCode));
		}
	}

	/**
	 * 绑定第二种类型的数据
	 */
	protected void bindModelTypeTwo()
	{
		RelativeLayout layout =  this.findViewById(R.id.lsq_none_layout);
		ImageView imageView = this.findViewById(R.id.lsq_item_none);
		if (layout != null)
		{
			layout.setVisibility(((Integer)getTag() == 0)?View.VISIBLE:View.GONE);
			imageView.setVisibility(((Integer)getTag() == 0)?View.VISIBLE:View.GONE);
		}
	}

	/**
	 * 缩略图前缀
	 *
	 * @return
	 */
	protected String getThumbPrefix()
	{
		return "lsq_time_effect_thumb_";
	}

	/**
	 * Item名称前缀
	 *
	 * @return
	 */
	protected String getTextPrefix()
	{
		return "lsq_time_effect_";
	}

	@Override
	public void onCellSelected(int position)
	{
		View filterBorderView = getBorderView();
		filterBorderView.setVisibility(View.VISIBLE);

		TextView titleView = getTitleView();
		titleView.setBackground(TuSdkContext.getDrawable("tusdk_view_filter_selected_text_roundcorner"));
	}

	@Override
	public void onCellDeselected()
	{
		View filterBorderView = getBorderView();
		filterBorderView.setVisibility(View.GONE);

		setFlag(-1);
		getTitleView().setBackground(TuSdkContext.getDrawable("tusdk_view_filter_unselected_text_roundcorner"));
		getImageView().invalidate();
	}

	public TuSdkImageView getImageView()
	{
		if (mThumbView == null)
		{
			mThumbView = findViewById(R.id.lsq_item_image);
		}
		return mThumbView;
	}
	
	public View getBorderView()
	{
		if (mFilterBorderView == null)
		{
			 mFilterBorderView = findViewById(R.id.lsq_item_border);
		}
		return mFilterBorderView;
	}
	
	public TextView getTitleView()
	{
		if (mTitlebView == null)
		{
			mTitlebView = findViewById(R.id.lsq_item_title);
		}
		return mTitlebView;
	}

	public void viewNeedRest()
	{
		super.viewNeedRest();
		
		if (this.getImageView() != null)
		{
			this.getImageView().setImageBitmap(null);

			FilterLocalPackage.shared().cancelLoadImage(getImageView());
		}
	}
	
	@Override
	protected void onLayouted()
	{
		super.onLayouted();
		if (this.getImageView() != null)
		{
	
			this.getImageView().setCornerRadiusDP(4);
		}
	}
	
	/**
	 * 标记该特效项是否被选中
	 * 
	 * @param flag
	 */
	public void setFlag(int flag)
	{
		this.flag = flag;
	}
	
	/**
	 * 标记该特效项是否被选中
	 * 
	 * @return
	 */
	public int getFlag()
	{
		return this.flag;
	}
}
