package com.dna.radius.clientmode;



import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import com.dna.radius.R;
import com.dna.radius.infrastructure.BaseActivity;
import com.dna.radius.infrastructure.MyApp;
import com.dna.radius.infrastructure.WaitingFragment;
import com.dna.radius.mapsample.MapWindowFragment;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.android.gms.maps.model.LatLng;


public class ClientOpeningScreenActivity extends BaseActivity{
	private final int CLIENT_WELCOME_ACTIVITY = 1;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.client_opening_screen);
		
		Tracker tracker = ((MyApp) getApplication()).getTracker(MyApp.TrackerName.APP_TRACKER);
		tracker.enableExceptionReporting(true);
		tracker.setScreenName("Client Main Screen");
		tracker.send(new HitBuilders.AppViewBuilder().build());
		
		isInBusinessMode = false;

		final FragmentManager fragmentManager = getSupportFragmentManager();
		FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
		WaitingFragment waitFragment = new WaitingFragment();
		fragmentTransaction.add(R.id.mapHolder, waitFragment);
		fragmentTransaction.commit();

		Thread t = new Thread(new Runnable() {
			@Override
			public void run() {

				ClientData.loadClientInfo();

				runOnUiThread(new Runnable() {
					@Override
					public void run() {

						Fragment mapWindowFragment = new MapWindowFragment();
						FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
						fragmentTransaction.replace(R.id.mapHolder, mapWindowFragment);
						fragmentTransaction.commit();

						displayWelcomeIfNeeded();
					}

					private void displayWelcomeIfNeeded() {
						if (ClientData.clientInfo == null) { 
							Intent intent = new Intent(getApplicationContext(), ClientWelcomeActivity.class);
							startActivityForResult(intent, CLIENT_WELCOME_ACTIVITY);
						}
					}
				});
			}
		});
		t.start();

	}


	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(requestCode==CLIENT_WELCOME_ACTIVITY){
			
			//when returning from welcome activity, if the user chose a new location - retrives it.
			//afterwards, changes the mapFragment current location.
			Bundle extras = data.getExtras();
			boolean didUserChoseLocation = extras.getBoolean(ClientWelcomeActivity.DID_USER_CHOSE_LAT_PARAM);
			if(didUserChoseLocation){
				final FragmentManager fragmentManager = getSupportFragmentManager();
				MapWindowFragment mapFragment = (MapWindowFragment)fragmentManager.findFragmentById(R.id.mapHolder);
				double lat = extras.getDouble(ClientWelcomeActivity.LATITUDE_PARAM);
				double lng = extras.getDouble(ClientWelcomeActivity.LONGITUDE_PARAM);
				LatLng location = new LatLng(lat, lng);
				mapFragment.setLocation(location);
			}

		}
	}
	

}
