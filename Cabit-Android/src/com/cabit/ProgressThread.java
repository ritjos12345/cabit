package com.cabit;

import com.cabit.client.MyRequestFactory;
import com.cabit.shared.CabitRequest;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class ProgressThread extends Thread {
    Handler mHandler;
    final static int STATE_DONE = 0;
    final static int STATE_RUNNING = 1;
    final static int WAIT_FOR_ID = 2;
    int mState;
    int total;
    Context myContext;

    ProgressThread(Handler h, Context context) {
        mHandler = h;
        myContext = context;
    }

    public void run() {
        mState = WAIT_FOR_ID;   
        total = 0;
        while(WAIT_FOR_ID == mState){
        	try {
                Thread.sleep(1500);
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
        while (STATE_RUNNING == mState) {
        	
        	/*MyRequestFactory requestFactory = 
		    		Util.getRequestFactory(myContext, MyRequestFactory.class);
            final CabitRequest request = requestFactory.cabitRequest();
            Log.i("orderCabMenu", 
            		"telling the server that client soon will order a cab");
        	*/
        	
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
    
    public int getCurrentState(){
    	return mState;
    }
    
    public void setState(int state) {
        mState = state;
    }
}
