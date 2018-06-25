package com.arrow.bitmaptest;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.StatFs;
import android.support.annotation.NonNull;
import android.util.Log;
import android.util.LruCache;
import android.widget.ImageView;

import com.arrow.test.R;
import com.arrow.utils.MyUtils;
import com.jakewharton.disklrucache.DiskLruCache;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class ImageLoader {
    private static final String TAG = "ImageLoader";

    public static final int MESSAGE_POST_RESULT = 1;

    private static final int CPU_COUNT = Runtime.getRuntime()
            .availableProcessors();
    private static final int CORE_POOL_SIZE = CPU_COUNT + 1;//核心线程数为当前设备CPU核心数+1
    private static final int MAXIMUM_POOL_SIZE = CPU_COUNT * 2 + 1;//最大容量为CPU核心数的2倍+1
    private static final long KEEP_ALIVE = 10L;//闲置超时时长为10秒

    private static final int TAG_KEY_URI = R.id.imageloader_uri;
    private static final long DISK_CACHE_SIZE = 1024 * 1024 * 50;
    private static final int IO_BUFFER_SIZE = 8 * 1024;
    private static final int DISK_CACHE_INDEX = 0;
    private boolean mIsDiskLruCacheCreated = false;

    private static final ThreadFactory sThreadFactory = new ThreadFactory() {
        private final AtomicInteger mCount = new AtomicInteger(1);
        @Override
        public Thread newThread(@NonNull Runnable runnable) {
            return new Thread(runnable,"ImageLoader#" + mCount.getAndIncrement());
        }
    };

    private static final Executor THREAD_POOL_EXECUTOR = new ThreadPoolExecutor(
      CORE_POOL_SIZE,MAXIMUM_POOL_SIZE,KEEP_ALIVE, TimeUnit.SECONDS,
            new LinkedBlockingQueue<Runnable>(),sThreadFactory);


    private Handler mMainHandler = new Handler(Looper.getMainLooper()){
        @Override
        public void handleMessage(Message msg) {
            LoaderResult result = (LoaderResult) msg.obj;
            ImageView imageView = result.imageView;
            String uri = (String) imageView.getTag();
            if (uri.equals(result.uri)){
                imageView.setImageBitmap(result.bitmap);
            }else {
                Log.w(TAG, "set image bitmap,but url has changed, ignored!");
            }
        }
    };

    private Context mContext;
    private ImageResizer mImageResize = new ImageResizer();
    private LruCache<String,Bitmap> mMemoryCache;
    private DiskLruCache mDiskLruCache;

    private ImageLoader(Context context) {
        mContext = context.getApplicationContext();
        int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
        int cacheSize = maxMemory / 8;
        mMemoryCache = new LruCache<String,Bitmap>(cacheSize){
            @Override
            protected int sizeOf(String key, Bitmap value) {
                return value.getRowBytes() * value.getHeight() / 1024;
            }
        };
        File diskCacheDir = getDiskCacheDir(mContext,"bitmap");
        if (!diskCacheDir.exists()){
            diskCacheDir.mkdirs();
        }
        if (getUsableSpace(diskCacheDir) >DISK_CACHE_SIZE){
            try {
                mDiskLruCache = DiskLruCache.open(diskCacheDir,1,1,DISK_CACHE_SIZE);
                mIsDiskLruCacheCreated = true;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static ImageLoader build(Context context){
        return new ImageLoader(context);
    }


    /**
     * 内存缓存的添加和获取
     * @param key
     * @param bitmap
     */
    //添加缓存
    private void addBitmapToMemoryCache(String key,Bitmap bitmap){
        if (getBitmapFromMemCache(key) == null){
            mMemoryCache.put(key,bitmap);
        }
    }
    //获取缓存
    private Bitmap getBitmapFromMemCache(String key){
        return mMemoryCache.get(key);
    }

    /**
     * 异步从内存、SD卡、网络加载bitmap，然后绑定bitmap到imageView上
     * 异步加载接口
     * @param uri
     * @param imageView
     */
    public void bindBitmap(final String uri, final ImageView imageView){
        bindBitmap(uri,imageView,0,0);
    }

    //尝试在内存缓存中获取图片，如果得到直接返回结果,否则在线程池中调用loadBitmap
    public void bindBitmap(final String uri, final ImageView imageView,
                            final int reqWidth, final int reqHeight) {
        imageView.setTag(TAG_KEY_URI,uri);
        Bitmap bitmap = loadBitmapFromMemCache(uri);
        if (bitmap != null){
            imageView.setImageBitmap(bitmap);
            return;
        }
    //当图片加载成功后会将图片，图片的地址以及需要绑定的ImageView封装成LoaderResult对象，
        // 然后通过mMainHandler发送向主线程中发送消息，最后给imageView设置图片
        Runnable loadBitmapTask = new Runnable() {
            @Override
            public void run() {
                Bitmap bitmap = null;
                bitmap = loadBitmap(uri, reqWidth, reqHeight);
                if (bitmap != null){
                    LoaderResult result = new LoaderResult(imageView,uri,bitmap);
                    mMainHandler.obtainMessage(MESSAGE_POST_RESULT,result).sendToTarget();
                }

            }
        };
        THREAD_POOL_EXECUTOR.execute(loadBitmapTask);
    }

    /**
     *同步加载接口
     * 先尝试在内存缓存中读取图片，接着尝试在磁盘缓存中读取图片，最后在网络上拉取图片
     * 这个方法不能在主线程中调用，由loadBitmapFromHttp进行执行环境的检查，通过Looper去判断
     * @param uri http url
     * @param reqWidth 图片的宽
     * @param reqHeight 图片的高
     * @return bitmap,或者null
     * @throws IOException
     */

    public Bitmap loadBitmap(String uri,int reqWidth,int reqHeight){
        Bitmap bitmap = loadBitmapFromMemCache(uri);
        if (bitmap != null){
            Log.i(TAG,  "loadBitmapFromMemCache,url:" + uri);
            return bitmap;
        }
        try {
            bitmap = loadBitmapFromDiskCache(uri, reqWidth, reqHeight);
            if (bitmap != null){
                Log.i(TAG, "loadBitmapFromDisk,url:" + uri);
                return bitmap;
            }
            bitmap = loadBitmapFromHttp(uri,reqWidth,reqHeight);
            Log.i(TAG, "loadBitmapFromHttp,url:" + uri);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (bitmap == null && !mIsDiskLruCacheCreated){
            Log.w(TAG,  "encounter error, DiskLruCache is not created.");
            try {
                bitmap = downloadBitmapFromUrl(uri);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return bitmap;
    }

    private Bitmap loadBitmapFromMemCache(String url){
        final String key = hashKeyFormUrl(url);
        Bitmap bitmap = getBitmapFromMemCache(key);
        return bitmap;
    }
    /**
     * 磁盘缓存的添加和获取
     * @param url
     * @param reqWidth
     * @param reqHeight
     * @return
     * @throws IOException
     */
    private Bitmap loadBitmapFromHttp(String url,int reqWidth,int reqHeight) throws IOException{
        //判断是否在主线程，如果在抛出异常
        if (Looper.myLooper() == Looper.getMainLooper()){
            throw new RuntimeException("can not visit network from UI Thread.");
        }
        if (mDiskLruCache == null){
            return null;
        }
        String key = hashKeyFormUrl(url);
        DiskLruCache.Editor editor = mDiskLruCache.edit(key);
        if (editor != null){
            OutputStream outputStream = editor.newOutputStream(DISK_CACHE_INDEX);
            if (downloadUrlToStream(url,outputStream)){
                editor.commit();
            }else {
                editor.abort();
            }
            mDiskLruCache.flush();
        }
        return loadBitmapFromDiskCache(url, reqWidth, reqHeight);
    }
    //磁盘获取通过Snapshot来获取，得到一个FileInputStream对象，然后通过FileDescriptor来加载压缩后的图片，最后将加载后的Bitmap添加到内存缓存中
    private Bitmap loadBitmapFromDiskCache(String url, int reqWidth,
                                           int reqHeight) throws IOException {
        if (Looper.myLooper() == Looper.getMainLooper()){
            Log.w(TAG, "load bitmap from UI Thread, it's not recommended!");
        }
        if (mDiskLruCache == null){
            return null;
        }

        Bitmap bitmap = null;
        String key = hashKeyFormUrl(url);
        DiskLruCache.Snapshot snaShot = mDiskLruCache.get(key);
        if (snaShot != null){
            FileInputStream fileInputStream = (FileInputStream) snaShot.getInputStream(DISK_CACHE_INDEX);
            FileDescriptor fileDescriptor = fileInputStream.getFD();
            bitmap = mImageResize.decodeSampleSizeBitmapFromFileDescriptor(fileDescriptor,reqWidth,reqHeight);
            if (bitmap != null){
                addBitmapToMemoryCache(key,bitmap);
            }
        }
        return bitmap;
    }

    public boolean downloadUrlToStream(String urlString,
                                       OutputStream outputStream) throws IOException {
        HttpURLConnection urlConnection = null;
        BufferedOutputStream out = null;
        BufferedInputStream in = null;

        try {
            final URL url = new URL(urlString);
            urlConnection = (HttpURLConnection) url.openConnection();
            in = new BufferedInputStream(urlConnection.getInputStream(),
                    IO_BUFFER_SIZE);
            out = new BufferedOutputStream(outputStream, IO_BUFFER_SIZE);

            int b;
            while ((b = in.read()) != -1) {
                out.write(b);
            }
            return true;
        } catch (IOException e) {
            Log.e(TAG, "downloadBitmap failed." + e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            MyUtils.close(out);
            MyUtils.close(in);
        }
        return false;
    }

    private Bitmap downloadBitmapFromUrl(String urlString) throws IOException {
        Bitmap bitmap = null;
        HttpURLConnection urlConnection = null;
        BufferedInputStream in = null;

        try {
            final URL url = new URL(urlString);
            urlConnection =(HttpURLConnection) url.openConnection();
            in = new BufferedInputStream(urlConnection.getInputStream(),IO_BUFFER_SIZE);
            bitmap = BitmapFactory.decodeStream(in);
        }  catch (IOException e) {
            Log.e(TAG, "Error in downloadBitmap: " + e);
        }finally {
            if (urlConnection != null){
                urlConnection.disconnect();
            }
            in.close();
        }
        return bitmap;
    }

    private String hashKeyFormUrl(String url){
        String cacheKey;
        try {
            final MessageDigest mDigest = MessageDigest.getInstance("MD5");
            mDigest.update(url.getBytes());
            cacheKey = bytesToHexString(mDigest.digest());
        } catch (NoSuchAlgorithmException e) {
            cacheKey = String.valueOf(url.hashCode());
        }
        return cacheKey;
    }

    private String bytesToHexString(byte[] bytes){
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i< bytes.length;i++){
            String hex = Integer.toHexString(0xFF & bytes[i]);
            if (hex.length() == 1){
                builder.append('0');
            }
            builder.append(hex);
        }
        return builder.toString();
    }

    public File getDiskCacheDir(Context mContext, String bitmap) {
        boolean externalStorageAvailable = Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
        final String cachePath;
        if (externalStorageAvailable){
            cachePath = mContext.getExternalCacheDir().getPath();
        }else {
            cachePath = mContext.getCacheDir().getPath();
        }
        return new File(cachePath + File.separator + bitmap);
    }

    @TargetApi(Build.VERSION_CODES.GINGERBREAD)
    private long getUsableSpace(File path){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD){
            return path.getUsableSpace();
        }
        final StatFs stats = new StatFs(path.getPath());
        return stats.getBlockSizeLong() * stats.getAvailableBlocksLong();
    }

    private static class LoaderResult {
        public ImageView imageView;
        public String uri;
        public Bitmap bitmap;

        public LoaderResult(ImageView imageView, String uri, Bitmap bitmap) {
            this.imageView = imageView;
            this.uri = uri;
            this.bitmap = bitmap;
        }
    }
}
