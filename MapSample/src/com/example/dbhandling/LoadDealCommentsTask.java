package com.example.dbhandling;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Date;

import android.content.ClipData.Item;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

import com.example.datastructures.Comment;
import com.example.datastructures.Comment.CommentDBLoadSimulatorDebug;
import com.example.mapsample.CommentsArrayAdapter;

public class LoadDealCommentsTask extends AsyncTask<Void, ArrayList<Comment>, Void>{

	private ArrayList<Comment> origCommentsListRef;
	private WeakReference<CommentsArrayAdapter> adapter;

	public LoadDealCommentsTask(ArrayList<Comment> commentsList, CommentsArrayAdapter adapter) {
		this.origCommentsListRef = commentsList;
		this.adapter = new WeakReference<CommentsArrayAdapter>(adapter);
	}

	@Override
	protected Void doInBackground(Void... params) {
		
		final CommentDBLoadSimulatorDebug dbCursur =  new CommentDBLoadSimulatorDebug(); //TODO - this list is for debug only!!
		final int NUM_OF_ITEMS_PER_UPDATE = 2; //TODO - modify it!!
		int counter = 0;
		ArrayList<Comment> commentsList = new ArrayList<Comment>();
		
		while(dbCursur.hasMoreComments()){
				counter++;
				Comment nextComment = dbCursur.getNext();
				commentsList.add(nextComment);
				if(counter%NUM_OF_ITEMS_PER_UPDATE==0){
					counter = 0;
					publishProgress(commentsList);
					commentsList = new ArrayList<Comment>();
					try {
						Thread.sleep(5000); //TODO - delete!
					} catch (InterruptedException e) {}
				}
		}
		
		if(commentsList.size()>0){
			publishProgress(commentsList);
		}

		return null;
	}

	@Override
	protected void onProgressUpdate(ArrayList<Comment>... comments) {
		// TODO Auto-generated method stub
		super.onProgressUpdate(comments);
		for(Comment comment: comments[0]){
			origCommentsListRef.add(comment); 
		}
		if (adapter != null) {
            final CommentsArrayAdapter adapterObj = adapter.get();
            if (adapterObj != null) {
            	adapterObj.notifyDataSetChanged();
            }
        }else{
        	Log.d("LoadDealComments", "comments fragment was destroyed, stops loading comments");
        	this.cancel(true);
        }
		
	}


	@Override
	protected void onPostExecute(Void result) {
		// TODO Auto-generated method stub
		super.onPostExecute(result);
		Log.d("LoadDealComments", "finished updating all comments");
    	
	}
}
