package CinemaBooking.Group2.dtos.movie;

import java.util.Date;

import CinemaBooking.Group2.models.Movie;
import CinemaBooking.Group2.models.Movie.MovieGenre;

public class MovieCardtos {
    private int id;
    private String title;
    private int durationMinutes;
    private MovieGenre genre;
    private String posterUrl;
    private Movie.MovieStatus status;
    private Date releaseDate;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
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

    public String getPosterUrl() {
        return posterUrl;
    }

    public void setPosterUrl(String posterUrl) {
        this.posterUrl = posterUrl;
    }

    public Movie.MovieStatus getStatus() {
        return status;
    }

    public void setStatus(Movie.MovieStatus status) {
        this.status = status;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Date getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(Date releaseDate) {
        this.releaseDate = releaseDate;
    }
}