package com.arrow.okhttptest;

import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.arrow.test.MainActivity;
import com.arrow.test.R;

public class OkhttpActivity extends AppCompatActivity implements View.OnClickListener{
    private Button downloadBtn1, downloadBtn2, downloadBtn3;
    private Button cancelBtn1, cancelBtn2, cancelBtn3;
    private ProgressBar progress1, progress2, progress3;
    private String url1 = "https://images.pexels.com/photos/459225/pexels-photo-459225.jpeg?auto=compress&cs=tinysrgb&dpr=2&h=650&w=940";
    private String url2 = "http://192.168.31.169:8080/out/music.mp3";
    private String url3 = "http://192.168.31.169:8080/out/code.zip";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_okhttp);
        init();
    }

    public void init(){
        downloadBtn1 = findViewById(R.id.main_btn_down1);
        downloadBtn2 = findViewById(R.id.main_btn_down2);
        downloadBtn3 = findViewById(R.id.main_btn_down3);

        cancelBtn1 = findViewById(R.id.main_btn_cancel1);
        cancelBtn2 = findViewById(R.id.main_btn_cancel2);
        cancelBtn3 = findViewById(R.id.main_btn_cancel3);

        progress1 = findViewById(R.id.main_progress1);
        progress2 = findViewById(R.id.main_progress2);
        progress3 = findViewById(R.id.main_progress3);

        downloadBtn1.setOnClickListener(this);
        downloadBtn2.setOnClickListener(this);
        downloadBtn3.setOnClickListener(this);

        cancelBtn1.setOnClickListener(this);
        cancelBtn2.setOnClickListener(this);
        cancelBtn3.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.main_btn_down1:
                DownloadManager.getInstance().download(url1,new DownloadObserver(){
                    @Override
                    public void onNext(DownloadInfo downloadInfo) {
                        super.onNext(downloadInfo);
                        progress1.setMax((int)downloadInfo.getTotal());
                        progress1.setProgress((int) downloadInfo.getProgress());
                        Log.i("Mytest", downloadInfo.getTotal()+"  ---  " + downloadInfo.getProgress() + downloadInfo.getFileName());
                    }

                    @Override
                    public void onComplete() {
                        super.onComplete();
                        if (downloadInfo != null){
                            Toast.makeText(OkhttpActivity.this,"Complete",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                break;
            case R.id.main_btn_down2:
                DownloadManager.getInstance().download(url2,new DownloadObserver(){
                    @Override
                    public void onNext(DownloadInfo downloadInfo) {
                        super.onNext(downloadInfo);
                        progress1.setMax((int)downloadInfo.getTotal());
                        progress1.setProgress((int) downloadInfo.getProgress());
                    }

                    @Override
                    public void onComplete() {
                        super.onComplete();
                        Toast.makeText(OkhttpActivity.this,
                                downloadInfo.getFileName() + Uri.encode("下载完成"),
                                Toast.LENGTH_SHORT).show();
                    }
                });
                break;
            case R.id.main_btn_down3:
                DownloadManager.getInstance().download(url3,new DownloadObserver(){
                    @Override
                    public void onNext(DownloadInfo downloadInfo) {
                        super.onNext(downloadInfo);
                        progress1.setMax((int)downloadInfo.getTotal());
                        progress1.setProgress((int) downloadInfo.getProgress());
                    }

                    @Override
                    public void onComplete() {
                        super.onComplete();
                        Toast.makeText(OkhttpActivity.this,
                                downloadInfo.getFileName() + Uri.encode("下载完成"),
                                Toast.LENGTH_SHORT).show();
                    }
                });
                break;
            case R.id.main_btn_cancel1:
                DownloadManager.getInstance().cancel(url1);
                break;
            case R.id.main_btn_cancel2:
                DownloadManager.getInstance().cancel(url2);
                break;
            case R.id.main_btn_cancel3:
                DownloadManager.getInstance().cancel(url3);
                break;
        }
    }
}
