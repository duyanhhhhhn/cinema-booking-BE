package CinemaBooking.Group2.dtos.scheduler;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class AdminUpdateShowtimeReqDto {
    private int cinemaId;
    private int roomId;
    private int movieId;
    private LocalDateTime startAt;
    private BigDecimal basePrice;

    public int getCinemaId() { return cinemaId; }
    public void setCinemaId(int cinemaId) { this.cinemaId = cinemaId; }

    public int getRoomId() { return roomId; }
    public void setRoomId(int roomId) { this.roomId = roomId; }

    public int getMovieId() { return movieId; }
    public void setMovieId(int movieId) { this.movieId = movieId; }

    public LocalDateTime getStartAt() { return startAt; }
    public void setStartAt(LocalDateTime startAt) { this.startAt = startAt; }

    public BigDecimal getBasePrice() { return basePrice; }
    public void setBasePrice(BigDecimal basePrice) { this.basePrice = basePrice; }
}