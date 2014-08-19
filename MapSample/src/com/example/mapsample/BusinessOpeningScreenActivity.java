package com.example.mapsample;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.example.datastructures.BusinessMarker;
import com.example.dbhandling.DBHandler;

public class BusinessOpeningScreenActivity extends Activity{
	//this value should be given as an input
	private long myBusinessId = 0;
	
	private BusinessMarker myBusiness;
	private DBHandler dbHandler;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		dbHandler = new DBHandler(this);
		//TODO - should be async
		myBusiness = dbHandler.getBusinessInfo(myBusinessId);
		
		setContentView(R.layout.business_opening_screen);
		FragmentManager fragmentManager = getFragmentManager();
		FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
		BusinessDashboardFragment dashboardFragment = new BusinessDashboardFragment();
		fragmentTransaction.add(R.id.business_fragment_layout, dashboardFragment);
		fragmentTransaction.commit();
		
			
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		if(dbHandler!=null){
			dbHandler.close();
			dbHandler = null;
		}
	}
}
