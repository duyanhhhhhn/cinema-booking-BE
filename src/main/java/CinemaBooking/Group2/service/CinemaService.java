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

		if (c == null)
			throw new RuntimeException("Cinema not found");

		return toResponse(c);
	}

	// ================= CREATE =================
	public CinemaResponseDTO create(CinemaRequestDTO dto) {

		Cinema c = new Cinema();

		c.setName(dto.getName());
		c.setAddress(dto.getAddress());
		c.setPhone(dto.getPhone());
		c.setDescription(dto.getDescription());

		c.setIsActive(dto.getIsActive() != null ? dto.getIsActive() : 1);

		if (dto.getImageUrl() != null && !dto.getImageUrl().isEmpty()) {

			String path = uploadCinemaImageFile(dto.getImageUrl());

			c.setImageUrl(path);
		}

		repo.insert(c);

		return toResponse(c);
	}

	// ================= UPDATE =================
	public CinemaResponseDTO update(int id, CinemaRequestDTO dto) {

		Cinema existing = repo.findById(id);

		if (existing == null)
			throw new RuntimeException("Cinema not found");

		existing.setName(dto.getName());
		existing.setAddress(dto.getAddress());
		existing.setPhone(dto.getPhone());
		existing.setDescription(dto.getDescription());

		existing.setIsActive(dto.getIsActive() != null ? dto.getIsActive() : existing.getIsActive());

		if (dto.getImageUrl() != null && !dto.getImageUrl().isEmpty()) {

			String oldPath = existing.getImageUrl();

			String newPath = uploadCinemaImageFile(dto.getImageUrl());

			existing.setImageUrl(newPath);

			if (oldPath != null && !oldPath.equals(newPath)) {

				try {
					Files.deleteIfExists(resolveUploadPath(oldPath));
				} catch (Exception ignore) {
				}
			}
		}

		repo.update(id, existing);

		return toResponse(existing);
	}

	// ================= DEACTIVATE =================
	public void deactivate(int id) {

		Cinema existing = repo.findById(id);

		if (existing == null)
			throw new RuntimeException("Cinema not found");

		repo.deactivate(id);
	}

	// ================= ACTIVATE =================
	public void activate(int id) {

		Cinema existing = repo.findById(id);
		if (existing == null)
			throw new RuntimeException("Cinema not found");
		repo.activate(id);
	}

	// ================= PUBLIC SEARCH =================
	public List<CinemaResponseDTO> getAllPagedWithSearch(int page, int perPage, String search) {

		List<CinemaResponseDTO> list = repo.findAllActive().stream()

				.filter(c -> matchSearch(c, search))

				.map(this::toResponse)

				.toList();

		return paginate(list, page, perPage);
	}

	public long countAllActiveCinemasWithSearch(String search) {

		return repo.findAllActive().stream().filter(c -> matchSearch(c, search)).count();
	}

	// ================= ADMIN SEARCH =================
	public List<CinemaResponseDTO> getAllPagedIncludingInactiveWithSearch(int page, int perPage, String search) {

		List<CinemaResponseDTO> list = repo.findAll().stream()

				.filter(c -> matchSearch(c, search))

				.map(this::toResponse)

				.toList();

		return paginate(list, page, perPage);
	}

	public long countAllCinemasIncludingInactiveWithSearch(String search) {

		return repo.findAll().stream().filter(c -> matchSearch(c, search)).count();
	}

	// ================= SEARCH HELPER =================
	private boolean matchSearch(Cinema c, String search) {

		if (search == null || search.isBlank())
			return true;

		String keyword = search.toLowerCase();

		return (c.getName() != null && c.getName().toLowerCase().contains(keyword))

				||

				(c.getAddress() != null && c.getAddress().toLowerCase().contains(keyword));
	}

	// ================= PAGINATION =================
	private <T> List<T> paginate(List<T> list, int page, int perPage) {

		int from = (page - 1) * perPage;
		int to = Math.min(from + perPage, list.size());

		if (from >= list.size())
			return List.of();

		return list.subList(from, to);
	}

	// ================= IMAGE UPLOAD =================

	public String uploadCinemaImage(int cinemaId, MultipartFile file) {

		Cinema cinema = repo.findById(cinemaId);

		if (cinema == null)
			throw new RuntimeException("Cinema không tồn tại");

		String oldRelPath = cinema.getImageUrl();

		Path newImagePath = null;

		try {

			newImagePath = saveCinemaImageToFolder(file);

			String newRelPath = toRelativePath(newImagePath);

			repo.uploadImage(cinemaId, newRelPath);

			if (oldRelPath != null && !oldRelPath.equals(newRelPath)) {

				Files.deleteIfExists(resolveUploadPath(oldRelPath));
			}

			return toPublicMediaUrl(newRelPath);

		} catch (Exception e) {

			if (newImagePath != null) {

				try {
					Files.deleteIfExists(newImagePath);
				} catch (Exception ignore) {
				}
			}

			throw new RuntimeException("Upload cinema image thất bại", e);
		}
	}

	private String uploadCinemaImageFile(MultipartFile file) {

		try {

			Path saved = saveCinemaImageToFolder(file);

			return toRelativePath(saved);

		} catch (Exception e) {

			throw new RuntimeException("Upload image thất bại", e);
		}
	}

	private Path uploadRoot() {

		return Paths.get(uploadDir).toAbsolutePath().normalize();
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

		return root.relativize(absolutePath).toString().replace("\\", "/");
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

	// ================= MAPPER =================
	private CinemaResponseDTO toResponse(Cinema c) {
		return new CinemaResponseDTO(c.getId(), c.getName(), c.getAddress(), c.getPhone(), c.getDescription(),
				c.getIsActive(), toPublicMediaUrl(c.getImageUrl()), c.getCreatedAt());
	}
}
