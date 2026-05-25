package CinemaBooking.Group2.dtos.staff;

import java.time.LocalTime;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class CreateStaffUrgentRequestDTO {

    @NotNull(message = "scheduleId không được để trống")
    private Integer scheduleId;

    @NotBlank(message = "type không được để trống")
    private String type;

    @NotBlank(message = "reason không được để trống")
    private String reason;

    private LocalTime expectedArrivalTime;

    public Integer getScheduleId() {
        return scheduleId;
    }

    public void setScheduleId(Integer scheduleId) {
        this.scheduleId = scheduleId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
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
}
