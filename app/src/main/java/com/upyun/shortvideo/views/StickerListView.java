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

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import org.lasque.tusdk.core.secret.TuSDKOnlineStickerDownloader;
import org.lasque.tusdk.core.type.DownloadTaskStatus;
import org.lasque.tusdk.core.view.recyclerview.TuSdkTableView;
import org.lasque.tusdk.modules.view.widget.sticker.StickerGroup;

import java.util.List;

/**
 * @author Yanlin
 *
 */
public class StickerListView extends TuSdkTableView<StickerGroup, StickerCellView> implements TuSDKOnlineStickerDownloader.TuSDKOnlineStickerDownloaderDelegate, View.OnAttachStateChangeListener {
	/** 行视图宽度 */
	private int mCellWidth;

	private TuSDKOnlineStickerDownloader mDownLoader;

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
	 *
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

	/**
	 * 获取贴纸的位置
	 *
	 * @param groupId
	 * @return
	 */
	public int getStickerCellViewPostision(long groupId)
	{
		List<StickerGroup> groups = this.getModeList();

		if (groups == null) return -1;

		for (int i = 0 ; i < groups.size() ; i++)
		{
			StickerGroup group = groups.get(i);
			if (group.groupId == groupId)
				return i;
		}
		return -1;
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

		// 设置下载器实例，StickerCellView 用于判断该贴纸是否已被下载
		view.setStickerDownloader(getStickerDownLoader());
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
		view.addOnAttachStateChangeListener(this);
	}

	/**
	 * 获取贴纸下载器
	 * @return TuSDKOnlineStickerDownloader
	 */
	public TuSDKOnlineStickerDownloader getStickerDownLoader()
	{
		if (mDownLoader == null)
		{
			mDownLoader = new TuSDKOnlineStickerDownloader();
			mDownLoader.setDelegate(this);
		}

		return mDownLoader;
	}

	/**
	 * 判断贴纸是否已被下载到本地
	 * @param stickerGroup
	 * @return
	 */
	public boolean isDownloaded(StickerGroup stickerGroup)
	{
		return getStickerDownLoader().isDownloaded(stickerGroup.groupId);
	}

	/**
	 * 下载指定贴纸，如果贴纸正在下载中则不做任何操作
	 *
	 * @param stickerGroup 贴纸对象
	 */
	public void downloadStickerGroup(StickerGroup stickerGroup)
	{
		if (stickerGroup == null || getStickerDownLoader().isDownloading(stickerGroup.groupId)) return;

		getStickerDownLoader().downloadStickerGroup(stickerGroup);

		StickerCellView stickerCellView = (StickerCellView)getStickCellView(stickerGroup.groupId);

		stickerCellView.showProgressAnimation();
	}

	/**
	 * 贴纸下载进度变化
	 * @param stickerGroupId
	 * @param progress
	 * @param status
	 */
	@Override
	public void onDownloadProgressChanged(long stickerGroupId, float progress, DownloadTaskStatus status)
	{
		if (status == DownloadTaskStatus.StatusDowned || status == DownloadTaskStatus.StatusDownFailed )
		{
			int position = getStickerCellViewPostision(stickerGroupId);
			this.getAdapter().notifyItemChanged(position);
		}
	}

	@Override
	public void onViewAttachedToWindow(View view)
	{
		if (view instanceof StickerCellView && ((StickerCellView)view).isDownlowding())
			((StickerCellView)view).showProgressAnimation();
	}

	@Override
	public void onViewDetachedFromWindow(View view)
	{
		// 贴纸视图解绑时清楚动画，避免快速滑动造成动画卡死
		if (view instanceof StickerCellView)
			((StickerCellView)view).hideProgressAnimation();
	}
}
