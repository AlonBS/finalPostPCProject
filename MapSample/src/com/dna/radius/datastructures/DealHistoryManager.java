package com.dna.radius.datastructures;

import java.util.ArrayList;

public class DealHistoryManager {
	
	
	private int totalNumOfLikes;
	private int totalNumOfDislikes;
	private int totalNumOfDeals;
	
	private ArrayList<Deal> oldDeals;
	
	public DealHistoryManager(int numOfLikes, int numOfDislikes,
			int totalNumOfDeals, ArrayList<Deal> deals) {
		
		this.totalNumOfLikes = numOfLikes;
		this.totalNumOfDislikes = numOfDislikes;
		this.totalNumOfDeals = totalNumOfDeals;
		
		this.oldDeals = deals;
	}
	
	public int getNumOfLikes() { return totalNumOfLikes; }
	
	public int getNumOfDislikes() { return totalNumOfDislikes; }
	
	public int getTotalNumOfDeals() { return totalNumOfDeals; }
	
	public ArrayList<Deal> getOldDeals() { return oldDeals;	}
	
	public void incTotalNumOfLikes (int newNum) { totalNumOfLikes += newNum; }
	
	public void incTotalNumOfDisLikes (int newNum) { totalNumOfDislikes += newNum; }
	
	public void incTotalNumOfDeals () { ++totalNumOfDeals; }
	
	public void addDeal(Deal d) { oldDeals.add(d); }
	
	public void removeOldDeal(Deal d) { oldDeals.remove(d); }
	

}
