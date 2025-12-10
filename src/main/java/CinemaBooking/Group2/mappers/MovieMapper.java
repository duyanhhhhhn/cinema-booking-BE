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
        dto.setDescription(movie.getDescription());
        dto.setDurationMinutes(movie.getDurationMinutes());
        dto.setGenre(movie.getGenre());
        dto.setLanguage(movie.getLanguage());
        dto.setFormat(movie.getFormat());
        dto.setDirector(movie.getDirector());
        dto.setCast(movie.getCast());
        dto.setPosterUrl(movie.getPosterUrl());
        dto.setBannerUrl(movie.getBannerUrl());
        dto.setTrailerUrl(movie.getTrailerUrl());
        dto.setReleaseDate(movie.getReleaseDate());
        dto.setEndDate(movie.getEndDate());
        dto.setCreatedAt(movie.getCreatedAt());
        if (movie.getStatus() != null) {
            dto.setStatus(
                MovieResponse.MovieStatus.valueOf(movie.getStatus().name())
            );
        }

        return dto;
    }
}
