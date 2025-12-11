package CinemaBooking.Group2.mappers;

import CinemaBooking.Group2.dtos.movie.MovieResponse;
import CinemaBooking.Group2.models.Movie;

public class MovieMapper {
	// Mapping data của dto với lại models nha cac anh trả về cho FE
    public static MovieResponse toResponseDto(Movie movie) {
        if (movie == null) {
            return null;
        }

        MovieResponse dto = new MovieResponse();
        dto.setId(movie.getId());
        dto.setTitle(movie.getTitle());
        dto.setShortDescription(movie.getShortDescription());
        dto.setDurationMinutes(movie.getDurationMinutes());
        if (movie.getStatus() != null) {
            dto.setStatus(
                MovieResponse.MovieStatus.valueOf(movie.getStatus().name())
            );
        }

        return dto;
    }
}
