package com.dna.radius.mapsample;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.dna.radius.R;
import com.dna.radius.clientmode.ClientData;
import com.dna.radius.dbhandling.DBHandler;
import com.dna.radius.dbhandling.DBHandler.DealLikeStatus;
import com.dna.radius.infrastructure.BaseActivity;

/***
 * this fragment allows the user to like or dislike a certain deal, if he is
 * in a client mode.
 * it is also contains an imageView for the business.
 * @author dror
 *
 */
public class LikeAndDislikeFragment extends Fragment{
	private DealLikeStatus updatedDealStatus,originalDealStatus;
	private View dislikeBtn;
	private View likeBtn;
	private ShowDealActivity activityParent;
	private TextView likesTextView;
	private TextView dislikesTextView;
	private ImageView likeImageView;
	private ImageView dislikeImageView;

	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.like_and_dislike_fragment,container, false);
		ImageView imageView = (ImageView) view.findViewById(R.id.business_image_view);

		activityParent = (ShowDealActivity)getActivity();

		//loads the business image
		DBHandler.loadBusinessImageViewAsync(activityParent.dealID, imageView,activityParent);

		//updates the likes and dislikes text views
		likesTextView = (TextView)view.findViewById(R.id.like_text_view);
		likesTextView.setText(Long.toString(activityParent.numOfLikes));
		dislikesTextView = (TextView)view.findViewById(R.id.dislike_text_view);
		dislikesTextView.setText(Long.toString(activityParent.numOfDislikes));

		likeImageView = (ImageView)view.findViewById(R.id.like_image_view);
		dislikeImageView = (ImageView)view.findViewById(R.id.dislike_image_view);

		//handles the like and dislikes buttons.
		if(!BaseActivity.isInBusinessMode){
			originalDealStatus = ClientData.getDealLikeStatus(activityParent.dealID);
			updatedDealStatus = originalDealStatus;
		}
		likeBtn = (View)view.findViewById(R.id.sounds_cool_btn);
		likeBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(BaseActivity.isInBusinessMode){
					Toast.makeText(activityParent, "it's impossible to like/dislike a deal on business mode", Toast.LENGTH_SHORT).show();
					return;
				}
				if(updatedDealStatus==DealLikeStatus.LIKE){
					updatedDealStatus = DealLikeStatus.DONT_CARE;

					String newStr = Long.toString(Long.parseLong(likesTextView.getText().toString())-1);
					likesTextView.setText(newStr);
				}else{
					if(updatedDealStatus==DealLikeStatus.DISLIKE){
						String newStr = Long.toString(Long.parseLong(dislikesTextView.getText().toString())-1);
						dislikesTextView.setText(newStr);
					}

					updatedDealStatus = DealLikeStatus.LIKE;
					String oldText =likesTextView.getText().toString();
					String newStr = Long.toString(Long.parseLong(oldText)+1);
					likesTextView.setText(newStr);
				}
				setDislikeAndLikeBG();
			}
		});
		dislikeBtn = view.findViewById(R.id.no_thanks_btn);
		dislikeBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if(BaseActivity.isInBusinessMode){
					Toast.makeText(activityParent, "it's impossible to like/dislike a deal on business mode", Toast.LENGTH_SHORT).show();
					return;
				}
				if(updatedDealStatus==DealLikeStatus.DISLIKE){
					updatedDealStatus = DealLikeStatus.DONT_CARE;
					String newStr = Long.toString(Long.parseLong(dislikesTextView.getText().toString())-1);
					dislikesTextView.setText(newStr);
				}else{
					if(updatedDealStatus==DealLikeStatus.LIKE){
						String newStr = Long.toString(Long.parseLong(likesTextView.getText().toString())-1);
						likesTextView.setText(newStr);
					}
					updatedDealStatus = DealLikeStatus.DISLIKE;
					String newStr = Long.toString(Long.parseLong(dislikesTextView.getText().toString())+1);
					dislikesTextView.setText(newStr);
				}
				setDislikeAndLikeBG();

			}
		});
		setDislikeAndLikeBG();
		return view;
	}


	
	
	
	private void saveUserActions(){
		if(BaseActivity.isInBusinessMode){
			return;
		}
		
		
		Log.d("LikeAndDislikeFragment.saveUserActions","previus preferences: " + originalDealStatus.name() + ", new preferenced: " + updatedDealStatus.name());

		if(updatedDealStatus==DealLikeStatus.LIKE){
			ClientData.addToLikes(activityParent.dealID);
		}else if(updatedDealStatus==DealLikeStatus.DISLIKE){
			ClientData.addToDislikes(activityParent.dealID);
		}else if(updatedDealStatus==DealLikeStatus.DONT_CARE){
			if(originalDealStatus == DealLikeStatus.LIKE){
				ClientData.removeFromLikes(activityParent.dealID);
			}else if(originalDealStatus == DealLikeStatus.DISLIKE){
				ClientData.removeFromDislikes(activityParent.dealID);
			}
		}
	}


	/**
	 * this function changes the backroung of the dislike and like buttons
	 * according to the user preferenced. 
	 */
	@SuppressWarnings("deprecation")
	private void setDislikeAndLikeBG(){
		if(BaseActivity.isInBusinessMode){
			return;
		}
		if(updatedDealStatus==DealLikeStatus.LIKE){

			likeImageView.setImageDrawable(getResources().getDrawable(R.drawable.ic_action_good_blue));
			likesTextView.setTextColor(Color.BLUE);
		}else{
			likeImageView.setImageDrawable(getResources().getDrawable(R.drawable.ic_action_good_black));
			likesTextView.setTextColor(Color.BLACK);
		}
		if(updatedDealStatus==DealLikeStatus.DISLIKE){
			dislikesTextView.setTextColor(Color.BLUE);
			dislikeImageView.setImageDrawable(getResources().getDrawable(R.drawable.ic_action_bad_blue));
		}else{
			dislikesTextView.setTextColor(Color.BLACK);
			dislikeImageView.setImageDrawable(getResources().getDrawable(R.drawable.ic_action_bad_black));
		}

	}
	@Override
	public void onDestroy() {
		
		saveUserActions();
		super.onDestroy();
		DBHandler.close(); //TODO - needed?
	}

}
