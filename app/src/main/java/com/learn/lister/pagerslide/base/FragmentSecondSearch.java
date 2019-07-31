package com.learn.lister.pagerslide.base;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.gson.Gson;
import com.learn.lister.pagerslide.Service.MusicService;
import com.learn.lister.pagerslide.bean.SearchResultBean;
import com.learn.lister.pagerslide.data.MyMusic;
import com.learn.lister.pagerslide.upload.OnMusicListener;
import com.learn.lister.pagerslide.upload.Upload;
import com.learn.lister.pagerslide.R;
import com.learn.lister.pagerslide.lists.AdapterInterface;
import com.learn.lister.pagerslide.lists.MusicListAdapter;
import com.learn.lister.pagerslide.utils.Music;
import com.learn.lister.pagerslide.detail.DetailActivity;
import com.learn.lister.pagerslide.popupwindows.SelectPicPopupWindow;

import java.util.ArrayList;
import java.util.List;


public class FragmentSecondSearch extends BaseFragment {
    private EditText editText;
    private ImageView search;
    private List<View> viewList;//view数组
    private ListView listView;
    private List<Music> listM;
    private Boolean isNormalClick = true;
    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if(msg.what == 0){
                //new Handler().postDelayed(new Runnable(){
                    //public void run() {
                MusicListAdapter adapter = new MusicListAdapter(MyMusic.OnlineMusicList,adapterInterface,getContext(),true);

                listView.setAdapter(adapter);
                        //execute the task
                    //}
                //}, 1000);

            }
        }
    };
    private OnMusicListener onMusicListener2 = new OnMusicListener() {
        @Override
        public void onSuccess(String s) {
         MyMusic.fistClick = true;
            //url = s;
        }

        @Override
        public void onError() {

        }

        @Override
        public void onFailed() {

        }
    };
    private OnMusicListener onMusicListener = new OnMusicListener() {
        @Override
        public void onSuccess(String s) {
            Log.d("<<<>>>",s);
            if (s.length()>=8000){
            Gson gson =new Gson();
            SearchResultBean searchResultBean = gson.fromJson(s, SearchResultBean.class);
            for (int i=0;i<15;i++) {
                Log.d("<<<>>>", searchResultBean.getData().getSongs().get(i).getName());
                Music onlineMusic = new Music();
                onlineMusic.name = searchResultBean.getData().getSongs().get(i).getName();
                onlineMusic.album = searchResultBean.getData().getSongs().get(i).getAl().getName();
                onlineMusic.MusicId = searchResultBean.getData().getSongs().get(i).getId();
                onlineMusic.singer = searchResultBean.getData().getSongs().get(i).getAr().get(0).getName();
                onlineMusic.picUrl = searchResultBean.getData().getSongs().get(i).getAl().getPicUrl();
                onlineMusic.isOnline = true;
                MyMusic.OnlineMusicList.add(onlineMusic);
                upload.getMusicUrl(onlineMusic.MusicId,onMusicListener2,i);
                //onlineMusic.MusicUrl = MyMusic.url;
            }
            Message message=new Message();
            message.what=0;
            mHandler.sendMessage(message);
            Looper.prepare();
            Toast.makeText(getContext(),"sent",Toast.LENGTH_SHORT).show();
                isNormalClick = true;
            Looper.loop();
               }
            else {Looper.prepare();
                Toast.makeText(getContext(),"没有找到哦，换个词试试吧！",Toast.LENGTH_SHORT).show();
                isNormalClick = false;
                Looper.loop();

            }
        }

        @Override
        public void onError() {

        }

        @Override
        public void onFailed() {

        }
    };
    private Upload upload = new Upload();
    private AdapterInterface adapterInterface = new AdapterInterface() {
        @Override
        public void onNewList() {
            Intent intent=new Intent();
            intent.putExtra("isPlayingOnline",true);
            intent.setClass(getActivity(), SelectPicPopupWindow.class);
            startActivity(intent);
        }
    };

    @Override
    public View initView() {
        View view = View.inflate(mContext, R.layout.fragment2, null);

        return view;
    }
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {

        super.onActivityCreated(savedInstanceState);
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              if (MyMusic.fistClick) {
                    listM = new ArrayList<>();
                    MyMusic.OnlineMusicList = listM;
                    upload.upload(editText.getText().toString(), onMusicListener, "15");
                    MyMusic.fistClick = false;
                } else{
                    if (!isNormalClick){
                        listM = new ArrayList<>();
                        MyMusic.OnlineMusicList = listM;
                        upload.upload(editText.getText().toString(), onMusicListener, "15");
                    }
                    if (isNormalClick){
                        if (MyMusic.OnlineMusicList.size() == 15) {
                            if (MyMusic.OnlineMusicList.get(14).MusicUrl!=null){
                        listM = new ArrayList<>();
                        MyMusic.OnlineMusicList = listM;
                        upload.upload(editText.getText().toString(), onMusicListener, "15");
                    }
                    }else Toast.makeText(getContext(), "点太快了，等一下再点", Toast.LENGTH_SHORT).show();}
                }
            }
        });
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (MyMusic.OnlineMusicList.get(i).MusicUrl == null)
                    Toast.makeText(getContext(),"等一下，还没加载好",Toast.LENGTH_SHORT).show();
                else{
                MyMusic.isPlayingFromInternet = 1;
                MyMusic.temperateNumberI = i;
                MusicService.mPosition = i;
                Bundle bundle= new Bundle();
                bundle.putInt("position",i);
                Intent intent=new Intent();
                intent.putExtras(bundle);
                intent.putExtra("position",i);
                intent.setClass(getActivity(), DetailActivity.class);
                startActivity(intent);}
               //Log.d("<<<>>>",MyMusic.OnlineMusicList.get(i).MusicUrl);
            }
        });
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment2, null);
        //对应的viewPager
        ViewPager viewPager = view.findViewById(R.id.viewPager2);
        editText = view.findViewById(R.id.edit_text);
        search = view.findViewById(R.id.search);
        listView = view.findViewById(R.id.search_result);
        LayoutInflater inflater1=getLayoutInflater();
        View view1 = inflater1.inflate(R.layout.first, null);
        View view2 = inflater1.inflate(R.layout.second, null);
        View view3 = inflater1.inflate(R.layout.third, null);
        View view4 = inflater1.inflate(R.layout.fourth, null);
        View view5 = inflater1.inflate(R.layout.fifth, null);
        View view6 = inflater1.inflate(R.layout.sixth, null);
        View view7 = inflater1.inflate(R.layout.seven, null);
        viewList = new ArrayList<View>();// 将要分页显示的View装入数组中
        viewList.add(view1);
        viewList.add(view2);
        viewList.add(view3);
        viewList.add(view4);
        viewList.add(view5);
        viewList.add(view6);
        viewList.add(view7);
        PagerAdapter pagerAdapter = new PagerAdapter() {

            @Override
            public boolean isViewFromObject(View arg0, Object arg1) {
                // TODO Auto-generated method stub
                return arg0 == arg1;
            }

            @Override
            public int getCount() {
                // TODO Auto-generated method stub
                return viewList.size();
            }

            @Override
            public void destroyItem(ViewGroup container, int position,
                                    Object object) {
                // TODO Auto-generated method stub
                container.removeView(viewList.get(position));
            }

            @Override
            public Object instantiateItem(ViewGroup container, int position) {
                // TODO Auto-generated method stub
                container.addView(viewList.get(position));


                return viewList.get(position);
            }
        };
        viewPager.setAdapter(pagerAdapter);
        if (MyMusic.OnlineMusicList.size()==15)
        {
            Message message = new Message();
        message.what=0;
        mHandler.sendMessage(message);}
        return view;

    }

}
