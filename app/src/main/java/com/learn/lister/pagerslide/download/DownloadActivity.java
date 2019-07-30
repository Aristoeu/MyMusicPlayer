package com.learn.lister.pagerslide.download;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.arialyy.annotations.Download;
import com.arialyy.aria.core.Aria;
import com.arialyy.aria.core.download.DownloadTask;
import com.learn.lister.pagerslide.R;

import java.util.Timer;
import java.util.TimerTask;

public class DownloadActivity extends AppCompatActivity {

    private TextView songName,Progress;
    private SeekBar seekBar;
    private int p;
    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if(msg.what == 0){
                //Toast.makeText(getApplication(),"percent:"+p,Toast.LENGTH_SHORT).show();
                seekBar.setProgress(p);
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download);
        seekBar = findViewById(R.id.seek_bar);
        songName = findViewById(R.id.song_name);
        Progress = findViewById(R.id.download_progress);
        seekBar.setMax(100);
        Aria.download(this).register();
        updateDownloadPercentage();
    }
    @Download.onTaskComplete void taskComplete(DownloadTask task) {
        Toast.makeText(DownloadActivity.this,"下载成功",Toast.LENGTH_SHORT).show();
        //在这里处理任务完成的状态
    }
    @Download.onTaskRunning protected void running(DownloadTask task) {
        p = task.getPercent();	//任务进度百分比
    }

    public void updateDownloadPercentage(){
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
