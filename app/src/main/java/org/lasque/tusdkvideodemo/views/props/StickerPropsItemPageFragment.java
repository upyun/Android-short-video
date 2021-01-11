package org.lasque.tusdkvideodemo.views.props;

/******************************************************************
 * droid-sdk-video 
 * org.lasque.tusdkvideodemo.views.props
 *
 * @author sprint
 * @Date 2018/12/28 1:33 PM
 * @Copyright (c) 2018 tutucloud.com. All rights reserved.
 ******************************************************************/

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import org.lasque.tusdk.core.TuSdkContext;
import org.lasque.tusdk.core.secret.TuSDKOnlineStickerDownloader;
import org.lasque.tusdk.core.type.DownloadTaskStatus;
import org.lasque.tusdk.modules.view.widget.sticker.StickerGroup;
import org.lasque.tusdk.modules.view.widget.sticker.StickerLocalPackage;
import com.upyun.shortvideo.R;
import org.lasque.tusdkvideodemo.views.props.model.PropsItem;
import org.lasque.tusdkvideodemo.views.props.model.PropsItemSticker;

import java.util.List;

/** 贴纸道具Frament */
@SuppressLint("ValidFragment")
public class StickerPropsItemPageFragment extends PropsItemPageFragment {


    public StickerPropsItemPageFragment() {
    }

    public interface StickerItemDelegate<ItemData extends PropsItem> extends ItemDelegate{
        /**
         * 移除贴纸道具
         * @param propsItem
         */
        void removePropsItem(ItemData propsItem);
    }

    /** 道具列表 */
    private List<PropsItemSticker> mPropsItems;

    /** 贴纸下载器 */
    private TuSDKOnlineStickerDownloader mStickerDownloader;

    /** 当前选中 */
    private int mCurrentPosition = -1;
    /** 当前长按 */
    private int mCurrentLongPos = -1;

    private int itemPosition(long groupId) {

        for (int i = 0; i < mPropsItems.size() ; i++ )
            if (mPropsItems.get(i).getStickerGrop().groupId == groupId) return i;

        return -1;
    }



    /**
     * 初始化
     * @param pageIndex
     * @param items
     */
    public StickerPropsItemPageFragment(int pageIndex,List<PropsItemSticker> items) {
        super(pageIndex, null);
        this.setDataSource(mDataSource);
        this.mPropsItems = items;

        /** 初始化贴纸下载器 */
        mStickerDownloader = new TuSDKOnlineStickerDownloader();
        // 监听贴纸下载状态
        mStickerDownloader.setDelegate(mStickerDownloaderDelegate);
    }

    @Override
    public void notifyDataSetChanged() {
//        mCurrentPosition = -1;
        super.notifyDataSetChanged();
    }

    /**
     * 移除贴纸
     * @param position
     */
    private void removeSticker(final int position){
        AlertDialog.Builder adBuilder = new AlertDialog.Builder(getContext(), android.R.style.Theme_Material_Dialog_Alert);
        adBuilder.setTitle(R.string.lsq_remove_sticker_title);
        adBuilder.setNegativeButton(R.string.lsq_remove_sticker_cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mCurrentLongPos = -1;
                dialog.dismiss();
                notifyItemChanged(position);
            }
        });

        adBuilder.setPositiveButton(R.string.lsq_remove_sticker_confirm, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                StickerGroup itemData = mDataSource.itemData(position).getStickerGrop();

                if(mStickerDownloader.isDownloaded(itemData.groupId)){
                    StickerLocalPackage.shared().removeDownloadWithIdt(itemData.groupId);
                    if(getItemDelegate() != null && mCurrentLongPos == position)  //避免移除错误
                        ((StickerItemDelegate)getItemDelegate()).removePropsItem(mDataSource.itemData(position));
                    notifyItemChanged(position);
                }
                mCurrentLongPos = -1;
                dialog.dismiss();
            }
        });
        adBuilder.show();
    }


    // -----------------------   StickerPropsItemPageFragment.DataSource  ----------------//

    private DataSource<StickerPropsItemPageFragment.PropsItemStickerViewHolder,PropsItemSticker> mDataSource = new DataSource<StickerPropsItemPageFragment.PropsItemStickerViewHolder,PropsItemSticker>() {

        /** 返回道具数量 */
        @Override
        public int itemCount(int pageIndex) {
            return mPropsItems.size();
        }

        /** 获取道具数据 */
        @Override
        public PropsItemSticker itemData(int position) {
            return mPropsItems.get(position);
        }

        /** 创建 PropsItemStickerViewHolder  */
        @Override
        public PropsItemStickerViewHolder onCreateViewHolder(ViewGroup viewGroup, int position) {

            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.sticker_list_cell_view,null);
            PropsItemStickerViewHolder viewHolder = new PropsItemStickerViewHolder(view);
            return viewHolder;
        }
    };

    // -------------------------  TuSDKOnlineStickerDownloader.TuSDKOnlineStickerDownloaderDelegate --------- //

    private TuSDKOnlineStickerDownloader.TuSDKOnlineStickerDownloaderDelegate mStickerDownloaderDelegate = new TuSDKOnlineStickerDownloader.TuSDKOnlineStickerDownloaderDelegate() {
        @Override
        public void onDownloadProgressChanged(long stickerGroupId, float progress, DownloadTaskStatus status) {

            if (status == DownloadTaskStatus.StatusDowned || status == DownloadTaskStatus.StatusDownFailed )
            {
                int position = itemPosition(stickerGroupId);

                if(position != -1 && mCurrentPosition == position)
                     if(getItemDelegate() != null)
                         getItemDelegate().didSelectPropsItem(mDataSource.itemData(position));

                notifyDataSetChanged();

            }

        }
    };

    // -----------------------   PropsItemStickerViewHolder  ---------------- //

    /* 贴纸道具 ViewHolder */
    public class PropsItemStickerViewHolder extends PropsItemViewHolder<PropsItemSticker>
    {
        /** 缩略图 */
        public ImageView mThumbImageView;
        public ImageView mDownloadStateImage;
        public ImageView mLoadProgressImage;
        public ImageView mDelStickerImage;
        public View mItemWrap;

        public PropsItemStickerViewHolder(View itemView) {
            super(itemView);
            mThumbImageView = itemView.findViewById(R.id.lsq_item_image);
            mDownloadStateImage = itemView.findViewById(R.id.lsq_item_state_image);
            mLoadProgressImage = itemView.findViewById(R.id.lsq_progress_image);
            mItemWrap = itemView.findViewById(R.id.lsq_item_wrap);
            mDelStickerImage = itemView.findViewById(R.id.lsq_item_remove);
        }

        /**
         * 判断当前贴纸是否正在下中
         *
         * @param groupId 贴纸组id
         * @return
         */
        private boolean isDownloading(long groupId)
        {
            return mStickerDownloader != null && mStickerDownloader.containsTask(groupId);
        }

        /**
         * 显示进度动画
         */
        private void showProgressAnimation(ImageView view)
        {
            if (view == null) return;

            view.setVisibility(View.VISIBLE);
            RotateAnimation rotate  = new RotateAnimation(0f, 360f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
            LinearInterpolator lin = new LinearInterpolator();
            rotate.setInterpolator(lin);
            rotate.setDuration(2000);
            rotate.setRepeatCount(-1);
            rotate.setFillAfter(true);

            view.setAnimation(rotate);

        }

        /**
         * 处理长按事件
         * @param position 长按位置
         */
        private void handleLongClickEvent(int position) {
            notifyItemChanged(mCurrentLongPos);
            mCurrentLongPos = position;
            notifyItemChanged(position);
        }

        /**
         * 处理点击事件
         *
         * @param position 单价位置
         */
        private void handleClickEvent(int position) {

            StickerGroup itemData = mDataSource.itemData(position).getStickerGrop();

            // 如果贴纸已被下载到本地
            if (mStickerDownloader.isDownloaded(itemData.groupId)) {

                if(mCurrentLongPos == position)
                {
                    removeSticker(position);

                }else {

                  if (getItemDelegate() != null)
                      getItemDelegate().didSelectPropsItem(mDataSource.itemData(position));
                  mCurrentPosition = position;
                }
            } else {
                mStickerDownloader.downloadStickerGroup(itemData);
                mCurrentPosition = position;
            }

            notifyItemChanged(position);
        }

        /**
         * 绑定数据
         *
         * @param propsItem
         */
        @Override
        public void bindModel(PropsItemSticker propsItem, final int position) {

            StickerGroup model = propsItem.getStickerGrop();

            // 已下载到本地
            boolean isContains = StickerLocalPackage.shared().containsGroupId(model.groupId);

            mLoadProgressImage.setVisibility(View.GONE);
            mDelStickerImage.setVisibility(View.GONE);
            mDownloadStateImage.setVisibility(View.GONE);

            if (isContains) {

                StickerLocalPackage.shared().loadGroupThumb(StickerLocalPackage.shared().getStickerGroup(model.groupId), mThumbImageView);

                /** 长按后UI处理 */
                if (position == mCurrentLongPos) {
                    mItemWrap.setBackground(TuSdkContext.getDrawable(R.drawable.sticker_cell_remove_background));
                    mDelStickerImage.setVisibility(View.VISIBLE);
                }

            } else
            {
                Glide.with(TuSdkContext.context()).load(model.getPreviewNamePath()).into(mThumbImageView);

                if (this.isDownloading(model.groupId))
                    showProgressAnimation(mLoadProgressImage);
                else
                    mDownloadStateImage.setVisibility(View.VISIBLE);
            }

            /** 点击选中处理 */
            mItemWrap.setBackground(getItemDelegate().propsItemUsed(propsItem) ? TuSdkContext.getDrawable(R.drawable.sticker_cell_background) : null);

            /** 点击选中事件 */
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    handleClickEvent(position);
                }
            });

            /** 点击长按事件 */
            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    handleLongClickEvent(position);
                    return true;
                }
            });

        }
    }
}

