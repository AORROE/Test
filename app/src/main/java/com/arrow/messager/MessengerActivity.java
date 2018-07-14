package com.arrow.messager;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.arrow.test.R;
import com.arrow.utils.Constants;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class MessengerActivity extends Activity {
//    private static final String TAG = "MessengerActivity";
//    private Messenger mService;
//
//    private ServiceConnection connection = new ServiceConnection() {
//        @Override
//        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
//            mService = new Messenger(iBinder);
//            Message message = Message.obtain(null, Constants.MSG_FROM_CLIENT);
//            Bundle data = new Bundle();
//            data.putString("msg","你好呀，我是客户端");
//            message.setData(data);
//            message.replyTo = mGet;
//            try {
//                mService.send(message);
//            } catch (RemoteException e) {
//                e.printStackTrace();
//            }
//        }
//
//        @Override
//        public void onServiceDisconnected(ComponentName componentName) {
//
//        }
//    };
//
//    private Messenger mGet = new Messenger(new MessengerHandle());
//
//    private class MessengerHandle extends Handler{
//        @Override
//        public void handleMessage(Message msg) {
//            switch (msg.what){
//                case Constants.MSG_FROM_SERVICE:
//                    Log.i(TAG, "handleMessage: "+ msg.getData().getString("reply"));
//                    break;
//            }
//            super.handleMessage(msg);
//        }
//    }

    TextView textView;
    Button btn;
    ImageView imageView;

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            textView.setText(msg.what + "s");
        }
    };

    private Handler mHandler = new Handler();

    @SuppressLint("HandlerLeak")
    private Handler lHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            imageView.setImageBitmap((Bitmap) msg.obj);
        }
    };
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messenger);
        btn = findViewById(R.id.btn1);
        textView =findViewById(R.id.tv1);
        imageView = findViewById(R.id.imag);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        final Bitmap bitmap = loadPicFromInternet();
                        Message message = Message.obtain(lHandler);
                        message.obj = bitmap;
                        lHandler.sendMessage(message);
//                        Handler lHandler = new Handler(Looper.getMainLooper());
//                        lHandler.post(new Runnable() {
//                            @Override
//                            public void run() {
//                                imageView.setImageBitmap(bitmap);
//                            }
//                        });
                    }
                }).start();

//                for (int i=0;i<10;i++){
//                    //message
////                    Message message = Message.obtain(handler);
////                    message.what = 10 -i;
////                    handler.sendMessageDelayed(message,1000 * i);
//
//                    //Runnable
//                    final int second = i;
//                    mHandler.postDelayed(new Runnable() {
//                        @Override
//                        public void run() {
//                            textView.setText(10 - second + "s");
//                        }
//                    },1000 * i);
//                }
            }
        });
//        Intent intent = new Intent(this,MessengerService.class);
//        bindService(intent,connection, Context.BIND_AUTO_CREATE);
    }

    public Bitmap loadPicFromInternet(){
        Bitmap bitmap = null;
        int respondCode = 0;
        InputStream is =null;
        try {
            URL url = new URL("https://images.pexels.com/photos/45170/kittens-cat-cat-puppy-rush-45170.jpeg?auto=compress&cs=tinysrgb&dpr=2&h=650&w=940");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(10000);
            connection.setReadTimeout(5000);
            connection.connect();
            respondCode = connection.getResponseCode();
            if (respondCode == 200){
                is = connection.getInputStream();
                bitmap = BitmapFactory.decodeStream(is);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bitmap;
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
//        unbindService(connection);
    }
}
