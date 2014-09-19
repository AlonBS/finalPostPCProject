package com.dna.radius.clientmode;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.dna.radius.R;
import com.dna.radius.dbhandling.ParseClassesNames;
import com.dna.radius.infrastructure.BaseActivity;
import com.dna.radius.infrastructure.LocationFinderFragment;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseUser;

public class ClientWelcomeActivity extends BaseActivity {
	public static final String DID_USER_CHOSE_LAT_PARAM = "userChoseLocation";
	public static final String LATITUDE_PARAM = "latitudeParam";
	public static final String LONGITUDE_PARAM = "longitudeParam";

	private Button notNowBtn, finishBtn;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.client_welcome_dialog_activity);

		setScreenSize();

		initViews();

		setNotNowBtnListener();

		setFinishBtnListener();

		//starts the location fragment
		final FragmentManager fragmentManager = getSupportFragmentManager();
		FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
		
		LocationFinderFragment locationFragment = new LocationFinderFragment();
		Bundle bdl = new Bundle();
		bdl.putString(LocationFinderFragment.ADDRESS_PARAMETER, "");
		locationFragment.setArguments(bdl);
		
		fragmentTransaction.add(R.id.client_welcome_main_fragment_layout, locationFragment);
		fragmentTransaction.commit();


	}

	private void setScreenSize() {

		// This will set this dialog-theme activity to take 80% of the screen
		DisplayMetrics metrics = getResources().getDisplayMetrics();
		int screenWidth = (int) (metrics.widthPixels * 0.9);
		int screenHeight = (int) (metrics.heightPixels * 0.8);
		getWindow().setLayout(screenWidth,screenHeight);

	}

	private void initViews() {

		notNowBtn = (Button) findViewById(R.id.not_not_btn);
		notNowBtn.setText(notNowBtn.getText().toString().toUpperCase());

		finishBtn = (Button) findViewById(R.id.finish_btn);
		finishBtn.setText(finishBtn.getText().toString().toUpperCase());

	}


	private void setNotNowBtnListener() {

		notNowBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				// user chose to set his location later - we close dialog
				finishRegistration();

				Bundle data = new Bundle(); //TODO bundle is needed?
				data.putBoolean(DID_USER_CHOSE_LAT_PARAM, false);
				Intent intent = new Intent();
				intent.putExtras(data);
				setResult(RESULT_OK, intent);
				
				finish(); // activity
			}
		});


	}

	private void setFinishBtnListener() {

		finishBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				//receives the location from the activity
				final FragmentManager fragmentManager = getSupportFragmentManager();
				LocationFinderFragment currentFragment = (LocationFinderFragment)fragmentManager.findFragmentById(R.id.client_welcome_main_fragment_layout);
				if( !currentFragment.neededInfoGiven() )
					return;
				
				ClientData.homeLocation = currentFragment.getLocation();

				//save to parse user new location and close
				finishRegistration();
				
				Bundle data = new Bundle();
				data.putBoolean(DID_USER_CHOSE_LAT_PARAM, true);
				data.putSerializable(LATITUDE_PARAM, ClientData.homeLocation.latitude);
				data.putSerializable(LONGITUDE_PARAM, ClientData.homeLocation.longitude);
				
				Intent intent = new Intent();
				intent.putExtras(data);
				setResult(RESULT_OK, intent);
				finish(); // activity
			}
		});

	}


	private void finishRegistration() { 

		ParseObject newClient = new ParseObject(ParseClassesNames.CLIENT_CLASS);

		ParseGeoPoint location = new ParseGeoPoint(ClientData.homeLocation.latitude, ClientData.homeLocation.longitude);
		newClient.put(ParseClassesNames.CLIENT_LOCATION, location);


		// store preferences (favorites, likes & dislikes)
		JSONObject prefs = new JSONObject();
		try {
			prefs.put(ParseClassesNames.CLIENT_PREFERRING_FAVORITES, new JSONArray());
			prefs.put(ParseClassesNames.CLIENT_PREFERRING_LIKES, new JSONArray());
			prefs.put(ParseClassesNames.CLIENT_PREFERRING_DISLIKES, new JSONArray());

		} catch (JSONException e) {

			Log.e("JSON_CREATION", e.getMessage());
		}
		newClient.put(ParseClassesNames.CLIENT_PREFERRING, prefs);


		// add a pointer in user to client. i.e. user->clientData
		ClientData.currentUser = ParseUser.getCurrentUser();
		ClientData.currentUser.put(ParseClassesNames.CLIENT_INFO, newClient);

		// sync online
		try {

			newClient.save();
			ClientData.currentUser.save();
			ClientData.clientInfo = newClient;

			ClientData.currentUser.fetchIfNeeded();
			ClientData.clientInfo.fetchIfNeeded();

		} catch (ParseException e) {
			Log.e("Welcome - Client", e.getMessage());
		}
	}



	@Override
	public void onBackPressed() {/* Do nothing*/}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		return true;
	}
}
