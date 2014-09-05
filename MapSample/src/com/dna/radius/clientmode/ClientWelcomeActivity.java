package com.dna.radius.clientmode;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.dna.radius.R;
import com.dna.radius.dbhandling.ParseClassesNames;
import com.dna.radius.infrastructure.LocationFinderFragment;
import com.parse.ParseException;
import com.parse.ParseObject;

@SuppressLint("DefaultLocale")
public class ClientWelcomeActivity extends FragmentActivity {
	
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
		LocationFinderFragment locationFragment = new LocationFinderFragment("");
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
				if(!currentFragment.didUserFillAllData()){
					return;
				}
				ClientData.homeLocation = currentFragment.getLocation();
				
				//save to parse user new location and close
				finishRegistration();
				finish(); // activity
			}
		});
		
	}
	
	
	private void finishRegistration() { 
		
		ParseObject newClient = new ParseObject(ParseClassesNames.CLIENT_CLASS);
		
		// store location on parse
		JSONObject coordinates = new JSONObject();
		try {
			coordinates.put(ParseClassesNames.CLIENT_LOCATION_LAT ,ClientData.homeLocation.latitude);
			coordinates.put(ParseClassesNames.CLIENT_LOCATION_LONG ,ClientData.homeLocation.longitude);
			
		} catch (JSONException e) {
			
			Log.e("JSON_CREATION", e.getMessage());
		}
		newClient.put(ParseClassesNames.CLIENT_LOCATION, coordinates);
		
		
		// store preferences (favorites, likes & dislikes)
		JSONObject prefs = new JSONObject();
		try {
			prefs.put(ParseClassesNames.CLIENT_FAVORITES, new JSONArray());
			prefs.put(ParseClassesNames.CLIENT_LIKES, new JSONArray());
			prefs.put(ParseClassesNames.CLIENT_DISLIKES, new JSONArray());
			
		} catch (JSONException e) {
			
			Log.e("JSON_CREATION", e.getMessage());
		}
		newClient.put(ParseClassesNames.CLIENT_PREFERRING, prefs);

		
		// add a pointer in user to client. i.e. user->clientData
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
}
