package org.lasque.tusdkvideodemo.views.props;

import android.content.Context;
import android.graphics.Rect;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.lasque.tusdk.core.TuSdkContext;
import org.lasque.tusdkvideodemo.views.props.model.PropsItem;

/******************************************************************
 * droid-sdk-video 
 * org.lasque.tusdkvideodemo.views.props
 *
 * @author sprint
 * @Date 2018/12/28 11:15 AM
 * @Copyright (c) 2018 tutucloud.com. All rights reserved.
 ******************************************************************/
/**
 * 道具页面Fragment
 */
public abstract class PropsItemPageFragment extends Fragment {

    /** 显示道具视图 */
    private RecyclerView mPropsItemsRecyclerView;
    /** 道具视频适配器 */
    private PropsItemRecycleAdapter mPropsItemRecycleAdapter;

    /** 数据源 */
    private DataSource mDataSource;
    /** 事件委托 */
    private ItemDelegate mItemDelegate;

    /** 当前页面索引 */
     int mPageIndex;

    public PropsItemPageFragment(int pageIndex,DataSource dataSource){
        this.mPageIndex = pageIndex;
        this.mDataSource = dataSource;
    }

    public PropsItemPageFragment() {
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    /**
     * 设置数据源
     * @param dataSource 数据源
     */
    public void setDataSource(DataSource dataSource) {
        this.mDataSource = dataSource;
    }

    /**
     * 设置事件委托
     * @param itemDelegate 事件委托
     */
    public void setItemDelegate(ItemDelegate itemDelegate) {
        this.mItemDelegate = itemDelegate;
    }

    /**
     * 获取设置的事件委托
     *
     * @return ItemDelegate
     */
    public ItemDelegate getItemDelegate() {
        return mItemDelegate;
    }

    /**
     * 刷新指定位置的数据
     *
     * @param position 位置索引
     */
    public void notifyItemChanged(int position) {
        if (mPropsItemRecycleAdapter != null)
            mPropsItemRecycleAdapter.notifyItemChanged(position);
    }

    /**
     * 刷新数据
     */
    public void notifyDataSetChanged() {
        if (mPropsItemRecycleAdapter != null)
            mPropsItemRecycleAdapter.notifyDataSetChanged();
    }

    // -----------------------   StickerPropsItemPageFragment.DataSource  ----------------//

    /** 数据源 */
    public interface DataSource<VH extends PropsItemViewHolder,ItemData extends PropsItem> {

        int itemCount(int pageIndex);
        ItemData itemData(int position);
        VH onCreateViewHolder(ViewGroup viewGroup, int position);
    }

    /** 数据委托 */
    public interface ItemDelegate <ItemData extends PropsItem> {

        /**
         * 选择一个道具回调
         *
         * @param propsItem
         */
        void didSelectPropsItem(ItemData propsItem);

        /**
         * 当前道具是否正在被使用
         *
         * @param propsItem 道具
         * @return
         */
        boolean propsItemUsed(ItemData propsItem);

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        mPropsItemsRecyclerView = new RecyclerView(getActivity());

        mPropsItemsRecyclerView.setPadding(
                TuSdkContext.dip2px(21f),
                TuSdkContext.dip2px(10f),
                TuSdkContext.dip2px(21f),
                TuSdkContext.dip2px(10f)
        );

        GridLayoutManager grid = new GridLayoutManager(getActivity(), 5);
        mPropsItemsRecyclerView.setLayoutManager(grid);
        mPropsItemsRecyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                super.getItemOffsets(outRect, view, parent, state);
                outRect.bottom = 30;
                outRect.left = 30;
            }
        });

        mPropsItemRecycleAdapter = new PropsItemRecycleAdapter(getActivity());
        mPropsItemsRecyclerView.setAdapter(mPropsItemRecycleAdapter);
        return mPropsItemsRecyclerView;
    }

    /**
     * 道具项视图 Holder
     */
    public abstract static class PropsItemViewHolder<ItemData>  extends RecyclerView.ViewHolder {

        protected View mItemView;

        public PropsItemViewHolder(View itemView) {
            super(itemView);
            mItemView = itemView;
        }

        /**
         * 绑定道具模型数据
         *
         * @param item 模型数据
         */
        public abstract void bindModel(ItemData item,int position);

    }

    /** 道具项视图适配器 */
    public class PropsItemRecycleAdapter extends RecyclerView.Adapter <PropsItemViewHolder> {

        public PropsItemRecycleAdapter(Context context) {
            super();
        }

        @Override
        public int getItemCount() {
            return mDataSource.itemCount(mPageIndex);
        }

        @Override
        public PropsItemViewHolder onCreateViewHolder(ViewGroup viewGroup, int position) {
            return mDataSource.onCreateViewHolder(viewGroup,position);
        }

        @Override
        public void onBindViewHolder(PropsItemViewHolder viewHolder, int position) {
            viewHolder.bindModel(mDataSource.itemData(position),position);
        }

    }
}
