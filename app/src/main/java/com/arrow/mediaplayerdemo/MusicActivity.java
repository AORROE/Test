package com.arrow.mediaplayerdemo;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Xml;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.arrow.test.MainActivity;
import com.arrow.test.R;

import java.text.SimpleDateFormat;
import java.util.Date;

public class MusicActivity extends AppCompatActivity implements View.OnClickListener{
    public MusicService mService;
    public MusicService.MyBind myBind;
//    ServiceConnection conn;
    public boolean isStop;

    public SeekBar seekBar;
    TextView curTime;
    TextView totalTime;
    TextView title;

    private Handler handler = new Handler();
    Intent intent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music);
        init();
       intent = new Intent(this,MusicService.class);
        //判断权限够不够，不够就给
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
            }, 1);
        } else {
            //够了就设置路径等，准备播放
            bindService(intent, conn, BIND_AUTO_CREATE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    bindService(intent, conn, BIND_AUTO_CREATE);
                } else {
                    Toast.makeText(this, "权限不够获取不到音乐，程序将退出", Toast.LENGTH_SHORT).show();
                    finish();
                }
                break;
            default:
                break;
        }
    }

    private void init() {
        Button play = findViewById(R.id.play);
        Button pause = findViewById(R.id.pause);
        Button next = findViewById(R.id.next);
        Button up = findViewById(R.id.up);

        curTime = findViewById(R.id.curTime);
        totalTime = findViewById(R.id.totalTime);
        title = findViewById(R.id.title);
        seekBar = findViewById(R.id.seekBar1);

        play.setOnClickListener(this);
        pause.setOnClickListener(this);
        next.setOnClickListener(this);
        up.setOnClickListener(this);
    }
    ServiceConnection conn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            mService = ((MusicService.MyBind) iBinder).getService();
            myBind = (MusicService.MyBind) iBinder;
            seekBar.setMax(myBind.getProgress());
            seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                    if (b){
                        myBind.seekToPositon(seekBar.getProgress());
                    }
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {

                }
            });
            handler.post(mRunnable);
            Log.d("Mytest", "Service与Activity已连接");
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {

        }
    };


    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(mRunnable);
        myBind.closeMedia();
        unbindService(conn);
    }



    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.play:
                myBind.startPlay();
                break;
            case R.id.pause:
                myBind.pauseMusic();
                break;
            case R.id.next:
                myBind.nextMusic();
                break;
            case R.id.up:
                myBind.preciousMusic();
                break;
        }
    }

    private Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            seekBar.setProgress(myBind.getPlayPosition());
            title.setText(transferMilliToTime(myBind.getPlayPosition()) + "s");
            handler.postDelayed(mRunnable,1000);
        }
    };

    private String transferMilliToTime(int Millis) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("mm:ss");
        String result = simpleDateFormat.format(new Date(Millis));
        return result;
    }

}
