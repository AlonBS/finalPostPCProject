package com.example.mapsample;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.widget.TextView;

public class ShowDealActivity extends Activity{
	
	public final static String BUSINESS_ID_PARAM = "BusinessID";
	public final static String USER_MODE_PARAM = "IsInUserMode";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_show_deal);
		
		Intent intent = getIntent();
		String businessName = intent.getStringExtra(BUSINESS_ID_PARAM);
		boolean mode = intent.getBooleanExtra(USER_MODE_PARAM, true); //TODO - we should decide on default param
		
		TextView businessNameTV = (TextView)findViewById(R.id.businessTitle);
		businessNameTV.setText(businessName);
		TextView tvDebug = (TextView)findViewById(R.id.debugString);
		if(mode)
			tvDebug.setText(" user mode");
		else
			tvDebug.setText(" business mode");
		
		
		FragmentManager fragmentManager = getFragmentManager();
		FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
		DealPresentorFragment dealPresantorFragment = new DealPresentorFragment();
		fragmentTransaction.add(R.id.show_deal_fragment, dealPresantorFragment);
		//CommentsFragment commentsFragment = new CommentsFragment();
		//fragmentTransaction.add(R.id.show_deal_fragment, commentsFragment);
		fragmentTransaction.commit();
		
}

}
