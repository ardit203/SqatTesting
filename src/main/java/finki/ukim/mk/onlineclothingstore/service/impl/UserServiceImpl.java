package finki.ukim.mk.onlineclothingstore.service.impl;

import finki.ukim.mk.onlineclothingstore.model.User;
import finki.ukim.mk.onlineclothingstore.model.enums.Role;
import finki.ukim.mk.onlineclothingstore.model.exceptions.*;
import finki.ukim.mk.onlineclothingstore.repository.UserRepository;
import finki.ukim.mk.onlineclothingstore.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.regex.Pattern;

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private static final Pattern PASSWORD_CHARS =
            Pattern.compile("^[A-Za-z0-9" + Pattern.quote(ALLOWED_SPECIAL) + "]+$");


    @Override
    public User findByUsername(String username) {
        return userRepository.findByUsername(username).orElseThrow(() -> new UserNotFoundException(username));
    }

    @Override
    public User register(String username, String password, String repeatPassword, String name, String surname, Role role) {
        if (username == null || password == null || repeatPassword == null ||
                username.isEmpty() || password.isEmpty() || repeatPassword.isEmpty()) {
            throw new InvalidArgumentsException();
        }

        if (!password.equals(repeatPassword)) {
            throw new PasswordsDoNotMatchException();
        }

        if (this.userRepository.findByUsername(username).isPresent()) {
            throw new UserAlreadyExistsException(username);
        }

        checkPassword(password);

        User user = new User(username, passwordEncoder.encode(password), name, surname, role);
        return userRepository.save(user);
    }

    @Override
    public List<String> findAllUsernames() {
        return userRepository.findAll().stream().map(User::getUsername).toList();
    }

    @Override
    public User changePassword(User user, String currentPassword, String newPassword, String confirmNewPassword) {
        if(user == null){
            throw new UserNotFoundException();
        }
        if (currentPassword == null || currentPassword.isEmpty() || newPassword == null || newPassword.isEmpty() || confirmNewPassword == null || confirmNewPassword.isEmpty()) {
            throw new PasswordCannotBeEmptyException();
        }

        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
            throw new InvalidCurrentPasswordException();
        }

        if (!newPassword.equals(confirmNewPassword)) {
            throw new NewPasswordsDoNotMatchException();
        }

        if (passwordEncoder.matches(newPassword, user.getPassword())) {
            throw new NewPasswordCannotBeSameAsOldException();
        }
        checkPassword(newPassword);

        user.setPassword(passwordEncoder.encode(newPassword));
        return userRepository.save(user);
    }


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException(
                username));
    }


    public void checkPassword(String password) {
        if (password.length() < minPasswordLength)
            throw new PasswordLengthException(minPasswordLength);

        if(password.length() > maxPasswordLength){
            throw new PasswordLengthExceededException(maxPasswordLength);
        }

        if (!containsOnlyAllowedSpecialSigns(password))
            throw new PasswordSpecialSignNotAllowedException(ALLOWED_SPECIAL);

        if (!hasUpper(password))
            throw new PasswordUpperCaseException(upperCaseCount);

        if (!hasNumber(password))
            throw new PasswordNumberException(numberCount);

        if (!hasSpecialSign(password))
            throw new PasswordSpecialSignException(ALLOWED_SPECIAL);



    }

    /*Prime Path coverage*/
    public boolean hasUpper(String password) {
        int count = 0;
        char [] passwordChars = password.toCharArray();
        int size = passwordChars.length;
        for (int i=0 ; i<size ; i++) {
            boolean isUpper = Character.isUpperCase(passwordChars[i]);
            if (isUpper) {
                count++;
            }
            if(count == upperCaseCount) return true;
        }
        return false;
    }

    public boolean hasNumber(String password) {
        int count = 0;
        for (char c : password.toCharArray()) {
            if (Character.isDigit(c)){
                count++;
            }
            if(count == numberCount) return true;
        }
        return false;
    }

    public boolean hasSpecialSign(String password) {
        for (char c : password.toCharArray()) {
            if (ALLOWED_SPECIAL.indexOf(c) >= 0) return true;
        }
        return false;
    }

    public boolean containsOnlyAllowedSpecialSigns(String password) {
        return PASSWORD_CHARS.matcher(password).matches();
    }
}
