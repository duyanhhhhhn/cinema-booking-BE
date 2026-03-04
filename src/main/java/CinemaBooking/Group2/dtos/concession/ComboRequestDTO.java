package CinemaBooking.Group2.dtos.concession;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import CinemaBooking.Group2.models.ComboItem;

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
	private MultipartFile bannerFile;
	public ComboRequestDTO(String name, BigDecimal price, String comboItem, MultipartFile bannerFile) {
		super();
		this.name = name;
		this.price = price;
		this.comboItem= comboItem;
		this.bannerFile = bannerFile;
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
	public MultipartFile getBannerFile() {
		return bannerFile;
	}
	public void setBannerFile(MultipartFile bannerFile) {
		this.bannerFile = bannerFile;
	}
}
