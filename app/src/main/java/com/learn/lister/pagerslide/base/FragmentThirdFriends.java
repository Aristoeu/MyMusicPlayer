package com.learn.lister.pagerslide.base;

import android.os.Bundle;
import android.view.View;
import android.widget.ScrollView;

import androidx.annotation.Nullable;

import com.learn.lister.pagerslide.R;



public class FragmentThirdFriends extends BaseFragment {
    @Override
    public View initView() {
        View view = View.inflate(mContext, R.layout.fragment3, null);

        return view;
    }


}
