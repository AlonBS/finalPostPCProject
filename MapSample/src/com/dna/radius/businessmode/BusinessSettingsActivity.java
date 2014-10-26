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
import com.dna.radius.infrastructure.DataRegexs;
import com.dna.radius.infrastructure.GeneralSettingsFragment;
import com.dna.radius.infrastructure.LocationSettingsFragment;
import com.dna.radius.infrastructure.MyApp;
import com.dna.radius.infrastructure.SupportedTypes.BusinessType;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.android.gms.maps.model.LatLng;

public class BusinessSettingsActivity extends BaseActivity{
	
	private FragmentTabHost mTabHost;
	
	private boolean passwordsMisMatch = false;
	
	private boolean hasError;
	private String msg;

	

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.business_settings_activity);

		Tracker tracker = ((MyApp) getApplication()).getTracker(MyApp.TrackerName.APP_TRACKER);
		tracker.enableExceptionReporting(true);
		tracker.setScreenName("Business Settings");
		tracker.send(new HitBuilders.AppViewBuilder().build());

		createTabHost();
		
		setApplyChangesOnClickListener();
	}


	private void createTabHost() {

		mTabHost = (FragmentTabHost) findViewById(android.R.id.tabhost);
		mTabHost.setup(this, getSupportFragmentManager(), android.R.id.tabcontent);

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

		Bundle gsb = new Bundle();
		gsb.putString(GeneralSettingsFragment.USER_NAME_PARAM, BusinessData.getUserName());
		gsb.putString(GeneralSettingsFragment.EMAIL_PARAM, BusinessData.getEmail());
		
		String s = getResources().getString(R.string.general_settings);
		mTabHost.addTab(mTabHost.newTabSpec(s).setIndicator(s), GeneralSettingsFragment.class, gsb);
	}


	private void createBusinessSettingsTab() {

		// Create 2nd tab
		Bundle bib = new Bundle();
		bib.putBoolean(BusinessInfoFragment.IS_IN_SETTINGS_MODE_PARAM, true);
		bib.putString(BusinessInfoFragment.BUSINESS_NAME_HINT_PARAM, BusinessData.getName());
		bib.putString(BusinessInfoFragment.BUSINESS_ADDRESS_HINT_PARAM, BusinessData.getAddress());
		bib.putString(BusinessInfoFragment.BUSINESS_PHONE_HINT_PARAM, BusinessData.getPhoneNumber());
		bib.putSerializable(BusinessInfoFragment.BUSINESS_TYPE_HINT_PARAM, BusinessData.getType());
		bib.putInt(BusinessInfoFragment.TEXT_COLOR_PARAM, Color.BLACK);

		String s = getResources().getString(R.string.business_settings);
		mTabHost.addTab(mTabHost.newTabSpec(s).setIndicator(s), BusinessInfoFragment.class, bib);
	}


	private void createLocationSettingsTab() {

		Bundle lsb = new Bundle();
		lsb.putString(LocationSettingsFragment.ADDRESS_PARAMETER, "");
		lsb.putParcelable(LocationSettingsFragment.DEFAULT_LOCATION_PARAMETER, BusinessData.getLocation());
		
		String s = getResources().getString(R.string.Location_settings);
		mTabHost.addTab(mTabHost.newTabSpec(s).setIndicator(s), LocationSettingsFragment.class, lsb);
	}
	
	
	private void setApplyChangesOnClickListener() {

		Button applyChangesButton = (Button) findViewById(R.id.apply_changes_button);
		
		applyChangesButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				
				boolean dataChanged = false;
				
				dataChanged = applyGeneralSettings();
				dataChanged |= applyBusinessSettings();
				dataChanged |= applyLocationSettings();
				
				if ( hasError ) { //One or more errors in data filling
					
					createAlertDialog(msg);
					return;
				}
				
								
				if ( dataChanged ) {
					
					String msg =  getResources().getString(R.string.data_changed_successfully);
					msg = passwordsMisMatch ? msg + "\n" + getResources().getString(R.string.new_password_wasnt_set) : msg;
					
					BusinessData.syncChanges();
					
					BusinessOpeningScreenActivity.refreshNeeded = true;
					Toast.makeText(BusinessSettingsActivity.this, msg, Toast.LENGTH_LONG).show();
					finish();
				}

			
			}
		});
	}
	
	
	private boolean applyGeneralSettings(){
		
		GeneralSettingsFragment gsf = (GeneralSettingsFragment) getSupportFragmentManager().findFragmentByTag(getResources().getString(R.string.general_settings));
		
		if (gsf == null) return false;

		boolean dataChanged = false;
		//tests if the users changed the user name and changes it accordingly
		String newUserName = gsf.getUserName();
		if(!newUserName.equals("") && !newUserName.equals(BusinessData.getUserName())){
			
			if (!newUserName.matches(DataRegexs.NAME)) { 
				
				hasError = true;
				msg = getResources().getString(R.string.illegal_user_name);
				return false;
			}
			
			dataChanged = true;
			BusinessData.setUserName(newUserName);
		}

		//tests if the users changed the email and changes it accordingly
		String newEmail = gsf.getEmail();
		if(!newEmail.equals("") && !newEmail.equals(BusinessData.getEmail()) ) {
			
			if (!newEmail.matches(DataRegexs.USER_MAIL)) {
				
				hasError = true;
				msg = getResources().getString(R.string.illegal_email);
				return false;
				
			}
			
			dataChanged = true;
			BusinessData.setEmail(newEmail);
		}

		//tests if the users changed the password and changes it accordingly
		String newPassword = gsf.getPassword();
		String newPasswordConformation = gsf.getConformationPassword();
		if(!newPassword.equals("")){
			if(!newPassword.equals(newPasswordConformation)) {
				passwordsMisMatch = true;
				
			} else {
				
				BusinessData.setPassword(newPassword);
				dataChanged = true;
			}
		}
		
		return dataChanged;
	}


	private boolean applyBusinessSettings() {

		BusinessInfoFragment bif = (BusinessInfoFragment) getSupportFragmentManager().findFragmentByTag(getResources().getString(R.string.business_settings));
		
		if (bif == null) return false;

		boolean dataChanged = false;
		
		String newBusinessName = bif.getBusinessName();
		if(!newBusinessName.equals("") && !newBusinessName.equals(BusinessData.getName())){
			
			if (!newBusinessName.matches(DataRegexs.NAME)) {
				
				hasError = true;
				msg = getResources().getString(R.string.illegal_business_name);
				return false;
				
			}
			BusinessData.setName(newBusinessName);
			dataChanged = true;
		}

		BusinessType newBusinessType = bif.getBusinessType();
		if(newBusinessType != BusinessData.getType()){
			BusinessData.setType(newBusinessType);
			dataChanged = true;
		}

		String newBusinessAddress = bif.getBusinessAddress();
		if(!newBusinessAddress.equals("") && !newBusinessAddress.equals(BusinessData.getAddress())){
			BusinessData.setAddress(newBusinessAddress);
			dataChanged = true;
		}

		String newPhoneNumber = bif.getBusinessPhoneNumber();
		if(!newPhoneNumber.equals("") && !newPhoneNumber.equals(BusinessData.getPhoneNumber())){
			BusinessData.setPhoneNumber(newPhoneNumber);
			dataChanged = true;
		}
		
		return dataChanged;
	}


	private boolean applyLocationSettings() {

		LocationSettingsFragment lsf = (LocationSettingsFragment) getSupportFragmentManager().findFragmentByTag(getResources().getString(R.string.Location_settings));
		
		if (lsf == null) return false;

		if (!lsf.neededInfoGiven()) return false;

		LatLng newLatLng = lsf.getLocation();
		if ( newLatLng.equals(BusinessData.getLocation()) ) return false;
		
		BusinessData.setLocation(newLatLng);		
		return true;
	}
	



	/*
	 * Apparently, there is an android bug whenever one of the fragments
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

