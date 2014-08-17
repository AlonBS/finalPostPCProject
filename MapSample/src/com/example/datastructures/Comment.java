package com.example.datastructures;
import java.util.ArrayList;
import java.util.Date;

/**
 * Represents a comment object
 * @author dror
 *
 */
public class Comment {
	private String commentStr;
	private String userName;
	private Date date;
	
	public Comment(String commentStr,String userName,Date date){
		this.commentStr = commentStr;
		this.userName = userName;
		this.date = date;
	}
	
	public String getCommentStr(){
		return commentStr;
	}
	public String getUserName(){
		return userName;
	}
	public Date getDate(){
		return date;
	}

	public static ArrayList<Comment> getCommentsDBDebug() {
		// TODO Auto-generated method stub
		return null;
	}
	
	/**
	 * This class is for debug purposes only! it is used for loading 
	 *  comments asynchronously using localDealCommentsTask.
	 *  We should delete this class whenever the parse server is up.
	 * @author dror
	 *
	 */
	public static class CommentDBLoadSimulatorDebug{
		private final static int NUM_OF_COMMENTS = 50;
		ArrayList<Comment> comments = new ArrayList<>();
		private int currentIndex = 0;
		private int num_of_comments;
		public CommentDBLoadSimulatorDebug(){
			for (int i=1;i<10;i++){
				comments.add(new Comment("this is a piece of shit", "drogba", new Date()));
				comments.add(new Comment("very coooool deal!!! it is very useful and fun", "partieli", new Date()));
				comments.add(new Comment("Its ok, not too bad", "alonBenShimol", new Date()));
				comments.add(new Comment("quite useful, you shuld try", "dror the king", new Date()));
				comments.add(new Comment("I like it a lot", "falafel moshiko", new Date()));
				comments.add(new Comment("better then eating the normal shit which my wife cooks...", "sami", new Date()));
				comments.add(new Comment("they are just fooling us, the offer isnt good enogh, dont go there!!", "ogabuna", new Date()));
				comments.add(new Comment("sorry I prefer my regulat ice cream", "Sarah Netanyahu", new Date()));
				comments.add(new Comment("It's a good deal, but the dishes are waayyyy to small", "Ran Ben Shimon", new Date()));
				comments.add(new Comment("I liked it ver much, thank you!!", "Frayer2000", new Date()));
				comments.add(new Comment("not bad :)", "gaylord", new Date()));
				comments.add(new Comment("The fish was awsome!! but I didn't like the fruid salad, and it wasn't so cheap after all", "member02", new Date()));
		
			}
			num_of_comments = comments.size();
		}
		
		public boolean hasMoreComments(){
			return currentIndex < num_of_comments;
		}
		
		public Comment getNext(){
			return comments.get(currentIndex++);
		}
		
		public ArrayList<Comment> getAllComments(){
			return comments;
		}
	}
	
	
	
}
