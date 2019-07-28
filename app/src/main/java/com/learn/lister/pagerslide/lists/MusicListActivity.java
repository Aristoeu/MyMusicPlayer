package com.learn.lister.pagerslide.lists;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.learn.lister.pagerslide.Service.MusicService;
import com.learn.lister.pagerslide.data.MyMusic;
import com.learn.lister.pagerslide.R;
import com.learn.lister.pagerslide.detail.DetailActivity;
import com.learn.lister.pagerslide.popupwindows.SelectPicPopupWindow;
import com.learn.lister.pagerslide.utils.MusicList;
import com.learn.lister.pagerslide.view.ListViewForScrollView;

import static com.learn.lister.pagerslide.data.MyMusic.LocalMusicList;
import static com.learn.lister.pagerslide.data.MyMusic.temperateNumberI;

public class MusicListActivity extends AppCompatActivity {
    private ListViewForScrollView mListView;
    private ImageView back;
    private MediaPlayer mediaPlayer = new MediaPlayer();
    private TextView toDetails;
    private AdapterInterface adapterInterface = new AdapterInterface() {
        @Override
        public void onNewList() {
            Intent intent=new Intent();
            intent.putExtra("isPlayingOnline",false);
            intent.setClass(getApplicationContext(), SelectPicPopupWindow.class);
            startActivity(intent);
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_list);
        ScrollView sv = findViewById(R.id.main_scroll);
        sv.smoothScrollTo(0, 0);
        mListView = findViewById(R.id.local_music_list);
        toDetails = findViewById(R.id.to_details);
        back = findViewById(R.id.back);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{ Manifest.permission.READ_EXTERNAL_STORAGE }, 2);
        }else
            init();
    }
    private void init() {
        LocalMusicList = MusicList.getMusicData(this);
        if (LocalMusicList ==null)
            Toast.makeText(this,"null",Toast.LENGTH_LONG).show();
        MusicListAdapter adapter = new MusicListAdapter(LocalMusicList, adapterInterface, this, true);
        mListView.setAdapter(adapter);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        toDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (temperateNumberI==-1)Toast.makeText(MusicListActivity.this,"你还没选歌哦",Toast.LENGTH_SHORT).show();
                else {
                    Bundle bundle = new Bundle();
                    bundle.putInt("position", MusicService.mPosition);
                    Intent intent = new Intent();
                    intent.putExtras(bundle);
                    intent.putExtra("position", MusicService.mPosition);
                    intent.setClass(MusicListActivity.this, DetailActivity.class);
                    startActivity(intent);
                }
            }
        });
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Toast.makeText(MusicListActivity.this, LocalMusicList.get(i).path,Toast.LENGTH_LONG).show();
                MyMusic.isPlayingFromInternet = 0;
                temperateNumberI = i;
                MusicService.mPosition = i;
                Bundle bundle= new Bundle();
                bundle.putInt("position",i);
                Intent intent=new Intent();
                intent.putExtras(bundle);
                intent.putExtra("position",i);
                intent.setClass(MusicListActivity.this, DetailActivity.class);
                startActivity(intent);

            }
        });
    }
}
