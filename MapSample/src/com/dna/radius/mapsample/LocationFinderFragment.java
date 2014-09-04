package com.dna.radius.mapsample;


import java.io.IOException;
import java.util.List;
import java.util.Locale;

import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.dna.radius.R;
import com.dna.radius.infrastructure.BaseActivity;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class LocationFinderFragment extends Fragment {
	private GoogleMap gMap;
	private boolean isInBusinessMode;
	private static final float DEFAULT_LATLNG_ZOOM = 20;
	private static final float DEFAULT_ANIMATED_ZOOM = 15;
	private static LatLng defaultLocation = new LatLng(31.781984, 35.218221);
	private LatLng chosenLocation = null;
	
	private String address = "";
	private boolean addressWasGivenAsParameter = false;

	/**
	 * Builds a new LocationFinderFragment. the fragment sets the map center according to the given address.
	 * if the given address is empty or null, the map center will be the default map center.
	 * @param address
	 */
	public LocationFinderFragment(String address){
		if(address==null || address.equals("")){
			this.address = "";
			addressWasGivenAsParameter = false;
		}else{
			this.address = address;
			addressWasGivenAsParameter = true;
		}
	}
	

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.find_location_fragment,container, false);

		isInBusinessMode = BaseActivity.isInBusinessMode;
		
		//sets a request according to the current
		TextView userRequestTv = (TextView)view.findViewById(R.id.find_location_user_request);
		String textRequest;
		if(isInBusinessMode){
			if(addressWasGivenAsParameter){
				textRequest = getResources().getString(R.string.find_location_please_varify);
			}else{
				textRequest = getResources().getString(R.string.find_location_business_request);
			}
		}else{
			textRequest = getResources().getString(R.string.find_location_client_request);
		}

		userRequestTv.setText(textRequest);
		
		
		//loads the google map objects and set it on the client's home page.
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
		final EditText etAddress = (EditText)view.findViewById(R.id.search_address_edit_text);
		if(addressWasGivenAsParameter){
			etAddress.setHint(address);
		}
		searchAddressBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String addressStr = etAddress.getText().toString();
				boolean success = searchForAddress(addressStr);
				if(!success){
					Toast.makeText(getActivity().getApplicationContext(), "couln't find the given address",Toast.LENGTH_SHORT ).show();
				}
			}
		});

		if(addressWasGivenAsParameter){
			boolean success = searchForAddress(address);
			if(!success){
				Toast.makeText(getActivity().getApplicationContext(), "couln't find your location based on the supplied address, please tap on the screen and set your location",Toast.LENGTH_LONG ).show();
			}
			chosenLocation = gMap.getCameraPosition().target;
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
	
	@Override
	public void onDestroyView() {
		// TODO Auto-generated method stub
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
	
	public boolean didUserFillAllData() {
		return chosenLocation != null;
	}

	/**
	 * returns the location which was chosen by the user.
	 * if the user still didn't choose a location, returns null.
	 * @return
	 */
	public LatLng getLocation(){
		return chosenLocation;
	}
	

}



