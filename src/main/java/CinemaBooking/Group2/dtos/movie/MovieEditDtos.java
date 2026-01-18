package CinemaBooking.Group2.dtos.movie;

import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.multipart.MultipartFile;

import CinemaBooking.Group2.models.Movie;
import CinemaBooking.Group2.models.Movie.MovieGenre;
import CinemaBooking.Group2.models.Movie.MovieStatus;

public class MovieEditDtos {
	    private String title;
	    private String shortDescription;
	    private String description;
	    private Integer durationMinutes; 
	    private MovieGenre genre;
	    private String language;
	    private String format;
	    private String director;
	    private String cast;

	    private MultipartFile posterFile;
	    private MultipartFile bannerFile;

	    private String trailerUrl;
	    @DateTimeFormat(pattern = "yyyy-MM-dd")
	    private Date releaseDate;
	    @DateTimeFormat(pattern = "yyyy-MM-dd")
	    private Date endDate;

	    private Movie.MovieStatus status;

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

		public Integer getDurationMinutes() {
			return durationMinutes;
		}

		public void setDurationMinutes(Integer durationMinutes) {
			this.durationMinutes = durationMinutes;
		}

		public MovieGenre getGenre() {
			return genre;
		}

		public void setGenre(MovieGenre genre) {
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

		public MultipartFile getPosterFile() {
			return posterFile;
		}

		public void setPosterFile(MultipartFile posterFile) {
			this.posterFile = posterFile;
		}

		public MultipartFile getBannerFile() {
			return bannerFile;
		}

		public void setBannerFile(MultipartFile bannerFile) {
			this.bannerFile = bannerFile;
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

		public Movie.MovieStatus getStatus() {
			return status;
		}

		public void setStatus(Movie.MovieStatus status) {
			this.status = status;
		}

		public MovieEditDtos(String title, String shortDescription, String description, int durationMinutes,
				MovieGenre genre, String language, String format, String director, String cast, MultipartFile posterFile,
				MultipartFile bannerFile, String trailerUrl, Date releaseDate, Date endDate, MovieStatus status) {
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
			this.posterFile = posterFile;
			this.bannerFile = bannerFile;
			this.trailerUrl = trailerUrl;
			this.releaseDate = releaseDate;
			this.endDate = endDate;
			this.status = status;
		}

		public MovieEditDtos() {
			super();
			// TODO Auto-generated constructor stub
		}
		
}
