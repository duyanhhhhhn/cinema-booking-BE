package CinemaBooking.Group2.service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import CinemaBooking.Group2.dtos.staff.CreateWorkShiftRequestDTO;
import CinemaBooking.Group2.dtos.staff.ShiftTemplateResponseDTO;
import CinemaBooking.Group2.dtos.staff.StaffResponseDTO;
import CinemaBooking.Group2.dtos.staff.StaffScheduleDetailResponseDTO;
import CinemaBooking.Group2.dtos.staff.StaffScheduleRequestDTO;
import CinemaBooking.Group2.models.StaffSchedule;
import CinemaBooking.Group2.models.User;
import CinemaBooking.Group2.models.WorkShift;
import CinemaBooking.Group2.models.Enum.StaffScheduleStatus;
import CinemaBooking.Group2.repositories.ScheduleRepository;
import CinemaBooking.Group2.repositories.ShiftRepository;
import CinemaBooking.Group2.repositories.UserRepository;
import CinemaBooking.Group2.security.AuthUserPrincipal;

@Service
public class StaffScheduleService {

    private static final ZoneId ZONE_VN = ZoneId.of("Asia/Ho_Chi_Minh");

    private final ScheduleRepository scheduleRepository;
    private final ShiftRepository shiftRepository;
    private final UserRepository userRepository;
    private final AuthService authService;

    public StaffScheduleService(
            ScheduleRepository scheduleRepository,
            ShiftRepository shiftRepository,
            UserRepository userRepository,
            AuthService authService) {
        this.scheduleRepository = scheduleRepository;
        this.shiftRepository = shiftRepository;
        this.userRepository = userRepository;
        this.authService = authService;
    }

    public List<ShiftTemplateResponseDTO> getShiftTemplates() {
        List<WorkShift> shifts = shiftRepository.getAll();
        List<ShiftTemplateResponseDTO> data = new ArrayList<>();
        for (WorkShift shift : shifts) {
            data.add(toShiftResponse(shift));
        }
        return data;
    }

    public ShiftTemplateResponseDTO getWorkShift(int shiftId) {
        return toShiftResponse(requireShift(shiftId));
    }

    @Transactional
    public ShiftTemplateResponseDTO createWorkShift(CreateWorkShiftRequestDTO request) {
        assertWorkShiftManagePermission();
        String shiftName = validateWorkShiftPayload(request, null);

        WorkShift shift = new WorkShift();
        shift.setName(shiftName);
        shift.setStartTime(request.getStartTime());
        shift.setEndTime(request.getEndTime());
        shift.setCreatedAt(LocalDateTime.now(ZONE_VN));

        int shiftId = shiftRepository.create(shift);
        if (shiftId <= 0) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Không thể tạo ca mẫu");
        }

        return toShiftResponse(requireShift(shiftId));
    }

    @Transactional
    public ShiftTemplateResponseDTO updateWorkShift(int shiftId, CreateWorkShiftRequestDTO request) {
        assertWorkShiftManagePermission();

        WorkShift existingShift = requireShift(shiftId);
        String shiftName = validateWorkShiftPayload(request, shiftId);

        existingShift.setName(shiftName);
        existingShift.setStartTime(request.getStartTime());
        existingShift.setEndTime(request.getEndTime());

        int updatedRows = shiftRepository.update(existingShift);
        if (updatedRows <= 0) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Không thể cập nhật ca mẫu");
        }

        return toShiftResponse(requireShift(shiftId));
    }

    @Transactional
    public void deleteWorkShift(int shiftId) {
        assertWorkShiftManagePermission();
        requireShift(shiftId);

        if (scheduleRepository.existsByShiftId(shiftId)) {
            conflict("Ca làm đang được sử dụng trong lịch làm, không thể xóa");
        }

        int deletedRows = shiftRepository.delete(shiftId);
        if (deletedRows <= 0) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Không thể xóa ca mẫu");
        }
    }

    @Transactional
    public StaffScheduleDetailResponseDTO upsertSchedule(StaffScheduleRequestDTO request) {
        if (request == null) {
            badRequest("Dữ liệu lịch làm không hợp lệ");
        }
        if (request.getWorkDate().isBefore(todayVN())) {
            conflict("Không thể tạo hoặc chỉnh sửa lịch làm cho ngày đã qua");
        }

        AuthUserPrincipal principal = authService.getPrincipal();
        User actor = requireUserByEmail(principal.email());
        WorkShift targetShift = requireShift(request.getShiftId());

        return switch (principal.role()) {
            case "STAFF" -> handleStaffRequest(actor, request, targetShift);
            case "MANAGER", "ADMIN" -> handleManagerAssignment(principal, request, targetShift);
            default -> throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Bạn không có quyền thao tác lịch làm");
        };
    }

    public List<StaffScheduleDetailResponseDTO> getMySchedule(
            LocalDate startDate,
            LocalDate endDate,
            StaffScheduleStatus status) {

        AuthUserPrincipal principal = authService.getPrincipal();
        User currentUser = requireUserByEmail(principal.email());
        DateRange range = resolveRange(startDate, endDate);

        List<StaffSchedule> schedules =
                scheduleRepository.findByStaffAndRange(currentUser.getId(), range.startDate(), range.endDate(), status);

        return toScheduleResponses(schedules);
    }

    public List<StaffScheduleDetailResponseDTO> getCinemaSchedule(
            LocalDate startDate,
            LocalDate endDate,
            StaffScheduleStatus status,
            Integer staffId,
            Integer cinemaIdParam) {

        AuthUserPrincipal principal = authService.getPrincipal();
        DateRange range = resolveRange(startDate, endDate);

        Integer scopedCinemaId = resolveCinemaScheduleScope(principal, cinemaIdParam);

        if (staffId != null) {
            User targetUser = requireScheduleMember(staffId);
            if (scopedCinemaId != null && !scopedCinemaId.equals(targetUser.getCinemaId())) {
                forbidden("Nhân sự không thuộc phạm vi rạp được phép xem");
            }
        }

        List<StaffSchedule> schedules =
                scheduleRepository.findByCinemaAndRange(scopedCinemaId, range.startDate(), range.endDate(), status, staffId);

        return toScheduleResponses(schedules);
    }

    private Integer resolveCinemaScheduleScope(AuthUserPrincipal principal, Integer cinemaIdParam) {
        return switch (principal.role()) {
            case "ADMIN" -> cinemaIdParam;
            case "MANAGER" -> requireScopedCinemaId(principal, "Manager");
            case "STAFF" -> requireScopedCinemaId(principal, "Nhân viên");
            default -> {
                forbidden("Bạn không có quyền xem lịch toàn rạp");
                yield null;
            }
        };
    }

    private StaffScheduleDetailResponseDTO handleStaffRequest(
            User actor,
            StaffScheduleRequestDTO request,
            WorkShift targetShift) {

        // Current DB enum has no REQUESTED state, so ASSIGNED is used as the staff preference/pending state.
        if (request.getStaffId() != null && request.getStaffId() != actor.getId()) {
            forbidden("Nhân viên chỉ được đăng ký lịch cho chính mình");
        }

        if (request.getStatus() != null && request.getStatus() != StaffScheduleStatus.ASSIGNED) {
            badRequest("Nhân viên chỉ được tạo đăng ký ca ở trạng thái ASSIGNED");
        }

        validateStaffSelectionWindow(request.getWorkDate());

        StaffSchedule exactSchedule =
                scheduleRepository.findByStaffShiftAndDate(actor.getId(), targetShift.getId(), request.getWorkDate());

        if (exactSchedule != null && exactSchedule.getStatus() == StaffScheduleStatus.CONFIRMED) {
            return toScheduleResponse(exactSchedule, new HashMap<>(), new HashMap<>());
        }

        validateStaffRequestConflicts(actor.getId(), request.getWorkDate(), targetShift, exactSchedule);

        StaffSchedule saved;
        if (exactSchedule != null) {
            exactSchedule.setStatus(StaffScheduleStatus.ASSIGNED);
            saved = save(exactSchedule);
        } else {
            StaffSchedule newSchedule = new StaffSchedule();
            newSchedule.setStaffId(actor.getId());
            newSchedule.setShiftId(targetShift.getId());
            newSchedule.setWorkDate(request.getWorkDate());
            newSchedule.setStatus(StaffScheduleStatus.ASSIGNED);
            saved = save(newSchedule);
        }

        return toScheduleResponse(saved, new HashMap<>(), new HashMap<>());
    }

    private StaffScheduleDetailResponseDTO handleManagerAssignment(
            AuthUserPrincipal principal,
            StaffScheduleRequestDTO request,
            WorkShift targetShift) {

        if (request.getStaffId() == null || request.getStaffId() <= 0) {
            badRequest("Manager phải chọn nhân viên để phân công");
        }

        User targetStaff = requireStaff(request.getStaffId());
        enforceCinemaScope(principal, targetStaff);

        StaffScheduleStatus targetStatus = request.getStatus();
        if (targetStatus == null) {
            targetStatus = StaffScheduleStatus.CONFIRMED;
        }
        if (targetStatus != StaffScheduleStatus.CONFIRMED && targetStatus != StaffScheduleStatus.CANCELLED) {
            badRequest("Manager chỉ được chốt ca CONFIRMED hoặc huỷ ca CANCELLED");
        }

        StaffSchedule exactSchedule = scheduleRepository.findByStaffShiftAndDate(
                targetStaff.getId(),
                targetShift.getId(),
                request.getWorkDate());

        if (targetStatus == StaffScheduleStatus.CANCELLED) {
            if (exactSchedule == null) {
                notFound("Không tìm thấy lịch làm để huỷ");
            }
            exactSchedule.setStatus(StaffScheduleStatus.CANCELLED);
            StaffSchedule saved = save(exactSchedule);
            return toScheduleResponse(saved, new HashMap<>(), new HashMap<>());
        }

        cancelOverlappingSchedules(targetStaff.getId(), request.getWorkDate(), targetShift,
                exactSchedule == null ? null : exactSchedule.getId());

        StaffSchedule saved;
        if (exactSchedule != null) {
            exactSchedule.setStatus(StaffScheduleStatus.CONFIRMED);
            saved = save(exactSchedule);
        } else {
            StaffSchedule newSchedule = new StaffSchedule();
            newSchedule.setStaffId(targetStaff.getId());
            newSchedule.setShiftId(targetShift.getId());
            newSchedule.setWorkDate(request.getWorkDate());
            newSchedule.setStatus(StaffScheduleStatus.CONFIRMED);
            saved = save(newSchedule);
        }

        return toScheduleResponse(saved, new HashMap<>(), new HashMap<>());
    }

    private void validateStaffRequestConflicts(
            int staffId,
            LocalDate workDate,
            WorkShift targetShift,
            StaffSchedule exactSchedule) {

        List<StaffSchedule> sameDaySchedules = scheduleRepository.findByStaffAndDate(staffId, workDate);
        Integer exactId = exactSchedule == null ? null : exactSchedule.getId();

        for (StaffSchedule schedule : sameDaySchedules) {
            if (schedule.getStatus() == StaffScheduleStatus.CANCELLED) {
                continue;
            }
            if (exactId != null && schedule.getId() == exactId) {
                continue;
            }

            WorkShift existingShift = requireShift(schedule.getShiftId());
            if (!isOverlapping(targetShift, existingShift)) {
                continue;
            }

            if (schedule.getStatus() == StaffScheduleStatus.CONFIRMED) {
                conflict("Bạn đã được manager chốt một ca trùng thời gian");
            }

            conflict("Bạn đã đăng ký một ca trùng thời gian");
        }
    }

    private void cancelOverlappingSchedules(
            int staffId,
            LocalDate workDate,
            WorkShift targetShift,
            Integer exactScheduleId) {

        List<StaffSchedule> sameDaySchedules = scheduleRepository.findByStaffAndDate(staffId, workDate);

        for (StaffSchedule schedule : sameDaySchedules) {
            if (schedule.getStatus() == StaffScheduleStatus.CANCELLED) {
                continue;
            }
            if (exactScheduleId != null && schedule.getId() == exactScheduleId) {
                continue;
            }

            WorkShift existingShift = requireShift(schedule.getShiftId());
            if (!isOverlapping(targetShift, existingShift)) {
                continue;
            }

            schedule.setStatus(StaffScheduleStatus.CANCELLED);
            save(schedule);
        }
    }

    private StaffSchedule save(StaffSchedule schedule) {
        if (schedule.getId() > 0) {
            scheduleRepository.update(schedule);
            return requireSchedule(schedule.getId());
        }

        int id = scheduleRepository.create(schedule);
        if (id <= 0) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Không thể lưu lịch làm");
        }
        return requireSchedule(id);
    }

    private List<StaffScheduleDetailResponseDTO> toScheduleResponses(List<StaffSchedule> schedules) {
        Map<Integer, User> staffCache = new HashMap<>();
        Map<Integer, WorkShift> shiftCache = new HashMap<>();

        List<StaffScheduleDetailResponseDTO> data = new ArrayList<>();
        for (StaffSchedule schedule : schedules) {
            data.add(toScheduleResponse(schedule, staffCache, shiftCache));
        }
        return data;
    }

    private StaffScheduleDetailResponseDTO toScheduleResponse(
            StaffSchedule schedule,
            Map<Integer, User> staffCache,
            Map<Integer, WorkShift> shiftCache) {

        StaffScheduleDetailResponseDTO dto = new StaffScheduleDetailResponseDTO();
        dto.setId(schedule.getId());
        dto.setWorkDate(schedule.getWorkDate());
        dto.setStatus(schedule.getStatus());
        dto.setStaff(toStaffResponse(loadStaff(schedule.getStaffId(), staffCache)));
        dto.setShift(toShiftResponse(loadShift(schedule.getShiftId(), shiftCache)));
        return dto;
    }

    private StaffResponseDTO toStaffResponse(User staff) {
        StaffResponseDTO dto = new StaffResponseDTO();
        dto.setId(staff.getId());
        dto.setFullName(staff.getFullName());
        dto.setAvatarUrl(staff.getAvatarUrl());
        dto.setPhone(staff.getPhone());
        dto.setRoleName(staff.getRoleName());
        if (staff.getPosition() != null) {
            dto.setPosition(staff.getPosition().name());
        }
        return dto;
    }

    private ShiftTemplateResponseDTO toShiftResponse(WorkShift shift) {
        ShiftTemplateResponseDTO dto = new ShiftTemplateResponseDTO();
        dto.setId(shift.getId());
        dto.setName(shift.getName());
        dto.setStartTime(shift.getStartTime());
        dto.setEndTime(shift.getEndTime());
        return dto;
    }

    private User loadStaff(int staffId, Map<Integer, User> staffCache) {
        return staffCache.computeIfAbsent(staffId, this::requireUserById);
    }

    private WorkShift loadShift(int shiftId, Map<Integer, WorkShift> shiftCache) {
        return shiftCache.computeIfAbsent(shiftId, this::requireShift);
    }

    private User requireUserByEmail(String email) {
        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Không tìm thấy thông tin đăng nhập");
        }
        return user;
    }

    private User requireUserById(int userId) {
        User user = userRepository.findById(userId);
        if (user == null) {
            notFound("Không tìm thấy nhân viên");
        }
        return user;
    }

    private User requireStaff(int userId) {
        User user = requireUserById(userId);
        if (user.getRoleId() != 3) {
            badRequest("Chỉ được phân công cho tài khoản STAFF");
        }
        return user;
    }

    private User requireScheduleMember(int userId) {
        User user = requireUserById(userId);
        if (user.getRoleId() != 2 && user.getRoleId() != 3) {
            badRequest("Chỉ được xem lịch của nhân sự thuộc rạp");
        }
        return user;
    }

    private WorkShift requireShift(int shiftId) {
        WorkShift shift = shiftRepository.findById(shiftId);
        if (shift == null) {
            notFound("Không tìm thấy ca làm");
        }
        return shift;
    }

    private StaffSchedule requireSchedule(int scheduleId) {
        StaffSchedule schedule = scheduleRepository.findById(scheduleId);
        if (schedule == null) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Không thể đọc lại lịch làm vừa lưu");
        }
        return schedule;
    }

    private void enforceCinemaScope(AuthUserPrincipal principal, User targetStaff) {
        if (!"MANAGER".equals(principal.role())) {
            return;
        }
        Integer cinemaId = requireScopedCinemaId(principal, "Manager");
        if (!cinemaId.equals(targetStaff.getCinemaId())) {
            forbidden("Manager chỉ được phân công nhân viên cùng rạp");
        }
    }

    private Integer requireScopedCinemaId(AuthUserPrincipal principal, String actorLabel) {
        if (principal.cinemaId() == null || principal.cinemaId() <= 0) {
            forbidden(actorLabel + " chưa được gán rạp");
        }
        return principal.cinemaId();
    }

    private void assertWorkShiftManagePermission() {
        AuthUserPrincipal principal = authService.getPrincipal();
        if (!"ADMIN".equals(principal.role()) && !"MANAGER".equals(principal.role())) {
            forbidden("Bạn không có quyền thao tác ca mẫu");
        }
    }

    private String validateWorkShiftPayload(CreateWorkShiftRequestDTO request, Integer currentShiftId) {
        if (request == null) {
            badRequest("Dữ liệu ca mẫu không hợp lệ");
        }
        if (request.getName() == null) {
            badRequest("Tên ca không được để trống");
        }
        if (request.getStartTime() == null || request.getEndTime() == null) {
            badRequest("Giờ bắt đầu và giờ kết thúc không được để trống");
        }

        String shiftName = request.getName().trim();
        if (shiftName.isBlank()) {
            badRequest("Tên ca không được để trống");
        }
        if (request.getStartTime().equals(request.getEndTime())) {
            badRequest("Giờ bắt đầu và giờ kết thúc không được trùng nhau");
        }

        WorkShift duplicatedShift = currentShiftId == null
                ? shiftRepository.findByNameIgnoreCase(shiftName)
                : shiftRepository.findByNameIgnoreCaseAndIdNot(shiftName, currentShiftId);

        if (duplicatedShift != null) {
            conflict("Tên ca mẫu đã tồn tại");
        }

        return shiftName;
    }

    private void validateStaffSelectionWindow(LocalDate workDate) {
        LocalDate today = todayVN();
        DayOfWeek todayDayOfWeek = today.getDayOfWeek();

        if (todayDayOfWeek != DayOfWeek.SATURDAY && todayDayOfWeek != DayOfWeek.SUNDAY) {
            forbidden("Nhân viên chỉ được đăng ký lịch vào thứ 7 hoặc chủ nhật");
        }

        LocalDate nextWeekMonday = today.with(DayOfWeek.MONDAY).plusWeeks(1);
        LocalDate nextWeekSunday = nextWeekMonday.plusDays(6);

        if (workDate.isBefore(nextWeekMonday) || workDate.isAfter(nextWeekSunday)) {
            badRequest("Nhân viên chỉ được đăng ký lịch của tuần sau");
        }
    }

    private DateRange resolveRange(LocalDate startDate, LocalDate endDate) {
        if (startDate == null && endDate == null) {
            LocalDate monday = todayVN().with(DayOfWeek.MONDAY);
            return new DateRange(monday, monday.plusDays(6));
        }

        if (startDate == null) {
            startDate = endDate.with(DayOfWeek.MONDAY);
        }
        if (endDate == null) {
            endDate = startDate.plusDays(6);
        }
        if (endDate.isBefore(startDate)) {
            badRequest("Khoảng thời gian không hợp lệ");
        }

        return new DateRange(startDate, endDate);
    }

    private boolean isOverlapping(WorkShift firstShift, WorkShift secondShift) {
        int firstStart = toMinutes(firstShift.getStartTime());
        int firstEnd = toEndMinutes(firstShift.getStartTime(), firstShift.getEndTime());
        int secondStart = toMinutes(secondShift.getStartTime());
        int secondEnd = toEndMinutes(secondShift.getStartTime(), secondShift.getEndTime());

        return firstStart < secondEnd && secondStart < firstEnd;
    }

    private int toMinutes(LocalTime time) {
        return time.getHour() * 60 + time.getMinute();
    }

    private int toEndMinutes(LocalTime startTime, LocalTime endTime) {
        int startMinutes = toMinutes(startTime);
        int endMinutes = toMinutes(endTime);
        if (endMinutes <= startMinutes) {
            endMinutes += 24 * 60;
        }
        return endMinutes;
    }

    private LocalDate todayVN() {
        return LocalDate.now(ZONE_VN);
    }

    private void badRequest(String message) {
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, message);
    }

    private void forbidden(String message) {
        throw new ResponseStatusException(HttpStatus.FORBIDDEN, message);
    }

    private void conflict(String message) {
        throw new ResponseStatusException(HttpStatus.CONFLICT, message);
    }

    private void notFound(String message) {
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, message);
    }

    private record DateRange(LocalDate startDate, LocalDate endDate) {
    }
}
