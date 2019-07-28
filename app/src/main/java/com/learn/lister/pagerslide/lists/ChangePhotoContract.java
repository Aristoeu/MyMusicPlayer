package com.learn.lister.pagerslide.lists;

import android.content.Intent;
import android.net.Uri;

public interface ChangePhotoContract {
    interface ChangePhotoPresenter{
        void openAlbum();
        void handleImageOnKitKat(Intent data);
        void handleImageBeforeKitKat(Intent data);
    }
    interface ChangePhotoView{
        void displayImage(String imagePath);
        void initView();
    }
    interface ChangePhotoModel{
        String getImagePath(Uri uri, String selection);
    }
}
