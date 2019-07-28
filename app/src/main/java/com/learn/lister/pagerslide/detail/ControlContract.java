package com.learn.lister.pagerslide.detail;

import android.view.View;

public interface ControlContract {
    interface ControlModel{
        void play(View view);
        void pre(View view);
        void next(View view);
    }
    interface ControlView{
        void updateUI();
        void updateProgress();
        void updatePlayText();
        void initView();
        void getUI();
    }

}
