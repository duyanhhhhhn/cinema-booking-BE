package CinemaBooking.Group2.models;

import java.time.LocalDateTime;

import CinemaBooking.Group2.models.Enum.StaffScheduleSwapStatus;

public class StaffScheduleSwapRequest {

    private int id;
    private int scheduleId;
    private int requesterStaffId;
    private int targetStaffId;
    private StaffScheduleSwapStatus status;
    private String note;
    private Integer approvedScheduleId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

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

    public int getRequesterStaffId() {
        return requesterStaffId;
    }

    public void setRequesterStaffId(int requesterStaffId) {
        this.requesterStaffId = requesterStaffId;
    }

    public int getTargetStaffId() {
        return targetStaffId;
    }

    public void setTargetStaffId(int targetStaffId) {
        this.targetStaffId = targetStaffId;
    }

    public StaffScheduleSwapStatus getStatus() {
        return status;
    }

    public void setStatus(StaffScheduleSwapStatus status) {
        this.status = status;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public Integer getApprovedScheduleId() {
        return approvedScheduleId;
    }

    public void setApprovedScheduleId(Integer approvedScheduleId) {
        this.approvedScheduleId = approvedScheduleId;
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
}
