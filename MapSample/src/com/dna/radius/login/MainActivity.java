package com.dna.radius.login;



import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;

import com.crashlytics.android.Crashlytics;
import com.dna.radius.R;
import com.dna.radius.businessmode.BusinessOpeningScreenActivity;
import com.dna.radius.clientmode.ClientOpeningScreenActivity;
import com.dna.radius.dbhandling.ParseClassesNames;
import com.dna.radius.infrastructure.BaseActivity;
import com.dna.radius.infrastructure.MyApp;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.parse.Parse;
import com.parse.ParseAnonymousUtils;
import com.parse.ParseException;
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
		Crashlytics.start(this);
		setContentView(R.layout.main_activity);
		
		Tracker tracker = ((MyApp) getApplication()).getTracker(MyApp.TrackerName.APP_TRACKER);
		tracker.enableExceptionReporting(true);
		tracker.setScreenName("Main Activity");
		tracker.send(new HitBuilders.AppViewBuilder().build());
		
		// Initializes Parse settings
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

		Parse.initialize(getApplicationContext(), APP_ID, CLIENT_KEY);
	}


	private void checkKeepLoggedIn(boolean isChecked) { 


		// call Owner / Client opening screen if 'keep me logged in' is checked
		if (isChecked) {

			ParseUser currentUser = ParseUser.getCurrentUser();

			// not anonymous user - we log in
			if (!ParseAnonymousUtils.isLinked(currentUser)) {

				// User is Verified - Start relevant screen
				if (currentUser != null) { 

					// we load 'client screen' if last was logged on in this screen, and business otherwise
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
	
	
	static void showErrorMessage(Fragment f, ParseException e) {

		int error_code = e.getCode();
		String msg; 

		switch (error_code) {

		case ParseException.CONNECTION_FAILED:
			msg = f.getResources().getString(R.string.connection_failed);
			break;

		case ParseException.USERNAME_MISSING:
			msg = f.getResources().getString(R.string.username_missing);
			break;

		case ParseException.PASSWORD_MISSING:
			msg = f.getResources().getString(R.string.password_missing);
			break;

		case ParseException.VALIDATION_ERROR:
			msg = f.getResources().getString(R.string.validation_failed);
			break;

		default:
			msg = f.getResources().getString(R.string.general_error);
		}
		
		BaseActivity parentActivity = (BaseActivity)f.getActivity();
		parentActivity.createAlertDialog(msg);
	}
	
	// This will disable menu in login/sign up etc.
	public boolean onCreateOptionsMenu(Menu menu) { return true; }
	
}