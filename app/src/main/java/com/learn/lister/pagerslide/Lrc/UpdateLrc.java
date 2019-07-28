package com.learn.lister.pagerslide.Lrc;

import android.os.Handler;
import android.os.Message;

import com.learn.lister.pagerslide.data.MyMusic;

import java.util.Timer;
import java.util.TimerTask;

import static com.learn.lister.pagerslide.data.MyMusic.lrcView;

public class UpdateLrc {
    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if(msg.what == 0){
                lrcView.updateTime(MyMusic.myPlayer.getCurrentPosition());
            }
        }
    };
    public void updateLrc(){
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                // (1) 使用handler发送消息
                Message message=new Message();
                message.what=0;
                mHandler.sendMessage(message);
            }
        },0,1000);
    }
}
