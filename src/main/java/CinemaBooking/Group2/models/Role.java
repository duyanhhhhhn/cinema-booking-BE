package CinemaBooking.Group2.models;

public class Role {

	private int id;
	private String name;
    private String description;
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	/**
	 * @param id
	 * @param name
	 * @param description
	 */
	public Role(int id, String name, String description) {
		super();
		this.id = id;
		this.name = name;
		this.description = description;
	}
	/**
	 * 
	 */
	public Role() {
		super();
		// TODO Auto-generated constructor stub
	}
    
    

}
