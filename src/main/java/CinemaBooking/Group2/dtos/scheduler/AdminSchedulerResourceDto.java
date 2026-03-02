package CinemaBooking.Group2.dtos.scheduler;


public class AdminSchedulerResourceDto {
    private int id;
    private String name;
    private String type;
    private int totalSeats;

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public int getTotalSeats() { return totalSeats; }
    public void setTotalSeats(int totalSeats) { this.totalSeats = totalSeats; }
}