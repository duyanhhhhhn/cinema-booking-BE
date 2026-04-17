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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import CinemaBooking.Group2.dtos.PageResponse;
import CinemaBooking.Group2.dtos.admin.CreateUserRequestDTO;
import CinemaBooking.Group2.dtos.admin.UpdateUserRequestDTO;
import CinemaBooking.Group2.dtos.admin.UserResponseDTO;
import CinemaBooking.Group2.dtos.client.UpdateProfileRequestDTO;
import CinemaBooking.Group2.models.User;
import CinemaBooking.Group2.repositories.UserRepository;
import CinemaBooking.Group2.security.AuthUserPrincipal;
import CinemaBooking.Group2.dtos.auth.UserDTO;

@Service
public class UserService {

	// ================= CONFIG =================
	@Value("${app.upload.dir:${user.dir}/uploads}")
	private String uploadDir;

	@Value("${app.upload.public-prefix:/media}")
	private String publicPrefix;

	private static final long MAX_AVATAR_SIZE = 1 * 1024 * 1024; // 1MB
	private static final Set<String> ALLOWED_EXT = Set.of("jpg", "jpeg", "png", "webp");

	// ================= DEPENDENCIES =================
	@Autowired
	private UserRepository userRepo;
	@Autowired
	private PasswordEncoder encoder;
	@Autowired
	private AuthService authService;

	// ================= CREATE USER =================
	public void createUser(CreateUserRequestDTO req) {
		AuthUserPrincipal principal = (AuthUserPrincipal) SecurityContextHolder.getContext().getAuthentication()
				.getPrincipal();

		String role = principal.role();
		Integer cinemaId = principal.cinemaId();

		if ("ADMIN".equals(role)) {
			if (req.getRoleId() != 2)
				throw new RuntimeException("ADMIN chỉ được tạo MANAGER");
		} else if ("MANAGER".equals(role)) {
			if (req.getRoleId() != 3)
				throw new RuntimeException("MANAGER chỉ được tạo STAFF");
			if (!cinemaId.equals(req.getCinemaId()))
				throw new RuntimeException("MANAGER chỉ được tạo user trong rạp của mình");
		} else {
			throw new RuntimeException("Bạn không có quyền tạo user");
		}

		if (userRepo.existsByEmail(req.getEmail()))
			throw new RuntimeException("Email đã tồn tại");

		userRepo.createStaff(req, encoder.encode(req.getPassword()));
	}

	// ================= GET USERS =================
	public List<UserResponseDTO> getUsers(Integer roleId, Integer cinemaId) {
		AuthUserPrincipal principal = (AuthUserPrincipal) SecurityContextHolder.getContext().getAuthentication()
				.getPrincipal();

		if ("STAFF".equals(principal.role()))
			throw new RuntimeException("Bạn không có quyền xem danh sách user");

		if ("MANAGER".equals(principal.role()))
			cinemaId = principal.cinemaId();

		return userRepo.findUsers(roleId, cinemaId);
	}

	// ================= UPDATE USER =================
	public void updateUser(int id, UpdateUserRequestDTO req) {
		AuthUserPrincipal principal = (AuthUserPrincipal) SecurityContextHolder.getContext().getAuthentication()
				.getPrincipal();

		User target = userRepo.findById(id);
		if (target == null)
			throw new RuntimeException("User không tồn tại");

		if ("MANAGER".equals(principal.role()) && !principal.cinemaId().equals(target.getCinemaId())) {
			throw new RuntimeException("Bạn không có quyền sửa user rạp khác");
		}

		userRepo.updateUser(id, req);
	}

	// ================= LOCK USER =================
	public void lockUser(int id) {
		AuthUserPrincipal principal = (AuthUserPrincipal) SecurityContextHolder.getContext().getAuthentication()
				.getPrincipal();

		// STAFF không được lock
		if ("STAFF".equals(principal.role())) {
			throw new RuntimeException("Bạn không có quyền khóa tài khoản");
		}

		User target = userRepo.findById(id);
		if (target == null)
			throw new RuntimeException("User không tồn tại");


		UpdateUserRequestDTO req = new UpdateUserRequestDTO();
		req.setIsActive(0);
		userRepo.updateUser(id, req);
	}

	// ================= UNLOCK USER =================
	public void unlockUser(int id) {
		AuthUserPrincipal principal = (AuthUserPrincipal) SecurityContextHolder.getContext().getAuthentication()
				.getPrincipal();

		// STAFF không được unlock
		if ("STAFF".equals(principal.role())) {
			throw new RuntimeException("Bạn không có quyền mở khóa tài khoản");
		}

		User target = userRepo.findById(id);
		if (target == null)
			throw new RuntimeException("User không tồn tại");


		UpdateUserRequestDTO req = new UpdateUserRequestDTO();
		req.setIsActive(1);
		userRepo.updateUser(id, req);
	}

	// ================= GET USER DETAIL =================
	public UserResponseDTO getUserDetail(int id) {
		AuthUserPrincipal principal = (AuthUserPrincipal) SecurityContextHolder.getContext().getAuthentication()
				.getPrincipal();

		if ("STAFF".equals(principal.role()))
			throw new RuntimeException("Không có quyền xem user");

		User target = userRepo.findById(id);
		if (target == null)
			throw new RuntimeException("User không tồn tại");

		if ("MANAGER".equals(principal.role()) && !principal.cinemaId().equals(target.getCinemaId())) {
			throw new RuntimeException("Không được xem user rạp khác");
		}

		return userRepo.findUserDetail(id);
	}

	public User findByEmail(String email) {
		return userRepo.findByEmail(email);
	}

	public User getCurrentAuthenticatedUser() {
		AuthUserPrincipal principal = authService.getPrincipal();
		User user = userRepo.findByEmail(principal.email());
		if (user == null) {
			throw new RuntimeException("User không tồn tại");
		}
		return user;
	}

	// ================= UPDATE PROFILE =================
	public void updateMyProfile(UpdateProfileRequestDTO req) {
		AuthUserPrincipal principal = (AuthUserPrincipal) SecurityContextHolder.getContext().getAuthentication()
				.getPrincipal();

		User user = userRepo.findByEmail(principal.email());
		if (user == null)
			throw new RuntimeException("User không tồn tại");

		userRepo.updateProfile(user.getId(), req.getFullName(), req.getPhone());
	}

	// ================= AVATAR =================
	public String uploadAvatar(MultipartFile file) {
		if (file == null || file.isEmpty())
			throw new RuntimeException("Avatar không được để trống");

		if (file.getSize() > MAX_AVATAR_SIZE)
			throw new RuntimeException("Avatar tối đa 1MB");

		AuthUserPrincipal principal = (AuthUserPrincipal) SecurityContextHolder.getContext().getAuthentication()
				.getPrincipal();

		User user = userRepo.findByEmail(principal.email());
		if (user == null)
			throw new RuntimeException("User không tồn tại");

		Path newAvatarPath = null;

		try {
			newAvatarPath = saveAvatarToFolder(file);
			String newAvatarRel = toRelativePath(newAvatarPath);

			userRepo.updateAvatar(user.getId(), newAvatarRel);

			// XÓA AVATAR CŨ
			if (user.getAvatarUrl() != null && !user.getAvatarUrl().isBlank()) {
				Files.deleteIfExists(resolveUploadPath(user.getAvatarUrl()));
			}

			return toPublicMediaUrl(newAvatarRel);

		} catch (Exception e) {
			if (newAvatarPath != null) {
				try {
					Files.deleteIfExists(newAvatarPath);
				} catch (Exception ignore) {
				}
			}
			throw new RuntimeException("Upload avatar thất bại: " + e.getMessage(), e);
		}
	}

	// ================= FILE UTILS =================
	private Path uploadRoot() {
		Path p = Paths.get(uploadDir);
		if (!p.isAbsolute())
			p = Paths.get(System.getProperty("user.dir")).resolve(p);
		return p.toAbsolutePath().normalize();
	}

	private Path saveAvatarToFolder(MultipartFile file) throws Exception {
		String contentType = file.getContentType();
		if (contentType == null || !contentType.startsWith("image/"))
			throw new RuntimeException("File phải là ảnh");

		String ext = getExtension(file.getOriginalFilename());
		if (!ALLOWED_EXT.contains(ext))
			throw new RuntimeException("Định dạng ảnh không hợp lệ");

		Path root = uploadRoot();
		Path dir = root.resolve("avatar").normalize();
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
		// Remove leading slash if present
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

	private int getOffset(int page, int perPage) {
		return (page - 1) * perPage;
	}

	// =======GET STAFF =======//
	public PageResponse<UserDTO> getStaffs(
	        int page,
	        int perPage,
	        String search,
	        Integer cinemaIdParam
	) {

		AuthUserPrincipal p = authService.getPrincipal();

		if ("STAFF".equals(p.role()))
		    throw new RuntimeException("Không có quyền");

		List<Integer> roles;
		Integer cinemaId = null;

		if ("ADMIN".equals(p.role())) {
		    roles = List.of(2, 3);
		    cinemaId = cinemaIdParam;   // 👈 ADMIN được chọn cinema
		} 
		else if ("MANAGER".equals(p.role())) {
		    roles = List.of(3);
		    cinemaId = p.cinemaId();    // 👈 ép cinema của manager
		}
		else {
		    throw new RuntimeException("Role không hợp lệ");
		}

	    int offset = getOffset(page, perPage);

	    List<UserDTO> list =
	        userRepo.findPagedUsers(
	            roles, cinemaId, search,
	            offset, perPage
	        );

	    int total =
	        userRepo.countUsers(roles, cinemaId, search);
	    
	    return new PageResponse<>(
	        "Lấy danh sách nhân viên thành công",
	        list,
	        new PageResponse.Meta(total, perPage, page)
	    );
	}
	
	//====== GET CUSTOMER ======//
    public PageResponse<UserDTO> getCustomers(
            int page,
            int perPage,
            String search
    ) {

        List<Integer> roles = List.of(4); // CUSTOMER

        int offset = getOffset(page, perPage);

        List<UserDTO> list =
            userRepo.findPagedUsers(
                roles, null, search,
                offset, perPage
            );

        int total =
            userRepo.countUsers(roles, null, search);

        return new PageResponse<>(
            "Lấy danh sách khách hàng thành công",
			list,
			new PageResponse.Meta(total, perPage, page)
        );
    }

 // ================= CREATE MANAGER/STAFF =================
    public void createManageUser(CreateUserRequestDTO req, MultipartFile avatar) {

        AuthUserPrincipal principal =
            (AuthUserPrincipal) SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal();

        String role = principal.role();
        Integer cinemaId = principal.cinemaId();

        if ("STAFF".equals(role))
            throw new RuntimeException("STAFF không được tạo user");

        if ("ADMIN".equals(role)) {

            if (req.getRoleId() != 2 && req.getRoleId() != 3)
                throw new RuntimeException("ADMIN chỉ được tạo MANAGER hoặc STAFF");

            // ADMIN được chọn cinema
            if (req.getCinemaId() == null)
                throw new RuntimeException("Phải chọn rạp");

        } else if ("MANAGER".equals(role)) {

            if (req.getRoleId() != 3)
                throw new RuntimeException("MANAGER chỉ được tạo STAFF");

            // 🔥 Không check nữa
            // 🔥 Override luôn
            req.setCinemaId(cinemaId);
        }

        if (userRepo.existsByEmail(req.getEmail()))
            throw new RuntimeException("Email đã tồn tại");

        if (avatar != null && !avatar.isEmpty()) {
            try {
                Path newAvatarPath = saveAvatarToFolder(avatar);
                String newAvatarRel = toRelativePath(newAvatarPath);
                // store relative path (same as createUser/updateUser)
                req.setAvatarUrl(newAvatarRel);
            } catch (Exception e) {
                throw new RuntimeException("Upload avatar thất bại: " + e.getMessage(), e);
            }
        }

        userRepo.createStaff(req, encoder.encode(req.getPassword()));
    }

    // ================= UPDATE MANAGER/STAFF =================
    public void updateManageUser(int id, UpdateUserRequestDTO req,MultipartFile avatar) {
        AuthUserPrincipal principal = (AuthUserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        
        User target = userRepo.findById(id);
        if (target == null)
            throw new RuntimeException("User không tồn tại");

        if ("STAFF".equals(principal.role()))
            throw new RuntimeException("STAFF không được chỉnh sửa user");

        if ("MANAGER".equals(principal.role()) && !principal.cinemaId().equals(target.getCinemaId()))
            throw new RuntimeException("MANAGER chỉ được chỉnh sửa STAFF cùng rạp");

        // Chỉ ADMIN có thể chỉnh MANAGER
        if ("MANAGER".equals(principal.role()) && target.getRoleId() == 2)
            throw new RuntimeException("MANAGER không được chỉnh MANAGER");

        if ("MANAGER".equals(principal.role())) {
            if (req.getRoleId() != null && req.getRoleId() != target.getRoleId())
                throw new RuntimeException("MANAGER không được đổi phân quyền");

            if (req.getCinemaId() != null && !principal.cinemaId().equals(req.getCinemaId()))
                throw new RuntimeException("MANAGER không được chuyển user sang rạp khác");

            req.setCinemaId(principal.cinemaId());
        }

        Integer nextRoleId = req.getRoleId() != null ? req.getRoleId() : target.getRoleId();
        if (nextRoleId != 2 && nextRoleId != 3)
            throw new RuntimeException("Chỉ hỗ trợ cập nhật MANAGER hoặc STAFF");

        Integer nextCinemaId = req.getCinemaId() != null
                ? req.getCinemaId()
                : (target.getCinemaId() > 0 ? target.getCinemaId() : null);

        if (nextCinemaId == null || nextCinemaId <= 0)
            throw new RuntimeException("Phải chọn rạp phụ trách");

        req.setCinemaId(nextCinemaId);

        if (nextRoleId == 2) {
            if (!"ADMIN".equals(principal.role()))
                throw new RuntimeException("Chỉ ADMIN được gán quyền MANAGER");

            req.setRoleId(2);
            req.setPosition("MANAGER");
        } else {
            String nextPosition = req.getPosition();
            if (nextPosition == null || nextPosition.isBlank()) {
                nextPosition = target.getPosition() != null ? target.getPosition().name() : null;
            }

            if (nextPosition == null || nextPosition.isBlank() || "MANAGER".equalsIgnoreCase(nextPosition))
                throw new RuntimeException("STAFF phải có chức vụ hợp lệ");

            req.setRoleId(3);
            req.setPosition(nextPosition.toUpperCase(Locale.ROOT));
        }
        
        if (req.getPassword() != null && !req.getPassword().isBlank()) {
            String hashedPassword = encoder.encode(req.getPassword());
            req.setPassword(hashedPassword);
        }
        if (avatar != null && !avatar.isEmpty()) {
            Path newAvatarPath = null;
            
            try {
                newAvatarPath = saveAvatarToFolder(avatar);
                String newAvatarRel = toRelativePath(newAvatarPath);
                // store relative path (same as createUser/updateUser)
                req.setAvatarUrl(newAvatarRel);
                // delete old avatar file of the target user
                if (target.getAvatarUrl() != null && !target.getAvatarUrl().isBlank()) {
                    Files.deleteIfExists(resolveUploadPath(target.getAvatarUrl()));
                }
            } catch (Exception e) {
                if (newAvatarPath != null) {
                    try {
                        Files.deleteIfExists(newAvatarPath);
                    } catch (Exception ignore) {
                    }
                }
                throw new RuntimeException("Upload avatar thất bại: " + e.getMessage(), e);
            }
        }
        System.out.println("REQ POSITION: [" + req.getPosition() + "]");
        userRepo.updateUser(id, req);
    }

    // ================= LOCK MANAGER/STAFF =================
    public void lockManageUser(int id) {
        AuthUserPrincipal principal = (AuthUserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        User target = userRepo.findById(id);
        if (target == null)
            throw new RuntimeException("User không tồn tại");

        if ("STAFF".equals(principal.role()))
            throw new RuntimeException("STAFF không được khóa user");

        // MANAGER chỉ được lock STAFF cùng rạp
        if ("MANAGER".equals(principal.role())) {
            if (!principal.cinemaId().equals(target.getCinemaId()))
                throw new RuntimeException("MANAGER không được khóa user rạp khác");
            if (target.getRoleId() == 2)
                throw new RuntimeException("MANAGER không được khóa MANAGER");
        }

        UpdateUserRequestDTO req = new UpdateUserRequestDTO();
        req.setIsActive(0);
        userRepo.updateUser(id, req);
    }

    // ================= UNLOCK MANAGER/STAFF =================
    public void unlockManageUser(int id) {
        AuthUserPrincipal principal = (AuthUserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        User target = userRepo.findById(id);
        if (target == null)
            throw new RuntimeException("User không tồn tại");

        if ("STAFF".equals(principal.role()))
            throw new RuntimeException("STAFF không được mở khóa user");

        // MANAGER chỉ được unlock STAFF cùng rạp
        if ("MANAGER".equals(principal.role())) {
            if (!principal.cinemaId().equals(target.getCinemaId()))
                throw new RuntimeException("MANAGER không được mở khóa user rạp khác");
            if (target.getRoleId() == 2)
                throw new RuntimeException("MANAGER không được mở khóa MANAGER");
        }

        UpdateUserRequestDTO req = new UpdateUserRequestDTO();
        req.setIsActive(1);
        userRepo.updateUser(id, req);
    }


}
