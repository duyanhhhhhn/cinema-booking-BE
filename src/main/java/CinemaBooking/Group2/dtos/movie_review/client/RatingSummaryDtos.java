package CinemaBooking.Group2.dtos.movie_review.client;

public class RatingSummaryDtos {
    public RatingSummaryDtos(int movieId, float avgRating) {
		super();
		this.movieId = movieId;
		this.avgRating = avgRating;
	}
	private int movieId;
    private float avgRating;
    private long ratingCount;

    public RatingSummaryDtos(int movieId, float avgRating, long ratingCount) {
        this.movieId = movieId;
        this.avgRating = avgRating;
        this.ratingCount = ratingCount;
    }

    public int getMovieId() { return movieId; }
    public float getAvgRating() { return avgRating; }
    public long getRatingCount() { return ratingCount; }
}
