package com.dna.radius.mapsample;
import java.util.ArrayList;
import java.util.Date;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.dna.radius.datastructures.Comment;
import com.dna.radius.dbhandling.DBHandler;
import com.example.mapsample.R;

public class CommentsFragment extends Fragment{
	
	private DBHandler dbHandler;
	public long businessID = -1;
		
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.comments_fragment,container, false);
		
		ListView commentsListView = (ListView)view.findViewById(R.id.comments_list_view);
		final ShowDealActivity parentActivity = (ShowDealActivity)getActivity();
		
		registerForContextMenu(commentsListView);
		
		//ArrayList<Comment> commentsList = new CommentDBLoadSimulatorDebug().getAllComments();
		ArrayList<Comment> commentsList = new ArrayList<Comment>();//todoDal.all(TodoDAL.DB_TYPE.SQLITE);
	    CommentsArrayAdapter adapter = new CommentsArrayAdapter(parentActivity,android.R.layout.simple_list_item_1, commentsList);
	    commentsListView.setAdapter(adapter);
		
	    if(businessID==-1){
	    	Log.e("CommentsFragment", "ERRORR!!! you didnt modify the businessID field before executing the comments fragment!");
	    	Intent intent = new Intent(Intent.ACTION_MAIN);
	    	intent.addCategory(Intent.CATEGORY_HOME);
	    	intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
	    	startActivity(intent);
	    }
	    
	    dbHandler = new DBHandler(parentActivity);
		dbHandler.loadCommentsListAsync(commentsList, adapter);
		
		if(!parentActivity.isInUserMode){
			LinearLayout linearLayout = (LinearLayout)view.findViewById(R.id.add_comment_layout);
			linearLayout.setVisibility(View.GONE);
		}else{
			final EditText newCommentEditText = (EditText)view.findViewById(R.id.comment_edit_text);
			
			newCommentEditText.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					newCommentEditText.setText("");		
				}
			});
			
			ImageView sendCommentButton = (ImageView)view.findViewById(R.id.comment_send);
			sendCommentButton.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					String userName = DBHandler.userName;
					Date date = new Date();
					String commentStr = newCommentEditText.getText().toString();
					dbHandler.addComment(businessID, new Comment(commentStr,userName,date));
					newCommentEditText.setText(getResources().getString(R.string.type_comment_here));
				}
			});
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
