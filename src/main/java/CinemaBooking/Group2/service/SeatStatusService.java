package CinemaBooking.Group2.service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import CinemaBooking.Group2.dtos.seat.SeatStatusDTO;
import CinemaBooking.Group2.dtos.seat.SeatStatusDTO.SeatStatus;
import CinemaBooking.Group2.models.Seat;
import CinemaBooking.Group2.repositories.BookingSeatRepository;
import CinemaBooking.Group2.repositories.SeatHoldRepository;
import CinemaBooking.Group2.repositories.SeatRepository;
import CinemaBooking.Group2.repositories.ShowtimeRepository;

/**
 * Service for retrieving seat status information
 */
@Service
public class SeatStatusService {

    @Autowired
    private SeatRepository seatRepository;

    @Autowired
    private BookingSeatRepository bookingSeatRepository;

    @Autowired
    private SeatHoldRepository seatHoldRepository;

    @Autowired
    private ShowtimeRepository showtimeRepository;

    
    public List<SeatStatusDTO> getSeatStatusByShowtime(int showtimeId, Integer currentUserId) {
        if (!showtimeRepository.isBookable(showtimeId)) {
            throw new RuntimeException("Showtime không khả dụng");
        }

        List<Seat> seats = seatRepository.findSeatsByShowtime(showtimeId);
        
        List<Integer> soldSeatIds = bookingSeatRepository.findSoldSeatIdsByShowtime(showtimeId);
        Set<Integer> soldSeats = new HashSet<>(soldSeatIds);
        
        List<Integer> heldSeatIds = seatHoldRepository.findActiveSeatIdsByShowtime(showtimeId);
        Set<Integer> heldSeats = new HashSet<>(heldSeatIds);
        
        List<SeatStatusDTO> result = new ArrayList<>();
        for (Seat seat : seats) {
            SeatStatusDTO dto = new SeatStatusDTO();
            dto.setSeatId(seat.getId());
            dto.setSeatCode(seat.getSeatCode());
            dto.setSeatType(seat.getSeatType());
            dto.setExtraPrice(seat.getExtraPrice());
            
            // Determine status
            if (soldSeats.contains(seat.getId())) {
                dto.setStatus(SeatStatus.SOLD);
            } else if (heldSeats.contains(seat.getId())) {
                dto.setStatus(SeatStatus.HELD);
                // Check if held by current user
                if (currentUserId != null && 
                    seatHoldRepository.isSeatHeldByUser(showtimeId, seat.getId(), currentUserId)) {
                    dto.setHeldByCurrentUser(1);
                }
            } else {
                dto.setStatus(SeatStatus.AVAILABLE);
            }
            
            result.add(dto);
        }
        
        return result;
    }

   
    public boolean isSeatAvailable(int showtimeId, int seatId) {
        if (!showtimeRepository.isBookable(showtimeId)) {
            return false;
        }

        // Check if sold
        if (bookingSeatRepository.isSeatSold(showtimeId, seatId)) {
            return false;
        }
        
        // Check if held
        if (seatHoldRepository.isSeatHeld(showtimeId, seatId)) {
            return false;
        }
        
        return true;
    }
}
