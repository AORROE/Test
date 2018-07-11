package com.arrow.servicetest;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.arrow.test.R;


@SuppressLint("Registered")
public class PersonActivity extends AppCompatActivity implements ContentService1.Callback{

    private TextView tv;
    private Button btn;
    private ContentService1 service1;
    private MyServiceConn conn;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main_person);
        tv = findViewById(R.id.tv);
        btn = findViewById(R.id.btn1);
        conn = new MyServiceConn();
        bindService(new Intent(this,ContentService1.class),conn,BIND_AUTO_CREATE);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(PersonActivity.this,OtherActivity.class));
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(conn);
    }

    @Override
    public void getPerson(Presons presons) {
        tv.setText(presons.getName());
    }

    public class MyServiceConn implements ServiceConnection {

        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            service1 = ((ContentService1.LocalBinder)iBinder).service1();
            service1.addCallback(PersonActivity.this);
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            service1 = null;
        }
    }
}
