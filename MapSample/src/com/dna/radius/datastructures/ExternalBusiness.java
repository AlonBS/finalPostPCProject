package com.dna.radius.datastructures;

import java.io.Serializable;
import java.util.Random;

import android.util.Log;

import com.dna.radius.infrastructure.SupportedTypes;
import com.google.android.gms.maps.model.LatLng;
import com.parse.ParseGeoPoint;

/**
 * represents a BusinessMarker.
 * contains all the relevant information which the MapFragment needs for
 * showing businesses over the map, and to call the ShowDealActivity for a certein business.
 *
 */
public class ExternalBusiness implements Serializable {

	private static final long serialVersionUID = -8217685657519928663L;


	private String externBusinessId;
	private String extenBusinessName;
	private SupportedTypes.BusinessType externBusinessType;

	private double externBusinessRating;
	private double externBuisnessLocationLat, externBuisnessLocationLang;
	private String externBusinessAddress;
	private String externBusinessPhone;

	private int externBusinessTotalLikes;
	private int externBusinessTotalDislikes;

	private Deal externBusinessDeal;


	public ExternalBusiness(String id, String name, SupportedTypes.BusinessType type,
			double rating, ParseGeoPoint location, String address, String phone,
			int totalLikes, int totalDislikes, Deal deal) {

		externBusinessId = id;
		extenBusinessName = name;
		externBusinessType = type;

		externBusinessRating = rating;

		if(location!=null){
			externBuisnessLocationLat = location.getLatitude();
			externBuisnessLocationLang = location.getLongitude();
		}
		externBusinessAddress = address;
		externBusinessPhone = phone;

		externBusinessTotalLikes = totalLikes;
		externBusinessTotalDislikes = totalDislikes;

		externBusinessDeal = deal;
	}


	/**
	 * @return the externBusinessId
	 */
	public String getExternBusinessId() { return externBusinessId; }


	/**
	 * @return the extenBusinessName
	 */
	public String getExtenBusinessName() { return extenBusinessName; }


	/**
	 * @return the externBusinessType
	 */
	public SupportedTypes.BusinessType getExternBusinessType() { return externBusinessType; }


	/**
	 * @return the externBusinessRating
	 */
	public double getExternBusinessRating() { return externBusinessRating; }


	/**
	 * @return the externBuisnessLocation
	 */
	public LatLng getExternBuisnessLocation() { return new LatLng(externBuisnessLocationLat, externBuisnessLocationLang); }


	/**
	 * @return the externBusinessAddress
	 */
	public String getExternBusinessAddress() { return externBusinessAddress; }


	/**
	 * @return the externBusinessPhone
	 */
	public String getExternBusinessPhone() { return externBusinessPhone; }


	/**
	 * 
	 * @return
	 */
	public int getExternBusinessTotalLikes() { return externBusinessTotalLikes; }


	/**
	 * 
	 * @return
	 */
	public int getExternBusinessTotalDislikes() { return externBusinessTotalDislikes; }


	/**
	 * @return the externBusinessDealId
	 */
	public Deal getExternBusinessDeal() { return externBusinessDeal; }
}

