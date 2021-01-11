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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import org.lasque.tusdk.core.TuSdkContext;
import com.upyun.shortvideo.R;
import org.lasque.tusdkvideodemo.views.props.model.PropsItemMonster;

import java.util.List;

/** 哈哈镜道具Frament */
@SuppressLint("ValidFragment")
public class PropsItemMonsterPageFragment extends PropsItemPageFragment {

    /** 道具列表 */
    private List<PropsItemMonster> mPropsItems;


    /**
     * 初始化
     * @param pageIndex
     * @param items
     */
    public PropsItemMonsterPageFragment(int pageIndex, List<PropsItemMonster> items) {
        super(pageIndex, null);
        this.setDataSource(mDataSource);
        this.mPropsItems = items;
    }

    // -----------------------   PropsItemMonsterPageFragment.DataSource  ----------------//

    private DataSource<PropsItemMonsterPageFragment.PropsItemMonsterViewHolder,PropsItemMonster> mDataSource = new DataSource<PropsItemMonsterPageFragment.PropsItemMonsterViewHolder,PropsItemMonster>() {

        /** 返回道具数量 */
        @Override
        public int itemCount(int pageIndex) {
            return mPropsItems.size();
        }

        /** 获取道具数据 */
        @Override
        public PropsItemMonster itemData(int position) {
            return mPropsItems.get(position);
        }

        /** 创建 PropsItemStickerViewHolder  */
        @Override
        public PropsItemMonsterViewHolder onCreateViewHolder(ViewGroup viewGroup, int position) {

            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.sticker_list_cell_view,null);
            PropsItemMonsterViewHolder viewHolder = new PropsItemMonsterViewHolder(view);
            return viewHolder;
        }
    };

    public PropsItemMonsterPageFragment() {
    }

    // -----------------------   PropsItemStickerViewHolder  ---------------- //

    /* 贴纸道具 ViewHolder */
    public class PropsItemMonsterViewHolder extends PropsItemViewHolder<PropsItemMonster>
    {
        /** 缩略图 */
        public ImageView mThumbImageView;
        public View mItemWrap;

        public PropsItemMonsterViewHolder(View itemView) {
            super(itemView);
            mThumbImageView = itemView.findViewById(R.id.lsq_item_image);
            mItemWrap = itemView.findViewById(R.id.lsq_item_wrap);
        }


        /**
         * 处理点击事件
         *
         * @param position 单价位置
         */
        private void handleClickEvent(int position) {

            if (getItemDelegate() != null)
                getItemDelegate().didSelectPropsItem(mDataSource.itemData(position));

        }

        /**
         * 绑定数据
         *
         * @param propsItem
         */
        @Override
        public void bindModel(PropsItemMonster propsItem, final int position) {

            mThumbImageView.setImageDrawable(TuSdkContext.getDrawable(String.format("lsq_ic_face_monster_%s",propsItem.getThumbName())));
            /** 点击选中处理 */
            mItemWrap.setBackground(getItemDelegate().propsItemUsed(propsItem) ? TuSdkContext.getDrawable(R.drawable.sticker_cell_background) : null);

            /** 点击选中事件 */
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    handleClickEvent(position);
                }
            });

        }
    }
}

