package CinemaBooking.Group2.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import CinemaBooking.Group2.dtos.seat.HoldSeatRequestDTO;
import CinemaBooking.Group2.dtos.seat.HoldSeatResponseDTO;
import CinemaBooking.Group2.dtos.seat.ReleaseSeatRequestDTO;
import CinemaBooking.Group2.dtos.seat.ReleaseSeatResponseDTO;
import CinemaBooking.Group2.dtos.seat.SeatStatusDTO;

@Service
public class SeatBookingFacade {

    @Autowired
    private SeatStatusService seatStatusService;

    @Autowired
    private SeatHoldService seatHoldService;

   
    public List<SeatStatusDTO> getShowtimeSeats(int showtimeId, Integer userId) {
        return seatStatusService.getSeatStatusByShowtime(showtimeId, userId);
    }

    
    public HoldSeatResponseDTO holdSeats(HoldSeatRequestDTO request, int userId) {
        // Validate input
        if (request.getSeatIds() == null || request.getSeatIds().isEmpty()) {
            return new HoldSeatResponseDTO(false, "No seats specified");
        }

        // Use the hold service to perform the operation
        SeatHoldService.HoldResult holdResult = seatHoldService.holdSeats(
            request.getShowtimeId(), 
            request.getSeatIds(), 
            userId
        );

        // Map to response DTO
        HoldSeatResponseDTO response = new HoldSeatResponseDTO();
        response.setSuccess(holdResult.isSuccess());
        response.setMessage(holdResult.getMessage());
        response.setHoldToken(holdResult.getHoldToken());
        response.setExpiresAt(holdResult.getExpiresAt());
        response.setHeldSeatIds(holdResult.getSuccessfulSeatIds());
        response.setFailedSeatIds(holdResult.getFailedSeatIds());

        return response;
    }

    public ReleaseSeatResponseDTO releaseSeats(ReleaseSeatRequestDTO request, int userId) {
        // Validate input
        if (request.getSeatIds() == null || request.getSeatIds().isEmpty()) {
            return new ReleaseSeatResponseDTO(false, "No seats specified");
        }

        // Use the hold service to release seats
        SeatHoldService.ReleaseResult releaseResult = seatHoldService.releaseSeats(
            request.getShowtimeId(), 
            request.getSeatIds(), 
            userId
        );

        // Map to response DTO
        ReleaseSeatResponseDTO response = new ReleaseSeatResponseDTO();
        response.setSuccess(releaseResult.isSuccess());
        response.setMessage(releaseResult.getMessage());
        response.setReleasedSeatIds(releaseResult.getReleasedSeatIds());

        return response;
    }

    public boolean isSeatAvailable(int showtimeId, int seatId) {
        return seatStatusService.isSeatAvailable(showtimeId, seatId);
    }

    public int cleanupExpiredHolds() {
        return seatHoldService.cleanupExpiredHolds();
    }
}
