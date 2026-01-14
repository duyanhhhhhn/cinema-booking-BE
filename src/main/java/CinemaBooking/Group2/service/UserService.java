package CinemaBooking.Group2.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.security.core.context.SecurityContextHolder;

import CinemaBooking.Group2.dtos.admin.CreateUserRequestDTO;
import CinemaBooking.Group2.dtos.admin.UpdateUserRequestDTO;
import CinemaBooking.Group2.dtos.admin.UserResponseDTO;
import CinemaBooking.Group2.models.User;
import CinemaBooking.Group2.repositories.UserRepository;
import CinemaBooking.Group2.security.AuthUserPrincipal;

@Service
public class UserService {

    @Autowired private UserRepository userRepo;
    @Autowired private PasswordEncoder encoder;

    // ================= CREATE =================
    public void createUser(CreateUserRequestDTO req) {

        AuthUserPrincipal principal =
            (AuthUserPrincipal) SecurityContextHolder
                .getContext().getAuthentication().getPrincipal();

        String role = principal.role();
        Integer cinemaId = principal.cinemaId();

        if ("ADMIN".equals(role)) {
            if (req.getRoleId() != 2) {
                throw new RuntimeException("ADMIN chỉ được tạo MANAGER");
            }
        }
        else if ("MANAGER".equals(role)) {
            if (req.getRoleId() != 3) {
                throw new RuntimeException("MANAGER chỉ được tạo STAFF");
            }
            if (!cinemaId.equals(req.getCinemaId())) {
                throw new RuntimeException("MANAGER chỉ được tạo user trong rạp của mình");
            }
        }
        else {
            throw new RuntimeException("Bạn không có quyền tạo user");
        }

        if (userRepo.existsByEmail(req.getEmail())) {
            throw new RuntimeException("Email đã tồn tại");
        }

        String encodedPassword = encoder.encode(req.getPassword());
        userRepo.createStaff(req, encodedPassword);
    }

    // ================= GET USERS =================
    public List<UserResponseDTO> getUsers(Integer roleId, Integer cinemaId) {

        AuthUserPrincipal principal =
            (AuthUserPrincipal) SecurityContextHolder
                .getContext().getAuthentication().getPrincipal();

        String role = principal.role();

        if ("STAFF".equals(role)) {
            throw new RuntimeException("Bạn không có quyền xem danh sách user");
        }

        if ("MANAGER".equals(role)) {
            cinemaId = principal.cinemaId();
        }

        return userRepo.findUsers(roleId, cinemaId);
    }

    // ================= UPDATE =================
    public void updateUser(int id, UpdateUserRequestDTO req) {

        AuthUserPrincipal principal =
            (AuthUserPrincipal) SecurityContextHolder
                .getContext().getAuthentication().getPrincipal();

        User target = userRepo.findById(id);
        if (target == null) {
            throw new RuntimeException("User không tồn tại");
        }

        if ("MANAGER".equals(principal.role())
                && !principal.cinemaId().equals(target.getCinemaId())) {
            throw new RuntimeException("Bạn không có quyền sửa user rạp khác");
        }

        userRepo.updateUser(id, req);
    }

    // ================= LOCK =================
    public void lockUser(int id) {

        AuthUserPrincipal principal =
            (AuthUserPrincipal) SecurityContextHolder
                .getContext().getAuthentication().getPrincipal();

        User target = userRepo.findById(id);
        if (target == null) {
            throw new RuntimeException("User không tồn tại");
        }

        if ("MANAGER".equals(principal.role())
                && !principal.cinemaId().equals(target.getCinemaId())) {
            throw new RuntimeException("Không được khóa user rạp khác");
        }

        UpdateUserRequestDTO req = new UpdateUserRequestDTO();
        req.setIsActive(0);
        userRepo.updateUser(id, req);
    }

    // ================= DETAIL =================
    public UserResponseDTO getUserDetail(int id) {

        AuthUserPrincipal principal =
            (AuthUserPrincipal) SecurityContextHolder
                .getContext().getAuthentication().getPrincipal();

        if ("STAFF".equals(principal.role())) {
            throw new RuntimeException("Không có quyền xem user");
        }

        User target = userRepo.findById(id);
        if (target == null) {
            throw new RuntimeException("User không tồn tại");
        }

        if ("MANAGER".equals(principal.role())
                && !principal.cinemaId().equals(target.getCinemaId())) {
            throw new RuntimeException("Không được xem user rạp khác");
        }

        return userRepo.findUserDetail(id);
    }

    public User findByEmail(String email) {
        return userRepo.findByEmail(email);
    }
}
