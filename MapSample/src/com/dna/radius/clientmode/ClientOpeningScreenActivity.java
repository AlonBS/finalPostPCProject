package com.dna.radius.clientmode;



import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import com.dna.radius.R;
import com.dna.radius.infrastructure.BaseActivity;
import com.dna.radius.login.IntroFragment;
import com.dna.radius.login.MainActivity;
import com.dna.radius.mapsample.MapWindowFragment;
import com.dna.radius.mapsample.WaitingFragment;

public class ClientOpeningScreenActivity extends BaseActivity{
	ClientData clientData;
	private int userID; //TODO - alon, should be given from the login module
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.client_opening_screen);

		final FragmentManager fragmentManager = getSupportFragmentManager();
		FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
		WaitingFragment waitFragment = new WaitingFragment();
		fragmentTransaction.add(R.id.mapHolder, waitFragment);
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
						
						if (getIntent().getExtras() == null) { // if the bundle returned by getExtras() is 
															   // not null, then it is not first time
							
							Intent intent = new Intent(getApplicationContext(), ClientWelcomeActivity.class);
							startActivity(intent);
						}
					}
				});
			}
		});
		t.start();
		
	}

}
