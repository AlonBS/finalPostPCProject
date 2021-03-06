package com.dna.radius.login;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.dna.radius.R;
import com.dna.radius.businessmode.BusinessOpeningScreenActivity;
import com.dna.radius.clientmode.ClientOpeningScreenActivity;
import com.dna.radius.dbhandling.ParseClassesNames;
import com.dna.radius.infrastructure.BaseActivity;
import com.dna.radius.infrastructure.DataRegexs;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;



public class SignUpFragment extends Fragment {

	/* This fragment's view*/
	private View v;

	private TextView userNameTextView, passwordTextView,
	passwordConTextView, emailAddressTextView;

	private Button signUpButton;

	private int app_mode;

	private ProgressBar progressBar;



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
		progressBar = (ProgressBar)v.findViewById(R.id.sign_up_progress_bar);
	}


	private void setNextButton() {

		signUpButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				signUpButton.setText("");
				signUpButton.setEnabled(false);
				progressBar.setVisibility(View.VISIBLE);
				String userNameText, passwordText, passwordConfirmText, emailText;

				userNameText = userNameTextView.getText().toString();
				passwordText = passwordTextView.getText().toString();
				passwordConfirmText = passwordConTextView.getText().toString();
				emailText = emailAddressTextView.getText().toString();

				if (illegalValues(userNameText, passwordText, passwordConfirmText, emailText) ){
					signUpButton.setText(getString(R.string.sign_up));
					signUpButton.setEnabled(true);
					progressBar.setVisibility(View.GONE);
					return;
				}

				// Save new user data into Parse.com Data Storage
				ParseUser user = new ParseUser();
				user.setUsername(userNameText);
				user.setPassword(passwordText);
				user.setEmail(emailText);
				user.put(ParseClassesNames.LAST_MODE, app_mode);


				user.signUpInBackground(new SignUpCallback() {

					public void done(ParseException e) {
						if (e == null) {

							startUserActivity();

						} else {
							signUpButton.setText(getString(R.string.sign_up));
							progressBar.setVisibility(View.GONE);
							signUpButton.setEnabled(true);
							
							BaseActivity parentActivity = (BaseActivity)getActivity();
							parentActivity.createAlertDialog(e.getMessage());
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


	// TODO extends checks - next version 
	private boolean illegalValues(String u, String p1, String p2, String e) {
		BaseActivity parentActivity = (BaseActivity)getActivity();
		if (!u.matches(DataRegexs.NAME) || u.equals("")){
			parentActivity.createAlertDialog(getResources().getString(R.string.illegal_user_name));
			return true;
		}

		else if ( p1.compareTo(p2) != 0 || p1.equals("")) {
			parentActivity.createAlertDialog(getResources().getString(R.string.passwords_mismatch));
			return true;

		}

		return false;
	}
}


