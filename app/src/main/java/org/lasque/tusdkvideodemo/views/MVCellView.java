/**
 * TuSDKVideoDemo
 * MVCellView.java
 *
 * @author  LiuHang
 * @Date  May 5, 2017 6:06:07 PM
 * @Copright (c) 2017 tusdk.com. All rights reserved.
 *
 */
package org.lasque.tusdkvideodemo.views;

import org.lasque.tusdk.core.TuSdkContext;
import org.lasque.tusdk.core.view.TuSdkImageView;
import org.lasque.tusdk.core.view.listview.TuSdkCellRelativeLayout;
import org.lasque.tusdk.modules.view.widget.sticker.StickerGroup;
import org.lasque.tusdk.modules.view.widget.sticker.StickerLocalPackage;
import org.lasque.tusdkvideodemo.R;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * 自定义的MV列表单元视图
 * @author LiuHang
 *
 */
public class MVCellView extends TuSdkCellRelativeLayout<StickerGroup>
{
	/** 缩略图 */
	private ImageView mThumbView;
	/** MV 名称 */
	private TextView mTitleView;
	
	/** MV 边框 */
	private RelativeLayout mMvBorderView ;
	/** MV 列表的背景图片 */
	private TuSdkImageView mBgImageView;

	public MVCellView(Context context, AttributeSet attrs, int defStyle)
	{
		super(context, attrs, defStyle);
	}

	public MVCellView(Context context, AttributeSet attrs)
	{
		super(context, attrs);
	}

	public MVCellView(Context context)
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
			getTitleView().setText(TuSdkContext.getString("lsq_mv_" + model.name));
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

	private RelativeLayout mMvNoneLayout;
	
	public RelativeLayout getMvNoneLayout()
	{
		if ( mMvNoneLayout == null )
		{
			mMvNoneLayout = (RelativeLayout)findViewById(R.id.lsq_mv_none_Wrap);
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
}
