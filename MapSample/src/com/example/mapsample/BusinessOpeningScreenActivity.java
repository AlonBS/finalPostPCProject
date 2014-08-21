package com.example.mapsample;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.datastructures.BusinessMarker;
import com.example.dbhandling.DBHandler;

public class BusinessOpeningScreenActivity extends Activity{
	//this value should be given as an input
	public long myBusinessId = 0;
	
	public BusinessMarker myBusiness;
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
		
		/*RatingBar ratingBar = (RatingBar)findViewById(R.id.businessRatingBar);
		//ratingBar.setRating(1);
		/**overrides rating bar's on touch method so it won't change anything*/
//		ratingBar.setOnTouchListener(new OnTouchListener() {
//	        public boolean onTouch(View v, MotionEvent event) {
//	            return true;
//	        }
//	    });
		
		TextView businessNameTv = (TextView)findViewById(R.id.businessTitle);
		businessNameTv.setText(myBusiness.name);
			
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
