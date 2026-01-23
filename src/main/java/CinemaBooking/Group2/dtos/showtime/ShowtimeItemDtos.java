package CinemaBooking.Group2.dtos.showtime;

public class ShowtimeItemDtos {
    private int id;
    private String startTime; 
    public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getStartTime() {
		return startTime;
	}
	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	private String type;
	public ShowtimeItemDtos(int id, String startTime, String type) {
		super();
		this.id = id;
		this.startTime = startTime;
		this.type = type;
	}
	public ShowtimeItemDtos() {
		super();
		// TODO Auto-generated constructor stub
	}   
	
}
