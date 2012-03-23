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

import java.util.Calendar;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.Button;

import com.cabit.client.MyRequestFactory;
import com.cabit.shared.CabitRequest;
import com.cabit.shared.GpsLocationProxy;
import com.cabit.shared.TaxiProxy;
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
    
    private Button buttonOrderEntry;
    
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
        
        setContentView(R.layout.main);
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

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {

              final int  latitude = data.getExtras().getInt("latitude");
              final int  longitude = data.getExtras().getInt("longitude");
              final String address = data.getExtras().getString("address");
              

              LocationManager lm = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
              final Location loc = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
              final int myLatitude = 33;
              final int myLongitude = 33;
              //TODO fix this
              /*if(loc != null){
            	  myLatitude = (int) (loc.getLatitude()*1e6);
            	  myLongitude= (int) (loc.getLongitude()*1e6);
              }*/
              new AlertDialog.Builder(this)
	            .setMessage("Do you want to order a cab to \n"+address+" ?")
	            .setTitle("Order a cab")
	            .setCancelable(false)
	            .setPositiveButton("yes", new DialogInterface.OnClickListener() {
	                   public void onClick(DialogInterface dialog, int id) {
	                	   

	                       
	                	   new AsyncTask<Void, Void, String>() {
	                           private String result;
	                           
	               			@Override
	               			protected String doInBackground(Void... params) {
	               			    MyRequestFactory requestFactory = Util.getRequestFactory(mContext, MyRequestFactory.class);
	                               final CabitRequest request = requestFactory.cabitRequest();
	                               Log.i(TAG, "Sending request to server");
	                               
	                               /*LocationProxy from = request.create(LocationProxy.class);
	                               LocationProxy to = request.create(LocationProxy.class);
	                               
	                               to.setLatitude(latitude);
	                               to.setLongitude(longitude);
	                               to.setTitle(address);
	                               
	                               from.setLatitude(myLatitude);
	                               from.setLongitude(myLongitude);
	                               
	                               request.orderCab(from, to).fire(new Receiver<String>(){
	                               	
	               					@Override
	               					public void onSuccess(String arg0) {
	               						System.out.println("1");
	               						result = arg0;
	               					}
	               					
	               					@Override
	                                   public void onFailure(ServerFailure error) {
	               						System.out.println("2:"+error.getClass().getName() +" , "+error.getExceptionType()+", " + error.getMessage() + " , "+error.getStackTraceString());
	                               		result = null;
	                                   }
	                              
	                               });
	                               */    
	                               return result;
	               			}
	               			
	               			@Override
	                           protected void onPostExecute(String result) {
	               				System.out.println("3");
	                           	if(result!=null){
	                           		new AlertDialog.Builder(mContext)
		                          	  .setTitle("Order was created")
		                          	  .setMessage("Waiting for :"+ result)
		                          	  .show();  
	                           	}else{
	                           		new AlertDialog.Builder(mContext)
		                          	  .setTitle("Order Error")
		                          	  .setMessage("find cab to order.")
		                          	  .show();  
	                           	}
	                               
	                           }
	               			

	                       }.execute();
	                   }
	               })
	               
	            .setNegativeButton("No", new DialogInterface.OnClickListener() {
	            		public void onClick(DialogInterface dialog, int id) {
	            			
	            		}
	               })
	             .show();           
              
            }
        }
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
	    
	    // TODO udi1
	    taxiOverlay = new DynamicOverlay<String>(this.getResources().getDrawable(R.drawable.taxi),mapView.getContext());
	    mapView.getOverlays().add(taxiOverlay);
	    
	   
	    
	    
	    
	    buttonOrderEntry = (Button)findViewById(R.id.button_commit);
        buttonOrderEntry.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				Intent intent = new Intent(CabitActivity.this, orderMenu.class); 
				startActivityForResult(intent,1 );	
			}
		});
        
        
        Timer timer = new Timer();
	     
	    int FPS = 12;
	    timer.scheduleAtFixedRate(new TimerTask() {
			@Override
			public void run() {
				UpdateTaxi();
				
			}
		},0,1000*FPS);  
    }

    private void UpdateTaxi() {
    	
        // Use an AsyncTask to avoid blocking the UI thread
        new AsyncTask<Void, Void, List<TaxiProxy>>() {
            private List<TaxiProxy> result;
            
            
			@Override
			protected List<TaxiProxy> doInBackground(Void... params) {
			    MyRequestFactory requestFactory = Util.getRequestFactory(mContext, MyRequestFactory.class);
                final CabitRequest request = requestFactory.cabitRequest();
                Log.i(TAG, "Sending request to server");
                request.GetAllTaxi().fire(	new Receiver<List<TaxiProxy>>(){
                	
					@Override
					public void onSuccess(List<TaxiProxy> arg0) {
						result = arg0;
					}
					
					@Override
                    public void onFailure(ServerFailure error) {
						System.out.println("2:"+error.getClass().getName() +" , "+error.getExceptionType()+", " + error.getMessage() + " , "+error.getStackTraceString());
                		result = null;
                    }
                });
                return result;
			}
			
			@Override
            protected void onPostExecute(List<TaxiProxy> result) {
				
				
            	if(result!=null){
	            	for (TaxiProxy taxiProxy : result) {
	            		taxiOverlay.UpdateItem(taxiProxy.getDriver(), (int )taxiProxy.getGpsLocation().getLatitude(), (int) taxiProxy.getGpsLocation().getLatitude(), taxiProxy.getDriver(),"coool");
					}
	            	taxiOverlay.RefreshItems();
            	}else{
            		AlertDialog.Builder dialog = new AlertDialog.Builder(mContext);
            		
            		dialog.setTitle("Error");
            		dialog.setMessage("recive null pointer from the server..");
            		dialog.show();
            		System.out.println("9");
            	}
                
            }
			

        }.execute();

        
		
	}

	/**
     * Sets the screen content based on the screen id.
     */
    private void setScreenContent(int screenId) {
        switch (screenId) {
            case R.layout.main:
                setHelloWorldScreenContent();
                break;
        }
    }

	@Override
	protected boolean isRouteDisplayed() {
		return false;
	}

	
}
