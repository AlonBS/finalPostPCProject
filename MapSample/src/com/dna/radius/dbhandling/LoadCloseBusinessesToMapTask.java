package com.dna.radius.dbhandling;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.dna.radius.datastructures.MapBusinessManager;
import com.dna.radius.datastructures.ExternalBusiness;
import com.dna.radius.datastructures.ExternalBusiness.BuisnessType;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class LoadCloseBusinessesToMapTask extends AsyncTask<Void, ArrayList<ExternalBusiness>, Void> {
	private Context mContext;
    private boolean stopFlag = false;
    private final WeakReference<GoogleMap> gMapRef;
    int NUM_OF_LOADS_BEFORE_REFRESH = 5;
    private MapBusinessManager businessManager;
	private double maxLatitude;
	private double minLatitude;
	private double maxLongitude;
	private double minLongitude;
    
    
    //TODO - the markersDB object and the  loadDBs_debug are only for debug, delete them!
    private static List<ExternalBusiness> markersDB; //TODO: delete
    public void setDBs_debug()
    {
    	int id = 0;
    	markersDB = new ArrayList<ExternalBusiness>();
    	Random r = new Random();
    	
    	markersDB.add(new ExternalBusiness("MCdonalds", BuisnessType.RESTAURANT, new LatLng(31.781099, 35.217668),Integer.toString(id++),new Random().nextInt(99999),new Random().nextInt(99999), Integer.toString(new Random().nextInt(99999))));
    	markersDB.add(new ExternalBusiness("Ivo", BuisnessType.RESTAURANT, new LatLng(31.779949, 35.218948),Integer.toString(id++),new Random().nextInt(99999),new Random().nextInt(99999), Integer.toString(new Random().nextInt(99999))));
    	markersDB.add(new ExternalBusiness("Dolfin Yam", BuisnessType.RESTAURANT, new LatLng(31.779968, 35.221209),Integer.toString(id++),new Random().nextInt(99999),new Random().nextInt(99999), Integer.toString(new Random().nextInt(99999))));
    	markersDB.add(new ExternalBusiness("Birman", BuisnessType.PUB, new LatLng(31.781855, 35.218086),Integer.toString(id++),new Random().nextInt(99999),new Random().nextInt(99999), Integer.toString(new Random().nextInt(99999))));
    	markersDB.add(new ExternalBusiness("Bullinat", BuisnessType.PUB, new LatLng(31.781984, 35.218221),Integer.toString(id++),new Random().nextInt(99999),new Random().nextInt(99999), Integer.toString(new Random().nextInt(99999))));
    	markersDB.add(new ExternalBusiness("Hamarush", BuisnessType.RESTAURANT, new LatLng(31.781823, 35.219065),Integer.toString(id++),new Random().nextInt(99999),new Random().nextInt(99999), Integer.toString(new Random().nextInt(99999))));
    	markersDB.add(new ExternalBusiness("Adom", BuisnessType.RESTAURANT, new LatLng(31.781334, 35.220703),Integer.toString(id++),new Random().nextInt(99999),new Random().nextInt(99999), Integer.toString(new Random().nextInt(99999))));
    	markersDB.add(new ExternalBusiness("Tel Aviv Bar", BuisnessType.PUB, new LatLng(31.781455, 35.220525),Integer.toString(id++),new Random().nextInt(99999),new Random().nextInt(99999), Integer.toString(new Random().nextInt(99999))));
    	markersDB.add(new ExternalBusiness("Jabutinski Bar", BuisnessType.PUB, new LatLng(31.779654, 35.221654),Integer.toString(id++),new Random().nextInt(99999),new Random().nextInt(99999), Integer.toString(new Random().nextInt(99999))));
    	markersDB.add(new ExternalBusiness("Reva Sheva", BuisnessType.GROCERIES, new LatLng(31.779793, 35.219728),Integer.toString(id++),new Random().nextInt(99999),new Random().nextInt(99999), Integer.toString(new Random().nextInt(99999))));
    	markersDB.add(new ExternalBusiness("The one with the shirts", BuisnessType.GROCERIES, new LatLng(31.779293, 35.221624),Integer.toString(id++),new Random().nextInt(99999),new Random().nextInt(99999), Integer.toString(new Random().nextInt(99999))));
    	markersDB.add(new ExternalBusiness("Hamashbir Latsarchan", BuisnessType.GROCERIES, new LatLng(31.781824, 35.219959),Integer.toString(id++),new Random().nextInt(99999),new Random().nextInt(99999), Integer.toString(new Random().nextInt(99999))));
    	markersDB.add(new ExternalBusiness("Hataklit", BuisnessType.PUB, new LatLng(31.781905, 35.221372),Integer.toString(id++),new Random().nextInt(99999),new Random().nextInt(99999), Integer.toString(new Random().nextInt(99999))));
    	markersDB.add(new ExternalBusiness("Hatav Hashmini", BuisnessType.GROCERIES, new LatLng(31.781191, 35.219621),Integer.toString(id++),new Random().nextInt(99999),new Random().nextInt(99999), Integer.toString(new Random().nextInt(99999))));
    	markersDB.add(new ExternalBusiness("Katsefet", BuisnessType.RESTAURANT, new LatLng(31.779921, 35.187777),Integer.toString(id++),new Random().nextInt(99999),new Random().nextInt(99999), Integer.toString(new Random().nextInt(99999))));
    }

    public LoadCloseBusinessesToMapTask(Context context,GoogleMap gMap,MapBusinessManager businessManager,double radius) {
        super();
        mContext = context;
        this.gMapRef = new WeakReference<GoogleMap>(gMap);
        this.businessManager = businessManager;
        setDBs_debug();
        
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
    	for(ExternalBusiness m:markersDB){
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
        
    }

    
}

