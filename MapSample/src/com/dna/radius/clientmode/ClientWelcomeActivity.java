package com.dna.radius.clientmode;

import java.util.ArrayList;

import com.dna.radius.R;
import com.dna.radius.dbhandling.ParseClassesNames;
import com.dna.radius.login.MainActivity;
import com.parse.ParseObject;
import com.parse.ParseUser;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class ClientWelcomeActivity extends FragmentActivity {
	
	private Button chooseLocationBtn, notNowBtn, finishBtn;
	
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
				
				finishRegistration(0,0);
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
				finishRegistration(12, 43); //TODO data recived from map
				finish(); // activity
			}
		});
		
	}
	
	
	
	// todo LATLING ?
	private void finishRegistration(double lat, double lng) { 
		
		ParseObject newClient = new ParseObject(ParseClassesNames.CLIENT_CLASS);
		
		ArrayList<Double> coordinates = new ArrayList<Double>();
		coordinates.add(lat);
		coordinates.add(lng);
		newClient.put(ParseClassesNames.CLIENT_LOCATION, coordinates);
		
		// add a pointer in user to client. i.e. user->clientData
		ParseUser currentUser = ParseUser.getCurrentUser();
		currentUser.put(ParseClassesNames.CLIENT_INFO, newClient);
		
		newClient.saveInBackground();
		currentUser.saveInBackground();
		
		
		
		// update SP to never display welcome screen again
		SharedPreferences settings = getSharedPreferences(MainActivity.getSPName(), Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = settings.edit();
		editor.putBoolean("client First", false);
		editor.commit();
		
	}
	

}
