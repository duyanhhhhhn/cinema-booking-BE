package CinemaBooking.Group2.dtos.movie_review.admin;



public class AdminReviewFilterDto {
    private Integer movieId;
    private Integer rating;
    private Boolean hidden;
    private String keyword;

    public AdminReviewFilterDto() {
    }

    public Integer getMovieId() {
        return movieId;
    }

    public void setMovieId(Integer movieId) {
        this.movieId = movieId;
    }

    public Integer getRating() {
        return rating;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }

    public Boolean getHidden() {
        return hidden;
    }

    public void setHidden(Boolean hidden) {
        this.hidden = hidden;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }
}