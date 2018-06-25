package com.arrow.crashtest;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import android.os.Process;
import android.util.Log;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CrashHandler implements Thread.UncaughtExceptionHandler{

    private static final String TAG = "CrashHandler";
    private static final boolean DEBUG = true;

    private static final String PATH = Environment.getExternalStoragePublicDirectory(
            Environment.DIRECTORY_PICTURES).getPath() + "/CrashTest/log/";
    private static final String FILE_NAME = "crash";
    private static final String FILE_NAME_SUFFX = ".trace";

    private static CrashHandler cInstance = new CrashHandler();
    private Thread.UncaughtExceptionHandler mDefaultCrashHandler;
    private Context mContext;

    public CrashHandler() {
    }

    public static CrashHandler getcInstance(){
        return cInstance;
    }

    public void init(Context context){
        mDefaultCrashHandler = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(this);
        mContext = context.getApplicationContext();
    }

    /**
     * 当程序有未被捕获的异常，系统会自动调用#UncaughtExcetion
     * @param thread 表示出现未捕获异常线程
     * @param throwable 表示未捕获的异常，可以得到异常信息
     */
    @Override
    public void uncaughtException(Thread thread, Throwable throwable) {
        try {
            //导出异常信息到SD卡中
            dumpExceptionToSDCard(throwable);
            uploadExceptionToServer();
            ////这里可以通过网络上传异常信息到服务器，便于开发人员分析日志从而解决bug
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        throwable.printStackTrace();

        //如果系统提供了默认的异常处理器，则交给系统去结束我们的程序，否则就由我们自己结束自己
        if (mDefaultCrashHandler != null){
            mDefaultCrashHandler.uncaughtException(thread,throwable);
        }else {
            Process.killProcess(Process.myPid());
        }

    }


    //将异常信息到如SD卡中
    private void dumpExceptionToSDCard(Throwable ex) throws PackageManager.NameNotFoundException, IOException {
        //判断SD卡是否存在或是否可用，如果否无法把异常写入SD卡中
        if(!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
            if (DEBUG){
                Log.w(TAG, "SDK不存在或无法使用");
                return;
            }
        }
        File dir = new File(PATH);
        if (!dir.exists()){
            dir.mkdirs();
        }
        long current = System.currentTimeMillis();
        String time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(current));
        File file = new File(PATH + FILE_NAME + time + FILE_NAME_SUFFX);
        Log.i(TAG, String.valueOf(dir.exists()) + file.toString());
        try {
            PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(file)));
            pw.print(time);
            dumpPhoneInfo(pw);
            pw.println();
            ex.printStackTrace(pw);
            pw.close();
            Log.i(TAG, file.toString());
        } catch (IOException e) {
//            Log.e(TAG, "dump crash info failed" + e.toString());
            e.printStackTrace();
        }
    }

    //获取Android手机的信息
    private void dumpPhoneInfo(PrintWriter pw) throws PackageManager.NameNotFoundException {
        PackageManager pm = mContext.getPackageManager();
        PackageInfo pi = pm.getPackageInfo(mContext.getPackageName(),PackageManager.GET_ACTIVITIES);
        pw.print("App Version --->");
        pw.print(pi.versionName);
        pw.print('_');
        pw.println(pi.versionCode);

        //android版本号
        pw.print("OS Version --->");
        pw.print(Build.VERSION.RELEASE);
        pw.print("_");
        pw.println(Build.VERSION.SDK_INT);

        //手机制造商
        pw.print("Vendor --->");
        pw.println(Build.MANUFACTURER);

        //手机型号
        pw.print("Model --->");
        pw.println(Build.MODEL);

        //cpu架构
//        pw.print("CPU ABI --->");
//        pw.println(Build.CPU_ABI);
    }

    private void uploadExceptionToServer(){
        //TODO Upload Exception Message To Your Web Server
    }
}
