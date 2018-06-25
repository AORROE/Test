package com.arrow.bitmaptest;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.util.LruCache;

import com.jakewharton.disklrucache.DiskLruCache;

import java.io.FileDescriptor;

public class ImageResizer {
    private static final String TAG = "ImageResizer";

    public ImageResizer() {
    }


    //从资源中加载一个Bitmap对象
    public Bitmap decodeSampledBitmapForResource(Resources res ,int resId,int reqWidth,int reqHeight){
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(res,resId,options);

        //计算采样率inSampleSize
        options.inSampleSize = calculateInSampleSize(options,reqWidth,reqHeight);

        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeResource(res,resId,options);
    }

    //从文件中加载一个Bitmap对象
    public Bitmap decodeSampleSizeBitmapFromFileDescriptor(FileDescriptor fd,int reqWidth,int reqHeight){
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFileDescriptor(fd,null,options);

        options.inSampleSize = calculateInSampleSize(options,reqWidth,reqHeight);
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFileDescriptor(fd,null,options);
    }



    public int calculateInSampleSize(BitmapFactory.Options options,int reqWidth,int reqHeight){
        if (reqWidth == 0 || reqHeight == 0){
            return  1;
        }

        //获取image的宽高
        int height = options.outHeight;
        int width = options.outWidth;
        Log.i(TAG, "origin, w=" + width + " h=" + height);
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth){
            int halfHeight = height/2;
            int halfWidth = width/2;

            while((halfHeight / inSampleSize) >= reqHeight
                    && halfWidth / inSampleSize >= reqWidth){
                inSampleSize *= 2;
            }
        }
        Log.i(TAG, "sampleSize:" + inSampleSize);
        return inSampleSize;
    }
}
