package CinemaBooking.Group2.dtos.movie;

import java.util.Date;

import CinemaBooking.Group2.models.Movie.MovieStatus;

public class MovieEditDtos {
    private String title;
    private String shortDescription;
    private String description;
    private int durationMinutes;
    private String genre;
    private String language;
    private String format;
    private String director;
    private String cast;
    private String posterUrl;
    private String bannerUrl;
    private String trailerUrl;
    private Date releaseDate;
    private Date endDate;
    private MovieStatus status;
    
	public MovieEditDtos() {
		super();
		// TODO Auto-generated constructor stub
	}
	public MovieEditDtos(String title, String shortDescription, String description, int durationMinutes, String genre,
			String language, String format, String director, String cast, String posterUrl, String bannerUrl,
			String trailerUrl, Date releaseDate, Date endDate, MovieStatus status) {
		super();
		this.title = title;
		this.shortDescription = shortDescription;
		this.description = description;
		this.durationMinutes = durationMinutes;
		this.genre = genre;
		this.language = language;
		this.format = format;
		this.director = director;
		this.cast = cast;
		this.posterUrl = posterUrl;
		this.bannerUrl = bannerUrl;
		this.trailerUrl = trailerUrl;
		this.releaseDate = releaseDate;
		this.endDate = endDate;
		this.status = status;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getShortDescription() {
		return shortDescription;
	}
	public void setShortDescription(String shortDescription) {
		this.shortDescription = shortDescription;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public int getDurationMinutes() {
		return durationMinutes;
	}
	public void setDurationMinutes(int durationMinutes) {
		this.durationMinutes = durationMinutes;
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
	public String getFormat() {
		return format;
	}
	public void setFormat(String format) {
		this.format = format;
	}
	public String getDirector() {
		return director;
	}
	public void setDirector(String director) {
		this.director = director;
	}
	public String getCast() {
		return cast;
	}
	public void setCast(String cast) {
		this.cast = cast;
	}
	public String getPosterUrl() {
		return posterUrl;
	}
	public void setPosterUrl(String posterUrl) {
		this.posterUrl = posterUrl;
	}
	public String getBannerUrl() {
		return bannerUrl;
	}
	public void setBannerUrl(String bannerUrl) {
		this.bannerUrl = bannerUrl;
	}
	public String getTrailerUrl() {
		return trailerUrl;
	}
	public void setTrailerUrl(String trailerUrl) {
		this.trailerUrl = trailerUrl;
	}
	public Date getReleaseDate() {
		return releaseDate;
	}
	public void setReleaseDate(Date releaseDate) {
		this.releaseDate = releaseDate;
	}
	public Date getEndDate() {
		return endDate;
	}
	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}
	public MovieStatus getStatus() {
		return status;
	}
	public void setStatus(MovieStatus status) {
		this.status = status;
	} 
}
