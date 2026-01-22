package CinemaBooking.Group2.dtos.showtime;

import java.time.LocalDate;

public class ShowtimeFilterDtos {
	private int cinemaId;
	private int movieId;
	private LocalDate date;
	public int getCinemaId() {
		return cinemaId;
	}
	public void setCinemaId(int cinemaId) {
		this.cinemaId = cinemaId;
	}
	public int getMovieId() {
		return movieId;
	}
	public void setMovieId(int movieId) {
		this.movieId = movieId;
	}
	public LocalDate getDate() {
		return date;
	}
	public void setDate(LocalDate date) {
		this.date = date;
	}
}
