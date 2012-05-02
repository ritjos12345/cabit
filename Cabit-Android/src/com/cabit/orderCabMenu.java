package com.cabit;


import java.util.ArrayList;
import java.util.List;
import com.cabit.R;
import com.cabit.client.MyRequestFactory;
import com.cabit.shared.GpsAddressProxy;
import com.cabit.shared.CabitRequest;
import com.cabit.shared.GpsLocationProxy;
import com.cabit.utils.Util;
import com.google.web.bindery.requestfactory.shared.Receiver;
import com.google.web.bindery.requestfactory.shared.ServerFailure;
import android.os.AsyncTask;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
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
	
	private final double defLa = 32.068302;
	private final double defLo = 34.845243;
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
	
	
	GpsLocationProxy mySrcLoc;
	GpsLocationProxy myDstLoc;
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
							str.append( arr.get(i).getAddressLine(j) + ", ");
							j++;
						}
						
						dests.add(str.toString());
					}	
			        
			    } catch (Exception e) {
			    	Log.e(TAG, "couldn't get to the google server");
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
		                Log.i(TAG, 
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
		                	myGpsLoc.setLatitude((long) (defLa*1e6));
			                myGpsLoc.setLongitude((long) (defLo*1e6)); 
			                //Log.e(TAG, "couldn't retrieve the gps location");
		                }
		                
		                Log.i(TAG, 
		                		myGpsLoc.getLatitude()+" "+myGpsLoc.getLongitude());
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
		
		if (index==dests.size()){
			// means, choosing the cancel opt
			Log.i(TAG, "ordering a cab was canceled");
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
                    mySrcLoc = request.create(GpsLocationProxy.class);
                    
                    if(loc != null){
                    	mySrcLoc.setLatitude((long) loc.getLatitude());
                    	mySrcLoc.setLongitude((long) loc.getLongitude());
                    }else{
                    	mySrcLoc.setLatitude((long) (defLa*1e6));
                    	mySrcLoc.setLongitude((long) (defLo*1e6)); 
                    	
                    }
                    mySrc.setLocation(mySrcLoc);
                    
	                // create dst address
	                GpsAddressProxy myDst = request.create(GpsAddressProxy.class);
	                myDst.setTitle(dests.get(index));
	                myDstLoc = request.create(GpsLocationProxy.class);
	                if(destAdresses == null || destAdresses.size() == 0){
	                	myDstLoc.setLatitude((long) (defLa*1e6));
	                	myDstLoc.setLongitude((long) (defLo*1e6));
	                }else{
	                	myDstLoc.setLatitude( (long) (destAdresses.get(index).getLatitude()*1e6));
	                	myDstLoc.setLongitude( (long) (destAdresses.get(index).getLongitude()*1e6));
	                }
                    mySrc.setLocation(myDstLoc);
	                
                    Log.i(TAG, "Sending request details to server: CreateOrder");
	                Log.i(TAG, "Src "+mySrc.getTitle()+" dest "+myDst.getTitle());
	                
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
		            	Log.i(TAG, "The order is "+result);
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
            	
                progressDialog.dismiss();
                progressThread.setState(ProgressThread.STATE_DONE);
                Toast.makeText(OrderCabMenu.this,"Fitting cab was found! Driver "+TAXI_ID ,Toast.LENGTH_LONG).show();
                
                // here we return to the map
                Intent intent = new Intent(OrderCabMenu.this, TrackTaxiActivity.class);
                Bundle b = new Bundle();
                
                /*Toast.makeText(OrderCabMenu.this,"" + mySrcLoc.getLatitude() / 1e6 +","
                		 + mySrcLoc.getLongitude() / 1e6 +","
                		 + myDstLoc.getLatitude() / 1e6 +","
                		 + myDstLoc.getLongitude() / 1e6 +","
                		,Toast.LENGTH_LONG).show();*/
                
                
                b.putString("taxi", TAXI_ID);
                
                b.putDouble("fromLat", mySrcLoc.getLatitude() / 1e6);
                b.putDouble("fromLon", mySrcLoc.getLongitude() / 1e6);
                b.putDouble("toLat", myDstLoc.getLatitude() / 1e6);
                b.putDouble("toLon", myDstLoc.getLongitude()/ 1e6);
                
                
    			
                intent.putExtras(b);

                startActivityForResult(intent, 1);
                
                
                // finish() ?
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