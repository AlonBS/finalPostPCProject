package com.dna.radius.businessmode;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTabHost;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import com.dna.radius.R;
import com.dna.radius.infrastructure.BaseActivity;
import com.dna.radius.infrastructure.GeneralSettingsFragment;
import com.dna.radius.infrastructure.LocationFinderFragment;
import com.dna.radius.infrastructure.SupportedTypes.BusinessType;
import com.google.android.gms.maps.model.LatLng;

public class BusinessSettingsActivity extends BaseActivity{
	public static enum CurrentRunningFragment{GENERAL_SETTINGS_FRAGMENT,BUSINESS_SETTINGS_FRAGMENT,LOCATION_SETTINGS_FRAGMENT};
	private Button generalSettingsBtn;
	private Button businessSettingsBtn;
	private Button locationSettingsBtn;
	private CurrentRunningFragment currentFragment;
	private FragmentTabHost mTabHost;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.business_settings_activity);


		// Locate android.R.id.tabhost in main_fragment.xml
		mTabHost = (FragmentTabHost) findViewById(android.R.id.tabhost);

		// Create the tabs in main_fragment.xml
		mTabHost.setup(this, getSupportFragmentManager(), R.id.tabcontent);

		Bundle generalSettingFragment = new Bundle();
		generalSettingFragment.putString(GeneralSettingsFragment.USER_NAME_PARAM, BusinessData.getUserName());
		generalSettingFragment.putString(GeneralSettingsFragment.EMAIL_PARAM, BusinessData.getEmail());
		// Create Tab1 with a custom image in res folder
		mTabHost.addTab(mTabHost.newTabSpec("tab1").setIndicator("Tab1"),
				GeneralSettingsFragment.class, generalSettingFragment);

		// Create Tab2
		Bundle fillDataBundle = new Bundle();
		fillDataBundle.putBoolean(BusinessFillDetailsFragment.IS_IN_SETTINGS_MODE_PARAM, true);
		fillDataBundle.putString(BusinessFillDetailsFragment.BUSINESS_NAME_HINT_PARAM, BusinessData.getName());
		fillDataBundle.putString(BusinessFillDetailsFragment.BUSINESS_ADDRESS_HINT_PARAM, BusinessData.getBusinessAddress());
		fillDataBundle.putString(BusinessFillDetailsFragment.BUSINESS_PHONE_HINT_PARAM, BusinessData.getPhoneNumber());
		fillDataBundle.putSerializable(BusinessFillDetailsFragment.BUSINESS_TYPE_HINT_PARAM, BusinessData.getType());
		mTabHost.addTab(mTabHost.newTabSpec("tab2").setIndicator("Tab2"),
				BusinessFillDetailsFragment.class, fillDataBundle);

		Bundle locationBundle = new Bundle();
		locationBundle.putString(LocationFinderFragment.ADDRESS_PARAMETER, "");
		// Create Tab3
		mTabHost.addTab(mTabHost.newTabSpec("tab3").setIndicator("Tab3"),
				LocationFinderFragment.class, locationBundle);




		//		generalSettingsBtn = (Button)findViewById(R.id.general_settings_btn);
		//		businessSettingsBtn = (Button)findViewById(R.id.business_settings_btn);
		//		locationSettingsBtn = (Button)findViewById(R.id.location_settings_btn);
		//
		//		generalSettingsBtn.setOnClickListener(new SwitchFragmentOnCliclListener());
		//		businessSettingsBtn.setOnClickListener(new SwitchFragmentOnCliclListener());
		//		locationSettingsBtn.setOnClickListener(new SwitchFragmentOnCliclListener());
		//
		//		FragmentManager fragmentManager = getSupportFragmentManager();
		//		FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
		//		Fragment newFragment = new GeneralSettingsFragment(BusinessData.getUserName(),BusinessData.getEmail());;
		//		fragmentTransaction.replace(R.id.business_settings_holder, newFragment);
		//		fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_ENTER_MASK);
		//		fragmentTransaction.commit();
		//
		//		currentFragment = CurrentRunningFragment.GENERAL_SETTINGS_FRAGMENT;
		//
				Button applyChagnesButton = (Button) findViewById(R.id.apply_changes_button);
				applyChagnesButton.setOnClickListener(new OnClickListener() {
		
					@Override
					public void onClick(View v) {
						//tests if it's possible to move to the next fragment
						switch (currentFragment) {
						case GENERAL_SETTINGS_FRAGMENT:
							handleApplyGeneralSettings();
							break;
						case BUSINESS_SETTINGS_FRAGMENT:
							handleApplyBusinessSettings();
							break;
						case LOCATION_SETTINGS_FRAGMENT:
							handleLocationSettings();
							break;
						default:
							return;
						}
					}
		
					
				});
		//
		//
		//

	}

	class SwitchFragmentOnCliclListener implements OnClickListener{

		@Override
		public void onClick(View v) {
			Button b = (Button)v;
			FragmentManager fragmentManager = getSupportFragmentManager();
			FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
			Fragment newFragment;
			if(b==generalSettingsBtn){
				newFragment = new GeneralSettingsFragment();
				Bundle bdl = new Bundle();
				bdl.putString(GeneralSettingsFragment.USER_NAME_PARAM, BusinessData.getUserName());
				bdl.putString(GeneralSettingsFragment.EMAIL_PARAM, BusinessData.getEmail());
				currentFragment = CurrentRunningFragment.GENERAL_SETTINGS_FRAGMENT;
				newFragment.setArguments(bdl);
			}else if(b==businessSettingsBtn){
				Bundle bdl = new Bundle();
				bdl.putBoolean(BusinessFillDetailsFragment.IS_IN_SETTINGS_MODE_PARAM, true);
				bdl.putString(BusinessFillDetailsFragment.BUSINESS_NAME_HINT_PARAM, BusinessData.getName());
				bdl.putString(BusinessFillDetailsFragment.BUSINESS_ADDRESS_HINT_PARAM, BusinessData.getBusinessAddress());
				bdl.putString(BusinessFillDetailsFragment.BUSINESS_PHONE_HINT_PARAM, BusinessData.getPhoneNumber());
				bdl.putSerializable(BusinessFillDetailsFragment.BUSINESS_TYPE_HINT_PARAM, BusinessData.getType());
				BusinessFillDetailsFragment generalSettingsFragment = new BusinessFillDetailsFragment();
				newFragment =generalSettingsFragment;
				generalSettingsFragment.setArguments(bdl);
				currentFragment = CurrentRunningFragment.BUSINESS_SETTINGS_FRAGMENT;
			}else{
				LocationFinderFragment locationFinderFragment  = new LocationFinderFragment();
				Bundle bdl = new Bundle();
				bdl.putString(LocationFinderFragment.ADDRESS_PARAMETER, "");
				locationFinderFragment.setArguments(bdl);

				locationFinderFragment.setPreviousLocation(BusinessData.getLocation());
				newFragment = locationFinderFragment;
				currentFragment = CurrentRunningFragment.LOCATION_SETTINGS_FRAGMENT;
			}
			fragmentTransaction.replace(R.id.business_settings_holder, newFragment);
			fragmentTransaction.addToBackStack(null);
			fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_ENTER_MASK);
			fragmentTransaction.commit();

		}

	}

	private void handleLocationSettings() {

		final FragmentManager fragmentManager = getSupportFragmentManager();
		LocationFinderFragment currentFragment = (LocationFinderFragment)fragmentManager.findFragmentById(R.id.business_settings_holder);

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

		final FragmentManager fragmentManager = getSupportFragmentManager();
		BusinessFillDetailsFragment currentFragment = (BusinessFillDetailsFragment)fragmentManager.findFragmentById(R.id.business_settings_holder);


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
		if(!newBusinessAddress.equals("") && !newBusinessAddress.equals(BusinessData.getBusinessAddress())){
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

		final FragmentManager fragmentManager = getSupportFragmentManager();
		GeneralSettingsFragment currentFragment = (GeneralSettingsFragment)fragmentManager.findFragmentById(R.id.business_settings_holder);


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
			didDataChanged = true;
			if(!newPassword.equals(newPasswordConformation)){
				Toast.makeText(this, getResources().getString(R.string.passwords_mismatch), Toast.LENGTH_SHORT).show();
			}else{
				BusinessData.setPassword(newPassword);
				didDataChanged = true;
			}
		}

		if(didDataChanged){
			BusinessOpeningScreenActivity.refreshNeeded = true;
			Toast.makeText(this, getResources().getString(R.string.data_changed_successfully), Toast.LENGTH_SHORT).show();
			finish();
		}
	}

}
