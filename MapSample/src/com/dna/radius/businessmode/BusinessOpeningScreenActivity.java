package com.dna.radius.businessmode;




import android.content.Intent;
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

import com.dna.radius.R;
import com.dna.radius.clientmode.ClientData;
import com.dna.radius.infrastructure.BaseActivity;
import com.dna.radius.infrastructure.WaitingFragment;
import com.dna.radius.login.MainActivity;
import com.dna.radius.mapsample.MapWindowFragment;



public class BusinessOpeningScreenActivity extends BaseActivity{

	/**buttons which allow switching between fragments*/
	private ImageView homeFragmentBtn;
	private ImageView mapFragmentBtn;
	private ImageView businessHistoryFragment;

	/**holds the lates button which was pressed*/
	private ImageView latestPressedBtn;


	public BusinessData ownerData;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.business_opening_screen);

		//Sets the waiting fragment.
		FragmentManager fragmentManager = getSupportFragmentManager();
		Fragment waitingFragment = new WaitingFragment();
		FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
		fragmentTransaction.replace(R.id.business_fragment_layout, waitingFragment);
		fragmentTransaction.commit();

		//a thread which loads the ClientData and OwnerData.
		Thread t = new Thread(new Runnable() {
			@Override
			public void run() {

				//String tempBusinessId = ""; //TODO how to we get the business id?
				//ownerData = new BusinessData(tempBusinessId,getApplicationContext());
				
				BusinessData.loadBusinessInfo();
				
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						
						loadDashBoard();
						
						loadNameAndRating();
						
						setOnClickListeners();

						displayWelcomeIfNeeded();
					}
					
					
					private void loadDashBoard() {
						
						final FragmentManager fragmentManager = getSupportFragmentManager();
						FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
						BusinessDashboardFragment dashboardFragment = new BusinessDashboardFragment();
						fragmentTransaction.replace(R.id.business_fragment_layout, dashboardFragment);
						fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
						fragmentTransaction.commit();
					}
					
					private void loadNameAndRating() {
						
						TextView businessNameTv = (TextView) findViewById(R.id.businessTitle);
						businessNameTv.setText(BusinessData.businessName);
						
						RatingBar ratingBar = (RatingBar) findViewById(R.id.businessRatingBar);
						ratingBar.setRating((float)BusinessData.businessRating);
						
						// overrides rating bar's on touch method so it won't change anything
						ratingBar.setOnTouchListener(new OnTouchListener() {
							public boolean onTouch(View v, MotionEvent event) { return true; }
						});
					}
					
					
					private void setOnClickListeners() {
						
						homeFragmentBtn = (ImageView)findViewById(R.id.refresh_btn);
						mapFragmentBtn = (ImageView)findViewById(R.id.map_btn);
						businessHistoryFragment = (ImageView)findViewById(R.id.stats_btn);
						
						FragmentChangerBtnOnClickListener f = new FragmentChangerBtnOnClickListener();
						
						homeFragmentBtn.setOnClickListener(f);
						mapFragmentBtn.setOnClickListener(f);
						businessHistoryFragment.setOnClickListener(f);

						latestPressedBtn = homeFragmentBtn;
					}
					

					private void displayWelcomeIfNeeded() {

						if (BusinessData.businessInfo == null) { 
 
							Intent intent = new Intent(getApplicationContext(), BusinessWelcomeActivity.class);
							startActivity(intent);
						}	
					}
				});
			}
		});
		t.start();

	}

	/***
	 * a listener for the fragment buttons. allows switching between the fragments,
	 * according to the button which was pressed.
	 *
	 */
	private class FragmentChangerBtnOnClickListener implements OnClickListener{

		@Override
		public void onClick(View clickedBtn) {
			
			//TODO add refresh button?
			if((latestPressedBtn == clickedBtn) && (clickedBtn!=homeFragmentBtn)) { return; }
			
			
			Fragment newFragment = null;
			if (clickedBtn == homeFragmentBtn) {
				
				newFragment =  new BusinessDashboardFragment();
				
			}else if (clickedBtn == mapFragmentBtn){
				
				newFragment =  new MapWindowFragment();
				
			}else if( clickedBtn == businessHistoryFragment){
				
				newFragment =  new BusinessHistoryFragment();
				
			}
			
			latestPressedBtn = (ImageView) clickedBtn;
			
			FragmentManager fragmentManager = getSupportFragmentManager();
			FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
			fragmentTransaction.replace(R.id.business_fragment_layout, newFragment);
			fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
			fragmentTransaction.commit();

		}


	}


}
