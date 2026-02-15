package CinemaBooking.Group2.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import CinemaBooking.Group2.repositories.BookingRepository;

/**
 * Scheduled task để tự động hủy các booking PENDING quá 5 phút
 */
@Component
public class BookingScheduler {

    private static final Logger logger = LoggerFactory.getLogger(BookingScheduler.class);

    @Autowired
    private BookingRepository bookingRepository;

    /**
     * Chạy mỗi phút để kiểm tra và hủy booking PENDING quá 5 phút
     * Cron: 0 * * * * * = Chạy vào giây 0 của mỗi phút
     */
    @Scheduled(cron = "0 * * * * *")
    public void cancelExpiredPendingBookings() {
        try {
            logger.debug("Running scheduled task to cancel expired pending bookings...");
            
            int canceledCount = bookingRepository.cancelExpiredPendingBookings(5);
            
            if (canceledCount > 0) {
                logger.info("Canceled {} expired pending booking(s)", canceledCount);
            }
        } catch (Exception e) {
            logger.error("Error while canceling expired pending bookings: {}", e.getMessage(), e);
        }
    }
}
