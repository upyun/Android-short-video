package org.lasque.tusdkvideodemo.views.newFilterUI;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import java.util.List;

/**
 * TuSDK
 * TuSDKFilterEngineModule$
 *
 * @author H.ys
 * @Date 2020/07/08$ 10:06$
 * @Copyright (c) 2019 tusdk.com. All rights reserved.
 */
public class FilterViewPagerAdapter extends FragmentStateAdapter {

    private List<FilterFragment> mFragments;


    public FilterViewPagerAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle, List<FilterFragment> fragments) {
        super(fragmentManager, lifecycle);
        this.mFragments = fragments;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return mFragments.get(position);
    }

    @Override
    public int getItemCount() {
        return mFragments.size();
    }
}
