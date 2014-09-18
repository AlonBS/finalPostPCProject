package com.dna.radius.businessmode;




import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.dna.radius.R;
import com.dna.radius.infrastructure.BaseActivity;
import com.dna.radius.infrastructure.WaitingFragment;
import com.dna.radius.mapsample.MapWindowFragment;



public class BusinessOpeningScreenActivity extends BaseActivity{

	private TextView businessNameTextView;
	private RatingBar businessRatingBar;
	
	
	/**buttons which allow switching between fragments*/
	private ImageView homeFragmentBtn;
	private ImageView mapFragmentBtn;
	private ImageView businessHistoryFragment;

	/**holds the lates button which was pressed*/
	private ImageView latestPressedBtn;

	static boolean refreshNeeded = false;




	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.business_opening_screen);
		
		isInBusinessMode = true;

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

				Log.e("as", "asd"); //TODO REMOVE
				BusinessData.loadBusinessInfo();

				runOnUiThread(new Runnable() {
					private View logoLayout;


					@Override
					public void run() {

						loadNameAndRating();

						setOnClickListeners();

						loadDashBoard();
						
						displayWelcomeIfNeeded();
					}


					private void loadDashBoard() {

						final FragmentManager fragmentManager = getSupportFragmentManager();
						FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
						BusinessDashboardFragment dashboardFragment = new BusinessDashboardFragment();
						fragmentTransaction.replace(R.id.business_fragment_layout, dashboardFragment);
						fragmentTransaction.addToBackStack(null);
						fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
						fragmentTransaction.commit();
					}



					private void setOnClickListeners() {
						logoLayout = (View)findViewById(R.id.logo_layout);
						homeFragmentBtn = (ImageView)findViewById(R.id.home_btn);
						mapFragmentBtn = (ImageView)findViewById(R.id.map_btn);
						businessHistoryFragment = (ImageView)findViewById(R.id.stats_btn);

						FragmentChangerBtnOnClickListener f = new FragmentChangerBtnOnClickListener();

						logoLayout.setOnClickListener(new OnClickListener() {
							
							@Override
							public void onClick(View arg0) {
								refresh();
							}
						});
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
			fragmentTransaction.addToBackStack(null);
			fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
			fragmentTransaction.commit();

		}
	}


	private void loadNameAndRating() {

		businessNameTextView = (TextView) findViewById(R.id.businessTitle);
		businessRatingBar = (RatingBar) findViewById(R.id.businessRatingBar);

		businessNameTextView.setText(BusinessData.businessName);
		businessRatingBar.setRating((float)BusinessData.businessRating);
		

//TODO not needed
//		// overrides rating bar's on touch method so it won't change anything
//		businessRatingBar.setOnTouchListener(new OnTouchListener() {
//			public boolean onTouch(View v, MotionEvent event) { return true; }
//		});
	}

	@Override
	protected void onResumeFragments() {
		super.onResumeFragments();
		if (refreshNeeded){
			refresh();
		}
	}


	private void refresh() {

		//Sets the waiting fragment.
		final FragmentManager fragmentManager = getSupportFragmentManager();
		Fragment waitingFragment = new WaitingFragment();
		FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
		fragmentTransaction.replace(R.id.business_fragment_layout, waitingFragment);
		fragmentTransaction.commit();
		refreshNeeded = false;

		new AsyncTask<Void, Void, Void>(){

			@Override
			protected Void doInBackground(Void... params) {
				BusinessData.refreshDB();
				return null;
			}

			protected void onPostExecute(Void result) {
				loadNameAndRating();

				//starting current fragment once again
				latestPressedBtn = homeFragmentBtn;
				FragmentManager fragmentManager = getSupportFragmentManager();
				Fragment currentFragment = new BusinessDashboardFragment();
				FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
				fragmentTransaction.replace(R.id.business_fragment_layout, currentFragment);
				fragmentTransaction.commit();
			}
		}.execute();

	}









}
