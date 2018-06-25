package com.arrow.crashtest;

import android.app.Application;

public class CrashTestApp extends Application {
    private static CrashTestApp crashTestApp;

    @Override
    public void onCreate() {
        super.onCreate();
        crashTestApp = this;

        //为应用设置异常处理，然后程序才能获取未处理的异常
        CrashHandler crashHandler = CrashHandler.getcInstance();
        crashHandler.init(this);
    }

    public static CrashTestApp getInstance(){
        return crashTestApp;
    }
}
