package com.cabit;




import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.AdapterView.OnItemSelectedListener;

public class orderCabMenu extends Activity{
	EditText editTextSearch;
	Button buttonCommit,buttonCancel; 
	ArrayList<String> dests;
	ProgressDialog progressDialog;
	ProgressThread progressThread;
	List<Address> destAdresses;
	final int PROGRESS_HORIZONTAL_DIALOG_ID = 1;
	
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
					Toast.makeText(orderCabMenu.this,"Enter a dest to search!",Toast.LENGTH_LONG).show();
					return;
				}
				
				// search for the correct title of the dest
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
				
				dests.add(new String(editTextSearch.getText().toString()));
				// connect to the server using editTextSearch.getText().toString() as the search val
				// update the vals at dests arraylist
				

				// this are fake values that we retrieve from google api 
        		//dests.add("vvv");
                //dests.add("gg");
				
				// open the search dialog
				registerForContextMenu(v);
				openContextMenu(v);
				
				// connect to the server using editTextSearch.getText().toString() as the search val
        	}
		});
        buttonCancel.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				finish();
				dests.clear();
				//dests.add("ss");
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
		int index = item.getItemId();
		
		/*super.onContextItemSelected(item);
        AdapterView.AdapterContextMenuInfo menuInfo;
        menuInfo =(AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
        int index = menuInfo.position;*/
		
		if (index==dests.size()){
			// means, choosing the cancel opt
			System.out.println("baaa");
		}
		else{
			
			Intent resultData = new Intent();
			resultData.putExtra("address", dests.get(index));
	        resultData.putExtra("latitude", (int)(destAdresses.get(0).getLatitude()*1e6));
	        resultData.putExtra("longitude",(int)(destAdresses.get(0).getLongitude()*1e6));
			
	        Toast.makeText(orderCabMenu.this,
	        		"Connecting to the server to"+dests.get(index),
	        		Toast.LENGTH_LONG).show();
			System.out.println(dests.get(index));
	        
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
			progressThread = new ProgressThread(handler);
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
