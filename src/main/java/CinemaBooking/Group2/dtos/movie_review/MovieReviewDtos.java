package CinemaBooking.Group2.dtos.movie_review;

import java.util.Date;

public class MovieReviewDtos {
	// user models
	private String full_name;
	private String email;
	
	// movie models
	private String title;
	private String short_description;
	private int duration_minutes;
	private String genre;
	
	// movie review models
	private int id;
	private String rating;
	private String comment;
	private Date created_at;
	
	
	public MovieReviewDtos() {
		super();
		// TODO Auto-generated constructor stub
	}
	public MovieReviewDtos(String full_name, String email, String title, String short_description, int duration_minutes,
			String genre, int id, String rating, String comment, Date created_at) {
		super();
		this.full_name = full_name;
		this.email = email;
		this.title = title;
		this.short_description = short_description;
		this.duration_minutes = duration_minutes;
		this.genre = genre;
		this.id = id;
		this.rating = rating;
		this.comment = comment;
		this.created_at = created_at;
	}
	public String getFull_name() {
		return full_name;
	}
	public void setFull_name(String full_name) {
		this.full_name = full_name;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getShort_description() {
		return short_description;
	}
	public void setShort_description(String short_description) {
		this.short_description = short_description;
	}
	public int getDuration_minutes() {
		return duration_minutes;
	}
	public void setDuration_minutes(int duration_minutes) {
		this.duration_minutes = duration_minutes;
	}
	public String getGenre() {
		return genre;
	}
	public void setGenre(String genre) {
		this.genre = genre;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getRating() {
		return rating;
	}
	public void setRating(String rating) {
		this.rating = rating;
	}
	public String getComment() {
		return comment;
	}
	public void setComment(String comment) {
		this.comment = comment;
	}
	public Date getCreated_at() {
		return created_at;
	}
	public void setCreated_at(Date created_at) {
		this.created_at = created_at;
	}
}
