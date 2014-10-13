package com.dna.radius.businessmode;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTabHost;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.Toast;

import com.dna.radius.R;
import com.dna.radius.infrastructure.BaseActivity;
import com.dna.radius.infrastructure.GeneralSettingsFragment;
import com.dna.radius.infrastructure.LocationFinderFragment;
import com.dna.radius.infrastructure.MyApp;
import com.dna.radius.infrastructure.SupportedTypes.BusinessType;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.android.gms.maps.model.LatLng;

public class BusinessSettingsActivity extends BaseActivity{
	private FragmentTabHost mTabHost;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.business_settings_activity);

		Tracker tracker = ((MyApp) getApplication()).getTracker(MyApp.TrackerName.APP_TRACKER);
		tracker.enableExceptionReporting(true);
		tracker.setScreenName("Business Settings");
		tracker.send(new HitBuilders.AppViewBuilder().build());

		createTabHost();


		Button applyChangesButton = (Button) findViewById(R.id.apply_changes_button);
		applyChangesButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				Fragment currentFragment = getSupportFragmentManager().findFragmentByTag(mTabHost.getCurrentTabTag());

				if(currentFragment.getClass() == GeneralSettingsFragment.class){
					Log.d("BusinessSettingsActivity", "apply button was pressesd, current fragment - general settings");
					handleApplyGeneralSettings();
				}
				else if(currentFragment.getClass() == BusinessFillDetailsFragment.class){
					Log.d("BusinessSettingsActivity", "apply button was pressesd, current fragment - business settings");
					handleApplyBusinessSettings();
				}
				else if(currentFragment.getClass() == LocationFinderFragment.class){
					Log.d("BusinessSettingsActivity", "apply button was pressesd, current fragment - location settings");
					handleLocationSettings();
				}
				else{
					Log.e("BusinessSettingsActivity", "apply button was pressesd, current fragment wasnt recognized");
					return;
				}
			}


		});

	}


	private void createTabHost() {

		mTabHost = (FragmentTabHost)findViewById(android.R.id.tabhost);

		//TODO - this is NOT NEEDED!!!!!
		mTabHost.setOnTabChangedListener(new OnTabChangeListener(){    
			public void onTabChanged(String tabID) {    
				mTabHost.clearFocus(); 
			}   
		});

		createGeneralSettingsTab();
		createBusinessSettingsTab();
		createLocationSettingsTab();


		for (int i = 0; i < mTabHost.getTabWidget().getChildCount(); i++) {
		
			mTabHost.getTabWidget().getChildAt(i).setFocusable(false); 
		}

	}


	private void createGeneralSettingsTab() {

		// Create the tabs in main_fragment.xml
		mTabHost.setup(this, getSupportFragmentManager(), android.R.id.tabcontent);
		Bundle generalSettingFragment = new Bundle();
		generalSettingFragment.putString(GeneralSettingsFragment.USER_NAME_PARAM, BusinessData.getUserName());
		generalSettingFragment.putString(GeneralSettingsFragment.EMAIL_PARAM, BusinessData.getEmail());
		// Create Tab1 with a custom image in res folder
		mTabHost.addTab(mTabHost.newTabSpec(getResources().getString(R.string.general_settings)).setIndicator(getResources().getString(R.string.general_settings)),
				GeneralSettingsFragment.class, generalSettingFragment);
	}


	private void createBusinessSettingsTab() {

		// Create Tab2
		Bundle fillDataBundle = new Bundle();
		fillDataBundle.putBoolean(BusinessFillDetailsFragment.IS_IN_SETTINGS_MODE_PARAM, true);
		fillDataBundle.putString(BusinessFillDetailsFragment.BUSINESS_NAME_HINT_PARAM, BusinessData.getName());
		fillDataBundle.putString(BusinessFillDetailsFragment.BUSINESS_ADDRESS_HINT_PARAM, BusinessData.getAddress());
		fillDataBundle.putString(BusinessFillDetailsFragment.BUSINESS_PHONE_HINT_PARAM, BusinessData.getPhoneNumber());
		fillDataBundle.putSerializable(BusinessFillDetailsFragment.BUSINESS_TYPE_HINT_PARAM, BusinessData.getType());
		fillDataBundle.putInt(BusinessFillDetailsFragment.TEXT_COLOR_PARAM, Color.BLACK);

		mTabHost.addTab(mTabHost.newTabSpec(getResources().getString(R.string.business_settings)).setIndicator(getResources().getString(R.string.business_settings)),
				BusinessFillDetailsFragment.class, fillDataBundle);
	}


	private void createLocationSettingsTab() {

		Bundle locationBundle = new Bundle();
		locationBundle.putString(LocationFinderFragment.ADDRESS_PARAMETER, "");
		locationBundle.putParcelable(LocationFinderFragment.DEFAULT_LOCATION_PARAMETER, BusinessData.getLocation());
		// Create Tab3
		mTabHost.addTab(mTabHost.newTabSpec(getResources().getString(R.string.Location_settings)).setIndicator(getResources().getString(R.string.Location_settings)),
				LocationFinderFragment.class, locationBundle);
	}



	private void handleLocationSettings() {

		LocationFinderFragment currentFragment = (LocationFinderFragment) getSupportFragmentManager().findFragmentByTag(mTabHost.getCurrentTabTag());

		if(!currentFragment.neededInfoGiven()){
			return;
		}

		LatLng newLatLng = currentFragment.getLocation();
		if(!newLatLng.equals(BusinessData.getLocation())){
			BusinessData.setLocation(newLatLng);

			BusinessOpeningScreenActivity.refreshNeeded = true;
			Toast.makeText(this, getResources().getString(R.string.data_changed_successfully), Toast.LENGTH_SHORT).show();
			finish();
		}
	}



	private void handleApplyBusinessSettings() {

		BusinessFillDetailsFragment currentFragment = (BusinessFillDetailsFragment) getSupportFragmentManager().findFragmentByTag(mTabHost.getCurrentTabTag());


		boolean didDataChanged = false;
		String newBusinessName = currentFragment.getBusinessName();
		if(!newBusinessName.equals("") && !newBusinessName.equals(BusinessData.getName())){
			didDataChanged = true;
			BusinessData.setName(newBusinessName);
		}

		BusinessType newBusinessType = currentFragment.getBusinessType();
		if(newBusinessType != BusinessData.getType()){
			didDataChanged = true;
			BusinessData.setType(newBusinessType);
		}

		String newBusinessAddress = currentFragment.getBusinessAddress();
		if(!newBusinessAddress.equals("") && !newBusinessAddress.equals(BusinessData.getAddress())){
			didDataChanged = true;
			BusinessData.setAddress(newBusinessAddress);
		}

		String newPhoneNumber = currentFragment.getBusinessPhoneNumber();
		if(!newPhoneNumber.equals("") && !newPhoneNumber.equals(BusinessData.getPhoneNumber())){
			didDataChanged = true;
			BusinessData.setPhoneNumber(newPhoneNumber);
		}

		if(didDataChanged){
			BusinessOpeningScreenActivity.refreshNeeded = true;
			Toast.makeText(this, getResources().getString(R.string.data_changed_successfully), Toast.LENGTH_SHORT).show();
			finish();
		}
	}


	private void handleApplyGeneralSettings(){
		boolean didDataChanged = false;
		GeneralSettingsFragment currentFragment = (GeneralSettingsFragment) getSupportFragmentManager().findFragmentByTag(mTabHost.getCurrentTabTag());

		//tests if the users changed the user name and changes it accordingly
		String newUserName = currentFragment.getUserName();
		if(!newUserName.equals("") && !newUserName.equals(BusinessData.getUserName())){
			didDataChanged = true;
			BusinessData.setUserName(newUserName);
		}

		//tests if the users changed the email and changes it accordingly
		String newEmail = currentFragment.getEmail();
		if(!newEmail.equals("") && !newEmail.equals(BusinessData.getEmail())){
			didDataChanged = true;
			BusinessData.setEmail(newEmail);
		}

		//tests if the users changed the password and changes it accordingly
		String newPassword = currentFragment.getPassword();
		String newPasswordConformation = currentFragment.getConformationPassword();
		if(!newPassword.equals("")){
			if(!newPassword.equals(newPasswordConformation)){
				createAlertDialog(getResources().getString(R.string.passwords_mismatch));
			}else{
				BusinessData.setPassword(newPassword);
				didDataChanged = true;
			}
		}

		if(didDataChanged){
			BusinessOpeningScreenActivity.refreshNeeded = true;
			createAlertDialog(getResources().getString(R.string.data_changed_successfully));
			finish();
		}
	}



	/*
	 * apparantly, there is an android bug whenever one of the fragments
	 * within a tab host contains several edit text.
	 * the problem is that the edit text can't receive focus in this case.
	 * after a research, I found out that the most suitable solution is that each
	 * edit text will have an on touch listener which requests for focus.
	 * No farther investigation is needed.
	 * @author dror
	 *
	 */
	public static class EditTextOnTouchListenerWithinTabhost implements OnTouchListener{
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			v.requestFocusFromTouch();
			return false;
		}


	}


}
