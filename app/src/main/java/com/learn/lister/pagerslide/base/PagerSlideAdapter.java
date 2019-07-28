package com.learn.lister.pagerslide.base;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import 	androidx.fragment.app.FragmentPagerAdapter;

import java.util.List;

/**
 * Fragment 滑动适配器
 */

public class PagerSlideAdapter extends FragmentPagerAdapter {

    private List<BaseFragment> mFragmentList;

    public PagerSlideAdapter(FragmentManager fm, List<BaseFragment> fragmentList) {
        super(fm);
        this.mFragmentList = fragmentList;
    }

    @Override
    public Fragment getItem(int position) {
        return mFragmentList.get(position);
    }

    @Override
    public int getCount() {
        return mFragmentList.size();
    }
}
