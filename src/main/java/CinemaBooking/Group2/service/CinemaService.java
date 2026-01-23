package CinemaBooking.Group2.service;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import CinemaBooking.Group2.dtos.cinema.CinemaRequestDTO;
import CinemaBooking.Group2.dtos.cinema.CinemaResponseDTO;
import CinemaBooking.Group2.models.Cinema;
import CinemaBooking.Group2.repositories.CinemaRepository;

@Service
public class CinemaService {

	@Value("${app.upload.dir:${user.dir}/uploads}")
	private String uploadDir;

	@Value("${app.upload.public-prefix:/media}")
	private String publicPrefix;

	private static final Set<String> ALLOWED_EXT = Set.of("jpg", "jpeg", "png", "webp");
	@Autowired
	private CinemaRepository repo;

	// ================= GET ALL =================
	public List<CinemaResponseDTO> getAll() {
		return repo.findAllActive().stream().map(this::toResponse).toList();
	}

	// ================= GET BY ID =================
	public CinemaResponseDTO getById(int id) {
		Cinema c = repo.findById(id);
		if (c == null) {
			throw new RuntimeException("Cinema not found");
			// Sau này thay bằng custom exception
		}
		return toResponse(c);
	}

	// ================= CREATE =================
	public CinemaResponseDTO create(CinemaRequestDTO dto) {

		Cinema c = new Cinema();
		c.setName(dto.getName());
		c.setAddress(dto.getAddress());
		c.setPhone(dto.getPhone());
		c.setDescription(dto.getDescription());
		c.setImageUrl(dto.getImageUrl());
		c.setIsActive(1); // mặc định active

		repo.insert(c);

		// Giả sử repo.insert set lại ID
		return toResponse(c);
	}

	// ================= UPDATE =================
	public CinemaResponseDTO update(int id, CinemaRequestDTO dto) {

		Cinema existing = repo.findById(id);
		if (existing == null) {
			throw new RuntimeException("Cinema not found");
		}

		existing.setName(dto.getName());
		existing.setAddress(dto.getAddress());
		existing.setPhone(dto.getPhone());
		existing.setDescription(dto.getDescription());
		existing.setImageUrl(dto.getImageUrl());
		existing.setIsActive(dto.getIsActive());

		repo.update(id, existing);
		System.out.println(existing.getImageUrl());
		return toResponse(existing);
	}

	// ================= DEACTIVATE (SOFT DELETE) =================
	public void deactivate(int id) {

		Cinema existing = repo.findById(id);
		if (existing == null) {
			throw new RuntimeException("Cinema not found");
		}

		repo.deactivate(id);
	}

	// ================= MAPPER =================
	private CinemaResponseDTO toResponse(Cinema c) {
	    return new CinemaResponseDTO(
	        c.getId(),
	        c.getName(),
	        c.getAddress(),
	        c.getPhone(),
	        c.getDescription(),
	        c.getIsActive(),
	        toPublicMediaUrl(c.getImageUrl()),
	        c.getCreatedAt()
	    );
	}

	public String uploadCinemaImage(int cinemaId, MultipartFile file) {

	    Cinema cinema = repo.findById(cinemaId);
	    if (cinema == null)
	        throw new RuntimeException("Cinema không tồn tại");

	    String oldImagePath = cinema.getImageUrl(); // ✅ GIỮ LẠI ẢNH CŨ
	    Path newImagePath = null;

	    try {
	        newImagePath = saveCinemaImageToFolder(file);
	        String newRelPath = toRelativePath(newImagePath);

	        repo.uploadImage(cinemaId, newRelPath);

	        //  CHỈ XOÁ ẢNH CŨ
	        if (oldImagePath != null && !oldImagePath.isBlank()) {
	            Files.deleteIfExists(resolveUploadPath(oldImagePath));
	        }

	        return toPublicMediaUrl(newRelPath);

	    } catch (Exception e) {
	        if (newImagePath != null) {
	            try { Files.deleteIfExists(newImagePath); } catch (Exception ignore) {}
	        }
	        throw new RuntimeException("Upload cinema image thất bại", e);
	    }
	}


	private Path uploadRoot() {
		// Always use the project's uploads folder to ensure files are saved to <projectDir>/uploads
		Path root = Paths.get(System.getProperty("user.dir")).resolve("uploads");
		return root.toAbsolutePath().normalize();
	}

	private Path saveCinemaImageToFolder(MultipartFile file) throws Exception {
		String contentType = file.getContentType();
		if (contentType == null || !contentType.startsWith("image/"))
			throw new RuntimeException("File phải là ảnh");

		String ext = getExtension(file.getOriginalFilename());
		if (!ALLOWED_EXT.contains(ext))
			throw new RuntimeException("Định dạng ảnh không hợp lệ");

		Path root = uploadRoot();
		Path dir = root.resolve("cinema").normalize();
		Files.createDirectories(dir);

		String filename = UUID.randomUUID() + "." + ext;
		Path target = dir.resolve(filename);

		try (InputStream in = file.getInputStream()) {
			Files.copy(in, target, StandardCopyOption.REPLACE_EXISTING);
		}

		return target;
	}

	private String toRelativePath(Path absolutePath) {
		Path root = uploadRoot();
		return root.relativize(absolutePath.toAbsolutePath().normalize()).toString().replace("\\", "/");
	}

	private Path resolveUploadPath(String relative) {
		if (relative == null || relative.isBlank())
			return null;
		return uploadRoot().resolve(relative).normalize();
	}

	private String toPublicMediaUrl(String relativePath) {
		if (relativePath == null)
			return "";
		String prefix = publicPrefix == null ? "media" : publicPrefix;
		if (prefix.startsWith("/"))
			prefix = prefix.substring(1);
		if (relativePath.startsWith("/"))
			relativePath = relativePath.substring(1);
		return prefix + "/" + relativePath;
	}

	private String getExtension(String filename) {
		if (filename == null)
			return "";
		int dot = filename.lastIndexOf('.');
		if (dot < 0)
			return "";
		return filename.substring(dot + 1).toLowerCase(Locale.ROOT);
	}
}