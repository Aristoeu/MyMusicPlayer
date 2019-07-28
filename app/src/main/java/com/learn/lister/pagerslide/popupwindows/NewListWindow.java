package com.learn.lister.pagerslide.popupwindows;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.learn.lister.pagerslide.R;
import com.learn.lister.pagerslide.utils.Music;

import java.util.ArrayList;
import java.util.List;

import static com.learn.lister.pagerslide.data.MyMusic.myMusicLists;
import static com.learn.lister.pagerslide.data.MyMusic.photosForLists;
import static com.learn.lister.pagerslide.data.MyMusic.totalList;
import static com.learn.lister.pagerslide.data.MyMusic.totalNumberOfList;
import static com.learn.lister.pagerslide.mine.FragmentFirstMine.INTENT_BROADCAST;

public class NewListWindow extends Activity {
    private LinearLayout layout;
    private EditText editText;
    private Button make;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_list);
        make = this.findViewById(R.id.make);
        layout=findViewById(R.id.pop_layout);
        editText = findViewById(R.id.add_lists);
        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        //添加选择窗口范围监听可以优先获取触点，即不再执行onTouchEvent()函数，点击其他地方时执行onTouchEvent()函数销毁Activity
        layout.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                // TODO Auto-generated method stub
                Toast.makeText(getApplicationContext(), "提示：点击窗口外部关闭窗口！",
                        Toast.LENGTH_SHORT).show();
            }
        });
        make.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!editText.getText().toString().isEmpty()) {
                    myMusicLists.add(editText.getText().toString());
                    totalNumberOfList++;
                    List<Music> newList = new ArrayList<>();
                    totalList.add(newList);
                    Bitmap bitmap = BitmapFactory.decodeResource(getResources(),R.drawable.topinfo_ban_bg);
                    photosForLists.add(bitmap);
                    Toast.makeText(NewListWindow.this,"创建成功",Toast.LENGTH_SHORT).show();
                    Intent broadcast = new Intent(INTENT_BROADCAST);
                    LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(broadcast);
                    finish();
                }
                else Toast.makeText(NewListWindow.this,"你还没有输入哦",Toast.LENGTH_SHORT).show();
            }
        });
    }
    @Override
    public boolean onTouchEvent(MotionEvent event){
        finish();
        return true;
    }
}
