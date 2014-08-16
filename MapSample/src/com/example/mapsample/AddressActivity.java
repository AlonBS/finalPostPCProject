package com.example.mapsample;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import android.app.Activity;
import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.mapsample.BusinessMarker.BuisnessType;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class AddressActivity extends Activity {
	private GoogleMap gMap;
	static final LatLng HAMBURG = new LatLng(53.558, 9.927);
	static final LatLng KIEL = new LatLng(53.551, 9.993);
	static final LatLng JAFFA_STREET = new LatLng(31.78507,35.214328);
	 String mAddress;
	 private ProgressBar mActivityIndicator;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_address);
		mActivityIndicator =(ProgressBar) findViewById(R.id.address_progress);
		final GoogleMap gMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
		final GetAddressTask getAddtessTask = new GetAddressTask(this);
		if (gMap!=null){
			//Marker hamburg = gMap.addMarker(new MarkerOptions().position(HAMBURG).title("Hamburg"));
			//Marker kiel = gMap.addMarker(new MarkerOptions().position(KIEL).title("Kiel").snippet("Kiel is cool").icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_launcher)));
			Marker Jerusalem = gMap.addMarker(new MarkerOptions().position(JAFFA_STREET).title("Jerusalem").snippet("many dosim").icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_launcher)));
			gMap.setOnMarkerClickListener(markerListener);
		}
		gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(JAFFA_STREET, 15));
		gMap.animateCamera(CameraUpdateFactory.zoomTo(10), 2000, null);
		
		Button findAddressBtn = (Button)findViewById(R.id.get_location_btn);
		findAddressBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				// Ensure that a Geocoder services is available
				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD &&  Geocoder.isPresent()) {
					// Show the activity indicator
					mActivityIndicator.setVisibility(View.VISIBLE);
					/*
					 * Reverse geocoding is long-running and synchronous.
					 * Run it on a background thread.
					 * Pass the current location to the background task.
					 * When the task finishes,	
					 * onPostExecute() displays the address.
					 */
					
					LatLng center = gMap.getCameraPosition().target;
					Location location = new Location("current location");
					location.setLatitude(center.latitude);
					location.setLongitude(center.longitude);
					//gMap.setMyLocationEnabled(true);
					(GetAddressTask()).execute(location);
				}
				
			}
		});
		

	}
	
	private GetAddressTask GetAddressTask(){return new GetAddressTask(this);}
	private OnMarkerClickListener markerListener = new OnMarkerClickListener() {
		@Override
		public boolean onMarkerClick(Marker marker) {
			Toast.makeText(getApplicationContext(),"your in: " +  marker.getTitle(), Toast.LENGTH_SHORT).show();
			return false;
		}
	};
	
	
	/**
	    * A subclass of AsyncTask that calls getFromLocation() in the
	    * background. The class definition has these generic types:
	    * Location - A Location object containing
	    * the current location.
	    * Void     - indicates that progress units are not used
	    * String   - An address passed to onPostExecute()
	    */
	    public class GetAddressTask extends AsyncTask<Location, Void, String> {
	        Context mContext;
	        public GetAddressTask(Context context) {
	            super();
	            mContext = context;
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
	        @Override
	        protected String doInBackground(Location... params) {
	            Geocoder geocoder = new Geocoder(mContext, Locale.getDefault());
	            // Get the current location from the input parameter list
	            Location loc = params[0];
	            // Create a list to contain the result address
	            List<Address> addresses = null;
	            try {
	                /*
	                 * Return 1 address.
	                 */
	                addresses = geocoder.getFromLocation(loc.getLatitude(),
	                        loc.getLongitude(), 1);
	            } catch (IOException e1) {
	            Log.e("LocationSampleActivity",
	                    "IO Exception in getFromLocation()");
	            e1.printStackTrace();
	            return ("IO Exception trying to get address");
	            } catch (IllegalArgumentException e2) {
	            // Error message to post in the log
	            String errorString = "Illegal arguments " +
	                    Double.toString(loc.getLatitude()) +
	                    " , " +
	                    Double.toString(loc.getLongitude()) +
	                    " passed to address service";
	            Log.e("LocationSampleActivity", errorString);
	            e2.printStackTrace();
	            return errorString;
	            }
	            // If the reverse geocode returned an address
	            if (addresses != null && addresses.size() > 0) {
	                // Get the first address
	                Address address = addresses.get(0);
	                /*
	                 * Format the first line of address (if available),
	                 * city, and country name.
	                 */
	                String addressText = String.format("%s, %s, %s",
	                        // If there's a street address, add it
	                        address.getMaxAddressLineIndex() > 0 ?
	                                address.getAddressLine(0) : "",
	                        // Locality is usually a city
	                        address.getLocality(),
	                        // The country of the address
	                        address.getCountryName());
	                // Return the text
	                return addressText;
	            } else {
	                return "No address found";
	            }
	        }
	        
	        /**
	         * A method that's called once doInBackground() completes. Turn
	         * off the indeterminate activity indicator and set
	         * the text of the UI element that shows the address. If the
	         * lookup failed, display the error message.
	         */
	        @Override
	        protected void onPostExecute(String address) {
	            // Set activity indicator visibility to "gone"
	            mActivityIndicator.setVisibility(View.GONE);
	            // Display the results of the lookup.
	            
	        	Toast.makeText(getApplicationContext(),address, Toast.LENGTH_SHORT).show();
	        }
	        
	    }
	    
	    
	    static List<BusinessMarker> markersList;
	    {
	    	markersList = new ArrayList<BusinessMarker>();
	    	Random r = new Random();
	    	long id = 0;
	    	markersList.add(new BusinessMarker("MCdonalds", BuisnessType.RESTURANT, new LatLng(31.781099, 35.217668), "Jerusalem",id++));
	    	markersList.add(new BusinessMarker("Ivo", BuisnessType.RESTURANT, new LatLng(31.779949, 35.218948), "Jerusalem",id++));
	    	markersList.add(new BusinessMarker("Dolfin Yam", BuisnessType.RESTURANT, new LatLng(31.779968, 35.221209), "Jerusalem",id++));
	    	markersList.add(new BusinessMarker("Birman", BuisnessType.PUB, new LatLng(31.781855, 35.218086), "Jerusalem",id++));
	    	markersList.add(new BusinessMarker("Bullinat", BuisnessType.PUB, new LatLng(31.781984, 35.218221), "Jerusalem",id++));
	    	markersList.add(new BusinessMarker("Hamarush", BuisnessType.RESTURANT, new LatLng(31.781823, 35.219065), "Jerusalem",id++));
	    	markersList.add(new BusinessMarker("Adom", BuisnessType.RESTURANT, new LatLng(31.781334, 35.220703), "Jerusalem",id++));
	    	markersList.add(new BusinessMarker("Tel Aviv Bar", BuisnessType.PUB, new LatLng(31.781455, 35.220525), "Jerusalem",id++));
	    	markersList.add(new BusinessMarker("Jabutinski Bar", BuisnessType.PUB, new LatLng(31.779654, 35.221654), "Jerusalem",id++));
	    	markersList.add(new BusinessMarker("Reva Sheva", BuisnessType.SHOPPING, new LatLng(31.779793, 35.219728), "Jerusalem",id++));
	    	markersList.add(new BusinessMarker("The one with the shirts", BuisnessType.SHOPPING, new LatLng(31.779293, 35.221624), "Jerusalem",id++));
	    	markersList.add(new BusinessMarker("Hamashbir Latsarchan", BuisnessType.SHOPPING, new LatLng(31.781824, 35.219959), "Jerusalem",id++));
	    	markersList.add(new BusinessMarker("Hataklit", BuisnessType.PUB, new LatLng(31.781905, 35.221372), "Jerusalem",id++));
	    	markersList.add(new BusinessMarker("Hatav Hashmini", BuisnessType.SHOPPING, new LatLng(31.781191, 35.219621), "Jerusalem",id++));
	    }
	
}
