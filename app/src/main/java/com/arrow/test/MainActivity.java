package com.arrow.test;

import android.content.Intent;
import android.os.Environment;
import android.os.UserManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.arrow.intentservice.LocalIntentService;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public class MainActivity extends AppCompatActivity {

    User user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Intent service = new Intent(this, LocalIntentService.class);
        service.putExtra("task_action","com.arrow.action.TASK1");
        startService(service);

        service.putExtra("task_action","com.arrow.action.TASK2");
        startService(service);

        service.putExtra("task_action","com.arrow.action.TASK3");
        startService(service);
//        Button button = findViewById(R.id.btn1);
//        button.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent();
//                intent.setClass(MainActivity.this, SecondActivity.class);
////                User user = new User(0, "jake", true);
//                intent.putExtra("extra_user", user);
//                startActivity(intent);
//            }
//        });
    }

    @Override
    protected void onResume() {
        persistToFile();
        super.onResume();
    }

    private void persistToFile(){
        new Thread(new Runnable() {
            @Override
            public void run() {

                user = new User(1,"arrow",false);
                File dir = new File(Environment
                        .getExternalStorageDirectory().getPath()+"/arrow/test/");
                if (!dir.exists()){
                    dir.mkdirs();
                }
                File cachedFile = new File(Environment
                        .getExternalStorageDirectory().getPath()+"/arrow/test/"+"usercache");
                ObjectOutputStream objectOutputStream = null;
                try {
                    objectOutputStream = new ObjectOutputStream(new FileOutputStream(cachedFile));
                    objectOutputStream.writeObject(user);
                    Log.d("MyTest", "persist user: " + user);
                    Log.d("MyTest", Environment
                            .getExternalStorageDirectory().getPath());
                } catch (IOException e) {
                    e.printStackTrace();
                }finally {
                    if (objectOutputStream !=null){
                        try {
                            objectOutputStream.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }

            }
        }).start();
    }
}
