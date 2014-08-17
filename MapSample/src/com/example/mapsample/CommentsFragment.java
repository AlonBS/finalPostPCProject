package com.example.mapsample;
import java.util.ArrayList;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.example.datastructures.Comment;
import com.example.datastructures.Comment.CommentDBLoadSimulatorDebug;
import com.example.dbhandling.DBHandler;

public class CommentsFragment extends Fragment{
	
	private DBHandler dbHandler;

	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.comments_fragment,container, false);
		
		ListView commentsListView = (ListView)view.findViewById(R.id.comments_list_view);
		ShowDealActivity parentActivity = (ShowDealActivity)getActivity();
		
		registerForContextMenu(commentsListView);
		
		//ArrayList<Comment> commentsList = new CommentDBLoadSimulatorDebug().getAllComments();
		ArrayList<Comment> commentsList = new ArrayList<Comment>();//todoDal.all(TodoDAL.DB_TYPE.SQLITE);
	    CommentsArrayAdapter adapter = new CommentsArrayAdapter(parentActivity,android.R.layout.simple_list_item_1, commentsList);
	    commentsListView.setAdapter(adapter);
		dbHandler = new DBHandler(parentActivity);
		dbHandler.getCommentsListAsync(commentsList, adapter);
		
		if(!parentActivity.isInUserMode){
			//comment_edit_text;
		}
		
		return view;
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		dbHandler.close();
	}
		
	
}
