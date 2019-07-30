package com.learn.lister.pagerslide.lists;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.BroadcastReceiver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.learn.lister.pagerslide.Service.MusicService;
import com.learn.lister.pagerslide.data.MyMusic;
import com.learn.lister.pagerslide.R;
import com.learn.lister.pagerslide.detail.DetailActivity;
import com.learn.lister.pagerslide.mine.FragmentFirstMine;
import com.learn.lister.pagerslide.view.ListViewForScrollView;

import butterknife.BindView;

import static com.learn.lister.pagerslide.data.MyMusic.IndexOfMusicLists;
import static com.learn.lister.pagerslide.data.MyMusic.LocalMusicList;
import static com.learn.lister.pagerslide.data.MyMusic.OnlineMusicList;
import static com.learn.lister.pagerslide.data.MyMusic.isPlayingFromInternet;
import static com.learn.lister.pagerslide.data.MyMusic.myMusicLists;
import static com.learn.lister.pagerslide.data.MyMusic.photosForLists;
import static com.learn.lister.pagerslide.data.MyMusic.temperateNumberI;

public class ChosenMusicActivity extends AppCompatActivity implements ChangePhotoContract.ChangePhotoPresenter, ChangePhotoContract.ChangePhotoModel, ChangePhotoContract.ChangePhotoView {
    private ListViewForScrollView mListView;
    private TextView toDetail;
    private ImageView photo;
    private TextView title,up_title;
    public static final int CHOOSE_PHOTO = 2;
    private MyReceiver myReceiver;
    TextView tv_subtitle;
    private AdapterInterface adapterInterface = new AdapterInterface() {
        @Override
        public void onNewList() {

        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chosen_music);
        initView();
        init();
        myReceiver = new MyReceiver(new Handler());
        IntentFilter itFilter = new IntentFilter();
        itFilter.addAction(MusicService.MAIN_UPDATE_UI);
        getApplicationContext().registerReceiver(myReceiver, itFilter);

    }

    public void initView() {
        ScrollView sv = findViewById(R.id.main_scroll);
        sv.smoothScrollTo(0, 0);
        mListView = findViewById(R.id.chosen_music_list);
        ImageView back = findViewById(R.id.back);
        photo = findViewById(R.id.photo_choose);
        title = findViewById(R.id.list_title);
        up_title = findViewById(R.id.up_title);
        toDetail = findViewById(R.id.toDetail);
        tv_subtitle = toDetail;
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        toDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (temperateNumberI==-1)Toast.makeText(ChosenMusicActivity.this,"你还没选歌哦",Toast.LENGTH_SHORT).show();
                else {
                    Bundle bundle = new Bundle();
                    bundle.putInt("position", MusicService.mPosition);
                    Intent intent = new Intent();
                    intent.putExtras(bundle);
                    intent.putExtra("position", MusicService.mPosition);
                    intent.setClass(ChosenMusicActivity.this, DetailActivity.class);
                    startActivity(intent);
                }
            }
        });
    }

    public void openAlbum() {
        Intent intent = new Intent("android.intent.action.GET_CONTENT");
        intent.setType("image/*");
        startActivityForResult(intent, CHOOSE_PHOTO); // 打开相册
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case CHOOSE_PHOTO:
                if (resultCode == RESULT_OK) {
                    // 判断手机系统版本号
                    if (Build.VERSION.SDK_INT >= 19) {
                        // 4.4及以上系统使用这个方法处理图片
                        handleImageOnKitKat(data);
                    } else {
                        // 4.4以下系统使用这个方法处理图片
                        handleImageBeforeKitKat(data);
                    }
                }
                break;
            default:
                break;
        }
    }
    @TargetApi(19)
    public void handleImageOnKitKat(Intent data) {
        String imagePath = null;
        Uri uri = data.getData();
        Log.d("TAG", "handleImageOnKitKat: uri is " + uri);
        if (DocumentsContract.isDocumentUri(this, uri)) {
            // 如果是document类型的Uri，则通过document id处理
            String docId = DocumentsContract.getDocumentId(uri);
            if("com.android.providers.media.documents".equals(uri.getAuthority())) {
                String id = docId.split(":")[1]; // 解析出数字格式的id
                String selection = MediaStore.Images.Media._ID + "=" + id;
                imagePath = getImagePath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, selection);
            } else if ("com.android.providers.downloads.documents".equals(uri.getAuthority())) {
                Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(docId));
                imagePath = getImagePath(contentUri, null);
            }
        } else if ("content".equalsIgnoreCase(uri.getScheme())) {
            // 如果是content类型的Uri，则使用普通方式处理
            imagePath = getImagePath(uri, null);
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            // 如果是file类型的Uri，直接获取图片路径即可
            imagePath = uri.getPath();
        }
        displayImage(imagePath); // 根据图片路径显示图片
    }
    public void handleImageBeforeKitKat(Intent data) {
        Uri uri = data.getData();
        String imagePath = getImagePath(uri, null);
        displayImage(imagePath);
    }
    public String getImagePath(Uri uri, String selection) {
        String path = null;
        // 通过Uri和selection来获取真实的图片路径
        Cursor cursor = getContentResolver().query(uri, null, selection, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            }
            cursor.close();
        }
        return path;
    }
    public void displayImage(String imagePath) {
        if (imagePath != null) {
            Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
            photosForLists.set(MyMusic.IndexOfMusicLists, bitmap);
            Intent broadcast = new Intent(FragmentFirstMine.CHANGE_PHOTO_BROADCAST);
            LocalBroadcastManager.getInstance(this).sendBroadcast(broadcast);
            photo.setImageBitmap(bitmap);
        } else {
            Toast.makeText(this, "failed to get image", Toast.LENGTH_SHORT).show();
        }
    }
    private void init() {
        if (MyMusic.totalList.get(MyMusic.IndexOfMusicLists) == null)
            Toast.makeText(this, "null", Toast.LENGTH_LONG).show();
        else {
            MusicListAdapter adapter = new MusicListAdapter(MyMusic.totalList.get(MyMusic.IndexOfMusicLists),adapterInterface,this,false);
            mListView.setAdapter(adapter);
            title.setText(myMusicLists.get(IndexOfMusicLists));
            if (IndexOfMusicLists!=0)up_title.setText(myMusicLists.get(IndexOfMusicLists));
            photo.setImageBitmap(photosForLists.get(IndexOfMusicLists));
            mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    MyMusic.isPlayingFromInternet = 2;
                    MyMusic.temperateNumberI = i;
                    MusicService.mPosition = i;
                    Bundle bundle = new Bundle();
                    bundle.putInt("position", i);
                    Intent intent = new Intent();
                    intent.putExtras(bundle);
                    intent.putExtra("position", i);
                    intent.setClass(ChosenMusicActivity.this, DetailActivity.class);
                    startActivity(intent);
                }
            });
            photo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (ContextCompat.checkSelfPermission(ChosenMusicActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(ChosenMusicActivity.this, new String[]{ Manifest.permission. WRITE_EXTERNAL_STORAGE }, 1);
                    } else {
                        openAlbum();
                    }
                }
            });
        }
    }
    public class MyReceiver extends BroadcastReceiver {
        private final Handler handler;
        public MyReceiver(Handler handler) {
            this.handler = handler;
        }

        @Override
        public void onReceive(final Context context, final Intent intent) {
            // Post the UI updating code to our Handler
            handler.post(new Runnable() {
                @Override
                public void run() {
                    //int play_pause = intent.getIntExtra(MusicService.KEY_MAIN_ACTIVITY_UI_BTN, -1);
                    int songid = intent.getIntExtra(MusicService.KEY_MAIN_ACTIVITY_UI_TEXT, -1);
                    switch (isPlayingFromInternet){
                        case 1:tv_subtitle.setText("正在播放："+OnlineMusicList.get(songid).name);break;
                        case 0:tv_subtitle.setText("正在播放："+LocalMusicList.get(songid).name);break;
                        case 2:tv_subtitle.setText("正在播放："+MyMusic.totalList.get(MyMusic.IndexOfMusicLists).get(songid).name);break;
                    }
                }
            });
        }
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        //Unbind from the service after exiting
        //unbindService(conn);
        getApplicationContext().unregisterReceiver(myReceiver);
    }
}

