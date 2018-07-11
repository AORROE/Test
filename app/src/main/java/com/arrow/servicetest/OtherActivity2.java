package com.arrow.servicetest;

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
import android.widget.EditText;
import android.widget.TextView;

import com.arrow.test.R;

public class OtherActivity2 extends AppCompatActivity{

    private Button btn;
    private TextView textView;
    private ContentService2 contentService1;
    private MyConn conn;
    private EditText editText;
    private ContentReceiver2 contentReceiver2;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_other);
        textView = findViewById(R.id.tv);
        btn = findViewById(R.id.btn1);
        editText = findViewById(R.id.ed);
        conn = new MyConn();
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = editText.getText().toString();
                contentService1.handleData(name);
            }
        });
        bindService(new Intent(this,ContentService2.class),conn,BIND_AUTO_CREATE);
        doRegisterReceiver();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(conn);
    }

    private void doRegisterReceiver(){
        contentReceiver2 = new ContentReceiver2();
        IntentFilter intentFilter = new IntentFilter("com.arrow.servicetest.broadcast");
        registerReceiver(contentReceiver2,intentFilter);
    }

    public class MyConn implements ServiceConnection{

        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            contentService1 = ((ContentService2.LocalBinder)iBinder).service1();
        }
        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            contentService1 = null;
        }
    }

    public class ContentReceiver2 extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String name = intent.getStringExtra("name");
            Presons presons = new Presons();
            presons.setName(name);
            textView.setText(presons.getName());
        }
    }
}
