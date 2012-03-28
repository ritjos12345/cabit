package com.cabit;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.cabit.client.MyRequestFactory;
import com.cabit.shared.AddressProxy;
import com.cabit.shared.CabitRequest;
import com.cabit.shared.GpsLocationProxy;
import com.cabit.shared.TaxiProxy;
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

public class orderCabMenu extends Activity{
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
        
        // initalize the gps systems we would use afterwards
        final LocationManager lm = (LocationManager)this.getSystemService(Context.LOCATION_SERVICE);
        Criteria crit = new Criteria();
        crit.setAccuracy(Criteria.ACCURACY_FINE);
        final String provider = lm.getBestProvider(crit, true);
        
        buttonCommit.setOnClickListener(new View.OnClickListener() {	
        	public void onClick(View v) {
	
        		dests.clear();
                
				// if no search val was added
				if (true==editTextSearch.getText().toString().equals("")){
					Toast.makeText(orderCabMenu.this,"Enter a dest to search!",Toast.LENGTH_LONG).show();
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
			        
			    } catch (IOException e) {
					// TODO Auto-generated catch block
			    	System.out.println("a5 "+e.getMessage());
					e.printStackTrace();
				}
				
			    // update the vals at dests arraylist
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
		                Location loc = lm.getLastKnownLocation(provider);
		                
		                GpsLocationProxy myGpsLoc = request.create(GpsLocationProxy.class);
		                //myGpsLoc.setLatitude((int) (33.5*1e6));
		                //myGpsLoc.setLongitude((int) (34.5*1e6));
		                
		                // get the current gps pos details 
		                myGpsLoc.setLatitude((long) loc.getLatitude());
		                myGpsLoc.setLongitude((long) loc.getLongitude());
		                
		                request.IAmNeer(myGpsLoc).fire();
		                
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
				finish();
				dests.clear();
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
			System.out.println("baaa");
		}
		else{
			
			/*Intent resultData = new Intent();
			resultData.putExtra("address", dests.get(index));
	        resultData.putExtra("latitude", (int)(destAdresses.get(index).getLatitude()*1e6));
	        resultData.putExtra("longitude",(int)(destAdresses.get(index).getLongitude()*1e6));
			*/
			
	        Toast.makeText(orderCabMenu.this,"Connecting to the server to"+dests.get(index),
	        		Toast.LENGTH_LONG).show();
			System.out.println(dests.get(index));
			
			// updating the server about the cab to search
			new AsyncTask<Void, Void, Integer>() {
	            Integer result = new Integer(0);
	            
				@Override
				protected Integer doInBackground(Void... params) {
				    MyRequestFactory requestFactory = Util.getRequestFactory(getBaseContext(), MyRequestFactory.class);
	                final CabitRequest request = requestFactory.cabitRequest();
	                Log.i("orderCabMenu", "Sending request details to server");
	                
	                // create the dests proxy vals
	                AddressProxy mySrc = request.create(AddressProxy.class);
	                mySrc.setTitle(srcTitle);
	                AddressProxy myDst = request.create(AddressProxy.class);
	                myDst.setTitle(dests.get(index));
	                
	                Log.i("orderCabMenu", "Src "+mySrc.getTitle()+" dest "+myDst.getTitle());
	                
	                // fire the order
	                request.CreateOrder(mySrc,myDst).fire(new Receiver<Integer>() {
	                	@Override
						public void onSuccess(Integer arg0) {
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
	            protected void onPostExecute(Integer result) {
	            	if(result!=null){
		            	orderId = result;
		            	Log.i("orderCabMenu", "The order is "+result);
		            	// start updating the user about the order status
		            	progressThread.setState(1);
		            	Toast.makeText(orderCabMenu.this,"order id is "+result+" searching for a cab",
	        	        		Toast.LENGTH_LONG).show();
	            	}else{
	            		Toast.makeText(orderCabMenu.this,"recive null pointer from the server..",
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
	    	progressDialog.setMessage("searching for a fitting cab");
	    	progressDialog.setCancelable(false);
	    	progressDialog.setIndeterminate(false);
	    	progressDialog.setButton(ProgressDialog.BUTTON_POSITIVE, "cancel",new DialogInterface.OnClickListener() {
		           public void onClick(DialogInterface dialog, int id) {
		        	   Toast.makeText(orderCabMenu.this,"Action is cancelled",Toast.LENGTH_LONG).show();
		        	   // means to kill the progress thread
		        	   progressThread.setState(0);
		        }
		       });
			
	    	
			showDialog(PROGRESS_HORIZONTAL_DIALOG_ID);
			// start a progress thread
			progressThread = new ProgressThread(handler,getBaseContext());
            progressThread.start();
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
                //dismissDialog(PROGRESS_HORIZONTAL_DIALOG_ID);
                progressDialog.dismiss();
                progressThread.setState(ProgressThread.STATE_DONE);
                Toast.makeText(orderCabMenu.this,"Fitting cab was found!",Toast.LENGTH_LONG).show();
                
                // here we return to the map
                //Intent intent = new Intent(OrderACabActivity.this, orderMenu.class);
				//startActivityForResult(intent,1 );
            }
        }
    };
	
	
	
	
    
}
