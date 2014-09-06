//package com.dna.radius.businessmode;
//
//import android.os.Bundle;
//import android.support.v4.app.Fragment;
//import android.support.v4.app.FragmentManager;
//import android.support.v4.app.FragmentTransaction;
//import android.view.View;
//import android.view.View.OnClickListener;
//import android.widget.Button;
//
//import com.dna.radius.R;
//import com.dna.radius.clientmode.ClientData;
//import com.dna.radius.infrastructure.BaseActivity;
//import com.dna.radius.infrastructure.GeneralSettingsFragment;
//import com.dna.radius.infrastructure.LocationFinderFragment;
//import com.dna.radius.infrastructure.applyChangesFragmentInterface;
//import com.dna.radius.infrastructure.SupportedTypes.BusinessType;
//
//public class BusinessSettingsActivity extends BaseActivity{
//	public static enum CurrentRunningFragment{GENERAL_SETTINGS_FRAGMENT,BUSINESS_SETTINGS_FRAGMENT,LOCATION_SETTINGS_FRAGMENT};
//	private Button generalSettingsBtn;
//	private Button businessSettingsBtn;
//	private Button locationSettingsBtn;
//	private CurrentRunningFragment currentFragment;
//
//	protected void onCreate(Bundle savedInstanceState) {
//		super.onCreate(savedInstanceState);
//		setContentView(R.layout.business_settings_activity);
//
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
//		Fragment newFragment = new GeneralSettingsFragment();
//		fragmentTransaction.replace(R.id.business_settings_holder, newFragment);
//		fragmentTransaction.addToBackStack(null);
//		fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_ENTER_MASK);
//		fragmentTransaction.commit();
//
//		currentFragment = CurrentRunningFragment.GENERAL_SETTINGS_FRAGMENT;
//		
//		Button applyChagnesButton = (Button) findViewById(R.id.apply_changes_button);
//		applyChagnesButton.setOnClickListener(new OnClickListener() {
//
//			@Override
//			public void onClick(View v) {
//				final FragmentManager fragmentManager = getSupportFragmentManager();
//				//tests if it's possible to move to the next fragment
//				applyChangesFragmentInterface currentFragment = (applyChangesFragmentInterface)fragmentManager.findFragmentById(R.id.business_settings_holder);
//				currentFragment.applyChangesIfNeeded();
//			}
//		});
//
//
//
//
//	}
//
//	class SwitchFragmentOnCliclListener implements OnClickListener{
//
//		@Override
//		public void onClick(View v) {
//			Button b = (Button)v;
//			FragmentManager fragmentManager = getSupportFragmentManager();
//			FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//			Fragment newFragment;
//			if(b==generalSettingsBtn){
//				//TODO!
//				newFragment = new GeneralSettingsFragment("Moshe Ohayon","sadf@gmail.com");
//				currentFragment = CurrentRunningFragment.GENERAL_SETTINGS_FRAGMENT;
//			}else if(b==businessSettingsBtn){
//				BusinessFillDetailsFragment generalSettingsFragment = new BusinessFillDetailsFragment();
//				generalSettingsFragment.setHint("Mcdonalds", BusinessType.COFFEE, "0522513123" , "jaffa street");
//				newFragment =generalSettingsFragment;
//				currentFragment = CurrentRunningFragment.BUSINESS_SETTINGS_FRAGMENT;
//			}else{
//				newFragment = new LocationFinderFragment(null);
//				currentFragment = CurrentRunningFragment.LOCATION_SETTINGS_FRAGMENT;
//			}
//			fragmentTransaction.replace(R.id.business_settings_holder, newFragment);
//			fragmentTransaction.addToBackStack(null);
//			fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_ENTER_MASK);
//			fragmentTransaction.commit();
//
//		}
//
//	}
//	
//	
//	private void handle
//
//}
