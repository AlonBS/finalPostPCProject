package com.dna.radius.mapsample;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.dna.radius.businessmode.BusinessOpeningScreenActivity;
import com.dna.radius.clientmode.ClientOpeningScreenActivity;
import com.dna.radius.dbhandling.DBHandler;
import com.example.mapsample.R;

public class AbstractActivity extends FragmentActivity{
	public static boolean isInBusinessMode = true; //TODO this value should be set when the application starts!!
	
	
	@Override
	protected void onCreate(Bundle arg0) {
		// TODO Auto-generated method stub
		setContentView(R.layout.waiting_fragment);
		super.onCreate(arg0);
	}
	
	public boolean onCreateOptionsMenu(Menu menu) {
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.main_menu, menu);
	    menu.findItem(R.id.business_settings_action).setVisible(isInBusinessMode);
	    return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		switch (item.getItemId()) {
		case R.id.log_out_action:
				//TODO - handle logging out
			break;
		case R.id.user_settings_action:
			//TODO - implement me
		break;
		case R.id.business_settings_action:
			//TODO - implement me
		break;
		case R.id.switch_mode_action:
				if(!isInBusinessMode && !DBHandler.doesUserHaveBusinessMode()){
					Toast.makeText(this, getResources().getString(R.string.dont_have_business), Toast.LENGTH_LONG).show();
				}else{
					isInBusinessMode = !isInBusinessMode;
					String msgPrefix = isInBusinessMode?  getResources().getString(R.string.to_business_mode):getResources().getString(R.string.to_client_mode);
					String msg = getResources().getString(R.string.are_you_sure) + " " + msgPrefix;
					new AlertDialog.Builder(this)
				    .setTitle(getResources().getString(R.string.switching) + " " + msgPrefix)
				    .setMessage(msg)
				    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
				        public void onClick(DialogInterface dialog, int whichButton) {
				        	Intent myIntent = null;
				        	if(isInBusinessMode){
				        		myIntent = new Intent(getApplicationContext(), BusinessOpeningScreenActivity.class);
							}else{
								myIntent = new Intent(getApplicationContext(), ClientOpeningScreenActivity.class);
							}
							startActivity(myIntent);
				        }
				    }).setNegativeButton("No", new DialogInterface.OnClickListener() {
				        public void onClick(DialogInterface dialog, int whichButton) {
				            // Do nothing.
				        }
				    }).show();
					

				}
			
			break;
		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
	}
}
