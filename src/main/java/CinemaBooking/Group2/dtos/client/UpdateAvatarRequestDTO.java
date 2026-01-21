package CinemaBooking.Group2.dtos.client;

import org.springframework.web.multipart.MultipartFile;

public class UpdateAvatarRequestDTO {
	private MultipartFile avatar;

    public MultipartFile getAvatar() {
        return avatar;
    }

    public void setAvatar(MultipartFile avatar) {
        this.avatar = avatar;
    }
}
