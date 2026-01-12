package CinemaBooking.Group2.dtos.movie_review.client;

public class MovieCreateReviewDtos {
	private int userId;
    private int movieId;
    private int rating;
    private String comment;

    public int getMovieId() { return movieId; }
    public void setMovieId(Integer movieId) { this.movieId = movieId; }

    public int getRating() { return rating; }
    public void setRating(int rating) { this.rating = rating; }

    public String getComment() { return comment; }
    public MovieCreateReviewDtos(Integer movieId, Integer rating, String comment) {
		super();
		this.movieId = movieId;
		this.rating = rating;
		this.comment = comment;
	}
	public int getUserId() {
		return userId;
	}
	public void setUserId(int userId) {
		this.userId = userId;
	}
	public void setMovieId(int movieId) {
		this.movieId = movieId;
	}
	public void setComment(String comment) { this.comment = comment; }
	public MovieCreateReviewDtos() {
		super();
		// TODO Auto-generated constructor stub
	}
	
}
