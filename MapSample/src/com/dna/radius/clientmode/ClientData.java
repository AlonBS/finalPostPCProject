package com.dna.radius.clientmode;

import java.util.ArrayList;
import android.util.Log;
import com.dna.radius.dbhandling.DBHandler;
import com.dna.radius.dbhandling.DBHandler.DealLikeStatus;
import com.google.android.gms.maps.model.LatLng;
import com.parse.ParseUser;

/***
 * represents a data object for handling all the relevant data which is needed by
 * the client. this data is relevant for both regular clients and business owner.
 * therefore I choose to implement this class as a public singleton.
 * The ClientData should be loaded only once for each log in, with a proper client id (currently it is loaded
 * through the business owner opening screen and the client opening screen).
 * afterwards, it can be used anywhere in the application using the getInstance() function.
 *
 */
public class ClientData{
	
	//ParseUser currentUser;
	
	private int id;
	/**lists which holds all the deals which the user liked or disliked*/
	private ArrayList<Integer> dislikeList = new ArrayList<Integer>();
	private ArrayList<Integer> likeList = new ArrayList<Integer>();
	private ArrayList<Integer> favourites = new ArrayList<Integer>();
	
	/**holds true if the user is also signed up as a business*/
	private boolean haveBusiness = false;
	
	private LatLng homeLocation;
	private String userName;
	static ClientData instance = null;
	
	private ClientData(int id){
		this.id = id;
	}
	
	public String getUserName(){
		return userName;
	}
	
	/** loads the Client data from the parse DB*/
	public static void loadClient(int id){
		instance = new ClientData(id);
		DBHandler.loadUserDataSync(instance);
	}
	
	/**returns the ClientData singleton*/
	public static ClientData getInstance(){
		if(instance==null){
			Log.e("ClientData","ClientData wasn't initialized with LoadClient");
		}
		return instance;
	}


	/***
	 * sets the user's home location according to the given LatLng.
	 * if the updateServers parameter is true, updates the parse servers as well.
	 */
	public void setHome(LatLng latlng,boolean updateServers){
		this.homeLocation = latlng;
		if(updateServers){
			DBHandler.setHome(id, latlng.latitude,  latlng.longitude);
		}
	}
	
	/***
	 * gets the user's home location according to the given LatLng.
	 * if the updateServers parameter is true, updates the parse servers as well.
	 */
	public LatLng getHome(){
		return homeLocation;
	}
	
	/***
	 * a setter for the haveBusiness flag.
	 */
	public void setHaveBusiness(boolean haveBusiness) {
		this.haveBusiness = haveBusiness;
	}
	
	/***
	 * return true if the current user is also registered as a business owner.
	 * @return
	 */
	public boolean doesUserHaveBusiness(){
		return haveBusiness;
	}
	
	/**
	 * returns the user's favourites list
	 * @return
	 */
	public void setFavourites(ArrayList<Integer> favourites){
		this.favourites = favourites;
	}
	
	/**
	 * returns the user's likes list
	 * @return
	 */
	public void setLikes(ArrayList<Integer> likes){
		this.likeList = likes;
	}

	/**
	 * returns the user's dislikes list
	 * @return
	 */
	public void setDislikes(ArrayList<Integer> dislikes){
		this.dislikeList = dislikes;
	}

	

	/**
	 * receives a business id and check if it's in the user favorites list.
	 */
	public boolean isInFavourites(int businessId){
		return favourites.contains(businessId);
	}

	public void addToFavorites(int businessId){
		if(favourites.contains(businessId)){
			Log.e("ClientData","Error! business already in favourites");
		}else{
			favourites.add(businessId);
			DBHandler.updateFavourites(id, favourites);
		}
		
	}
	
	public void removeFromFavorites(int businessId){
		if(!favourites.contains(businessId)){
			Log.e("ClientData","Error! business wasnt in favourites before");
		}else{
			favourites.add(businessId);
			DBHandler.updateFavourites(id, favourites);
		}
		
	}
	
	/**
	 * return LIKE/DISLIKE/DONT_CARE according to the user preferences regarding
	 * to the current business deal.
	 * @return
	 */
	public DealLikeStatus getDealLikeStatus(int businessId){
		if(likeList.contains(businessId)){
			return DealLikeStatus.LIKE;
		}else if(dislikeList.contains(businessId)){
			return DealLikeStatus.DISLIKE;
		}else{
			return DealLikeStatus.DONT_CARE;
		}
	}
	
	public void addLikeToDeal(int businessId){
		likeList.add(businessId);
		DBHandler.setLikeToDeal(id, businessId, getDealLikeStatus(businessId));
	}
	
	public void addDislikeToDeal(int businessId){
		DBHandler.setDislikeToDeal(id, businessId, getDealLikeStatus(businessId));
	}
	
	public void setDontCareToDeal(int businessId){
		DBHandler.setDontCareToDeal(id, businessId, getDealLikeStatus(businessId));
	}
}
