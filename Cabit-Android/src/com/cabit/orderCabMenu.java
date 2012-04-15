package com.cabit;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import com.cabit.R;
import com.cabit.R.id;
import com.cabit.R.layout;
import com.cabit.client.MyRequestFactory;
import com.cabit.shared.GpsAddressProxy;
import com.cabit.shared.CabitRequest;
import com.cabit.shared.GpsLocationProxy;
import com.cabit.shared.TaxiProxy;
import com.cabit.utils.Util;
import com.google.web.bindery.requestfactory.shared.Receiver;
import com.google.web.bindery.requestfactory.shared.ServerFailure;
import android.os.AsyncTask;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class OrderCabMenu extends Activity{
	
	private static final String TAG = "orderCabMenu";
	private Context mContext = this;
	
	EditText editTextSearch;
	Button buttonCommit,buttonCancel; 
	ArrayList<String> dests;
	ProgressDialog progressDialog;
	ProgressThread progressThread;
	List<Address> destAdresses;
	final int PROGRESS_HORIZONTAL_DIALOG_ID = 1;
	int orderId;
	 
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ordermenu);
        
        buttonCommit = (Button)findViewById(R.id.button_commit);
        buttonCancel = (Button)findViewById(R.id.button_cancel);
        editTextSearch = (EditText) findViewById(R.id.edit_text_search);
        dests = new ArrayList<String>();
        
        buttonCommit.setOnClickListener(new View.OnClickListener() {	
        	public void onClick(View v) {
	
        		dests.clear();
                
				// if no search val was added
				if (true==editTextSearch.getText().toString().equals("")){
					Toast.makeText(OrderCabMenu.this,"Enter a dest to search!",Toast.LENGTH_LONG).show();
					return;
				}
				
				// search for the correct title of the dst using editTextSearch.getText().toString() as the search val
				Geocoder g = new Geocoder(getBaseContext());
			    String locationName = editTextSearch.getText().toString(); 
			    try {
			    	int maxAddress = 5;
			    	// get a list of the corresponding titles
					List<Address> arr =g.getFromLocationName(locationName , maxAddress);
					destAdresses = arr;
					for (int i=0; i<arr.size(); i++){
						StringBuffer str= new StringBuffer("");
						int j = 0;
						while(arr.get(i).getAddressLine(j) != null){
							str.append( arr.get(i).getAddressLine(j) + " , ");
							j++;
						}
						
						dests.add(str.toString());
					}	
			        
			    } catch (Exception e) {
			    	Log.e("orderCabMenu", "couldn't get to the google server");
				}
				
			    // add the search dest to the dests list
				dests.add(new String(editTextSearch.getText().toString()));
				
				// connect the server and tell it searching a cab soon
				new AsyncTask<Void, Void, Void>() {

					@Override
					protected Void doInBackground(Void... params) {
					    MyRequestFactory requestFactory = 
					    		Util.getRequestFactory(getBaseContext(), MyRequestFactory.class);
		                final CabitRequest request = requestFactory.cabitRequest();
		                Log.i("orderCabMenu", 
		                		"telling the server that client soon will order a cab");
		                
		                // ask about the current gps locatoin
		                Location loc = Util.GetMyLocation(mContext);
		                
		                GpsLocationProxy myGpsLoc = request.create(GpsLocationProxy.class);
		                
		                // get the current gps pos details 
		                try{
		                	myGpsLoc.setLatitude((long) loc.getLatitude());
			                myGpsLoc.setLongitude((long) loc.getLongitude());
		                }
		                catch(Exception e){
		                	// in case that couldn't retrieve the gps location, set fake pos
		                	myGpsLoc.setLatitude((int) (33.5*1e6));
			                myGpsLoc.setLongitude((int) (34.5*1e6));
			                Log.e("orderCabMenu", "couldn't retrieve the gps location");
		                }
		                
		                Log.i("orderCabMenu", 
		                		myGpsLoc.getLatitude() + " " + myGpsLoc.getLongitude());
		                // send the request
		                request.IAmNear(myGpsLoc).fire();
		                
		                return null;
					}
					
		       	}.execute();
	
				// open the search dialog
				registerForContextMenu(v);
				openContextMenu(v);
        	}
		});
        buttonCancel.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				dests.clear();
				finish();
			}
        });
    }
    
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        
    	for (int i=0; i<dests.size();i++){
    		menu.add(Menu.NONE, i, Menu.NONE, dests.get(i));
    	}
    	menu.add(Menu.NONE, dests.size(), Menu.NONE, "Cancel");
        super.onCreateContextMenu(menu, v, menuInfo);
        return;
    }
    
	public boolean onContextItemSelected(MenuItem item) {
		final int index = item.getItemId();
		final String srcTitle = "";
		
		/*super.onContextItemSelected(item);
        AdapterView.AdapterContextMenuInfo menuInfo;
        menuInfo =(AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
        int index = menuInfo.position;*/
		
		if (index==dests.size()){
			// means, choosing the cancel opt
			Log.i("orderCabMenu", "ordering a cab was canceled");
		}
		else{
			
	        Toast.makeText(OrderCabMenu.this,"searching for a cab to "+dests.get(index),
	        		Toast.LENGTH_LONG).show();
			
			// updating the server about the cab to search
			new AsyncTask<Void, Void, Integer>() {
	            Integer result = new Integer(0);
	            
				@Override
				protected Integer doInBackground(Void... params) {
					
					MyRequestFactory requestFactory = Util.getRequestFactory(getBaseContext(), MyRequestFactory.class);
	                final CabitRequest request = requestFactory.cabitRequest();
	                
	                // create src address
                    GpsAddressProxy mySrc = request.create(GpsAddressProxy.class);
                    Location loc = Util.GetMyLocation(mContext);
                    mySrc.setTitle(Util.GetAddressFromLocation(mContext, loc) );
                    GpsLocationProxy mySrcLoc = request.create(GpsLocationProxy.class);
                    mySrcLoc.setLatitude((long) loc.getLatitude());
                    mySrcLoc.setLongitude((long) loc.getLongitude());
                    mySrc.setLocation(mySrcLoc);
                    
	                 
                    
	                // create dst address
	                GpsAddressProxy myDst = request.create(GpsAddressProxy.class);
	                myDst.setTitle(dests.get(index));
	                GpsLocationProxy myDstLoc = request.create(GpsLocationProxy.class);
	                myDstLoc.setLatitude((long) destAdresses.get(index).getLatitude());
	                myDstLoc.setLongitude((long) destAdresses.get(index).getLongitude());
                    mySrc.setLocation(myDstLoc);
	                
                    Log.i("orderCabMenu", "Sending request details to server: CreateOrder");
	                Log.i("orderCabMenu", "Src "+mySrc.getTitle()+" dest "+myDst.getTitle());
	                
	                // fire the order
	                request.CreateOrder(mySrc,myDst).fire(new Receiver<Integer>() {
	                	@Override
						public void onSuccess(Integer arg0) {
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
	            protected void onPostExecute(Integer result) {
	            	if(result!=null){
		            	orderId = result;
		            	Log.i("orderCabMenu", "The order is "+result);
		            	// update the progress thread to start asking about the order state 
		            	progressThread.setState(1);
		            	progressThread.setOrderId(orderId);
		            	progressDialog.setMessage("searching for a fitting cab");
		            	Toast.makeText(OrderCabMenu.this,"order id is "+result+" searching for a cab",
	        	        		Toast.LENGTH_LONG).show();
	            	}else{
	            		Toast.makeText(OrderCabMenu.this,"recive null pointer from the server..",
	        	        		Toast.LENGTH_LONG).show();
	            	}
	            }
				
	        }.execute();
			
			/*ProgressDialog.show(this,"מחפש נהגי מונית","טוען...נא להמתין", false,true,new DialogInterface.OnCancelListener(){
                public void onCancel(DialogInterface dialog){
                	Toast.makeText(orderMenu.this,"Action is cancelled",Toast.LENGTH_LONG).show();
                   }
               });*/
			
			progressDialog= new ProgressDialog(this);
	    	progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
	    	//progressDialog.setMessage("searching for a fitting cab");
	    	progressDialog.setMessage("posting the order");
	    	progressDialog.setCancelable(false);
	    	progressDialog.setIndeterminate(false);
	    	progressDialog.setButton(ProgressDialog.BUTTON_POSITIVE, "cancel",new DialogInterface.OnClickListener() {
		           public void onClick(DialogInterface dialog, int id) {
		        	   Toast.makeText(OrderCabMenu.this,"Action is cancelled",Toast.LENGTH_LONG).show();
		        	   // means to kill the progress thread
		        	   progressThread.setState(0);
		        }
		       });
	    	
	    	// start a progress thread
			progressThread = new ProgressThread(handler,getBaseContext());
            progressThread.start();
			showDialog(PROGRESS_HORIZONTAL_DIALOG_ID);
		}
		return true;
	}
	
	/*public Dialog onCreateDialog(int id, Bundle args){
		
		ProgressDialog progressDialog= new ProgressDialog(this);
		progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setMessage("טוען..נא להמתין");
        progressDialog.setCancelable(false);
        progressDialog.setIndeterminate(false);
        progressDialog.setButton(ProgressDialog.BUTTON_POSITIVE, "cancel",new DialogInterface.OnClickListener() {
               public void onClick(DialogInterface dialog, int id) {
            	   Toast.makeText(orderMenu.this,"Action is cancelled",Toast.LENGTH_LONG).show();
            	   progressThread.setState(0);
               }
           }); 
        
		return progressDialog;
	}*/
	
	// Define the Handler that receives messages from the thread and update the progress
    final Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            int total = msg.getData().getInt("total");
            progressDialog.setProgress(total);
            // means that the progress tread was canceled
            if (0==progressThread.getCurrentState()){
            	return;
            }
            progressDialog.show();
           
            // if finished talking with the server 
            if (total >= 100){
            	// get the driver ID
            	String TAXI_ID = progressThread.getDriverId();
            	
            //if (total >= 1000){
                //dismissDialog(PROGRESS_HORIZONTAL_DIALOG_ID);
                progressDialog.dismiss();
                progressThread.setState(ProgressThread.STATE_DONE);
                Toast.makeText(OrderCabMenu.this,"Fitting cab was found!",Toast.LENGTH_LONG).show();
                
/*
                // here we return to the map
                Intent intent = new Intent(orderCabMenu.this, TrackTaxiActivity.class);
                Bundle b = new Bundle();
                b.putString("taxi", TAXI_ID);
                intent.putExtras(b);

                startActivityForResult(intent, 1);
                
                
                // finish() ?
*/
            }
        }
    };
	
	
	
	
    
}







/* how to call TrackTaxi:
 * 
 
Intent intent = new Intent(FirstActivity.this, TrackTaxiActivity.class);
Bundle b = new Bundle();
b.putInt("taxi", TAXI_ID);
intent.putExtras(b);

*/