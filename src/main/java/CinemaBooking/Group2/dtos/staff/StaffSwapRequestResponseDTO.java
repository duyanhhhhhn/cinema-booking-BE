package CinemaBooking.Group2.dtos.staff;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class StaffSwapRequestResponseDTO {

    private int id;
    private int scheduleId;
    private Integer approvedScheduleId;
    private String status;
    private String note;
    private LocalDate workDate;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private StaffResponseDTO requester;
    private StaffResponseDTO target;
    private ShiftTemplateResponseDTO shift;
    private String sourceScheduleStatus;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getScheduleId() {
        return scheduleId;
    }

    public void setScheduleId(int scheduleId) {
        this.scheduleId = scheduleId;
    }

    public Integer getApprovedScheduleId() {
        return approvedScheduleId;
    }

    public void setApprovedScheduleId(Integer approvedScheduleId) {
        this.approvedScheduleId = approvedScheduleId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public LocalDate getWorkDate() {
        return workDate;
    }

    public void setWorkDate(LocalDate workDate) {
        this.workDate = workDate;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public StaffResponseDTO getRequester() {
        return requester;
    }

    public void setRequester(StaffResponseDTO requester) {
        this.requester = requester;
    }

    public StaffResponseDTO getTarget() {
        return target;
    }

    public void setTarget(StaffResponseDTO target) {
        this.target = target;
    }

    public ShiftTemplateResponseDTO getShift() {
        return shift;
    }

    public void setShift(ShiftTemplateResponseDTO shift) {
        this.shift = shift;
    }

    public String getSourceScheduleStatus() {
        return sourceScheduleStatus;
    }

    public void setSourceScheduleStatus(String sourceScheduleStatus) {
        this.sourceScheduleStatus = sourceScheduleStatus;
    }
}
