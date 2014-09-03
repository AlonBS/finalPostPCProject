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
import com.dna.radius.mapsample.MapWindowFragment;
import com.dna.radius.mapsample.WaitingFragment;



public class BusinessOpeningScreenActivity extends BaseActivity{
	//TODO this value should be given as an input
	public int userID = 0;

	/**buttons which allow switching between fragments*/
	private ImageView homeFragmentBtn;
	private ImageView mapFragmentBtn;
	private ImageView businessHistoryFragment;

	/**holds the lates button which was pressed*/
	private ImageView latestPressedBtn;


	public OwnerData ownerData;
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

				String tempBusinessId = ""; //TODO how to we get the business id?
				ownerData = new OwnerData(tempBusinessId,getApplicationContext());
				ClientData.loadClientInfo();
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						//loads the business dashboard fragment
						FragmentManager fragmentManager = getSupportFragmentManager();
						FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
						BusinessDashboardFragment dashboardFragment = new BusinessDashboardFragment();
						fragmentTransaction.replace(R.id.business_fragment_layout, dashboardFragment);
						fragmentTransaction.commit();

						//sets the business name and rating
						TextView businessNameTv = (TextView)findViewById(R.id.businessTitle);
						businessNameTv.setText(ownerData.name);
						RatingBar ratingBar = (RatingBar)findViewById(R.id.businessRatingBar);
						ratingBar.setRating(ownerData.rating);
						/**overrides rating bar's on touch method so it won't change anything*/
						ratingBar.setOnTouchListener(new OnTouchListener() {
							public boolean onTouch(View v, MotionEvent event) {
								return true;
							}
						});

						//add listeners to fragment buttons
						homeFragmentBtn = (ImageView)findViewById(R.id.refresh_btn);
						mapFragmentBtn = (ImageView)findViewById(R.id.map_btn);
						businessHistoryFragment = (ImageView)findViewById(R.id.stats_btn);	
						homeFragmentBtn.setOnClickListener(new FragmentBtnOnClickListener());
						mapFragmentBtn.setOnClickListener(new FragmentBtnOnClickListener());
						businessHistoryFragment.setOnClickListener(new FragmentBtnOnClickListener());

						latestPressedBtn = homeFragmentBtn;

						displayWelcomeIfNeeded();
					}

					private void displayWelcomeIfNeeded() {

						if (getIntent().getExtras() == null) { // if the bundle returned by getExtras() is 
																// not null, then it is not first time
 
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
	private class FragmentBtnOnClickListener implements OnClickListener{

		@Override
		public void onClick(View clickedBtn) {
			Fragment newFragment = null;

			if(clickedBtn==homeFragmentBtn){
				newFragment =  new BusinessDashboardFragment();
			}else if(clickedBtn==mapFragmentBtn){
				newFragment =  new MapWindowFragment();
			}else if(clickedBtn==businessHistoryFragment){
				newFragment =  new BusinessHistoryFragment();
			}
			if((latestPressedBtn == clickedBtn) && (clickedBtn!=homeFragmentBtn)){
				return;
			}
			latestPressedBtn = (ImageView) clickedBtn;
			FragmentManager fragmentManager = getSupportFragmentManager();
			FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

			fragmentTransaction.replace(R.id.business_fragment_layout, newFragment);
			fragmentTransaction.commit();

		}


	}


}
