package CinemaBooking.Group2.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import CinemaBooking.Group2.service.SeatBookingFacade;

/**
 * Scheduled tasks for seat management
 */
@Component
public class SeatScheduledTasks {

    private static final Logger logger = LoggerFactory.getLogger(SeatScheduledTasks.class);

    @Autowired
    private SeatBookingFacade seatBookingFacade;

    /**
     * Clean up expired seat holds every minute
     * This ensures seats are released automatically after expiry
     */
    @Scheduled(fixedRate = 60000) // Run every 60 seconds
    public void cleanupExpiredHolds() {
        try {
            int cleaned = seatBookingFacade.cleanupExpiredHolds();
            if (cleaned > 0) {
                logger.info("Cleaned up {} expired seat holds", cleaned);
            }
        } catch (Exception e) {
            logger.error("Error cleaning up expired seat holds", e);
        }
    }
}
