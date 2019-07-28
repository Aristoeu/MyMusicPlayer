package com.learn.lister.pagerslide.mine;

import androidx.fragment.app.FragmentActivity;

public interface UpdateContract {
    interface UpdateView{
        void updateUI(int m);
        void updatePhoto();
    }
interface UpdatePresenter{
    void save(FragmentActivity activity);
    void read(FragmentActivity activity,OnUpdateListener onUpdateListener);
}

}
