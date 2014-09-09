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

import com.dna.radius.R;
import com.dna.radius.businessmode.BusinessData;
import com.dna.radius.clientmode.ClientData;
import com.dna.radius.datastructures.ExternalBusiness;
import com.dna.radius.dbhandling.DBHandler;
import com.dna.radius.infrastructure.BaseActivity;
import com.dna.radius.infrastructure.SupportedTypes;
import com.dna.radius.mapsample.MapBusinessManager.Property;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnCameraChangeListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

public class MapWindowFragment extends Fragment {
	private GoogleMap gMap;
	public static final double LOAD_RADIUS = 0.01; 	
	private static final float DEFAULT_LATLNG_ZOOM = 20;
	private static final float DEFAULT_ANIMATED_ZOOM = 15;
	private HashMap<SupportedTypes.BusinessType,ImageView> typeToButton;
	private ImageView restBtn,pubBtn,hotelBtn,shoppingBtn,coffeeBtn;
	private View view;
	private Spinner preferencedSpinner;
	private LatLng latestMapCenter = null;


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.map_window_fragment,container, false);


		//loads the google map objects and set it on the client's home page.
		FragmentManager manager = getActivity().getSupportFragmentManager();
		gMap = ((SupportMapFragment)manager.findFragmentById(R.id.map)).getMap();
		if (gMap!=null){
			gMap.setOnMarkerClickListener(markerListener);
		}
			
		MapBusinessManager.init(gMap);
		
		if(BaseActivity.isInBusinessMode){
			latestMapCenter = BusinessData.getLocation();
		}else{
			latestMapCenter = ClientData.getHome();
		}
		gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latestMapCenter, DEFAULT_LATLNG_ZOOM));
		gMap.animateCamera(CameraUpdateFactory.zoomTo(DEFAULT_ANIMATED_ZOOM), 2000, null);

		//adds a button which finds the current user location
		gMap.setMyLocationEnabled(true);

		//loads businesses to map
//		DBHandler.loadBusinessListAndMapMarkersAsync(gMap.getCameraPosition().target, gMap, businessManager,LOAD_RADIUS,this);
		MapBusinessManager.loadExternalBusinesses();
		
		final MapWindowFragment thisFragment = this;
		//if the the map center was changed significantly (more then a Radius),
		//starts loading businesses again, around the new radius.
		gMap.setOnCameraChangeListener(new OnCameraChangeListener() {
			@Override
			public void onCameraChange(CameraPosition position) {
				if (Math.abs(position.target.latitude-latestMapCenter.latitude)>LOAD_RADIUS ||
						Math.abs(position.target.longitude-latestMapCenter.longitude)>LOAD_RADIUS){
					latestMapCenter = position.target;
//					DBHandler.stopLoadBusinessListAndMapMsarkersAsync();

//					DBHandler.loadBusinessListAndMapMarkersAsync(position.target, gMap, businessManager, LOAD_RADIUS,thisFragment);
					Log.d("MapWindowFragment","map center was changed significantly. loading businesses again.");
				}

			}
		});

		//handles the filter buttons.
		restBtn = (ImageView)view.findViewById(R.id.resturant_filter_btn);
		pubBtn = (ImageView)view.findViewById(R.id.pub_filter_btn);
		hotelBtn = (ImageView)view.findViewById(R.id.hotel_filter_btn);
		shoppingBtn = (ImageView)view.findViewById(R.id.shopping_filter_btn);
		coffeeBtn = (ImageView)view.findViewById(R.id.coffee_filter_btn);

		typeToButton = new HashMap<>();
		typeToButton.put(SupportedTypes.BusinessType.COFFEE, coffeeBtn);
		typeToButton.put(SupportedTypes.BusinessType.PUB, pubBtn);
		typeToButton.put(SupportedTypes.BusinessType.ACCOMMODATION, hotelBtn);
		typeToButton.put(SupportedTypes.BusinessType.GROCERIES, shoppingBtn);
		typeToButton.put(SupportedTypes.BusinessType.RESTAURANT, restBtn);

		restBtn.setOnClickListener(filterBtnClickListener);
		pubBtn.setOnClickListener(filterBtnClickListener);
		hotelBtn.setOnClickListener(filterBtnClickListener);
		shoppingBtn.setOnClickListener(filterBtnClickListener);
		coffeeBtn.setOnClickListener(filterBtnClickListener);

		//handles the search address feature
		final ImageView searchAddressBtn = (ImageView)view.findViewById(R.id.search_address_button);
		final EditText etAddress = (EditText)view.findViewById(R.id.search_address_edit_text);
		etAddress.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				etAddress.setText("");
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
					Toast.makeText(getActivity().getApplicationContext(), "couln't find the given address",Toast.LENGTH_SHORT ).show();
				}
				catch (NullPointerException e) {
					e.printStackTrace();
					Toast.makeText(getActivity().getApplicationContext(), "couln't find the given address",Toast.LENGTH_SHORT ).show();
				}


				etAddress.setText(getResources().getString(R.string.type_address));

			}
		});

		//handles the set/get home location feature
		final ImageView homeButton = (ImageView)view.findViewById(R.id.map_home_btn);
		if(BaseActivity.isInBusinessMode){
			homeButton.setVisibility(View.GONE);
		}else{
			homeButton.setOnLongClickListener(new OnLongClickListener() {
				@Override
				public boolean onLongClick(View v) {
					LatLng latLng = gMap.getCameraPosition().target;
					ClientData.setHome(latLng);

					Toast.makeText(getActivity(), "new home location was selected", Toast.LENGTH_SHORT).show();
					return false;
				}
			});
			homeButton.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					LatLng loc = ClientData.getHome();
					gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(loc, DEFAULT_ANIMATED_ZOOM));
					gMap.animateCamera(CameraUpdateFactory.zoomTo(DEFAULT_ANIMATED_ZOOM), 2000, null);
				}
			});	
		}
		//handles the spinner preferences (top deals/favourites/etc)
		preferencedSpinner = (Spinner)view.findViewById(R.id.filter_spinner);
		preferencedSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
				updateOverlays();		        
			}
			public void onNothingSelected(AdapterView<?> parent) {
			}
		});

		return view;
	}

	
	public void setLocation(LatLng location){
		gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, DEFAULT_ANIMATED_ZOOM));
		gMap.animateCamera(CameraUpdateFactory.zoomTo(DEFAULT_ANIMATED_ZOOM), 2000, null);
	}

	public GoogleMap getGMap(){
		return gMap;
	}
	
	@Override
	public void onDestroyView() {
		super.onDestroyView();

		//kills the old map
		SupportMapFragment mapFragment = ((SupportMapFragment) getActivity().getSupportFragmentManager().findFragmentById(R.id.map));

		if(mapFragment != null && !getActivity().isFinishing()) {
			FragmentManager fM = getActivity().getSupportFragmentManager();
			android.support.v4.app.FragmentTransaction trans = fM.beginTransaction();

			trans.remove(mapFragment);
			trans.commitAllowingStateLoss();
			mapFragment = null;
		}

	}


	/***
	 * a listener for a filter button
	 */
	private OnClickListener filterBtnClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			ImageView currentButton = (ImageView)v;
			currentButton.setSelected(!currentButton.isSelected());
			updateOverlays();

		}

	};

	/**
	 * This function is called to make sure the the overlays visibility is according to
	 * the user preferenced.
	 */
	public void updateOverlays(){

		String item = preferencedSpinner.getSelectedItem().toString();	 
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

		for(ExternalBusiness bm :  MapBusinessManager.getAllBusinesses()){
			
			Marker m = MapBusinessManager.getMarker(bm);
			if(m==null){
				Log.d("filterBtnClickListener","didn't find corresponding marker for a business");
				continue;
			}
			ImageView button = typeToButton.get(bm.getExternBusinessType());
			if(button.isSelected()){
				m.setVisible(false);
			}else{
				boolean visibilityState = MapBusinessManager.hasProperty(bm, p);
				m.setVisible(visibilityState);
			}
		}
	}

	/**
	 * this function is called whenever the user presses on the map marker.
	 * This will open a new deal ShowDealActivity. 
	 */
	private OnMarkerClickListener markerListener = new OnMarkerClickListener() {
		@Override
		public boolean onMarkerClick(Marker marker) {
			ExternalBusiness pressedExternalBusiness = MapBusinessManager.getBusiness(marker);
			if (pressedExternalBusiness != null) {
				
				Intent myIntent = new Intent(getActivity(), ShowDealActivity.class);
				myIntent.putExtra(ShowDealActivity.EXTERNAL_BUSINESS_KEY, pressedExternalBusiness);
				
//TODO remove
//				myIntent.putExtra(ShowDealActivity.BUSINESS_NAME_PARAM, pressedExternalBusiness.businessName);
//				myIntent.putExtra(ShowDealActivity.BUSINESS_ID_PARAM, pressedExternalBusiness.externBusinessId);
//				myIntent.putExtra(ShowDealActivity.DEAL_ID_PARAM, pressedExternalBusiness.currentDealID); 
//				myIntent.putExtra(ShowDealActivity.BUSINESS_TYPE_PARAM, pressedExternalBusiness.externBusinessType);
//				myIntent.putExtra(ShowDealActivity.DEAL_RATING_PARAM, pressedExternalBusiness.rating);
//				myIntent.putExtra(ShowDealActivity.NUM_OF_DISLIKES_PARAM, pressedExternalBusiness.numOfDislikes); 
//				myIntent.putExtra(ShowDealActivity.NUM_OF_LIKES_PARAM, pressedExternalBusiness.numOfLikes);
//				myIntent.putExtra(ShowDealActivity.USER_MODE_PARAM, !BaseActivity.isInBusinessMode);
//				myIntent.putExtra(ShowDealActivity.PHONE_STR_PARAM, pressedExternalBusiness.phoneStr); 
//				myIntent.putExtra(ShowDealActivity.ADDRESS_STR_PARAM, pressedExternalBusiness.addressStr);
//				myIntent.putExtra(ShowDealActivity.CURRENT_DEAL_STR_PARAM, pressedExternalBusiness.currentDealStr);
				getActivity().startActivity(myIntent);
				
			}
			else {
				Log.e("MapWindowsFragment", "the returned marker is null");
			}
			return false;
		}

	};


}
