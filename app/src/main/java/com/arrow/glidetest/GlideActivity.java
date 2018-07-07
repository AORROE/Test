package com.arrow.glidetest;

import android.annotation.SuppressLint;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.arrow.test.R;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.FutureTarget;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;

import java.io.File;
import java.lang.annotation.Target;
import java.util.concurrent.ExecutionException;

public class GlideActivity extends AppCompatActivity {

    ImageView imageView;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_glide);
        imageView = findViewById(R.id.img);
    }

    @SuppressLint("CheckResult")
    public void loadImage(View view){
        Log.i("Mytest", "loadImage: ");
        String image = "http://guolin.tech/book.png";
//        RequestOptions options = new RequestOptions();
//        options.override(com.bumptech.glide.request.target.Target.SIZE_ORIGINAL);
        Glide.with(this)
                .load(image)
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, com.bumptech.glide.request.target.Target<Drawable> target, boolean isFirstResource) {
                        return false;
                    }
                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, com.bumptech.glide.request.target.Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        return false;
                    }
                })
                .into(imageView);
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//
//                try {
//                    String url = "http://www.guolin.tech/book.png";
//                    FutureTarget<File> target = Glide.with(getApplicationContext())
//                            .asFile()
//                            .load(url)
//                            .submit();
//                    final File imageFile = target.get();
//                    runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            Toast.makeText(getApplicationContext(),imageFile.getPath(),Toast.LENGTH_SHORT).show();
//                        }
//                    });
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                } catch (ExecutionException e) {
//                    e.printStackTrace();
//                }
//            }
//        }).start();
    }
}
