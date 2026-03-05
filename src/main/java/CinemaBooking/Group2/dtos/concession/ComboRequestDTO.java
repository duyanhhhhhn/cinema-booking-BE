package CinemaBooking.Group2.dtos.concession;

import java.math.BigDecimal;

import org.springframework.web.multipart.MultipartFile;

public class ComboRequestDTO {
	public String getComboItem() {
		return comboItem;
	}
	public void setComboItem(String comboItem) {
		this.comboItem = comboItem;
	}
	private String name;
	private BigDecimal price;
	private String comboItem;
	public ComboRequestDTO(String name, BigDecimal price, String comboItem) {
		super();
		this.name = name;
		this.price = price;
		this.comboItem= comboItem;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public BigDecimal getPrice() {
		return price;
	}
	public void setPrice(BigDecimal price) {
		this.price = price;
	}
	public String getItem() {
		return comboItem;
	}
	public void setItem(String item) {
		this.comboItem = item;
	}
}
