package CinemaBooking.Group2.dtos.movie_review.admin;

public class AdminReviewMovieOptionDto {
    private int movieId;
    private String movieTitle;
    private long reviewCount;
    private double avgRating;

    public AdminReviewMovieOptionDto() {}

    public AdminReviewMovieOptionDto(int movieId, String movieTitle, long reviewCount, double avgRating) {
        this.movieId = movieId;
        this.movieTitle = movieTitle;
        this.reviewCount = reviewCount;
        this.avgRating = avgRating;
    }

    public int getMovieId() { return movieId; }
    public void setMovieId(int movieId) { this.movieId = movieId; }

    public String getMovieTitle() { return movieTitle; }
    public void setMovieTitle(String movieTitle) { this.movieTitle = movieTitle; }

    public long getReviewCount() { return reviewCount; }
    public void setReviewCount(long reviewCount) { this.reviewCount = reviewCount; }

    public double getAvgRating() { return avgRating; }
    public void setAvgRating(double avgRating) { this.avgRating = avgRating; }
}
