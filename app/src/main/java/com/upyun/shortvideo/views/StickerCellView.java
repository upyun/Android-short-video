/**
 * TuSDKVideoDemo
 * StickerCellView.java
 *
 * @author  Yanlin
 * @Date  Jan 5, 2017 6:06:07 PM
 * @Copright (c) 2016 tusdk.com. All rights reserved.
 *
 */
package com.upyun.shortvideo.views;

import org.lasque.tusdk.core.TuSdkContext;
import org.lasque.tusdk.core.view.listview.TuSdkCellRelativeLayout;
import org.lasque.tusdk.modules.view.widget.sticker.StickerGroup;
import org.lasque.tusdk.modules.view.widget.sticker.StickerLocalPackage;

import com.nostra13.universalimageloader.core.ImageLoader;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;

/**
 * @author Yanlin
 *
 */
public class StickerCellView extends TuSdkCellRelativeLayout<StickerGroup>
{

	/** 缩略图 */
	private ImageView mThumbView;
	
	/** 下载图标 */
	private ImageView mDownloadView;
	
	public StickerCellView(Context context, AttributeSet attrs, int defStyle)
	{
		super(context, attrs, defStyle);
	}

	public StickerCellView(Context context, AttributeSet attrs)
	{
		super(context, attrs);
	}

	public StickerCellView(Context context)
	{
		super(context);
	}

	@Override
	protected void bindModel() 
	{
		StickerGroup model = this.getModel();
		
		if (model == null || getImageView() == null) return;

		// 贴纸栏上的禁用按钮
		if((Integer)getTag() == 0)
		{
			getImageView().setImageResource(TuSdkContext.getDrawableResId("lsq_style_default_btn_sticker_off"));
			return;
		}
		
		boolean isContains = StickerLocalPackage.shared().containsGroupId(model.groupId);
		ImageView iconImageView = getImageView();
		ImageView downloadView = getDownloadImageView();
		if (isContains)
		{
			model = StickerLocalPackage.shared().getStickerGroup(model.groupId);	
			StickerLocalPackage.shared().loadGroupThumb(model, this.getImageView());
			downloadView.setVisibility(View.GONE);
		}
		else
		{
			ImageLoader.getInstance().displayImage(model.getPreviewNamePath(), iconImageView);
			downloadView.setVisibility(View.VISIBLE);
		}
	}
	
	private ImageView getImageView()
	{
		if (mThumbView == null)
			mThumbView = (ImageView)findViewById(com.upyun.shortvideo.R.id.lsq_item_image);
		
		return mThumbView;
	}
	
	private ImageView getDownloadImageView()
	{
		if (mDownloadView == null)
			mDownloadView = (ImageView)findViewById(com.upyun.shortvideo.R.id.lsq_item_state_image);
		
		return mDownloadView;
	}
	
	public void viewNeedRest()
	{
		super.viewNeedRest();
		
		if (this.getImageView() != null)
		{
			this.getImageView().setImageBitmap(null);

			StickerLocalPackage.shared().cancelLoadImage(this.getImageView());
		}
	}

}



