package CinemaBooking.Group2.dtos.seat;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
@JsonIgnoreProperties(ignoreUnknown = true)
public class SeatUpdateDTO {
	private Integer id;
	private int number;
    private String code;
    private String type;
    private String status;
    private BigDecimal price;
	/**
	 * @return the id
	 */
	public Integer getId() {
		return id;
	}
	/**
	 * @param id the id to set
	 */
	public void setId(Integer id) {
		this.id = id;
	}
	/**
	 * @return the number
	 */
	public int getNumber() {
		return number;
	}
	/**
	 * @param number the number to set
	 */
	public void setNumber(int number) {
		this.number = number;
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
	public BigDecimal getPrice() {
		return price;
	}
	/**
	 * @param price the price to set
	 */
	public void setPrice(BigDecimal price) {
		this.price = price;
	}
	/**
	 * @param id
	 * @param number
	 * @param code
	 * @param type
	 * @param status
	 * @param price
	 */
	public SeatUpdateDTO(Integer id, int number, String code, String type, String status, BigDecimal price) {
		super();
		this.id = id;
		this.number = number;
		this.code = code;
		this.type = type;
		this.status = status;
		this.price = price;
	}
	/**
	 * 
	 */
	public SeatUpdateDTO() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	
    
    
}
