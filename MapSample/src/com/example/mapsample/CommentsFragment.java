package com.example.mapsample;
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
}
