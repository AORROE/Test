package com.arrow.mediaplayerdemo;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.Environment;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class MusicService extends Service {
    private MediaPlayer player = new MediaPlayer();
    private MyBind mBinder = new MyBind();
    private int i = 0;

    //歌曲路径
    private String[] musicPath = new String[]{
            Environment.getExternalStorageDirectory() + "/Sounds/a1.mp3",
            Environment.getExternalStorageDirectory() + "/Sounds/a2.mp3",
            Environment.getExternalStorageDirectory() + "/Sounds/a3.mp3",
            Environment.getExternalStorageDirectory() + "/Sounds/a4.mp3"
    };

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }


     class MyBind extends Binder{
        public MusicService getService(){
            return MusicService.this;
        }

        public void startPlay(){
            if (player.isPlaying()){
                player.start();
            }
        }

        public void pauseMusic(){
            if (player.isPlaying()){
                player.pause();
            }
        }

        public void reset(){
            if (player.isPlaying()){
                player.reset();
                iniMediaPlayerFile(i);
            }
        }

        public void closeMedia(){
            if (player.isPlaying()){
                player.stop();
                player.release();
            }
        }
        public void nextMusic() {
            if (player != null && i < 4 && i >= 0) {
                //切换歌曲reset()很重要很重要很重要，没有会报IllegalStateException
                player.reset();
                iniMediaPlayerFile(i + 1);
                //这里的if只要是为了不让歌曲的序号越界，因为只有4首歌
                if (i == 2) {

                } else {
                    i = i + 1;
                }
                startPlay();
            }
        }

        public void preciousMusic() {
            if (player != null && i < 4 && i > 0) {
                player.reset();
                iniMediaPlayerFile(i - 1);
                if (i == 1) {

                } else {

                    i = i - 1;
                }
                startPlay();
            }
        }

        public int getProgress() {

            return player.getDuration();
        }

        public int getPlayPosition() {

            return player.getCurrentPosition();
        }

        public void seekToPositon(int msec) {
            player.seekTo(msec);
        }
    }

    private void iniMediaPlayerFile(int dex) {
        //获取文件路径
        try {
            //此处的两个方法需要捕获IO异常
            //设置音频文件到MediaPlayer对象中
            player.setDataSource(musicPath[dex]);
            //让MediaPlayer对象准备
            player.prepare();
        } catch (IOException e) {
            Log.d(null, "设置资源，准备阶段出错");
            e.printStackTrace();
        }
    }

}
