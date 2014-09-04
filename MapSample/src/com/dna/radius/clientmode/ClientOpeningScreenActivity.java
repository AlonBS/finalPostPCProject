package com.dna.radius.clientmode;



import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import com.dna.radius.R;
import com.dna.radius.infrastructure.BaseActivity;
import com.dna.radius.mapsample.MapWindowFragment;
import com.dna.radius.mapsample.WaitingFragment;


public class ClientOpeningScreenActivity extends BaseActivity{
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.client_opening_screen);

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
							startActivity(intent);
						}
					}
				});
			}
		});
		t.start();
		
	}

}
