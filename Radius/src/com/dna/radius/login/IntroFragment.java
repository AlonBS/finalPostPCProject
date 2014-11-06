package com.dna.radius.login;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import com.dna.radius.R;

public class IntroFragment extends Fragment {
	
	/* This fragment's view */
	private View v;
	
	static final String MODE_KEY = "mode";
	static final int CUSTOMER_MODE = 1;
	static final int BUSINESS_MODE = 2;
	

	
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		// Inflate the layout for this fragment
		v = inflater.inflate(R.layout.intro_fragment, container, false);
		
		setOnClickListeners();
		
		return v;
	}
	
	

	private void setOnClickListeners() {
		
		final View customerBtn =  v.findViewById(R.id.customer_mode_button);
		final View businessBtn =  v.findViewById(R.id.business_mode_button);
		
		customerBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				getActivity().getIntent().putExtra(MODE_KEY, CUSTOMER_MODE);
				
				startSignUpFragment();
			}
		});
		
		
		businessBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				getActivity().getIntent().putExtra(MODE_KEY, BUSINESS_MODE);
				
				startSignUpFragment();
				
			}
		});
	}
	
	
	private void startSignUpFragment() {
		
		MainActivity.fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
		
		MainActivity.fragmentTransaction.replace(R.id.loaded_fragment, new SignUpFragment());
		
		MainActivity.fragmentTransaction.addToBackStack(null);
		
		MainActivity.fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
		
		MainActivity.fragmentTransaction.commit();
		
	}
}




