package CinemaBooking.Group2.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import CinemaBooking.Group2.dtos.cinema.CinemaRequestDTO;
import CinemaBooking.Group2.dtos.cinema.CinemaResponseDTO;
import CinemaBooking.Group2.models.Cinema;
import CinemaBooking.Group2.repositories.CinemaRepository;

@Service
public class CinemaService {

    @Autowired
    private CinemaRepository repo;

    // ================= GET ALL =================
    public List<CinemaResponseDTO> getAll() {
        return repo.findAllActive()
                   .stream()
                   .map(this::toResponse)
                   .toList();
    }

    // ================= GET BY ID =================
    public CinemaResponseDTO getById(int id) {
        Cinema c = repo.findById(id);
        if (c == null) {
            throw new RuntimeException("Cinema not found");
            // Sau này thay bằng custom exception
        }
        return toResponse(c);
    }

    // ================= CREATE =================
    public CinemaResponseDTO create(CinemaRequestDTO dto) {

        Cinema c = new Cinema();
        c.setName(dto.getName());
        c.setAddress(dto.getAddress());
        c.setPhone(dto.getPhone());
        c.setDescription(dto.getDescription());
        c.setImageUrl(dto.getImageUrl()); 
        c.setIsActive(1); // mặc định active

        repo.insert(c);

        // Giả sử repo.insert set lại ID
        return toResponse(c);
    }

    // ================= UPDATE =================
    public CinemaResponseDTO update(int id, CinemaRequestDTO dto) {

        Cinema existing = repo.findById(id);
        if (existing == null) {
            throw new RuntimeException("Cinema not found");
        }

        existing.setName(dto.getName());
        existing.setAddress(dto.getAddress());
        existing.setPhone(dto.getPhone());
        existing.setDescription(dto.getDescription());
        existing.setImageUrl(dto.getImageUrl());
        existing.setIsActive(dto.getIsActive());

        repo.update(id, existing);

        return toResponse(existing);
    }

    // ================= DEACTIVATE (SOFT DELETE) =================
    public void deactivate(int id) {

        Cinema existing = repo.findById(id);
        if (existing == null) {
            throw new RuntimeException("Cinema not found");
        }

        repo.deactivate(id);
    }

    // ================= MAPPER =================
    private CinemaResponseDTO toResponse(Cinema c) {
        return new CinemaResponseDTO(
            c.getId(),
			c.getName(),
			c.getAddress(),
			c.getPhone(),
			c.getDescription(),
			c.getIsActive(),
			c.getImageUrl(),
			c.getCreatedAt()
		);
    }
}
