package com.dna.radius.clientmode;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import com.dna.radius.R;
import com.dna.radius.infrastructure.BaseActivity;
import com.dna.radius.infrastructure.GeneralSettingsFragment;
import com.dna.radius.infrastructure.MyApp;
import com.dna.radius.login.MainActivity;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

public class ClientGeneralSettingsActivity extends BaseActivity{
	private GeneralSettingsFragment generalSettingsFragment;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.client_general_settings_activity);

		//TODO check with dror this is ok
		setTracker("Client Settings");
		
		setApplyChangesOnClickListener();
		
		loadGeneralSettingsFragment();
	}
	
	
	private void setApplyChangesOnClickListener() {
		
		Button applyChangesBtn = (Button)findViewById(R.id.apply_changes_button);
		applyChangesBtn.setOnClickListener(new OnClickListener() {
			@Override
			
			public void onClick(View v) {
				
				applyChangesIfNeeded();
			}
			
			
			public void applyChangesIfNeeded() {

				boolean dataChanged = false;

				//tests if the user has changed his name and changes it accordingly
				String newUserName = generalSettingsFragment.getUserName();
				if (!newUserName.equals("") && !newUserName.equals(ClientData.getUserName())){
					
					ClientData.setUserName(newUserName);
					dataChanged = true;
				}

				//tests if the user has changed his email and changes it accordingly
				String newEmail = generalSettingsFragment.getEmail();
				if (!newEmail.equals("") && !newEmail.equals(ClientData.getEmail())){
					
					ClientData.setEmail(newEmail);
					dataChanged = true;
				}

				//tests if the user has changed his password and changes it accordingly
				String newPassword = generalSettingsFragment.getPassword();
				String newPasswordConfirmation = generalSettingsFragment.getConformationPassword();
				if (!newPassword.equals("")) {
					
					if (!newPassword.equals(newPasswordConfirmation)) {
						
						createAlertDialog(getResources().getString(R.string.passwords_mismatch));
						
					} else {
						
						ClientData.setPassword(newPassword);
						dataChanged = true;
					}
				}

				if (dataChanged) {
					Toast.makeText(ClientGeneralSettingsActivity.this, getResources().getString(R.string.data_changed_successfully), Toast.LENGTH_SHORT).show();
					finish();
				}
			}
		});
		
	}
	
	
	private void loadGeneralSettingsFragment() {
		
		generalSettingsFragment = new GeneralSettingsFragment();
		Bundle bdl = new Bundle();
	    bdl.putString(GeneralSettingsFragment.USER_NAME_PARAM, ClientData.getUserName());
	    bdl.putString(GeneralSettingsFragment.EMAIL_PARAM, ClientData.getEmail());
	    generalSettingsFragment.setArguments(bdl);
		
		
		final FragmentManager fragmentManager = getSupportFragmentManager();
		FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
		fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
		fragmentTransaction.add(R.id.general_settings_holder, generalSettingsFragment);
		fragmentTransaction.commit();
	}
}
