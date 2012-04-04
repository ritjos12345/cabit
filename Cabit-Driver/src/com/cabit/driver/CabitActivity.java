/*
 * Copyright 2010 Google Inc.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.cabit.driver;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.R.bool;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.View.OnKeyListener;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;
import android.os.Handler;

//import com.cabit.Util;
import com.cabit.driver.R;
import com.cabit.client.MyRequestFactory;
import com.cabit.shared.CabitRequest;
import com.cabit.shared.TaxiProxy;
import com.cabit.shared.TaxiStatusProxy;
import com.cabit.driver.ProgressThread;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapView;
import com.google.android.maps.MyLocationOverlay;
import com.google.android.maps.Overlay;
import com.google.web.bindery.requestfactory.shared.Receiver;
import com.google.web.bindery.requestfactory.shared.Request;
import com.google.web.bindery.requestfactory.shared.ServerFailure;



/**
 * Main activity 
 * a menu item to invoke the accounts activity.
 */
public class CabitActivity extends MapActivity  {
    /**
     * Tag for logging.
     */
    private static final String TAG = "CabitActivity";

    /**
     * The current context.
     */
    private Context mContext = this;
    
    private MapView mapView = null;
    
    private MyLocationOverlay myLocationOverlay;
    
    private int mHour;
    private int mMinute;
	final int ALERT_DIALOG_ID = 0;
	final int PROGRESS_HORIZONTAL_DIALOG_ID = 1;
	final int PROGRESS_SPINNER_DIALOG_ID = 2;
	final int TIME_PICK_DIALOG_ID = 3;
	final int DATE_PICK_DIALOG_ID = 4;
	ProgressDialog progressDialog;
	ProgressThread progressThread;
	Timer timer;
	Boolean wantTimer=true;
    
    
    /**
     * A {@link BroadcastReceiver} to receive the response from a register or
     * unregister request, and to update the UI.
     */
    private final BroadcastReceiver mUpdateUIReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String accountName = intent.getStringExtra(DeviceRegistrar.ACCOUNT_NAME_EXTRA);
            int status = intent.getIntExtra(DeviceRegistrar.STATUS_EXTRA,
                    DeviceRegistrar.ERROR_STATUS);
            String message = null;
            String connectionStatus = Util.DISCONNECTED;
            if (status == DeviceRegistrar.REGISTERED_STATUS) {
                message = getResources().getString(R.string.registration_succeeded);
                connectionStatus = Util.CONNECTED;
            } else if (status == DeviceRegistrar.UNREGISTERED_STATUS) {
                message = getResources().getString(R.string.unregistration_succeeded);
            } else {
                message = getResources().getString(R.string.registration_error);
            }

            // Set connection status
            SharedPreferences prefs = Util.getSharedPreferences(mContext);
            prefs.edit().putString(Util.CONNECTION_STATUS, connectionStatus).commit();

            // Display a notification
            Util.generateNotification(mContext, String.format(message, accountName));
        }
    };

	

    /**
     * Begins the activity.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
        
    	Log.i(TAG, "onCreate");
        registerReceiver(mUpdateUIReceiver, new IntentFilter(Util.UPDATE_UI_INTENT));
        
        timer = new Timer();
	     
	    int FPS = 12;
	    timer.scheduleAtFixedRate(new TimerTask() {
			@Override
			public void run() {
				if(wantTimer==true)
				{
					Log.i(TAG, "updateTaxy");
					UpdateTaxi();
				}
				
			}
		},0,1000*FPS);  
        
    }
    
    @Override
    public void onResume() {
        super.onResume();

        
        SharedPreferences prefs = Util.getSharedPreferences(mContext);
        String connectionStatus = prefs.getString(Util.CONNECTION_STATUS, Util.DISCONNECTED);
        if (Util.DISCONNECTED.equals(connectionStatus)) {
            startActivity(new Intent(this, AccountsActivity.class));
        }
         
        setScreenContent(R.layout.main); 
        
    }

    
    @Override
    protected void onPause() {
            super.onPause();
            // when our activity pauses, we want to remove listening for location updates
            myLocationOverlay.disableMyLocation();
    }
    

	/**
     * Shuts down the activity.
     */
    @Override
    public void onDestroy() {
        unregisterReceiver(mUpdateUIReceiver);
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        // Invoke the Register activity
        menu.getItem(0).setIntent(new Intent(this, AccountsActivity.class));
        return true;
    }

    // Manage UI Screens

    private void setMainContent() {
    	
	    	mapView = (MapView) findViewById(R.id.mapview);
		    mapView.setBuiltInZoomControls(true);
		    
		    mapView.setClickable(true);
	        mapView.setEnabled(true);
	        mapView.setSatellite(true);
	        
			 // create an overlay that shows our current location
	        
	        
	        myLocationOverlay = new FixedMyLocationOverlay(this, mapView);
	        
	        
	        // add this overlay to the MapView and refresh it
	        mapView.getOverlays().add(myLocationOverlay);
	        mapView.postInvalidate();
	        mapView.setOnKeyListener(new OnKeyListener() {
				
				@Override
				public boolean onKey(View v, int keyCode, KeyEvent event) {
					new AlertDialog.Builder(mContext)
                    .setTitle("New client request")
                    .setMessage("www.udi@gmail.com is waiting for you. \n Target: Herzel, SK S0A, Canada")
                    .show();
					return true;
				}
			});
	        // call convenience method that zooms map on our location
	        
    	
    	myLocationOverlay.enableCompass();
    	myLocationOverlay.enableMyLocation();
    	myLocationOverlay.runOnFirstFix(new Runnable() {
			
			@Override
			public void run() {
				GeoPoint myLocationGeoPoint = myLocationOverlay.getMyLocation();
	            if(myLocationGeoPoint != null) {
	                    mapView.getController().animateTo(myLocationGeoPoint);
	                    mapView.getController().setZoom(10);
	            }
	            /*else {
	                    Toast.makeText(this, "Cannot determine location", Toast.LENGTH_SHORT).show();
	            }*/
				
			}
		});
    	
    	/*Timer timer = new Timer();
	     
	    int FPS = 12;
	    timer.scheduleAtFixedRate(new TimerTask() {
			@Override
			public void run() {
				UpdateTaxi();
				
			}
		},0,1000*FPS);  */
    	
    	
    	
    }
    
    
private void UpdateTaxi() {
	wantTimer=false;
	System.out.println("StartUpdate!!!!!!!!!!!!!!!!!!!!!!!!!!");
	new AsyncTask<Void, Void, TaxiStatusProxy>(){
		private TaxiStatusProxy result;
		@Override
		protected TaxiStatusProxy doInBackground(Void... params) {
			// TODO Auto-generated method stub
			MyRequestFactory requestFactory = Util.getRequestFactory(mContext, MyRequestFactory.class);
            final CabitRequest request = requestFactory.cabitRequest();
            System.out.println("before requestttttttttttttttt");
            request.UpdateLocation(null).fire(new Receiver<TaxiStatusProxy>() {

				@Override
				public void onSuccess(TaxiStatusProxy arg0) {
					// TODO Auto-generated method stub
					result = arg0;
					System.out.println("sucsessssssssssssss");
				}
			});
			return result;
		}
		
		@Override
        protected void onPostExecute(TaxiStatusProxy result) {
			clientWantsYou().show();
		}
		
	}.execute();
	
        
		
	}

private void msgDisplay(String msg)
{
    Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
}

    /**
     * This method zooms to the user's location with a zoom level of 10.
     * @return 
     */
    private AlertDialog clientWantsYou() {
    	
    	

	    	    // Define the Handler that receives messages from the thread and update the progress
	    	    final Handler handler = new Handler() {
	    	        public void handleMessage(Message msg) {
	    	            int total = msg.getData().getInt("total");
	    	            progressDialog.setProgress(total);
	    	            if (total >= 100){
	    	                //dismissDialog(PROGRESS_HORIZONTAL_DIALOG_ID);
	    	            	progressDialog.dismiss();
	    	                progressThread.setState(ProgressThread.STATE_DONE);
	    	                msgDisplay("Progress Dialog is Done");
	    	            }
	    	        }
	    	    };

    	
    	
    	
    	
    	
    	
    	
    	
    	
    	final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("A client have choose you!"+'\n'+"Would you like to pick him?");
        builder.setTitle("New Cab Order!");
        builder.setIcon(R.drawable.taxi);
        builder.setCancelable(false);
        progressDialog= new ProgressDialog(this);
    	progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
    	progressDialog.setMessage("Updating the client...");
    	progressDialog.setCancelable(false);
    	progressDialog.setIndeterminate(false);
    	progressDialog.setButton(ProgressDialog.BUTTON_POSITIVE, "cancel",new DialogInterface.OnClickListener() {
	           public void onClick(DialogInterface dialog, int id) {
	        	   msgDisplay("Action is cancelled");
	        }
	       });
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
        	
               public void onClick(DialogInterface dialog, int id) {
               	   progressDialog.show();
            	   //showDialog(PROGRESS_HORIZONTAL_DIALOG_ID);
               	
                   progressThread = new ProgressThread(handler);
                    progressThread.start();
                   //msgDisplay("Client was update.");
               }
           });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
               public void onClick(DialogInterface dialog, int id) {
               	ProgressDialog.show(CabitActivity.this, "","Canceling the client...", false,true,new DialogInterface.OnCancelListener(){
                       public void onCancel(DialogInterface dialog){
                           msgDisplay("Action is cancelled");
                          }
                      });
                   msgDisplay("Client was canceled.");
               }
           });
        AlertDialog alert = builder.create();
        return(alert);
        
        
    }
    

    
	/**
     * Sets the screen content based on the screen id.
     */
    private void setScreenContent(int screenId) {
    	setContentView(R.layout.main);        
        switch (screenId) {
            case R.layout.main:
                setMainContent();
                break;
        }
    }

	@Override
	protected boolean isRouteDisplayed() {
		// TODO Auto-generated method stub
		return false;
	}

	
}
