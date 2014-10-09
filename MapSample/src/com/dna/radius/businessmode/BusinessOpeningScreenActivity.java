package com.dna.radius.businessmode;




import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.hardware.SensorManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.OrientationEventListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.dna.radius.R;
import com.dna.radius.infrastructure.BaseActivity;
import com.dna.radius.infrastructure.MyApp;
import com.dna.radius.infrastructure.WaitingFragment;
import com.dna.radius.infrastructure.MyApp.TrackerName;
import com.dna.radius.mapsample.MapWindowFragment;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;



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

	private OrientationListener orientationListener;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.business_opening_screen);

		((MyApp) getApplication()).getTracker(MyApp.TrackerName.APP_TRACKER);

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

		forcePortraitOrientation();
		orientationListener = new OrientationListener(this, SensorManager.SENSOR_DELAY_NORMAL);
		if (orientationListener.canDetectOrientation()){
			Log.d("BusinessOpeningScreenActivity","orientationListener Can DetectOrientation");
			orientationListener.disable();
		}
		else{
			Log.d("BusinessOpeningScreenActivity","orientationListener Can't DetectOrientation");
		}


	}

	/***
	 * a listener for the fragment buttons. allows switching between the fragments,
	 * according to the button which was pressed.
	 *
	 */
	private class FragmentChangerBtnOnClickListener implements OnClickListener{

		@Override
		public void onClick(View clickedBtn) {

			if((latestPressedBtn == clickedBtn) && (clickedBtn!=homeFragmentBtn)) { return; }

			String newFragmentName="";
			Fragment newFragment = null;
			if (clickedBtn == homeFragmentBtn) {
				newFragmentName = "home fragment";
				newFragment =  new BusinessDashboardFragment();


			}else if (clickedBtn == mapFragmentBtn){
				newFragmentName = "map fragment";
				newFragment =  new MapWindowFragment();

			}else if( clickedBtn == businessHistoryFragment){
				newFragmentName = "business fragment";
				newFragment =  new BusinessHistoryFragment();

			}

			latestPressedBtn = (ImageView) clickedBtn;

			FragmentManager fragmentManager = getSupportFragmentManager();
			FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
			fragmentTransaction.replace(R.id.business_fragment_layout, newFragment);
			fragmentTransaction.addToBackStack(null);
			fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
			fragmentTransaction.commit();


			// sends event that the switched fragment
			Tracker t = ((MyApp) getApplication()).getTracker(
					TrackerName.APP_TRACKER);
			// Build and send an Event.
			t.send(new HitBuilders.EventBuilder()
			.setCategory("no catagory")
			.setAction("fragment switch, to: " + newFragmentName)
			.setLabel("fragment switch to: " + newFragmentName)
			.build());


		}
	}


	private void loadNameAndRating() {

		businessNameTextView = (TextView) findViewById(R.id.businessTitle);
		businessRatingBar = (RatingBar) findViewById(R.id.businessRatingBar);

		businessNameTextView.setText(BusinessData.businessName);
		businessRatingBar.setRating((float)BusinessData.businessRating);

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



	public void enableOrientationChange(){
		Log.d("Business Opening Screen Activity", "orientation changed was enabled");
		orientationListener.enable();
	}
	public void disableOrientationChange(){
		Log.d("Business Opening Screen Activity", "orientation changed was disabled");
		orientationListener.disable();
		forcePortraitOrientation();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
	}


	/***
	 * forces the screen to be in lanscapr rotation mode
	 */
	protected void forcePortraitOrientation() {
		int current = getRequestedOrientation();
		if ( current != ActivityInfo.SCREEN_ORIENTATION_PORTRAIT ) {
			setRequestedOrientation( ActivityInfo.SCREEN_ORIENTATION_PORTRAIT );
		}
	}



	/***
	 * allows the screen to rotate whenever thae OrientationListener is enabled
	 */
	static class OrientationListener extends OrientationEventListener{
		FragmentActivity context;
		private int THRESHOLD = 20;
		int  currentOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
		public OrientationListener(Context context, int rate) {
			super(context, rate);
			this.context = (FragmentActivity) context;
		}


		@Override
		public void onOrientationChanged(int orientation) {
			int currentScreenOrientation = context.getResources().getConfiguration().orientation;

			if(isLandscape(orientation) && currentScreenOrientation!=ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE){
				Log.d("OrientationListener", "changes to landscape");
				context.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);	
			}else if(isPortrait(orientation) && currentScreenOrientation!=ActivityInfo.SCREEN_ORIENTATION_PORTRAIT){
				Log.d("OrientationListener", "changes to portrait");
				context.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);	

			}else{
				Log.d("OrientationListener", "the change was low : " + orientation);
			}
		}

		private boolean isLandscape(int orientation){
			boolean retVal = orientation >= (90 - THRESHOLD) && orientation <= (90 + THRESHOLD);
			retVal |= orientation >= (270 - THRESHOLD) && orientation <= (270 + THRESHOLD);
			return retVal;
		}

		private boolean isPortrait(int orientation){
			boolean retVal = (orientation >= (360 - THRESHOLD) && orientation <= 360) || (orientation >= 0 && orientation <= THRESHOLD);
			retVal |= (orientation >= (180 - THRESHOLD) && orientation <= 180) || (orientation >= 0 && orientation <= THRESHOLD);
			return retVal;
		}

	}

	@Override
	protected void onStart() {
		super.onStart();
		GoogleAnalytics.getInstance(this).reportActivityStart(this);
	}

	@Override
	protected void onStop() {
		super.onStop();
		GoogleAnalytics.getInstance(this).reportActivityStop(this);
	}





}
