package CinemaBooking.Group2.dtos.staff;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class StaffUrgentRequestResponseDTO {

    private int id;
    private int scheduleId;
    private String type;
    private String status;
    private String reason;
    private LocalTime expectedArrivalTime;
    private LocalDate workDate;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private StaffResponseDTO requester;
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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
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
