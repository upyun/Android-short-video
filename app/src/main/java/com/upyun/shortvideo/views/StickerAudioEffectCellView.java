/**
 * TuSDKVideoDemo
 * MVCellView.java
 *
 * @author  LiuHang
 * @Date  May 5, 2017 6:06:07 PM
 * @Copright (c) 2017 tusdk.com. All rights reserved.
 *
 */
package com.upyun.shortvideo.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.lasque.tusdk.core.TuSdkContext;
import org.lasque.tusdk.core.view.TuSdkImageView;
import org.lasque.tusdk.core.view.listview.TuSdkCellRelativeLayout;
import org.lasque.tusdk.core.view.listview.TuSdkListSelectableCellViewInterface;
import org.lasque.tusdk.modules.view.widget.sticker.StickerGroup;
import org.lasque.tusdk.modules.view.widget.sticker.StickerLocalPackage;
import com.upyun.shortvideo.R;

/**
 * 自定义的MV列表单元视图
 * @author LiuHang
 *
 */
public class StickerAudioEffectCellView extends TuSdkCellRelativeLayout<StickerGroup> implements TuSdkListSelectableCellViewInterface
{
	/** 缩略图 */
	private ImageView mThumbView;
	/** MV 名称 */
	private TextView mTitleView;
	
	/** MV 边框 */
	private RelativeLayout mMvBorderView ;
	/** MV 列表的背景图片 */
	private TuSdkImageView mBgImageView;

	public StickerAudioEffectCellView(Context context, AttributeSet attrs, int defStyle)
	{
		super(context, attrs, defStyle);
	}

	public StickerAudioEffectCellView(Context context, AttributeSet attrs)
	{
		super(context, attrs);
	}

	public StickerAudioEffectCellView(Context context)
	{
		super(context);
	}

	@Override
	protected void bindModel() 
	{
		StickerGroup model = this.getModel();
		if ( model == null ) return;
	
		if ( this.getImageView() != null )
		{
			StickerLocalPackage.shared().loadGroupThumb(model, this.getImageView());
		}

		if ( this.getTitleView() != null )
		{
			String modelName;

			if (model.name == null )
				modelName = "";
			else
			modelName = TuSdkContext.getString("lsq_mv_" + model.name);

			getTitleView().setText(modelName);
		}
		
		if((Integer) getTag() == 0 )
		{
			getMvLayout().setVisibility(View.INVISIBLE);
			getMvNoneLayout().setVisibility(View.VISIBLE);
		}
		else 
		{
			getMvLayout().setVisibility(View.VISIBLE);
			getMvNoneLayout().setVisibility(View.INVISIBLE);
		}
	}

	public ImageView getImageView()
	{
		if ( mThumbView == null )
		{
			mThumbView = (ImageView)findViewById(R.id.lsq_item_image);
		}
		return mThumbView;
	}
	
	@Override
	protected void onLayouted() 
	{
		super.onLayouted();
		
		if ( this.getBgImageView() != null )
		{
			this.getBgImageView().setCornerRadiusDP(4);
		}
	}

	private View mMvNoneLayout;
	
	public View getMvNoneLayout()
	{
		if ( mMvNoneLayout == null )
		{
		mMvNoneLayout = findViewById(R.id.lsq_mv_none_btn);
	}
		return mMvNoneLayout;
	}
	
	private RelativeLayout mMvLayout;
	
	public RelativeLayout getMvLayout()
	{
		if ( mMvLayout == null )
		{
			mMvLayout = (RelativeLayout)findViewById(R.id.lsq_mv_wrap);
		}
		return mMvLayout;
	}

	public RelativeLayout getBorderView()
	{
		if ( mMvBorderView == null )
		{				
			mMvBorderView = (RelativeLayout)findViewById(R.id.lsq_item_border);		
			
		}
	
		return mMvBorderView;
	}

	public TextView getTitleView()
	{
		if ( mTitleView == null )
		{
			mTitleView = (TextView)findViewById(R.id.lsq_item_title);
		}
		return mTitleView;
	}
	
	public TuSdkImageView getBgImageView()
	{
		if ( mBgImageView == null )
		{
			mBgImageView = (TuSdkImageView)findViewById(R.id.lsq_mv_bg);
		}
		return mBgImageView;
	}

	public void viewNeedRest()
	{
		super.viewNeedRest();
		
		if ( this.getImageView() != null )
		{
			this.getImageView().setImageBitmap(null);

			StickerLocalPackage.shared().cancelLoadImage(this.getImageView());
		}
	}

	@Override
	public void onCellSelected(int position)
	{
		// 显示或隐藏边框
		getBorderView().setVisibility(View.VISIBLE);

	}

	@Override
	public void onCellDeselected() {
		// 显示或隐藏边框
		getBorderView().setVisibility(View.GONE);

	}
}
