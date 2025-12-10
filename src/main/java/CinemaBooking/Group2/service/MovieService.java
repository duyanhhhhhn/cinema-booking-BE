package CinemaBooking.Group2.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import CinemaBooking.Group2.dto.movie.MovieResponse;
import CinemaBooking.Group2.mapper.MovieMapper;
import CinemaBooking.Group2.models.Movie;
import CinemaBooking.Group2.repositories.MovieRepository;

@Service
public class MovieService {
	@Autowired
	private MovieRepository movieRepository;
	
	// lấy toàn bộ movie để trả về dto, từ dto trả về cho phía controller, controller trả về FE
	public List<MovieResponse> getAllMovie() {
		try {
			List<Movie> movies = movieRepository.getAllMovie();
			return movies.stream()
						 .map(MovieMapper::toResponseDto)
						 .collect(Collectors.toList());
		}
		catch (Exception e) {
		   throw new RuntimeException("Lỗi khi lấy danh sách phim, kiểm tra lại repository", e);
		}
	}
	public List<MovieResponse> getAllMovieStatus() {
		try {
			List<Movie> movies = movieRepository.getAllMovieCommingSoon();
			return movies.stream()
						 .map(MovieMapper::toResponseDto)
						 .collect(Collectors.toList());
		}
		catch (Exception e) {
		   throw new RuntimeException("Lỗi khi lấy danh sách phim, kiểm tra lại repository", e);
		}
	}
}
 