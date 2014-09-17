package com.dna.radius.mapsample;

import java.util.ArrayList;
import java.util.HashMap;

import testing_stuff.ListViewWithMaxDimensions;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.dna.radius.R;
import com.dna.radius.clientmode.ClientData;
import com.dna.radius.datastructures.Comment;
import com.dna.radius.datastructures.ExternalBusiness;
import com.dna.radius.dbhandling.DBHandler;
import com.dna.radius.infrastructure.BaseActivity;

/**
 * This fragment shows comments on a certein deal.
 * if the user is in client mode, it allows him to comment on the deal as well.
 */
public class CommentsFragment extends Fragment{


	private String dealID = "";

	/***
	 * In order to prevent spamming, 
	 * for each comment which the users add, the map holds the time it happened.
	 */
	static HashMap<String,Long > previousComments = new HashMap<>();

	public static void restartCommentsHistory(){
		previousComments = new HashMap<>();
		
	}
	
	
	/**the amount of time (in milliseconds) which the user need to wait before writing another comment
	 * to a deal. currently set to 5 minutes*/
	private final static long  WAITING_TIME_BETWEEN_COMMENTS =  1000 * 60 * 5;

	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.comments_fragment,container, false);
		
		final ShowDealActivity parentActivity = (ShowDealActivity)getActivity();
		ExternalBusiness externBusiness = parentActivity.getExternalBusiness();
		
		if(externBusiness.getExternBusinessDeal() == null){
			Log.e("CommentsFragment", "the given deal was null");
			return view;
		}
		
		dealID = externBusiness.getExternBusinessDeal().getId();
		
		
		//load the comments list
		ListView commentsListView = (ListView)view.findViewById(R.id.comments_list_view);
		registerForContextMenu(commentsListView);
		ArrayList<Comment> commentsList = new ArrayList<Comment>();//todoDal.all(TodoDAL.DB_TYPE.SQLITE);
		CommentsArrayAdapter adapter = new CommentsArrayAdapter(parentActivity,android.R.layout.simple_list_item_1, commentsList);
		commentsListView.setAdapter(adapter);
		
//		//sets the listview max height to be 50% of the activity
//		DisplayMetrics metrics = getActivity().getResources().getDisplayMetrics();
//		int activityHeight = metrics.heightPixels;
//		//float listViewMaxHeight = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, (float) (activityHeight*0.5), getResources().getDisplayMetrics());
		//commentsListView.setMaxHeight((int) (activityHeight*0.5));

//		DisplayMetrics metrics = getActivity().getResources().getDisplayMetrics();
//		int activityHeight = metrics.heightPixels;
//		DisplayMetrics displaymetrics = new DisplayMetrics();
//		getActivity().getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
//		float screenHeight = displaymetrics.heightPixels;
//		float listViewHeight = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, (float) (screenHeight*0.2), getResources().getDisplayMetrics());
//		commentsListView.getLayoutParams().height = (int) listViewHeight;
	
		
		DBHandler.loadCommentsListAsync(adapter,dealID);

		//if the user is in user mode - allows him to comment on a certain deal.
		if(BaseActivity.isInBusinessMode){
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

			View sendCommentButton = view.findViewById(R.id.comment_send);
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
						// todo remove
						Toast.makeText(getActivity(), "you already commented on this deal lately", Toast.LENGTH_SHORT).show();				
						return;
					}

					// TODO (alon) - change toast, maybe remove
					Toast.makeText(getActivity(), "Thank you for your comment!", Toast.LENGTH_SHORT).show();				
					previousComments.put(dealID,System.currentTimeMillis());
					String commentContent = newCommentEditText.getText().toString();
					
					//TODO put in ClientData ALON 
					ClientData.commentOnADeal(dealID, commentContent);
					newCommentEditText.setText(getResources().getString(R.string.type_comment_here));
				}
			});
		}

		Log.d("CommentsFragment", "A comments fragment was created, deal id: " + dealID);
		return view;
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}


}
