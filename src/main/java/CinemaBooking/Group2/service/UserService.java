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

import CinemaBooking.Group2.dtos.admin.CreateUserRequestDTO;
import CinemaBooking.Group2.dtos.admin.UpdateUserRequestDTO;
import CinemaBooking.Group2.dtos.admin.UserResponseDTO;
import CinemaBooking.Group2.dtos.client.UpdateProfileRequestDTO;
import CinemaBooking.Group2.models.User;
import CinemaBooking.Group2.repositories.UserRepository;
import CinemaBooking.Group2.security.AuthUserPrincipal;

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
    @Autowired private UserRepository userRepo;
    @Autowired private PasswordEncoder encoder;

    // ================= CREATE USER =================
    public void createUser(CreateUserRequestDTO req) {
        AuthUserPrincipal principal =
            (AuthUserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

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
        AuthUserPrincipal principal =
            (AuthUserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if ("STAFF".equals(principal.role()))
            throw new RuntimeException("Bạn không có quyền xem danh sách user");

        if ("MANAGER".equals(principal.role()))
            cinemaId = principal.cinemaId();

        return userRepo.findUsers(roleId, cinemaId);
    }

    // ================= UPDATE USER =================
    public void updateUser(int id, UpdateUserRequestDTO req) {
        AuthUserPrincipal principal =
            (AuthUserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        User target = userRepo.findById(id);
        if (target == null) throw new RuntimeException("User không tồn tại");

        if ("MANAGER".equals(principal.role())
                && !principal.cinemaId().equals(target.getCinemaId())) {
            throw new RuntimeException("Bạn không có quyền sửa user rạp khác");
        }

        userRepo.updateUser(id, req);
    }

    // ================= LOCK USER =================
    public void lockUser(int id) {
        AuthUserPrincipal principal =
            (AuthUserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        User target = userRepo.findById(id);
        if (target == null) throw new RuntimeException("User không tồn tại");

        if ("MANAGER".equals(principal.role())
                && !principal.cinemaId().equals(target.getCinemaId())) {
            throw new RuntimeException("Không được khóa user rạp khác");
        }

        UpdateUserRequestDTO req = new UpdateUserRequestDTO();
        req.setIsActive(0);
        userRepo.updateUser(id, req);
    }

    // ================= GET USER DETAIL =================
    public UserResponseDTO getUserDetail(int id) {
        AuthUserPrincipal principal =
            (AuthUserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if ("STAFF".equals(principal.role()))
            throw new RuntimeException("Không có quyền xem user");

        User target = userRepo.findById(id);
        if (target == null) throw new RuntimeException("User không tồn tại");

        if ("MANAGER".equals(principal.role())
                && !principal.cinemaId().equals(target.getCinemaId())) {
            throw new RuntimeException("Không được xem user rạp khác");
        }

        return userRepo.findUserDetail(id);
    }

    public User findByEmail(String email) {
        return userRepo.findByEmail(email);
    }

    // ================= UPDATE PROFILE =================
    public void updateMyProfile(UpdateProfileRequestDTO req) {
        AuthUserPrincipal principal =
            (AuthUserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        User user = userRepo.findByEmail(principal.email());
        if (user == null) throw new RuntimeException("User không tồn tại");

        userRepo.updateProfile(user.getId(), req.getFullName(), req.getPhone());
    }

    // ================= AVATAR =================
    public String uploadAvatar(MultipartFile file) {
        if (file == null || file.isEmpty())
            throw new RuntimeException("Avatar không được để trống");

        if (file.getSize() > MAX_AVATAR_SIZE)
            throw new RuntimeException("Avatar tối đa 1MB");

        AuthUserPrincipal principal =
            (AuthUserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        User user = userRepo.findByEmail(principal.email());
        if (user == null) throw new RuntimeException("User không tồn tại");

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
                try { Files.deleteIfExists(newAvatarPath); } catch (Exception ignore) {}
            }
            throw new RuntimeException("Upload avatar thất bại: " + e.getMessage(), e);
        }
    }

    // ================= FILE UTILS =================
    private Path uploadRoot() {
        Path p = Paths.get(uploadDir);
        if (!p.isAbsolute()) p = Paths.get(System.getProperty("user.dir")).resolve(p);
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
        return root.relativize(absolutePath.toAbsolutePath().normalize())
                .toString()
                .replace("\\", "/");
    }

    private Path resolveUploadPath(String relative) {
        if (relative == null || relative.isBlank()) return null;
        return uploadRoot().resolve(relative).normalize();
    }

    private String toPublicMediaUrl(String relativePath) {
        if (!relativePath.startsWith("/")) relativePath = "/" + relativePath;
        return publicPrefix + relativePath;
    }

    private String getExtension(String filename) {
        if (filename == null) return "";
        int dot = filename.lastIndexOf('.');
        if (dot < 0) return "";
        return filename.substring(dot + 1).toLowerCase(Locale.ROOT);
    }
}
