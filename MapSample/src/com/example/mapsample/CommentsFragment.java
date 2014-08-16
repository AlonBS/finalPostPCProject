package com.example.mapsample;
import java.util.Date;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class CommentsFragment extends Fragment{
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.comments_fragment,container, false);
		
		ShowDealActivity parentActivity = (ShowDealActivity)getActivity();
		if(!parentActivity.isInUserMode){
			
			//comment_edit_text;
		}
		
		return view;
	}

	public class Comment{
		public String comment;
		public String userName;
		public Date commentDate;
		
		public Comment(String commentStr, String userName, Date date){
			this.comment = commentStr;
			this.userName = userName;
			this. commentDate = date;
		}
		
		String getComment(){
			return comment;
		}
		String getUserName(){
			return userName;
		}
		Date getDate(){
			return commentDate;
		}
		
		
	}
}
