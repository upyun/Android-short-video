/**
 * TuSDKVideoDemo
 * MixingCellView.java
 *
 * @author  LiuHang
 * @Date  Jul 4, 2017 6:06:07 PM
 * @Copright (c) 2017 tusdk.com. All rights reserved.
 *
 */
package org.lasque.tusdkvideodemo.views;

import org.lasque.tusdk.core.TuSdkContext;
import org.lasque.tusdk.core.seles.tusdk.FilterLocalPackage;
import org.lasque.tusdk.core.view.TuSdkImageView;
import org.lasque.tusdk.core.view.listview.TuSdkCellRelativeLayout;
import org.lasque.tusdkvideodemo.R;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView.ScaleType;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * 自定义的混音列表单元视图
 * 
 * @author LiuHang
 *
 */
public class MixingCellView extends TuSdkCellRelativeLayout<MixingCellView.MixingEntity>
{
	/** 缩略图 */
	private TuSdkImageView mThumbView;

	/** 混音单元名称 */
	private TextView mTitlebView;
	
	/** 混音单元边框 */
	private RelativeLayout mMixingBorderView ;
	
	// 标记该混音单元是否被选中
	private int flag = -1;
	
	private MixingCellViewDelegate mMixingCellViewDelegate;

	public MixingCellView(Context context, AttributeSet attrs, int defStyle)
	{
		super(context, attrs, defStyle);
	}

	public MixingCellView(Context context, AttributeSet attrs)
	{
		super(context, attrs);
	}

	public MixingCellView(Context context)
	{
		super(context);
	}
	
	/** 混音分组元素视图委托 */
	public interface MixingCellViewDelegate
	{
		/** 长按视图 */
		void onMixingCellViewLongClick(MixingCellView view);
	}
	
	public void setLongClickDelegate(MixingCellViewDelegate delegate) 
	{
		this.mMixingCellViewDelegate = delegate;
		if (mMixingCellViewDelegate == null)
		{
			this.setOnLongClickListener(null);
		}
		else
		{
			this.setOnLongClickListener(mOnlonClickListener);
		}
	}
	
	public MixingCellViewDelegate getLongClickDelegate()
	{
		return mMixingCellViewDelegate;
	}
	
	private View.OnLongClickListener mOnlonClickListener = new View.OnLongClickListener()
	{

		@Override
		public boolean onLongClick(View v) 
		{
			getLongClickDelegate().onMixingCellViewLongClick(MixingCellView.this);
			return true;
		}
		
	};
	
	@SuppressLint("DefaultLocale") 
	@Override
	protected void bindModel() 
	{
		MixingCellView.MixingEntity model = this.getModel();
		
		if (model == null) return;
				
		String filterImageName = "lsq_mixing_thumb_" + model.mName.toLowerCase();
		
		Drawable filterImage = TuSdkContext.getDrawable(filterImageName);
		
		if (this.getImageView() != null)
		{
			if (model.mTypeId == 0) 
			{
				RelativeLayout.LayoutParams lp = (LayoutParams) getImageView().getLayoutParams();
				lp.width = TuSdkContext.dip2px(27);
				lp.height = TuSdkContext.dip2px(27);
				getImageView().setLayoutParams(lp);
				getImageView().setScaleType(ScaleType.CENTER_INSIDE);
			}
			getImageView().setImageDrawable(filterImage);
		}
		
		if (this.getTitleView() != null)
		{
			getTitleView().setText(TuSdkContext.getString("lsq_mixing_" + model.mName.toLowerCase()));
			
			if (model.mTypeId == 0) 
			{
				getTitleView().setBackground(null);
				getTitleView().setTextColor(TuSdkContext.getColor("lsq_dubbing_unselected_color"));
			}
		}
	}
	
	public TuSdkImageView getImageView()
	{
		if (mThumbView == null)
		{
			mThumbView = (TuSdkImageView)findViewById(R.id.lsq_item_image);
		}
		return mThumbView;
	}
	
	public RelativeLayout getBorderView()
	{
		if (mMixingBorderView == null)
		{
			 mMixingBorderView = (RelativeLayout)findViewById(R.id.lsq_item_border);
		}
		return mMixingBorderView;
	}
	
	public TextView getTitleView()
	{
		if (mTitlebView == null)
		{
			mTitlebView = (TextView)findViewById(R.id.lsq_item_title);
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
	 * 标记该滤镜项是否被选中
	 * 
	 * @param flag
	 */
	public void setFlag(int flag)
	{
		this.flag = flag;
	}
	
	/**
	 * 标记该滤镜项是否被选中
	 * 
	 * @return
	 */
	public int getFlag()
	{
		return this.flag;
	}
	
	/** 歌曲列表数据结构 */
	public static class MixingEntity
	{
		/** 数据结构类型 */
		public int mTypeId;
		
		public String mName;

		/**
		 * @param typeId
		 * @param name
		 */
		public MixingEntity(int typeId, String name) 
		{
			super();
			this.mTypeId = typeId;
			this.mName = name;
		}	
	}
}
