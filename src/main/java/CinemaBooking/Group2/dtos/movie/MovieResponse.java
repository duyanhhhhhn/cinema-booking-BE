package CinemaBooking.Group2.dtos.movie;


public class MovieResponse {

	public enum MovieStatus {
	    COMING_SOON,
	    NOW_SHOWING,
	    ENDED
	}
	
	private int id;
    private String title;
    private String shortDescription;
    private int durationMinutes;
    private MovieStatus status; 

	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getShortDescription() {
		return shortDescription;
	}
	public void setShortDescription(String shortDescription) {
		this.shortDescription = shortDescription;
	}
	public MovieStatus getStatus() {
		return status;
	}
	public void setStatus(MovieStatus status) {
		this.status = status;
	}
	/**
	 * @param id
	 * @param title
	 * @param shortDescription
	 * @param durationMinutes
	 */
	public int getDurationMinutes() {
		return durationMinutes;
	}
	public void setDurationMinutes(int durationMinutes) {
		this.durationMinutes = durationMinutes;
	}
	
	public MovieResponse(int id, String title, String shortDescription, int durationMinutes) {
		super();
		this.id = id;
		this.title = title;
		this.shortDescription = shortDescription;
		this.durationMinutes = durationMinutes;
	}
	/**
	 * 
	 */
	public MovieResponse() {
		super();
		// TODO Auto-generated constructor stub
	}
    
    
}
