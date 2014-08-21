package com.example.mapsample;




import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.datastructures.BusinessMarker;
import com.example.dbhandling.DBHandler;

public class BusinessOpeningScreenActivity extends FragmentActivity{
	//this value should be given as an input
	public long myBusinessId = 0;

	public BusinessMarker myBusiness;
	private DBHandler dbHandler;

	private ImageView homeFragmentBtn;

	private ImageView mapFragmentBtn;

	private ImageView statsFragmentBtn;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		dbHandler = new DBHandler(this);
		//TODO - should be async
		myBusiness = dbHandler.getBusinessInfo(myBusinessId);

		setContentView(R.layout.business_opening_screen);
		FragmentManager fragmentManager = getSupportFragmentManager();
		FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
		BusinessDashboardFragment dashboardFragment = new BusinessDashboardFragment();
		fragmentTransaction.add(R.id.business_fragment_layout, dashboardFragment);
		fragmentTransaction.commit();

		RatingBar ratingBar = (RatingBar)findViewById(R.id.businessRatingBar);
		ratingBar.setRating(1);
		/**overrides rating bar's on touch method so it won't change anything*/
		ratingBar.setOnTouchListener(new OnTouchListener() {
			public boolean onTouch(View v, MotionEvent event) {
				return true;
			}
		});

		TextView businessNameTv = (TextView)findViewById(R.id.businessTitle);
		businessNameTv.setText(myBusiness.name);


		homeFragmentBtn = (ImageView)findViewById(R.id.refresh_btn);
		mapFragmentBtn = (ImageView)findViewById(R.id.map_btn);
		statsFragmentBtn = (ImageView)findViewById(R.id.stats_btn);	
		homeFragmentBtn.setOnClickListener(new FragmentBtnOnClickListener());
		mapFragmentBtn.setOnClickListener(new FragmentBtnOnClickListener());
		statsFragmentBtn.setOnClickListener(new FragmentBtnOnClickListener());
	}

	private class FragmentBtnOnClickListener implements OnClickListener{

		@Override
		public void onClick(View v) {
			FragmentManager fragmentManager = getSupportFragmentManager();
			FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
			Fragment newFragment = null;

			if(v==homeFragmentBtn){
				newFragment =  new BusinessDashboardFragment();
			}else if(v==mapFragmentBtn){
				newFragment =  new MapWindowFragment();
			}else if(v==statsFragmentBtn){
				newFragment =  new BusinessDashboardFragment(); //TODO - implement this
			}
			
			fragmentTransaction.replace(R.id.business_fragment_layout, newFragment);
			fragmentTransaction.commit();

		}


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
