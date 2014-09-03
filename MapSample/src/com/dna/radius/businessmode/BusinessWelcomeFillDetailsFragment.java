package com.dna.radius.businessmode;

import com.dna.radius.R;
import com.dna.radius.datastructures.ExternalBusiness.BuisnessType;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.AdapterView.OnItemSelectedListener;

public class BusinessWelcomeFillDetailsFragment extends Fragment{

	private EditText businessNameEditText;
	private int businessType;
	private Spinner businessTypeSpinner;
	private boolean typeSelected = false;
	private String businessName;
			
			
	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View view = inflater.inflate(R.layout.business_welcome_fill_details_fragment,container, false);	

		businessNameEditText = (EditText) view.findViewById(R.id.business_name_textView);
		businessTypeSpinner = (Spinner) view.findViewById(R.id.business_type_spinner);

		setBusinessTypeSpinner();
		
		
		return view;
	}
	

	private void setBusinessTypeSpinner() {

		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(),
				R.array.business_type_spinner, android.R.layout.simple_spinner_item);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		businessTypeSpinner.setAdapter(adapter);

		businessTypeSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {

			public void onItemSelected(AdapterView<?> parent, View view, 
					int pos, long id) {
				
				String typeStr = parent.getItemAtPosition(pos).toString();
				typeSelected = true;
				if (typeStr.compareToIgnoreCase(BuisnessType.RESTAURANT.getStringRep()) == 0){
					
					businessType = BuisnessType.RESTAURANT.getParseID();
					
				}
				else if (typeStr.compareToIgnoreCase(BuisnessType.PUB.getStringRep()) == 0){
					
					businessType = BuisnessType.PUB.getParseID();
				}
				else if (typeStr.compareToIgnoreCase(BuisnessType.COFFEE.getStringRep()) == 0){
					
					businessType = BuisnessType.COFFEE.getParseID();
				}
				else if (typeStr.compareToIgnoreCase(BuisnessType.GROCERIES.getStringRep()) == 0){
					
					businessType = BuisnessType.GROCERIES.getParseID();
				}
				else if (typeStr.compareToIgnoreCase(BuisnessType.ACCOMMODATION.getStringRep()) == 0){
					
					businessType = BuisnessType.ACCOMMODATION.getParseID();
				}else{
					Log.e("BusinessWelcomeFillDerailsFragment", "error choosing a spinner option");
					typeSelected = false;
				}
			}

			public void onNothingSelected(AdapterView<?> parent) {
				//TODO REMOVE
				Toast.makeText(getActivity(), "PLEASE MAKE A SELECTION MADAFACKA", Toast.LENGTH_SHORT).show();
				
				typeSelected = false;
			}

		});

	}

	/**
	 * return true iff the user filled all the relevant data
	 * @return
	 */
	public boolean finishedFillingAllData() {

		businessName = businessNameEditText.getText().toString();

		if (businessName.isEmpty() || illegalBusinessName(businessName)) {

			//TODO maybe different message?
			Toast.makeText(getActivity(), "PUT BUSINESS!", Toast.LENGTH_SHORT).show();
			return false;
		}

		if (!typeSelected) {

			//TODO maybe different message?
			Toast.makeText(getActivity(), "PUT TYPE!", Toast.LENGTH_SHORT).show();
			return false;
		}
		
		return true;
		
	}
	
	private boolean illegalBusinessName(String bn) {

		//TODO business name filter? alpha-numeric only ? 
		return false;

	}
	
	public String getBusinessName(){
		return businessName;
	}
	public int getBusinessType(){
		return businessType;
	}
}
