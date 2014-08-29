package com.dna.radius.mapsample;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

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
import android.widget.Toast;

import com.dna.radius.clientmode.ClientData;
import com.dna.radius.datastructures.Comment;
import com.dna.radius.dbhandling.DBHandler;
import com.example.mapsample.R;

public class CommentsFragment extends Fragment{
	
	public int dealID = -1;
	private final static long  WAITING_TIME_BETWEEN_COMMENTS =  1000 * 60 * 5; //5 minutes
	
	/***
	 * for each comment which the users add, the map holds the time it happened.
	 */
	static final HashMap<Integer,Long > previousComments = new HashMap<>();
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.comments_fragment,container, false);
		final ClientData clientData = ClientData.getInstance();
		ListView commentsListView = (ListView)view.findViewById(R.id.comments_list_view);
		final ShowDealActivity parentActivity = (ShowDealActivity)getActivity();
		
		registerForContextMenu(commentsListView);
		
		//ArrayList<Comment> commentsList = new CommentDBLoadSimulatorDebug().getAllComments();
		ArrayList<Comment> commentsList = new ArrayList<Comment>();//todoDal.all(TodoDAL.DB_TYPE.SQLITE);
	    CommentsArrayAdapter adapter = new CommentsArrayAdapter(parentActivity,android.R.layout.simple_list_item_1, commentsList);
	    commentsListView.setAdapter(adapter);
		
	    if(dealID==-1){
	    	Log.e("CommentsFragment", "ERRORR!!! you didnt modify the dealID field before executing the comments fragment!");
	    	Intent intent = new Intent(Intent.ACTION_MAIN);
	    	intent.addCategory(Intent.CATEGORY_HOME);
	    	intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
	    	startActivity(intent);
	    }
	    
		DBHandler.loadCommentsListAsync(commentsList, adapter);
		
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
					//sends the comment only if the user didn't commented on thid deal in the past minutes
					boolean addComment = true;					
					if(previousComments.containsKey(dealID)){
						long lastCommentTime = previousComments.get(dealID);
						long currentTime = System.currentTimeMillis();
						
						if(currentTime < lastCommentTime + WAITING_TIME_BETWEEN_COMMENTS){
							addComment = false;
						}else{
							previousComments.remove(dealID);
							addComment = true;
						}
					}
					
					if(!addComment){
						Toast.makeText(getActivity(), "you already commented on this deal lately", Toast.LENGTH_SHORT).show();				
						return;
					}
					
					Toast.makeText(getActivity(), "Thank you for your comment!", Toast.LENGTH_SHORT).show();				
					previousComments.put(dealID,System.currentTimeMillis());
					String userName = clientData.getUserName();
					Date date = new Date();
					String commentStr = newCommentEditText.getText().toString();
					DBHandler.addComment(dealID, new Comment(commentStr,userName,date));
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
		DBHandler.close();
	}
		
	
}
