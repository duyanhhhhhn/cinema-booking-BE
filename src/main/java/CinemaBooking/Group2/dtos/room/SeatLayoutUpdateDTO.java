package CinemaBooking.Group2.dtos.room;

public class SeatLayoutUpdateDTO {
	private Object layout;
    private int totalSeats;

    public Object getLayout() {
        return layout;
    }

    public void setLayout(Object layout) {
        this.layout = layout;
    }

    public int getTotalSeats() {
        return totalSeats;
    }

    public void setTotalSeats(int totalSeats) {
        this.totalSeats = totalSeats;
    }
}
