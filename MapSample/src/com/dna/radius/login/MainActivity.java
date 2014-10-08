package com.dna.radius.login;



import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.MenuItem;
import android.view.Window;

import com.dna.radius.R;
import com.dna.radius.businessmode.BusinessOpeningScreenActivity;
import com.dna.radius.clientmode.ClientOpeningScreenActivity;
import com.dna.radius.dbhandling.ParseClassesNames;
import com.dna.radius.infrastructure.BaseActivity;
import com.dna.radius.infrastructure.MyApp;
import com.dna.radius.infrastructure.MyApp.TrackerName;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Logger.LogLevel;
import com.google.android.gms.analytics.Tracker;
import com.parse.Parse;
import com.parse.ParseAnonymousUtils;
import com.parse.ParseUser;

//This activity extends BaseActivity in order to inherit it's createDialog function
public class MainActivity extends BaseActivity {

	private final static String APP_ID = "x3jwFIHknyHP4pajQ7P9ottjCnwlnZIPl3JQLNzZ";
	private final static String CLIENT_KEY = "EXlH8MQcMa8l50sAcrL0jSbQlOhGW6MdJAu4hHAA";

	public static final String KEEP_LOGGED = "keepLoggedIn";

	public static final String SP_NAME = "com.dna.radius.SHARED_PREFERENCES";




	//static FragmentManager fragmentManager;
	static FragmentTransaction fragmentTransaction;


	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.start_activity);
		
		Tracker t = ((MyApp) getApplication()).getTracker(MyApp.TrackerName.APP_TRACKER);
//		Tracker t = GoogleAnalytics.getInstance(this).newTracker("UA-55549552-1");
		// initializse Parse settings
		setParse();
		

		// if 'keep me logged in' was previously set - we log in if possible
		SharedPreferences settings1 = getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
		boolean isChecked = settings1.getBoolean(KEEP_LOGGED, false);

		checkKeepLoggedIn(isChecked);
	}


	/*
	 * Sets Parse.com (connecting information to Parse.com cloud).
	 */
	private void setParse() {

		//TODO read about ACL and security

		Parse.initialize(getApplicationContext(), APP_ID, CLIENT_KEY);
		//ParseUser.enableAutomaticUser(); TODO should remove?

	}


	private void checkKeepLoggedIn(boolean isChecked) { 


		// call Owner / Client opening screen if 'keep me logged in' is checked
		if (isChecked) {

			ParseUser currentUser = ParseUser.getCurrentUser();

			// not anonymous user - we log in
			if (!ParseAnonymousUtils.isLinked(currentUser)) {

				// User is Verified - Start relevant screen
				if (currentUser != null) { 

					// we lode 'client screen' if last was logged on in this screen, and business otherwise
					int lastMode = currentUser.getInt(ParseClassesNames.LAST_MODE);
					if (lastMode == IntroFragment.CUSTOMER_MODE) {

						Intent intent = new Intent(getApplicationContext(), ClientOpeningScreenActivity.class);
						startActivity(intent);
						finish();
					}
					else{

						Intent intent = new Intent(getApplicationContext(), BusinessOpeningScreenActivity.class);
						startActivity(intent);
						finish();
					}
				}
			}

			else {

				SharedPreferences settings = getSharedPreferences(MainActivity.SP_NAME, Context.MODE_PRIVATE);
				SharedPreferences.Editor editor = settings.edit();
				editor.putBoolean(KEEP_LOGGED, false);
				editor.commit();
			}
		}

		openLoginScreen();
	}


	private void openLoginScreen() {

		fragmentTransaction = getSupportFragmentManager().beginTransaction();

		fragmentTransaction.replace(R.id.loaded_fragment,  new LoginFragment());

		fragmentTransaction.commit();

	}

	@Override
	public void onAttachedToWindow() {
		super.onAttachedToWindow();
		Window window = getWindow();
		window.setFormat(PixelFormat.RGBA_8888);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		return true;
	}
	
	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		GoogleAnalytics.getInstance(this).reportActivityStart(this);
	}
	
	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		GoogleAnalytics.getInstance(this).reportActivityStop(this);
	}


}