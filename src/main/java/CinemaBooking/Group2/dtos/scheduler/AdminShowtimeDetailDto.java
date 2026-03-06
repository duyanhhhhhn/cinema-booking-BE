package CinemaBooking.Group2.dtos.scheduler;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class AdminShowtimeDetailDto {
    private int id;

    private int cinemaId;
    private int roomId;
    private String roomName;
    private String roomType;

    private int movieId;
    private String movieTitle;
    private String posterUrl;
    private String movieFormat;
    private int durationMinutes;

    private LocalDateTime startAt;
    private LocalDateTime endAt;
    private BigDecimal basePrice;
    private String status;

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getCinemaId() { return cinemaId; }
    public void setCinemaId(int cinemaId) { this.cinemaId = cinemaId; }

    public int getRoomId() { return roomId; }
    public void setRoomId(int roomId) { this.roomId = roomId; }

    public String getRoomName() { return roomName; }
    public void setRoomName(String roomName) { this.roomName = roomName; }

    public String getRoomType() { return roomType; }
    public void setRoomType(String roomType) { this.roomType = roomType; }

    public int getMovieId() { return movieId; }
    public void setMovieId(int movieId) { this.movieId = movieId; }

    public String getMovieTitle() { return movieTitle; }
    public void setMovieTitle(String movieTitle) { this.movieTitle = movieTitle; }

    public String getPosterUrl() { return posterUrl; }
    public void setPosterUrl(String posterUrl) { this.posterUrl = posterUrl; }

    public String getMovieFormat() { return movieFormat; }
    public void setMovieFormat(String movieFormat) { this.movieFormat = movieFormat; }

    public int getDurationMinutes() { return durationMinutes; }
    public void setDurationMinutes(int durationMinutes) { this.durationMinutes = durationMinutes; }

    public LocalDateTime getStartAt() { return startAt; }
    public void setStartAt(LocalDateTime startAt) { this.startAt = startAt; }

    public LocalDateTime getEndAt() { return endAt; }
    public void setEndAt(LocalDateTime endAt) { this.endAt = endAt; }

    public BigDecimal getBasePrice() { return basePrice; }
    public void setBasePrice(BigDecimal basePrice) { this.basePrice = basePrice; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}