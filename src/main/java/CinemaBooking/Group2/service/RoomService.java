package CinemaBooking.Group2.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

import CinemaBooking.Group2.dtos.room.RoomDetailDTO;
import CinemaBooking.Group2.dtos.room.RoomRequestDTO;
import CinemaBooking.Group2.dtos.room.RoomResponseDTO;
import CinemaBooking.Group2.dtos.room.SeatLayoutUpdateDTO;
import CinemaBooking.Group2.models.Room;
import CinemaBooking.Group2.repositories.RoomRepository;

@Service
public class RoomService {

    private final RoomRepository repo;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public RoomService(RoomRepository repo) {
        this.repo = repo;
    }

    // ================= GET ALL ROOMS =================
    public List<RoomResponseDTO> getAllRooms() {
        List<Room> rooms = repo.findAll();
        if (rooms == null || rooms.isEmpty()) return List.of();
        return rooms.stream().map(this::toResponse).toList();
    }

    // ================= GET BY CINEMA =================
    public List<RoomResponseDTO> getByCinema(int cinemaId) {
        List<Room> rooms = repo.findByCinema(cinemaId);
        if (rooms == null || rooms.isEmpty()) return List.of();
        return rooms.stream().map(this::toResponse).toList();
    }

    // ================= GET BY ID =================
    public RoomResponseDTO getById(int id) {
        Room room = repo.findById(id); // repo trả Room, không phải Optional
        if (room == null) throw new RuntimeException("Room not found");
        return toResponse(room);
    }

    // ================= CREATE =================
    public RoomResponseDTO create(RoomRequestDTO dto) {
        Room r = new Room();
        r.setCinemaId(dto.getCinemaId());
        r.setName(dto.getName());
        r.setType(dto.getType());
        r.setTotalSeats(dto.getTotalSeats());
        r.setSeatLayout(dto.getSeatLayout() == null ? "[]" : dto.getSeatLayout());
        repo.insert(r);
        return toResponse(r);
    }

    // ================= UPDATE =================
    public RoomResponseDTO update(int id, RoomRequestDTO dto) {
        Room existing = repo.findById(id);
        if (existing == null) throw new RuntimeException("Room not found");

        existing.setName(dto.getName());
        existing.setType(dto.getType());
        existing.setTotalSeats(dto.getTotalSeats());
        existing.setSeatLayout(dto.getSeatLayout());

        repo.update(id, existing);
        return toResponse(existing);
    }

    // ================= UPDATE SEAT LAYOUT =================
    public RoomResponseDTO updateSeatLayout(int id, SeatLayoutUpdateDTO dto) {

        Room room = repo.findById(id);
        if (room == null) throw new RuntimeException("Room not found");

        try {

            String json;

            if (dto.getLayout() instanceof String) {
                json = (String) dto.getLayout();
            } else {
                json = objectMapper.writeValueAsString(dto.getLayout());
            }

            room.setSeatLayout(json);
            room.setTotalSeats(dto.getTotalSeats());

            repo.updateSeatLayout(id, json, dto.getTotalSeats());

            return toResponse(room);

        } catch (Exception e) {
            throw new RuntimeException("Update seat layout failed");
        }
    }

    // ================= DELETE =================
    public void delete(int id) {
        Room existing = repo.findById(id);
        if (existing == null) throw new RuntimeException("Room not found");
        repo.delete(id);
    }

    // ================= GET ROOM DETAIL =================
    public RoomResponseDTO getRoomDetail(int id) {
        return getById(id);
    }
    public RoomDetailDTO getRoomDetailForClient(int id) {
        Room room = repo.findById(id);
        if (room == null) throw new RuntimeException("Room not found");

        try {

        	String raw = room.getSeatLayout();

        	List<RoomDetailDTO.RowDTO> layout;
        	try {
        	    layout = objectMapper.readValue(
        	        raw,
        	        objectMapper.getTypeFactory()
        	                .constructCollectionType(List.class, RoomDetailDTO.RowDTO.class)
        	    );
        	} catch (Exception e) {
        	    throw new RuntimeException("Failed to parse seat layout: " + e.getMessage());
        	}

        	return new RoomDetailDTO(
        	    room.getId(),
        	    room.getCinemaId(),
        	    room.getName(),
        	    room.getType(),
        	    room.getTotalSeats(),
        	    layout
        	);

        } catch (Exception e) {
            throw new RuntimeException("Failed to parse seat layout: " + e.getMessage());
        }
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
}