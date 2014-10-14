package com.dna.radius.infrastructure;


import java.util.List;
import java.util.Locale;

import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.dna.radius.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class LocationSettingsFragment extends Fragment {

	private GoogleMap gMap;
	private static final float DEFAULT_LATLNG_ZOOM = 22;
	private static final float DEFAULT_ANIMATED_ZOOM = 18;
	private LatLng defaultLocation = new LatLng(31.781984, 35.218221);
	private LatLng chosenLocation = null;

	/**
	 * Parameters usage:
	 * Builds a new LocationFinderFragment. the fragment sets the map center according to the given address.
	 * if the given address is empty or null, the map center will be the default map center.
	 * @param address
	 */
	public static String ADDRESS_PARAMETER = "addressParam";
	public static String DEFAULT_LOCATION_PARAMETER = "locationParam";

	//this variable is set if the location already exists 
	//(whenever the owner wants to change his location through the settings activity)
	private LatLng prevLocation = null;


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.find_location_fragment,container, false);

		//sets a request according to the current
		TextView userRequestTextView = (TextView)view.findViewById(R.id.find_location_user_request);
		String textRequest;

		
		String address = getArguments().getString(ADDRESS_PARAMETER);
		if (address==null){
			address = "";
		}
		
		if(getArguments().containsKey(DEFAULT_LOCATION_PARAMETER) && (LatLng)getArguments().getParcelable(DEFAULT_LOCATION_PARAMETER) != null){
			defaultLocation = (LatLng)getArguments().getParcelable(DEFAULT_LOCATION_PARAMETER);
		}

		if (BaseActivity.isInBusinessMode) {

			if( !address.isEmpty() )
				textRequest = getResources().getString(R.string.find_location_please_varify);
			else
				textRequest = getResources().getString(R.string.find_location_business_request);

		}else {
			textRequest = getResources().getString(R.string.find_location_client_request);
		}

		userRequestTextView.setText(textRequest);

		//loads the google map objects and set the default location
		FragmentManager manager = getActivity().getSupportFragmentManager();
		gMap = ((SupportMapFragment)manager.findFragmentById(R.id.map)).getMap();
		gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultLocation, DEFAULT_LATLNG_ZOOM));
		gMap.animateCamera(CameraUpdateFactory.zoomTo(DEFAULT_ANIMATED_ZOOM), 2000, null);

		//adds a button which finds the current user location
		gMap.setMyLocationEnabled(true);

		gMap.setOnMapClickListener(new OnMapClickListener() {
			@Override
			public void onMapClick(LatLng point) {
				if(chosenLocation!=null){
					gMap.clear();
				}

				gMap.addMarker(new MarkerOptions().position(point));
				chosenLocation = point;
			}
		});


		//handles the search address feature
		final ImageView searchAddressBtn = (ImageView)view.findViewById(R.id.search_address_button);
		final EditText addressEditText = (EditText)view.findViewById(R.id.search_address_edit_text);
		if( !address.isEmpty() ){
			addressEditText.setHint(address);
		}

		searchAddressBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String addressStr = addressEditText.getText().toString();
				boolean success = searchForAddress(addressStr);
				if(!success){
					BaseActivity parentActivity = (BaseActivity)getActivity();
					parentActivity.createAlertDialog(getResources().getString(R.string.couldnt_find_address));
				}
			}
		});

		if( !address.isEmpty() ){
			boolean success = searchForAddress(address);
			if(!success){
				//createAlertDialog("couln't find your location based on the supplied address, please tap on the screen and set your location");
			}else{
				chosenLocation = gMap.getCameraPosition().target;
			}
		}

		//if the business already has a location - the map center will be the existing location
		if(prevLocation!=null){
			gMap.clear();
			gMap.addMarker(new MarkerOptions().position(prevLocation));
			chosenLocation = prevLocation;
			gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(chosenLocation, DEFAULT_LATLNG_ZOOM));
			gMap.animateCamera(CameraUpdateFactory.zoomTo(DEFAULT_ANIMATED_ZOOM), 2000, null);

		}
		return view;
	}


	
	private boolean searchForAddress(String addressStr){
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

				if(chosenLocation!=null){
					gMap.clear();
				}
				gMap.addMarker(new MarkerOptions().position(latLngLocation));
				chosenLocation = latLngLocation;
				Log.d("SEARCH ADRESS", "done searcing for : " + addressStr );
			}else{
				return false;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;

	}

	/**
	 * this function is called in case that the business already had a location before.
	 * meaning that the business is already exists and the owner changes his data through
	 * the settings activity.
	 * @param prevLocation
	 */
	public void setPreviousLocation(LatLng prevLocation){
		this.prevLocation = prevLocation;
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();

		//kills the old map
		SupportMapFragment mapFragment = ((SupportMapFragment) getActivity().getSupportFragmentManager().findFragmentById(R.id.map));

		if (mapFragment != null && !getActivity().isFinishing()) {
			FragmentManager fM = getActivity().getSupportFragmentManager();
			FragmentTransaction trans = fM.beginTransaction();

			trans.remove(mapFragment);
			trans.commitAllowingStateLoss();
			mapFragment = null;
		}

	}

	public boolean neededInfoGiven() {
		if(chosenLocation != null){
			return true;
		}else{
			BaseActivity parentActivity = (BaseActivity)getActivity();
			parentActivity.createAlertDialog(getResources().getString(R.string.forgot_to_set_location));
			return false;
		}
	}

	/**
	 * returns the location which was chosen by the user.
	 * if the user still didn't choose a location, returns null.
	 * @return
	 */
	public LatLng getLocation() {
		return chosenLocation;
	}
}



