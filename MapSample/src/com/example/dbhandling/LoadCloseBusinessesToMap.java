package com.example.dbhandling;

import java.util.ArrayList;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import com.example.datastructures.BusinessMarker;

public class LoadCloseBusinessesToMap extends AsyncTask<Void, ArrayList<BusinessMarker>, Void> {
    Context mContext;
    boolean stopFlag = false;
    int NUM_OF_LOADS_BEFORE_REFRESH = 5;
    public LoadCloseBusinessesToMap(Context context) {
        super();
        mContext = context;
    }
    
    public void close(){
    	stopFlag = true;
    }
    
    /**
     * Get a Geocoder instance, get the latitude and longitude
     * look up the address, and return it
     *
     * @params params One or more Location objects
     * @return A string containing the address of the current
     * location, or an empty string if no address can be found,
     * or an error message
     */
    protected Void doInBackground(Void... params) {
    	//Marker Jerusalem = gMap.addMarker(new MarkerOptions().position(JAFFA_STREET).title("Jerusalem").snippet("many dosim").icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_launcher)));
        int counter = 0;
        ArrayList<BusinessMarker> newBusinesses = new ArrayList<BusinessMarker>();
//    	for(BusinessMarker m:markersDB){
//        	//businessesList.add(m);
//        	newBusinesses.add(m);
//        	counter++;
//        	if(counter%5==0){
//        		publishProgress(newBusinesses);
//        		newBusinesses = new ArrayList<BusinessMarker>();
//        	}
//        }
    	if(counter%5!=0){
    		publishProgress(newBusinesses);
    	}
    	return null;
    }
    
    @Override
    protected void onProgressUpdate(ArrayList<BusinessMarker>... values) {
    	for(BusinessMarker bm : values[0]){
        	//putOverlayOnMap(bm);
    	}
    	super.onProgressUpdate(values);
    	
    }
    
    /**
     * A method that's called once doInBackground() completes. 
     */
    @Override
    protected void onPostExecute(Void result) {
        
    	Toast.makeText(mContext,"finished update", Toast.LENGTH_SHORT).show();
    }

    
}

