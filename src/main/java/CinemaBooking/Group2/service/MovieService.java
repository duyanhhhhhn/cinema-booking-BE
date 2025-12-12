package CinemaBooking.Group2.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import CinemaBooking.Group2.dtos.movie.MovieDetailDtos;
import CinemaBooking.Group2.dtos.movie.MovieResponse;
import CinemaBooking.Group2.mappers.MovieMapper;
import CinemaBooking.Group2.models.Movie;
import CinemaBooking.Group2.repositories.MovieRepository;

@Service
public class MovieService {

    @Autowired
    private MovieRepository movieRepository;

    /**
     * Retrieve all movies from the database and convert them into MovieResponse DTOs.
     *
     * Purpose:
     * - Repository layer returns Model objects (Movie).
     * - Service layer maps Model objects to DTO objects (MovieResponse).
     * - Controller returns DTO objects to the client (FE).
     *
     * Why mapping here:
     * - Keeps the Controller clean.
     * - Centralizes transformation logic in one place.
     *
     * @return list of MovieResponse DTOs
     */
    public List<MovieResponse> getAllMovie() {
        try {
            List<Movie> movies = movieRepository.getAllMovie();
            return movies.stream()
                    .map(MovieMapper::toResponseDto)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new RuntimeException("Failed to fetch movie list. Please check repository/database.", e);
        }
    }

    /**
     * Retrieve movies that are publicly visible based on status:
     * - COMING_SOON
     * - NOW_SHOWING
     *
     * This method supports pagination:
     * - page starts from 1
     * - perPage is the number of records per page
     *
     * Important:
     * - Pagination must be done in the Repository using LIMIT/OFFSET.
     * - Service simply passes page/perPage and maps the result to DTO.
     *
     * @param page    current page number (1-based index)
     * @param perPage number of items per page
     * @return list of MovieResponse DTOs for the requested page
     */
    public List<MovieResponse> getAllMovieStatus(int page, int perPage) {
        try {
            List<Movie> movies = movieRepository.getAllMovieCommingSoon(page, perPage);
            return movies.stream()
                    .map(MovieMapper::toResponseDto)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new RuntimeException("Failed to fetch movies by status with pagination.", e);
        }
    }

    /**
     * Count the total number of movies that match the public statuses:
     * - COMING_SOON
     * - NOW_SHOWING
     *
     * This total is used for the "meta" section in API response:
     * meta: { page, total, perPage }
     *
     * Why separate query:
     * - The paginated SELECT query only returns one page of data.
     * - The client needs total to know how many pages exist.
     *
     * @return total number of matching movies
     */
    public int countMovieStatus() {
        try {
            return movieRepository.countMovieComingSoonNowShowing();
        } catch (Exception e) {
            throw new RuntimeException("Failed to count movies by status.", e);
        }
    }
    
    /**
     * Get movie detail by id and convert it to MovieDetailDtos.
     *
     * Responsibilities:
     * - Call repository to fetch Movie model from database.
     * - If not found, return null (Controller will decide to return 404).
     * - Convert Movie model to MovieDetailDtos for API output.
     *
     * Why DTO:
     * - Prevent exposing internal fields that FE does not need.
     * - Keep API response stable even if DB/model changes.
     *
     * @param id movie id
     * @return MovieDetailDtos if found, otherwise null
     */
    public MovieDetailDtos getMovieDetailById(int id) {
        try {
            Movie movie = movieRepository.getMovieDetailById(id);
            if (movie == null) {
                return null;
            }
            return MovieMapper.toDetailDto(movie);
        } catch (Exception e) {
            throw new RuntimeException("Failed to get movie detail in service layer.", e);
        }
    }

}
