package CinemaBooking.Group2.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import CinemaBooking.Group2.dtos.movie.MovieCreateDtos;
import CinemaBooking.Group2.dtos.movie.MovieDetailDtos;
import CinemaBooking.Group2.dtos.movie.MovieResponse;
import CinemaBooking.Group2.mappers.MovieMapper;
import CinemaBooking.Group2.models.Movie;
import CinemaBooking.Group2.repositories.MovieRepository;

@Service
public class MovieService {

    @Autowired
    private MovieRepository movieRepository;

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

    public int countMovieStatus() {
        try {
            return movieRepository.countMovieComingSoonNowShowing();
        } catch (Exception e) {
            throw new RuntimeException("Failed to count movies by status.", e);
        }
    }
    
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
    
    public boolean createNewMovie(MovieCreateDtos dto) {
        try {
            // 0) Null request
            if (dto == null) {
                System.out.println("[CREATE_MOVIE] dto is null");
                return false;
            }

            // 1) Validate title
            String title = dto.getTitle();
            if (title == null || title.trim().isEmpty()) {
                System.out.println("[CREATE_MOVIE] invalid title: " + title);
                return false;
            }

            // 2) Validate duration
            int duration = dto.getDurationMinutes();
            if (duration <= 0) {
                System.out.println("[CREATE_MOVIE] invalid durationMinutes: " + duration);
                return false;
            }

            // (optional) log important fields
            System.out.println("[CREATE_MOVIE] title=" + title);
            System.out.println("[CREATE_MOVIE] duration=" + duration);
            System.out.println("[CREATE_MOVIE] endDate=" + dto.getEndDate());

            // 3) Map DTO -> Model
            Movie movie = MovieMapper.toModel(dto);
            if (movie == null) {
                System.out.println("[CREATE_MOVIE] mapper returned null Movie");
                return false;
            }

            // 4) Default status
            if (movie.getStatus() == null) {
                movie.setStatus(Movie.MovieStatus.COMING_SOON);
            }

            // 5) Insert DB
            boolean inserted = movieRepository.createNewMovie(movie);
            System.out.println("[CREATE_MOVIE] repository inserted=" + inserted);
            return inserted;

        } catch (Exception e) {
            e.printStackTrace(); // để thấy lỗi thật nếu repository/mapper throw
            throw new RuntimeException("Failed to create new movie in service layer.", e);
        }
    }

    public boolean deleteMovie(int id) {
    	try {
    		if (id <= 0) return false;
    		return movieRepository.deleteMovieById(id);
    	} catch(Exception e) {
    		e.printStackTrace();
    		throw new RuntimeException("Failed to delete movie by id", e);
    	}
    }
}
