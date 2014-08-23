package com.example.mapsample;


import java.util.ArrayList;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.example.datastructures.Comment;
import com.example.dbhandling.DBHandler;
import com.google.android.gms.drive.internal.ac;

public class BusinessDashboardFragment extends Fragment{
		private DBHandler dbHandler;
		private BusinessOpeningScreenActivity  activityParent = null;
		private ArrayList<Comment> commentsList;
		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			// TODO Auto-generated method stub
			View view = inflater.inflate(R.layout.business_dashboard_fragment,container, false);	
			
			activityParent = (BusinessOpeningScreenActivity)getActivity();
			
			dbHandler = new DBHandler(activityParent);
			final TextView dealTv = (TextView) view.findViewById(R.id.deal_tv);
			dbHandler.loadDealAsync(activityParent.myBusinessId, dealTv);
			 
			ImageView imageView = (ImageView)view.findViewById(R.id.buisness_image_view);
			dbHandler.loadBusinessImageViewAsync(activityParent.myBusinessId, activityParent.myBusiness.type, imageView);
			
			commentsList = new ArrayList<>();
			ListView commentsListView = (ListView)view.findViewById(R.id.comments_list_view);
			CommentsArrayAdapter commentsAdapter = new CommentsArrayAdapter(activityParent,android.R.layout.simple_list_item_1 , commentsList);
			commentsListView.setAdapter(commentsAdapter);
			dbHandler.loadCommentsListAsync(commentsList, commentsAdapter);
			
			TextView numOfLikesTV = (TextView)view.findViewById(R.id.num_of_likes_tv);
			numOfLikesTV.setText(Long.toString(activityParent.myBusiness.numOfLikes));
			
			TextView numOfDislikesTV = (TextView)view.findViewById(R.id.num_of_dislikes_tv);
			numOfDislikesTV.setText(Long.toString(activityParent.myBusiness.numOfDislikes));
			
			ImageView addDeal = (ImageView)view.findViewById(R.id.add_deal_iv);
			addDeal.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					final EditText input = new EditText(activityParent);
					// TODO Auto-generated method stub
					new AlertDialog.Builder(activityParent)
				    .setTitle("Add A new Deal")
				    .setMessage("please add a new deal to replace the old one")
				    .setView(input)
				    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
				        public void onClick(DialogInterface dialog, int whichButton) {
				            String newDealStr = input.getText().toString();
				            dbHandler.ChangeDealAndLoadToTextView(activityParent.myBusinessId, dealTv, newDealStr);
				        }
				    }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
				        public void onClick(DialogInterface dialog, int whichButton) {
				            // Do nothing.
				        }
				    }).show();

				}
			});
			
			TopBusinessesHorizontalView topBusinessesScroll = (TopBusinessesHorizontalView)view.findViewById(R.id.top_businesses_list_view);
			topBusinessesScroll.addBusiness(activityParent.myBusiness);
			topBusinessesScroll.addBusiness(activityParent.myBusiness);
			topBusinessesScroll.addBusiness(activityParent.myBusiness);
			topBusinessesScroll.addBusiness(activityParent.myBusiness);
			topBusinessesScroll.addBusiness(activityParent.myBusiness);
			topBusinessesScroll.addBusiness(activityParent.myBusiness);
			topBusinessesScroll.addBusiness(activityParent.myBusiness);
			
			return view;
			
			
		
		
		}
		
		@Override
		public void onPause() {
		// TODO Auto-generated method stub
			super.onPause();
			if(dbHandler!=null){
				dbHandler.close();
				dbHandler = null;
			}
		}
		
		@Override
		public void onDestroyView() {
		// TODO Auto-generated method stub
			super.onDestroyView();
			if(dbHandler!=null){
				dbHandler.close();
				dbHandler = null;
			}
		}
}
