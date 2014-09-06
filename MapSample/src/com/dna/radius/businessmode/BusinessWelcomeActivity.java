package com.dna.radius.businessmode;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.dna.radius.R;
import com.dna.radius.clientmode.ClientData;
import com.dna.radius.dbhandling.ParseClassesNames;
import com.dna.radius.infrastructure.LocationFinderFragment;
import com.dna.radius.login.MainActivity;
import com.google.android.gms.maps.model.LatLng;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;

public class BusinessWelcomeActivity extends FragmentActivity {

	private Bitmap businessBitmap;



	/**
	 * This integer and constants were meant to tell which fragment should be 
	 * loaded whenever the next button is pressed.*/
	private int numberOfTimesNextWasPressed = 0;
	private final int FIND_LOCATION_FRAGEMENT_IS_NEXT = 0;
	private final int GET_IMAGE_FRAGEMENT_TURN_IS_NEXT = 1;
	private final int FINISH_FILLING_DETAILS_IS_NEXT= 2;

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
		fragmentTransaction.add(R.id.business_welcome_main_fragment_layout, businessWelcomeFragment);
		fragmentTransaction.commit();

	}


	@Override
	public void onBackPressed() {
		//if the user is in the fill detail screen - do nothing
		--numberOfTimesNextWasPressed;
		if (numberOfTimesNextWasPressed < FIND_LOCATION_FRAGEMENT_IS_NEXT) {
			numberOfTimesNextWasPressed = 0;
			return;
		}

		//if not - set the button to be a progress button
		super.onBackPressed();
		progressButton.setCompoundDrawablesWithIntrinsicBounds(null, null,  getResources().getDrawable(R.drawable.ic_action_next_item),  null);
		progressButton.setText(getResources().getString(R.string.next_button).toUpperCase()); //TODO CHECK TO UPPER CASE?

	}


	private void setProgressBtn(){

		progressButton = (Button)findViewById(R.id.progress_btn);
		progressButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				if (numberOfTimesNextWasPressed == FIND_LOCATION_FRAGEMENT_IS_NEXT || 
						numberOfTimesNextWasPressed == GET_IMAGE_FRAGEMENT_TURN_IS_NEXT) {

					final FragmentManager fragmentManager = getSupportFragmentManager();
					Fragment nextFragment = null;

					if (numberOfTimesNextWasPressed == FIND_LOCATION_FRAGEMENT_IS_NEXT) {

						//tests if it's possible to move to the next fragment
						BusinessFillDetailsFragment currentFragment = (BusinessFillDetailsFragment)fragmentManager.findFragmentById(R.id.business_welcome_main_fragment_layout);
						if( !currentFragment.neededInfoGiven()) return;

						//Retrieves the business data from fragment
						BusinessData.businessName = currentFragment.getBusinessName();
						BusinessData.businessType = currentFragment.getBusinessType();
						BusinessData.businessAddress = currentFragment.getBusinessAddress();
						BusinessData.businessPhoneNumber = currentFragment.getBusinessPhoneNumber();

						nextFragment = new LocationFinderFragment(BusinessData.businessAddress);


					}else if (numberOfTimesNextWasPressed == GET_IMAGE_FRAGEMENT_TURN_IS_NEXT) {

						//gets the chosen location from the fragment
						LocationFinderFragment currentFragment = (LocationFinderFragment)fragmentManager.findFragmentById(R.id.business_welcome_main_fragment_layout);
						if (!currentFragment.neededInfoGiven()) return;

						BusinessData.businessLocation = currentFragment.getLocation();
						nextFragment = new BusinessChooseImageFragment();

						//changes the next Button to finish button
						progressButton.setCompoundDrawablesWithIntrinsicBounds(null, null,  getResources().getDrawable(R.drawable.ic_action_done),  null);
						progressButton.setText(getResources().getString(R.string.finish_btn).toUpperCase());
					}

					numberOfTimesNextWasPressed++;

					//moves to the next fragment
					FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
					fragmentTransaction.replace(R.id.business_welcome_main_fragment_layout, nextFragment);
					fragmentTransaction.addToBackStack(null);
					fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_ENTER_MASK);
					fragmentTransaction.commit();

				}else if (numberOfTimesNextWasPressed == FINISH_FILLING_DETAILS_IS_NEXT) {

					finishRegistration();
					finish(); // activity

				}else{

					Log.e("BusinessWelcomeActivity", "error with the next button. numberOfTimesNextWasPressed:" + numberOfTimesNextWasPressed);
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

		newBusiness.put(ParseClassesNames.BUSINESS_NAME, BusinessData.businessName);
		newBusiness.put(ParseClassesNames.BUSINESS_TYPE, BusinessData.businessType.getStringRep());
		newBusiness.put(ParseClassesNames.BUSINESS_ADDRESS, BusinessData.businessAddress);
		newBusiness.put(ParseClassesNames.BUSINESS_PHONE, BusinessData.businessPhoneNumber);

		// store location on parse
		JSONObject coordinates = new JSONObject();
		try {
			coordinates.put(ParseClassesNames.BUSINESS_LOCATION_LAT ,BusinessData.businessLocation.latitude);
			coordinates.put(ParseClassesNames.BUSINESS_LOCATION_LONG ,BusinessData.businessLocation.longitude);

		} catch (JSONException e) {

			Log.e("Business - location create", e.getMessage());
		}
		newBusiness.put(ParseClassesNames.BUSINESS_LOCATION, coordinates);

		BusinessData.businessRating = BusinessData.DEFAULT_RATING;
		newBusiness.put(ParseClassesNames.BUSINESS_RATING, BusinessData.DEFAULT_RATING);

		//TODO add picture to parse - using parseFile
//		newBusiness.put(ParseClassesNames.BUSINESS_PICTURE, "yosi");




		BusinessData.currentDeal = null;
		newBusiness.put(ParseClassesNames.BUSINESS_CURRENT_DEAL, new JSONObject());

		//build deal-history column:
		JSONObject dealsHistory = new JSONObject();
		try {
			dealsHistory.put(ParseClassesNames.BUSINESS_HISTORY_TOTAL_LIKES, 0);
			dealsHistory.put(ParseClassesNames.BUSINESS_HISTORY_TOTAL_DISLIKES, 0);
			dealsHistory.put(ParseClassesNames.BUSINESS_HISTORY_TOTAL_NUM_OF_DEALS, 0);
			dealsHistory.put(ParseClassesNames.BUSINESS_HISTORY_DEALS, new JSONArray());

		} catch (JSONException e) {

			Log.e("Business - deal history create", e.getMessage());
		}
		newBusiness.put(ParseClassesNames.CLIENT_PREFERRING, dealsHistory);






		//		
		//		 "totalLikes" : 13523523,
		//			"totalDisLikes" : 23234234,
		//			"totalNumberOfDeals" : 30,
		//			"deals" : [
		//		        {
		//					"dealId": "234fg##5",
		//					"dealContent": "Kol ha'olam kulu - gesher zar meod"
		//					"numberOfLikes": 141
		//					"numberOfDislikes": 234
		//		            "date": "12/14/1988"
		//		        },
		//		        {
		//		            "business_Id": "234fg"
		//		        }
		//		    ],
		//		









		// add a pointer in user to business. i.e. user->businessData
		ParseUser currentUser = ParseUser.getCurrentUser();
		currentUser.put(ParseClassesNames.BUSINESS_INFO, newBusiness);


		// sync online
		try {

			newBusiness.save();
			BusinessData.currentUser.save();
			BusinessData.businessInfo = newBusiness;

			BusinessData.currentUser.fetchIfNeeded();
			BusinessData.businessInfo.fetchIfNeeded();

		} catch (ParseException e) {
			Log.e("Welcome - business", e.getMessage());
		}
	}


}
