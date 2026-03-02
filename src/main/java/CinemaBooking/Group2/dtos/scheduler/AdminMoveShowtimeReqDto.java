package CinemaBooking.Group2.dtos.scheduler;

import java.time.LocalDateTime;

public class AdminMoveShowtimeReqDto {
    private int cinemaId;
    private int roomId;
    private LocalDateTime startAt;

    public int getCinemaId() { return cinemaId; }
    public void setCinemaId(int cinemaId) { this.cinemaId = cinemaId; }

    public int getRoomId() { return roomId; }
    public void setRoomId(int roomId) { this.roomId = roomId; }

    public LocalDateTime getStartAt() { return startAt; }
    public void setStartAt(LocalDateTime startAt) { this.startAt = startAt; }
}