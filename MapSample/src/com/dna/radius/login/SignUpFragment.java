package com.dna.radius.login;

import com.dna.radius.R;
import com.dna.radius.businessmode.BusinessOpeningScreenActivity;
import com.dna.radius.clientmode.ClientOpeningScreenActivity;
import com.dna.radius.dbhandling.ParseClassesNames;

import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;



public class SignUpFragment extends Fragment {

	/* This fragment's view*/
	private View v;

	private TextView userNameTextView, passwordTextView,
		passwordConTextView, emailAddressTextView;
	
	private Button signUpButton;

	private int app_mode;



	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// Inflate the layout for this fragment

		v = inflater.inflate(R.layout.signup_fragment, container, false);
		
		app_mode = getActivity().getIntent().getExtras().getInt(IntroFragment.MODE_KEY);

		initViews();

		setNextButton();

		return v;
	}




	private void initViews() {

		userNameTextView = (TextView) v.findViewById(R.id.user_name_textView);
		passwordTextView = (TextView) v.findViewById(R.id.password_textView);
		passwordConTextView = (TextView) v.findViewById(R.id.password_confirmation_textView);
		emailAddressTextView = (TextView) v.findViewById(R.id.email_address_textView);
		signUpButton = (Button) v.findViewById(R.id.sign_up_button);
	
	
	}
		



	private void setNextButton() {

		signUpButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				String userNameText, passwordText, passwordConfirmText, emailText;

				userNameText = userNameTextView.getText().toString();
				passwordText = passwordTextView.getText().toString();
				passwordConfirmText = passwordConTextView.getText().toString();
				emailText = emailAddressTextView.getText().toString();

				if (illegalValues(userNameText, passwordText, passwordConfirmText, emailText) ) return;

				// Save new user data into Parse.com Data Storage
				ParseUser user = new ParseUser();
				user.setUsername(userNameText);
				user.setPassword(passwordText);
				user.setEmail(emailText);
				user.put(ParseClassesNames.LAST_MODE, app_mode);
				
				//TODO add email verification using emailVerified field
				
				//TODO in main??
//				if (app_mode == IntroFragment.CUSTOMER_MODE) {
//					
//					user.put("Client Info", ParseUser.getCurrentUser());
//					user.put("Business Info", "");
//				}
//				else {
//					user.put("Client Info", "");
//					user.put("Business Info", ParseUser.getCurrentUser());
//				}
				
				
				user.signUpInBackground(new SignUpCallback() {

					public void done(ParseException e) {
						if (e == null) {

							//TODO Remove Toast
							// Show a simple Toast message upon successful registration
							Toast.makeText(getActivity().getApplicationContext(),
									"Successfully Signed up, please log in.",
									Toast.LENGTH_LONG).show();

							startUserActivity();


						} else {

							//TODO Mofify Toast
							Toast.makeText(getActivity().getApplicationContext(),
									"Sign up Error", Toast.LENGTH_LONG)
									.show();
						}
					}
				});
			}
		});
	}


	private void startUserActivity() {

		FragmentActivity mainActivity = getActivity();

		if (app_mode == IntroFragment.CUSTOMER_MODE) {

			Intent intent = new Intent(mainActivity.getApplicationContext(), ClientOpeningScreenActivity.class);
			startActivity(intent);
			mainActivity.finish();
		}
		else {
			
			Intent intent = new Intent(mainActivity.getApplicationContext(), BusinessOpeningScreenActivity.class);
			startActivity(intent);
			mainActivity.finish();

		}

	}


	// TODO COMPLETE CHECKS 
	private boolean illegalValues(String u, String p1, String p2, String e) {

		if (u.matches("[^a-zA-Z0-9 ]")) {

			Toast.makeText(getActivity().getApplicationContext(),
					getResources().getString(R.string.illegal_user_name),
					Toast.LENGTH_LONG).show();
			return true;
		}

		else if ( p1.compareTo(p2) != 0 ) {

			Toast.makeText(getActivity().getApplicationContext(),
					getResources().getString(R.string.passwords_mismatch),
					Toast.LENGTH_LONG).show();
			return true;

		}

		else if (e.contains("!!!")) {

			// TODO check if email exists and valid
			return true;
		}

		return false;
	}
}

	
