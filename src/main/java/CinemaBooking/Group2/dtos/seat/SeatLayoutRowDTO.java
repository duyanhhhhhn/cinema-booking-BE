package CinemaBooking.Group2.dtos.seat;

import java.util.List;


public class SeatLayoutRowDTO {

    private String rowLabel;
    private List<SeatUpdateDTO> seats; 

    public String getRowLabel() {
        return rowLabel;
    }

    public void setRowLabel(String rowLabel) {
        this.rowLabel = rowLabel;
    }

    public List<SeatUpdateDTO> getSeats() {
        return seats;
    }

    public void setSeats(List<SeatUpdateDTO> seats) {
        this.seats = seats;
    }
}