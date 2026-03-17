package CinemaBooking.Group2.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import CinemaBooking.Group2.dtos.showtime.ShowtimeDetailDtos;
import CinemaBooking.Group2.models.PriceAdjustment;
import CinemaBooking.Group2.repositories.BookingRepository;
import CinemaBooking.Group2.repositories.ShowtimeSheetMapRepositories;

@Service
public class ShowtimeSeatMapService {

    private static final Logger logger = LoggerFactory.getLogger(ShowtimeSeatMapService.class);

    private final ShowtimeSheetMapRepositories repo;
    
    @Autowired
    private BookingRepository bookingRepository;

    public ShowtimeSeatMapService(ShowtimeSheetMapRepositories repo) {
        this.repo = repo;
    }

    public ShowtimeDetailDtos getShowtimeDetailWithSeatMap(int showtimeId) {
        if (showtimeId <= 0) {
            throw new IllegalArgumentException("showtimeId invalid.");
        }

        ShowtimeDetailDtos dto = repo.getShowtimeDetailWithSeatMap(showtimeId);

        if (dto == null) {
            throw new IllegalArgumentException("showtime not found.");
        }
        
        // Calculate dynamic price based on PriceAdjustment (Weekend/Holiday)
        if (dto.getStartTime() != null) {
            List<PriceAdjustment> adjustments = bookingRepository.findActivePriceAdjustments();
            LocalDate showtimeDate = dto.getStartTime().toLocalDate();
            // Use Short style to match "Mon", "Tue" etc. stored in DB
            String dayName = showtimeDate.getDayOfWeek().getDisplayName(TextStyle.SHORT, Locale.ENGLISH);
            
            logger.info("Showtime ID: {}, Date: {}, Day: {}, Active Adjustments: {}", 
                    showtimeId, showtimeDate, dayName, adjustments.size());
            
            for (PriceAdjustment adj : adjustments) {
                logger.info("Checking adjustment: {} (Days: {}, Range: {} - {})", 
                        adj.getName(), adj.getApplyOnDays(), adj.getStartDate(), adj.getEndDate());
                
                boolean match = false;
                
                // 1. Check Date Range
                if (adj.getStartDate() != null && adj.getEndDate() != null) {
                    if (!showtimeDate.isBefore(adj.getStartDate()) && !showtimeDate.isAfter(adj.getEndDate())) {
                        match = true;
                    }
                }
                // 2. Check Days of Week
                else if (adj.getApplyOnDays() != null) {
                    // Check strict match by splitting, to avoid partial matches
                    String[] days = adj.getApplyOnDays().split(",");
                    for (String d : days) {
                        if (d.trim().equalsIgnoreCase(dayName)) {
                            match = true;
                            break;
                        }
                    }
                }
                
                if (match) {
                    BigDecimal currentBase = dto.getBasePrice();
                    if (currentBase == null) currentBase = BigDecimal.ZERO;
                    
                    logger.info("Applying adjustment: {} to base price: {}", adj.getName(), currentBase);
                    
                    if (adj.getAdjustmentType() == PriceAdjustment.AdjustmentType.PERCENT) {
                        BigDecimal increase = currentBase.multiply(adj.getValue())
                                .divide(new BigDecimal(100));
                        dto.setBasePrice(currentBase.add(increase));
                    } else {
                        // AMOUNT
                        dto.setBasePrice(currentBase.add(adj.getValue()));
                    }
                    
                    logger.info("New base price: {}", dto.getBasePrice());
                    
                    // Break after first match to align with BookingService logic
                    break;
                }
            }
        }

        return dto;
    }
}