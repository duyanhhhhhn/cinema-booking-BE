package CinemaBooking.Group2.dtos.seat;

import java.util.List;

public class ReleaseSeatResponseDTO {
    
    private boolean success;
    private String message;
    private List<Integer> releasedSeatIds;
    
    public ReleaseSeatResponseDTO() {
    }
    
    public ReleaseSeatResponseDTO(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<Integer> getReleasedSeatIds() {
        return releasedSeatIds;
    }

    public void setReleasedSeatIds(List<Integer> releasedSeatIds) {
        this.releasedSeatIds = releasedSeatIds;
    }
}
