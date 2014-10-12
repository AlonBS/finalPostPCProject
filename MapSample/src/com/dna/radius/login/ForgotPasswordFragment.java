package com.dna.radius.login;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.dna.radius.R;
import com.dna.radius.infrastructure.BaseActivity;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.RequestPasswordResetCallback;


public class ForgotPasswordFragment extends Fragment {

	private View v;

	private EditText emailAddressEditText;

	private Button resetPasswordButton;

	private ProgressBar progressBar;


	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		v = inflater.inflate(R.layout.forgot_password_fragment, container, false);

		initViews();

		setSendPasswordOnClickListener();

		return v;
	}

	private void initViews() {

		emailAddressEditText = (EditText) v.findViewById(R.id.email_address_edit_text);
		resetPasswordButton = (Button) v.findViewById(R.id.reset_password_button);
		progressBar = (ProgressBar) v.findViewById(R.id.forgot_progress_bar);
	}

	private void setSendPasswordOnClickListener() {

		resetPasswordButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				
				String email = emailAddressEditText.getText().toString();
				if (email.isEmpty()) {
					Toast.makeText(getActivity(), getResources().getString(R.string.enter_email), Toast.LENGTH_LONG).show();
					return;
				}

				progressBar.setVisibility(View.VISIBLE);
				resetPasswordButton.setText("");

				ParseUser.requestPasswordResetInBackground(email, new RequestPasswordResetCallback() {
					public void done(ParseException e) {
						if (e == null) {
							
							// An email was successfully sent with reset instructions.
							BaseActivity parentActivity = (BaseActivity) getActivity();
							parentActivity.createAlertDialog(getResources().getString(R.string.email_sent));
							
							getActivity().getSupportFragmentManager().popBackStack();
						
						} else {
							
							MainActivity.showErrorMessage(ForgotPasswordFragment.this, e);
							progressBar.setVisibility(View.GONE);
							resetPasswordButton.setText(getString(R.string.reset_password));
						}
					}
				});



			}
		});



	}


}


