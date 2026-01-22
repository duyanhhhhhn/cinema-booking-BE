package CinemaBooking.Group2.dtos.showtime;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class ShowtimeDetailDtos {
    private int showtimeId;

    private int movieId;
    private String movieTitle;
    private String movieStatus;
    private int durationMinutes;

    private int roomId;
    private String roomName;

    private int cinemaId;
    private String cinemaName;
    private String cinemaAddress;

    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private BigDecimal basePrice;
    private String showtimeStatus;

    private List<ShowtimeSeatItemDtos> seats;

	public int getShowtimeId() {
		return showtimeId;
	}

	public void setShowtimeId(int showtimeId) {
		this.showtimeId = showtimeId;
	}

	public int getMovieId() {
		return movieId;
	}

	public void setMovieId(int movieId) {
		this.movieId = movieId;
	}

	public String getMovieTitle() {
		return movieTitle;
	}

	public void setMovieTitle(String movieTitle) {
		this.movieTitle = movieTitle;
	}

	public String getMovieStatus() {
		return movieStatus;
	}

	public void setMovieStatus(String movieStatus) {
		this.movieStatus = movieStatus;
	}

	public int getDurationMinutes() {
		return durationMinutes;
	}

	public void setDurationMinutes(int durationMinutes) {
		this.durationMinutes = durationMinutes;
	}

	public int getRoomId() {
		return roomId;
	}

	public void setRoomId(int roomId) {
		this.roomId = roomId;
	}

	public String getRoomName() {
		return roomName;
	}

	public void setRoomName(String roomName) {
		this.roomName = roomName;
	}

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

	public String getCinemaAddress() {
		return cinemaAddress;
	}

	public void setCinemaAddress(String cinemaAddress) {
		this.cinemaAddress = cinemaAddress;
	}

	public LocalDateTime getStartTime() {
		return startTime;
	}

	public void setStartTime(LocalDateTime startTime) {
		this.startTime = startTime;
	}

	public LocalDateTime getEndTime() {
		return endTime;
	}

	public void setEndTime(LocalDateTime endTime) {
		this.endTime = endTime;
	}

	public BigDecimal getBasePrice() {
		return basePrice;
	}

	public void setBasePrice(BigDecimal basePrice) {
		this.basePrice = basePrice;
	}

	public String getShowtimeStatus() {
		return showtimeStatus;
	}

	public void setShowtimeStatus(String showtimeStatus) {
		this.showtimeStatus = showtimeStatus;
	}

	public List<ShowtimeSeatItemDtos> getSeats() {
		return seats;
	}

	public void setSeats(List<ShowtimeSeatItemDtos> seats) {
		this.seats = seats;
	}

}
