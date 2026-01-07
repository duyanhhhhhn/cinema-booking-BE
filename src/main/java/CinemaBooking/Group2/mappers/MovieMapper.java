package CinemaBooking.Group2.mappers;

import CinemaBooking.Group2.dtos.movie.MovieCreateDtos;
import CinemaBooking.Group2.dtos.movie.MovieDetailDtos;
import CinemaBooking.Group2.dtos.movie.MovieDtos;
import CinemaBooking.Group2.dtos.movie.MovieEditDtos;
import CinemaBooking.Group2.models.Movie;

public class MovieMapper {

    public static MovieDtos toResponseDto(Movie movie) {
        if (movie == null) return null;

        MovieDtos dto = new MovieDtos();
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
        dto.setStatus(movie.getStatus());
        dto.setCreatedAt(movie.getCreatedAt());
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
        dto.setStatus(movie.getStatus());
        dto.setCreatedAt(movie.getCreatedAt());
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

        movie.setTrailerUrl(dto.getTrailerUrl());
        movie.setReleaseDate(dto.getReleaseDate());
        movie.setEndDate(dto.getEndDate());

        if (dto.getStatus() != null) {
            movie.setStatus(dto.getStatus());
        }

        return movie;
    }

    public static Movie toModelEdit(MovieEditDtos data) {
        if (data == null) return null;

        Movie movie = new Movie();
        movie.setTitle(data.getTitle());
        movie.setShortDescription(data.getShortDescription());
        movie.setDescription(data.getDescription());
        movie.setDurationMinutes(data.getDurationMinutes());
        movie.setGenre(data.getGenre());
        movie.setLanguage(data.getLanguage());
        movie.setFormat(data.getFormat());
        movie.setDirector(data.getDirector());
        movie.setCast(data.getCast());

        movie.setTrailerUrl(data.getTrailerUrl());
        movie.setReleaseDate(data.getReleaseDate());
        movie.setEndDate(data.getEndDate());

        // STATUS: CHO PHÉP NULL ĐỂ SERVICE QUYẾT ĐỊNH DEFAULT HOẶC GIỮ NGUYÊN THEO LOGIC CỦA BẠN.
        movie.setStatus(data.getStatus());

        return movie;
    }

}
