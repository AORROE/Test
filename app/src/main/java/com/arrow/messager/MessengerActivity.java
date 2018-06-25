package com.arrow.messager;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.util.Log;

import com.arrow.test.R;
import com.arrow.utils.Constants;

public class MessengerActivity extends Activity {
    private static final String TAG = "MessengerActivity";
    private Messenger mService;

    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            mService = new Messenger(iBinder);
            Message message = Message.obtain(null, Constants.MSG_FROM_CLIENT);
            Bundle data = new Bundle();
            data.putString("msg","你好呀，我是客户端");
            message.setData(data);
            message.replyTo = mGet;
            try {
                mService.send(message);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {

        }
    };

    private Messenger mGet = new Messenger(new MessengerHandle());

    private class MessengerHandle extends Handler{
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case Constants.MSG_FROM_SERVICE:
                    Log.i(TAG, "handleMessage: "+ msg.getData().getString("reply"));
                    break;
            }
            super.handleMessage(msg);
        }
    }
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messenger);
        Intent intent = new Intent(this,MessengerService.class);
        bindService(intent,connection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(connection);
    }
}
