package CinemaBooking.Group2.dtos.showtime;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class ShowtimePublicDtos {
    private int id;               // showtimeId
    private String startTime;     // "HH:mm"
    private BigDecimal price;     // base_price
    private String roomName;

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getStartTime() { return startTime; }
    public void setStartTime(String startTime) { this.startTime = startTime; }

    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }

    public String getRoomName() { return roomName; }
    public void setRoomName(String roomName) { this.roomName = roomName; }
}
