package org.lasque.tusdkvideodemo.views.props;

/******************************************************************
 * droid-sdk-video 
 * org.lasque.tusdkvideodemo.views.props
 *
 * @author sprint
 * @Date 2018/12/28 11:02 AM
 * @Copyright (c) 2018 tutucloud.com. All rights reserved.
 ******************************************************************/

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;


import org.lasque.tusdk.core.utils.TLog;

import java.util.Collection;
import java.util.HashMap;

/**
 *  Page 适配器基础类
 */
public class BaseFragmentPagerAdapter<F extends Fragment> extends FragmentStateAdapter {

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        TLog.i("getItem "+position);
        if (mDataSource != null) {
            F f = mDataSource.frament(position);
            mPageFramentMap.put(position,f);
            return f;
        }
        return null;
    }

    @Override
    public int getItemCount() {
        if (mDataSource != null) return mDataSource.pageCount();
        return 0;
    }

    /* 数据源委托对象 */
    public static interface DataSource <F> {

        F frament(int pageIndex);

        int pageCount();
    }

    /** 数据源 */
    private DataSource<F> mDataSource;

    private HashMap<Integer,F> mPageFramentMap  = new HashMap<>();


    public BaseFragmentPagerAdapter(FragmentManager fm, Lifecycle lifecycle, DataSource<F> dataSource) {
        super(fm,lifecycle);
        this.mDataSource = dataSource;
    }

    public Collection<F> allPages() {
        return mPageFramentMap.values();
    }
}

