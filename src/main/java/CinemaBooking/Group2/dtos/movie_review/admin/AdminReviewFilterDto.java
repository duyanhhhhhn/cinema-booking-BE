package CinemaBooking.Group2.dtos.movie_review.admin;

import java.time.LocalDate;

public class AdminReviewFilterDto {
    private Integer movieId; // null => ALL

    public AdminReviewFilterDto() {}

    public Integer getMovieId() { return movieId; }
    public void setMovieId(Integer movieId) { this.movieId = movieId; }
}