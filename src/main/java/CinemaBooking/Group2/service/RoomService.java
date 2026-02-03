package CinemaBooking.Group2.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

import CinemaBooking.Group2.dtos.room.RoomRequestDTO;
import CinemaBooking.Group2.dtos.room.RoomResponseDTO;
import CinemaBooking.Group2.models.Room;
import CinemaBooking.Group2.repositories.RoomRepository;

@Service
public class RoomService {

    @Autowired
    private RoomRepository repo;
    
    private final ObjectMapper objectMapper = new ObjectMapper();

    // ================= GET BY CINEMA =================
    public List<RoomResponseDTO> getByCinema(int cinemaId) {
        return repo.findByCinema(cinemaId)
                   .stream()
                   .map(this::toResponse)
                   .toList();
    }

    // ================= GET BY ID =================
    public RoomResponseDTO getById(int id) {

        Room r = repo.findById(id);
        if (r == null) {
            throw new RuntimeException("Room not found");
            // Sau này thay bằng NotFoundException
        }

        return toResponse(r);
    }

    // ================= CREATE =================
    public RoomResponseDTO create(RoomRequestDTO dto) {

        Room r = new Room();
        r.setCinemaId(dto.getCinemaId());
        r.setName(dto.getName());
        r.setType(dto.getType());
        r.setTotalSeats(dto.getTotalSeats());
        if (dto.getSeatLayout() == null) {
            r.setSeatLayout("[]");
        } else {
            r.setSeatLayout(dto.getSeatLayout());
        }

        repo.insert(r);

        // repo.insert set lại id
        return toResponse(r);
    }

    // ================= UPDATE =================
    public RoomResponseDTO update(int id, RoomRequestDTO dto) {

        Room existing = repo.findById(id);
        if (existing == null) {
            throw new RuntimeException("Room not found");
        }

        existing.setName(dto.getName());
        existing.setType(dto.getType());
        existing.setTotalSeats(dto.getTotalSeats());
        existing.setSeatLayout(dto.getSeatLayout());

        repo.update(id, existing);

        return toResponse(existing);
    }
    
    //================== UPDATE SEAT LAYOUT =================
    public RoomResponseDTO updateSeatLayout(int id, Object seatLayout) {

        Room room = repo.findById(id);

        if (room == null) {
            throw new RuntimeException("Room not found");
        }

        try {

            String json = objectMapper.writeValueAsString(seatLayout);

            room.setSeatLayout(json);

            repo.updateSeatLayout(id, json);

            return toResponse(room);

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Update seat layout failed");
        }
    }

    // ================= DELETE =================
    public void delete(int id) {

        Room existing = repo.findById(id);
        if (existing == null) {
            throw new RuntimeException("Room not found");
        }

        repo.delete(id);
    }

    // ================= MAPPER =================
    private RoomResponseDTO toResponse(Room r) {
        return new RoomResponseDTO(
                r.getId(),
                r.getCinemaId(),
                r.getName(),
                r.getType(),
                r.getTotalSeats(),
                r.getSeatLayout()
        );
    }
    
    // ================= GET ROOM DETAIL =================
    public RoomResponseDTO getRoomDetail(int id) {
		Room r = repo.findById(id);
		if (r == null) {
			throw new RuntimeException("Room not found");
		}
		return toResponse(r);
	}
}
