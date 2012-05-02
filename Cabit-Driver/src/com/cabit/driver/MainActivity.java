package com.cabit.driver;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.cabit.driver.R;
import com.cabit.driver.R.drawable;
import com.cabit.driver.R.id;
import com.cabit.driver.R.layout;
import com.cabit.driver.utils.DynamicOverlayAllTaxi;
import com.cabit.driver.utils.DynamicOverlayMyLocation;
import com.cabit.driver.utils.Util;
import com.cabit.client.MyRequestFactory;
import com.cabit.shared.CabitRequest;
import com.cabit.shared.GpsLocationProxy;
import com.cabit.shared.TaxiProxy;
import com.cabit.shared.TaxiStatusProxy;


import com.google.android.maps.MapActivity;
import com.google.android.maps.MapView;
import com.google.web.bindery.requestfactory.shared.Receiver;
import com.google.web.bindery.requestfactory.shared.ServerFailure;

public class MainActivity extends MapActivity {

	private static final String TAG = "MainActivity";
	private Context mContext = this;
	
	protected MapView mapView; 
	protected DynamicOverlayMyLocation myLocationOverlay;

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

	boolean isTimerRunning = true;

	public void onPause(){
		super.onPause();
		myLocationOverlay.stop();
		
	}
	
	public void onCreate(Bundle savedInstanceState) 
    {
		super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        mapView = (MapView) findViewById(R.id.mapview);
		mapView.setBuiltInZoomControls(true);
		mapView.setTraffic(true);
		
		myLocationOverlay =new DynamicOverlayMyLocation(this.getResources().getDrawable(R.drawable.dot), mapView, false);
		mapView.getOverlays().add(myLocationOverlay);
		myLocationOverlay.start();
		
		//registerReceiver(mUpdateUIReceiver, new IntentFilter(Util.UPDATE_UI_INTENT));
        
        timer = new Timer();
	     
	    int FPS = 12;
	    timer.scheduleAtFixedRate(new TimerTask() {
			@Override
			public void run() {
				if(isTimerRunning){
					Log.i(TAG, "updateTaxy");
					UpdateTaxi();
				}
			}
		},0,1000*FPS);
    }
	/**
     * Shuts down the activity.
     */
    @Override
    public void onDestroy() {
        //unregisterReceiver(mUpdateUIReceiver);
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
		
		
private void UpdateTaxi() {
	//System.out.println("StartUpdate!!!!!!!!!!!!!!!!!!!!!!!!!!");
	new AsyncTask<Void, Void, TaxiStatusProxy>(){
		private TaxiStatusProxy result;
		@Override
		protected TaxiStatusProxy doInBackground(Void... params) {
			// TODO Auto-generated method stub
			MyRequestFactory requestFactory = Util.getRequestFactory(mContext, MyRequestFactory.class);
            final CabitRequest request = requestFactory.cabitRequest();
            //System.out.println("before requestttttttttttttttt");
            
            // ask about the current gps locatoin
            Location loc = Util.GetMyLocation(mContext);
            
            GpsLocationProxy myGpsLoc = request.create(GpsLocationProxy.class);
            
            // get the current gps pos details
            if(loc != null){
            	myGpsLoc.setLatitude((long) loc.getLatitude());
                myGpsLoc.setLongitude((long) loc.getLongitude());
            }else{
            	// in case that couldn't retrieve the gps location, set fake pos
            	myGpsLoc.setLatitude((int) (32.117566*1e6));
                myGpsLoc.setLongitude((int) (34.801598*1e6));
                //Log.e(TAG, "couldn't retrieve the gps location");
            }
            
            request.UpdateLocation(myGpsLoc).fire(new Receiver<TaxiStatusProxy>() {

				@Override
				public void onSuccess(TaxiStatusProxy arg0) {
					// TODO Auto-generated method stub
					result = arg0;
					//System.out.println("sucsessssssssssssss");
				}
			});
			return result;
		}
		
		@Override
        protected void onPostExecute(TaxiStatusProxy result) {
			if(result!= null){
				if(result.getOrders() != null && result.getOrders().size() > 0){
					isTimerRunning = false;
					String from = result.getOrders().get(0).getFrom().getTitle();
					String to = result.getOrders().get(0).getTo().getTitle();
					int orderId = result.getOrders().get(0).getId();
					if(from == null) 
						from = "";
					if(to == null) 
						to = "";
					clientWantsYou(from, to, orderId).show();
				}
			}
		}
		
	}.execute();
	
        
		
	}
private void msgDisplay(String msg)
{
    Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
}

    /**
     * This method zooms to the user's location with a zoom level of 10.
     * @param to 
     * @param from 
     * @param orderId 
     * @return 
     */
    private AlertDialog clientWantsYou(String from, String to, final int orderId) {
    	
    	

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
        builder.setMessage("A client have choose you!\n"+
        					"From: "+ from +"\n"+
        					"To: "+ to +"\n"+
        					"A client have choose you!"+"\n"+
        					"Would you like to pick him?");
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
            	   updateOrder(orderId,true);
               	   
            	   //showDialog(PROGRESS_HORIZONTAL_DIALOG_ID);
               	
               	   //progressDialog.show();
                   //progressThread = new ProgressThread(handler);
                    //progressThread.start();
                    
                    isTimerRunning = true;
                   //msgDisplay("Client was update.");
               }
           });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
               public void onClick(DialogInterface dialog, int id) {
            	   updateOrder(orderId,false);
               	ProgressDialog.show(MainActivity.this, "","Canceling the client...", false,true,new DialogInterface.OnCancelListener(){
                       public void onCancel(DialogInterface dialog){
                           msgDisplay("Action is cancelled");
                          }
                      });
                   msgDisplay("Client was canceled.");
                   isTimerRunning = true;
               }
           });
        AlertDialog alert = builder.create();
        return(alert);
        
        
    }
    

    
    
    protected void updateOrder(final int orderId, final boolean status){
    	new AsyncTask<Void, Void, Boolean>() {
    		Boolean result = null;
            
			@Override
			protected Boolean doInBackground(Void... params) {

				    MyRequestFactory requestFactory = Util.getRequestFactory(getBaseContext(), MyRequestFactory.class);
	                final CabitRequest request = requestFactory.cabitRequest();

	                request.UpdateOrder(orderId,status).fire(new Receiver<Boolean>() {
                	@Override
					public void onSuccess(Boolean arg0) {
						result = arg0;
					}
					
					@Override
                    public void onFailure(ServerFailure error) {
                		result = null;
                    }
				});
				return result;
			}
	        @Override
            protected void onPostExecute(Boolean result) {
            	if(result!=null){
            		
            	}
            }
			
        }.execute();
    	
    	
    }
	@Override
	protected boolean isRouteDisplayed() {
		return false;
	}

}
