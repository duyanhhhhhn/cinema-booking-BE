package CinemaBooking.Group2.models;

import java.util.Date;

public class MovieReview {

	private int id;
    private int userId;
    private int movieId;
    private int rating;
    private String comment;
    private Date createdAt;
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getUserId() {
		return userId;
	}
	public void setUserId(int userId) {
		this.userId = userId;
	}
	public int getMovieId() {
		return movieId;
	}
	public void setMovieId(int movieId) {
		this.movieId = movieId;
	}
	public int getRating() {
		return rating;
	}
	public void setRating(int rating) {
		this.rating = rating;
	}
	public String getComment() {
		return comment;
	}
	public void setComment(String comment) {
		this.comment = comment;
	}
	public Date getCreatedAt() {
		return createdAt;
	}
	public void setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
	}
	/**
	 * @param id
	 * @param userId
	 * @param movieId
	 * @param rating
	 * @param comment
	 * @param createdAt
	 */
	public MovieReview(int id, int userId, int movieId, int rating, String comment, Date createdAt) {
		super();
		this.id = id;
		this.userId = userId;
		this.movieId = movieId;
		this.rating = rating;
		this.comment = comment;
		this.createdAt = createdAt;
	}
	/**
	 * 
	 */
	public MovieReview() {
		super();
		// TODO Auto-generated constructor stub
	}
    
    

}
