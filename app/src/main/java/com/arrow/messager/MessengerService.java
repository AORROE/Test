package com.arrow.messager;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;

import com.arrow.utils.Constants;

public class MessengerService extends Service {

    private static final String TAG = "MessengerService";
    private class MessengerHandle extends Handler{
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case Constants.MSG_FROM_CLIENT:
                    Log.i(TAG, "handleMessage: "+msg.getData().getString("msg"));
                    Messenger client = msg.replyTo;
                    Message replyMsg = Message.obtain(null,Constants.MSG_FROM_SERVICE);
                    Bundle bundle = new Bundle();
                    bundle.putString("reply","Hello,I am Service,i have recive you message ");
                    replyMsg.setData(bundle);
                    try {
                        client.send(replyMsg);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                    break;
            }
            super.handleMessage(msg);
        }
    }

    public MessengerService() {
    }

    private final Messenger messenger = new Messenger(new MessengerHandle());
    @Override
    public IBinder onBind(Intent intent) {
        return messenger.getBinder();
    }
}
