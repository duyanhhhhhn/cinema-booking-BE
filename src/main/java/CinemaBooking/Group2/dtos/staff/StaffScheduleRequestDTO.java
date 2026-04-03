package CinemaBooking.Group2.dtos.staff;

import java.time.LocalDate;

import CinemaBooking.Group2.models.Enum.StaffScheduleStatus;
import jakarta.validation.constraints.NotNull;

public class StaffScheduleRequestDTO {

    private Integer staffId;

    @NotNull(message = "shiftId không được để trống")
    private Integer shiftId;

    @NotNull(message = "workDate không được để trống")
    private LocalDate workDate;

    private StaffScheduleStatus status;

    public Integer getStaffId() {
        return staffId;
    }

    public void setStaffId(Integer staffId) {
        this.staffId = staffId;
    }

    public Integer getShiftId() {
        return shiftId;
    }

    public void setShiftId(Integer shiftId) {
        this.shiftId = shiftId;
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
}
