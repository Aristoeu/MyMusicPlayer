package com.learn.lister.pagerslide.popupwindows;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.learn.lister.pagerslide.data.MyMusic;
import com.learn.lister.pagerslide.R;

import java.util.List;

public class SelectPicPopupWindow extends Activity  {

    private LinearLayout layout;
    private Boolean isPlayingOnline;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.alert_dialog);
        isPlayingOnline = getIntent().getBooleanExtra("isPlayingOnline",true);
        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layout=(LinearLayout)findViewById(R.id.pop_layout);
        for (int i=0;i<MyMusic.myMusicLists.size();i++)
        Log.d("<<<>>>",MyMusic.myMusicLists.get(i));
        //添加选择窗口范围监听可以优先获取触点，即不再执行onTouchEvent()函数，点击其他地方时执行onTouchEvent()函数销毁Activity
        layout.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                // TODO Auto-generated method stub
                Toast.makeText(getApplicationContext(), "提示：点击窗口外部关闭窗口！",
                        Toast.LENGTH_SHORT).show();
            }
        });
        //添加按钮监听
        //add_to_list.setOnClickListener(this);
        //ArrayAdapter<String> adapter = new ArrayAdapter<String>(SelectPicPopupWindow.this,R.layout.support_simple_spinner_dropdown_item,MyMusic.myMusicLists);
        myAdapter adapter = new myAdapter(MyMusic.myMusicLists);
        ListView listView = findViewById(R.id.list_to_choose);
        listView.setAdapter(adapter);
    }
    public class myAdapter extends BaseAdapter{
        private Context context;
        private List<String> list;

        public myAdapter(List<String> list) {
            this.context = SelectPicPopupWindow.this;
            this.list = list;

        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int i) {
            return list.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(final int i, View view, ViewGroup viewGroup) {
            ViewHolder holder = null;
            if (view == null) {
                holder = new ViewHolder();
                //引入布局
                view = View.inflate(context, R.layout.item_list, null);
                //实例化对象
                holder.music = view.findViewById(R.id.lists_name);
                view.setTag(holder);
            } else {
                holder = (ViewHolder) view.getTag();
            }
            //给控件赋值
            holder.music.setText(list.get(i));
            holder.music.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (isPlayingOnline)
                        MyMusic.totalList.get(i).add(MyMusic.OnlineMusicList.get(MyMusic.MyIndexOfChosenMusic));
                    else
                        MyMusic.totalList.get(i).add(MyMusic.LocalMusicList.get(MyMusic.MyIndexOfChosenMusic));
                    finish();
                }
            });
            return view;
        }

        class ViewHolder {
            TextView music;//歌曲名
        }

    }
    //实现onTouchEvent触屏函数但点击屏幕时销毁本Activity
    @Override
    public boolean onTouchEvent(MotionEvent event){
        finish();
        return true;
    }

}