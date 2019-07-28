package com.learn.lister.pagerslide.main;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
//import android.support.v7.app.NotificationCompat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.learn.lister.pagerslide.R;
import com.learn.lister.pagerslide.Service.MusicService;
import com.learn.lister.pagerslide.base.PagerSlideAdapter;
import com.learn.lister.pagerslide.base.BaseFragment;
import com.learn.lister.pagerslide.data.MyMusic;
import com.learn.lister.pagerslide.detail.DetailActivity;
import com.learn.lister.pagerslide.lists.MusicListActivity;
import com.learn.lister.pagerslide.mine.FragmentFirstMine;
import com.learn.lister.pagerslide.base.FragmentSecondSearch;
import com.learn.lister.pagerslide.base.FragmentThirdFriends;
import com.learn.lister.pagerslide.base.FragmentFourthVideos;
import com.learn.lister.pagerslide.utils.Music;
import com.learn.lister.pagerslide.utils.MusicList;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.learn.lister.pagerslide.data.MyMusic.ChosenMusicList;
import static com.learn.lister.pagerslide.data.MyMusic.LocalMusicList;
import static com.learn.lister.pagerslide.data.MyMusic.myMusicLists;
import static com.learn.lister.pagerslide.data.MyMusic.photosForLists;
import static com.learn.lister.pagerslide.data.MyMusic.temperateNumberI;
import static com.learn.lister.pagerslide.data.MyMusic.totalList;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    @BindView(R.id.page_0) TextView text0;
    @BindView(R.id.page_1) TextView text1;
    @BindView(R.id.page_2) TextView text2;
    @BindView(R.id.page_3) TextView text3;
    @BindView(R.id.main_tab_line) ImageView tab_line;
    @BindView(R.id.main_pager) ViewPager mViewPager;
    @BindView(R.id.drawer_layout) DrawerLayout drawerLayout;
    @BindView(R.id.set) ImageView set;
    @BindView(R.id.search_top) ImageView search_top;
    @BindView(R.id.to_detail)LinearLayout toDetail;

    private int screenWidth;
    private List<BaseFragment> mFragmentList = new ArrayList<>();
    private PagerSlideAdapter adapter;
    private  final int REQUEST_EXTERNAL_STORAGE = 1;
    private  String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE };
    public  void verifyStoragePermissions(Activity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity,
                Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(activity, PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE);
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        verifyStoragePermissions(this);
        myMusicLists.add("我的收藏");
        totalList.add(ChosenMusicList);
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(),R.drawable.topinfo_ban_bg);
        photosForLists.add(bitmap);
        ButterKnife.bind(this);
        LocalMusicList = MusicList.getMusicData(this);
        initData();
        initWidth();
        setListener();
        set.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });
        search_top.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mViewPager.setCurrentItem(1);
                Toast.makeText(MainActivity.this,"在这个页面搜索哦",Toast.LENGTH_SHORT).show();
            }
        });
        toDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (temperateNumberI==-1)Toast.makeText(MainActivity.this,"你还没选歌哦",Toast.LENGTH_SHORT).show();
                else {
                    Bundle bundle = new Bundle();
                    bundle.putInt("position", MusicService.mPosition);
                    Intent intent = new Intent();
                    intent.putExtras(bundle);
                    intent.putExtra("position", MusicService.mPosition);
                    intent.setClass(MainActivity.this, DetailActivity.class);
                    startActivity(intent);
                }
            }
        });
       // save();
        List<String> permissionList = new ArrayList<>();
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            permissionList.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        }

        if (ContextCompat.checkSelfPermission(this,Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            permissionList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }

        if (!permissionList.isEmpty()){
            ActivityCompat.requestPermissions(this,permissionList.toArray(new String[permissionList.size()]),1);
        }

    }


    @TargetApi(Build.VERSION_CODES.O)
    private void createNotificationChannel(String channelId, String channelName, int importance) {
        NotificationChannel channel = new NotificationChannel(channelId, channelName, importance);
        NotificationManager notificationManager = (NotificationManager) getSystemService(
                NOTIFICATION_SERVICE);
        notificationManager.createNotificationChannel(channel);
    }

    private void initData() {
        mFragmentList.add(new FragmentFirstMine());
        mFragmentList.add(new FragmentSecondSearch());
        mFragmentList.add(new FragmentThirdFriends());
        mFragmentList.add(new FragmentFourthVideos());
        adapter = new PagerSlideAdapter(getSupportFragmentManager(), mFragmentList);
        mViewPager.setAdapter(adapter);
        mViewPager.setCurrentItem(0);
        text0.setTextSize(20);
    }


public void save(){
    Gson gson = new Gson();
    String json = gson.toJson(LocalMusicList);
    Log.d("<<<>>>",json);
    Gson gson2 = new Gson();
    List<Music> list = new ArrayList<>();
    list = gson2.fromJson(json, new TypeToken<List<Music>>(){}.getType());
    Log.d("<<<>>>","fromjson");
    Gson gson1 = new Gson();
    Log.d("<<<>>>",gson1.toJson(list));
    //SharedPreferences.Editor editor = getSharedPreferences("tAG",MODE_PRIVATE).edit();
    //editor.putString("data",json);
    //editor.apply();
}
    private void setListener() {

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) tab_line.getLayoutParams();
                lp.leftMargin = screenWidth/4*position + positionOffsetPixels/4;
                tab_line.setLayoutParams(lp);
            }

            @Override
            public void onPageSelected(int position) {
                resetTextView();
                switch (position) {
                    case 0:
                        text0.setTextSize(20);
                        text1.setTextSize(16);
                        text2.setTextSize(16);
                        text3.setTextSize(16);
                        break;
                    case 1:
                        //text1.setTextColor(Color.BLUE);
                        text1.setTextSize(20);
                        text0.setTextSize(16);
                        text2.setTextSize(16);
                        text3.setTextSize(16);
                        break;
                    case 2:
                        text2.setTextSize(20);
                        text1.setTextSize(16);
                        text0.setTextSize(16);
                        text3.setTextSize(16);
                        //text2.setTextColor(Color.BLUE);
                        break;
                    case 3:
                        text3.setTextSize(20);
                        text1.setTextSize(16);
                        text2.setTextSize(16);
                        text0.setTextSize(16);
                        //text3.setTextColor(Color.BLUE);
                        //sendChatMsg(getCurrentFocus());
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

        text0.setOnClickListener(this);
        text1.setOnClickListener(this);
        text2.setOnClickListener(this);
        text3.setOnClickListener(this);

    }

    private void resetTextView() {
        text0.setTextColor(Color.BLACK);
        text1.setTextColor(Color.BLACK);
        text2.setTextColor(Color.BLACK);
        text3.setTextColor(Color.BLACK);
    }

    private void initWidth() {
        DisplayMetrics dpMetrics = new DisplayMetrics();
        getWindow().getWindowManager().getDefaultDisplay().getMetrics(dpMetrics);
        screenWidth = dpMetrics.widthPixels;
        LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) tab_line.getLayoutParams();
        lp.width = screenWidth / 4;
        tab_line.setLayoutParams(lp);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.page_0:
                mViewPager.setCurrentItem(0);
                break;
            case R.id.page_1:
                mViewPager.setCurrentItem(1);
                break;
            case R.id.page_2:
                mViewPager.setCurrentItem(2);
                break;
            case R.id.page_3:
                mViewPager.setCurrentItem(3);
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0){
                    for (int i = 0; i < grantResults.length; i++) {

                        int grantResult = grantResults[i];
                        if (grantResult == PackageManager.PERMISSION_DENIED){
                            String s = permissions[i];
                            Toast.makeText(this,s+"权限被拒绝了",Toast.LENGTH_SHORT).show();
                        }
                    }
                }
                break;
            default:
                break;
        }
    }

}

