package com.example.dbhandling;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.example.datastructures.BusinessMarker;
import com.example.datastructures.BusinessesManager;
import com.example.datastructures.BusinessMarker.BuisnessType;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class LoadCloseBusinessesToMapTask extends AsyncTask<Void, ArrayList<BusinessMarker>, Void> {
	private Context mContext;
    private boolean stopFlag = false;
    private final WeakReference<GoogleMap> gMapRef;
    int NUM_OF_LOADS_BEFORE_REFRESH = 5;
    private BusinessesManager businessManager;
    
    
    //TODO - the markersDB object and the  loadDBs_debug are only for debug, delete them!
    private static List<BusinessMarker> markersDB; //TODO: delete
    public void setDBs_debug()
    {
    	long id = 0;
    	markersDB = new ArrayList<BusinessMarker>();
    	Random r = new Random();
    	markersDB.add(new BusinessMarker("MCdonalds", BuisnessType.RESTURANT, new LatLng(31.781099, 35.217668), "Jerusalem",id++,new Random().nextInt(99999),new Random().nextInt(99999)));
    	markersDB.add(new BusinessMarker("Ivo", BuisnessType.RESTURANT, new LatLng(31.779949, 35.218948), "Jerusalem",id++,new Random().nextInt(99999),new Random().nextInt(99999)));
    	markersDB.add(new BusinessMarker("Dolfin Yam", BuisnessType.RESTURANT, new LatLng(31.779968, 35.221209), "Jerusalem",id++,new Random().nextInt(99999),new Random().nextInt(99999)));
    	markersDB.add(new BusinessMarker("Birman", BuisnessType.PUB, new LatLng(31.781855, 35.218086), "Jerusalem",id++,new Random().nextInt(99999),new Random().nextInt(99999)));
    	markersDB.add(new BusinessMarker("Bullinat", BuisnessType.PUB, new LatLng(31.781984, 35.218221), "Jerusalem",id++,new Random().nextInt(99999),new Random().nextInt(99999)));
    	markersDB.add(new BusinessMarker("Hamarush", BuisnessType.RESTURANT, new LatLng(31.781823, 35.219065), "Jerusalem",id++,new Random().nextInt(99999),new Random().nextInt(99999)));
    	markersDB.add(new BusinessMarker("Adom", BuisnessType.RESTURANT, new LatLng(31.781334, 35.220703), "Jerusalem",id++,new Random().nextInt(99999),new Random().nextInt(99999)));
    	markersDB.add(new BusinessMarker("Tel Aviv Bar", BuisnessType.PUB, new LatLng(31.781455, 35.220525), "Jerusalem",id++,new Random().nextInt(99999),new Random().nextInt(99999)));
    	markersDB.add(new BusinessMarker("Jabutinski Bar", BuisnessType.PUB, new LatLng(31.779654, 35.221654), "Jerusalem",id++,new Random().nextInt(99999),new Random().nextInt(99999)));
    	markersDB.add(new BusinessMarker("Reva Sheva", BuisnessType.SHOPPING, new LatLng(31.779793, 35.219728), "Jerusalem",id++,new Random().nextInt(99999),new Random().nextInt(99999)));
    	markersDB.add(new BusinessMarker("The one with the shirts", BuisnessType.SHOPPING, new LatLng(31.779293, 35.221624), "Jerusalem",id++,new Random().nextInt(99999),new Random().nextInt(99999)));
    	markersDB.add(new BusinessMarker("Hamashbir Latsarchan", BuisnessType.SHOPPING, new LatLng(31.781824, 35.219959), "Jerusalem",id++,new Random().nextInt(99999),new Random().nextInt(99999)));
    	markersDB.add(new BusinessMarker("Hataklit", BuisnessType.PUB, new LatLng(31.781905, 35.221372), "Jerusalem",id++,new Random().nextInt(99999),new Random().nextInt(99999)));
    	markersDB.add(new BusinessMarker("Hatav Hashmini", BuisnessType.SHOPPING, new LatLng(31.781191, 35.219621), "Jerusalem",id++,new Random().nextInt(99999),new Random().nextInt(99999)));
    
    }

    public LoadCloseBusinessesToMapTask(Context context,GoogleMap gMap,BusinessesManager businessManager) {
        super();
        mContext = context;
        this.gMapRef = new WeakReference<GoogleMap>(gMap);
        this.businessManager = businessManager;
        setDBs_debug();
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
        ArrayList<BusinessMarker> newBusinesses = new ArrayList<BusinessMarker>();

    	for(BusinessMarker m:markersDB){
    		if(stopFlag){
    			return null;
    		}
        	//businessesList.add(m);
        	newBusinesses.add(m);
        	counter++;
        	if(counter%5==0){
        		publishProgress(newBusinesses);
        		newBusinesses = new ArrayList<BusinessMarker>();
        	}
        }
    	if(counter%5!=0){
    		publishProgress(newBusinesses);
    	}
    	return null;
    }
    
    @Override
    protected void onProgressUpdate(ArrayList<BusinessMarker>... values) {
    	if(stopFlag){
    		Log.d("LoadCloseBusinessesToMap", "stop flag is on, stops loading businesses to map");
    		return;
    	}
    	for(BusinessMarker bm : values[0]){
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

