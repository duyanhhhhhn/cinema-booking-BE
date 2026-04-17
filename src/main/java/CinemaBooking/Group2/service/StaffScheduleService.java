package CinemaBooking.Group2.service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import CinemaBooking.Group2.dtos.staff.CreateWorkShiftRequestDTO;
import CinemaBooking.Group2.dtos.staff.CreateStaffUrgentRequestDTO;
import CinemaBooking.Group2.dtos.staff.CreateStaffSwapRequestDTO;
import CinemaBooking.Group2.dtos.staff.ShiftTemplateResponseDTO;
import CinemaBooking.Group2.dtos.staff.StaffRegistrationWindowResponseDTO;
import CinemaBooking.Group2.dtos.staff.StaffResponseDTO;
import CinemaBooking.Group2.dtos.staff.StaffScheduleDetailResponseDTO;
import CinemaBooking.Group2.dtos.staff.StaffScheduleRequestDTO;
import CinemaBooking.Group2.dtos.staff.StaffUrgentRequestResponseDTO;
import CinemaBooking.Group2.dtos.staff.StaffSwapActionRequestDTO;
import CinemaBooking.Group2.dtos.staff.StaffSwapRequestResponseDTO;
import CinemaBooking.Group2.dtos.staff.UpdateStaffRegistrationWindowRequestDTO;
import CinemaBooking.Group2.dtos.auth.UserPositionResponseDTO;
import CinemaBooking.Group2.models.StaffSchedule;
import CinemaBooking.Group2.models.StaffScheduleUrgentRequest;
import CinemaBooking.Group2.models.StaffScheduleSwapRequest;
import CinemaBooking.Group2.models.User;
import CinemaBooking.Group2.models.WorkShift;
import CinemaBooking.Group2.models.Enum.StaffScheduleStatus;
import CinemaBooking.Group2.models.Enum.StaffScheduleUrgentRequestStatus;
import CinemaBooking.Group2.models.Enum.StaffScheduleUrgentRequestType;
import CinemaBooking.Group2.models.Enum.StaffScheduleSwapStatus;
import CinemaBooking.Group2.repositories.ScheduleRepository;
import CinemaBooking.Group2.repositories.ShiftRepository;
import CinemaBooking.Group2.repositories.StaffScheduleUrgentRequestRepository;
import CinemaBooking.Group2.repositories.StaffScheduleSwapRequestRepository;
import CinemaBooking.Group2.repositories.UserRepository;
import CinemaBooking.Group2.security.AuthUserPrincipal;

@Service
public class StaffScheduleService {

    private static final ZoneId ZONE_VN = ZoneId.of("Asia/Ho_Chi_Minh");

    private final ScheduleRepository scheduleRepository;
    private final ShiftRepository shiftRepository;
    private final StaffScheduleUrgentRequestRepository urgentRequestRepository;
    private final StaffScheduleSwapRequestRepository swapRequestRepository;
    private final UserRepository userRepository;
    private final AuthService authService;
    private final NotificationService notificationService;
    private final StaffRegistrationWindowService registrationWindowService;

    public StaffScheduleService(
            ScheduleRepository scheduleRepository,
            ShiftRepository shiftRepository,
            StaffScheduleUrgentRequestRepository urgentRequestRepository,
            StaffScheduleSwapRequestRepository swapRequestRepository,
            UserRepository userRepository,
            AuthService authService,
            NotificationService notificationService,
            StaffRegistrationWindowService registrationWindowService) {
        this.scheduleRepository = scheduleRepository;
        this.shiftRepository = shiftRepository;
        this.urgentRequestRepository = urgentRequestRepository;
        this.swapRequestRepository = swapRequestRepository;
        this.userRepository = userRepository;
        this.authService = authService;
        this.notificationService = notificationService;
        this.registrationWindowService = registrationWindowService;
    }

    public StaffRegistrationWindowResponseDTO getRegistrationWindowStatus() {
        return registrationWindowService.toResponse();
    }

    public StaffRegistrationWindowResponseDTO updateRegistrationWindow(
            UpdateStaffRegistrationWindowRequestDTO request) {
        AuthUserPrincipal principal = authService.getPrincipal();
        if (!"ADMIN".equals(principal.role())) {
            forbidden("Chỉ ADMIN mới được bật hoặc tắt đăng ký ngay");
        }
        if (request == null || request.getForceOpen() == null) {
            badRequest("Thiếu trạng thái mở đăng ký");
        }

        registrationWindowService.setForceOpen(Boolean.TRUE.equals(request.getForceOpen()));
        return registrationWindowService.toResponse();
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
        validateScheduleWriteWindow(request.getWorkDate(), targetShift);

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

    public UserPositionResponseDTO getCurrentUserPosition(User currentUser) {
        UserPositionResponseDTO dto = new UserPositionResponseDTO();
        String role = normalizeRole(currentUser.getRoleName());
        boolean isTicketSeller =
                currentUser.getPosition() == User.UserPosition.TICKET_SELLER;

        dto.setUserId(currentUser.getId());
        dto.setRole(role);
        dto.setCinemaId(currentUser.getCinemaId());
        dto.setPosition(currentUser.getPosition() == null ? null : currentUser.getPosition().name());
        dto.setTicketSeller(isTicketSeller);

        if ("ADMIN".equals(role) || "MANAGER".equals(role)) {
            dto.setHasActiveApprovedShiftNow(false);
            dto.setCanAccessTicketSelling(true);
            return dto;
        }

        ActiveTicketSellingShift activeShift = isTicketSeller
                ? findActiveTicketSellingShift(currentUser)
                : null;

        dto.setHasActiveApprovedShiftNow(activeShift != null);
        dto.setCanAccessTicketSelling(activeShift != null);
        dto.setActiveShift(toActiveShiftResponse(activeShift));
        return dto;
    }

    public boolean canAccessTicketSelling(User currentUser) {
        String role = normalizeRole(currentUser.getRoleName());
        if ("ADMIN".equals(role) || "MANAGER".equals(role)) {
            return true;
        }
        if (!"STAFF".equals(role)) {
            return false;
        }
        if (currentUser.getPosition() != User.UserPosition.TICKET_SELLER) {
            return false;
        }
        return findActiveTicketSellingShift(currentUser) != null;
    }

    public void assertCanAccessTicketSelling(User currentUser) {
        String role = normalizeRole(currentUser.getRoleName());
        if ("ADMIN".equals(role) || "MANAGER".equals(role)) {
            return;
        }
        if (!"STAFF".equals(role)) {
            forbidden("Bạn không có quyền sử dụng chức năng bán vé");
        }
        if (currentUser.getPosition() != User.UserPosition.TICKET_SELLER) {
            forbidden("Chỉ nhân viên có vị trí TICKET_SELLER mới được sử dụng chức năng bán vé");
        }
        if (findActiveTicketSellingShift(currentUser) == null) {
            forbidden("Chỉ nhân viên bán vé đang trong ca làm hợp lệ mới được sử dụng chức năng bán vé");
        }
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

    @Transactional
    public void deleteSchedule(int scheduleId) {
        AuthUserPrincipal principal = authService.getPrincipal();
        StaffSchedule schedule = requireSchedule(scheduleId);
        WorkShift shift = requireShift(schedule.getShiftId());
        validateScheduleWriteWindow(schedule.getWorkDate(), shift);

        if ("STAFF".equals(principal.role())) {
            User actor = requireUserByEmail(principal.email());
            validateStaffScheduleDeletion(actor, schedule);
        } else if ("MANAGER".equals(principal.role()) || "ADMIN".equals(principal.role())) {
            User targetStaff = requireStaff(schedule.getStaffId());
            enforceCinemaScope(principal, targetStaff);
        } else {
            forbidden("Bạn không có quyền xóa lịch làm");
        }

        int deletedRows = scheduleRepository.delete(scheduleId);
        if (deletedRows <= 0) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Không thể xóa lịch làm");
        }
    }

    public List<StaffResponseDTO> getSwapCandidates(int scheduleId) {
        AuthUserPrincipal principal = authService.getPrincipal();
        if (!"STAFF".equals(principal.role())) {
            forbidden("Chỉ STAFF mới được tạo yêu cầu nhờ làm thay");
        }

        User actor = requireUserByEmail(principal.email());
        StaffSchedule schedule = requireSchedule(scheduleId);
        WorkShift shift = requireShift(schedule.getShiftId());
        validateSwapSourceSchedule(actor, schedule, shift);

        List<User> users = userRepository.findActiveStaffByCinemaAndPosition(
                actor.getCinemaId(),
                actor.getPosition() == null ? null : actor.getPosition().name(),
                actor.getId());

        List<StaffResponseDTO> candidates = new ArrayList<>();
        for (User candidate : users) {
            if (canCoverShift(candidate, schedule.getWorkDate(), shift, null)) {
                candidates.add(toStaffResponse(candidate));
            }
        }
        return candidates;
    }

    @Transactional
    public StaffUrgentRequestResponseDTO createUrgentRequest(CreateStaffUrgentRequestDTO request) {
        if (request == null || request.getScheduleId() == null) {
            badRequest("Dữ liệu yêu cầu khẩn không hợp lệ");
        }

        AuthUserPrincipal principal = authService.getPrincipal();
        if (!"STAFF".equals(principal.role())) {
            forbidden("Chỉ STAFF mới được gửi yêu cầu khẩn");
        }

        User actor = requireUserByEmail(principal.email());
        StaffSchedule schedule = requireSchedule(request.getScheduleId());
        WorkShift shift = requireShift(schedule.getShiftId());
        validateUrgentSourceSchedule(actor, schedule, shift);

        StaffScheduleUrgentRequest openRequest = urgentRequestRepository.findOpenByScheduleId(schedule.getId());
        if (openRequest != null) {
            conflict("Ca này đang có một yêu cầu khẩn chờ duyệt");
        }

        StaffScheduleUrgentRequestType type = parseUrgentRequestType(request.getType());
        String reason = normalizeOptionalText(request.getReason());
        if (reason == null) {
            badRequest("Vui lòng nhập lý do");
        }

        LocalTime expectedArrivalTime = null;
        if (type == StaffScheduleUrgentRequestType.LATE_ARRIVAL) {
            expectedArrivalTime = validateExpectedArrivalTime(schedule, shift, request.getExpectedArrivalTime());
        }

        StaffScheduleUrgentRequest urgentRequest = new StaffScheduleUrgentRequest();
        urgentRequest.setScheduleId(schedule.getId());
        urgentRequest.setRequesterStaffId(actor.getId());
        urgentRequest.setType(type);
        urgentRequest.setStatus(StaffScheduleUrgentRequestStatus.PENDING_ADMIN_APPROVAL);
        urgentRequest.setReason(reason);
        urgentRequest.setExpectedArrivalTime(expectedArrivalTime);

        int requestId = urgentRequestRepository.create(urgentRequest);
        if (requestId <= 0) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Không thể tạo yêu cầu khẩn");
        }

        StaffScheduleUrgentRequest savedRequest = requireUrgentRequest(requestId);
        notifyUrgentRequestCreated(savedRequest, actor, schedule, shift);
        return toUrgentResponse(savedRequest, new HashMap<>(), new HashMap<>(), new HashMap<>());
    }

    @Transactional
    public StaffSwapRequestResponseDTO createSwapRequest(CreateStaffSwapRequestDTO request) {
        if (request == null || request.getScheduleId() == null || request.getTargetStaffId() == null) {
            badRequest("Dữ liệu nhờ làm thay không hợp lệ");
        }

        AuthUserPrincipal principal = authService.getPrincipal();
        if (!"STAFF".equals(principal.role())) {
            forbidden("Chỉ STAFF mới được tạo yêu cầu nhờ làm thay");
        }

        User actor = requireUserByEmail(principal.email());
        StaffSchedule schedule = requireSchedule(request.getScheduleId());
        WorkShift shift = requireShift(schedule.getShiftId());
        validateSwapSourceSchedule(actor, schedule, shift);

        StaffScheduleSwapRequest openRequest = swapRequestRepository.findOpenByScheduleId(schedule.getId());
        if (openRequest != null) {
            conflict("Ca này đang có một yêu cầu nhờ làm thay đang chờ xử lý");
        }

        User targetStaff = requireStaff(request.getTargetStaffId());
        validateSwapTarget(actor, targetStaff);

        if (!canCoverShift(targetStaff, schedule.getWorkDate(), shift, null)) {
            conflict("Nhân viên được nhờ phải trống lịch ở ca này");
        }

        StaffScheduleSwapRequest swapRequest = new StaffScheduleSwapRequest();
        swapRequest.setScheduleId(schedule.getId());
        swapRequest.setRequesterStaffId(actor.getId());
        swapRequest.setTargetStaffId(targetStaff.getId());
        swapRequest.setStatus(StaffScheduleSwapStatus.PENDING_STAFF_RESPONSE);
        swapRequest.setNote(normalizeOptionalText(request.getNote()));

        int requestId = swapRequestRepository.create(swapRequest);
        if (requestId <= 0) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Không thể tạo yêu cầu nhờ làm thay");
        }

        StaffScheduleSwapRequest savedRequest = requireSwapRequest(requestId);
        notifySwapRequestCreated(savedRequest, actor, targetStaff, schedule, shift);

        return toSwapResponse(savedRequest, new HashMap<>(), new HashMap<>(), new HashMap<>());
    }

    public List<StaffSwapRequestResponseDTO> getSwapRequests(
            String box,
            StaffScheduleSwapStatus status,
            Integer cinemaIdParam) {

        AuthUserPrincipal principal = authService.getPrincipal();
        User actor = requireUserByEmail(principal.email());
        String normalizedBox = normalizeSwapBox(box, principal.role());

        List<StaffScheduleSwapRequest> requests;
        if ("INCOMING".equals(normalizedBox)) {
            if (!"STAFF".equals(principal.role())) {
                forbidden("Chỉ STAFF mới có hộp yêu cầu đến");
            }
            requests = swapRequestRepository.findByTargetStaff(actor.getId(), status);
        } else if ("OUTGOING".equals(normalizedBox)) {
            if (!"STAFF".equals(principal.role())) {
                forbidden("Chỉ STAFF mới có hộp yêu cầu đã gửi");
            }
            requests = swapRequestRepository.findByRequesterStaff(actor.getId(), status);
        } else {
            if (!"MANAGER".equals(principal.role())) {
                forbidden("Bạn không có quyền xem yêu cầu đổi ca cần duyệt");
            }

            Integer scopedCinemaId = requireScopedCinemaId(principal, "Manager");
            requests = swapRequestRepository.findPendingReviewByCinema(scopedCinemaId);
        }

        Map<Integer, User> staffCache = new HashMap<>();
        Map<Integer, WorkShift> shiftCache = new HashMap<>();
        Map<Integer, StaffSchedule> scheduleCache = new HashMap<>();

        List<StaffSwapRequestResponseDTO> data = new ArrayList<>();
        for (StaffScheduleSwapRequest request : requests) {
            data.add(toSwapResponse(request, staffCache, shiftCache, scheduleCache));
        }
        return data;
    }

    public List<StaffUrgentRequestResponseDTO> getUrgentRequests(
            String box,
            StaffScheduleUrgentRequestStatus status,
            Integer cinemaIdParam) {

        AuthUserPrincipal principal = authService.getPrincipal();
        User actor = requireUserByEmail(principal.email());
        String normalizedBox = normalizeUrgentBox(box, principal.role());

        List<StaffScheduleUrgentRequest> requests;
        if ("OUTGOING".equals(normalizedBox)) {
            if (!"STAFF".equals(principal.role())) {
                forbidden("Chỉ STAFF mới có danh sách yêu cầu khẩn của mình");
            }
            requests = urgentRequestRepository.findByRequesterStaff(actor.getId(), status);
        } else {
            if (!"MANAGER".equals(principal.role()) && !"ADMIN".equals(principal.role())) {
                forbidden("Bạn không có quyền xem yêu cầu khẩn cần duyệt");
            }

            Integer scopedCinemaId = resolveCinemaScheduleScope(principal, cinemaIdParam);
            StaffScheduleUrgentRequestStatus effectiveStatus =
                    status == null ? StaffScheduleUrgentRequestStatus.PENDING_ADMIN_APPROVAL : status;
            requests = urgentRequestRepository.findByReviewCinema(scopedCinemaId, effectiveStatus);
        }

        Map<Integer, User> staffCache = new HashMap<>();
        Map<Integer, WorkShift> shiftCache = new HashMap<>();
        Map<Integer, StaffSchedule> scheduleCache = new HashMap<>();

        List<StaffUrgentRequestResponseDTO> data = new ArrayList<>();
        for (StaffScheduleUrgentRequest request : requests) {
            data.add(toUrgentResponse(request, staffCache, shiftCache, scheduleCache));
        }
        return data;
    }

    @Transactional
    public StaffSwapRequestResponseDTO respondSwapRequest(int requestId, StaffSwapActionRequestDTO request) {
        if (request == null || request.getAction() == null) {
            badRequest("Dữ liệu phản hồi đổi ca không hợp lệ");
        }

        AuthUserPrincipal principal = authService.getPrincipal();
        if (!"STAFF".equals(principal.role())) {
            forbidden("Chỉ STAFF mới được phản hồi yêu cầu đổi ca");
        }

        User actor = requireUserByEmail(principal.email());
        StaffScheduleSwapRequest swapRequest = requireSwapRequest(requestId);
        String action = normalizeOptionalText(request.getAction());
        if (action == null) {
            badRequest("Action không hợp lệ");
        }

        switch (action.toUpperCase()) {
            case "ACCEPT" -> acceptSwapRequest(actor, swapRequest);
            case "REJECT" -> rejectSwapRequest(actor, swapRequest);
            case "CANCEL" -> cancelSwapRequest(actor, swapRequest);
            default -> badRequest("Action phản hồi không hợp lệ");
        }

        return toSwapResponse(requireSwapRequest(requestId), new HashMap<>(), new HashMap<>(), new HashMap<>());
    }

    @Transactional
    public StaffSwapRequestResponseDTO reviewSwapRequest(int requestId, StaffSwapActionRequestDTO request) {
        if (request == null || request.getAction() == null) {
            badRequest("Dữ liệu duyệt đổi ca không hợp lệ");
        }

        AuthUserPrincipal principal = authService.getPrincipal();
        if (!"MANAGER".equals(principal.role())) {
            forbidden("Bạn không có quyền duyệt yêu cầu đổi ca");
        }

        StaffScheduleSwapRequest swapRequest = requireSwapRequest(requestId);
        if (swapRequest.getStatus() != StaffScheduleSwapStatus.PENDING_ADMIN_APPROVAL) {
            conflict("Yêu cầu này không còn ở trạng thái chờ manager duyệt");
        }

        StaffSchedule sourceSchedule = requireSchedule(swapRequest.getScheduleId());
        User requester = requireStaff(swapRequest.getRequesterStaffId());
        User targetStaff = requireStaff(swapRequest.getTargetStaffId());
        enforceCinemaScope(principal, requester);
        enforceCinemaScope(principal, targetStaff);

        String action = normalizeOptionalText(request.getAction());
        if (action == null) {
            badRequest("Action không hợp lệ");
        }

        if ("REJECT".equalsIgnoreCase(action)) {
            swapRequest.setStatus(StaffScheduleSwapStatus.ADMIN_REJECTED);
            swapRequestRepository.update(swapRequest);
            notifySwapReviewResolved(swapRequest, requester, targetStaff, sourceSchedule, false);
            return toSwapResponse(requireSwapRequest(requestId), new HashMap<>(), new HashMap<>(), new HashMap<>());
        }

        if (!"APPROVE".equalsIgnoreCase(action)) {
            badRequest("Action duyệt không hợp lệ");
        }

        if (sourceSchedule.getStaffId() != requester.getId() || sourceSchedule.getStatus() != StaffScheduleStatus.CONFIRMED) {
            conflict("Ca gốc không còn ở trạng thái có thể chuyển");
        }

        WorkShift shift = requireShift(sourceSchedule.getShiftId());
        validateSwapSourceSchedule(requester, sourceSchedule, shift);
        validateSwapTarget(requester, targetStaff);
        StaffSchedule exactTargetSchedule = scheduleRepository.findByStaffShiftAndDate(
                targetStaff.getId(),
                shift.getId(),
                sourceSchedule.getWorkDate());
        if (exactTargetSchedule != null && exactTargetSchedule.getStatus() != StaffScheduleStatus.CANCELLED) {
            conflict("Người làm thay không còn trống lịch ở ca này");
        }
        if (!canCoverShift(
                targetStaff,
                sourceSchedule.getWorkDate(),
                shift,
                exactTargetSchedule == null ? null : exactTargetSchedule.getId())) {
            conflict("Người làm thay không còn trống lịch ở ca này");
        }

        StaffSchedule savedTargetSchedule;
        if (exactTargetSchedule != null) {
            exactTargetSchedule.setStatus(StaffScheduleStatus.CONFIRMED);
            exactTargetSchedule.setRequestedByRole("SWAP_APPROVED");
            savedTargetSchedule = save(exactTargetSchedule);
        } else {
            StaffSchedule newSchedule = new StaffSchedule();
            newSchedule.setStaffId(targetStaff.getId());
            newSchedule.setShiftId(shift.getId());
            newSchedule.setWorkDate(sourceSchedule.getWorkDate());
            newSchedule.setStatus(StaffScheduleStatus.CONFIRMED);
            newSchedule.setRequestedByRole("SWAP_APPROVED");
            savedTargetSchedule = save(newSchedule);
        }

        sourceSchedule.setStatus(StaffScheduleStatus.CANCELLED);
        sourceSchedule.setRequestedByRole("SWAP_TRANSFERRED");
        save(sourceSchedule);

        swapRequest.setStatus(StaffScheduleSwapStatus.ADMIN_APPROVED);
        swapRequest.setApprovedScheduleId(savedTargetSchedule.getId());
        swapRequestRepository.update(swapRequest);
        notifySwapReviewResolved(swapRequest, requester, targetStaff, sourceSchedule, true);

        return toSwapResponse(requireSwapRequest(requestId), new HashMap<>(), new HashMap<>(), new HashMap<>());
    }

    @Transactional
    public StaffUrgentRequestResponseDTO reviewUrgentRequest(int requestId, StaffSwapActionRequestDTO request) {
        if (request == null || request.getAction() == null) {
            badRequest("Dữ liệu duyệt yêu cầu khẩn không hợp lệ");
        }

        AuthUserPrincipal principal = authService.getPrincipal();
        if (!"MANAGER".equals(principal.role()) && !"ADMIN".equals(principal.role())) {
            forbidden("Bạn không có quyền duyệt yêu cầu khẩn");
        }

        StaffScheduleUrgentRequest urgentRequest = requireUrgentRequest(requestId);
        if (urgentRequest.getStatus() != StaffScheduleUrgentRequestStatus.PENDING_ADMIN_APPROVAL) {
            conflict("Yêu cầu khẩn này không còn ở trạng thái chờ duyệt");
        }

        StaffSchedule sourceSchedule = requireSchedule(urgentRequest.getScheduleId());
        User requester = requireStaff(urgentRequest.getRequesterStaffId());
        enforceCinemaScope(principal, requester);
        WorkShift shift = requireShift(sourceSchedule.getShiftId());

        String action = normalizeOptionalText(request.getAction());
        if (action == null) {
            badRequest("Action không hợp lệ");
        }

        if ("REJECT".equalsIgnoreCase(action)) {
            urgentRequest.setStatus(StaffScheduleUrgentRequestStatus.ADMIN_REJECTED);
            urgentRequestRepository.update(urgentRequest);
            notifyUrgentReviewResolved(urgentRequest, requester, sourceSchedule, shift, false);
            return toUrgentResponse(requireUrgentRequest(requestId), new HashMap<>(), new HashMap<>(), new HashMap<>());
        }

        if (!"APPROVE".equalsIgnoreCase(action)) {
            badRequest("Action duyệt không hợp lệ");
        }

        if (sourceSchedule.getStatus() != StaffScheduleStatus.CONFIRMED) {
            conflict("Ca này không còn ở trạng thái có thể xử lý");
        }
        if (hasShiftEnded(sourceSchedule.getWorkDate(), shift)) {
            conflict("Ca này đã kết thúc nên không thể duyệt yêu cầu khẩn");
        }

        if (urgentRequest.getType() == StaffScheduleUrgentRequestType.EMERGENCY_LEAVE) {
            sourceSchedule.setStatus(StaffScheduleStatus.CANCELLED);
            sourceSchedule.setRequestedByRole("EMERGENCY_APPROVED");
            save(sourceSchedule);
        } else {
            validateApprovedLateArrival(sourceSchedule, shift, urgentRequest.getExpectedArrivalTime());
        }

        urgentRequest.setStatus(StaffScheduleUrgentRequestStatus.ADMIN_APPROVED);
        urgentRequestRepository.update(urgentRequest);
        notifyUrgentReviewResolved(urgentRequest, requester, sourceSchedule, shift, true);

        return toUrgentResponse(requireUrgentRequest(requestId), new HashMap<>(), new HashMap<>(), new HashMap<>());
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

        if (request.getStaffId() != null && request.getStaffId() != actor.getId()) {
            forbidden("Nhân viên chỉ được đăng ký lịch cho chính mình");
        }

        StaffScheduleStatus requestedStatus = request.getStatus() == null
                ? StaffScheduleStatus.ASSIGNED
                : request.getStatus();

        if (requestedStatus != StaffScheduleStatus.ASSIGNED) {
            badRequest("Nhân viên chỉ được gửi đăng ký ca làm của chính mình");
        }

        StaffSchedule exactSchedule =
                scheduleRepository.findByStaffShiftAndDate(actor.getId(), targetShift.getId(), request.getWorkDate());

        return handleStaffSelection(actor, request, targetShift, exactSchedule);
    }

    private StaffScheduleDetailResponseDTO handleManagerAssignment(
            AuthUserPrincipal principal,
            StaffScheduleRequestDTO request,
            WorkShift targetShift) {

        if (request.getStaffId() == null || request.getStaffId() <= 0) {
            badRequest("Thiếu nhân viên cần cập nhật lịch");
        }

        User targetStaff = requireStaff(request.getStaffId());
        enforceCinemaScope(principal, targetStaff);

        StaffScheduleStatus targetStatus = request.getStatus();
        if (targetStatus == null) {
            targetStatus = StaffScheduleStatus.CONFIRMED;
        }
        if (targetStatus != StaffScheduleStatus.CONFIRMED
                && targetStatus != StaffScheduleStatus.CANCELLED) {
            forbidden("Manager/Admin chỉ được duyệt hoặc từ chối ca do staff đăng ký.");
        }

        StaffSchedule exactSchedule = scheduleRepository.findByStaffShiftAndDate(
                targetStaff.getId(),
                targetShift.getId(),
                request.getWorkDate());

        if (targetStatus == StaffScheduleStatus.CANCELLED) {
            if (exactSchedule == null) {
                notFound("Không tìm thấy lịch làm để huỷ");
            }
            if (exactSchedule.getStatus() == StaffScheduleStatus.CANCELLED) {
                return toScheduleResponse(exactSchedule, new HashMap<>(), new HashMap<>());
            }

            exactSchedule.setStatus(StaffScheduleStatus.CANCELLED);
            StaffSchedule saved = save(exactSchedule);
            return toScheduleResponse(saved, new HashMap<>(), new HashMap<>());
        }

        if (exactSchedule == null) {
            StaffSchedule saved = createManagerConfirmedSchedule(
                    principal.role(),
                    targetStaff.getId(),
                    request.getWorkDate(),
                    targetShift);
            return toScheduleResponse(saved, new HashMap<>(), new HashMap<>());
        }

        if (exactSchedule.getStatus() == StaffScheduleStatus.CONFIRMED) {
            return toScheduleResponse(exactSchedule, new HashMap<>(), new HashMap<>());
        }

        if (isStaffRequestAwaitingManagerApproval(exactSchedule)) {
            cancelOverlappingSchedules(targetStaff.getId(), request.getWorkDate(), targetShift, exactSchedule.getId());
            exactSchedule.setStatus(StaffScheduleStatus.CONFIRMED);
            StaffSchedule saved = save(exactSchedule);
            return toScheduleResponse(saved, new HashMap<>(), new HashMap<>());
        }

        if (isManagerProposalAwaitingStaffApproval(exactSchedule)
                || exactSchedule.getStatus() == StaffScheduleStatus.CANCELLED) {
            StaffSchedule saved = createManagerConfirmedSchedule(
                    principal.role(),
                    targetStaff.getId(),
                    request.getWorkDate(),
                    targetShift,
                    exactSchedule);
            return toScheduleResponse(saved, new HashMap<>(), new HashMap<>());
        }

        throw new ResponseStatusException(HttpStatus.CONFLICT, "Ca này không ở trạng thái có thể cập nhật");
    }

    private StaffScheduleDetailResponseDTO handleStaffSelection(
            User actor,
            StaffScheduleRequestDTO request,
            WorkShift targetShift,
            StaffSchedule exactSchedule) {

        validateStaffSelectionWindow(request.getWorkDate());

        if (exactSchedule != null && exactSchedule.getStatus() == StaffScheduleStatus.CONFIRMED) {
            return toScheduleResponse(exactSchedule, new HashMap<>(), new HashMap<>());
        }

        validateStaffRequestConflicts(actor.getId(), request.getWorkDate(), targetShift, exactSchedule);

        StaffSchedule saved;
        if (exactSchedule != null) {
            exactSchedule.setStatus(StaffScheduleStatus.ASSIGNED);
            exactSchedule.setRequestedByRole("STAFF");
            saved = save(exactSchedule);
        } else {
            StaffSchedule newSchedule = new StaffSchedule();
            newSchedule.setStaffId(actor.getId());
            newSchedule.setShiftId(targetShift.getId());
            newSchedule.setWorkDate(request.getWorkDate());
            newSchedule.setStatus(StaffScheduleStatus.ASSIGNED);
            newSchedule.setRequestedByRole("STAFF");
            saved = save(newSchedule);
        }

        return toScheduleResponse(saved, new HashMap<>(), new HashMap<>());
    }

    private StaffScheduleDetailResponseDTO handleStaffApproval(
            User actor,
            StaffScheduleRequestDTO request,
            WorkShift targetShift,
            StaffSchedule exactSchedule) {

        if (exactSchedule == null) {
            notFound("Không tìm thấy ca manager đề xuất để xác nhận");
        }

        if (exactSchedule.getStatus() == StaffScheduleStatus.CONFIRMED) {
            return toScheduleResponse(exactSchedule, new HashMap<>(), new HashMap<>());
        }

        if (!isManagerProposalAwaitingStaffApproval(exactSchedule)) {
            badRequest("Nhân viên chỉ được xác nhận ca đang chờ duyệt từ manager");
        }

        cancelOverlappingSchedules(actor.getId(), request.getWorkDate(), targetShift, exactSchedule.getId());

        exactSchedule.setStatus(StaffScheduleStatus.CONFIRMED);
        StaffSchedule saved = save(exactSchedule);
        return toScheduleResponse(saved, new HashMap<>(), new HashMap<>());
    }

    private StaffScheduleDetailResponseDTO handleStaffCancellation(User actor, StaffSchedule exactSchedule) {
        if (exactSchedule == null) {
            notFound("Không tìm thấy lịch làm để huỷ hoặc từ chối");
        }

        if (exactSchedule.getStatus() == StaffScheduleStatus.CONFIRMED) {
            forbidden("Ca đã được chốt, nhân viên không thể tự huỷ");
        }

        if (exactSchedule.getStatus() == StaffScheduleStatus.CANCELLED) {
            return toScheduleResponse(exactSchedule, new HashMap<>(), new HashMap<>());
        }

        if (!isManagerProposalAwaitingStaffApproval(exactSchedule)
                && !isStaffRequestAwaitingManagerApproval(exactSchedule)
                && !"STAFF".equals(normalizeRole(exactSchedule.getRequestedByRole()))) {
            forbidden("Nhân viên không thể thao tác với lịch làm này");
        }

        if (exactSchedule.getStaffId() != actor.getId()) {
            forbidden("Nhân viên chỉ được thao tác với lịch của chính mình");
        }

        exactSchedule.setStatus(StaffScheduleStatus.CANCELLED);
        StaffSchedule saved = save(exactSchedule);
        return toScheduleResponse(saved, new HashMap<>(), new HashMap<>());
    }

    private StaffSchedule createOrRefreshManagerProposal(
            String proposerRole,
            int staffId,
            LocalDate workDate,
            WorkShift targetShift,
            StaffSchedule exactSchedule) {

        validateManagerProposalConflicts(staffId, workDate, targetShift, exactSchedule);

        if (exactSchedule != null) {
            exactSchedule.setStatus(StaffScheduleStatus.ASSIGNED);
            exactSchedule.setRequestedByRole(normalizeRole(proposerRole));
            return save(exactSchedule);
        }

        StaffSchedule newSchedule = new StaffSchedule();
        newSchedule.setStaffId(staffId);
        newSchedule.setShiftId(targetShift.getId());
        newSchedule.setWorkDate(workDate);
        newSchedule.setStatus(StaffScheduleStatus.ASSIGNED);
        newSchedule.setRequestedByRole(normalizeRole(proposerRole));
        return save(newSchedule);
    }

    private StaffSchedule createManagerConfirmedSchedule(
            String proposerRole,
            int staffId,
            LocalDate workDate,
            WorkShift targetShift) {
        return createManagerConfirmedSchedule(proposerRole, staffId, workDate, targetShift, null);
    }

    private StaffSchedule createManagerConfirmedSchedule(
            String proposerRole,
            int staffId,
            LocalDate workDate,
            WorkShift targetShift,
            StaffSchedule exactSchedule) {

        validateManagerProposalConflicts(staffId, workDate, targetShift, exactSchedule);

        if (exactSchedule != null) {
            exactSchedule.setStatus(StaffScheduleStatus.CONFIRMED);
            if (exactSchedule.getRequestedByRole() == null || exactSchedule.getRequestedByRole().isBlank()) {
                exactSchedule.setRequestedByRole(normalizeRole(proposerRole));
            }
            return save(exactSchedule);
        }

        StaffSchedule newSchedule = new StaffSchedule();
        newSchedule.setStaffId(staffId);
        newSchedule.setShiftId(targetShift.getId());
        newSchedule.setWorkDate(workDate);
        newSchedule.setStatus(StaffScheduleStatus.CONFIRMED);
        newSchedule.setRequestedByRole(normalizeRole(proposerRole));
        return save(newSchedule);
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

    private void validateManagerProposalConflicts(
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

            if (isManagerProposalAwaitingStaffApproval(schedule)) {
                schedule.setStatus(StaffScheduleStatus.CANCELLED);
                save(schedule);
                continue;
            }

            if (schedule.getStatus() == StaffScheduleStatus.CONFIRMED) {
                conflict("Nhân viên đã có ca được chốt trùng thời gian");
            }

            if (isStaffRequestAwaitingManagerApproval(schedule)) {
                conflict("Nhân viên đã đăng ký một ca trùng thời gian, cần xử lý yêu cầu đó trước");
            }

            conflict("Đã tồn tại lịch làm trùng thời gian");
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
        dto.setRequestedByRole(normalizeRole(schedule.getRequestedByRole()));
        dto.setCreatedAt(schedule.getCreatedAt());
        dto.setStaff(toStaffResponse(loadStaff(schedule.getStaffId(), staffCache)));
        dto.setShift(toShiftResponse(loadShift(schedule.getShiftId(), shiftCache)));
        return dto;
    }

    private StaffSwapRequestResponseDTO toSwapResponse(
            StaffScheduleSwapRequest request,
            Map<Integer, User> staffCache,
            Map<Integer, WorkShift> shiftCache,
            Map<Integer, StaffSchedule> scheduleCache) {

        StaffSchedule schedule = loadSchedule(request.getScheduleId(), scheduleCache);

        StaffSwapRequestResponseDTO dto = new StaffSwapRequestResponseDTO();
        dto.setId(request.getId());
        dto.setScheduleId(request.getScheduleId());
        dto.setApprovedScheduleId(request.getApprovedScheduleId());
        dto.setStatus(request.getStatus().name());
        dto.setNote(request.getNote());
        dto.setCreatedAt(request.getCreatedAt());
        dto.setUpdatedAt(request.getUpdatedAt());
        dto.setWorkDate(schedule.getWorkDate());
        dto.setSourceScheduleStatus(schedule.getStatus().name());
        dto.setRequester(toStaffResponse(loadStaff(request.getRequesterStaffId(), staffCache)));
        dto.setTarget(toStaffResponse(loadStaff(request.getTargetStaffId(), staffCache)));
        dto.setShift(toShiftResponse(loadShift(schedule.getShiftId(), shiftCache)));
        return dto;
    }

    private StaffUrgentRequestResponseDTO toUrgentResponse(
            StaffScheduleUrgentRequest request,
            Map<Integer, User> staffCache,
            Map<Integer, WorkShift> shiftCache,
            Map<Integer, StaffSchedule> scheduleCache) {

        StaffSchedule schedule = loadSchedule(request.getScheduleId(), scheduleCache);

        StaffUrgentRequestResponseDTO dto = new StaffUrgentRequestResponseDTO();
        dto.setId(request.getId());
        dto.setScheduleId(request.getScheduleId());
        dto.setType(request.getType().name());
        dto.setStatus(request.getStatus().name());
        dto.setReason(request.getReason());
        dto.setExpectedArrivalTime(request.getExpectedArrivalTime());
        dto.setCreatedAt(request.getCreatedAt());
        dto.setUpdatedAt(request.getUpdatedAt());
        dto.setWorkDate(schedule.getWorkDate());
        dto.setSourceScheduleStatus(schedule.getStatus().name());
        dto.setRequester(toStaffResponse(loadStaff(request.getRequesterStaffId(), staffCache)));
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

    private UserPositionResponseDTO.ActiveShiftDTO toActiveShiftResponse(ActiveTicketSellingShift activeShift) {
        if (activeShift == null) {
            return null;
        }

        UserPositionResponseDTO.ActiveShiftDTO dto = new UserPositionResponseDTO.ActiveShiftDTO();
        dto.setScheduleId(activeShift.schedule().getId());
        dto.setShiftId(activeShift.shift().getId());
        dto.setWorkDate(activeShift.schedule().getWorkDate());
        dto.setShiftName(activeShift.shift().getName());
        dto.setStartTime(activeShift.shift().getStartTime());
        dto.setEndTime(activeShift.shift().getEndTime());
        dto.setStatus(activeShift.schedule().getStatus().name());
        dto.setApprovedLateArrivalTime(activeShift.approvedLateArrivalTime());
        return dto;
    }

    private void validateSwapSourceSchedule(User actor, StaffSchedule schedule, WorkShift shift) {
        if (schedule.getStaffId() != actor.getId()) {
            forbidden("Bạn chỉ được tạo yêu cầu nhờ làm thay cho ca của chính mình");
        }
        if (schedule.getStatus() != StaffScheduleStatus.CONFIRMED) {
            conflict("Chỉ ca đã được chốt mới có thể nhờ làm thay");
        }
        if (actor.getPosition() == null) {
            conflict("Nhân viên chưa được gán vị trí công việc");
        }
        if (hasShiftStarted(schedule.getWorkDate(), shift)) {
            conflict("Chỉ được nhờ làm thay trước khi ca bắt đầu");
        }
    }

    private void validateSwapTarget(User actor, User targetStaff) {
        if (targetStaff.getId() == actor.getId()) {
            badRequest("Không thể tự nhờ chính mình làm thay");
        }
        if (targetStaff.getCinemaId() != actor.getCinemaId()) {
            conflict("Chỉ được nhờ nhân viên cùng rạp làm thay");
        }
        if (actor.getPosition() == null || targetStaff.getPosition() == null) {
            conflict("Nhân viên phải có vị trí công việc rõ ràng mới được đổi ca");
        }
        if (actor.getPosition() != targetStaff.getPosition()) {
            conflict("Chỉ nhân viên cùng vị trí công việc mới được làm thay ca cho nhau");
        }
    }

    private void validateStaffScheduleDeletion(User actor, StaffSchedule schedule) {
        if (schedule.getStaffId() != actor.getId()) {
            forbidden("Bạn chỉ được xóa lịch của chính mình");
        }
        if (schedule.getStatus() == StaffScheduleStatus.CONFIRMED) {
            forbidden("Ca đã được chốt, hãy dùng nhờ làm thay hoặc yêu cầu khẩn");
        }

        String requestedByRole = normalizeRole(schedule.getRequestedByRole());
        if ("MANAGER".equals(requestedByRole) || "ADMIN".equals(requestedByRole)) {
            forbidden("Ca do manager đề xuất cần phản hồi bằng xác nhận hoặc từ chối");
        }
        if ("SWAP_TRANSFERRED".equals(requestedByRole) || "EMERGENCY_APPROVED".equals(requestedByRole)) {
            forbidden("Không thể xóa lịch đã phát sinh từ điều phối vận hành");
        }
    }

    private void validateUrgentSourceSchedule(User actor, StaffSchedule schedule, WorkShift shift) {
        if (schedule.getStaffId() != actor.getId()) {
            forbidden("Bạn chỉ được gửi yêu cầu khẩn cho ca của chính mình");
        }
        if (schedule.getStatus() != StaffScheduleStatus.CONFIRMED) {
            conflict("Chỉ ca đã được chốt mới được gửi yêu cầu khẩn");
        }
        if (!todayVN().equals(schedule.getWorkDate())) {
            conflict("Chỉ ca trong hôm nay mới được gửi yêu cầu khẩn hoặc xin đi muộn");
        }
        if (hasShiftEnded(schedule.getWorkDate(), shift)) {
            conflict("Ca này đã kết thúc nên không thể gửi yêu cầu khẩn");
        }
    }

    private StaffScheduleUrgentRequestType parseUrgentRequestType(String type) {
        String normalizedType = normalizeOptionalText(type);
        if (normalizedType == null) {
            badRequest("Loại yêu cầu khẩn không hợp lệ");
        }

        try {
            return StaffScheduleUrgentRequestType.valueOf(normalizedType.toUpperCase());
        } catch (IllegalArgumentException ex) {
            badRequest("Loại yêu cầu khẩn không hợp lệ");
            return StaffScheduleUrgentRequestType.EMERGENCY_LEAVE;
        }
    }

    private LocalTime validateExpectedArrivalTime(
            StaffSchedule schedule,
            WorkShift shift,
            LocalTime expectedArrivalTime) {

        if (expectedArrivalTime == null) {
            badRequest("Vui lòng chọn giờ dự kiến đi muộn");
        }

        ShiftDateTimeRange range = buildShiftDateTimeRange(schedule.getWorkDate(), shift);
        LocalDateTime expectedArrivalDateTime =
                normalizeShiftTime(schedule.getWorkDate(), expectedArrivalTime, range);

        if (!expectedArrivalDateTime.isAfter(range.startDateTime())) {
            conflict("Giờ đến dự kiến phải sau giờ bắt đầu ca");
        }
        if (!expectedArrivalDateTime.isBefore(range.endDateTime())) {
            conflict("Giờ đến dự kiến phải nằm trong khung ca làm");
        }
        if (!expectedArrivalDateTime.isAfter(nowVN())) {
            conflict("Giờ đến dự kiến phải ở tương lai");
        }

        return expectedArrivalTime;
    }

    private void validateApprovedLateArrival(
            StaffSchedule schedule,
            WorkShift shift,
            LocalTime expectedArrivalTime) {
        if (expectedArrivalTime == null) {
            conflict("Yêu cầu đi muộn đang thiếu giờ đến dự kiến");
        }

        ShiftDateTimeRange range = buildShiftDateTimeRange(schedule.getWorkDate(), shift);
        LocalDateTime expectedArrivalDateTime =
                normalizeShiftTime(schedule.getWorkDate(), expectedArrivalTime, range);
        if (!expectedArrivalDateTime.isBefore(range.endDateTime())) {
            conflict("Giờ đến dự kiến không còn nằm trong ca làm");
        }
    }

    private ActiveTicketSellingShift findActiveTicketSellingShift(User currentUser) {
        LocalDate today = todayVN();
        LocalDateTime now = nowVN();
        List<StaffSchedule> schedules = scheduleRepository.findByStaffAndRange(
                currentUser.getId(),
                today.minusDays(1),
                today,
                null);

        for (StaffSchedule schedule : schedules) {
            if (schedule.getStatus() != StaffScheduleStatus.CONFIRMED) {
                continue;
            }

            WorkShift shift = requireShift(schedule.getShiftId());
            ShiftDateTimeRange range = buildShiftDateTimeRange(schedule.getWorkDate(), shift);
            if (now.isBefore(range.startDateTime()) || !now.isBefore(range.endDateTime())) {
                continue;
            }

            LocalTime approvedLateArrivalTime = findApprovedLateArrivalTime(schedule.getId());
            if (approvedLateArrivalTime != null) {
                LocalDateTime expectedArrivalDateTime =
                        normalizeShiftTime(schedule.getWorkDate(), approvedLateArrivalTime, range);
                if (!expectedArrivalDateTime.isBefore(range.endDateTime())
                        || now.isBefore(expectedArrivalDateTime)) {
                    continue;
                }
            }

            return new ActiveTicketSellingShift(schedule, shift, approvedLateArrivalTime);
        }

        return null;
    }

    private LocalTime findApprovedLateArrivalTime(int scheduleId) {
        StaffScheduleUrgentRequest request = urgentRequestRepository.findLatestApprovedByScheduleId(scheduleId);
        if (request == null || request.getType() != StaffScheduleUrgentRequestType.LATE_ARRIVAL) {
            return null;
        }
        return request.getExpectedArrivalTime();
    }

    private boolean canCoverShift(User targetStaff, LocalDate workDate, WorkShift shift, Integer exactScheduleId) {
        List<StaffSchedule> sameDaySchedules = scheduleRepository.findByStaffAndDate(targetStaff.getId(), workDate);

        for (StaffSchedule schedule : sameDaySchedules) {
            if (schedule.getStatus() == StaffScheduleStatus.CANCELLED) {
                continue;
            }
            if (exactScheduleId != null && schedule.getId() == exactScheduleId) {
                continue;
            }

            WorkShift existingShift = requireShift(schedule.getShiftId());
            if (!isOverlapping(shift, existingShift)) {
                continue;
            }

            return false;
        }

        return true;
    }

    private String normalizeSwapBox(String box, String role) {
        String normalizedRole = normalizeRole(role);
        String normalizedBox = normalizeOptionalText(box);
        if (normalizedBox == null) {
            return "STAFF".equals(normalizedRole) ? "INCOMING" : "REVIEW";
        }

        return normalizedBox.toUpperCase();
    }

    private String normalizeUrgentBox(String box, String role) {
        String normalizedRole = normalizeRole(role);
        String normalizedBox = normalizeOptionalText(box);
        if (normalizedBox == null) {
            return "STAFF".equals(normalizedRole) ? "OUTGOING" : "REVIEW";
        }

        return normalizedBox.toUpperCase();
    }

    private void acceptSwapRequest(User actor, StaffScheduleSwapRequest swapRequest) {
        if (swapRequest.getTargetStaffId() != actor.getId()) {
            forbidden("Bạn không phải nhân viên được nhờ làm thay của yêu cầu này");
        }
        if (swapRequest.getStatus() != StaffScheduleSwapStatus.PENDING_STAFF_RESPONSE) {
            conflict("Yêu cầu này không còn chờ nhân viên phản hồi");
        }

        StaffSchedule schedule = requireSchedule(swapRequest.getScheduleId());
        WorkShift shift = requireShift(schedule.getShiftId());
        User requester = requireStaff(swapRequest.getRequesterStaffId());
        validateSwapSourceSchedule(requester, schedule, shift);
        validateSwapTarget(requester, actor);
        if (!canCoverShift(actor, schedule.getWorkDate(), shift, null)) {
            conflict("Bạn không trống lịch ở ca này nên không thể nhận làm thay");
        }

        List<User> managers = requireSwapReviewManagers(actor.getCinemaId());

        swapRequest.setStatus(StaffScheduleSwapStatus.PENDING_ADMIN_APPROVAL);
        swapRequestRepository.update(swapRequest);

        notifySwapAcceptedByTarget(swapRequest, requester, actor, schedule, shift, managers);
    }

    private void rejectSwapRequest(User actor, StaffScheduleSwapRequest swapRequest) {
        if (swapRequest.getTargetStaffId() != actor.getId()) {
            forbidden("Bạn không phải nhân viên được nhờ làm thay của yêu cầu này");
        }
        if (swapRequest.getStatus() != StaffScheduleSwapStatus.PENDING_STAFF_RESPONSE) {
            conflict("Yêu cầu này không còn chờ nhân viên phản hồi");
        }

        swapRequest.setStatus(StaffScheduleSwapStatus.STAFF_REJECTED);
        swapRequestRepository.update(swapRequest);

        User requester = requireStaff(swapRequest.getRequesterStaffId());
        StaffSchedule schedule = requireSchedule(swapRequest.getScheduleId());
        notifySwapRejectedByTarget(swapRequest, requester, actor, schedule);
    }

    private void cancelSwapRequest(User actor, StaffScheduleSwapRequest swapRequest) {
        if (swapRequest.getRequesterStaffId() != actor.getId()) {
            forbidden("Chỉ người gửi yêu cầu mới có thể hủy yêu cầu này");
        }
        if (swapRequest.getStatus() != StaffScheduleSwapStatus.PENDING_STAFF_RESPONSE
                && swapRequest.getStatus() != StaffScheduleSwapStatus.PENDING_ADMIN_APPROVAL) {
            conflict("Yêu cầu này không còn ở trạng thái có thể hủy");
        }

        swapRequest.setStatus(StaffScheduleSwapStatus.CANCELLED);
        swapRequestRepository.update(swapRequest);

        User targetStaff = requireStaff(swapRequest.getTargetStaffId());
        StaffSchedule schedule = requireSchedule(swapRequest.getScheduleId());
        notifySwapCancelledByRequester(swapRequest, actor, targetStaff, schedule);
    }

    private void notifySwapRequestCreated(
            StaffScheduleSwapRequest swapRequest,
            User requester,
            User targetStaff,
            StaffSchedule schedule,
            WorkShift shift) {

        String message = requester.getFullName()
                + " muốn nhờ bạn làm thay ca "
                + buildShiftSummary(schedule, shift)
                + buildReasonSuffix(swapRequest.getNote());

        notifyUserSafely(
                targetStaff.getId(),
                "STAFF_SWAP_REQUEST",
                "Có yêu cầu làm thay mới",
                message,
                buildStaffSwapActionUrl(swapRequest.getId()),
                "Xem yêu cầu",
                "STAFF_SWAP_REQUEST",
                swapRequest.getId());
    }

    private void notifySwapAcceptedByTarget(
            StaffScheduleSwapRequest swapRequest,
            User requester,
            User targetStaff,
            StaffSchedule schedule,
            WorkShift shift,
            List<User> managers) {

        String requesterMessage = targetStaff.getFullName()
                + " đã đồng ý làm thay ca "
                + buildShiftSummary(schedule, shift)
                + ". Yêu cầu đang chờ manager chi nhánh duyệt.";

        notifyUserSafely(
                requester.getId(),
                "STAFF_SWAP_ACCEPTED",
                "Yêu cầu làm thay đã được đồng ý",
                requesterMessage,
                buildStaffSwapActionUrl(swapRequest.getId()),
                "Xem trạng thái",
                "STAFF_SWAP_REQUEST",
                swapRequest.getId());

        notifyManagersForSwapReview(swapRequest, requester, targetStaff, schedule, shift, managers);
    }

    private void notifySwapRejectedByTarget(
            StaffScheduleSwapRequest swapRequest,
            User requester,
            User targetStaff,
            StaffSchedule schedule) {

        WorkShift shift = requireShift(schedule.getShiftId());
        String message = targetStaff.getFullName()
                + " đã từ chối làm thay ca "
                + buildShiftSummary(schedule, shift)
                + ". Bạn có thể chọn nhân sự khác phù hợp.";

        notifyUserSafely(
                requester.getId(),
                "STAFF_SWAP_REJECTED",
                "Yêu cầu làm thay đã bị từ chối",
                message,
                buildStaffSwapActionUrl(swapRequest.getId()),
                "Mở nhờ làm thay",
                "STAFF_SWAP_REQUEST",
                swapRequest.getId());
    }

    private void notifySwapCancelledByRequester(
            StaffScheduleSwapRequest swapRequest,
            User requester,
            User targetStaff,
            StaffSchedule schedule) {

        WorkShift shift = requireShift(schedule.getShiftId());
        String message = requester.getFullName()
                + " đã hủy yêu cầu làm thay cho ca "
                + buildShiftSummary(schedule, shift)
                + ".";

        notifyUserSafely(
                targetStaff.getId(),
                "STAFF_SWAP_CANCELLED",
                "Yêu cầu làm thay đã bị hủy",
                message,
                buildStaffSwapActionUrl(swapRequest.getId()),
                "Mở nhờ làm thay",
                "STAFF_SWAP_REQUEST",
                swapRequest.getId());
    }

    private void notifyManagersForSwapReview(
            StaffScheduleSwapRequest swapRequest,
            User requester,
            User targetStaff,
            StaffSchedule schedule,
            WorkShift shift,
            List<User> managers) {

        Set<Integer> recipientIds = new LinkedHashSet<>();
        for (User manager : managers) {
            recipientIds.add(manager.getId());
        }

        String message = requester.getFullName()
                + " nhờ "
                + targetStaff.getFullName()
                + " làm thay ca "
                + buildShiftSummary(schedule, shift)
                + buildReasonSuffix(swapRequest.getNote())
                + ". Vui lòng kiểm tra và duyệt yêu cầu.";

        for (Integer recipientId : recipientIds) {
            notifyUserSafely(
                    recipientId,
                    "STAFF_SWAP_REVIEW",
                    "Có yêu cầu làm thay chờ duyệt",
                    message,
                    buildManagerSwapActionUrl(swapRequest.getId()),
                    "Mở trang duyệt",
                    "STAFF_SWAP_REQUEST",
                    swapRequest.getId());
        }
    }

    private void notifySwapReviewResolved(
            StaffScheduleSwapRequest swapRequest,
            User requester,
            User targetStaff,
            StaffSchedule schedule,
            boolean approved) {

        WorkShift shift = requireShift(schedule.getShiftId());
        String shiftSummary = buildShiftSummary(schedule, shift);
        String requesterTitle = approved
                ? "Yêu cầu làm thay đã được duyệt"
                : "Yêu cầu làm thay không được duyệt";
        String requesterMessage = approved
                ? "Manager chi nhánh đã duyệt yêu cầu làm thay cho ca " + shiftSummary + "."
                : "Manager chi nhánh đã từ chối yêu cầu làm thay cho ca " + shiftSummary + ".";
        String targetTitle = approved
                ? "Ca làm thay đã được duyệt"
                : "Yêu cầu làm thay không được duyệt";
        String targetMessage = approved
                ? "Bạn đã được duyệt làm thay ca " + shiftSummary + "."
                : "Yêu cầu làm thay cho ca " + shiftSummary + " đã bị từ chối.";

        notifyUserSafely(
                requester.getId(),
                approved ? "STAFF_SWAP_APPROVED" : "STAFF_SWAP_ADMIN_REJECTED",
                requesterTitle,
                requesterMessage,
                buildStaffSwapActionUrl(swapRequest.getId()),
                "Xem kết quả",
                "STAFF_SWAP_REQUEST",
                swapRequest.getId());

        notifyUserSafely(
                targetStaff.getId(),
                approved ? "STAFF_SWAP_APPROVED" : "STAFF_SWAP_ADMIN_REJECTED",
                targetTitle,
                targetMessage,
                buildStaffSwapActionUrl(swapRequest.getId()),
                "Xem kết quả",
                "STAFF_SWAP_REQUEST",
                swapRequest.getId());
    }

    private void notifyUrgentRequestCreated(
            StaffScheduleUrgentRequest urgentRequest,
            User requester,
            StaffSchedule schedule,
            WorkShift shift) {

        for (User reviewer : requireUrgentReviewUsers(requester.getCinemaId())) {
            notifyUserSafely(
                    reviewer.getId(),
                    urgentRequest.getType() == StaffScheduleUrgentRequestType.LATE_ARRIVAL
                            ? "STAFF_LATE_REQUEST"
                            : "STAFF_URGENT_REQUEST",
                    "Có yêu cầu khẩn chờ duyệt",
                    buildUrgentRequestReviewMessage(urgentRequest, requester, schedule, shift),
                    buildManagerUrgentActionUrl(urgentRequest.getId()),
                    "Mở lịch làm",
                    "STAFF_URGENT_REQUEST",
                    urgentRequest.getId());
        }
    }

    private void notifyUrgentReviewResolved(
            StaffScheduleUrgentRequest urgentRequest,
            User requester,
            StaffSchedule schedule,
            WorkShift shift,
            boolean approved) {

        String shiftSummary = buildShiftSummary(schedule, shift);
        String requestLabel = urgentRequest.getType() == StaffScheduleUrgentRequestType.LATE_ARRIVAL
                ? "xin đi muộn"
                : "hủy khẩn";
        String title = approved
                ? "Yêu cầu " + requestLabel + " đã được duyệt"
                : "Yêu cầu " + requestLabel + " không được duyệt";
        String message = approved
                ? "Quản lý đã duyệt yêu cầu " + requestLabel + " cho ca " + shiftSummary + "."
                : "Quản lý đã từ chối yêu cầu " + requestLabel + " cho ca " + shiftSummary + ".";

        if (approved && urgentRequest.getType() == StaffScheduleUrgentRequestType.LATE_ARRIVAL
                && urgentRequest.getExpectedArrivalTime() != null) {
            message += " Thời gian có mặt dự kiến: " + formatTime(urgentRequest.getExpectedArrivalTime()) + ".";
        }

        notifyUserSafely(
                requester.getId(),
                approved
                        ? (urgentRequest.getType() == StaffScheduleUrgentRequestType.LATE_ARRIVAL
                                ? "STAFF_LATE_APPROVED"
                                : "STAFF_URGENT_APPROVED")
                        : (urgentRequest.getType() == StaffScheduleUrgentRequestType.LATE_ARRIVAL
                                ? "STAFF_LATE_REJECTED"
                                : "STAFF_URGENT_REJECTED"),
                title,
                message,
                buildStaffUrgentActionUrl(urgentRequest.getId()),
                "Xem trạng thái",
                "STAFF_URGENT_REQUEST",
                urgentRequest.getId());
    }

    private void notifyUserSafely(
            int recipientUserId,
            String type,
            String title,
            String message,
            String actionUrl,
            String actionLabel,
            String relatedEntityType,
            Integer relatedEntityId) {

        if (recipientUserId <= 0) {
            return;
        }

        try {
            notificationService.createNotification(
                    recipientUserId,
                    type,
                    title,
                    message,
                    actionUrl,
                    actionLabel,
                    relatedEntityType,
                    relatedEntityId);
        } catch (RuntimeException ignored) {
        }
    }

    private String buildStaffSwapActionUrl(int requestId) {
        return "/admin/staff-schedules/my/swaps?focusRequest=" + requestId;
    }

    private String buildManagerSwapActionUrl(int requestId) {
        return "/admin/staff-schedules/swaps?focusRequest=" + requestId;
    }

    private String buildStaffUrgentActionUrl(int requestId) {
        return "/admin/staff-schedules/my?focusUrgentRequest=" + requestId;
    }

    private String buildManagerUrgentActionUrl(int requestId) {
        return "/admin/staff-schedules?focusUrgentRequest=" + requestId;
    }

    private List<User> requireSwapReviewManagers(Integer cinemaId) {
        List<User> managers = userRepository.findActiveUsersByRoleNameAndCinema("MANAGER", cinemaId);
        if (managers == null || managers.isEmpty()) {
            conflict("Chi nhánh này chưa có manager để duyệt yêu cầu làm thay");
        }
        return managers;
    }

    private List<User> requireUrgentReviewUsers(Integer cinemaId) {
        Set<Integer> ids = new LinkedHashSet<>();
        List<User> reviewers = new ArrayList<>();

        List<User> managers = userRepository.findActiveUsersByRoleNameAndCinema("MANAGER", cinemaId);
        for (User manager : managers) {
            if (ids.add(manager.getId())) {
                reviewers.add(manager);
            }
        }

        List<User> admins = userRepository.findActiveUsersByRoleName("ADMIN");
        for (User admin : admins) {
            if (ids.add(admin.getId())) {
                reviewers.add(admin);
            }
        }

        if (reviewers.isEmpty()) {
            conflict("Hiện chưa có quản lý hoặc admin để duyệt yêu cầu khẩn");
        }
        return reviewers;
    }

    private String buildShiftSummary(StaffSchedule schedule, WorkShift shift) {
        return schedule.getWorkDate()
                + " • "
                + shift.getName()
                + " ("
                + formatTime(shift.getStartTime())
                + " - "
                + formatTime(shift.getEndTime())
                + ")";
    }

    private String buildReasonSuffix(String note) {
        String normalizedNote = normalizeOptionalText(note);
        if (normalizedNote == null) {
            return ".";
        }
        return ". Lý do: " + normalizedNote;
    }

    private String buildUrgentRequestReviewMessage(
            StaffScheduleUrgentRequest urgentRequest,
            User requester,
            StaffSchedule schedule,
            WorkShift shift) {

        StringBuilder message = new StringBuilder();
        if (urgentRequest.getType() == StaffScheduleUrgentRequestType.LATE_ARRIVAL) {
            message.append(requester.getFullName())
                    .append(" xin đi muộn cho ca ")
                    .append(buildShiftSummary(schedule, shift));
            if (urgentRequest.getExpectedArrivalTime() != null) {
                message.append(". Dự kiến có mặt lúc ")
                        .append(formatTime(urgentRequest.getExpectedArrivalTime()));
            }
        } else {
            message.append(requester.getFullName())
                    .append(" xin hủy khẩn ca ")
                    .append(buildShiftSummary(schedule, shift));
        }

        message.append(buildReasonSuffix(urgentRequest.getReason()))
                .append(" Vui lòng kiểm tra và duyệt yêu cầu.");
        return message.toString();
    }

    private String formatTime(LocalTime time) {
        if (time == null) {
            return "--:--";
        }
        return String.format("%02d:%02d", time.getHour(), time.getMinute());
    }

    private User loadStaff(int staffId, Map<Integer, User> staffCache) {
        return staffCache.computeIfAbsent(staffId, this::requireUserById);
    }

    private WorkShift loadShift(int shiftId, Map<Integer, WorkShift> shiftCache) {
        return shiftCache.computeIfAbsent(shiftId, this::requireShift);
    }

    private StaffSchedule loadSchedule(int scheduleId, Map<Integer, StaffSchedule> scheduleCache) {
        return scheduleCache.computeIfAbsent(scheduleId, this::requireSchedule);
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
            notFound("Không tìm thấy lịch làm");
        }
        return schedule;
    }

    private StaffScheduleSwapRequest requireSwapRequest(int requestId) {
        StaffScheduleSwapRequest request = swapRequestRepository.findById(requestId);
        if (request == null) {
            notFound("Không tìm thấy yêu cầu đổi ca");
        }
        return request;
    }

    private StaffScheduleUrgentRequest requireUrgentRequest(int requestId) {
        StaffScheduleUrgentRequest request = urgentRequestRepository.findById(requestId);
        if (request == null) {
            notFound("Không tìm thấy yêu cầu khẩn");
        }
        return request;
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

    private boolean requiresStaffApprovalForManagerProposal(LocalDate workDate, StaffSchedule exactSchedule) {
        if (!isNextWeek(workDate)) {
            return false;
        }
        if (exactSchedule == null) {
            return true;
        }
        if (exactSchedule.getStatus() == StaffScheduleStatus.CONFIRMED) {
            return false;
        }
        return !isStaffRequestAwaitingManagerApproval(exactSchedule);
    }

    private boolean isStaffRequestAwaitingManagerApproval(StaffSchedule schedule) {
        return schedule != null
                && schedule.getStatus() == StaffScheduleStatus.ASSIGNED
                && "STAFF".equals(normalizeRole(schedule.getRequestedByRole()));
    }

    private boolean isManagerProposalAwaitingStaffApproval(StaffSchedule schedule) {
        if (schedule == null || schedule.getStatus() != StaffScheduleStatus.ASSIGNED) {
            return false;
        }

        String requestedByRole = normalizeRole(schedule.getRequestedByRole());
        return "MANAGER".equals(requestedByRole) || "ADMIN".equals(requestedByRole);
    }

    private boolean isNextWeek(LocalDate workDate) {
        LocalDate nextWeekMonday = todayVN().with(DayOfWeek.MONDAY).plusWeeks(1);
        LocalDate nextWeekSunday = nextWeekMonday.plusDays(6);
        return !workDate.isBefore(nextWeekMonday) && !workDate.isAfter(nextWeekSunday);
    }

    private String normalizeRole(String role) {
        return role == null ? null : role.trim().toUpperCase();
    }

    private String normalizeOptionalText(String value) {
        if (value == null) {
            return null;
        }
        String normalized = value.trim();
        return normalized.isBlank() ? null : normalized;
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

    private void validateScheduleWriteWindow(LocalDate workDate, WorkShift targetShift) {
        if (workDate.isBefore(todayVN()) || hasShiftStarted(workDate, targetShift)) {
            conflict("Không thể tạo hoặc chỉnh sửa lịch cho thời gian đã qua hoặc ca đã bắt đầu");
        }
    }

    private void validateStaffSelectionWindow(LocalDate workDate) {
        LocalDate today = todayVN();
        if (!registrationWindowService.canStaffRegisterToday()) {
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

    private boolean hasShiftStarted(LocalDate workDate, WorkShift shift) {
        return !buildShiftDateTimeRange(workDate, shift).startDateTime().isAfter(nowVN());
    }

    private boolean hasShiftEnded(LocalDate workDate, WorkShift shift) {
        return !buildShiftDateTimeRange(workDate, shift).endDateTime().isAfter(nowVN());
    }

    private ShiftDateTimeRange buildShiftDateTimeRange(LocalDate workDate, WorkShift shift) {
        LocalDateTime startDateTime = LocalDateTime.of(workDate, shift.getStartTime());
        LocalDateTime endDateTime = LocalDateTime.of(workDate, shift.getEndTime());
        if (!endDateTime.isAfter(startDateTime)) {
            endDateTime = endDateTime.plusDays(1);
        }
        return new ShiftDateTimeRange(startDateTime, endDateTime);
    }

    private LocalDateTime normalizeShiftTime(
            LocalDate workDate,
            LocalTime time,
            ShiftDateTimeRange range) {

        LocalDateTime dateTime = LocalDateTime.of(workDate, time);
        if (range.endDateTime().toLocalDate().isAfter(range.startDateTime().toLocalDate())
                && dateTime.isBefore(range.startDateTime())) {
            dateTime = dateTime.plusDays(1);
        }
        return dateTime;
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

    private LocalDateTime nowVN() {
        return LocalDateTime.now(ZONE_VN);
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

    private record ShiftDateTimeRange(LocalDateTime startDateTime, LocalDateTime endDateTime) {
    }

    private record ActiveTicketSellingShift(
            StaffSchedule schedule,
            WorkShift shift,
            LocalTime approvedLateArrivalTime) {
    }
}
