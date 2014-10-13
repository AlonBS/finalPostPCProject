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
import com.dna.radius.businessmode.BusinessSettingsActivity;
import com.dna.radius.clientmode.ClientGeneralSettingsActivity;
import com.dna.radius.clientmode.ClientOpeningScreenActivity;
import com.dna.radius.dbhandling.ParseClassesNames;
import com.dna.radius.login.MainActivity;
import com.dna.radius.map.CommentsFragment;
import com.parse.ParseUser;

/***
 * This Activity is used in order to share settings and menu between
 * all activities running on this application.
 */

public abstract class BaseActivity extends FragmentActivity{

	public static final String SEPERATOR = "###";

	public static final String DATE_FORMAT = "dd.MM.yyyy 'at' HH:mm";

	//this value should be set when the application starts!!
	public static boolean isInBusinessMode; 

	@Override
	protected void onCreate(Bundle arg0) {
		// TODO Auto-generated method stub
		super.onCreate(arg0);
	}

	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main_menu, menu);
		return true;
	}


	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.log_out_action:
			handleLogOut();
			break;

		case R.id.settings_action:
			handleSettings();
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

		//resets the comments fragment
		CommentsFragment.restartCommentsHistory();
	
		// back to 'log in' screen
		Intent intent = new Intent(getApplicationContext(), MainActivity.class);
		startActivity(intent);

		finish();

	}


	private void handleSettings() {

		if(isInBusinessMode){
			Intent myIntent = new Intent(this, BusinessSettingsActivity.class);
			startActivity(myIntent);
		}else{

			Intent myIntent = new Intent(this, ClientGeneralSettingsActivity.class);
			startActivity(myIntent);
		}


	}


	private void handleSwitchMode() {

		String msgPrefix = !isInBusinessMode ?  getResources().getString(R.string.to_business_mode):getResources().getString(R.string.to_client_mode);
		String msg = getResources().getString(R.string.are_you_sure) + " " + msgPrefix;

		new AlertDialog.Builder(this)
		.setTitle(getResources().getString(R.string.switch_mode))
		.setMessage(msg)
		.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {

				Intent myIntent = null;
				isInBusinessMode = !isInBusinessMode;

				ParseUser currentUser = ParseUser.getCurrentUser();
				if (currentUser != null) {
					if (isInBusinessMode)
						ParseUser.getCurrentUser().put(ParseClassesNames.LAST_MODE, ParseClassesNames.LAST_MODE_BUSINESS_MODE);
					else
						ParseUser.getCurrentUser().put(ParseClassesNames.LAST_MODE, ParseClassesNames.LAST_MODE_CLIENT_MODE);

					currentUser.saveInBackground();
				}


				if(isInBusinessMode)
					myIntent = new Intent(getApplicationContext(), BusinessOpeningScreenActivity.class);
				else
					myIntent = new Intent(getApplicationContext(), ClientOpeningScreenActivity.class);

				startActivity(myIntent);
				finish();
			}
		}).setNegativeButton("No", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
			}
		}).show();
	}


	public void createAlertDialog(String string) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(string)
		.setCancelable(false)
		.setPositiveButton("OK", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				return;
			}
		});
		builder.show();
	}



	@Override
	protected void onDestroy() {
		super.onDestroy();
	}
}
