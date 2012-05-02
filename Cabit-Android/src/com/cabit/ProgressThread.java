package com.cabit;

import com.cabit.client.MyRequestFactory;
import com.cabit.shared.CabitRequest;
import com.cabit.utils.Util;
import com.google.web.bindery.requestfactory.shared.Receiver;
import com.google.web.bindery.requestfactory.shared.ServerFailure;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;


// talking with server thread
public class ProgressThread extends Thread {
    Handler mHandler;
    final static int STATE_DONE = 0;
    final static int STATE_RUNNING = 1;
    final static int WAIT_FOR_ID = 2;
    int mState;
    int total;
    int orderId;
    int statusSum;
    Context myContext;
    String driverId;

    ProgressThread(Handler h, Context context) {
        mHandler = h;
        myContext = context;
        // initialize the order num
        orderId = -1;
        statusSum = 0;
        driverId = "";
    }

    public void run() {
        mState = WAIT_FOR_ID;   
        total = 0;
        while(WAIT_FOR_ID == mState){
        	try {
                Thread.sleep(2500);
            } catch (InterruptedException e) {
                Log.e("ERROR", "ProgressThread Interrupted");
            }
        	Message msg = mHandler.obtainMessage();
            Bundle b = new Bundle();
            b.putInt("total", total);
            msg.setData(b);
            mHandler.sendMessage(msg);
        	total++;
        }
        while (STATE_RUNNING == mState) {
        	
        	MyRequestFactory requestFactory = 
		    		Util.getRequestFactory(myContext, MyRequestFactory.class);
            final CabitRequest request = requestFactory.cabitRequest();
            Log.i("orderCabMenu", 
            		"asking about the current order status");
            // send the request
        	request.GetOrderStatus(orderId).fire(new Receiver<String>() {	
        		@Override
				public void onSuccess(String arg0) {
        			Log.i("orderCabMenu", "got request status "+arg0);
        			//statusSum += 1;
        			driverId = arg0;
        		}
        		@Override
                public void onFailure(ServerFailure error) {
        			Log.e("ERROR", "ProgressThread failed to connect the server");
                }
        	});
        	
        	//if (3>statusSum){
        	// means no answer yet from the server
        	if (driverId == null || driverId.equals("")){
        		try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    Log.e("ERROR", "ProgressThread Interrupted");
                }
                Message msg = mHandler.obtainMessage();
                Bundle b = new Bundle();
                b.putInt("total", total);
                msg.setData(b);
                mHandler.sendMessage(msg);
                total++;
			}
        	// finished getting the order info
        	else{
        		Message msg = mHandler.obtainMessage();
	            Bundle b = new Bundle();
	            b.putInt("total", 100);
	            msg.setData(b);
	            mHandler.sendMessage(msg);
	            break;
        	}
        }
    }
    
    public int getCurrentState(){
    	return mState;
    }
    
    public void setOrderId(int id){
    	orderId = id;
    }
    
    public void setState(int state) {
        mState = state;
    }
    
    public String getDriverId(){
    	return driverId;
    }
}
