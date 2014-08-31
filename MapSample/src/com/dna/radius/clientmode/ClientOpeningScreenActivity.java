package com.dna.radius.clientmode;



import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import com.dna.radius.R;
import com.dna.radius.login.MainActivity;
import com.dna.radius.mapsample.AbstractActivity;
import com.dna.radius.mapsample.MapWindowFragment;
import com.dna.radius.mapsample.WaitingFragment;

public class ClientOpeningScreenActivity extends AbstractActivity{
	ClientData clientData;
	private int userID; //TODO - alon, should be given from the login module
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.client_opening_screen);

		// The FragmentManager provides methods for interacting
		// with Fragments in this Activity
		final FragmentManager fragmentManager = getSupportFragmentManager();

		// The FragmentTransaction adds, removes, replaces and defines animations for Fragments
		// beginTransaction() is used to begin any edits of Fragments
		FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

		// Create our Fragment object
		WaitingFragment waitFragment = new WaitingFragment();

		// Add the Fragment to the Activity
		fragmentTransaction.add(R.id.mapHolder, waitFragment);

		// Schedules for the addition of the Fragment to occur
		fragmentTransaction.commit();



		Thread t = new Thread(new Runnable() {
			@Override
			public void run() {
				ClientData.loadClient(userID);
				clientData = ClientData.getInstance();
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
						
						SharedPreferences settings = getSharedPreferences(MainActivity.getSPName(), Context.MODE_PRIVATE);
						boolean firstTime = settings.getBoolean("client First", false);
						
						if (firstTime) {
							
							// display welcome screen to complete login
							
							
							// update SP to never display welcome screen again
							SharedPreferences.Editor editor = settings.edit();
							editor.putBoolean("client First", true);
							editor.commit();
							
							
						}
						
						
						
						
						
					}
				});
			}
		});
		t.start();
		
	}

}
