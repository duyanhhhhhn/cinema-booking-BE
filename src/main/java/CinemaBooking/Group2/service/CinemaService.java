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

    public List<CinemaResponseDTO> getAll() {
        return repo.findAllActive()
                   .stream()
                   .map(this::toResponse)
                   .toList();
    }

    public CinemaResponseDTO getById(int id) {
        Cinema c = repo.findById(id);
        if (c == null) {
            throw new RuntimeException("Cinema not found");
        }
        return toResponse(c);
    }

    public void create(CinemaRequestDTO dto) {
        Cinema c = new Cinema();
        c.setName(dto.getName());
        c.setAddress(dto.getAddress());
        c.setPhone(dto.getPhone());
        c.setDescription(dto.getDescription());
        repo.insert(c);
    }

    public void update(int id, CinemaRequestDTO dto) {
        Cinema existing = repo.findById(id);
        if (existing == null) {
            throw new RuntimeException("Cinema not found");
        }

        existing.setName(dto.getName());
        existing.setAddress(dto.getAddress());
        existing.setPhone(dto.getPhone());
        existing.setDescription(dto.getDescription());
        existing.setIsActive(dto.getIsActive());

        repo.update(id, existing);
    }

    public void deactivate(int id) {
        repo.deactivate(id);
    }

    private CinemaResponseDTO toResponse(Cinema c) {
        return new CinemaResponseDTO(
            c.getId(),
            c.getName(),
            c.getAddress(),
            c.getPhone(),
            c.getDescription(),
            c.getIsActive()
        );
    }
}
