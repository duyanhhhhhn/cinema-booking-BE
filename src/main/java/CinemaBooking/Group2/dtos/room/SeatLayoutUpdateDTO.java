package CinemaBooking.Group2.dtos.room;

public class SeatLayoutUpdateDTO {

    private String layout; // JSON string từ FE
    private int totalSeats;

    public String getLayout() {
        return layout;
    }

    public void setLayout(String layout) {
        this.layout = layout;
    }

    public int getTotalSeats() {
        return totalSeats;
    }

    public void setTotalSeats(int totalSeats) {
        this.totalSeats = totalSeats;
    }
}