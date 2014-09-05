/**
 * This package should hold all the basic claases used to
 * put this app. running. 
 */
package com.dna.radius.infrastructure;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.dna.radius.R;
import com.dna.radius.businessmode.BusinessOpeningScreenActivity;
import com.dna.radius.clientmode.ClientOpeningScreenActivity;
import com.dna.radius.login.MainActivity;
import com.parse.ParseUser;

/***
 * This Activity is used in order to share settings and menu between
 * all activities running on this app.
 */

public class BaseActivity extends FragmentActivity{
	//TODO this value should be set when the application starts!!
	public static boolean isInBusinessMode = true; 


	@Override
	protected void onCreate(Bundle arg0) {
		// TODO Auto-generated method stub
		setContentView(R.layout.waiting_fragment);
		super.onCreate(arg0);
	}

	
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main_menu, menu);
		menu.findItem(R.id.business_settings_action).setVisible(isInBusinessMode);
		return true;
	}

	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		switch (item.getItemId()) {
		case R.id.log_out_action:
			handleLogOut();
			break;

		case R.id.user_settings_action:
			handleSettings();
			break;

		case R.id.business_settings_action:
			//TODO - implement me
			break;

		case R.id.switch_mode_action:

			handleSwitchMode();
			break;

		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}


	private void handleLogOut() {

		// log out currentUser from parse
		ParseUser.logOut();

		// remove 'keep logged in' 
		SharedPreferences settings = getSharedPreferences(MainActivity.SP_NAME, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = settings.edit();
		editor.putBoolean(MainActivity.KEEP_LOGGED, false);
		editor.commit();

		// back to 'log in' screen
		Intent intent = new Intent(getApplicationContext(), MainActivity.class);
		startActivity(intent);
		//finish(); TODO WE NEED TO DESYTRO THIS ACTIVITY. BUT CRASHES

	}

	
	private void handleSettings() {
		
		ParseUser user = ParseUser.getCurrentUser();
		
		//TODO SHOULD DESTINGIUSH BETWEEN client and business
	}


	private void handleSwitchMode() {
		

			isInBusinessMode = !isInBusinessMode;
			String msgPrefix = isInBusinessMode?  getResources().getString(R.string.to_business_mode):getResources().getString(R.string.to_client_mode);
			String msg = getResources().getString(R.string.are_you_sure) + " " + msgPrefix;
			new AlertDialog.Builder(this)
			.setTitle(getResources().getString(R.string.switching) + " " + msgPrefix)
			.setMessage(msg)
			.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
					Intent myIntent = null;
					if(isInBusinessMode){
						myIntent = new Intent(getApplicationContext(), BusinessOpeningScreenActivity.class);
					}else{
						myIntent = new Intent(getApplicationContext(), ClientOpeningScreenActivity.class);
					}
					startActivity(myIntent);
					//TODO ADD finish(); ??? 
				}
			}).setNegativeButton("No", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
					// Do nothing.
				}
			}).show();
	}

	
	@Override
	protected void onDestroy() {
		super.onDestroy();
	}
}
