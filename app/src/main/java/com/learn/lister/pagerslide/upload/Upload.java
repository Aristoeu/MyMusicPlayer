package com.learn.lister.pagerslide.upload;

import android.util.Log;

import com.learn.lister.pagerslide.data.MyMusic;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Upload implements MusicModel {
    public void upload(String m, final OnMusicListener onMusicListener,String n){
        OkHttpClient client = new OkHttpClient();
        RequestBody body = new FormBody.Builder()
                .add("keyword",m)
                .add("type","song")
                .add("pageSize",n)
                .add("page","0")
                .build();

        final Request request = new Request.Builder()
                .url("https://v1.itooi.cn/netease/search")
                .post(body).build();
        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d("<<<>>>","<<<<e="+e);
                onMusicListener.onFailed();

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if(response.isSuccessful()) {
                    String d = response.body().string();
                    Log.d("<<<>>>","<<<<d="+d);
                    onMusicListener.onSuccess(d);
                }
                else onMusicListener.onError();
            }
        });
    }
    public void getMusicUrl(String m, final OnMusicListener onMusicListener, final int i){
        OkHttpClient client = new OkHttpClient();

        RequestBody body = new FormBody.Builder()
                .add("id",m)
                .add("quality","flac")
                .add("isRedirect","0")
                .build();

        final Request request = new Request.Builder()
                .url("https://v1.itooi.cn/netease/url")
                .post(body).build();
        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d("<<<>>>","<<<<e="+e);
                onMusicListener.onFailed();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if(response.isSuccessful()) {
                    String d = response.body().string();
                    Log.d("<<<>>>","<<<<d="+d);
                    String regex = "\"data\":\"(.*)\"";
                    Pattern pattern = Pattern.compile(regex);
                    Matcher matcher = pattern.matcher(d);
                    while (matcher.find()) {
                        Log.d("<<<>>>",matcher.group(1));
                        //MyMusic.url = matcher.group(1);
                        if (MyMusic.OnlineMusicList.size()>=(i+1))
                        MyMusic.OnlineMusicList.get(i).MusicUrl = matcher.group(1);
                        if (MyMusic.OnlineMusicList.size()>=(i+1))
                        Log.d("<<<>>>","<<<>>>"+ MyMusic.OnlineMusicList.get(i).MusicUrl+"<<<>>>");
                        onMusicListener.onSuccess(matcher.group(1));
                    }
                }
                else onMusicListener.onError();
            }
        });
    }
}
