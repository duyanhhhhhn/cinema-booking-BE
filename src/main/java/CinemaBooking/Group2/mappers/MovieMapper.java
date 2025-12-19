package CinemaBooking.Group2.mappers;

import CinemaBooking.Group2.dtos.movie.MovieCreateDtos;
import CinemaBooking.Group2.dtos.movie.MovieDeleteDtos;
import CinemaBooking.Group2.dtos.movie.MovieDetailDtos;
import CinemaBooking.Group2.dtos.movie.MovieResponse;
import CinemaBooking.Group2.models.Movie;
import CinemaBooking.Group2.models.Movie.MovieStatus;

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
    
    public static MovieDetailDtos toDetailDto(Movie movie) {
        if (movie == null) return null;

        MovieDetailDtos dto = new MovieDetailDtos();
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

        // Convert enum from Model to DTO enum (different enum types)
        if (movie.getStatus() != null) {
            dto.setStatus(MovieDetailDtos.MovieStatus.valueOf(movie.getStatus().name()));
        }

        return dto;
    }
    public static Movie toModel(MovieCreateDtos dto) {
        if (dto == null) return null;

        Movie movie = new Movie();
        movie.setTitle(dto.getTitle());
        movie.setShortDescription(dto.getShortDescription());
        movie.setDescription(dto.getDescription());
        movie.setDurationMinutes(dto.getDurationMinutes());

        movie.setGenre(dto.getGenre());
        movie.setLanguage(dto.getLanguage());
        movie.setFormat(dto.getFormat());
        movie.setDirector(dto.getDirector());
        movie.setCast(dto.getCast());

        movie.setPosterUrl(dto.getPosterUrl());
        movie.setBannerUrl(dto.getBannerUrl());
        movie.setTrailerUrl(dto.getTrailerUrl());

        movie.setReleaseDate(dto.getReleaseDate());
        movie.setEndDate(dto.getEndDate());

        // status is same enum type (Movie.MovieStatus) in DTO now
        movie.setStatus(dto.getStatus());

        return movie;
    }
    
    public static int toId(MovieDeleteDtos dto) {
    	return dto == null ? 0 : dto.getId();
    }

}
