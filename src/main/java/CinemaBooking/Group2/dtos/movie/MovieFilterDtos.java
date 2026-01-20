package CinemaBooking.Group2.dtos.movie;

import CinemaBooking.Group2.models.Movie;
import CinemaBooking.Group2.models.Movie.MovieStatus;

public class MovieFilterDtos {
	private String keyword;
    private Movie.MovieStatus status;
    private String genre; 
    private String language;
	public String getKeyword() {
		return keyword;
	}
	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}
	public Movie.MovieStatus getStatus() {
		return status;
	}
	public void setStatus(Movie.MovieStatus status) {
		this.status = status;
	}
	public String getGenre() {
		return genre;
	}
	public void setGenre(String genre) {
		this.genre = genre;
	}
	public String getLanguage() {
		return language;
	}
	public void setLanguage(String language) {
		this.language = language;
	}
	public MovieFilterDtos() {
		super();
		// TODO Auto-generated constructor stub
	}
	public MovieFilterDtos(String keyword, MovieStatus status, String genre, String language) {
		super();
		this.keyword = keyword;
		this.status = status;
		this.genre = genre;
		this.language = language;
	}
   
}
