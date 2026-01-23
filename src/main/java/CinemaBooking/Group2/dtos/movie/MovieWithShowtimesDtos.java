package CinemaBooking.Group2.dtos.movie;

import java.util.List;

import CinemaBooking.Group2.dtos.showtime.ShowtimeItemDtos;

public class MovieWithShowtimesDtos {
    private int cinemaId;
    private String cinemaName;
    private String address;

    private String posterUrl;
    private String movieTitle;
    private String movieGenre;
    private Integer durationMinutes;

    private List<ShowtimeItemDtos> showtimes;

    public MovieWithShowtimesDtos() {}

    public int getCinemaId() {
        return cinemaId;
    }

    public void setCinemaId(int cinemaId) {
        this.cinemaId = cinemaId;
    }

    public String getCinemaName() {
        return cinemaName;
    }

    public void setCinemaName(String cinemaName) {
        this.cinemaName = cinemaName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPosterUrl() {
        return posterUrl;
    }

    public void setPosterUrl(String posterUrl) {
        this.posterUrl = posterUrl;
    }

    public String getMovieTitle() {
        return movieTitle;
    }

    public void setMovieTitle(String movieTitle) {
        this.movieTitle = movieTitle;
    }

    public String getMovieGenre() {
        return movieGenre;
    }

    public void setMovieGenre(String movieGenre) {
        this.movieGenre = movieGenre;
    }

    public Integer getDurationMinutes() {
        return durationMinutes;
    }

    public void setDurationMinutes(Integer durationMinutes) {
        this.durationMinutes = durationMinutes;
    }

    public List<ShowtimeItemDtos> getShowtimes() {
        return showtimes;
    }

    public void setShowtimes(List<ShowtimeItemDtos> showtimes) {
        this.showtimes = showtimes;
    }
}
