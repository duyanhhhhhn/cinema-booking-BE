package CinemaBooking.Group2.service;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import CinemaBooking.Group2.dtos.scheduler.*;
import CinemaBooking.Group2.repositories.ShowtimeSchedulerAdminRepository;
import CinemaBooking.Group2.dtos.scheduler.AdminShowtimeDetailDto;
import CinemaBooking.Group2.dtos.scheduler.AdminUpdateShowtimeReqDto;
@Service
public class ShowtimeSchedulerAdminService {

    private final ShowtimeSchedulerAdminRepository repo;

    private static final int CLEANUP_MINUTES = 10;
    private static final LocalTime TIMELINE_START = LocalTime.of(8, 0);
    private static final LocalTime TIMELINE_END = LocalTime.of(23, 0);
    private static final ZoneId ZONE_VN = ZoneId.of("Asia/Ho_Chi_Minh");

    public ShowtimeSchedulerAdminService(ShowtimeSchedulerAdminRepository repo) {
        this.repo = repo;
    }

    private static LocalDateTime nowVN() {
        return ZonedDateTime.now(ZONE_VN).toLocalDateTime();
    }

    private static boolean isPastOrNow(LocalDateTime t) {
        return t == null || !t.isAfter(nowVN());
    }

    private static void badRequest(String msg) {
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, msg);
    }

    private static void conflict(String msg) {
        throw new ResponseStatusException(HttpStatus.CONFLICT, msg);
    }

    private static void notFound(String msg) {
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, msg);
    }

    public static class SchedulerResult {
        public AdminSchedulerResponseDto data;
        public int totalConflicts;
        public LocalTime timelineStart = TIMELINE_START;
        public LocalTime timelineEnd = TIMELINE_END;
    }

    public SchedulerResult getScheduler(int cinemaId, LocalDate date) {
        if (cinemaId <= 0 || date == null) badRequest("Dữ liệu không hợp lệ.");

        LocalDateTime from = date.atStartOfDay();
        LocalDateTime to = date.plusDays(1).atStartOfDay();

        var rooms = repo.findRoomsByCinema(cinemaId);
        var eventsRows = repo.findEventsByCinemaInRange(cinemaId, from, to);
        var cinemas = repo.findAllCinemaOptions();

        String cinemaName = null;
        for (var c : cinemas) {
            if (c.getId() == cinemaId) {
                cinemaName = c.getName();
                break;
            }
        }

        List<AdminSchedulerResourceDto> resources = new ArrayList<>(rooms.size());
        for (var r : rooms) {
            AdminSchedulerResourceDto dto = new AdminSchedulerResourceDto();
            dto.setId(r.id);
            dto.setName(r.name);
            dto.setType(r.type);
            dto.setTotalSeats(r.totalSeats);
            resources.add(dto);
        }

        List<AdminSchedulerEventDto> events = new ArrayList<>(eventsRows.size());
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

        SchedulerResult out = new SchedulerResult();
        out.data = new AdminSchedulerResponseDto(cinemaName, resources, events);
        out.totalConflicts = markConflicts(events);
        return out;
    }

    private int markConflicts(List<AdminSchedulerEventDto> events) {
        Map<Integer, List<AdminSchedulerEventDto>> byRoom = new HashMap<>();
        for (var e : events) byRoom.computeIfAbsent(e.getResource(), k -> new ArrayList<>()).add(e);

        int count = 0;
        for (var list : byRoom.values()) {
            list.sort(Comparator.comparing(AdminSchedulerEventDto::getStart));
            for (int i = 0; i < list.size(); i++) {
                var a = list.get(i);
                if (a.getStart() == null || a.getEnd() == null) continue;

                for (int j = i + 1; j < list.size(); j++) {
                    var b = list.get(j);
                    if (b.getStart() == null || b.getEnd() == null) continue;
                    if (!b.getStart().isBefore(a.getEnd())) break;

                    if (b.getStart().isBefore(a.getEnd()) && b.getEnd().isAfter(a.getStart())) {
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
        if (req == null) badRequest("Dữ liệu tạo suất chiếu không hợp lệ.");
        if (req.getCinemaId() <= 0 || req.getRoomId() <= 0 || req.getMovieId() <= 0) badRequest("Dữ liệu tạo suất chiếu không hợp lệ.");
        if (req.getStartAt() == null) badRequest("Vui lòng chọn thời gian bắt đầu.");
        if (req.getBasePrice() == null || req.getBasePrice().compareTo(BigDecimal.ZERO) <= 0) badRequest("Giá vé không hợp lệ.");

        if (!repo.roomBelongsToCinema(req.getRoomId(), req.getCinemaId())) badRequest("Phòng không thuộc rạp đã chọn.");

        String roomType = repo.findRoomType(req.getRoomId());
        String movieFormat = repo.findMovieFormat(req.getMovieId());
        if (!isCompatible(movieFormat, roomType)) conflict("Phòng không phù hợp với định dạng phim.");

        LocalDateTime startAt = req.getStartAt();
        if (isPastOrNow(startAt)) conflict("Không thể tạo lịch chiếu vào thời gian đã trôi qua.");

        int dur = repo.findMovieDurationMinutes(req.getMovieId());
        if (dur <= 0) badRequest("Thời lượng phim không hợp lệ.");

        LocalDateTime endAt = startAt.plusMinutes(dur + CLEANUP_MINUTES);

        var locked = repo.lockOverlaps(req.getRoomId(), startAt, endAt, null);
        if (!locked.isEmpty()) conflict("Khung giờ bị trùng với suất chiếu khác trong phòng.");

        return repo.insertShowtime(req.getMovieId(), req.getRoomId(), startAt, endAt, req.getBasePrice());
    }

    @Transactional
    public void moveShowtime(int id, AdminMoveShowtimeReqDto req) {
        if (id <= 0 || req == null) badRequest("Dữ liệu di chuyển suất chiếu không hợp lệ.");
        if (req.getCinemaId() <= 0 || req.getRoomId() <= 0 || req.getStartAt() == null) badRequest("Dữ liệu di chuyển suất chiếu không hợp lệ.");

        var current = repo.findShowtimeById(id);
        if (current == null) notFound("Không tìm thấy suất chiếu.");

        if (!repo.showtimeBelongsToCinema(id, req.getCinemaId())) badRequest("Suất chiếu không thuộc rạp đã chọn.");
        if (!repo.roomBelongsToCinema(req.getRoomId(), req.getCinemaId())) badRequest("Phòng không thuộc rạp đã chọn.");

        if (!"SCHEDULED".equalsIgnoreCase(current.status)) conflict("Suất chiếu không ở trạng thái cho phép chỉnh sửa.");
        if (current.startAt != null && isPastOrNow(current.startAt)) conflict("Suất chiếu đã bắt đầu, không thể chỉnh sửa/di chuyển.");

        String roomType = repo.findRoomType(req.getRoomId());
        String movieFormat = repo.findMovieFormat(current.movieId);
        if (!isCompatible(movieFormat, roomType)) conflict("Phòng không phù hợp với định dạng phim.");

        LocalDateTime startAt = req.getStartAt();
        if (isPastOrNow(startAt)) conflict("Không thể di chuyển vào khung giờ đã trôi qua.");

        if (current.startAt == null || current.endAt == null || !current.endAt.isAfter(current.startAt)) {
            conflict("Dữ liệu thời gian suất chiếu hiện tại không hợp lệ.");
        }

        Duration duration = Duration.between(current.startAt, current.endAt);
        LocalDateTime endAt = startAt.plus(duration);

        var locked = repo.lockOverlaps(req.getRoomId(), startAt, endAt, id);
        if (!locked.isEmpty()) conflict("Khung giờ bị trùng với suất chiếu khác trong phòng.");

        int affected = repo.updateMove(id, req.getRoomId(), startAt, endAt);
        if (affected != 1) conflict("Không thể cập nhật suất chiếu (có thể đã bị huỷ hoặc không tồn tại).");
    }

    @Transactional
    public void cancelShowtime(int id) {
        if (id <= 0) badRequest("Dữ liệu huỷ suất chiếu không hợp lệ.");

        var current = repo.findShowtimeById(id);
        if (current == null) notFound("Không tìm thấy suất chiếu.");

        if ("COMPLETED".equalsIgnoreCase(current.status)) conflict("Suất chiếu đã hoàn tất, không thể huỷ.");

        repo.updateStatus(id, "CANCELLED");
    }

    public List<AdminMovieOptionDto> getMovieOptions(String keyword) {
        return repo.findMovieOptions(keyword);
    }

    public List<AdminMovieOptionDto> getMovieOptions(
            String keyword,
            String roomType,
            Integer roomId,
            Integer cinemaId
    ) {
        if (roomId != null && roomId > 0 && cinemaId != null && cinemaId > 0) {
            return repo.findMovieOptionsByRoom(cinemaId, roomId, keyword);
        }

        return repo.findMovieOptions(keyword, roomType);
    }

    private boolean isCompatible(String movieFormat, String roomType) {
        String mf = (movieFormat == null ? "" : movieFormat.trim().toUpperCase());
        String rt = (roomType == null ? "" : roomType.trim().toUpperCase());

        if (rt.isBlank()) return false;
        if (mf.isBlank()) return true;

        if (mf.contains("IMAX")) return rt.contains("IMAX");
        if (mf.contains("3D")) return rt.contains("3D");
        if (mf.contains("4DX") || mf.contains("ULTRA 4DX")) return rt.contains("4DX");
        if (mf.contains("SCREENX")) return rt.contains("SCREENX");

        if (mf.contains("2D")) return true;
        return true;
    }
    
    public AdminShowtimeDetailDto getShowtimeDetail(int id, int cinemaId) {
        if (id <= 0 || cinemaId <= 0) badRequest("Dữ liệu không hợp lệ.");

        var d = repo.findShowtimeDetail(id);
        if (d == null) notFound("Không tìm thấy suất chiếu.");
        if (d.cinemaId != cinemaId) badRequest("Suất chiếu không thuộc rạp đã chọn.");

        AdminShowtimeDetailDto dto = new AdminShowtimeDetailDto();
        dto.setId(d.id);

        dto.setCinemaId(d.cinemaId);
        dto.setRoomId(d.roomId);
        dto.setRoomName(d.roomName);
        dto.setRoomType(d.roomType);

        dto.setMovieId(d.movieId);
        dto.setMovieTitle(d.movieTitle);
        dto.setPosterUrl(d.posterUrl);
        dto.setMovieFormat(d.movieFormat);
        dto.setDurationMinutes(d.durationMinutes);

        dto.setStartAt(d.startAt);
        dto.setEndAt(d.endAt);
        dto.setBasePrice(d.basePrice);
        dto.setStatus(d.status);

        return dto;
    }

    @Transactional
    public void editShowtime(int id, AdminUpdateShowtimeReqDto req) {
        if (id <= 0 || req == null) badRequest("Dữ liệu chỉnh sửa không hợp lệ.");
        if (req.getCinemaId() <= 0 || req.getRoomId() <= 0 || req.getMovieId() <= 0) badRequest("Dữ liệu chỉnh sửa không hợp lệ.");
        if (req.getStartAt() == null) badRequest("Vui lòng chọn thời gian bắt đầu.");
        if (req.getBasePrice() == null || req.getBasePrice().compareTo(BigDecimal.ZERO) <= 0) badRequest("Giá vé không hợp lệ.");

        var current = repo.findShowtimeById(id);
        if (current == null) notFound("Không tìm thấy suất chiếu.");

        if (!repo.showtimeBelongsToCinema(id, req.getCinemaId())) badRequest("Suất chiếu không thuộc rạp đã chọn.");
        if (!repo.roomBelongsToCinema(req.getRoomId(), req.getCinemaId())) badRequest("Phòng không thuộc rạp đã chọn.");

        if (!"SCHEDULED".equalsIgnoreCase(current.status)) conflict("Suất chiếu không ở trạng thái cho phép chỉnh sửa.");
        if (current.startAt != null && isPastOrNow(current.startAt)) conflict("Suất chiếu đã bắt đầu, không thể chỉnh sửa.");

        LocalDateTime startAt = req.getStartAt();
        if (isPastOrNow(startAt)) conflict("Không thể chỉnh sửa vào khung giờ đã trôi qua.");

        String roomType = repo.findRoomType(req.getRoomId());
        String movieFormat = repo.findMovieFormat(req.getMovieId());
        if (!isCompatible(movieFormat, roomType)) conflict("Phòng không phù hợp với định dạng phim.");

        int dur = repo.findMovieDurationMinutes(req.getMovieId());
        if (dur <= 0) badRequest("Thời lượng phim không hợp lệ.");

        LocalDateTime endAt = startAt.plusMinutes(dur + CLEANUP_MINUTES);

        var locked = repo.lockOverlaps(req.getRoomId(), startAt, endAt, id);
        if (!locked.isEmpty()) conflict("Khung giờ bị trùng với suất chiếu khác trong phòng.");

        int affected = repo.updateEdit(id, req.getRoomId(), req.getMovieId(), startAt, endAt, req.getBasePrice());
        if (affected != 1) conflict("Không thể cập nhật suất chiếu (có thể đã bị huỷ hoặc không tồn tại).");
    }
    
    public List<CinemaOptionDto> getAllCinemas() {
        return repo.findAllCinemaOptions();
    }
}