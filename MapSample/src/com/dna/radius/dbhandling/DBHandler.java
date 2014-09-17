package com.dna.radius.dbhandling;

import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.dna.radius.R;
import com.dna.radius.businessmode.TopBusinessesHorizontalView;
import com.dna.radius.datastructures.Comment;
import com.dna.radius.datastructures.Deal;
import com.dna.radius.datastructures.ExternalBusiness;
import com.dna.radius.infrastructure.BaseActivity;
import com.dna.radius.infrastructure.SupportedTypes;
import com.dna.radius.mapsample.CommentsArrayAdapter;
import com.dna.radius.mapsample.MapManager;
import com.google.android.gms.maps.model.LatLng;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseGeoPoint;
import com.parse.ParseImageView;
import com.parse.ParseObject;
import com.parse.ParseQuery;


/**
 * The DBHandler is used for accessing Parse server, as well as local DB (using sqlite).
 */
public class DBHandler {


	/**
	 * 
	 * @param dealId
	 * @param removalNeeded - true if one needs to decrement this dealId number of dislikes
	 * 							(This is done so the parseQuery wouldn't run twice)
	 */
	public static void addLikeExternally(String dealId, boolean deleteNeeded) {

		addLikes_N_Dislikes(dealId, ParseClassesNames.BUSINESS_CURRENT_DEAL_LIKES,
				deleteNeeded, ParseClassesNames.BUSINESS_CURRENT_DEAL_DISLIKES);
	}


	/**
	 * 
	 * @param dealId
	 * @param removalNeeded - true if one needs to decrement this dealId number of likes
	 * 							(This is done so the parseQuery wouldn't run twice)
	 */
	public static void addDislikeExternally(String dealId, boolean deleteNeeded) {

		addLikes_N_Dislikes(dealId, ParseClassesNames.BUSINESS_CURRENT_DEAL_DISLIKES,
				deleteNeeded, ParseClassesNames.BUSINESS_CURRENT_DEAL_LIKES);
	}


	/**
	 * 
	 * @param dealId
	 * @param n1 - Should be - ParseClassesNames.BUSINESS_CURRENT_DEAL_LIKES / _DISLIKES
	 * @param deleteNeeded - see above
	 * @param n2 - if deleteNeeded is true, should be in contra to n1 (i.e. likes vs. dislikes etc.)
	 */
	private static void addLikes_N_Dislikes(String dealId, final String n1, final boolean deleteNeeded, final String n2) {

		String businessId = dealId.split(BaseActivity.SEPERATOR)[0];
		ParseQuery<ParseObject> query = ParseQuery.getQuery(ParseClassesNames.BUSINESS_CLASS);

		query.getInBackground(businessId, new AddRemoveLikesDislikesCallBack(dealId, n1, deleteNeeded, n2));

	}
	
	
	public static void removelikeExternally(String dealId) {
		
		removeLikes_N_Dislikes(dealId, ParseClassesNames.BUSINESS_CURRENT_DEAL_LIKES);
	}


	public static void removeDislikeExternally(String dealId) {

		removeLikes_N_Dislikes(dealId, ParseClassesNames.BUSINESS_CURRENT_DEAL_DISLIKES);
	}
	
	
	private static void removeLikes_N_Dislikes(String dealId, final String n1) {

		String businessId = dealId.split(BaseActivity.SEPERATOR)[0];
		ParseQuery<ParseObject> query = ParseQuery.getQuery(ParseClassesNames.BUSINESS_CLASS);

		query.getInBackground(businessId, new RemoveLikesDislikesCallBack(dealId, n1));

	}
	
	
	
	private static class AddRemoveLikesDislikesCallBack extends GetCallback<ParseObject> {

		private  String n1, n2;
		private  boolean deleteNeeded;
		private String dealId;

		public AddRemoveLikesDislikesCallBack(String dealId, String n1, boolean deleteNeeded, String n2) {

			this.n1 = n1;
			this.n2 = n2;
			this.deleteNeeded = deleteNeeded;
			this.dealId = dealId;

		}

		@Override
		public void done(ParseObject object, ParseException e) {

			if (e == null) {

				try {

					JSONObject curDealJO = object.getJSONObject(ParseClassesNames.BUSINESS_CURRENT_DEAL);

					if (curDealJO.isNull(ParseClassesNames.BUSINESS_CURRENT_DEAL_ID)) return;
					if (curDealJO.getString(ParseClassesNames.BUSINESS_CURRENT_DEAL_ID).compareTo(dealId) != 0) return;

					curDealJO.put(n1, curDealJO.getInt(n1) + 1);

					if (deleteNeeded)
						curDealJO.put(n2, curDealJO.getInt(n2) - 1 );

					object.put(ParseClassesNames.BUSINESS_CURRENT_DEAL, curDealJO);
					object.saveInBackground();
				}


				catch (JSONException exc) {
					Log.e("External - add like/dislike to deal", exc.getMessage());
				}
			}
			else {
				Log.e("External like/dislike", e.getMessage());
			}
		}
	}
	
	
	
	private static class RemoveLikesDislikesCallBack extends GetCallback<ParseObject> {

		private  String n1;
		private String dealId;

		public RemoveLikesDislikesCallBack(String dealId, String n1) {

			this.n1 = n1;
			this.dealId = dealId;

		}

		@Override
		public void done(ParseObject object, ParseException e) {

			if (e == null) {

				try {

					JSONObject curDealJO = object.getJSONObject(ParseClassesNames.BUSINESS_CURRENT_DEAL);

					if (curDealJO.isNull(ParseClassesNames.BUSINESS_CURRENT_DEAL_ID)) return;
					if (curDealJO.getString(ParseClassesNames.BUSINESS_CURRENT_DEAL_ID).compareTo(dealId) != 0) return;

					curDealJO.put(n1, curDealJO.getInt(n1) - 1);

					object.put(ParseClassesNames.BUSINESS_CURRENT_DEAL, curDealJO);
					object.saveInBackground();
				}

				catch (JSONException exc) {
					Log.e("External - remove like/dislike from deal", exc.getMessage());
				}
			}
			else {
				Log.e("External like/dislike", e.getMessage());
			}
		}
	}
	
	
	


	
	
	
	
	



	//TODO check radius - and check radius vs. google's radius 
	//	
	//
	//		query.whereLessThanOrEqualTo(ParseClassesNames.BUSINESS_LOCATION + "." +
	//				ParseClassesNames.BUSINESS_LOCATION_LAT, top);
	//		query.whereGreaterThanOrEqualTo(ParseClassesNames.BUSINESS_LOCATION + "." +
	//				ParseClassesNames.BUSINESS_LOCATION_LAT, buttom);
	//		query.whereLessThanOrEqualTo(ParseClassesNames.BUSINESS_LOCATION + "." +
	//				ParseClassesNames.BUSINESS_LOCATION_LONG, right);
	//		query.whereGreaterThanOrEqualTo(ParseClassesNames.BUSINESS_LOCATION + "." +
	//				ParseClassesNames.BUSINESS_LOCATION_LONG, left);
	public static void getExternalBusinessAtRadius(LatLng location, double radius) {

		ParseQuery<ParseObject> query = ParseQuery.getQuery(ParseClassesNames.BUSINESS_CLASS);
		query.whereWithinRadians(ParseClassesNames.BUSINESS_LOCATION, //TODO check radians indeed work
				new ParseGeoPoint(location.latitude, location.longitude), radius);

		query.findInBackground(new FindCallback<ParseObject>() {
			public void done(List<ParseObject> objects, ParseException e) {
				if (e == null) {

					int count = 0;
					ArrayList<ExternalBusiness>result = new ArrayList<ExternalBusiness>();

					for (ParseObject o : objects ) {

						JSONObject j = o.getJSONObject(ParseClassesNames.BUSINESS_CURRENT_DEAL);

						if (j.isNull(ParseClassesNames.BUSINESS_CURRENT_DEAL_ID)) continue; //we don't show business who currently don't have a deal						

						Deal externBusinessDeal;
						try {
							externBusinessDeal = new Deal(
									j.getString(ParseClassesNames.BUSINESS_CURRENT_DEAL_ID),
									j.getString(ParseClassesNames.BUSINESS_CURRENT_DEAL_CONTENT),
									j.getInt(ParseClassesNames.BUSINESS_CURRENT_DEAL_LIKES),
									j.getInt(ParseClassesNames.BUSINESS_CURRENT_DEAL_DISLIKES),
									new SimpleDateFormat(BaseActivity.DATE_FORMAT).parse(j.getString(ParseClassesNames.BUSINESS_CURRENT_DEAL_DATE)), // TODO add get date
									null); //TODO getDAte() // TODO - think about comment in external business

							ExternalBusiness newExtern = new ExternalBusiness(
									o.getObjectId(),
									o.getString(ParseClassesNames.BUSINESS_NAME),
									SupportedTypes.BusinessType.stringToType(o.getString(ParseClassesNames.BUSINESS_TYPE)),
									o.getDouble(ParseClassesNames.BUSINESS_RATING),
									o.getParseGeoPoint(ParseClassesNames.BUSINESS_LOCATION),
									o.getString(ParseClassesNames.BUSINESS_ADDRESS),
									o.getString(ParseClassesNames.BUSINESS_PHONE),
									0,
									0,
									externBusinessDeal); 

							result.add(newExtern);

							if (++count == 5) {
								boolean isRelevant = MapManager.addExternalBusiness(result);
								result.clear();

								if(!isRelevant) break;
							}

						} catch (JSONException | java.text.ParseException e1) {

							Log.e("DBHandler getExternalBusinessAtRadius", e1.getMessage());
						} 
					}

					MapManager.addExternalBusiness(result);

				} else {

					Log.e("DBHandler getExternalBusinessAtRadius", e.getMessage());
				}
			}
		});
	}


	
	
	/**
	 * if the business has a bitmap on parse server, loads it asynchronously and
	 * updates the relevant accordingly.
	 * 
	 * otherwise - does nothing at all :(
	 * 
	 */
	//TODO 
	public static void loadBusinessImageViewAsync(String businessId ,final ImageView imageView) {


		ParseQuery<ParseObject> query = ParseQuery.getQuery(ParseClassesNames.BUSINESS_CLASS);
		query.getInBackground(businessId, new GetCallback<ParseObject>() {

			@Override
			public void done(ParseObject object, ParseException e) {

				if (e == null) {

					ParseFile file = object.getParseFile(ParseClassesNames.BUSINESS_IMAGE);

					loadImageBusinessIdentified(file, imageView);

				}
			}

			private void loadImageBusinessIdentified(ParseFile imageFile, ImageView imageView) {

				final WeakReference<ImageView> imageViewWR = new WeakReference<ImageView>(imageView);

				if (imageFile == null) return;

				imageFile.getDataInBackground( new GetDataCallback() {

					public void done(byte[] data, ParseException e) {

						if (e == null) {

							Bitmap businessImage = BitmapFactory.decodeByteArray(data, 0 ,data.length);

							if (imageViewWR == null ||imageViewWR.get() == null) return;

							ImageView im = imageViewWR.get();
							im.setImageBitmap(businessImage);
							im.setVisibility(View.VISIBLE);

						}
						else {
							Log.e("DB - loadImageBusinessIdentified", e.getMessage());
						}
					}
				});		
			}
		});

	}







	private static final int NUM_OF_COMMENTS = 50;

	// TODO - this is not used.
	/**
	 * receives a commentArrayAdapter and comments list. updates both of the
	 * parameter asynchronously, using parse.
	 */
	public static void loadCommentsListAsync(CommentsArrayAdapter adapter, String dealID){

		final WeakReference<CommentsArrayAdapter> adapterRef = new WeakReference<CommentsArrayAdapter>(adapter);
		String businessId = dealID.split(BaseActivity.SEPERATOR)[0];

		ParseQuery<ParseObject> query = ParseQuery.getQuery(ParseClassesNames.BUSINESS_CLASS);

		query.getInBackground(businessId, new GetCallback<ParseObject>() {

			@Override
			public void done(ParseObject object, ParseException e) {

				if (e == null) {

					try {

						JSONObject curDealJO = object.getJSONObject(ParseClassesNames.BUSINESS_CURRENT_DEAL);
						if (curDealJO.isNull(ParseClassesNames.BUSINESS_CURRENT_DEAL_COMMENTS)) return;

						ArrayList<Comment> currentDealComments = new ArrayList<Comment>();
						JSONArray ja;

						ja = curDealJO.getJSONArray(ParseClassesNames.BUSINESS_CURRENT_DEAL_COMMENTS);

						for (int i = 0 ; i < ja.length() && i < NUM_OF_COMMENTS ; ++i) {

							JSONObject commentJO = ja.getJSONObject(i);
							Comment c = new Comment(
									commentJO.getString(ParseClassesNames.BUSINESS_CURRENT_DEAL_COMMENTS_AUTHOR),
									commentJO.getString(ParseClassesNames.BUSINESS_CURRENT_DEAL_COMMENTS_CONTENT),
									new SimpleDateFormat(BaseActivity.DATE_FORMAT).parse(commentJO.getString(ParseClassesNames.BUSINESS_CURRENT_DEAL_COMMENTS_DATE)));

							currentDealComments.add(c);
						}

						if (adapterRef!=null && adapterRef.get()!=null) {

							CommentsArrayAdapter commentsAdapter = adapterRef.get();
							commentsAdapter.addAll(currentDealComments);
						}

					} catch (JSONException | java.text.ParseException e1) {

						Log.e("DBHandler - loadCommentsListAsync", e1.getMessage());
					}
				}

				else{

					Log.e("DBHandler - loadCommentsListAsync", e.getMessage());
				}
			}
		});
	}

	//TODO add comment to deal in business mode
	/**
	 * This is called from ClientMode only
	 * @param dealId
	 * @param newComment
	 */
	public static void addCommentToDealExternally(String businessId, final Comment newComment) {

		ParseQuery<ParseObject> query = ParseQuery.getQuery(ParseClassesNames.BUSINESS_CLASS);
		query.getInBackground(businessId, new GetCallback<ParseObject>() {

			@Override
			public void done(ParseObject o, ParseException e) {

				if (e == null) {

					try {

						JSONObject newCommentJO = new JSONObject();

						newCommentJO.put(ParseClassesNames.BUSINESS_CURRENT_DEAL_COMMENTS_AUTHOR, newComment.getAuthorName());
						newCommentJO.put(ParseClassesNames.BUSINESS_CURRENT_DEAL_COMMENTS_CONTENT, newComment.getCommentContent());
						newCommentJO.put(ParseClassesNames.BUSINESS_CURRENT_DEAL_COMMENTS_DATE, new SimpleDateFormat(BaseActivity.DATE_FORMAT).format(newComment.getCommentDate()));

						JSONObject currentDealJO = o.getJSONObject(ParseClassesNames.BUSINESS_CURRENT_DEAL);
						currentDealJO.getJSONArray(ParseClassesNames.BUSINESS_CURRENT_DEAL_COMMENTS).put(newCommentJO);
						o.put(ParseClassesNames.BUSINESS_CURRENT_DEAL, currentDealJO);

						o.saveInBackground();	// TODO should be eventually 


					} catch (JSONException e1) {

						Log.e("DBHandler - loadCommentsListAsync", e1.getMessage());
					}
				}

				else{

					Log.e("DBHandler - loadCommentsListAsync", e.getMessage());
				}
			}
		});

	}



	public static List<ExternalBusiness> LoadTopBusinessesSync(ParseGeoPoint gp, double radius) {

		//TODO simulate asynchronous way

		final List<ExternalBusiness> result = new ArrayList<ExternalBusiness>();

		ParseQuery<ParseObject> query = ParseQuery.getQuery(ParseClassesNames.BUSINESS_CLASS);
		query.whereWithinRadians(ParseClassesNames.BUSINESS_LOCATION, gp, radius );

		try {

			if (query.count() < TopBusinessesHorizontalView.MAX_TOP_BUSINESSES) {

				radius += 0.01; //TODO change ammount
				if (query.count() < TopBusinessesHorizontalView.MAX_TOP_BUSINESSES) {
					radius += 0.02;
				}

			}
		} catch (ParseException e1) {
			Log.e("DBHandler - LoadTopBusinessesSync", e1.getMessage());
		}

		query.addDescendingOrder(ParseClassesNames.BUSINESS_RATING);

		try {
			List<ParseObject> objects = query.find();

			for (ParseObject o : objects) {

				try {
					JSONObject j = o.getJSONObject(ParseClassesNames.BUSINESS_CURRENT_DEAL);

					Deal externBusinessDeal = null;

					if (!j.isNull(ParseClassesNames.BUSINESS_CURRENT_DEAL_ID)) {

						externBusinessDeal = new Deal(
								j.getString(ParseClassesNames.BUSINESS_CURRENT_DEAL_ID),
								j.getString(ParseClassesNames.BUSINESS_CURRENT_DEAL_CONTENT),
								j.getInt(ParseClassesNames.BUSINESS_CURRENT_DEAL_LIKES),
								j.getInt(ParseClassesNames.BUSINESS_CURRENT_DEAL_DISLIKES),
								new SimpleDateFormat(BaseActivity.DATE_FORMAT).parse(j.getString(ParseClassesNames.BUSINESS_CURRENT_DEAL_DATE)), // TODO add get date
								null); //TODO getDAte() // TODO - think about comment in external business
					}

					int totalLikes = 0, totalDislikes = 0, totalDeals = 0; //TODO totalDeals is currently not used
					j = o.getJSONObject(ParseClassesNames.BUSINESS_HISTORY);

					if (!j.isNull(ParseClassesNames.BUSINESS_HISTORY_TOTAL_NUM_OF_DEALS)) {

						totalLikes = j.getInt(ParseClassesNames.BUSINESS_HISTORY_TOTAL_LIKES);						
						totalDislikes = j.getInt(ParseClassesNames.BUSINESS_HISTORY_TOTAL_DISLIKES);
						//TODO (dror - we want this?
						//totalDeals = j.getInt(ParseClassesNames.BUSINESS_HISTORY_TOTAL_NUM_OF_DEALS);
					}


					ExternalBusiness newExtern = new ExternalBusiness(
							o.getObjectId(),
							o.getString(ParseClassesNames.BUSINESS_NAME),
							SupportedTypes.BusinessType.stringToType(o.getString(ParseClassesNames.BUSINESS_TYPE)),
							o.getDouble(ParseClassesNames.BUSINESS_RATING),
							o.getParseGeoPoint(ParseClassesNames.BUSINESS_RATING),
							o.getString(ParseClassesNames.BUSINESS_ADDRESS),
							o.getString(ParseClassesNames.BUSINESS_PHONE),
							totalLikes,
							totalDislikes,
							externBusinessDeal); 


					result.add(newExtern);
					if (result.size() >= TopBusinessesHorizontalView.MAX_TOP_BUSINESSES) break;


				} catch (JSONException | java.text.ParseException e1) {

					Log.e("DBHandler - LoadTopBusinessesSync", e1.getMessage());
				} 
			}
		} catch (ParseException e) {
			Log.e("DBHandler - LoadTopBusinessesSync", e.getMessage());
		}

		//THIS is the correct version
		//		query.findInBackground(new FindCallback<ParseObject>() {
		//			public void done(List<ParseObject> objects, ParseException e) {
		//
		//				if (e == null) {
		//
		//					for (ParseObject o : objects) {
		//
		//						try {
		//							JSONObject j = o.getJSONObject(ParseClassesNames.BUSINESS_CURRENT_DEAL);
		//							Deal externBusinessDeal = new Deal(
		//									j.getString(ParseClassesNames.BUSINESS_CURRENT_DEAL_ID),
		//									j.getString(ParseClassesNames.BUSINESS_CURRENT_DEAL_CONTENT),
		//									j.getInt(ParseClassesNames.BUSINESS_CURRENT_DEAL_LIKES),
		//									j.getInt(ParseClassesNames.BUSINESS_CURRENT_DEAL_DISLIKES),
		//									new SimpleDateFormat(BaseActivity.DATE_FORMAT).parse(j.getString(ParseClassesNames.BUSINESS_CURRENT_DEAL_DATE))); //TODO getDAte()
		//
		//
		//							ExternalBusiness newExtern = new ExternalBusiness(
		//									o.getObjectId(),
		//									o.getString(ParseClassesNames.BUSINESS_NAME),
		//									SupportedTypes.BusinessType.stringToType(o.getString(ParseClassesNames.BUSINESS_TYPE)),
		//									o.getDouble(ParseClassesNames.BUSINESS_RATING),
		//									o.getParseGeoPoint(ParseClassesNames.BUSINESS_RATING),
		//									o.getString(ParseClassesNames.BUSINESS_ADDRESS),
		//									o.getString(ParseClassesNames.BUSINESS_PHONE),
		//									externBusinessDeal); 
		//							
		//							
		//							result.add(newExtern);
		//							if (result.size() >= TopBusinessesHorizontalView.MAX_TOP_BUSINESSES) break;
		//							
		//
		//						} catch (JSONException | java.text.ParseException e1) {
		//							
		//							Log.e("DBHandler - LoadTopBusinessesSync", e1.getMessage());
		//						} 
		//
		//
		//						//TODO not needed - but add simulation mechanism
		//						boolean isRelevant = MapBusinessManager.addExternalBusiness(null);
		//						if(!isRelevant){
		//							break;
		//						}
		//
		//						Log.d("test", o.getObjectId());
		//					}
		//
		//				} else {
		//					
		//					Log.e("DBHandler - LoadTopBusinessesSync", e.getMessage());
		//				}
		//			}
		//		});

		return result;
	}


	


	
}


