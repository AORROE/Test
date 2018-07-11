package com.arrow.mediaplayerdemo;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import com.arrow.test.R;

import java.io.IOException;

public class MusicService2 extends Service{
    private static final String TAG = "MyTest";
    private static MediaPlayer player;

    public MusicService2() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return new MyBinder();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        player = MediaPlayer.create(getApplication(), R.raw.test);
        player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
//                mediaPlayer.stop();
////                mediaPlayer.release();
//                mediaPlayer.reset();
            }
        });
        Log.i(TAG, "oooooo连接成功oooooo");
    }


    public class MyBinder extends Binder{
        public MusicService2 getService(){
            return MusicService2.this;
        }

        //判断是否处于播放状态
        public boolean isPlaying(){
            return player.isPlaying();
        }

        //播放或暂停歌曲
        public void play() {
            if (player.isPlaying()) {
                player.pause();
                Log.i(TAG, "暂停音乐");
            } else {
                player.start();
                Log.i(TAG, "播放音乐");
            }

        }

        public void pauseMusic(){
            if (player.isPlaying()){
                player.pause();
            }
        }

        public void resetMusic(){
            player.reset();
        }

        //返回歌曲的长度，单位为毫秒
        public int getDuration(){
            return player.getDuration();
        }

        //返回歌曲目前的进度，单位为毫秒
        public int getCurrenPostion(){
            return player.getCurrentPosition();
        }

        //设置歌曲播放的进度，单位为毫秒
        public void seekTo(int mesc){
            player.seekTo(mesc);
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (player.isPlaying()){
            player.stop();
        }
        player.release();
    }
}
