package com.dna.radius.clientmode;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.dna.radius.R;
import com.dna.radius.dbhandling.ParseClassesNames;
import com.google.android.gms.maps.model.LatLng;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class ClientWelcomeActivity extends FragmentActivity {
	
	private Button chooseLocationBtn, notNowBtn, finishBtn;
	
	/** Default locations - TODO - add more */
	
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.client_welcome_dialog_activity);
		
		setScreenSize();
		
		initViews();
		
		setChooseLocationBtnListener();
		
		setNotNowBtnListener();
		
		setFinishBtnListener();

	}
	
	private void setScreenSize() {
		
		// This will set this dialog-theme activity to take 80% of the screen
		DisplayMetrics metrics = getResources().getDisplayMetrics();
		int height = (int) (metrics.heightPixels * 1);
        int width = (int) (metrics.widthPixels * 1);
		getWindow().setLayout(height, width);
	}
	
	private void initViews() {
		
		chooseLocationBtn = (Button) findViewById(R.id.choose_location_btn);
		notNowBtn = (Button) findViewById(R.id.not_not_btn);
		finishBtn = (Button) findViewById(R.id.finish_btn);
		
	}
	
	
	private void setChooseLocationBtnListener() {
		
		chooseLocationBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO  DROR  - how to open map in here!? 
				
				ClientData.homeLocation = new LatLng(34, 34);
				
				if (/*legal coordinates were taken from map*/ true) {
					
					finishBtn.setEnabled(true);
					
				}
			}
		});
			
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
				// TODO Auto-generated method stub
				
				//save to parse user new location and close
				finishRegistration(); //TODO data recived from map
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
