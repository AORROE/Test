package com.arrow.servicetest;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.arrow.test.R;

/**
 * BrocastReceiver
 */
@SuppressLint("Registered")
public class PersonActivity2 extends AppCompatActivity{

    private TextView tv;
    private Button btn;
    private ContentService2 service1;
    private MyServiceConn conn;
    private ContentReceiver contentReceiver;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main_person);
        tv = findViewById(R.id.tv);
        btn = findViewById(R.id.btn1);
        conn = new MyServiceConn();
        bindService(new Intent(this,ContentService2.class),conn,BIND_AUTO_CREATE);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(PersonActivity2.this,OtherActivity2.class));
//                service1.handleData("6666");
            }
        });
        doRegisterReceiver();
    }

    private void doRegisterReceiver(){
        contentReceiver = new ContentReceiver();
        IntentFilter intentFilter = new IntentFilter("com.arrow.servicetest.broadcast");
        registerReceiver(contentReceiver,intentFilter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(conn);
        if (contentReceiver != null){
            unregisterReceiver(contentReceiver);
        }
    }

//    @Override
//    public void getPerson(Presons presons) {
//        tv.setText(presons.getName());
//    }

    public class MyServiceConn implements ServiceConnection {

        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            service1 = ((ContentService2.LocalBinder)iBinder).service1();
//            service1.addCallback(PersonActivity2.this);
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            service1 = null;
        }
    }

    public class ContentReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            String name = intent.getStringExtra("name");
            Presons presons = new Presons();
            presons.setName(name);
            tv.setText(presons.getName());
        }
    }
}
