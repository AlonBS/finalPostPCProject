package com.dna.radius.mapsample;

import android.os.Bundle;
import android.support.v4.app.Fragment;
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

/***
 * this fragment allows the user to like or dislike a certain deal, if he is
 * in a client mode.
 * it is also contains an imageView for the business.
 * @author dror
 *
 */
public class LikeAndDislikeFragment extends Fragment{
	private DealLikeStatus dealStatus;
	private ImageView dislikeBtn;
	private View likeBtn;
	private ShowDealActivity activityParent;
	private ClientData clientData;
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.like_and_dislike_fragment,container, false);
		ImageView imageView = (ImageView) view.findViewById(R.id.business_image_view);
		
		activityParent = (ShowDealActivity)getActivity();
		final String businessID  = activityParent.businessID;
		
		//loads the business image
		DBHandler.loadBusinessImageViewAsync(businessID, imageView,activityParent);
	
		//updates the likes and dislikes text views
		final TextView likesText = (TextView)view.findViewById(R.id.like_counter);
		likesText.setText(Long.toString(activityParent.numOfLikes));
		final TextView dislikesText = (TextView)view.findViewById(R.id.dislike_counter);
		dislikesText.setText(Long.toString(activityParent.numOfDislikes));
		
		//handles the like and dislikes buttons.
		dealStatus = clientData.getDealLikeStatus(businessID);
		likeBtn = (ImageView)view.findViewById(R.id.sounds_cool_btn);
		likeBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(!activityParent.isInUserMode){
					Toast.makeText(activityParent, "it's impossible to like/dislike a deal on business mode", Toast.LENGTH_SHORT).show();
					return;
				}
				if(dealStatus==DealLikeStatus.LIKE){
					dealStatus = DealLikeStatus.DONT_CARE;
					
					String newStr = Long.toString(Long.parseLong(likesText.getText().toString())-1);
					likesText.setText(newStr);
					clientData.setDontCareToDeal(businessID);
				}else{
					if(dealStatus==DealLikeStatus.DISLIKE){
						String newStr = Long.toString(Long.parseLong(dislikesText.getText().toString())-1);
						dislikesText.setText(newStr);
					}
					
					dealStatus = DealLikeStatus.LIKE;
					clientData.addToLikes(businessID);
					String oldText =likesText.getText().toString();
					String newStr = Long.toString(Long.parseLong(oldText)+1);
					likesText.setText(newStr);
				}
				setDislikeAndLikeBG();
			}
		});
		dislikeBtn = (ImageView)view.findViewById(R.id.no_thanks_btn);
		dislikeBtn.setOnClickListener(new OnClickListener() {
				
			@Override
			public void onClick(View v) {
				if(!activityParent.isInUserMode){
					return;
				}
				if(dealStatus==DealLikeStatus.DISLIKE){
					dealStatus = DealLikeStatus.DONT_CARE;
					clientData.setDontCareToDeal(businessID);
					String newStr = Long.toString(Long.parseLong(dislikesText.getText().toString())-1);
					dislikesText.setText(newStr);
				}else{
					if(dealStatus==DealLikeStatus.LIKE){
						String newStr = Long.toString(Long.parseLong(likesText.getText().toString())-1);
						likesText.setText(newStr);
					}
					dealStatus = DealLikeStatus.DISLIKE;
					clientData.addToDislikes(businessID);
					String newStr = Long.toString(Long.parseLong(dislikesText.getText().toString())+1);
					dislikesText.setText(newStr);
				}
				setDislikeAndLikeBG();
				
			}
		});
		setDislikeAndLikeBG();
		return view;
	}
	
	/**
	 * this function changes the backroung of the dislike and like buttons
	 * according to the user preferenced. 
	 */
	private void setDislikeAndLikeBG(){
		if(!activityParent.isInUserMode){

			dislikeBtn.setBackgroundDrawable(getResources().getDrawable(R.drawable.like_dislike_shape));
			likeBtn.setBackgroundDrawable(getResources().getDrawable(R.drawable.like_dislike_shape));
			return;
		}
		if(dealStatus==DealLikeStatus.LIKE){
			likeBtn.setBackgroundDrawable(getResources().getDrawable(R.drawable.like_buttons_background_on));
		}else{
			likeBtn.setBackgroundDrawable(getResources().getDrawable(R.drawable.like_buttons_background_off));
		}
		if(dealStatus==DealLikeStatus.DISLIKE){
			dislikeBtn.setBackgroundDrawable(getResources().getDrawable(R.drawable.like_buttons_background_on));
		}else{
			dislikeBtn.setBackgroundDrawable(getResources().getDrawable(R.drawable.like_buttons_background_off));
		}
		
	}
	@Override
	public void onDestroy() {
		super.onDestroy();
		DBHandler.close();
	}

}
