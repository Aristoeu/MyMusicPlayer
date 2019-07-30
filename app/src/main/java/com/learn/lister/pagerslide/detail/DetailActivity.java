package com.learn.lister.pagerslide.detail;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager.widget.PagerAdapter;

import android.animation.ObjectAnimator;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;

import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.arialyy.annotations.Download;
import com.arialyy.aria.core.Aria;
import com.arialyy.aria.core.download.DownloadTask;
import com.learn.lister.pagerslide.Lrc.OnSingleTapListener;
import com.learn.lister.pagerslide.data.MyMusic;
import com.learn.lister.pagerslide.Lrc.LrcView;
import com.learn.lister.pagerslide.Lrc.UpdateLrc;
import com.learn.lister.pagerslide.upload.OnMusicListener;
import com.learn.lister.pagerslide.upload.Upload;
import com.learn.lister.pagerslide.R;
import com.learn.lister.pagerslide.Service.MusicService;
import com.learn.lister.pagerslide.view.MusicButton;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.learn.lister.pagerslide.data.MyMusic.LocalMusicList;
import static com.learn.lister.pagerslide.data.MyMusic.OnlineMusicList;
import static com.learn.lister.pagerslide.data.MyMusic.downloadingMusic;
import static com.learn.lister.pagerslide.data.MyMusic.isPlayingFromInternet;
import static com.learn.lister.pagerslide.data.MyMusic.lrcView;
import static com.learn.lister.pagerslide.data.MyMusic.myPlayMode;

public class DetailActivity extends AppCompatActivity implements ControlContract.ControlModel, ControlContract.ControlView{
    private static SeekBar seekBar;
    private MusicService.MyBinder musicControl;
    private MyConnection conn;
    private ImageView play;
    private MusicButton imageView;
    private TextView tv_title,tv_cur_time,tv_total_time;
    private static final int UPDATE_UI = 0;
    private String dd;
    private MyReceiver myReceiver;
    private ImageView playMode;
    private int mySongId=-1,songid;
    private Upload upload = new Upload();
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case UPDATE_UI:
                    updateUI();
                    break;
            }
        }
    };
    private List<View> viewList;//view数组
    private ObjectAnimator neddleObjectAnimator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        Intent intent = new Intent(this,MusicService.class);
        Bundle bundle = getIntent().getExtras();
        intent.putExtras(bundle);
        conn = new MyConnection();
        startService(intent);
        bindService(intent, conn, BIND_AUTO_CREATE);
        myReceiver = new MyReceiver(new Handler());
        IntentFilter itFilter = new IntentFilter();
        itFilter.addAction(MusicService.MAIN_UPDATE_UI);
        getApplicationContext().registerReceiver(myReceiver, itFilter);
        songid = getIntent().getIntExtra("position",0);
        initView();
    }

    public void initView() {
        //对应的viewPager
        ViewPager viewPager = findViewById(R.id.viewPager);
        LayoutInflater inflater=getLayoutInflater();
        View view_music_button = inflater.inflate(R.layout.music_button, null);
        final LrcView lrcView1 = view_music_button.findViewById(R.id.lllll);
        ImageView needleImage = view_music_button.findViewById(R.id.needle);
        final RelativeLayout relativeLayout = view_music_button.findViewById(R.id.relative);
        //final View view_lrc_view = inflater.inflate(R.layout.lrc_view, null);
        viewList = new ArrayList<View>();// 将要分页显示的View装入数组中
        viewList.add(view_music_button);
        //viewList.add(view_lrc_view);
        neddleObjectAnimator = ObjectAnimator.ofFloat(needleImage, "rotation", 0, 25);
        needleImage.setPivotX(0);
        needleImage.setPivotY(0);
        neddleObjectAnimator.setDuration(800);
        neddleObjectAnimator.setInterpolator(new LinearInterpolator());
        //neddleObjectAnimator.start();

        OnSingleTapListener onSingleTapListener1 = new OnSingleTapListener() {
            @Override
            public void onTap() {
                lrcView1.setVisibility(View.INVISIBLE);
                relativeLayout.setVisibility(View.VISIBLE);
            }
        };
        relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                lrcView1.setVisibility(View.VISIBLE);
                relativeLayout.setVisibility(View.INVISIBLE);
            }
        });
        MyMusic.onSingleTapListener = onSingleTapListener1;
        Aria.download(this).register();
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
        seekBar=findViewById(R.id.sb);
        ImageView pre = findViewById(R.id.pre1);
        ImageView next = findViewById(R.id.next1);
        tv_title = findViewById(R.id.tv_title);
        tv_cur_time =findViewById(R.id.tv_cur_time);
        tv_total_time = findViewById(R.id.tv_total_time);
        imageView = view_music_button.findViewById(R.id.imageview);
        ImageView download = findViewById(R.id.download);
        playMode = findViewById(R.id.playMode);
        switch (myPlayMode){
            case 1:playMode.setImageResource(R.drawable.play_icn_shuffle);break;
            case 2:playMode.setImageResource(R.drawable.play_icn_one);break;
            case 0:playMode.setImageResource(R.drawable.play_icn_loop);break;
        }
        play = findViewById(R.id.playStop1);
        ImageView back = findViewById(R.id.back);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if (b) {
                    musicControl.seekTo(i);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                Log.d("DetailActivity",seekBar.getProgress()+"<<<>>>");
                //musicControl.seekTo(seekBar.getProgress());
            }
        });
        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                play(view);
            }
        });
        pre.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pre(view);
            }
        });
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                next(view);
            }
        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        playMode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (myPlayMode){
                    case 0:playMode.setImageResource(R.drawable.play_icn_shuffle);myPlayMode = 1;break;
                    case 1:playMode.setImageResource(R.drawable.play_icn_one);myPlayMode = 2;break;
                    case 2:playMode.setImageResource(R.drawable.play_icn_loop);myPlayMode = 0;break;
                }
            }
        });
        download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (isPlayingFromInternet){
                    case 0:
                        Toast.makeText(DetailActivity.this,"这是本地歌曲哦，不用下载",Toast.LENGTH_SHORT).show();break;
                    case 1:
                        Aria.download(this)
                                .load(OnlineMusicList.get(songid).MusicUrl)
                                .setFilePath(Environment.getExternalStorageDirectory().getPath() + "/" + OnlineMusicList.get(songid).name + ".mp3")
                                .start();
                        downloadingMusic = OnlineMusicList.get(songid).name;
                        Toast.makeText(DetailActivity.this,downloadingMusic + "开始下载",Toast.LENGTH_SHORT).show();
                    break;
                    case 2:if (MyMusic.totalList.get(MyMusic.IndexOfMusicLists).get(songid).isOnline)
                    {   Aria.download(this)
                                .load(MyMusic.totalList.get(MyMusic.IndexOfMusicLists).get(songid).MusicUrl)
                                .setFilePath(Environment.getExternalStorageDirectory().getPath() + "/" + MyMusic.totalList.get(MyMusic.IndexOfMusicLists).get(songid).name + ".mp3")
                                .start();
                    downloadingMusic = MyMusic.totalList.get(MyMusic.IndexOfMusicLists).get(songid).name;
                        Toast.makeText(DetailActivity.this,downloadingMusic + "开始下载",Toast.LENGTH_SHORT).show();break;}
                    else Toast.makeText(DetailActivity.this,"这是本地歌曲哦，不用下载",Toast.LENGTH_SHORT).show();break;
                }

            }
        });
        lrcView = view_music_button.findViewById(R.id.lllll);
        lrcView.setPlayer(MyMusic.myPlayer);
        lrcView.loadLrc(MyMusic.lrc);
        lrcView.setOnPlayClickListener(new LrcView.OnPlayClickListener() {
            @Override
            public boolean onPlayClick(long time) {
                MyMusic.myPlayer.seekTo((int) time);
                if (!MyMusic.myPlayer.isPlaying()) {
                    MyMusic.myPlayer.start();
                }
                return true;
            }
        });

        UpdateLrc updateLrc = new UpdateLrc();
        updateLrc.updateLrc();
        getUI();
    }

    public void getUI() {

        switch (isPlayingFromInternet) {
            case 0:
                upload.upload(LocalMusicList.get(songid).name, onMusicListener, "1");
                break;
            case 1:
                imageView.setImageURL(OnlineMusicList.get(songid).picUrl);
                lrcView.loadLrcByUrl(OnlineMusicList.get(songid).MusicId);
                break;
            case 2:
                if (MyMusic.totalList.get(MyMusic.IndexOfMusicLists).get(songid).isOnline) {
                    imageView.setImageURL(MyMusic.totalList.get(MyMusic.IndexOfMusicLists).get(songid).picUrl);
                    lrcView.loadLrcByUrl(MyMusic.totalList.get(MyMusic.IndexOfMusicLists).get(songid).MusicId);
                } else
                    upload.upload(MyMusic.totalList.get(MyMusic.IndexOfMusicLists).get(songid).name, onMusicListener, "1");
                break;
        }
    }
    //同步get请求

    private OnMusicListener onMusicListener = new OnMusicListener() {
        @Override
        public void onSuccess(String d) {
            dd=d;
            if(d.length()>=100){
                String url;
                (DetailActivity.this).runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        String str = dd;
                        String regex = "\"picUrl\":\"(.*)\",\"name\"";
                        String regex2 = ",\"id\":(.*),\"pl\":";
                        Pattern pattern = Pattern.compile(regex);
                        Pattern pattern2 = Pattern.compile(regex2);
                        Matcher matcher2 = pattern2.matcher(str);
                        Matcher matcher = pattern.matcher(str);
                        while (matcher.find()) {
                            imageView.setImageURL(matcher.group(1));
                        }
                        while (matcher2.find()){
                            // Log.d("<<<>>><<<>>>", matcher2.group(1));
                            lrcView.loadLrcByUrl(matcher2.group(1));
                        }
                        // 在这里执行你要想的操作 比如直接在这里更新ui或者调用回调在 在回调中更新ui
                    }
                });

            }
        }

        @Override
        public void onError() {

        }

        @Override
        public void onFailed() {

        }
    };

    private class MyConnection implements ServiceConnection {

        //This method will be entered after the service is started.
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            //Log.i(TAG, "::MyConnection::onServiceConnected");
            //Get MyBinder in service
            musicControl = (MusicService.MyBinder) service;
            //Update button text
            updatePlayText();
            updateUI();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            //Log.i(TAG, "::MyConnection::onServiceDisconnected");

        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        //Start the update UI bar after entering the interface
        if (musicControl != null) {
            handler.sendEmptyMessage(UPDATE_UI);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //Unbind from the service after exiting
        unbindService(conn);
        getApplicationContext().unregisterReceiver(myReceiver);
    }

    @Override
    protected void onStop() {
        super.onStop();
        //Stop the progress of the update progress bar
        handler.removeCallbacksAndMessages(null);
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
                    int play_pause = intent.getIntExtra(MusicService.KEY_MAIN_ACTIVITY_UI_BTN, -1);
                    songid = intent.getIntExtra(MusicService.KEY_MAIN_ACTIVITY_UI_TEXT, -1);
                    switch (isPlayingFromInternet){
                        case 1:tv_title.setText(OnlineMusicList.get(songid).name);break;
                        case 0:tv_title.setText(LocalMusicList.get(songid).name);break;
                        case 2:tv_title.setText(MyMusic.totalList.get(MyMusic.IndexOfMusicLists).get(songid).name);break;
                    }
                    if (mySongId!=songid)
                        getUI();
                    mySongId = songid;
                    switch (play_pause) {
                        case MusicService.VAL_UPDATE_UI_PLAY:
                            play.setImageResource(R.drawable.desk_pause_prs);
                            neddleObjectAnimator.start();
                            imageView.play();
                            break;
                        case MusicService.VAL_UPDATE_UI_PAUSE:
                            play.setImageResource(R.drawable.desk_play_prs);
                            imageView.pause();
                            neddleObjectAnimator.reverse();
                            break;
                        default:
                            break;
                    }
                }
            });
        }
    }


    public void play(View view) {
        Intent intent = new Intent(MusicService.ACTION);
        Bundle bundle = new Bundle();
        bundle.putInt(MusicService.KEY_USR_ACTION,MusicService.ACTION_PLAY_PAUSE);
        intent.putExtras(bundle);
        sendBroadcast(intent);
        updatePlayText();
        //neddleObjectAnimator.start();
    }

    public void next(View view) {
        Intent intent = new Intent(MusicService.ACTION);
        Bundle bundle = new Bundle();
        bundle.putInt(MusicService.KEY_USR_ACTION,MusicService.ACTION_NEXT);
        intent.putExtras(bundle);
        sendBroadcast(intent);
        updatePlayText();
    }

    public void pre(View view) {
        Intent intent = new Intent(MusicService.ACTION);
        Bundle bundle = new Bundle();
        bundle.putInt(MusicService.KEY_USR_ACTION,MusicService.ACTION_PRE);
        intent.putExtras(bundle);
        sendBroadcast(intent);
        updatePlayText();
    }

    public void updateUI(){

        //Set the maximum value of the progress bar
        int cur_time = musicControl.getCurrenPostion(), total_time = musicControl.getDuration();
        seekBar.setMax(total_time);
        //Set the progress of the progress bar
        seekBar.setProgress(cur_time);

        String str = musicControl.getName();
        tv_title.setText(str);
        tv_cur_time.setText(timeToString(cur_time));
        tv_total_time.setText(timeToString(total_time));

        updateProgress();

        //Update the UI bar every 500 milliseconds using Handler
        handler.sendEmptyMessageDelayed(UPDATE_UI, 500);
    }

    private String timeToString(int time) {
        time /= 1000;
        return String.format("%02d:%02d",time/60,time%60);
    }
    //Update progress bar
    public void updateProgress() {
        int currenPostion = musicControl.getCurrenPostion();
        seekBar.setProgress(currenPostion);
    }


    //Update button text
    public void updatePlayText() {
        if(MusicService.mlastPlayer!=null &&MusicService.mlastPlayer.isPlaying()){
            imageView.play();
            play.setImageResource(R.drawable.desk_pause_prs);
        }else{
            imageView.pause();
            play.setImageResource(R.drawable.desk_play_prs);
        }
    }
    @Download.onTaskComplete void taskComplete(DownloadTask task) {
        Toast.makeText(DetailActivity.this,"下载成功",Toast.LENGTH_SHORT).show();
        //在这里处理任务完成的状态
    }
    @Download.onTaskFail void taskFail(DownloadTask task) {
        Toast.makeText(DetailActivity.this,downloadingMusic + "下载失败，这首歌不支持下载，或者你之前可能已经下载过了",Toast.LENGTH_SHORT).show();
    }

}
