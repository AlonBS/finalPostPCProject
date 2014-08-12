package com.example.mapsample;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;

public class ClientOpeningScreenActivity extends Activity{
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.client_opening_screen);

		// The FragmentManager provides methods for interacting
		// with Fragments in this Activity
		FragmentManager fragmentManager = getFragmentManager();
		
		// The FragmentTransaction adds, removes, replaces and defines animations for Fragments
		// beginTransaction() is used to begin any edits of Fragments
		FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
		
		// Create our Fragment object
		MapWindowFragment mapWindowFragment = new MapWindowFragment();
		
		// Add the Fragment to the Activity
		fragmentTransaction.add(R.id.mapHolder, mapWindowFragment);
		
		// Schedules for the addition of the Fragment to occur
		fragmentTransaction.commit();
	}
}
