package CinemaBooking.Group2.dtos.staff;

import jakarta.validation.constraints.NotNull;

public class CreateStaffSwapRequestDTO {

    @NotNull(message = "scheduleId không được để trống")
    private Integer scheduleId;

    @NotNull(message = "targetStaffId không được để trống")
    private Integer targetStaffId;

    private String note;

    public Integer getScheduleId() {
        return scheduleId;
    }

    public void setScheduleId(Integer scheduleId) {
        this.scheduleId = scheduleId;
    }

    public Integer getTargetStaffId() {
        return targetStaffId;
    }

    public void setTargetStaffId(Integer targetStaffId) {
        this.targetStaffId = targetStaffId;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
}
