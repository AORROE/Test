package com.arrow.servicetest;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;

import java.util.Observable;
import java.util.Observer;

public class NewContentService extends Service {

    private MyObserver observable;

    @Override
    public IBinder onBind(Intent intent) {
        return new Localbinder();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        observable = new MyObserver();
    }

    public void addObservable(Observer observer){
        observable.addObserver(observer);
    }

    public class Localbinder extends Binder{
        public NewContentService getService(){
            return NewContentService.this;
        }
    }

    public void handleDatas(final String name){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                myHandler.sendMessage(myHandler.obtainMessage(0,name));
            }
        }).start();
    }

    @SuppressLint("HandlerLeak")
    private Handler myHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            String name = (String) msg.obj;
            Presons presons = new Presons();
            presons.setName(name);
            observable.notifyChanged(presons);
        }
    };

    public class MyObserver extends Observable{
        public void notifyChanged(Object o){
            this.setChanged();
            this.notifyObservers(o);
        }
    }
}
