package CinemaBooking.Group2.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import CinemaBooking.Group2.dtos.scheduler.*;
import CinemaBooking.Group2.repositories.ShowtimeSchedulerAdminRepository;

@Service
public class ShowtimeSchedulerAdminService {

    private final ShowtimeSchedulerAdminRepository repo;

    private static final int CLEANUP_MINUTES = 10;
    private static final LocalTime TIMELINE_START = LocalTime.of(8, 0);
    private static final LocalTime TIMELINE_END = LocalTime.of(23, 0);

    public ShowtimeSchedulerAdminService(ShowtimeSchedulerAdminRepository repo) {
        this.repo = repo;
    }

    public static class SchedulerResult {
        public AdminSchedulerResponseDto data;
        public int totalConflicts;
        public LocalTime timelineStart = TIMELINE_START;
        public LocalTime timelineEnd = TIMELINE_END;
    }

    public SchedulerResult getScheduler(int cinemaId, LocalDate date) {
        if (cinemaId <= 0 || date == null) {
            throw new IllegalArgumentException("INVALID_INPUT");
        }

        LocalDateTime from = date.atStartOfDay();
        LocalDateTime to = date.plusDays(1).atStartOfDay();

        var rooms = repo.findRoomsByCinema(cinemaId);
        var eventsRows = repo.findEventsByCinemaInRange(cinemaId, from, to);

        List<AdminSchedulerResourceDto> resources = new ArrayList<>();
        for (var r : rooms) {
            AdminSchedulerResourceDto dto = new AdminSchedulerResourceDto();
            dto.setId(r.id);
            dto.setName(r.name);
            dto.setType(r.type);
            dto.setTotalSeats(r.totalSeats);
            resources.add(dto);
        }

        List<AdminSchedulerEventDto> events = new ArrayList<>();
        for (var e : eventsRows) {
            AdminSchedulerEventDto dto = new AdminSchedulerEventDto();
            dto.setId(e.id);
            dto.setResource(e.roomId);
            dto.setMovieId(e.movieId);
            dto.setText(e.movieTitle);
            dto.setPosterUrl(e.posterUrl);
            dto.setStart(e.startAt);
            dto.setEnd(e.endAt);
            dto.setBasePrice(e.basePrice);
            dto.setStatus(e.status);
            events.add(dto);
        }

        int conflicts = markConflicts(events);

        SchedulerResult out = new SchedulerResult();
        out.data = new AdminSchedulerResponseDto(resources, events);
        out.totalConflicts = conflicts;
        return out;
    }

    private int markConflicts(List<AdminSchedulerEventDto> events) {
        Map<Integer, List<AdminSchedulerEventDto>> byRoom = new HashMap<>();
        for (var e : events) {
            byRoom.computeIfAbsent(e.getResource(), k -> new ArrayList<>()).add(e);
        }

        int count = 0;
        for (var entry : byRoom.entrySet()) {
            var list = entry.getValue();
            list.sort(Comparator.comparing(AdminSchedulerEventDto::getStart));

            for (int i = 0; i < list.size(); i++) {
                var a = list.get(i);

                for (int j = i + 1; j < list.size(); j++) {
                    var b = list.get(j);
                    if (!b.getStart().isBefore(a.getEnd())) break;

                    boolean overlap = b.getStart().isBefore(a.getEnd()) && b.getEnd().isAfter(a.getStart());
                    if (overlap) {
                        if (!a.isConflict()) { a.setConflict(true); count++; }
                        if (!b.isConflict()) { b.setConflict(true); count++; }

                        if (!a.getConflictWithIds().contains(b.getId())) a.getConflictWithIds().add(b.getId());
                        if (!b.getConflictWithIds().contains(a.getId())) b.getConflictWithIds().add(a.getId());
                    }
                }
            }
        }
        return count;
    }

    @Transactional
    public int createShowtime(AdminCreateShowtimeReqDto req) {
        if (req == null
                || req.getCinemaId() <= 0
                || req.getRoomId() <= 0
                || req.getMovieId() <= 0
                || req.getStartAt() == null
                || req.getBasePrice() == null) {
            throw new IllegalArgumentException("INVALID_INPUT");
        }

        if (!repo.roomBelongsToCinema(req.getRoomId(), req.getCinemaId())) {
            throw new IllegalArgumentException("ROOM_NOT_IN_CINEMA");
        }

        int dur = repo.findMovieDurationMinutes(req.getMovieId());
        if (dur <= 0) {
            throw new IllegalArgumentException("MOVIE_DURATION_INVALID");
        }

        LocalDateTime startAt = req.getStartAt();
        LocalDateTime endAt = startAt.plusMinutes(dur + CLEANUP_MINUTES);

        var locked = repo.lockOverlaps(req.getRoomId(), startAt, endAt, null);
        if (!locked.isEmpty()) {
            throw new IllegalStateException("CONFLICT_SHOWTIME_OVERLAP");
        }

        return repo.insertShowtime(req.getMovieId(), req.getRoomId(), startAt, endAt, req.getBasePrice());
    }

    @Transactional
    public void moveShowtime(int id, AdminMoveShowtimeReqDto req) {
        if (id <= 0
                || req == null
                || req.getCinemaId() <= 0
                || req.getRoomId() <= 0
                || req.getStartAt() == null) {
            throw new IllegalArgumentException("INVALID_INPUT");
        }

        var current = repo.findShowtimeById(id);
        if (current == null) {
            throw new IllegalStateException("SHOWTIME_NOT_FOUND");
        }

        if (!"SCHEDULED".equalsIgnoreCase(current.status)) {
            throw new IllegalArgumentException("SHOWTIME_NOT_EDITABLE");
        }

        if (!repo.roomBelongsToCinema(req.getRoomId(), req.getCinemaId())) {
            throw new IllegalArgumentException("ROOM_NOT_IN_CINEMA");
        }

        int dur = repo.findMovieDurationMinutes(current.movieId);
        if (dur <= 0) {
            throw new IllegalArgumentException("MOVIE_DURATION_INVALID");
        }

        LocalDateTime startAt = req.getStartAt();
        LocalDateTime endAt = startAt.plusMinutes(dur + CLEANUP_MINUTES);

        var locked = repo.lockOverlaps(req.getRoomId(), startAt, endAt, id);
        if (!locked.isEmpty()) {
            throw new IllegalStateException("CONFLICT_SHOWTIME_OVERLAP");
        }

        repo.updateMove(id, req.getRoomId(), startAt, endAt);
    }

    @Transactional
    public void cancelShowtime(int id) {
        if (id <= 0) {
            throw new IllegalArgumentException("INVALID_INPUT");
        }

        var current = repo.findShowtimeById(id);
        if (current == null) {
            throw new IllegalStateException("SHOWTIME_NOT_FOUND");
        }

        if ("COMPLETED".equalsIgnoreCase(current.status)) {
            throw new IllegalArgumentException("SHOWTIME_NOT_CANCELLABLE");
        }

        repo.updateStatus(id, "CANCELLED");
    }
    
    public List<AdminMovieOptionDto> getMovieOptions(String keyword) {
        return repo.findMovieOptions(keyword);
    }
}