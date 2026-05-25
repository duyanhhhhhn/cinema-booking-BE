package CinemaBooking.Group2.models;

import java.time.LocalDateTime;
import java.time.LocalTime;

import CinemaBooking.Group2.models.Enum.StaffScheduleUrgentRequestStatus;
import CinemaBooking.Group2.models.Enum.StaffScheduleUrgentRequestType;

public class StaffScheduleUrgentRequest {

    private int id;
    private int scheduleId;
    private int requesterStaffId;
    private StaffScheduleUrgentRequestType type;
    private StaffScheduleUrgentRequestStatus status;
    private String reason;
    private LocalTime expectedArrivalTime;
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

    public StaffScheduleUrgentRequestType getType() {
        return type;
    }

    public void setType(StaffScheduleUrgentRequestType type) {
        this.type = type;
    }

    public StaffScheduleUrgentRequestStatus getStatus() {
        return status;
    }

    public void setStatus(StaffScheduleUrgentRequestStatus status) {
        this.status = status;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public LocalTime getExpectedArrivalTime() {
        return expectedArrivalTime;
    }

    public void setExpectedArrivalTime(LocalTime expectedArrivalTime) {
        this.expectedArrivalTime = expectedArrivalTime;
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
