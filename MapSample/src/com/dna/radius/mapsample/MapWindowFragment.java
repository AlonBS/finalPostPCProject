package com.dna.radius.mapsample;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.dna.radius.clientmode.ClientData;
import com.dna.radius.datastructures.BusinessManager;
import com.dna.radius.datastructures.BusinessMarker;
import com.dna.radius.datastructures.BusinessManager.Property;
import com.dna.radius.datastructures.BusinessMarker.BuisnessType;
import com.dna.radius.dbhandling.DBHandler;
import com.example.mapsample.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnCameraChangeListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapWindowFragment extends Fragment {
	private BusinessManager businessManager;
	private GoogleMap gMap;
	static final LatLng HAMBURG = new LatLng(53.558, 9.927);
	static final LatLng KIEL = new LatLng(53.551, 9.993);
	static final LatLng JAFFA_STREET = new LatLng(31.78507,35.214328);
	private boolean isInBusinessMode;
	
	//TODO - we should decide where does this constant goes
	static public final LatLng DEFAULT_LOCATION = new LatLng(31.78507,35.214328);
//	private ArrayList<BusinessMarker> businessesList = new ArrayList<BusinessMarker>();
//	private HashMap <Marker, BusinessMarker> markerToBusiness = new HashMap <Marker, BusinessMarker>();
//	private HashMap <BusinessMarker, Marker> BusinessToMarker = new HashMap <BusinessMarker, Marker>();
	final double RADIUS = 0.01; 	
	private static final float DEFAULT_LATLNG_ZOOM = 20;
	private static final float DEFAULT_ANIMATED_ZOOM = 15;
	private HashMap<BuisnessType,ImageView> typeToButton;
	private ImageView restBtn,pubBtn,hotelBtn,shoppingBtn,coffeeBtn;
	private View view;
	protected DBHandler dbHandler;
	private Property p;
	private Spinner spinner;
	private LatLng latestMapCenter = null;
	private ClientData clientData;
	
	
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
		clientData = ClientData.getInstance();
		
		businessManager = new BusinessManager(clientData);
		dbHandler = new DBHandler(getActivity());
		isInBusinessMode = AbstractActivity.isInBusinessMode;
		
		
		view = inflater.inflate(R.layout.map_window_fragment,container, false);
		FragmentManager manager = getActivity().getSupportFragmentManager();
		gMap = ((SupportMapFragment)manager.findFragmentById(R.id.map)).getMap();
	    if (gMap!=null){
			gMap.setOnMarkerClickListener(markerListener);
		}
	    latestMapCenter = clientData.getHome();

	    gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latestMapCenter, DEFAULT_LATLNG_ZOOM));
	    gMap.animateCamera(CameraUpdateFactory.zoomTo(DEFAULT_ANIMATED_ZOOM), 2000, null);
	    
	    gMap.setMyLocationEnabled(true);
		
	    dbHandler.loadBusinessListAndMapMarkersAsync(gMap.getCameraPosition().target, gMap, businessManager,RADIUS,getActivity());
	    
	    gMap.setOnCameraChangeListener(new OnCameraChangeListener() {
			
			@Override
			public void onCameraChange(CameraPosition position) {
				if (Math.abs(position.target.latitude-latestMapCenter.latitude)>RADIUS ||
						Math.abs(position.target.longitude-latestMapCenter.longitude)>RADIUS){
					latestMapCenter = position.target;
					dbHandler.stopLoadBusinessListAndMapMarkersAsync();
					dbHandler.loadBusinessListAndMapMarkersAsync(position.target, gMap, businessManager, RADIUS,getActivity());
					Log.d("MapWindowFragment","map center was changed significantly. loading businesses again.");
				}
				
			}
		});
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
		
		
		final ImageView homeButton = (ImageView)view.findViewById(R.id.map_home_btn);
		homeButton.setOnLongClickListener(new OnLongClickListener() {
			
			@Override
			public boolean onLongClick(View v) {
				LatLng latLng = gMap.getCameraPosition().target;
				clientData.setHome(latLng,true);
				
				Toast.makeText(getActivity(), "new home location was selected", Toast.LENGTH_SHORT).show();
				return false;
			}
		});
		homeButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				LatLng loc = clientData.getHome();
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
		
		//kills the old map
		SupportMapFragment mapFragment = ((SupportMapFragment) getActivity().getSupportFragmentManager().findFragmentById(R.id.map));

	    if(mapFragment != null) {
	        FragmentManager fM = getFragmentManager();
	        fM.beginTransaction().remove(mapFragment).commit();
	        mapFragment = null;
	    }
		
		
	}
	@Override
	public void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);

		
		//load personal info from sqlite (favourites)
		loadPersonalInfo();
		
		
		
		
		
		
		
		
	}
	
	private OnClickListener filterBtnClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			ImageView currentButton = (ImageView)v;
			currentButton.setSelected(!currentButton.isSelected());
			updateOverlays();
			
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
				myIntent.putExtra(ShowDealActivity.DEAL_RATING_PARAM, bMarker.numOfStars); //Optional parameters
				myIntent.putExtra(ShowDealActivity.NUM_OF_DISLIKES_PARAM, bMarker.numOfDislikes); //Optional parameters
				myIntent.putExtra(ShowDealActivity.NUM_OF_LIKES_PARAM, bMarker.numOfLikes); //Optional parameters
				myIntent.putExtra(ShowDealActivity.USER_MODE_PARAM, !isInBusinessMode);
				getActivity().startActivity(myIntent);
			}
			return false;
		}
		
	};
	

	   
}
