package CinemaBooking.Group2.dtos.staff;

import java.time.LocalDate;
import java.time.LocalDateTime;

import CinemaBooking.Group2.models.Enum.StaffScheduleStatus;

public class StaffScheduleDetailResponseDTO {

    private int id;
    private LocalDate workDate;
    private StaffScheduleStatus status;
    private String requestedByRole;
    private LocalDateTime createdAt;
    private StaffResponseDTO staff;
    private ShiftTemplateResponseDTO shift;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public LocalDate getWorkDate() {
        return workDate;
    }

    public void setWorkDate(LocalDate workDate) {
        this.workDate = workDate;
    }

    public StaffScheduleStatus getStatus() {
        return status;
    }

    public void setStatus(StaffScheduleStatus status) {
        this.status = status;
    }

    public String getRequestedByRole() {
        return requestedByRole;
    }

    public void setRequestedByRole(String requestedByRole) {
        this.requestedByRole = requestedByRole;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public StaffResponseDTO getStaff() {
        return staff;
    }

    public void setStaff(StaffResponseDTO staff) {
        this.staff = staff;
    }

    public ShiftTemplateResponseDTO getShift() {
        return shift;
    }

    public void setShift(ShiftTemplateResponseDTO shift) {
        this.shift = shift;
    }
}
