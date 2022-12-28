package com.ak17apps.ecstasyofgold.utils;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.widget.Toast;

public class MyService extends Service {
    private final IBinder binder = new LocalBinder();
    private ServiceCallbacks serviceCallbacks;
    private Handler handler;
    private Runnable runnable;

    public class LocalBinder extends Binder {
        public MyService getService() {
            return MyService.this;
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        resume();
    }

    public void stopRunnable(){
        serviceCallbacks = null;
        runnable = null;
        handler = null;
    }

    public void resume(){
        handler = new Handler();
        runnable = new Runnable() {
            @Override
            public void run() {
                if(serviceCallbacks != null) {
                    serviceCallbacks.doSomething();
                }
                if(handler != null) {
                    handler.postDelayed(this, 2000);
                }
            }
        };
        handler.postDelayed(runnable, 2000);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    public void setCallbacks(ServiceCallbacks callbacks) {
        serviceCallbacks = callbacks;
    }
}
