package org.lasque.tusdkvideodemo.views;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import java.util.List;

/**
 * 特效类适配器
 * @author xujie
 * @Date 2018/9/29
 */
public class EffectComponetAdapter<T extends Fragment> extends FragmentPagerAdapter {

    private List<T> mFragment;

    public EffectComponetAdapter(FragmentManager fm, List<T> fragment) {
        super(fm);
        this.mFragment = fragment;
    }


    @Override
    public int getCount() {
        return mFragment.size();
    }

    @Override
    public Fragment getItem(int i) {
        return mFragment.get(i);
    }

}
