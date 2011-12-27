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
package com.cabit;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;

import com.cabit.client.MyRequestFactory;
import com.cabit.shared.CabitRequest;
import com.cabit.shared.LocationProxy;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.web.bindery.requestfactory.shared.Receiver;
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
    
    private MapView mapView;
    
    private DynamicOverlay<String> taxiOverlay;
    
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
        Log.i(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        
        
        // Register a receiver to provide register/unregister notifications
        registerReceiver(mUpdateUIReceiver, new IntentFilter(Util.UPDATE_UI_INTENT));
        
        
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

    private void setHelloWorldScreenContent() {
    
        mapView = (MapView) findViewById(R.id.mapview);
	    mapView.setBuiltInZoomControls(true);
	    
	    taxiOverlay = new DynamicOverlay<String>(this.getResources().getDrawable(R.drawable.taxi),mapView.getContext());
	    mapView.getOverlays().add(taxiOverlay);
	    
	    Timer timer = new Timer();
	    
	    //UpdateTaxi();
	    
	    class UpdateTaxiTask extends TimerTask {
    	   public void run() {
    		   UpdateTaxi();
    	   }
    	}
	    final int FPS = 10;
	    TimerTask taxiTask = new UpdateTaxiTask();
	    timer.scheduleAtFixedRate(taxiTask, 0, 1000*FPS);
	    
	    
        
    }

    private void UpdateTaxi() {

        // Use an AsyncTask to avoid blocking the UI thread
        new AsyncTask<Void, Void, List<LocationProxy>>() {
            private List<LocationProxy> result;


                        
            
			@Override
			protected List<LocationProxy> doInBackground(Void... params) {
			    MyRequestFactory requestFactory = Util.getRequestFactory(mContext, MyRequestFactory.class);
                final CabitRequest request = requestFactory.cabitRequest();
                Log.i(TAG, "Sending request to server");
                request.getAllCabs().fire(	new Receiver<List<LocationProxy>>(){
                	
					@Override
					public void onSuccess(List<LocationProxy> arg0) {
						System.out.println("1");
						result = arg0;
					}
					
					@Override
                    public void onFailure(ServerFailure error) {
						System.out.println("2");
                		result = null;
                    }
               
                });
                    
                return result;
			}
			
			@Override
            protected void onPostExecute(List<LocationProxy> result) {
				System.out.println("3");
            	if(result!=null){
            		System.out.println("4");
	            	for (LocationProxy locationProxy : result) {
	            		System.out.println("5");
	            		taxiOverlay.UpdateItem(locationProxy.getTitle(), locationProxy.getLatitude(), locationProxy.getLongitude(),locationProxy.getTitle(),"coool");
	            		System.out.println("6");
					}
            	}else{
            		System.out.println("7");
            		AlertDialog.Builder dialog = new AlertDialog.Builder(mContext);
            		
            		dialog.setTitle("Error");
            		dialog.setMessage("recive null pointer from the server..");
            		System.out.println("8");
            		dialog.show();
            		System.out.println("9");
            	}
                
            }
			

        }.execute();
        System.out.println("10");
        
		
	}

	/**
     * Sets the screen content based on the screen id.
     */
    private void setScreenContent(int screenId) {
        setContentView(screenId);
        switch (screenId) {
            case R.layout.main:
                setHelloWorldScreenContent();
                break;
        }
    }

	@Override
	protected boolean isRouteDisplayed() {
		// TODO Auto-generated method stub
		return false;
	}

	
}
