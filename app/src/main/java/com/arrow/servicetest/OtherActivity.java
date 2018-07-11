package com.arrow.servicetest;

import android.content.ComponentName;
import android.content.Intent;
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

public class OtherActivity extends AppCompatActivity implements ContentService1.Callback{

    private Button btn;
    private TextView textView;
    private ContentService1 contentService1;
    private MyConn conn;
    private EditText editText;

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
        bindService(new Intent(this,ContentService1.class),conn,BIND_AUTO_CREATE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(conn);
    }

    @Override
    public void getPerson(Presons presons) {
        textView.setText(presons.getName());
    }

    public class MyConn implements ServiceConnection{

        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            contentService1 = ((ContentService1.LocalBinder) iBinder).service1();
            contentService1.addCallback(OtherActivity.this);
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            contentService1 = null;
        }
    }
}
