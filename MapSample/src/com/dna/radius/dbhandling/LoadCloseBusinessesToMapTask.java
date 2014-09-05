package com.dna.radius.dbhandling;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

import android.os.AsyncTask;
import android.util.Log;

import com.dna.radius.datastructures.ExternalBusiness;
import com.dna.radius.datastructures.MapBusinessManager;
import com.dna.radius.mapsample.MapWindowFragment;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class LoadCloseBusinessesToMapTask extends AsyncTask<Void, ArrayList<ExternalBusiness>, Void> {
	private MapWindowFragment calledFragment;
    private boolean stopFlag = false;
    private final WeakReference<GoogleMap> gMapRef;
    int NUM_OF_LOADS_BEFORE_REFRESH = 5;
    private MapBusinessManager businessManager;
	private double maxLatitude;
	private double minLatitude;
	private double maxLongitude;
	private double minLongitude;
    
    
    
    

    public LoadCloseBusinessesToMapTask(MapWindowFragment calledFragment,GoogleMap gMap,MapBusinessManager businessManager,double radius) {
        super();
        this.calledFragment = calledFragment;
        this.gMapRef = new WeakReference<GoogleMap>(gMap);
        this.businessManager = businessManager;
        
        
        LatLng mapCenter = gMap.getCameraPosition().target;
      	maxLatitude = mapCenter.latitude + radius;
      	minLatitude = mapCenter.latitude - radius;
      	maxLongitude = mapCenter.longitude + radius;
      	minLongitude = mapCenter.longitude - radius;
      	
		/*//Latitude: 1 deg = 110.54 km. therefor, 1km = 1deg/110.54
		maxLatitude = mapCenter.latitude + radius/110.54;
		minLatitude = mapCenter.latitude - radius/110.54;
		//Longitude: 1 deg = 111.320*cos(latitude) km. therefor, 1km = cos-1(1deg/111.320)
		maxLongitude = mapCenter.longitude + Math.acos(radius/111.320);
		minLongitude = mapCenter.longitude - Math.acos(radius/111.320);*/
    }
    
    
    public void stopTask(){
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
        ArrayList<ExternalBusiness> newBusinesses = new ArrayList<ExternalBusiness>();
        
        //TODO - load from parse only the businesses which their latlng coordinates are 
        //in the range: minLatidue:maxLatidue and minLongitude:maxLongitude 
    	for(ExternalBusiness m:DBHandler.markersDB){
    		if(stopFlag){
    			return null;
    		}
    		if(m.pos.latitude>minLatitude && m.pos.latitude<maxLatitude &&
    				m.pos.longitude>minLongitude && m.pos.longitude<maxLongitude){
	        	newBusinesses.add(m);
	        	counter++;
	        	if(counter%5==0){
	        		publishProgress(newBusinesses);
	        		newBusinesses = new ArrayList<ExternalBusiness>();
	        	}
    		}
        }
    	if(counter%5!=0){
    		publishProgress(newBusinesses);
    	}
    	return null;
    }
    
    @Override
    protected void onProgressUpdate(ArrayList<ExternalBusiness>... values) {
    	if(stopFlag){
    		Log.d("LoadCloseBusinessesToMap", "stop flag is on, stops loading businesses to map");
    		return;
    	}
    	for(ExternalBusiness bm : values[0]){
    		if (gMapRef != null && bm != null) {
	            final GoogleMap gMap = gMapRef.get();
	            if (gMap != null) {
	        		Marker m =  gMap.addMarker(new MarkerOptions().position(bm.pos).title(bm.name).icon(BitmapDescriptorFactory.fromResource(bm.iconID)));
	            	businessManager.addBusiness(bm, m);
	            }else{
	            	return;
	            }
    		}else{
    			return;
    		}
    	}
    	super.onProgressUpdate(values);
    	
    }
    
    /**
     * A method that's called once doInBackground() completes. 
     */
    @Override
    protected void onPostExecute(Void result) {
    	if (calledFragment != null && calledFragment.isVisible()) {
    		calledFragment.updateOverlays();
		}else{
			return;
		}
    
    }

    
}

