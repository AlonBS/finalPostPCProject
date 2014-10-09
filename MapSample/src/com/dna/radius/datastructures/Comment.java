package com.dna.radius.datastructures;
import java.io.Serializable;
import java.util.Date;

/**
 * Represents a comment object.
 *
 */
public class Comment implements Serializable {

	private static final long serialVersionUID = -247133707286255824L;
	
	
	private String authorName;
	private String commentContent;
	private Date commentDate;
	
	public Comment (String author, String con ,Date date){
		
		this.authorName = author;
		this.commentContent = con;
		this.commentDate = date;
	}
	
	public String getAuthorName() { return authorName; }
	
	public String getCommentContent() { return commentContent; }
	
	public Date getCommentDate() { return commentDate; }
	
	
	
}
