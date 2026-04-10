package CinemaBooking.Group2.dtos.staff;

import jakarta.validation.constraints.NotBlank;

public class StaffSwapActionRequestDTO {

    @NotBlank(message = "action không được để trống")
    private String action;

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }
}
