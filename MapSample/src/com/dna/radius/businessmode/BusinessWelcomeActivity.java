package com.dna.radius.businessmode;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ProgressBar;

import com.dna.radius.R;
import com.dna.radius.datastructures.Deal;
import com.dna.radius.datastructures.DealHistoryManager;
import com.dna.radius.dbhandling.ParseClassesNames;
import com.dna.radius.infrastructure.BaseActivity;
import com.dna.radius.infrastructure.LocationFinderFragment;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseUser;

public class BusinessWelcomeActivity extends BaseActivity {

	//private Bitmap businessBitmap;



	/**
	 * This integer and constants were meant to tell which fragment should be 
	 * loaded whenever the next button is pressed.*/
	private int fragmentCount = 0;
	private final int ON_FILL_DETAILS_FRAGMENT = 0;
	private final int ON_LOCATION_FRAGMENT = 1;

	private Button progressButton;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.business_welcome_dialog_activity);
		
		setScreenSize();

		setProgressBtn();

		//starts the fill details fragment
		final FragmentManager fragmentManager = getSupportFragmentManager();
		FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
		BusinessFillDetailsFragment businessWelcomeFragment = new BusinessFillDetailsFragment();
		
		Bundle fillDataBundle = new Bundle();
		fillDataBundle.putBoolean(BusinessFillDetailsFragment.IS_IN_SETTINGS_MODE_PARAM, false);
		businessWelcomeFragment.setArguments(fillDataBundle);
		fragmentTransaction.add(R.id.business_welcome_main_fragment_layout, businessWelcomeFragment);
		fragmentTransaction.commit();

	}


	@Override
	public void onBackPressed() {
		//if the user is in the fill detail screen - do nothing
		if (fragmentCount <= ON_FILL_DETAILS_FRAGMENT) {
			fragmentCount = 0;
			return;
		}

		//if not - set the button to be a progress button
		super.onBackPressed();
		progressButton.setCompoundDrawablesWithIntrinsicBounds(null, null,  getResources().getDrawable(R.drawable.ic_action_next_item),  null);
		progressButton.setText(getResources().getString(R.string.next_button).toUpperCase()); //TODO CHECK TO UPPER CASE?

		--fragmentCount;
	}


	private void setProgressBtn(){

		progressButton = (Button)findViewById(R.id.progress_btn);
		progressButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				final FragmentManager fragmentManager = getSupportFragmentManager();
				
				if (fragmentCount == ON_FILL_DETAILS_FRAGMENT) {
					
					BusinessFillDetailsFragment currentFragment = (BusinessFillDetailsFragment)fragmentManager.findFragmentById(R.id.business_welcome_main_fragment_layout);
					if( !currentFragment.neededInfoGiven()) return;

					//Retrieves the business data from fragment
					BusinessData.businessName = currentFragment.getBusinessName();
					BusinessData.businessType = currentFragment.getBusinessType();
					BusinessData.businessAddress = currentFragment.getBusinessAddress();
					BusinessData.businessPhoneNumber = currentFragment.getBusinessPhoneNumber();

					Fragment nextFragment = new LocationFinderFragment();
					Bundle bdl = new Bundle();
					bdl.putString(LocationFinderFragment.ADDRESS_PARAMETER, BusinessData.businessAddress);
					nextFragment.setArguments(bdl);
					
					fragmentCount++;

					//moves to the next fragment
					FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
					fragmentTransaction.replace(R.id.business_welcome_main_fragment_layout, nextFragment);
					fragmentTransaction.addToBackStack(null);
					fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_ENTER_MASK);
					fragmentTransaction.commit();

					//changes the next Button to finish button
					progressButton.setCompoundDrawablesWithIntrinsicBounds(null, null,  getResources().getDrawable(R.drawable.ic_action_done),  null);
					progressButton.setText(getResources().getString(R.string.finish_btn).toUpperCase());

				}else if (fragmentCount == ON_LOCATION_FRAGMENT) {
					LocationFinderFragment currentFragment = (LocationFinderFragment)fragmentManager.findFragmentById(R.id.business_welcome_main_fragment_layout);
					if (!currentFragment.neededInfoGiven()) return;

					BusinessData.businessLocation = currentFragment.getLocation();

					progressButton.setVisibility(View.INVISIBLE);
					ProgressBar progressBar = (ProgressBar)findViewById(R.id.registration_progress_bar);
					progressBar.setVisibility(View.VISIBLE);
					new AsyncTask<Void, Void, Void>() {

						@Override
						protected Void doInBackground(Void... params) {
							finishRegistration();
							return null;
						}
						protected void onPostExecute(Void result) {
							finish();  // activity
						}
					}.execute();
						

				}else{
					Log.e("BusinessWelcomeActivity", "error with the next button. numberOfTimesNextWasPressed:" + fragmentCount);
				}
			
			}
		});
			

	}

	private void setScreenSize() {
		// This will set this dialog-themed activity to take 80% of the screen
		DisplayMetrics metrics = getResources().getDisplayMetrics();
		int screenWidth = (int) (metrics.widthPixels * 0.9);
		int screenHeight = (int) (metrics.heightPixels * 0.8);
		getWindow().setLayout(screenWidth, screenHeight);
	}



	private void finishRegistration() { 

		ParseObject newBusiness = new ParseObject(ParseClassesNames.BUSINESS_CLASS);

		// store raw-data
		newBusiness.put(ParseClassesNames.BUSINESS_NAME, BusinessData.businessName);
		newBusiness.put(ParseClassesNames.BUSINESS_TYPE, BusinessData.businessType.getStringRep());
		newBusiness.put(ParseClassesNames.BUSINESS_ADDRESS, BusinessData.businessAddress);
		newBusiness.put(ParseClassesNames.BUSINESS_PHONE, BusinessData.businessPhoneNumber);
		
		BusinessData.businessRating = BusinessData.DEFAULT_RATING;
		newBusiness.put(ParseClassesNames.BUSINESS_RATING, BusinessData.DEFAULT_RATING);

		// store location on parse
		ParseGeoPoint location = new ParseGeoPoint(BusinessData.businessLocation.latitude, BusinessData.businessLocation.longitude);
		newBusiness.put(ParseClassesNames.BUSINESS_LOCATION, location);
		

		// store preferences (favorites, likes & dislikes)
		JSONObject prefs = new JSONObject();
		try {
			prefs.put(ParseClassesNames.BUSINESS_PREFERRING_FAVORITES, new JSONArray());

		} catch (JSONException e) {

			Log.e("BusinessWelcome - JSON_CREATION", e.getMessage());
		}
		newBusiness.put(ParseClassesNames.BUSINESS_PREFERRING, prefs);
		
		
		BusinessData.currentDeal = null;
		newBusiness.put(ParseClassesNames.BUSINESS_CURRENT_DEAL, new JSONObject());

		
		//build deal-history column:
		BusinessData.dealsHistory = new DealHistoryManager(0, 0, 0, new ArrayList<Deal>());
		JSONObject dealsHistory = new JSONObject();
		try {
			dealsHistory.put(ParseClassesNames.BUSINESS_HISTORY_TOTAL_LIKES, 0);
			dealsHistory.put(ParseClassesNames.BUSINESS_HISTORY_TOTAL_DISLIKES, 0);
			dealsHistory.put(ParseClassesNames.BUSINESS_HISTORY_TOTAL_NUM_OF_DEALS, 0);
			dealsHistory.put(ParseClassesNames.BUSINESS_HISTORY_DEALS, new JSONArray());

		} catch (JSONException e) {

			Log.e("Business - deal history create", e.getMessage());
		}
		newBusiness.put(ParseClassesNames.BUSINESS_HISTORY, dealsHistory);


		// add a pointer in user to business. i.e. user->businessData
		BusinessData.currentUser = ParseUser.getCurrentUser();
		BusinessData.currentUser.put(ParseClassesNames.BUSINESS_INFO, newBusiness);


		// sync online
		try {

			newBusiness.save();
			BusinessData.currentUser.save();
			BusinessData.businessInfo = newBusiness;

			BusinessData.currentUser.fetchIfNeeded();
			BusinessData.businessInfo.fetchIfNeeded();
			
			BusinessOpeningScreenActivity.refreshNeeded = true;

		} catch (ParseException e) {
			Log.e("Welcome - business", e.getMessage());
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		return true;
	}

}
