package CinemaBooking.Group2.dtos.movie_review.client;

import java.util.Date;

public class MovieReviewClientDtos {
	private int id;
    private int userId;
    private int movieId;
    private int rating;
    private String comment;
    private Date createdAt;
    
    private String full_name;
    private int role_id;
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
	public String getFull_name() {
		return full_name;
	}
	public void setFull_name(String full_name) {
		this.full_name = full_name;
	}
	public int getRole_id() {
		return role_id;
	}
	public void setRole_id(int role_id) {
		this.role_id = role_id;
	}
	public MovieReviewClientDtos(int id, int userId, int movieId, int rating, String comment, Date createdAt,
			String full_name, int role_id) {
		super();
		this.id = id;
		this.userId = userId;
		this.movieId = movieId;
		this.rating = rating;
		this.comment = comment;
		this.createdAt = createdAt;
		this.full_name = full_name;
		this.role_id = role_id;
	}
	public MovieReviewClientDtos() {
		super();
		// TODO Auto-generated constructor stub
	} 
    
}
