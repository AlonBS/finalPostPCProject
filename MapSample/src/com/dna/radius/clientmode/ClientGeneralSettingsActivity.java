package com.dna.radius.clientmode;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import com.dna.radius.R;
import com.dna.radius.infrastructure.BaseActivity;
import com.dna.radius.infrastructure.GeneralSettingsFragment;

public class ClientGeneralSettingsActivity extends BaseActivity{
	@Override
	protected void onCreate(Bundle arg0) {
		// TODO Auto-generated method stub
		super.onCreate(arg0);
		setContentView(R.layout.client_general_settings_activity);
		
		final FragmentManager fragmentManager = getSupportFragmentManager();
		FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
		GeneralSettingsFragment generalSettingsFragment = new GeneralSettingsFragment();
		fragmentTransaction.add(R.id.general_settings_holder, generalSettingsFragment);
		fragmentTransaction.commit();
	}
}
