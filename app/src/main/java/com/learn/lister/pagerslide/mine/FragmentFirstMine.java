package com.learn.lister.pagerslide.mine;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.learn.lister.pagerslide.base.BaseFragment;
import com.learn.lister.pagerslide.data.MyMusic;
import com.learn.lister.pagerslide.R;
import com.learn.lister.pagerslide.lists.ChosenMusicActivity;
import com.learn.lister.pagerslide.lists.MusicListActivity;
import com.learn.lister.pagerslide.popupwindows.NewListWindow;
import com.learn.lister.pagerslide.utils.Music;

import java.util.ArrayList;
import java.util.List;

import static com.learn.lister.pagerslide.data.MyMusic.ChosenMusicList;
import static com.learn.lister.pagerslide.data.MyMusic.IndexOfMusicLists;
import static com.learn.lister.pagerslide.data.MyMusic.i;
import static com.learn.lister.pagerslide.data.MyMusic.myMusicLists;
import static com.learn.lister.pagerslide.data.MyMusic.photosForLists;
import static com.learn.lister.pagerslide.data.MyMusic.totalList;

/**
 * Created by Aristo in 2019
 */

public class FragmentFirstMine extends BaseFragment implements UpdateContract.UpdateView {
    private TextView textView,loveMusic,addPlus;
    private ImageView imageView,default_photo,imageView1,imageView2,imageView3,imageView4,imageView5;
    private LinearLayout mContainer;
    public static final String INTENT_BROADCAST = "android.intent.action.MEDICAL_BROADCAST";
    public static final String CHANGE_PHOTO_BROADCAST = "android.intent.action.CHANGE_PHOTO";
    public static final int CLOSE_SEARCH_MSG=1;
    NewListWindow newListWindow;
    UpdatePresenter updatePresenter = new UpdatePresenter();
    //private MediaPlayer mediaPlayer = new MediaPlayer();
    public Handler mHandler = new Handler(){
        public void handleMessage(Message msg){
            switch (msg.what){
                case CLOSE_SEARCH_MSG:
                    updateUI(MyMusic.totalNumberOfList);
                    break;
            }
        }
    };
    private OnUpdateListener onUpdateListener = new OnUpdateListener() {
        @Override
        public void onSuccess() {
            updateUI(i);
        }
    };

    View view;
    @Override
    public View initView() {
        View view = View.inflate(mContext, R.layout.fragment1, null);
        return view;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view=inflater.inflate(R.layout.fragment1,null);
        textView = view.findViewById(R.id.localMusic);
        imageView = view.findViewById(R.id.local_music_icn);
        loveMusic = view.findViewById(R.id.loveMusic);
        addPlus = view.findViewById(R.id.add_plus);
        mContainer = view.findViewById(R.id.mContainer);
        default_photo = view.findViewById(R.id.default_love_list);
        return view;

    }
    public void updateUI(int m) {
        if (i <= 5){
            view = View.inflate(getContext(), R.layout.like_list, null);
            TextView textView = view.findViewById(R.id.loveMusic);
            textView.setText(MyMusic.myMusicLists.get(m));

            textView.setOnClickListener(new View.OnClickListener() {
                int m = i;
                @Override
                public void onClick(View view) {
                    Toast.makeText(getContext(), "响应" + m, Toast.LENGTH_SHORT).show();
                    MyMusic.IndexOfMusicLists = m;
                    if (!MyMusic.totalList.get(IndexOfMusicLists).isEmpty()) {
                        startActivity(new Intent(getActivity(), ChosenMusicActivity.class));
                    }
                    else Toast.makeText(getContext(), "你还没有收藏歌哦", Toast.LENGTH_SHORT).show();
                    if (MyMusic.totalList.get(0) == ChosenMusicList)
                        Log.d("<<<>>>", "equal");
                    else Log.d("<<<>>>", "not equal");
                }
            });
            switch (i) {
                case 1:
                    imageView1 = view.findViewById(R.id.photos);
                    //imageView1.setId(R.id.text_view_1);
                    break;
                case 2:
                    imageView2 = view.findViewById(R.id.photos);
                  //  textView.setId(R.id.text_view_2);
                    break;
                case 3:
                    imageView3 = view.findViewById(R.id.photos);
                 //   textView.setId(R.id.text_view_3);
                    break;
                case 4:
                    imageView4 = view.findViewById(R.id.photos);
                 //   textView.setId(R.id.text_view_4);
                    break;
                case 5:
                    imageView5 = view.findViewById(R.id.photos);
                  //  textView.setId(R.id.text_view_5);
                    break;
            }i++;
            mContainer.addView(view);

    }else {Toast.makeText(getContext(),"只能建5个歌单哦",Toast.LENGTH_SHORT).show();}
    }
    public void updatePhoto(){
        switch (IndexOfMusicLists){
            case 0:default_photo.setImageBitmap(photosForLists.get(IndexOfMusicLists));break;
            case 1:imageView1.setImageBitmap(photosForLists.get(IndexOfMusicLists));break;
            case 2:imageView2.setImageBitmap(photosForLists.get(IndexOfMusicLists));break;
            case 3:imageView3.setImageBitmap(photosForLists.get(IndexOfMusicLists));break;
            case 4:imageView4.setImageBitmap(photosForLists.get(IndexOfMusicLists));break;
            case 5:imageView5.setImageBitmap(photosForLists.get(IndexOfMusicLists));break;
        }
    }
    @Override
    public void onDestroy() {
        updatePresenter.save(getActivity());
        // save();
        super.onDestroy();
    }
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {

        super.onActivityCreated(savedInstanceState);
        updatePresenter.read(getActivity(),onUpdateListener);
        //read();
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setClass(getActivity(), MusicListActivity.class);
                startActivity(intent);
            }
        });
        loveMusic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!MyMusic.ChosenMusicList.isEmpty())
                {
                    for (int i=0;i<ChosenMusicList.size();i++)
                        Log.d("<<<>>>",ChosenMusicList.get(i).name);
                    IndexOfMusicLists = 0;
                    startActivity(new Intent(getActivity(), ChosenMusicActivity.class));
                }
                else {Log.d("<<<>>>","null");
                    Toast.makeText(getContext(),"你还没有收藏歌哦",Toast.LENGTH_SHORT).show();}
            }
        });
        addPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), NewListWindow.class));
            }
        });
        LocalBroadcastManager broadcastManager = LocalBroadcastManager.getInstance(getActivity());
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(INTENT_BROADCAST);
        intentFilter.addAction(CHANGE_PHOTO_BROADCAST);
        BroadcastReceiver bordcastReceiver = new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent) {
                //信息处理
                switch (intent.getAction()){
                    case INTENT_BROADCAST:
                        updateUI(MyMusic.totalNumberOfList);
                        Toast.makeText(context, "信息更新", Toast.LENGTH_SHORT).show();
                        break;
                    case CHANGE_PHOTO_BROADCAST:
                        updatePhoto();
                }


            }
        };
        broadcastManager.registerReceiver(bordcastReceiver, intentFilter);

    }
/*
    public void save() {
        Gson gson = new Gson();
        String songList = gson.toJson(totalList);
        Log.d("&&&&&&",songList);
        Gson gson1 = new Gson();
        String titleList = gson.toJson(MyMusic.myMusicLists);
        Log.d("&&&&&&","song title "+ titleList);
        SharedPreferences.Editor editor = getActivity().getSharedPreferences("information", Context.MODE_PRIVATE).edit();
        editor.putString("data",songList);
        editor.putString("titles",titleList);
        editor.apply();
        editor.commit();
    }

    public void read() {
        SharedPreferences preferences = getActivity().getSharedPreferences("information", Context.MODE_PRIVATE);
        String songList = preferences.getString("data","");
        String titleList = preferences.getString("titles","");
        Log.d("&&&&&&","get data "+songList);
        if (songList.equals("") ){
            Toast.makeText(getContext(),"null",Toast.LENGTH_SHORT).show();

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
                    updateUI(i);
            }
        }MyMusic.totalNumberOfList = totalList.size()-1;
        Log.d("<<<>>>","fromjson");
    }
*/


}
