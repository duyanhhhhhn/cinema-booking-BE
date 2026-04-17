package CinemaBooking.Group2.dtos.auth;

import java.time.LocalDate;
import java.time.LocalTime;

import com.fasterxml.jackson.annotation.JsonProperty;

public class UserPositionResponseDTO {

    private Integer userId;
    private String role;
    private String position;
    private Integer cinemaId;
    @JsonProperty("isTicketSeller")
    private boolean isTicketSeller;
    @JsonProperty("hasActiveApprovedShiftNow")
    private boolean hasActiveApprovedShiftNow;
    @JsonProperty("canAccessTicketSelling")
    private boolean canAccessTicketSelling;
    private ActiveShiftDTO activeShift;

    public static class ActiveShiftDTO {
        private Integer scheduleId;
        private Integer shiftId;
        private LocalDate workDate;
        private String shiftName;
        private LocalTime startTime;
        private LocalTime endTime;
        private String status;
        private LocalTime approvedLateArrivalTime;

        public Integer getScheduleId() {
            return scheduleId;
        }

        public void setScheduleId(Integer scheduleId) {
            this.scheduleId = scheduleId;
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

        public String getShiftName() {
            return shiftName;
        }

        public void setShiftName(String shiftName) {
            this.shiftName = shiftName;
        }

        public LocalTime getStartTime() {
            return startTime;
        }

        public void setStartTime(LocalTime startTime) {
            this.startTime = startTime;
        }

        public LocalTime getEndTime() {
            return endTime;
        }

        public void setEndTime(LocalTime endTime) {
            this.endTime = endTime;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public LocalTime getApprovedLateArrivalTime() {
            return approvedLateArrivalTime;
        }

        public void setApprovedLateArrivalTime(LocalTime approvedLateArrivalTime) {
            this.approvedLateArrivalTime = approvedLateArrivalTime;
        }
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public Integer getCinemaId() {
        return cinemaId;
    }

    public void setCinemaId(Integer cinemaId) {
        this.cinemaId = cinemaId;
    }

    public boolean isTicketSeller() {
        return isTicketSeller;
    }

    public void setTicketSeller(boolean ticketSeller) {
        isTicketSeller = ticketSeller;
    }

    public boolean hasActiveApprovedShiftNow() {
        return hasActiveApprovedShiftNow;
    }

    public void setHasActiveApprovedShiftNow(boolean hasActiveApprovedShiftNow) {
        this.hasActiveApprovedShiftNow = hasActiveApprovedShiftNow;
    }

    public boolean canAccessTicketSelling() {
        return canAccessTicketSelling;
    }

    public void setCanAccessTicketSelling(boolean canAccessTicketSelling) {
        this.canAccessTicketSelling = canAccessTicketSelling;
    }

    public ActiveShiftDTO getActiveShift() {
        return activeShift;
    }

    public void setActiveShift(ActiveShiftDTO activeShift) {
        this.activeShift = activeShift;
    }
}
