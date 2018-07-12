package com.shouryaaggarwal.hp.shaketoswitch;

import android.os.Handler;

public class Utils {

    // Delay mechanism

    public interface DelayCallback{
        void afterDelay();
    }


    public static void delay(float secs, final DelayCallback delayCallback){
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                delayCallback.afterDelay();
            }
        }, (int)(secs * 1000)); // afterDelay will be executed after (secs*1000) milliseconds.
    }
}
