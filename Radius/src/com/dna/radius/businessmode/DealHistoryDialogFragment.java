package com.dna.radius.businessmode;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.dna.radius.R;
import com.dna.radius.datastructures.Deal;

public class DealHistoryDialogFragment extends DialogFragment implements View.OnClickListener{
	private DealHistoryDialogCommunicator communitor;
	private Deal dealHistoryObj;
	private TextView dealTextViewStr;

	public static enum DealHistoryDialogResult{DELETE_DEAL_OBJECT,SET_AS_CURRENT,USER_CANCELED};

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.business_deal_history_dialog_layout,container, false);	
		getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		
		setScreenSize();

		View setDealFromHistoryBtn = (View)view.findViewById(R.id.set_history_as_current_btn);
		View deleteDealFromHistoryBtn = (View)view.findViewById(R.id.delete_from_history_btn);
		View cancelBtn = (ImageView)view.findViewById(R.id.history_dialog_user_canceled_btn);
		setDealFromHistoryBtn.setOnClickListener(this);
		deleteDealFromHistoryBtn.setOnClickListener(this);
		cancelBtn.setOnClickListener(this);

		dealTextViewStr = (TextView)view.findViewById(R.id.deal_history_text_view);
		dealTextViewStr.setText(dealHistoryObj.getDealContent());

		return view;
	}
	
	
	private void setScreenSize() {
		// This will set this dialog-themed activity to take 80% of the screen
		DisplayMetrics metrics = getResources().getDisplayMetrics();
		int screenWidth = (int) (metrics.widthPixels * 0.9);
		int screenHeight = (int) (metrics.heightPixels * 0.9);
		getDialog().getWindow().setLayout(screenWidth, screenHeight);
	}

	

	/**
	 * this C-tor creates the dialog and sets the edit text to hold the previous deal text.
	 * @param previousDeal
	 */
	public DealHistoryDialogFragment(Deal dealHistoryObj,DealHistoryDialogCommunicator communitor){
		this.dealHistoryObj = dealHistoryObj;
		this.communitor = communitor;
	}


	@Override
	public void onClick(View v) {

		if(v.getId()==R.id.set_history_as_current_btn){
			communitor.onDealHistoryDialogResult(DealHistoryDialogResult.SET_AS_CURRENT,dealHistoryObj);
			dismiss();
		}else if(v.getId()==R.id.delete_from_history_btn){
			communitor.onDealHistoryDialogResult(DealHistoryDialogResult.DELETE_DEAL_OBJECT,dealHistoryObj);
			dismiss();
		}else if(v.getId()==R.id.history_dialog_user_canceled_btn){
			communitor.onDealHistoryDialogResult(DealHistoryDialogResult.USER_CANCELED,dealHistoryObj);
			dismiss();
		}else{
			Log.d("businessHistoryDialog","received click even without knowing its source");
			communitor.onDealHistoryDialogResult(DealHistoryDialogResult.USER_CANCELED,dealHistoryObj);
			dismiss();
		}
	}

	/**
	 * any activity who wants to use this DialogFragment must implement this interface.
	 * it allows the activity to retrieve the data which was given by the user.
	 */
	interface DealHistoryDialogCommunicator{
		//the result will be USER_ERROR if the new deal str is too long, if it's empty, or if it's identical to the old deal.

		/**the result parameter represents the button which was pressed by the user
		 * the newDealStr represents the new deal string
		 * in case of an error/cancel - the newDealStr will be empty */
		public void onDealHistoryDialogResult(DealHistoryDialogResult result, Deal dealHistoryObj);
	}
}
