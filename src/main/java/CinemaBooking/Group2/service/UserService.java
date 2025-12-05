package CinemaBooking.Group2.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import CinemaBooking.Group2.models.User;
import CinemaBooking.Group2.repositories.UserRepository;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder; // inject bean

    public boolean register(User user) {
        if (userRepository.checkUserExistsByEmail(user.getEmail())) {
            return false;
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setIsActive(1); // active
        if(user.getRoleId() == 0) user.setRoleId(0);

        return userRepository.addUser(user);
    }
    
}

