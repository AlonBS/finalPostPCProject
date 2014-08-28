package com.dna.radius.businessmode;




import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.dna.radius.clientmode.ClientData;
import com.dna.radius.datastructures.BusinessMarker;
import com.dna.radius.dbhandling.DBHandler;
import com.dna.radius.mapsample.AbstractActivity;
import com.dna.radius.mapsample.MapWindowFragment;
import com.dna.radius.mapsample.WaitingFragment;
import com.example.mapsample.R;

public class BusinessOpeningScreenActivity extends AbstractActivity{
	//TODO this value should be given as an input
	public int myBusinessId = 0;
	//TODO this value should be given as an input
	public int userID = 0;
	
	private DBHandler dbHandler;

	private ImageView homeFragmentBtn;

	private ImageView mapFragmentBtn;

	private ImageView businessHistoryFragment;
	
	private ImageView latestBtn;
	
	public  OwnerData ownerData;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.business_opening_screen);
		
		FragmentManager fragmentManager = getSupportFragmentManager();
		Fragment waitingFragment = new WaitingFragment();
		FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
		fragmentTransaction.replace(R.id.business_fragment_layout, waitingFragment);
		fragmentTransaction.commit();

		Thread t = new Thread(new Runnable() {
			@Override
			public void run() {

				ownerData = new OwnerData(myBusinessId,getApplicationContext());
				ClientData.loadClient(userID);
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						FragmentManager fragmentManager = getSupportFragmentManager();
						FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
						BusinessDashboardFragment dashboardFragment = new BusinessDashboardFragment();
						fragmentTransaction.replace(R.id.business_fragment_layout, dashboardFragment);
						fragmentTransaction.commit();
						
						TextView businessNameTv = (TextView)findViewById(R.id.businessTitle);
						businessNameTv.setText(ownerData.name);
						
						homeFragmentBtn = (ImageView)findViewById(R.id.refresh_btn);
						mapFragmentBtn = (ImageView)findViewById(R.id.map_btn);
						businessHistoryFragment = (ImageView)findViewById(R.id.stats_btn);	
						homeFragmentBtn.setOnClickListener(new FragmentBtnOnClickListener());
						mapFragmentBtn.setOnClickListener(new FragmentBtnOnClickListener());
						businessHistoryFragment.setOnClickListener(new FragmentBtnOnClickListener());
						
						latestBtn = homeFragmentBtn;
						
						RatingBar ratingBar = (RatingBar)findViewById(R.id.businessRatingBar);
						ratingBar.setRating(ownerData.rating);
						/**overrides rating bar's on touch method so it won't change anything*/
						ratingBar.setOnTouchListener(new OnTouchListener() {
							public boolean onTouch(View v, MotionEvent event) {
								return true;
							}
						});
					}
				});
			}
		});
		t.start();
		dbHandler = new DBHandler(this);
		
	}

	private class FragmentBtnOnClickListener implements OnClickListener{

		@Override
		public void onClick(View clickedBtn) {
			Fragment newFragment = null;
			
			if(clickedBtn==homeFragmentBtn){
				newFragment =  new BusinessDashboardFragment();
			}else if(clickedBtn==mapFragmentBtn){
				newFragment =  new MapWindowFragment();
			}else if(clickedBtn==businessHistoryFragment){
				newFragment =  new BusinessHistoryFragment(); //TODO - implement this
			}
			if((latestBtn == clickedBtn) && (clickedBtn!=homeFragmentBtn)){
				return;
			}
			latestBtn = (ImageView) clickedBtn;
			FragmentManager fragmentManager = getSupportFragmentManager();
			FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
			
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
