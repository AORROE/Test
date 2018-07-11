package com.arrow.servicetest;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.arrow.test.R;

import java.util.Observable;
import java.util.Observer;

/**
 * 观察者模式
 */
public class Main4Activity extends AppCompatActivity implements Observer{

    private NewContentService service;
    private MyServiceConnection conn;
    private TextView tex;
    private Button btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main4);
        tex = findViewById(R.id.tv);
        btn = findViewById(R.id.btn2);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                service.handleDatas("Hello");
            }
        });
        conn = new MyServiceConnection();
        bindService(new Intent(this,NewContentService.class),conn,BIND_AUTO_CREATE);
    }

    private class MyServiceConnection implements ServiceConnection{

        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            service = ((NewContentService.Localbinder) iBinder).getService();
            service.addObservable(Main4Activity.this);
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            service = null;
        }
    }

    @Override
    public void update(Observable observable, Object o) {
        Presons presons = (Presons) o;
        tex.setText(presons.getName());
    }
}
