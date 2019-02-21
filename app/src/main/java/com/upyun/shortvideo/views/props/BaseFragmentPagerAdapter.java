package com.upyun.shortvideo.views.props;

/******************************************************************
 * droid-sdk-video 
 * org.lasque.tusdkvideodemo.views.props
 *
 * @author sprint
 * @Date 2018/12/28 11:02 AM
 * @Copyright (c) 2018 tutucloud.com. All rights reserved.
 ******************************************************************/

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import org.lasque.tusdk.core.utils.TLog;

import java.util.Collection;
import java.util.HashMap;

/**
 *  Page 适配器基础类
 */
public class BaseFragmentPagerAdapter<F extends Fragment> extends FragmentPagerAdapter {

    /* 数据源委托对象 */
    public static interface DataSource <F> {

        F frament(int pageIndex);

        int pageCount();
    }

    /** 数据源 */
    private DataSource<F> mDataSource;

    private HashMap<Integer,F> mPageFramentMap  = new HashMap<>();


    public BaseFragmentPagerAdapter(FragmentManager fm, DataSource<F> dataSource) {
        super(fm);
        this.mDataSource = dataSource;
    }

    public Collection<F> allPages() {
        return mPageFramentMap.values();
    }

    @Override
    public F getItem(int i) {
        TLog.i("getItem "+i);
        if (mDataSource != null) {
            F f = mDataSource.frament(i);
            mPageFramentMap.put(i,f);
            return f;
        }

        return null;
    }

    @Override
    public int getCount() {
        if (mDataSource != null) return mDataSource.pageCount();
        return 0;
    }
}

