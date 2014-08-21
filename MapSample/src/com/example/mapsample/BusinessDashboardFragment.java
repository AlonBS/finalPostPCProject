package com.example.mapsample;


import java.util.ArrayList;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.datastructures.Comment;
import com.example.dbhandling.DBHandler;

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
			TextView dealTv = (TextView) view.findViewById(R.id.deal_tv);
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
			
			

			
			
			return view;
			
			
		
		
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
