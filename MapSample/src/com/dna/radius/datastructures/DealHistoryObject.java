package com.dna.radius.datastructures;

import java.util.Date;

/**
 * represents a deal which was used by the owner before.
 * should be displayed in the deal history list.
 * @author dror
 *
 */
public class DealHistoryObject {
	private String deal;
	private Date date;
	private int dealID;
	private int numOfLikes;
	private int numOfDislikes;
	
	public DealHistoryObject(int dealID, String deal, Date date, int numOfLikes, int numOfDislikes) {
		this.deal = deal;
		this.date = date;
		this.dealID = dealID;
		this.numOfLikes = numOfLikes;
		this.numOfDislikes = numOfDislikes;
	}
	
	public Date getDate() {
		return date;
	}
	public String getDealStr() {
		return deal;
	}
	public int getDealID() {
		return dealID;
	}
	public int getNumOfDislikes() {
		return numOfDislikes;
	}
	public int getNumOfLikes() {
		return numOfLikes;
	}
}
