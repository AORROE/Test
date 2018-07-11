package com.arrow.servicetest;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;

import java.util.ArrayList;
import java.util.List;

public class ContentService2 extends Service {

    public ContentService2() {

    }

    @Override
    public IBinder onBind(Intent intent) {
        return new LocalBinder();
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    public final class LocalBinder extends Binder{
        public ContentService2 service1(){
            return ContentService2.this;
        }
    }

//    public interface Callback{
//        void getPerson(Presons presons);
//    }



    public void handleData(final String name){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                sendContentBroadcast(name);
            }
        }).start();
    }

    protected void sendContentBroadcast(String name){
        Intent intent = new Intent();
        intent.setAction("com.arrow.servicetest.broadcast");
        intent.putExtra("name",name);
        sendBroadcast(intent);
    }
}

