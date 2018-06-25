package com.arrow.intentservice;

import android.app.IntentService;
import android.content.Intent;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.util.Log;

public class LocalIntentService extends IntentService{
    private static final String TAG="LocalIntentService";

    public LocalIntentService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        String action = intent.getStringExtra("task_action");
        Log.i(TAG, "receive task:" +action);
        SystemClock.sleep(3000);
        if ("com.arrow.aciton.TASK1".equals(action)){
            Log.i(TAG, "handle task " + action);
        }
    }

    @Override
    public void onDestroy() {
        Log.i(TAG, "onDestroy: ");
        super.onDestroy();
    }
}
