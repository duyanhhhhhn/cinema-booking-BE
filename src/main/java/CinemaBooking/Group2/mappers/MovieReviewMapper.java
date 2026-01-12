package CinemaBooking.Group2.mappers;

import CinemaBooking.Group2.dtos.movie_review.admin.MovieReviewDtos;
import CinemaBooking.Group2.dtos.movie_review.client.MovieCreateReviewDtos;
import CinemaBooking.Group2.dtos.movie_review.client.MovieReviewClientDtos;
import CinemaBooking.Group2.models.MovieReview;

import java.util.Date;

public class MovieReviewMapper {

    private MovieReviewMapper() {}

    /** Map Model -> DTO (basic fields). Extra display fields can be set separately. */
    public static MovieReviewClientDtos toDto(MovieReview model) {
        if (model == null) return null;

        MovieReviewClientDtos dto = new MovieReviewClientDtos();
        dto.setId(model.getId());
        dto.setUserId(model.getUserId());
        dto.setMovieId(model.getMovieId());
        dto.setRating(model.getRating());
        dto.setComment(model.getComment());
        dto.setCreatedAt(model.getCreatedAt());
        return dto;
    }

    /** Map CreateDto + userId -> Model (id/createdAt are DB-generated). */
    public static MovieReview toModel(int userId, MovieCreateReviewDtos req) {
        if (req == null) return null;
        MovieReview m = new MovieReview();
        m.setUserId(userId);
        m.setMovieId(req.getMovieId());
        m.setRating(req.getRating());
        m.setComment(req.getComment());
        return m;
    }

    /**
     * Convenience builder for DTO when you already have joined display fields.
     * Use this if repository returns separate columns (full_name, avatar_url, movie_title).
     */
    public static MovieReviewDtos buildDto(
            // movie_review
            int id,
            int rating,              
            String comment,
            Date createdAt,

            // user
            String fullName,
            String email,

            // movie
            String title,
            String shortDescription,
            int durationMinutes,
            String genre
    ) {
        MovieReviewDtos dto = new MovieReviewDtos();

        // user models
        dto.setFull_name(fullName);
        dto.setEmail(email);

        // movie models
        dto.setTitle(title);
        dto.setShort_description(shortDescription);
        dto.setDuration_minutes(durationMinutes);
        dto.setGenre(genre);

        // movie review models
        dto.setId(id);
        dto.setRating(String.valueOf(rating)); // vì DTO của bạn là String rating
        dto.setComment(comment);
        dto.setCreated_at(createdAt);

        return dto;
    }

}
