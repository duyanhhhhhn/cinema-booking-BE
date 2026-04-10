package CinemaBooking.Group2.service;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;

import CinemaBooking.Group2.dtos.room.RoomDetailDTO;
import CinemaBooking.Group2.dtos.room.RoomRequestDTO;
import CinemaBooking.Group2.dtos.room.RoomResponseDTO;
import CinemaBooking.Group2.dtos.room.SeatLayoutUpdateDTO;
import CinemaBooking.Group2.dtos.seat.SeatLayoutRowDTO;
import CinemaBooking.Group2.dtos.seat.SeatUpdateDTO;
import CinemaBooking.Group2.models.Room;
import CinemaBooking.Group2.models.Seat;
import CinemaBooking.Group2.repositories.RoomRepository;
import CinemaBooking.Group2.repositories.SeatRepository;

@Service
public class RoomService {
	@Autowired
	private SeatRepository seatRepo;
	private final RoomRepository repo;
	private final ObjectMapper objectMapper = new ObjectMapper();

	public RoomService(RoomRepository repo) {
		this.repo = repo;
	}

	// ================= GET ALL ROOMS =================
	public List<RoomResponseDTO> getAllRooms() {
		List<Room> rooms = repo.findAll();
		if (rooms == null || rooms.isEmpty())
			return List.of();
		return rooms.stream().map(this::toResponse).toList();
	}

	// ================= GET BY CINEMA =================
	public List<RoomResponseDTO> getByCinema(int cinemaId) {
		List<Room> rooms = repo.findByCinema(cinemaId);
		if (rooms == null || rooms.isEmpty())
			return List.of();
		return rooms.stream().map(this::toResponse).toList();
	}

	// ================= GET BY ID =================
	public RoomResponseDTO getById(int id) {
		Room room = repo.findById(id);
		if (room == null)
			throw new RuntimeException("Room not found");
		return toResponse(room);
	}

	// ================= CREATE =================
	public RoomResponseDTO create(RoomRequestDTO dto) {
		Room r = new Room();
		r.setCinemaId(dto.getCinemaId());
		r.setName(dto.getName());
		r.setType(dto.getType());
		r.setTotalSeats(dto.getTotalSeats());
		r.setStatus(1); // default active
		r.setSeatLayout(dto.getSeatLayout() == null ? "[]" : dto.getSeatLayout());
		
		repo.insert(r);
		return toResponse(r);
	}

	// ================= UPDATE =================
	public RoomResponseDTO update(int id, RoomRequestDTO dto) {
		Room existing = repo.findById(id);
		if (existing == null)
			throw new RuntimeException("Room not found");

		existing.setName(dto.getName());
		existing.setType(dto.getType());
		existing.setTotalSeats(dto.getTotalSeats());
		existing.setSeatLayout(dto.getSeatLayout());
		existing.setStatus(dto.getStatus());
		repo.update(id, existing);
		return toResponse(existing);
	}

	// ================= UPDATE SEAT LAYOUT =================
	@Transactional
	public RoomResponseDTO updateSeatLayout(int roomId, SeatLayoutUpdateDTO dto) {

    Room room = repo.findById(roomId);
    if (room == null) throw new RuntimeException("Room not found");

    try {
        String json = dto.getLayout();

        // 1. save room
        repo.updateSeatLayout(roomId, json, dto.getTotalSeats());
        room.setSeatLayout(json);
        room.setTotalSeats(dto.getTotalSeats());

        // 2. parse JSON
        List<SeatLayoutRowDTO> rows = objectMapper.readValue(
                json,
                objectMapper.getTypeFactory()
                        .constructCollectionType(List.class, SeatLayoutRowDTO.class)
        );

        // 3. lấy seat hiện tại trong DB
        List<Seat> existingSeats = seatRepo.getSeatsByRoomId(roomId);

        Map<Integer, Seat> existingMap = existingSeats.stream()
                .collect(Collectors.toMap(Seat::getId, s -> s));

        // new: map by seat code to detect existing seats when incoming DTO has no id
        Map<String, Seat> existingByCode = existingSeats.stream()
                .collect(Collectors.toMap(Seat::getSeatCode, s -> s));

        Set<Integer> incomingIds = new HashSet<>();

        // 4. LOOP xử lý INSERT + UPDATE
        for (SeatLayoutRowDTO row : rows) {
            for (SeatUpdateDTO s : row.getSeats()) {

                Seat seat = new Seat();
                seat.setRoomId(roomId);
                seat.setSeatCode(s.getCode());
                seat.setSeatType(Seat.SeatType.valueOf(s.getType().toUpperCase()));
                seat.setExtraPrice(getExtraPrice(s.getType()));

                if (s.getId() == null) {
                    // If incoming seat has no id but a seat with same code exists in DB, treat as UPDATE
                    Seat existed = existingByCode.get(s.getCode());
                    if (existed != null) {
                        seat.setId(existed.getId());
                        seatRepo.updateSeat(seat);
                        incomingIds.add(existed.getId());
                    } else {
                        // INSERT
                        int newId = seatRepo.insertSeat(seat);
                        if (newId != -1) {
                            incomingIds.add(newId);
                        }
                    }

                } else {
                    //  UPDATE
                    seat.setId(s.getId());
                    seatRepo.updateSeat(seat);
                    incomingIds.add(s.getId());
                }
            }
        }

        // 5. DELETE seat bị xoá
        List<Integer> toDelete = existingMap.keySet().stream()
                .filter(id -> !incomingIds.contains(id))
                .toList();

        seatRepo.deleteByIds(toDelete);

        return toResponse(room);

    } catch (Exception e) {
        e.printStackTrace();
        throw new RuntimeException("Update seat layout failed: " + e.getMessage());
    }
}

	private BigDecimal getExtraPrice(String type) {
		return switch (type) {
		case "VIP" -> BigDecimal.valueOf(50000);
		case "COUPLE" -> BigDecimal.valueOf(100000);
		default -> BigDecimal.ZERO;
		};
	}

	// ================= DELETE =================
	public void delete(int id) {
		Room existing = repo.findById(id);
		if (existing == null)
			throw new RuntimeException("Room not found");
		repo.delete(id);
	}

	// ================= GET ROOM DETAIL =================
	public RoomResponseDTO getRoomDetail(int id) {
		return getById(id);
	}

	public RoomDetailDTO getRoomDetailForClient(int id) {
		Room room = repo.findById(id);
		if (room == null)
			throw new RuntimeException("Room not found");

		try {

			String raw = room.getSeatLayout();

			List<RoomDetailDTO.RowDTO> layout;
			try {
				layout = objectMapper.readValue(raw,
						objectMapper.getTypeFactory().constructCollectionType(List.class, RoomDetailDTO.RowDTO.class));
			} catch (Exception e) {
				throw new RuntimeException("Failed to parse seat layout: " + e.getMessage());
			}

			return new RoomDetailDTO(room.getId(), room.getCinemaId(), room.getName(), room.getType(),
					room.getTotalSeats(), layout);

		} catch (Exception e) {
			throw new RuntimeException("Failed to parse seat layout: " + e.getMessage());
		}
	}
	
	//================== UPDATE ROOM STATUS =================
	public void updateStatus(int id, int status) {
	    Room existing = repo.findById(id);
	    if (existing == null) {
	        throw new RuntimeException("Room not found");
	    }

	    repo.updateStatus(id, status);
	}

	// ================= MAPPER =================
	private RoomResponseDTO toResponse(Room r) {
	    return new RoomResponseDTO(
	        r.getId(),
	        r.getCinemaId(),
	        r.getName(),
	        r.getType(),
	        r.getTotalSeats(),
	        r.getSeatLayout(),
	        r.getStatus()
	    );
	}
}
