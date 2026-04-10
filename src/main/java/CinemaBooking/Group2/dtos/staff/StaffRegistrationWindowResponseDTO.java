package CinemaBooking.Group2.dtos.staff;

public class StaffRegistrationWindowResponseDTO {
    private boolean forceOpen;
    private boolean weekendOpenToday;
    private boolean staffCanRegisterNow;

    public boolean isForceOpen() {
        return forceOpen;
    }

    public void setForceOpen(boolean forceOpen) {
        this.forceOpen = forceOpen;
    }

    public boolean isWeekendOpenToday() {
        return weekendOpenToday;
    }

    public void setWeekendOpenToday(boolean weekendOpenToday) {
        this.weekendOpenToday = weekendOpenToday;
    }

    public boolean isStaffCanRegisterNow() {
        return staffCanRegisterNow;
    }

    public void setStaffCanRegisterNow(boolean staffCanRegisterNow) {
        this.staffCanRegisterNow = staffCanRegisterNow;
    }
}
