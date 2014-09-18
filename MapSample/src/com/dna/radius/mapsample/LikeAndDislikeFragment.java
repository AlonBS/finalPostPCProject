package com.dna.radius.mapsample;

import java.text.SimpleDateFormat;
import java.util.Date;

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
import com.dna.radius.datastructures.ExternalBusiness;
import com.dna.radius.dbhandling.DBHandler;
import com.dna.radius.infrastructure.BaseActivity;

/***
 * this fragment allows the user to like or dislike a certain deal, if he is
 * in a client mode.
 * it is also contains an imageView for the business.
 *
 */
public class LikeAndDislikeFragment extends Fragment{

	private View view; //This fragment's view

	private ClientChoice newChoice, oldChoice;

	private View dislikeBtn;
	private View likeBtn;
	private ShowDealActivity parentActivity;
	private TextView likesTextView;
	private TextView dislikesTextView;
	private ImageView likeImageView;
	private ImageView dislikeImageView;

	private ExternalBusiness pressedExtern;

	private int newNumOfLikes, newNumOfDislikes;

	public enum ClientChoice{LIKE,DISLIKE,DONT_CARE};



	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {

		view = inflater.inflate(R.layout.like_and_dislike_fragment,container, false);
		parentActivity = (ShowDealActivity)getActivity();
		pressedExtern = parentActivity.getExternalBusiness();

		loadImage();

		boolean hasDeal = loadLikesDislikes();

		if (!hasDeal) return view;

		newNumOfLikes = pressedExtern.getExternBusinessDeal().getNumOfLikes();
		newNumOfDislikes = pressedExtern.getExternBusinessDeal().getNumOfDislikes();

		likeImageView = (ImageView)view.findViewById(R.id.like_image_view);
		dislikeImageView = (ImageView)view.findViewById(R.id.dislike_image_view);

		//handles the like and dislikes buttons.
		if(!BaseActivity.isInBusinessMode){
			oldChoice = ClientData.getDealLikeStatus(pressedExtern.getExternBusinessDeal().getId());
			newChoice = oldChoice;
		}

		setLikeBtnOnClickListener();
		setDislikeBtnOnClickListener();
		setDislikeAndLikeBG();
		
		TextView dealTV = (TextView)view.findViewById(R.id.dealTextView);
		TextView dateTV = (TextView)view.findViewById(R.id.deal_date_text_view);
		if (pressedExtern.getExternBusinessDeal() != null) {
			Date dealDate = pressedExtern.getExternBusinessDeal().getDealDate();
			String dateStr = new SimpleDateFormat(BaseActivity.DATE_FORMAT).format(dealDate);
			dateTV.setText(dateStr);
			dealTV.setText(pressedExtern.getExternBusinessDeal().getDealContent());
		}
		else {
			dealTV.setText(getResources().getString(R.string.no_deal_currently));
		}

		

		
		return view;
	}


	private void loadImage() {

		//loads the business image		
		ImageView imageView = (ImageView) view.findViewById(R.id.business_image_view);
		DBHandler.loadBusinessImageViewAsync(pressedExtern.getExternBusinessId(), imageView);
	}

	private boolean loadLikesDislikes() {

		likesTextView = (TextView)view.findViewById(R.id.like_text_view);
		dislikesTextView = (TextView)view.findViewById(R.id.dislike_text_view);

		//updates the likes and dislikes text views
		if (pressedExtern.getExternBusinessDeal() != null) {

			likesTextView.setText(Long.toString(pressedExtern.getExternBusinessDeal().getNumOfLikes()));
			dislikesTextView.setText(Long.toString(pressedExtern.getExternBusinessDeal().getNumOfDislikes()));
		}
		else {

			likesTextView.setText(ShowDealActivity.EMPTY_DEAL);
			dislikesTextView.setText(ShowDealActivity.EMPTY_DEAL);
			return false;
		}

		return true;
	}

	private void setLikeBtnOnClickListener() {

		likeBtn = (View)view.findViewById(R.id.show_deal_like_btn);
		
		likeBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				if (BaseActivity.isInBusinessMode) {
					Toast.makeText(parentActivity, getString(R.string.no_like_in_bus_mode), Toast.LENGTH_SHORT).show();
					return;
				}

				if (newChoice == ClientChoice.LIKE) {
					newChoice = ClientChoice.DONT_CARE;
					likesTextView.setText(Integer.toString(--newNumOfLikes));

				} else {

					if (newChoice == ClientChoice.DISLIKE) {
						dislikesTextView.setText(Integer.toString(--newNumOfDislikes));
						
					}

					newChoice = ClientChoice.LIKE;
					likesTextView.setText(Integer.toString(++newNumOfLikes));
				}

				setDislikeAndLikeBG();
			}
		});

	}

	
	private void setDislikeBtnOnClickListener() {

		dislikeBtn = view.findViewById(R.id.show_deal_dislike_btn);
		dislikeBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if(BaseActivity.isInBusinessMode){
					Toast.makeText(parentActivity, "it's impossible to like/dislike a deal on business mode", Toast.LENGTH_SHORT).show();
					return;
				}
				
				if (newChoice == ClientChoice.DISLIKE) {
					
					newChoice = ClientChoice.DONT_CARE;
					dislikesTextView.setText(Integer.toString(--newNumOfDislikes));
					
				} else {
					
					if (newChoice == ClientChoice.LIKE) {
						
						likesTextView.setText(Integer.toString(--newNumOfLikes));
					}

					newChoice = ClientChoice.DISLIKE;
					dislikesTextView.setText(Integer.toString(++newNumOfDislikes));
				}
				
				setDislikeAndLikeBG();
			}
		});
	}



	/**
	 * this function changes the backroung of the dislike and like buttons
	 * according to the user preferenced. 
	 */
	private void setDislikeAndLikeBG(){

		if(BaseActivity.isInBusinessMode) {
			return;
		}

		if (newChoice == ClientChoice.LIKE) {

			likeImageView.setImageDrawable(getResources().getDrawable(R.drawable.ic_action_good_blue));
			likesTextView.setTextColor(Color.BLUE);

		}else{
			
			likeImageView.setImageDrawable(getResources().getDrawable(R.drawable.ic_action_good_black));
			likesTextView.setTextColor(Color.BLACK);
		}

		if (newChoice == ClientChoice.DISLIKE) {
			
			dislikesTextView.setTextColor(getResources().getColor(R.color.red_1));
			dislikeImageView.setImageDrawable(getResources().getDrawable(R.drawable.ic_action_bad_blue));

		} else {

			dislikesTextView.setTextColor(Color.BLACK);
			dislikeImageView.setImageDrawable(getResources().getDrawable(R.drawable.ic_action_bad_black));
		}
	}

			

	private void saveUserActions() {

		if (BaseActivity.isInBusinessMode || oldChoice == newChoice) return;

		String dealId = pressedExtern.getExternBusinessDeal().getId();

		switch (newChoice) { 
			case LIKE:
				ClientData.addToLikes(dealId);
				break;

			case DISLIKE:
				ClientData.addToDislikes(dealId);
				break;

			case DONT_CARE:
				if (oldChoice == ClientChoice.LIKE)
					ClientData.removeFromLikes(dealId);
				else 
					ClientData.removeFromDislikes(dealId);	
				break;

			default:
				Log.e("Like Dislike fragment", "ClientChoice wasn't recognized");
		}
		
		MapManager.updateExternalBusinessLikesAndDislikes(pressedExtern.getExternBusinessId(), newNumOfLikes, newNumOfDislikes);
	}


	@Override
	public void onDestroy() {

		saveUserActions();
		super.onDestroy();
	}
}
