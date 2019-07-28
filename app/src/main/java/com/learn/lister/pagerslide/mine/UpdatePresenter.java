package com.learn.lister.pagerslide.mine;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import androidx.fragment.app.FragmentActivity;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.learn.lister.pagerslide.data.MyMusic;
import com.learn.lister.pagerslide.utils.Music;

import java.util.ArrayList;
import java.util.List;

import static com.learn.lister.pagerslide.data.MyMusic.ChosenMusicList;
import static com.learn.lister.pagerslide.data.MyMusic.i;
import static com.learn.lister.pagerslide.data.MyMusic.myMusicLists;
import static com.learn.lister.pagerslide.data.MyMusic.totalList;

public class UpdatePresenter implements UpdateContract.UpdatePresenter{
    public void save(FragmentActivity activity) {
        Gson gson = new Gson();
        String songList = gson.toJson(totalList);
        Log.d("&&&&&&",songList);
        Gson gson1 = new Gson();
        String titleList = gson.toJson(MyMusic.myMusicLists);
        Log.d("&&&&&&","song title "+ titleList);
        SharedPreferences.Editor editor = activity.getSharedPreferences("information", Context.MODE_PRIVATE).edit();
        editor.putString("data",songList);
        editor.putString("titles",titleList);
        editor.apply();
        editor.commit();
    }
    public void read(FragmentActivity activity,OnUpdateListener onUpdateListener) {
        SharedPreferences preferences = activity.getSharedPreferences("information", Context.MODE_PRIVATE);
        String songList = preferences.getString("data","");
        String titleList = preferences.getString("titles","");
        Log.d("&&&&&&","get data "+songList);
        if (songList.equals("") ){
            Toast.makeText(activity,"null",Toast.LENGTH_SHORT).show();

        }
        else {
            List<List<Music>> list = new ArrayList<>();
            MyMusic.totalList = list;
            List<String> list1 = new ArrayList<>();
            MyMusic.myMusicLists = list1;
            Gson gson2 = new Gson();
            MyMusic.myMusicLists = gson2.fromJson(titleList,new TypeToken<List<String>>(){}.getType());
            Gson gson3 = new Gson();
            totalList = gson3.fromJson(songList, new TypeToken<List<List<Music>>>(){}.getType());

            ChosenMusicList = totalList.get(0);
            if (myMusicLists.size()>1){
                i = 1;
                while (i<myMusicLists.size())
                    onUpdateListener.onSuccess();
            }
        }MyMusic.totalNumberOfList = totalList.size()-1;
        Log.d("<<<>>>","fromjson");
    }
}
