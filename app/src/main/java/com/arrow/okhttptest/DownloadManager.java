package com.arrow.okhttptest;

import android.util.Log;

import com.arrow.crashtest.CrashTestApp;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicReference;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import io.reactivex.schedulers.Schedulers;
import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class DownloadManager {
    private static AtomicReference<DownloadManager> INSTANCE = new AtomicReference<>();
    private HashMap<String, Call> downCall;
    private OkHttpClient okHttpClient;

    public static DownloadManager getInstance(){
        for (;;){
            DownloadManager downloadManager = INSTANCE.get();
            if (downloadManager != null){
                return downloadManager;
            }
            downloadManager = new DownloadManager();
            if (INSTANCE.compareAndSet(null,downloadManager)){
                return downloadManager;
            }
        }
    }

    public DownloadManager() {
        downCall = new HashMap<>();
        okHttpClient = new OkHttpClient.Builder().build();
    }

    /**
     * 使用Rxjava下载文件
     * filter(s -> !downCall.containsKey(s))//call的map已经有了,就证明正在下载,则这次不下载
     * s -> Observable.just(createDownInfo(s))
     * @param url
     * @param downloadObserver
     */
    public void download(String url,DownloadObserver downloadObserver){
        Log.i("Mytest", this.toString());
        Observable.just(url)
                .filter(s -> !downCall.containsKey(s))//call的map已经有了,就证明正在下载,则这次不下载
                .flatMap((Function<String, ObservableSource<DownloadInfo>>) s -> Observable.just(createDownInfo(s)))
                .map((Function<DownloadInfo, Object>) this::getRealFileName)//检测本地文件夹,生成新的文件名
                .flatMap(downloadInfo -> Observable.create(new DownloadSubscribe((DownloadInfo) downloadInfo)))//下载
                .observeOn(AndroidSchedulers.mainThread())//在主线程回调
                .subscribeOn(Schedulers.io())//在子线程执行
                .subscribe(downloadObserver);//添加观察者
    }

    /**
     * 取消监听
     * @param url
     */
    public void cancel(String url){
        Call call = downCall.get(url);
        if (call != null){
            call.cancel();
        }
        downCall.remove(url);
    }

    /**
     * 创建DownloadInfo
     * @param url
     * @return
     */
    private DownloadInfo createDownInfo(String url){
        DownloadInfo downloadInfo = new DownloadInfo(url);
        long contentLength = getContentLength(url);
        downloadInfo.setTotal(contentLength);
        String fileName = url.substring(url.lastIndexOf("/"));
        downloadInfo.setFileName(fileName);
        return downloadInfo;
    }

    /**
     * 获得真正的文件名
     * @param downloadInfo
     * @return
     */
    private DownloadInfo getRealFileName(DownloadInfo downloadInfo){
        String fileName = downloadInfo.getFileName();
        long downloadLength = 0;
        long contentLength = downloadInfo.getTotal();
        File file = new File(CrashTestApp.getInstance().getFilesDir(),fileName);
        if (file.exists()){
            downloadLength = file.length();
        }
        int i = 1;
        while (downloadLength >= contentLength){
            int dotIndex = fileName.lastIndexOf('.');
            String fileNameOther;
            if (dotIndex == -1){
                fileNameOther = fileName + "(" + i + ")";
            }else {
                fileNameOther = fileName.substring(0,dotIndex) + "(" + i + ")" + fileName.substring(dotIndex);
            }
            File newFile = new File(CrashTestApp.getInstance().getFilesDir(),fileNameOther);
            file = newFile;
            downloadLength = newFile.length();
            i++;
        }
        downloadInfo.setProgress(downloadLength);
        downloadInfo.setFileName(file.getName());
        return downloadInfo;
    }

    /**
     *
     */
    private class DownloadSubscribe implements ObservableOnSubscribe<DownloadInfo>{
        private DownloadInfo downloadInfo;

        public DownloadSubscribe(DownloadInfo downloadInfo) {
            this.downloadInfo = downloadInfo;
        }

        @Override
        public void subscribe(ObservableEmitter<DownloadInfo> emitter) throws Exception {
            String url = downloadInfo.getUrl();
            long downloadLength = downloadInfo.getProgress();
            long contentLength = downloadInfo.getTotal();
            emitter.onNext(downloadInfo);

            Request request = new Request.Builder()
                    .addHeader("RANGE","bytes=" + downloadLength + "-" + contentLength)
                    .url(url)
                    .build();
            Call call = okHttpClient.newCall(request);
            downCall.put(url,call);
            Response response = call.execute();

            File file = new File(CrashTestApp.getInstance().getFilesDir(),downloadInfo.getFileName());
            InputStream is = null;
            FileOutputStream fileOutputStream = null;

            try{
                is = response.body().byteStream();
                fileOutputStream = new FileOutputStream(file,true);
                byte[] buffer = new byte[2048];
                int len;
                while ((len = is.read(buffer))!= -1){
                    fileOutputStream.write(buffer,0,len);
                    downloadLength += len;
                    downloadInfo.setProgress(downloadLength);
                    emitter.onNext(downloadInfo);
                }
                fileOutputStream.flush();
                downCall.remove(url);
            }catch (Exception e){

            }finally {
                IOUtil.closeAll(is,fileOutputStream);
            }
            emitter.onComplete();
        }
    }


    /**
     * 获得url的长度
     * @param downloadurl
     * @return
     */
    private long getContentLength(String downloadurl){
        Request request = new Request.Builder()
                .url(downloadurl)
                .build();
        try {
            Response response = okHttpClient.newCall(request).execute();
            if (response != null && response.isSuccessful()){
                long contentLength = response.body().contentLength();
                response.close();
                return contentLength == 0 ? DownloadInfo.TOTAL_ERROR : contentLength;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return DownloadInfo.TOTAL_ERROR;
    }
}
