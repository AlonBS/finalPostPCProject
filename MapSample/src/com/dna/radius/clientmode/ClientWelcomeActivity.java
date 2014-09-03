package com.dna.radius.clientmode;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.dna.radius.R;
import com.dna.radius.dbhandling.ParseClassesNames;
import com.dna.radius.login.MainActivity;
import com.google.android.gms.maps.model.LatLng;
import com.parse.ParseObject;
import com.parse.ParseUser;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class ClientWelcomeActivity extends FragmentActivity {
	
	private Button chooseLocationBtn, notNowBtn, finishBtn;
	
	private LatLng location;
	
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
		
		// This will set this dialog-themed activity to take 80% of the screen
		DisplayMetrics metrics = getResources().getDisplayMetrics();
        int screenSize = (int) (metrics.widthPixels * 0.80);
		getWindow().setLayout(screenSize, screenSize);
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
				
				location = new LatLng(34, 34);
				
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
		ArrayList<Double> coordinates = new ArrayList<Double>();
		coordinates.add(location.latitude);
		coordinates.add(location.longitude);
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
		ParseUser currentUser = ParseUser.getCurrentUser();
		currentUser.put(ParseClassesNames.CLIENT_INFO, newClient);
		
		// sync online
		newClient.saveInBackground();
		currentUser.saveInBackground();
	}
	

}
