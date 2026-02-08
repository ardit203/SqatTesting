package finki.ukim.mk.onlineclothingstore.service;

import finki.ukim.mk.onlineclothingstore.model.User;
import finki.ukim.mk.onlineclothingstore.model.enums.Role;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;

public interface UserService extends UserDetailsService {
    int minPasswordLength = 8;
    int maxPasswordLength = 36;
    int upperCaseCount = 2;
    int numberCount = 2;
    String ALLOWED_SPECIAL = ".!@#$%^&";// "!@#$%^&*()-_=+[]{};:'\",.<>?/\\|`~";
    String message = String.format("Your password must be at least %d and at most %d characters long, " +
            "include at least %d uppercase letter (A–Z) and at least %d number (0–9), " +
            "and contain at least one special character from %s. " +
            "Only Latin letters (A–Z, a–z), numbers (0–9), and these special characters are allowed: %s. No other symbols or emojis are permitted.", minPasswordLength, maxPasswordLength, upperCaseCount, numberCount, ALLOWED_SPECIAL, ALLOWED_SPECIAL);

    User findByUsername(String username);

    User register(String username, String password, String repeatPassword, String name, String surname, Role role);

    List<String> findAllUsernames();

    User changePassword(User user, String currentPassword, String newPassword, String confirmNewPassword);
}
