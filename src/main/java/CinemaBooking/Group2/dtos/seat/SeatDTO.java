package CinemaBooking.Group2.dtos.seat;

public class SeatDTO {
	private int id;
    private String code;     // A1
    private String row;      // A
    private String number;   // 1
    private String type;     // STANDARD
    private String status;   // AVAILABLE / BOOKED / SOLD
    private long price;
	/**
	 * @return the id
	 */
	public int getId() {
		return id;
	}
	/**
	 * @param id the id to set
	 */
	public void setId(int id) {
		this.id = id;
	}
	/**
	 * @return the code
	 */
	public String getCode() {
		return code;
	}
	/**
	 * @param code the code to set
	 */
	public void setCode(String code) {
		this.code = code;
	}
	/**
	 * @return the row
	 */
	public String getRow() {
		return row;
	}
	/**
	 * @param row the row to set
	 */
	public void setRow(String row) {
		this.row = row;
	}
	/**
	 * @return the number
	 */
	public String getNumber() {
		return number;
	}
	/**
	 * @param number the number to set
	 */
	public void setNumber(String number) {
		this.number = number;
	}
	/**
	 * @return the type
	 */
	public String getType() {
		return type;
	}
	/**
	 * @param type the type to set
	 */
	public void setType(String type) {
		this.type = type;
	}
	/**
	 * @return the status
	 */
	public String getStatus() {
		return status;
	}
	/**
	 * @param status the status to set
	 */
	public void setStatus(String status) {
		this.status = status;
	}
	/**
	 * @return the price
	 */
	public long getPrice() {
		return price;
	}
	/**
	 * @param price the price to set
	 */
	public void setPrice(long price) {
		this.price = price;
	}
	/**
	 * @param id
	 * @param code
	 * @param row
	 * @param number
	 * @param type
	 * @param status
	 * @param price
	 */
	public SeatDTO(int id, String code, String row, String number, String type, String status, long price) {
		super();
		this.id = id;
		this.code = code;
		this.row = row;
		this.number = number;
		this.type = type;
		this.status = status;
		this.price = price;
	}
	/**
	 * 
	 */
	public SeatDTO() {
		super();
		// TODO Auto-generated constructor stub
	}
    
    
}
