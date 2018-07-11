package com.arrow.mediaplayerdemo;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.arrow.test.R;

import java.text.SimpleDateFormat;
import java.util.Date;

public class MusicActivity2 extends AppCompatActivity implements View.OnClickListener{
    private SeekBar seekBar2;
    private TextView title;

    private ServiceConnection connection;
    private MusicService2.MyBinder myBinder;
    MusicService2 service2;


    private static final int UPDATE_PROGRESS = 0;
    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case UPDATE_PROGRESS:
                    updateProgress();
                    break;
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music);
        initView();
        connection = new MyConnection();
        Intent intent = new Intent(this,MusicService2.class);
        startService(intent);
        bindService(intent,connection,BIND_AUTO_CREATE);
    }

    public void initView(){
        Button play = findViewById(R.id.play);
        Button pause = findViewById(R.id.pause);
        Button next = findViewById(R.id.next);
        Button up = findViewById(R.id.up);
        play.setOnClickListener(this);
        up.setOnClickListener(this);
        title = findViewById(R.id.titles);
        seekBar2 = findViewById(R.id.seekBar1);
        seekBar2.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if (b){
                    myBinder.seekTo(i);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    private class MyConnection implements ServiceConnection{

        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            service2 = ((MusicService2.MyBinder) iBinder).getService();
            myBinder = (MusicService2.MyBinder) iBinder;
            updatePlayText();
            seekBar2.setMax(myBinder.getDuration());
            seekBar2.setProgress(myBinder.getCurrenPostion());
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {

        }
    }

    private void updateProgress(){
        int currPosition = myBinder.getCurrenPostion();
        seekBar2.setProgress(currPosition);
        title.setText(transferMilliToTime(myBinder.getCurrenPostion()) + "s");
        mHandler.sendEmptyMessageDelayed(UPDATE_PROGRESS,1000);
    }

    public void updatePlayText(){
        if (myBinder.isPlaying()){
            title.setText(transferMilliToTime(myBinder.getCurrenPostion()) + "s");
            mHandler.sendEmptyMessage(UPDATE_PROGRESS);
        }else {
            title.setText(transferMilliToTime(myBinder.getCurrenPostion()) + "s");
        }
    }

    private String transferMilliToTime(int Millis) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("mm:ss");
        String result = simpleDateFormat.format(new Date(Millis));
        return result;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.play:
                myBinder.play();
                Toast.makeText(this,"aaa",Toast.LENGTH_SHORT).show();
                updatePlayText();
                break;
            case R.id.up:
//                myBinder.resetMusic();
                break;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (myBinder.isPlaying()){
            myBinder.pauseMusic();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (myBinder != null){
            myBinder.play();
            mHandler.sendEmptyMessage(UPDATE_PROGRESS);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(connection);
    }

    @Override
    protected void onStop() {
        super.onStop();
        mHandler.removeCallbacksAndMessages(null);
    }
}
