package CinemaBooking.Group2.dtos.room;

import java.util.List;

public class RoomDetailDTO {
    private int id;
    private int cinemaId;
    private String name;
    private String type;
    private int totalSeats;
    private List<RowDTO> seatLayout; // đã parse JSON

    public RoomDetailDTO(int id, int cinemaId, String name, String type, int totalSeats, List<RowDTO> seatLayout) {
        this.id = id;
        this.cinemaId = cinemaId;
        this.name = name;
        this.type = type;
        this.totalSeats = totalSeats;
        this.seatLayout = seatLayout;
    }

    public static class RowDTO {
        private String rowName;
        private List<SeatDTO> seats;

        public RowDTO() {}

        public RowDTO(String rowName, List<SeatDTO> seats) {
            this.rowName = rowName;
            this.seats = seats;
        }

        // getter/setter
        public String getRowName() { return rowName; }
        public void setRowName(String rowName) { this.rowName = rowName; }
        public List<SeatDTO> getSeats() { return seats; }
        public void setSeats(List<SeatDTO> seats) { this.seats = seats; }
    }

    public static class SeatDTO {
        private String seatNumber;
        private String type;

        public SeatDTO() {}

        public SeatDTO(String seatNumber, String type) {
            this.seatNumber = seatNumber;
            this.type = type;
        }

        // getter/setter
        public String getSeatNumber() { return seatNumber; }
        public void setSeatNumber(String seatNumber) { this.seatNumber = seatNumber; }
        public String getType() { return type; }
        public void setType(String type) { this.type = type; }
    }

    // getter/setter
    public int getId() { return id; }
    public int getCinemaId() { return cinemaId; }
    public String getName() { return name; }
    public String getType() { return type; }
    public int getTotalSeats() { return totalSeats; }
    public List<RowDTO> getSeatLayout() { return seatLayout; }
}