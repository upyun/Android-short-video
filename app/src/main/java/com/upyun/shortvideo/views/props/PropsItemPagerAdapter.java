package com.upyun.shortvideo.views.props;

import android.support.v4.app.FragmentManager;

import java.util.Collection;

/******************************************************************
 * droid-sdk-video 
 * org.lasque.tusdkvideodemo.views.props
 *
 * @author sprint
 * @Date 2018/12/28 11:15 AM
 * @Copyright (c) 2018 tutucloud.com. All rights reserved.
 ******************************************************************/
public class PropsItemPagerAdapter<F extends PropsItemPageFragment> extends BaseFragmentPagerAdapter<F> {


    public PropsItemPagerAdapter(FragmentManager fm, DataSource<F> dataSource) {
        super(fm, dataSource);
    }

    /**
     * 刷新所有页面数据
     */
    public void notifyAllPageData() {

        Collection<F> pages = this.allPages();
        for (F page : pages) {
            page.notifyDataSetChanged();
        }
    }


}

