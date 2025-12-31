package CinemaBooking.Group2.dtos.seat;

import java.time.LocalDateTime;
import java.util.List;

public class HoldSeatResponseDTO {
    
    private boolean success;
    private String message;
    private String holdToken;
    private LocalDateTime expiresAt;
    private List<Integer> heldSeatIds;
    private List<Integer> failedSeatIds;
    
    public HoldSeatResponseDTO() {
    }
    
    public HoldSeatResponseDTO(boolean success, String message) {
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

    public String getHoldToken() {
        return holdToken;
    }

    public void setHoldToken(String holdToken) {
        this.holdToken = holdToken;
    }

    public LocalDateTime getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(LocalDateTime expiresAt) {
        this.expiresAt = expiresAt;
    }

    public List<Integer> getHeldSeatIds() {
        return heldSeatIds;
    }

    public void setHeldSeatIds(List<Integer> heldSeatIds) {
        this.heldSeatIds = heldSeatIds;
    }

    public List<Integer> getFailedSeatIds() {
        return failedSeatIds;
    }

    public void setFailedSeatIds(List<Integer> failedSeatIds) {
        this.failedSeatIds = failedSeatIds;
    }
}