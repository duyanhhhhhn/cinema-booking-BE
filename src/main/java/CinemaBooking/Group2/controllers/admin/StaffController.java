package CinemaBooking.Group2.controllers.admin;

import java.time.LocalDate;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import CinemaBooking.Group2.dtos.ApiResponse;
import CinemaBooking.Group2.dtos.staff.CreateWorkShiftRequestDTO;
import CinemaBooking.Group2.dtos.staff.ShiftTemplateResponseDTO;
import CinemaBooking.Group2.dtos.staff.StaffScheduleDetailResponseDTO;
import CinemaBooking.Group2.dtos.staff.StaffScheduleRequestDTO;
import CinemaBooking.Group2.models.Enum.StaffScheduleStatus;
import CinemaBooking.Group2.service.StaffScheduleService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/staff")
@Validated
public class StaffController {

    private final StaffScheduleService staffScheduleService;

    public StaffController(StaffScheduleService staffScheduleService) {
        this.staffScheduleService = staffScheduleService;
    }

    @GetMapping("/shifts")
    public ResponseEntity<ApiResponse<List<ShiftTemplateResponseDTO>>> getShiftTemplates() {
        List<ShiftTemplateResponseDTO> data = staffScheduleService.getShiftTemplates();
        return ResponseEntity.ok(new ApiResponse<>("Lấy danh sách ca làm thành công", data));
    }

    @GetMapping("/work_shift/{id}")
    public ResponseEntity<ApiResponse<ShiftTemplateResponseDTO>> getWorkShift(@PathVariable int id) {
        ShiftTemplateResponseDTO data = staffScheduleService.getWorkShift(id);
        return ResponseEntity.ok(new ApiResponse<>("Lấy chi tiết ca làm thành công", data));
    }

    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER')")
    @PostMapping("/work_shift")
    public ResponseEntity<ApiResponse<ShiftTemplateResponseDTO>> createWorkShift(
            @Valid @RequestBody CreateWorkShiftRequestDTO request) {

        ShiftTemplateResponseDTO data = staffScheduleService.createWorkShift(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>("Tạo ca mẫu thành công", data));
    }

    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER')")
    @PutMapping("/work_shift/{id}")
    public ResponseEntity<ApiResponse<ShiftTemplateResponseDTO>> updateWorkShift(
            @PathVariable int id,
            @Valid @RequestBody CreateWorkShiftRequestDTO request) {

        ShiftTemplateResponseDTO data = staffScheduleService.updateWorkShift(id, request);
        return ResponseEntity.ok(new ApiResponse<>("Cập nhật ca mẫu thành công", data));
    }

    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER')")
    @DeleteMapping("/work_shift/{id}")
    public ResponseEntity<ApiResponse<String>> deleteWorkShift(@PathVariable int id) {
        staffScheduleService.deleteWorkShift(id);
        return ResponseEntity.ok(new ApiResponse<>("Xóa ca mẫu thành công", "OK"));
    }

    @PostMapping("/schedule")
    public ResponseEntity<ApiResponse<StaffScheduleDetailResponseDTO>> upsertSchedule(
            @Valid @RequestBody StaffScheduleRequestDTO request) {

        StaffScheduleDetailResponseDTO data = staffScheduleService.upsertSchedule(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>("Xử lý lịch làm thành công", data));
    }

    @GetMapping("/schedule/my")
    public ResponseEntity<ApiResponse<List<StaffScheduleDetailResponseDTO>>> getMySchedule(
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate startDate,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate endDate,
            @RequestParam(required = false) StaffScheduleStatus status) {

        List<StaffScheduleDetailResponseDTO> data =
                staffScheduleService.getMySchedule(startDate, endDate, status);
        return ResponseEntity.ok(new ApiResponse<>("Lấy lịch làm việc cá nhân thành công", data));
    }

    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER','STAFF')")
    @GetMapping("/schedule/cinema")
    public ResponseEntity<ApiResponse<List<StaffScheduleDetailResponseDTO>>> getCinemaSchedule(
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate startDate,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate endDate,
            @RequestParam(required = false) StaffScheduleStatus status,
            @RequestParam(required = false) Integer staffId,
            @RequestParam(required = false) Integer cinemaId) {

        List<StaffScheduleDetailResponseDTO> data =
                staffScheduleService.getCinemaSchedule(startDate, endDate, status, staffId, cinemaId);
        return ResponseEntity.ok(new ApiResponse<>("Lấy lịch làm việc toàn rạp thành công", data));
    }
}
