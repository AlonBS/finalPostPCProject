package com.example.mapsample;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.datastructures.BusinessMarker;
import com.example.datastructures.BusinessMarker.BuisnessType;
import com.example.datastructures.BusinessesManager;
import com.example.datastructures.BusinessesManager.Property;
import com.example.dbhandling.DBHandler;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapWindowFragment extends Fragment {
	private BusinessesManager businessManager;
	private GoogleMap gMap;
	static final LatLng HAMBURG = new LatLng(53.558, 9.927);
	static final LatLng KIEL = new LatLng(53.551, 9.993);
	static final LatLng JAFFA_STREET = new LatLng(31.78507,35.214328);
	
	//TODO - we should decide where does this constant goes
	static public final LatLng DEFAULT_LOCATION = new LatLng(31.78507,35.214328);
//	private ArrayList<BusinessMarker> businessesList = new ArrayList<BusinessMarker>();
//	private HashMap <Marker, BusinessMarker> markerToBusiness = new HashMap <Marker, BusinessMarker>();
//	private HashMap <BusinessMarker, Marker> BusinessToMarker = new HashMap <BusinessMarker, Marker>();
	
	private static final float DEFAULT_LATLNG_ZOOM = 20;
	private static final float DEFAULT_ANIMATED_ZOOM = 15;
	private HashMap<BuisnessType,ImageView> typeToButton;
	private ImageView restBtn,pubBtn,hotelBtn,shoppingBtn,coffeeBtn;
	private View view;
	protected DBHandler dbHandler;
	private Property p;
	private Spinner spinner;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
		businessManager = new BusinessesManager(getActivity());
		
		view = inflater.inflate(R.layout.map_window_fragment,container, false);
	    gMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
	    if (gMap!=null){
			gMap.setOnMarkerClickListener(markerListener);
		}
	    gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(JAFFA_STREET, DEFAULT_LATLNG_ZOOM));
	    gMap.animateCamera(CameraUpdateFactory.zoomTo(DEFAULT_ANIMATED_ZOOM), 2000, null);
	    (LoadBuisnessesTask()).execute();
	    	
	    
	    gMap.setMyLocationEnabled(true);
	    restBtn = (ImageView)view.findViewById(R.id.resturant_filter_btn);
		pubBtn = (ImageView)view.findViewById(R.id.pub_filter_btn);
		hotelBtn = (ImageView)view.findViewById(R.id.hotel_filter_btn);
		shoppingBtn = (ImageView)view.findViewById(R.id.shopping_filter_btn);
		coffeeBtn = (ImageView)view.findViewById(R.id.coffee_filter_btn);
		
		typeToButton = new HashMap<>();
		typeToButton.put(BuisnessType.COFFEE, coffeeBtn);
		typeToButton.put(BuisnessType.PUB, pubBtn);
		typeToButton.put(BuisnessType.HOTEL, hotelBtn);
		typeToButton.put(BuisnessType.SHOPPING, shoppingBtn);
		typeToButton.put(BuisnessType.RESTURANT, restBtn);
		
		restBtn.setOnClickListener(filterBtnClickListener);
		pubBtn.setOnClickListener(filterBtnClickListener);
		hotelBtn.setOnClickListener(filterBtnClickListener);
		shoppingBtn.setOnClickListener(filterBtnClickListener);
		coffeeBtn.setOnClickListener(filterBtnClickListener);
		
		/*restBtn.setSelected(true);
		pubBtn.setSelected(true);
		hotelBtn.setSelected(true);
		shoppingBtn.setSelected(true);
		coffeeBtn.setSelected(true);*/
		
		
		final ImageView searchAddressBtn = (ImageView)view.findViewById(R.id.search_address_button);
		final EditText etAddress = (EditText)view.findViewById(R.id.search_address_edit_text);
		etAddress.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				etAddress.setText("");
			}
		});
		
		dbHandler = new DBHandler(getActivity());
		final ImageView homeButton = (ImageView)view.findViewById(R.id.map_home_btn);
		homeButton.setOnLongClickListener(new OnLongClickListener() {
			
			@Override
			public boolean onLongClick(View v) {
				LatLng latLng = gMap.getCameraPosition().target;
				dbHandler.setHome(latLng.latitude, latLng.longitude);
				
				Toast.makeText(getActivity(), "new home location was selected", Toast.LENGTH_SHORT).show();
				return false;
			}
		});
		homeButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				LatLng loc = dbHandler.getHome();
				gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(loc, DEFAULT_ANIMATED_ZOOM));
				gMap.animateCamera(CameraUpdateFactory.zoomTo(DEFAULT_ANIMATED_ZOOM), 2000, null);
			}
		});	
		
		
		searchAddressBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				final String addressStr = etAddress.getText().toString();
				Log.d("SEARCH ADRESS", "searching for: " + addressStr );
				Geocoder geoCoder = new Geocoder(getActivity(), Locale.getDefault());
			    try {
			        List<Address> addresses = geoCoder.getFromLocationName(addressStr, 5);
			        if (addresses.size() > 0) {
			            Double lat = (double) (addresses.get(0).getLatitude());
			            Double lon = (double) (addresses.get(0).getLongitude());
			            Log.d("lat-long", "" + lat + "......." + lon);
			            final LatLng latLngLocation = new LatLng(lat, lon);

			            gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLngLocation, DEFAULT_LATLNG_ZOOM));
			            // Zoom in, animating the camera.
			            gMap.animateCamera(CameraUpdateFactory.zoomTo(DEFAULT_ANIMATED_ZOOM), 2000, null);
			        }
			    } catch (IOException e) {
			        e.printStackTrace();
			        Toast.makeText(getActivity().getApplicationContext(), "couln't find the given address",Toast.LENGTH_SHORT );
			    }
			    catch (NullPointerException e) {
			        e.printStackTrace();
			        Toast.makeText(getActivity().getApplicationContext(), "couln't find the given address",Toast.LENGTH_SHORT );
			    }
				
				
				etAddress.setText(getResources().getString(R.string.type_address));
				
			}
		});
		
		
		//ArrayAdapter adapter = ArrayAdapter.createFromResource(getActivity(), R.array.spinner_options, R.drawable.spinner_item);
		spinner = (Spinner)view.findViewById(R.id.filter_spinner);
		//spinner.setAdapter(adapter);
		p = Property.ALL;
		
		spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
		    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
		    	updateOverlays();		        
		    }
		    public void onNothingSelected(AdapterView<?> parent) {
		    }
		});
		
		return view;
	}
	
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		dbHandler.close();
	}
	@Override
	public void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		
		loadDBs_debug();
		
		//load personal info from sqlite (favourites)
		loadPersonalInfo();
		
		
		
		
		
		
		
		
	}
	
	private OnClickListener filterBtnClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			ImageView currentButton = (ImageView)v;
			currentButton.setSelected(!currentButton.isSelected());
			updateOverlays();
			/*ImageView btn;
			BusinessMarker.BuisnessType type;
			if(v==restBtn){
				btn = (ImageView)view.findViewById(R.id.resturant_filter_btn);
				type = BuisnessType.RESTURANT;
			}else if(v==pubBtn){
				btn = (ImageView)view.findViewById(R.id.pub_filter_btn);
				type = BuisnessType.PUB;
			}else if(v==hotelBtn){
				btn = (ImageView)view.findViewById(R.id.hotel_filter_btn);
				type = BuisnessType.HOTEL;
			}else if(v==shoppingBtn){
				btn = (ImageView)view.findViewById(R.id.shopping_filter_btn);
				type = BuisnessType.SHOPPING;
			}else if(v==coffeeBtn){
				btn = (ImageView)view.findViewById(R.id.coffee_filter_btn);
				type = BuisnessType.COFFEE;
			}else{
				Log.d("filterBtnClickListener", "filterBtnClickListener was called, but the selected button wasnt found");
				return;
			}
			
			boolean newState = !btn.isSelected();
			btn.setSelected(newState);
			for(BusinessMarker bm :  businessManager.getAllBusinesses()){
				if(bm.type==type){
					Marker m = businessManager.getMarker(bm);
					if(m==null){
						Log.d("filterBtnClickListener","didn't find corresponding marker for a business");
					}else{
						m.setVisible(newState);
					}
					
				}
			}*/
		}

	};
	
	private void updateOverlays(){
		
		
		String item = spinner.getSelectedItem().toString();	 
		Property p;
        if(item.equals("My favourites")){
        	p = Property.FAVORITES_PROP;
        }else if(item.equals("Top businesses")){
        	p = Property.TOP_BUSINESS_PROP;
        }else if(item.equals("Top deals")){
        	p = Property.TOP_DEALS_PROP;
        }else{
        	p = Property.ALL;
        }
        
		for(BusinessMarker bm :  businessManager.getAllBusinesses()){
			Marker m = businessManager.getMarker(bm);
			if(m==null){
				Log.d("filterBtnClickListener","didn't find corresponding marker for a business");
				continue;
			}
			ImageView button = typeToButton.get(bm.type);
			if(button.isSelected()){
				m.setVisible(false);
			}else{
				boolean visibilityState = businessManager.hasProperty(bm, p);
				m.setVisible(visibilityState);
			}
		}
	}
	private void loadPersonalInfo() {
		
		
	}

	private void putOverlayOnMap(BusinessMarker bm){
		Marker m =  gMap.addMarker(new MarkerOptions().position(bm.pos).title(bm.name).icon(BitmapDescriptorFactory.fromResource(bm.iconID)));
    	businessManager.addBusiness(bm, m);

	}
	
	private LoadBusinessesToMap LoadBuisnessesTask(){return new LoadBusinessesToMap(getActivity());}
	
	
	/**
	 * is called whenever the user presses on the map marker.
	 * This will open a new deal ShowDealActivity. 
	 */
	private OnMarkerClickListener markerListener = new OnMarkerClickListener() {
		@Override
		public boolean onMarkerClick(Marker marker) {
			BusinessMarker bMarker = businessManager.getBusiness(marker);
			if(bMarker==null)
				Toast.makeText(getActivity(),"sry null", Toast.LENGTH_SHORT).show();
			else{
				Toast.makeText(getActivity(),"your in: " +  bMarker.name, Toast.LENGTH_SHORT).show();
				Intent myIntent = new Intent(getActivity(), ShowDealActivity.class);
				myIntent.putExtra(ShowDealActivity.BUSINESS_NAME_PARAM, bMarker.name); //Optional parameters
				myIntent.putExtra(ShowDealActivity.BUSINESS_ID_PARAM, bMarker.businessId); //Optional parameters
				myIntent.putExtra(ShowDealActivity.BUSINESS_TYPE_PARAM, bMarker.type); //Optional parameters
				myIntent.putExtra(ShowDealActivity.DEAL_RATING_PARAM, bMarker.rating); //Optional parameters
				myIntent.putExtra(ShowDealActivity.NUM_OF_DISLIKES_PARAM, bMarker.numOfDislikes); //Optional parameters
				myIntent.putExtra(ShowDealActivity.NUM_OF_LIKES_PARAM, bMarker.numOfLikes); //Optional parameters
				myIntent.putExtra(ShowDealActivity.USER_MODE_PARAM, true);
				
				//myIntent.putExtra(ShowDealActivity.BUSINESS_MARKER_PARAM, bMarker);
				//myIntent.putExtra(ShowDealActivity.BUSINESS_MARKER_PARAM, bMarker);
				getActivity().startActivity(myIntent);
			}
			return false;
		}
		
	};
	

	    public class LoadBusinessesToMap extends AsyncTask<Void, ArrayList<BusinessMarker>, Void> {
	        Context mContext;
	        int NUM_OF_LOADS_BEFORE_REFRESH = 5;
	        public LoadBusinessesToMap(Context context) {
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
	        protected Void doInBackground(Void... params) {
	        	//Marker Jerusalem = gMap.addMarker(new MarkerOptions().position(JAFFA_STREET).title("Jerusalem").snippet("many dosim").icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_launcher)));
	            int counter = 0;
	            ArrayList<BusinessMarker> newBusinesses = new ArrayList<BusinessMarker>();
	        	for(BusinessMarker m:markersDB){
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
	        	for(BusinessMarker bm : values[0]){
	            	putOverlayOnMap(bm);
	        	}
	        	super.onProgressUpdate(values);
	        	
	        }
	        
	        /**
	         * A method that's called once doInBackground() completes. 
	         */
	        @Override
	        protected void onPostExecute(Void result) {
	            
	        	Toast.makeText(getActivity(),"finished update", Toast.LENGTH_SHORT).show();
	        }

	        
	    }
	    
	    private static List<BusinessMarker> markersDB; //TODO: delete
	    public void loadDBs_debug()
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
	
}
