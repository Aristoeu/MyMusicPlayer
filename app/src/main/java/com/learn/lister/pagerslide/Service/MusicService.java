package com.learn.lister.pagerslide.Service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;

import com.learn.lister.pagerslide.data.MyMusic;
import com.learn.lister.pagerslide.R;
import com.learn.lister.pagerslide.Widget.MusicWidget;
import com.learn.lister.pagerslide.main.MainActivity;
import com.learn.lister.pagerslide.utils.Music;

import java.io.IOException;
import java.util.Random;

import static com.learn.lister.pagerslide.data.MyMusic.LocalMusicList;
import static com.learn.lister.pagerslide.data.MyMusic.OnlineMusicList;
import static com.learn.lister.pagerslide.data.MyMusic.isPlayingFromInternet;
import static com.learn.lister.pagerslide.data.MyMusic.myPlayMode;
import static com.learn.lister.pagerslide.data.MyMusic.myPlayer;
import static com.learn.lister.pagerslide.data.MyMusic.temperateNumberI;

public class MusicService extends Service {
    public static MediaPlayer mlastPlayer;
    public static int mPosition;
    private int mlastPositon;
    private int position;
    private String path = "";
    private String url = "";
    private String TAG = "MusicServiceLog";
    private MediaPlayer player;
    private Music music;
    private Music onlineMusic;
    private Music chosenMusic;
    private Context context;
    private RemoteViews remoteView;
    private Notification notification;
    private String notificationChannelID = "1";
    public static String ACTION = "to_service";
    public static String KEY_USR_ACTION = "key_usr_action";
    public static final int ACTION_PRE = 0, ACTION_PLAY_PAUSE = 1, ACTION_NEXT = 2;
    public static String MAIN_UPDATE_UI = "main_activity_update_ui";  //Action
    public static String KEY_MAIN_ACTIVITY_UI_BTN = "main_activity_ui_btn_key";
    public static String KEY_MAIN_ACTIVITY_UI_TEXT = "main_activity_ui_text_key";
    public static final int  VAL_UPDATE_UI_PLAY = 1,VAL_UPDATE_UI_PAUSE =2;
    private int notifyId = 1;

    @Override
    public IBinder onBind(Intent intent) {

        //When onCreate() is executed, onBind() will be executed to return the method of operating the name.
        return new MyBinder();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
       // LocalMusicList = MusicList.getMusicData(context);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ACTION);
        registerReceiver(receiver, intentFilter);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        initNotificationBar();
        Bundle bundle = intent.getExtras();
        //mlastPositon = position;
        position = bundle.getInt("position");
        //prepare();


        // need to be fixed


        if (mlastPlayer == null|| mlastPositon != position){
            prepare();
        }else{
            player = mlastPlayer;
            myPlayer = player;
        }
        return super.onStartCommand(intent, flags, startId);
    }
    private void postState(Context context, int state,int songid) {
        Intent actionIntent = new Intent(MusicService.MAIN_UPDATE_UI);
        actionIntent.putExtra(MusicService.KEY_MAIN_ACTIVITY_UI_BTN,state);
        actionIntent.putExtra(MusicService.KEY_MAIN_ACTIVITY_UI_TEXT, songid);
        updateNotification();
        context.sendBroadcast(actionIntent);
    }
    private void initNotificationBar(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            CharSequence name = "notification channel";
            String description = "notification description";
            int importance = NotificationManager.IMPORTANCE_MIN;
            NotificationChannel mChannel = new NotificationChannel(notificationChannelID, name, importance);
            mChannel.setDescription(description);
            mChannel.setLightColor(Color.RED);
            mChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
            mNotificationManager.createNotificationChannel(mChannel);
        }
        NotificationCompat.Builder  mBuilder = new NotificationCompat.Builder(this, notificationChannelID);
        Intent intent = new Intent(this, MainActivity.class);
        Bundle bundle = new Bundle();
        bundle.putInt("position", mPosition);
        intent.putExtras(bundle);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,0, intent, 0);
        remoteView = new RemoteViews(getPackageName(),R.layout.notification);
        String title;
        switch (isPlayingFromInternet){
            case 1:title = MyMusic.OnlineMusicList.get(mPosition).name;break;
            case 0:title = LocalMusicList.get(mPosition).name;break;
            case 2:default:
                Log.d("<<<>>>",MyMusic.IndexOfMusicLists+"INDEX"+mPosition+"mposition");
                title = MyMusic.totalList.get(MyMusic.IndexOfMusicLists).get(mPosition).name;break;
        }
//❤存疑 原来不是temperate 原来是mPosition
        //


        //注意这里是else 没有继续if

        //
        Log.i(TAG, "updateNotification title = " + title);
        remoteView.setTextViewText(R.id.notification_title, title);
        remoteView.setOnClickPendingIntent(R.id.play_pause,getPendingIntent(this, R.id.play_pause));
        remoteView.setOnClickPendingIntent(R.id.prev_song, getPendingIntent(this, R.id.prev_song));
        remoteView.setOnClickPendingIntent(R.id.next_song, getPendingIntent(this, R.id.next_song));
        if (MusicService.mlastPlayer != null && MusicService.mlastPlayer.isPlaying()) {
            String s = getResources().getString(R.string.pause);
            remoteView.setTextViewText(R.id.play_pause, s);
        }else {
            String s = getResources().getString(R.string.play);
            remoteView.setTextViewText(R.id.play_pause, s);
        }
        mBuilder.setContentIntent(pendingIntent)
                .setContent(remoteView)
                .setWhen(System.currentTimeMillis())
                .setOngoing(false)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setDefaults(Notification.DEFAULT_LIGHTS)
                .setSmallIcon(R.mipmap.zjalmusic)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(),R.mipmap.jalmusic));
        notification = mBuilder.build();
        notification.flags = Notification.FLAG_ONGOING_EVENT;
        NotificationManager manager = (NotificationManager) getSystemService(Service.NOTIFICATION_SERVICE);
        manager.notify(notifyId,notification);
        updateNotification();
    }

    private void updateNotification() {
        String title;
        switch (isPlayingFromInternet){
            case 0:title = LocalMusicList.get(mPosition).name;break;
            case 1:title = MyMusic.OnlineMusicList.get(mPosition).name;break;
            case 2:
                default:title = MyMusic.totalList.get(MyMusic.IndexOfMusicLists).get(mPosition).name;break;
        }
        Log.i(TAG, "updateNotification title = " + title);
        remoteView.setTextViewText(R.id.notification_title, title);
        if (MusicService.mlastPlayer != null && MusicService.mlastPlayer.isPlaying()) {
            String s = getResources().getString(R.string.pause);
            remoteView.setTextViewText(R.id.play_pause, s);
        }else {
            String s = getResources().getString(R.string.play);
            remoteView.setTextViewText(R.id.play_pause, s);
        }

        notification.contentView = remoteView;
        NotificationManager manager = (NotificationManager) getSystemService(Service.NOTIFICATION_SERVICE);
        manager.notify(notifyId,notification);
    }

    private PendingIntent getPendingIntent(Context context, int buttonId) {
        Intent intent = new Intent();
        intent.setClass(context, MusicWidget.class);
        intent.addCategory(Intent.CATEGORY_ALTERNATIVE);
        intent.setData(Uri.parse(""+buttonId));
        PendingIntent pi = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        return pi;
    }
    void prepare(){
        switch (isPlayingFromInternet){
            case 0:{
                music = LocalMusicList.get(position);
                path = music.path;
                Log.i(TAG,"path:"+path);
                player = new MediaPlayer();//This is only done once, used to prepare the player.
                myPlayer = player;
                if (mlastPlayer !=null){
                    mlastPlayer.stop();
                    mlastPlayer.release();
                }
                mlastPlayer = myPlayer;
                mPosition = position;
                mlastPositon = position;
                myPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                try {
                    Log.i(TAG,path);
                    myPlayer.setDataSource(path); //Prepare resources
                    myPlayer.prepare();
                    myPlayer.start();
                    Log.i(TAG, "Ready to play name");
                } catch (IOException e) {
                    Log.i(TAG,"ERROR");
                    e.printStackTrace();
                }
                postState(getApplicationContext(), VAL_UPDATE_UI_PLAY,position);
                myPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        switch (myPlayMode){
                            case 0:next(1);break;
                            case 1:
                                Random ra =new Random();
                                next(ra.nextInt(5));break;
                            case 2:next(0);break;
                        }
                        Toast.makeText(context, "自动为您切换下一首:"+music.name, Toast.LENGTH_SHORT).show();
                        prepare();
                    }
                });
            }break;
            case 1:{
                //name = LocalMusicList.get(position);
                onlineMusic = OnlineMusicList.get(position);
                //path = name.path;
                url = onlineMusic.MusicUrl;
                //Log.i(TAG,"path:"+path);
                player = new MediaPlayer();//This is only done once, used to prepare the player.
                myPlayer = player;
                if (mlastPlayer !=null){
                    mlastPlayer.stop();
                    mlastPlayer.release();
                }
                mlastPlayer = myPlayer;
                mPosition = position;
                mlastPositon = position;
                myPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                try {
                    //Log.i(TAG,path);
                    //myPlayer.setDataSource(path); //Prepare resources
                    myPlayer.setDataSource(url);
                    myPlayer.prepare();
                    myPlayer.start();
                    Log.i(TAG, "Ready to play name");
                } catch (IOException e) {
                    Log.i(TAG,"ERROR");
                    e.printStackTrace();
                }
                postState(getApplicationContext(), VAL_UPDATE_UI_PLAY,position);
                myPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        switch (myPlayMode){
                            case 0:next(1);break;
                            case 1:
                                Random ra =new Random();
                                next(ra.nextInt(5));break;
                            case 2:next(0);break;
                        }
                        Toast.makeText(context, "自动为您切换下一首:"+onlineMusic.name, Toast.LENGTH_SHORT).show();
                        prepare();
                    }
                });
            }break;
            case 2:
                default:if (MyMusic.totalList.get(MyMusic.IndexOfMusicLists).get(position).isOnline)
                {
                    //name = LocalMusicList.get(position);
                    chosenMusic = MyMusic.totalList.get(MyMusic.IndexOfMusicLists).get(position);
                    //path = name.path;
                    url = chosenMusic.MusicUrl;
                    //Log.i(TAG,"path:"+path);
                    player = new MediaPlayer();//This is only done once, used to prepare the player.
                    myPlayer = player;
                    if (mlastPlayer !=null){
                        mlastPlayer.stop();
                        mlastPlayer.release();
                    }
                    mlastPlayer = myPlayer;
                    mPosition = position;
                    mlastPositon = position;
                    myPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                    try {
                        //Log.i(TAG,path);
                        //myPlayer.setDataSource(path); //Prepare resources
                        myPlayer.setDataSource(url);
                        myPlayer.prepare();
                        myPlayer.start();
                        Log.i(TAG, "Ready to play name");
                    } catch (IOException e) {
                        Log.i(TAG,"ERROR");
                        e.printStackTrace();
                    }
                    postState(getApplicationContext(), VAL_UPDATE_UI_PLAY,position);
                    myPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                        @Override
                        public void onCompletion(MediaPlayer mp) {
                            switch (myPlayMode){
                                case 0:next(1);break;
                                case 1:
                                    Random ra =new Random();
                                    next(ra.nextInt(5));break;
                                case 2:next(0);break;
                            }
                            Toast.makeText(context, "自动为您切换下一首:"+chosenMusic.name, Toast.LENGTH_SHORT).show();
                            prepare();
                        }
                    });
                }
                else {
                    chosenMusic = MyMusic.totalList.get(MyMusic.IndexOfMusicLists).get(position);
                    path = chosenMusic.path;
                    Log.i(TAG,"path:"+path);
                    player = new MediaPlayer();//This is only done once, used to prepare the player.
                    myPlayer = player;
                    if (mlastPlayer !=null){
                        mlastPlayer.stop();
                        mlastPlayer.release();
                    }
                    mlastPlayer = myPlayer;
                    mPosition = position;
                    mlastPositon = position;
                    myPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                    try {
                        Log.i(TAG,path);
                        myPlayer.setDataSource(path); //Prepare resources
                        myPlayer.prepare();
                        myPlayer.start();
                        Log.i(TAG, "Ready to play name");
                    } catch (IOException e) {
                        Log.i(TAG,"ERROR");
                        e.printStackTrace();
                    }
                    postState(getApplicationContext(), VAL_UPDATE_UI_PLAY,position);
                    myPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                        @Override
                        public void onCompletion(MediaPlayer mp) {
                            switch (myPlayMode){
                                case 0:next(1);break;
                                case 1:
                                    Random ra =new Random();
                                    next(ra.nextInt(5));break;
                                case 2:next(0);break;
                            }
                            Toast.makeText(context, "自动为您切换下一首:"+chosenMusic.name, Toast.LENGTH_SHORT).show();
                            prepare();
                        }
                    });
                }break;
        }

    }

    //This method contains operations on name
    public class MyBinder extends Binder {

        public boolean isPlaying(){
            return myPlayer.isPlaying();
        }

        public void play() {
            if (myPlayer.isPlaying()) {
                myPlayer.pause();
                Log.i(TAG, "Play stop");
            } else {
                myPlayer.start();
                Log.i(TAG, "Play start");
            }
        }

        //Play the next name
        /*public void next(int type){
            mPosition +=type;
            mPosition = (mPosition + LocalMusicList.size())% LocalMusicList.size();
            name = LocalMusicList.get(mPosition);
            prepare();
        }*/

        //Returns the length of the name in milliseconds
        public int getDuration(){
            return myPlayer.getDuration();
        }

        //Return the name of the name
        public String getName(){
            switch (isPlayingFromInternet){
                case 0:music = LocalMusicList.get(position);return music.name;
                case 1:onlineMusic = OnlineMusicList.get(position);return onlineMusic.name;
                case 2:
                    default:chosenMusic = MyMusic.totalList.get(MyMusic.IndexOfMusicLists).get(position);return chosenMusic.name;
            }
        }

        //Returns the current progress of the name in milliseconds
        public int getCurrenPostion(){
            return myPlayer.getCurrentPosition();
        }

        //Set the progress of name playback in milliseconds
        public void seekTo(int mesc){
            myPlayer.seekTo(mesc);
        }
    }

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action  = intent.getAction();
            if (ACTION.equals(action)) {
                int widget_action = intent.getIntExtra(KEY_USR_ACTION, -1);

                switch (widget_action) {
                    case ACTION_PRE:
                        switch (myPlayMode){
                            case 0:next(-1);break;
                            case 1:
                                Random ra =new Random();
                                next(ra.nextInt(20));break;
                            case 2:next(0);break;
                        }
                        Log.d(TAG,"action_prev");
                        break;
                    case ACTION_PLAY_PAUSE:
                        play();
                        break;
                    case ACTION_NEXT:
                        switch (myPlayMode){
                            case 0:next(1);break;
                            case 1:
                                Random ra =new Random();
                                next(ra.nextInt(5));break;
                            case 2:next(0);break;
                        }
                        Log.d(TAG,"action_next");
                        break;
                    default:
                        break;
                }
            }
        }
    };

    public void play() {
        if (myPlayer.isPlaying()) {
            myPlayer.pause();
            postState(getApplicationContext(), VAL_UPDATE_UI_PAUSE,position);
            Log.i(TAG, "Play stop");
        } else {
            myPlayer.start();
            postState(getApplicationContext(), VAL_UPDATE_UI_PLAY,position);
            Log.i(TAG, "Play start");
        }
    }

    //Play the next name
    public void next(int type){
        position +=type;
        switch (isPlayingFromInternet){
            case 0:position = (position + LocalMusicList.size())% LocalMusicList.size();
                music = LocalMusicList.get(position);break;
            case 1:position = (position + OnlineMusicList.size())% OnlineMusicList.size();
                onlineMusic = OnlineMusicList.get(position);break;
            case 2:default:position = (position + MyMusic.totalList.get(MyMusic.IndexOfMusicLists).size())% MyMusic.totalList.get(MyMusic.IndexOfMusicLists).size();
                chosenMusic = MyMusic.totalList.get(MyMusic.IndexOfMusicLists).get(position);break;
        }
        prepare();
    }


}