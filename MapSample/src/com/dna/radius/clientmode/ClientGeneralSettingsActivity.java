package com.dna.radius.clientmode;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import com.dna.radius.R;
import com.dna.radius.businessmode.BusinessData;
import com.dna.radius.infrastructure.BaseActivity;
import com.dna.radius.infrastructure.GeneralSettingsFragment;

public class ClientGeneralSettingsActivity extends BaseActivity{
	private GeneralSettingsFragment generalSettingsFragment;


	@Override
	protected void onCreate(Bundle arg0) {
		// TODO Auto-generated method stub
		super.onCreate(arg0);
		setContentView(R.layout.client_general_settings_activity);
		
		final FragmentManager fragmentManager = getSupportFragmentManager();
		FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
		generalSettingsFragment = new GeneralSettingsFragment(ClientData.getUserName(),ClientData.getEmail());
		fragmentTransaction.add(R.id.general_settings_holder, generalSettingsFragment);
		fragmentTransaction.commit();
		
		Button applyChangesBtn = (Button)findViewById(R.id.apply_changes_button);
		applyChangesBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				applyChangesIfNeeded();
			}
		});
	}
	
	
	public void applyChangesIfNeeded() {

		boolean didDataChanged = false;

		//tests if the users changed the user name and changes it accordingly
		String newUserName = generalSettingsFragment.getUserName();
		if(!newUserName.equals("") && !newUserName.equals(ClientData.getUserName())){
			didDataChanged = true;
			if(isInBusinessMode){
				BusinessData.setUserName(newUserName);
			}else{
				ClientData.setUserName(newUserName);
			}
		}

		//tests if the users changed the email and changes it accordingly
		String newEmail = generalSettingsFragment.getEmail();
		if(!newEmail.equals("") && !newEmail.equals(ClientData.getEmail())){
			didDataChanged = true;
			if(isInBusinessMode){
				BusinessData.setEmail(newEmail);
			}else{
				ClientData.setEmail(newEmail);
			}
		}

		//tests if the users changed the password and changes it accordingly
		String newPassword = generalSettingsFragment.getPassword();
		String newPasswordConformation = generalSettingsFragment.getConformationPassword();
		if(!newPassword.equals("")){
			didDataChanged = true;
			if(!newPassword.equals(newPasswordConformation)){
				Toast.makeText(this, getResources().getString(R.string.passwords_mismatch), Toast.LENGTH_SHORT).show();
			}else{
				if(isInBusinessMode){
					BusinessData.setPassword(newPassword);
				}else{
					ClientData.setPassword(newPassword);
				}
				didDataChanged = true;
			}
		}

		if(didDataChanged){
			Toast.makeText(this, getResources().getString(R.string.data_changed_successfully), Toast.LENGTH_SHORT).show();
		}
	}
}