package com.learn.lister.pagerslide.data;

import android.graphics.Bitmap;
import android.media.MediaPlayer;

import com.learn.lister.pagerslide.Lrc.LrcView;
import com.learn.lister.pagerslide.Lrc.OnSingleTapListener;
import com.learn.lister.pagerslide.utils.Music;

import java.util.ArrayList;
import java.util.List;

public class MyMusic {
    public static MediaPlayer myPlayer = new MediaPlayer();
    public static LrcView lrcView;
    public static List<Music> LocalMusicList = new ArrayList<>();
    public static List<Music> OnlineMusicList = new ArrayList<>();
    public static int isPlayingFromInternet = 0;
    public static List<Music> ChosenMusicList = new ArrayList<>();
    public static int MyIndexOfChosenMusic = 0;
    public static int myPlayMode = 0;
    public static int totalNumberOfList = 0;
    public static int IndexOfMusicLists = 0;
    public static List<List<Music>> totalList = new ArrayList<>();
    public static List<String> myMusicLists = new ArrayList<>();
    public static List<Bitmap> photosForLists = new ArrayList<>();
    public static int temperateNumberI = -1;
    public static int i = 1;
    public static boolean fistClick = true;
    public static OnSingleTapListener onSingleTapListener;
    public static String downloadingMusic;
    public static String lrc = "[00:00.000] 没找到歌词\n" ;
}
