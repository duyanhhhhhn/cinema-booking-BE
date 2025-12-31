package CinemaBooking.Group2.dtos.seat;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public class ReleaseSeatRequestDTO {
    
    @NotNull(message = "Showtime ID is required")
    private Integer showtimeId;
    
    @NotEmpty(message = "Seat IDs cannot be empty")
    private List<Integer> seatIds;
    
    public ReleaseSeatRequestDTO() {
    }
    
    public ReleaseSeatRequestDTO(Integer showtimeId, List<Integer> seatIds) {
        this.showtimeId = showtimeId;
        this.seatIds = seatIds;
    }

    public Integer getShowtimeId() {
        return showtimeId;
    }

    public void setShowtimeId(Integer showtimeId) {
        this.showtimeId = showtimeId;
    }

    public List<Integer> getSeatIds() {
        return seatIds;
    }

    public void setSeatIds(List<Integer> seatIds) {
        this.seatIds = seatIds;
    }
}
