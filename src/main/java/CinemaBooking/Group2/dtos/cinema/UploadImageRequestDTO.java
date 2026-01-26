package CinemaBooking.Group2.dtos.cinema;

import org.springframework.web.multipart.MultipartFile;

public class UploadImageRequestDTO {
	private MultipartFile image;

	public MultipartFile getImage() {
		return image;
	}

	public void setImage(MultipartFile image) {
		this.image = image;
	}
}
