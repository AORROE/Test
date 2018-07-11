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

public class ContentService1 extends Service {
    private List<Callback> list;

    public ContentService1() {

    }

    @Override
    public IBinder onBind(Intent intent) {
        return new LocalBinder();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        list = new ArrayList<Callback>();
    }

    public final class LocalBinder extends Binder{
        public ContentService1 service1(){
            return ContentService1.this;
        }
    }

    public interface Callback{
        void getPerson(Presons presons);
    }

    public void addCallback(Callback callback) {
        list.add(callback);
    }

    public void handleData(final String name){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                handler.sendMessage(handler.obtainMessage(0,name));
            }
        }).start();
    }

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            String name = (String) msg.obj;
            Presons presons = new Presons();
            presons.setName(name);
            for (int i = 0;i <list.size();i++){
                list.get(i).getPerson(presons);
            }
        }
    };
}

