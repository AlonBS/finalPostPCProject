package com.dna.radius.businessmode;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.dna.radius.R;
import com.dna.radius.businessmode.BusinessData;
import com.dna.radius.clientmode.ClientData;
import com.dna.radius.infrastructure.BaseActivity;

public class BusinessSettingsFragment extends Fragment{

	
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.general_settings_fragment,container, false);
		
		final boolean isInBusinessMode = BaseActivity.isInBusinessMode;
		
		final EditText userNameEditText = (EditText)view.findViewById(R.id.user_name_edit_text);
		final EditText newPasswordEditText = (EditText)view.findViewById(R.id.password_edit_text);
		final EditText newPasswordConformationEditText = (EditText)view.findViewById(R.id.password_confirmation_edit_text);
		final EditText emailEditText = (EditText)view.findViewById(R.id.email_address_edit_text);
		
		final String currentUserName = isInBusinessMode? BusinessData.getUserName() : ClientData.getUserName();
		userNameEditText.setHint(currentUserName);
		final String currentEmail = isInBusinessMode? BusinessData.getEmail() : ClientData.getEmail();
		emailEditText.setHint(currentEmail);
		
		Button applyChangesBtn = (Button)view.findViewById(R.id.apply_changes_button);
		applyChangesBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				boolean didDataChanged = false;
				
				//tests if the users changed the user name and changes it accordingly
				String newUserName = userNameEditText.getText().toString();
				if(!newUserName.equals("") && !newUserName.equals(currentUserName)){
					didDataChanged = true;
					if(isInBusinessMode){
						BusinessData.setUserName(newUserName);
					}else{
						ClientData.setUserName(newUserName);
					}
				}
				
				//tests if the users changed the email and changes it accordingly
				String newEmail = emailEditText.getText().toString();
				if(!newEmail.equals("") && !newEmail.equals(currentEmail)){
					didDataChanged = true;
					if(isInBusinessMode){
						BusinessData.setEmail(newEmail);
					}else{
						ClientData.setEmail(newEmail);
					}
				}
				
				//tests if the users changed the password and changes it accordingly
				String newPassword = newPasswordEditText.getText().toString();
				String newPasswordConformation = newPasswordConformationEditText.getText().toString();
				if(!newPassword.equals("")){
					didDataChanged = true;
					if(!newPassword.equals(newPasswordConformation)){
						Toast.makeText(getActivity(), getResources().getString(R.string.passwords_mismatch), Toast.LENGTH_SHORT).show();
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
					Toast.makeText(getActivity(), getResources().getString(R.string.data_changed_successfully), Toast.LENGTH_SHORT).show();
				}
			}
		});
		
		return view;
	}	



}
