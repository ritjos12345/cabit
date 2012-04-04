package com.cabit.driver;


import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class ProgressThread extends Thread {
    Handler mHandler;
    final static int STATE_DONE = 0;
    final static int STATE_RUNNING = 1;
    int mState;
    int total;

    ProgressThread(Handler h) {
        mHandler = h;
    }

    public void run() {
        mState = STATE_RUNNING;   
        total = 0;
        while (mState == STATE_RUNNING) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                Log.e("ERROR", "Thread Interrupted");
            }
            Message msg = mHandler.obtainMessage();
            Bundle b = new Bundle();
            b.putInt("total", total);
            msg.setData(b);
            mHandler.sendMessage(msg);
            total++;
        }
    }
    public void setState(int state) {
        mState = state;
    }
}
