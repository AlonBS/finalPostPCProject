package com.dna.radius.businessmode;




import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.dna.radius.R;
import com.dna.radius.clientmode.ClientData;
import com.dna.radius.datastructures.ExternalBusiness;
import com.dna.radius.dbhandling.DBHandler;
import com.dna.radius.infrastructure.BaseActivity;
import com.dna.radius.infrastructure.WaitingFragment;
import com.dna.radius.login.MainActivity;
import com.dna.radius.mapsample.MapWindowFragment;



public class TempBusinessOpeningScreenActivity extends BaseActivity{

	public BusinessData ownerData;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.business_opening_screen);

		//Sets the waiting fragment.
		FragmentManager fragmentManager = getSupportFragmentManager();
		Fragment waitingFragment = new BusinessDashboardFragment();
		FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
		fragmentTransaction.replace(R.id.business_fragment_layout, waitingFragment);
		fragmentTransaction.commit();

	}





}
