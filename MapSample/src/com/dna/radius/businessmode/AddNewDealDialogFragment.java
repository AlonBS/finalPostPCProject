package com.dna.radius.businessmode;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.dna.radius.R;

public class AddNewDealDialogFragment extends DialogFragment implements View.OnClickListener{
	private AddNewDealCommunicator communitor;
	private boolean userHadADealBefore = false;
	private String previousDealStr = "";
	private EditText dealEditText;
	
	public static enum AddDealDialogResult{USER_CANCELED,USER_APPROVED,USER_ERROR};

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View view = inflater.inflate(R.layout.business_add_new_deal_layout,container, false);	
		getDialog().setTitle(getResources().getString(R.string.add_a_new_deal_dialog_title));

		ImageView okBtn = (ImageView)view.findViewById(R.id.add_deal_ok_btn);
		ImageView cancelBtn = (ImageView)view.findViewById(R.id.add_deal_cancel_btn);
		okBtn.setOnClickListener(this);
		cancelBtn.setOnClickListener(this);

		dealEditText = (EditText)view.findViewById(R.id.add_new_deal_edit_text);
		if(userHadADealBefore){
			dealEditText.setText(previousDealStr);
		}
		return view;
	}

	/**
	 * this C-tor creates the dialog and sets the edit text to hold the previous deal text.
	 * @param previousDeal
	 */
	public AddNewDealDialogFragment(String previousDeal,AddNewDealCommunicator communitor){
		this.previousDealStr = previousDeal;
		this.userHadADealBefore = true;
		this.communitor = communitor;
	}
	
	/**
	 * this C-tor creates the dialog and doesn't sets the edit text
	 */
	public AddNewDealDialogFragment(AddNewDealCommunicator communitor){
		this.communitor = communitor;
	}

	@Override
	public void onClick(View v) {

		if(v.getId()==R.id.add_deal_ok_btn){

			String newDealStr = dealEditText.getText().toString();
			if( newDealStr==null || newDealStr.equals("")){
				Toast.makeText(getActivity(), getResources().getString(R.string.add_a_new_deal_deal_is_empty), Toast.LENGTH_SHORT).show();
				return;
			}else if(newDealStr.equals(previousDealStr)){
				Toast.makeText(getActivity(), getResources().getString(R.string.add_a_new_deal_deal_identical_to_previous), Toast.LENGTH_SHORT).show();
				return;
			}
			else{
				communitor.onAddNewDealDialogResult(AddDealDialogResult.USER_APPROVED,newDealStr);
				dismiss();
			}
		}else if(v.getId()==R.id.add_deal_cancel_btn){
			communitor.onAddNewDealDialogResult(AddDealDialogResult.USER_CANCELED,"");
			dismiss();
		}
	}

	/**
	 * any activity who wants to use this DialogFragment must implement this interface.
	 * it allows the activity to retrieve the data which was given by the user.
	 */
	interface AddNewDealCommunicator{
		//the result will be USER_ERROR if the new deal str is too long, if it's empty, or if it's identical to the old deal.

		/**the result parameter represents the button which was pressed by the user
		 * the newDealStr represents the new deal string
		 * in case of an error/cancel - the newDealStr will be empty */
		public void onAddNewDealDialogResult(AddDealDialogResult result, String newDealStr);
	}
}
