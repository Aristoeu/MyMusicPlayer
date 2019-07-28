package com.learn.lister.pagerslide.lists;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.learn.lister.pagerslide.data.MyMusic;
import com.learn.lister.pagerslide.R;
import com.learn.lister.pagerslide.utils.Music;

import java.util.List;

public class MusicListAdapter extends BaseAdapter {
    private Context context;
    private List<Music> list;
    private AdapterInterface adapterInterface;
    private Boolean isSettingsVisible;

    public MusicListAdapter(List<Music> list, AdapterInterface adapterInterface, Context context,boolean isSettingsVisible) {
        this.context = context;
        this.list = list;
        this.adapterInterface = adapterInterface;
        this.isSettingsVisible = isSettingsVisible;
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
            view = View.inflate(context, R.layout.item_music_listview, null);
            //实例化对象
            holder.music = (TextView) view.findViewById(R.id.item_mymusic_music);
            holder.singer = (TextView) view.findViewById(R.id.item_mymusic_singer);
            holder.duration = (TextView) view.findViewById(R.id.item_mymusic_duration);
            holder.position = (TextView) view.findViewById(R.id.item_mymusic_postion);
            holder.settings = view.findViewById(R.id.settings);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        //给控件赋值
        holder.music.setText(list.get(i).name.toString());
        holder.singer.setText(list.get(i).singer.toString());
        holder.duration.setText("");
        //时间需要转换一下
        holder.position.setText(i + 1 + "");
        if (isSettingsVisible) {
            holder.settings.setVisibility(View.VISIBLE);
            holder.settings.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    MyMusic.MyIndexOfChosenMusic = i;
                    adapterInterface.onNewList();
                }
            });
        }
        else holder.settings.setVisibility(View.INVISIBLE);
        return view;
    }

    class ViewHolder {
        TextView music;//歌曲名
        TextView singer;//歌手
        TextView duration;//时长
        TextView position;//序号
        ImageView settings;

    }

}
