package com.dna.radius.clientmode;



import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import com.dna.radius.mapsample.AbstractActivity;
import com.dna.radius.mapsample.MapWindowFragment;
import com.dna.radius.mapsample.WaitingFragment;
import com.example.mapsample.R;

public class ClientOpeningScreenActivity extends AbstractActivity{
	ClientData clientData;
	private int id; //TODO - alon, should be given from the login module
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
				ClientData.loadClient(id);
				clientData = ClientData.getInstance();
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						Fragment mapWindowFragment = new MapWindowFragment();
						FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
						fragmentTransaction.replace(R.id.mapHolder, mapWindowFragment);
						fragmentTransaction.commit();
					}
				});
			}
		});
		t.start();
		
	}

}
