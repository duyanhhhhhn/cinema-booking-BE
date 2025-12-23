package CinemaBooking.Group2.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

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

    public void createUser(CreateUserRequestDTO req) {

        // 1️ Lấy user đang đăng nhập
        AuthUserPrincipal principal =
            (AuthUserPrincipal) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();

        String creatorRole = principal.role();
        Integer creatorCinemaId = principal.cinemaId();

        // 2️ Phân quyền tạo user
        if ("ADMIN".equals(creatorRole)) {
            if (req.getRoleId() != 2) {
                throw new RuntimeException("ADMIN chỉ được tạo MANAGER");
            }
        }


        else if ("MANAGER".equals(creatorRole)) {
            // MANAGER → chỉ được tạo STAFF
            if (req.getRoleId() != 3) { // role_id = 3 là STAFF
                throw new RuntimeException("MANAGER chỉ được tạo STAFF");
            }

            //  Ràng buộc cinema
            if (!creatorCinemaId.equals(req.getCinemaId())) {
                throw new RuntimeException("MANAGER chỉ được tạo nhân viên trong rạp của mình");
            }
        }

        else {
            throw new RuntimeException("Bạn không có quyền tạo người dùng");
        }

        // 3️ Check email
        if (userRepo.existsByEmail(req.getEmail())) {
            throw new RuntimeException("Email đã tồn tại");
        }

        // 4️ Hash password
        String encodedPassword = encoder.encode(req.getPassword());

        // 5️ Insert DB
        userRepo.createStaff(req, encodedPassword);
    }
    
    public List<UserResponseDTO> getUsers(Integer roleId, Integer cinemaId) {

        // 1️ Lấy user đang đăng nhập
        AuthUserPrincipal principal =
            (AuthUserPrincipal) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();

        String role = principal.role();
        Integer myCinemaId = principal.cinemaId();

        // 2️ STAFF không được xem
        if ("STAFF".equals(role)) {
            throw new RuntimeException("Bạn không có quyền xem danh sách user");
        }

        // 3️ MANAGER: cưỡng chế cinema
        if ("MANAGER".equals(role)) {
            cinemaId = myCinemaId; //  override
        }

        // 4️ ADMIN: không giới hạn
        return userRepo.findUsers(roleId, cinemaId);
    }
    
    public void updateUser(int id, UpdateUserRequestDTO req) {

        AuthUserPrincipal principal =
            (AuthUserPrincipal) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();

        User target = userRepo.findById(id);
        if (target == null) throw new RuntimeException("User không tồn tại");

        if ("MANAGER".equals(principal.role())) {
            if (!principal.cinemaId().equals(target.getCinemaId())) {
                throw new RuntimeException("Bạn không có quyền sửa user rạp khác");
            }
        }

        userRepo.updateUser(id, req);
    }
    
    public void lockUser(int id) {

        AuthUserPrincipal principal =
            (AuthUserPrincipal) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();

        User target = userRepo.findById(id);
        if (target == null) throw new RuntimeException("User không tồn tại");

        if ("MANAGER".equals(principal.role())) {
            if (!principal.cinemaId().equals(target.getCinemaId())) {
                throw new RuntimeException("Không được khóa user rạp khác");
            }
        }

        UpdateUserRequestDTO req = new UpdateUserRequestDTO();
        req.setIsActive(0);
        userRepo.updateUser(id, req);
    }

}
