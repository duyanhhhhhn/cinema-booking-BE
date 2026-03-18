package CinemaBooking.Group2.models;

import java.util.Date;

public class Movie {

	public enum MovieStatus {
	    COMING_SOON,
	    NOW_SHOWING,
	    ENDED
	}
	
	public enum MovieGenre {
	    ACTION,
	    COMEDY,
	    ROMANCE,
	    DRAMA,
	    HORROR,
	    THRILLER,
	    SCI_FI,
	    FANTASY,
	    ANIMATION,
	    ADVENTURE,
	    CRIME,
	    WAR,
	    FAMILY,
	    MUSIC,
	    DOCUMENTARY,
	    MYSTERY
	}

	private int id;
    private String title;
    private String shortDescription;
    private String description;
    private int durationMinutes;
    private MovieGenre genre;
    private String language;
    private String format;
    private String director;
    private String cast;
    private String posterUrl;
    private String bannerUrl;
    private String trailerUrl;
    private String ageating;
    private Date releaseDate;
    private Date endDate;
    private MovieStatus status; 
    private Date createdAt;
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getAgerating() {
		return ageating;
	}
	public void setAgerating(String ageRating) {
		this.ageating = ageRating;
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
	public Movie(String title, String shortDescription, String description, int durationMinutes, MovieGenre genre,
			String language, String format, String director, String cast, String posterUrl, String bannerUrl,
			String trailerUrl, Date releaseDate , String ageRating , Date endDate, MovieStatus status) {
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
		this.ageating = ageRating;
		this.endDate = endDate;
		this.status = status;
	}
	public MovieStatus getStatus() {
		return status;
	}
	public void setStatus(MovieStatus status) {
		this.status = status;
	}
	public Date getCreatedAt() {
		return createdAt;
	}
	public void setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
	}
	/**
	 * @param id
	 * @param title
	 * @param shortDescription
	 * @param description
	 * @param durationMinutes
	 * @param genre
	 * @param language
	 * @param format
	 * @param director
	 * @param cast
	 * @param posterUrl
	 * @param bannerUrl
	 * @param trailerUrl
	 * @param releaseDate
	 * @param endDate
	 * @param status
	 * @param createdAt
	 */
	public Movie(int id, String title, String shortDescription, String description, int durationMinutes, MovieGenre genre,
			String language, String format, String director, String cast, String posterUrl, String bannerUrl,
			String trailerUrl, Date releaseDate, String ageRating ,Date endDate, MovieStatus status, Date createdAt) {
		super();
		this.id = id;
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
		this.ageating = ageRating;
		this.endDate = endDate;
		this.status = status;
		this.createdAt = createdAt;
	}
	/**
	 * 
	 */
	public Movie() {
		super();
		// TODO Auto-generated constructor stub
	}
	public void setReleaseDate(Object releaseDate2) {
		// TODO Auto-generated method stub
		
	}
    
    

}
