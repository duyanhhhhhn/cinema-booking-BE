package CinemaBooking.Group2.dtos.showtime;

import java.util.List;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonProperty;

import CinemaBooking.Group2.dtos.seat.SeatRowDTO;

public class ShowtimeSeatResponseDTO {
    private int showtimeId;
    private String cinemaName;
    @JsonProperty("seatMap")
    private List<SeatRowDTO> rows;

    private String movieTitle;
    private String moviePosterUrl;
    private String genre;
    private int duration;
    private String roomName;
    private String fullAddress;
    private LocalDateTime startTime;

    /**
     * @return the showtimeId
     */
    public int getShowtimeId() {
        return showtimeId;
    }

    /**
     * @param showtimeId the showtimeId to set
     */
    public void setShowtimeId(int showtimeId) {
        this.showtimeId = showtimeId;
    }

    /**
     * @return the cinemaName
     */
    public String getCinemaName() {
        return cinemaName;
    }

    /**
     * @param cinemaName the cinemaName to set
     */
    public void setCinemaName(String cinemaName) {
        this.cinemaName = cinemaName;
    }

    /**
     * @return the rows
     */
    public List<SeatRowDTO> getRows() {
        return rows;
    }

    /**
     * @param rows the rows to set
     */
    public void setRows(List<SeatRowDTO> rows) {
        this.rows = rows;
    }

    // Getters and setters for new fields
    public String getMovieTitle() {
        return movieTitle;
    }

    public void setMovieTitle(String movieTitle) {
        this.movieTitle = movieTitle;
    }

    public String getMoviePosterUrl() {
        return moviePosterUrl;
    }

    public void setMoviePosterUrl(String moviePosterUrl) {
        this.moviePosterUrl = moviePosterUrl;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    public String getFullAddress() {
        return fullAddress;
    }

    public void setFullAddress(String fullAddress) {
        this.fullAddress = fullAddress;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    /**
     * @param showtimeId
     * @param cinemaName
     * @param rows
     */
    public ShowtimeSeatResponseDTO(int showtimeId, String cinemaName, List<SeatRowDTO> rows) {
        super();
        this.showtimeId = showtimeId;
        this.cinemaName = cinemaName;
        this.rows = rows;
    }

    /**
     * Full constructor including new fields
     */
    public ShowtimeSeatResponseDTO(int showtimeId, String movieTitle, String moviePosterUrl, String genre, int duration,
            String cinemaName, String roomName, String fullAddress, LocalDateTime startTime, List<SeatRowDTO> rows) {
        super();
        this.showtimeId = showtimeId;
        this.movieTitle = movieTitle;
        this.moviePosterUrl = moviePosterUrl;
        this.genre = genre;
        this.duration = duration;
        this.cinemaName = cinemaName;
        this.roomName = roomName;
        this.fullAddress = fullAddress;
        this.startTime = startTime;
        this.rows = rows;
    }

    /**
     * 
     */
    public ShowtimeSeatResponseDTO() {
        super();
        // TODO Auto-generated constructor stub
    }

}