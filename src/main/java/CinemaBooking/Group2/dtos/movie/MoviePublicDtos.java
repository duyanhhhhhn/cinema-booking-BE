package CinemaBooking.Group2.dtos.movie;

import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;

import CinemaBooking.Group2.models.Movie;
import CinemaBooking.Group2.models.Movie.MovieGenre;
import CinemaBooking.Group2.models.Movie.MovieStatus;

public class MoviePublicDtos {
    private int id;
    private String title;
    private String posterUrl;
    private Movie.MovieGenre genre;
    private Integer durationMinutes;
    private Movie.MovieStatus status;
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getPosterUrl() {
		return posterUrl;
	}
	public void setPosterUrl(String posterUrl) {
		this.posterUrl = posterUrl;
	}
	public Movie.MovieGenre getGenre() {
		return genre;
	}
	public void setGenre(Movie.MovieGenre genre) {
		this.genre = genre;
	}
	public Integer getDurationMinutes() {
		return durationMinutes;
	}
	public void setDurationMinutes(Integer durationMinutes) {
		this.durationMinutes = durationMinutes;
	}
	public Movie.MovieStatus getStatus() {
		return status;
	}
	public void setStatus(Movie.MovieStatus status) {
		this.status = status;
	}
}
