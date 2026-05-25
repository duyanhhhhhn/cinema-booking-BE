package CinemaBooking.Group2.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import CinemaBooking.Group2.repositories.BookingSeatRepository;
import CinemaBooking.Group2.repositories.SeatHoldRepository;
import CinemaBooking.Group2.repositories.ShowtimeRepository;

/**
 * Service for handling seat hold operations with race condition prevention
 */
@Service
public class SeatHoldService {

    @Autowired
    private SeatHoldRepository seatHoldRepository;

    @Autowired
    private BookingSeatRepository bookingSeatRepository;

    @Autowired
    private ShowtimeRepository showtimeRepository;

    // Hold duration in minutes
    private static final int HOLD_DURATION_MINUTES = 10;

    /**
     * Hold multiple seats for a user with race condition handling
     * Returns lists of successfully held and failed seat IDs
     */
    @Transactional
    public HoldResult holdSeats(int showtimeId, List<Integer> seatIds, int userId) {
        HoldResult result = new HoldResult();
        
        // Validate showtime exists
        if (!showtimeRepository.isBookable(showtimeId)) {
            result.setSuccess(false);
            result.setMessage("Showtime không khả dụng");
            return result;
        }

        String holdToken = UUID.randomUUID().toString();
        LocalDateTime expiresAt = LocalDateTime.now().plusMinutes(HOLD_DURATION_MINUTES);
        
        List<Integer> successfulHolds = new ArrayList<>();
        List<Integer> failedHolds = new ArrayList<>();

        for (Integer seatId : seatIds) {
            // Check if seat is already sold
            if (bookingSeatRepository.isSeatSold(showtimeId, seatId)) {
                failedHolds.add(seatId);
                continue;
            }

            // Check if already held by this user (allow re-hold)
            if (seatHoldRepository.isSeatHeldByUser(showtimeId, seatId, userId)) {
                successfulHolds.add(seatId);
                continue;
            }

            // Try to hold the seat (atomic operation with race condition handling)
            boolean held = seatHoldRepository.holdSeat(showtimeId, seatId, userId, holdToken, expiresAt);
            
            if (held) {
                successfulHolds.add(seatId);
            } else {
                failedHolds.add(seatId);
            }
        }

        result.setSuccess(!successfulHolds.isEmpty());
        result.setHoldToken(holdToken);
        result.setExpiresAt(expiresAt);
        result.setSuccessfulSeatIds(successfulHolds);
        result.setFailedSeatIds(failedHolds);
        
        if (successfulHolds.isEmpty()) {
            result.setMessage("All seats are unavailable");
        } else if (failedHolds.isEmpty()) {
            result.setMessage("All seats held successfully");
        } else {
            result.setMessage("Some seats held successfully, others unavailable");
        }

        return result;
    }

    /**
     * Release seats held by a user
     */
    @Transactional
    public ReleaseResult releaseSeats(int showtimeId, List<Integer> seatIds, int userId) {
        ReleaseResult result = new ReleaseResult();
        List<Integer> releasedSeats = new ArrayList<>();

        for (Integer seatId : seatIds) {
            boolean released = seatHoldRepository.releaseSeat(showtimeId, seatId, userId);
            if (released) {
                releasedSeats.add(seatId);
            }
        }

        result.setSuccess(!releasedSeats.isEmpty());
        result.setReleasedSeatIds(releasedSeats);
        
        if (releasedSeats.isEmpty()) {
            result.setMessage("No seats were released (not held by user or expired)");
        } else {
            result.setMessage(releasedSeats.size() + " seat(s) released successfully");
        }

        return result;
    }

    /**
     * Release seats by hold token
     */
    public boolean releaseSeatsByToken(String holdToken) {
        return seatHoldRepository.releaseSeatByToken(holdToken);
    }

    /**
     * Clean up expired holds (can be called by scheduled job)
     */
    public int cleanupExpiredHolds() {
        return seatHoldRepository.cleanupExpiredHolds();
    }

    // Inner classes for results
    public static class HoldResult {
        private boolean success;
        private String message;
        private String holdToken;
        private LocalDateTime expiresAt;
        private List<Integer> successfulSeatIds;
        private List<Integer> failedSeatIds;

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

        public List<Integer> getSuccessfulSeatIds() {
            return successfulSeatIds;
        }

        public void setSuccessfulSeatIds(List<Integer> successfulSeatIds) {
            this.successfulSeatIds = successfulSeatIds;
        }

        public List<Integer> getFailedSeatIds() {
            return failedSeatIds;
        }

        public void setFailedSeatIds(List<Integer> failedSeatIds) {
            this.failedSeatIds = failedSeatIds;
        }
    }

    public static class ReleaseResult {
        private boolean success;
        private String message;
        private List<Integer> releasedSeatIds;

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
}
