package com.dna.radius.datastructures;

import java.util.Date;

/**
 * represents a deal which was used by the owner before.
 * should be displayed in the deal history list.
 *
 */
public class Deal {
	
	private String id;
	private String content;
	private int numOfLikes;
	private int numOfDislikes;
	private Date date;
	
	public Deal(String id, String content,
			int numOfLikes, int numOfDislikes, Date date) {
		
		this.id = id;
		this.content = content;
		this.numOfLikes = numOfLikes;
		this.numOfDislikes = numOfDislikes;
		this.date = date;
	}
	
	public String getId() { return id; }
	
	public String getContent() { return content; }
	
	public int getNumOfDislikes() { return numOfDislikes; }
	
	public int getNumOfLikes() { return numOfLikes; }
	
	public Date getDate() { return date; }
	
	public void setNumOfLikes(int numOfLikes) { this.numOfLikes = numOfLikes; }
	
	public void setNumOfDislikes(int numOfDislikes) { this.numOfDislikes = numOfDislikes; }
	
	
}
