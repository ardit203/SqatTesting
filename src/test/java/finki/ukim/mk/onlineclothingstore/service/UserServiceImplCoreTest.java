package finki.ukim.mk.onlineclothingstore.service;

import finki.ukim.mk.onlineclothingstore.model.User;
import finki.ukim.mk.onlineclothingstore.model.enums.Role;
import finki.ukim.mk.onlineclothingstore.model.exceptions.*;
import finki.ukim.mk.onlineclothingstore.repository.UserRepository;
import finki.ukim.mk.onlineclothingstore.service.impl.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;

import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.lang.reflect.Field;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplCoreTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl userService;

    private final String username = "user1";
    private final String name = "User";
    private final String surname = "Surname";
    private final Role role = Role.ROLE_USER;


    /* =================================================
       findByUsername(username)
       ================================================= */

    @Test
    void US_1_findByUsernameValidUsername() {
        User u = new User();
        u.setUsername(username);

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(u));

        User result = userService.findByUsername(username);

        assertNotNull(result);
        assertEquals(username, result.getUsername());


        verify(userRepository).findByUsername(username);
        verifyNoMoreInteractions(userRepository);
        verifyNoInteractions(passwordEncoder);
    }

    @Test
    void US_2_findByUsernameInvalidUsername() {

        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.findByUsername(username));

        verify(userRepository).findByUsername(username);
        verifyNoMoreInteractions(userRepository);
        verifyNoInteractions(passwordEncoder);
    }

    /* =================================================
       register(...)
       ================================================= */

    @Test
    void US_3_registerInvalidArgs() {

        assertThrows(InvalidArgumentsException.class, () ->
                userService.register(null, "x", "x", name, surname, role));


        verifyNoInteractions(userRepository);
        verifyNoInteractions(passwordEncoder);
    }

    @Test
    void US_4_registerPasswordsMismatch() {

        assertThrows(PasswordsDoNotMatchException.class, () ->
                userService.register(username, "ABcdef12!", "DIFFERENT12!", name, surname, role));

        verifyNoInteractions(userRepository);
        verifyNoInteractions(passwordEncoder);
    }

    @Test
    void US_5_registerUserAlreadyExists() {
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(new User()));

        assertThrows(UserAlreadyExistsException.class, () ->
                userService.register(username, "ABcdef12!", "ABcdef12!", name, surname, role));

        verify(userRepository).findByUsername(username);
        verify(userRepository, never()).save(any());
        verifyNoInteractions(passwordEncoder);
    }

    @Test
    void US_6_registerPasswordPolicyFails() {

        String weakUpper = "Abcdef12!";

        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

        assertThrows(PasswordUpperCaseException.class, () ->
                userService.register(username, weakUpper, weakUpper, name, surname, role));

        verify(userRepository).findByUsername(username);
        verify(userRepository, never()).save(any());
        verify(passwordEncoder, never()).encode(any());
    }

    @Test
    void US_7_registerValidInputs() {
        String raw = "ABcdef12!";
        String encoded = "ENC";

        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());
        when(passwordEncoder.encode(raw)).thenReturn(encoded);
        when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        User saved = userService.register(username, raw, raw, name, surname, role);


        assertNotNull(saved);
        assertEquals(username, saved.getUsername());
        assertEquals(name, saved.getName());
        assertEquals(surname, saved.getSurname());
        assertEquals(role, saved.getRole());
        assertEquals(encoded, saved.getPassword());


        verify(userRepository).findByUsername(username);
        verify(passwordEncoder).encode(raw);
        verify(userRepository).save(any(User.class));
    }

    /* =================================================
       changePassword(...)
       ================================================= */

    @Test
    void US_8_changePasswordUserNull() {
        assertThrows(UserNotFoundException.class, () ->
                userService.changePassword(null, "old", "new", "new"));

        verifyNoInteractions(passwordEncoder);
        verify(userRepository, never()).save(any());
    }

    @Test
    void US_9_changePasswordMissingFields() {
        User u = new User();
        u.setPassword("ENC_OLD");

        assertThrows(PasswordCannotBeEmptyException.class, () ->
                userService.changePassword(u, "", "new", "new"));

        verifyNoInteractions(passwordEncoder);
        verify(userRepository, never()).save(any());
    }

    @Test
    void US_10_changePasswordCurrentPasswordWrong() {
        User u = new User();
        u.setPassword("ENC_OLD");

        when(passwordEncoder.matches("wrongOld", "ENC_OLD")).thenReturn(false);

        assertThrows(InvalidCurrentPasswordException.class, () ->
                userService.changePassword(u, "wrongOld", "ABnew12!", "ABnew12!"));

        verify(passwordEncoder).matches("wrongOld", "ENC_OLD");
        verify(passwordEncoder, never()).encode(any());
        verify(userRepository, never()).save(any());
    }

    @Test
    void US_11_changePasswordNewPasswordsMismatch() {
        User u = new User();
        u.setPassword("ENC_OLD");

        when(passwordEncoder.matches("old", "ENC_OLD")).thenReturn(true);

        assertThrows(NewPasswordsDoNotMatchException.class, () ->
                userService.changePassword(u, "old", "ABnew12!", "DIFFERENT12!"));

        verify(passwordEncoder).matches("old", "ENC_OLD");
        verify(passwordEncoder, never()).encode(any());
        verify(userRepository, never()).save(any());
    }

    @Test
    void US_12_changePasswordNewSameAsOld() {
        User u = new User();
        u.setPassword("ENC_OLD");

        when(passwordEncoder.matches("old", "ENC_OLD")).thenReturn(true);

        when(passwordEncoder.matches("old", "ENC_OLD")).thenReturn(true);

        assertThrows(NewPasswordCannotBeSameAsOldException.class, () ->
                userService.changePassword(u, "old", "old", "old"));

        verify(passwordEncoder, atLeastOnce()).matches(any(), any());
        verify(passwordEncoder, never()).encode(any());
        verify(userRepository, never()).save(any());
    }

    @Test
    void US_13_changePasswordNewPasswordFailsPolicy() {
        User u = new User();
        u.setPassword("ENC_OLD");

        String current = "old";
        String newPwdNoSpecial = "ABcdef12";

        when(passwordEncoder.matches(current, "ENC_OLD")).thenReturn(true);
        when(passwordEncoder.matches(newPwdNoSpecial, "ENC_OLD")).thenReturn(false);

        assertThrows(PasswordSpecialSignException.class, () ->
                userService.changePassword(u, current, newPwdNoSpecial, newPwdNoSpecial));

        verify(passwordEncoder).matches(current, "ENC_OLD");
        verify(passwordEncoder).matches(newPwdNoSpecial, "ENC_OLD");
        verify(passwordEncoder, never()).encode(any());
        verify(userRepository, never()).save(any());
    }

    @Test
    void US_14_changePasswordValidUpdatesPassword() {
        User u = new User();
        u.setPassword("ENC_OLD");

        String current = "old";
        String newPwd = "ABnew12!";
        String encNew = "ENC_NEW";

        when(passwordEncoder.matches(current, "ENC_OLD")).thenReturn(true);
        when(passwordEncoder.matches(newPwd, "ENC_OLD")).thenReturn(false);
        when(passwordEncoder.encode(newPwd)).thenReturn(encNew);
        when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        User saved = userService.changePassword(u, current, newPwd, newPwd);

        assertEquals(encNew, saved.getPassword());

        verify(passwordEncoder).matches(current, "ENC_OLD");
        verify(passwordEncoder).matches(newPwd, "ENC_OLD");
        verify(passwordEncoder).encode(newPwd);
        verify(userRepository).save(u);
    }

    /* =================================================
       checkPassword(password)
       ================================================= */

    @Test
    void US_15_checkPasswordLengthTooShort() {

        String p = "AB12!aa";
        assertThrows(PasswordLengthException.class, () -> userService.checkPassword(p));
    }

    @Test
    void US_16_checkPasswordLengthTooLong() {

        String p = "A".repeat(37);
        assertThrows(PasswordLengthExceededException.class, () -> userService.checkPassword(p));
    }

    @Test
    void US_17_checkPasswordForbiddenChars() {

        String p = "ABcdef12! "; // includes space
        assertThrows(PasswordSpecialSignNotAllowedException.class, () -> userService.checkPassword(p));
    }

    @Test
    void US_18_checkPasswordNotEnoughUppercase() {

        String p = "Abcdef12!";
        assertThrows(PasswordUpperCaseException.class, () -> userService.checkPassword(p));
    }

    @Test
    void US_19_checkPasswordNotEnoughNumbers() {

        String p = "ABcdefg1!";
        assertThrows(PasswordNumberException.class, () -> userService.checkPassword(p));
    }

    @Test
    void US_20_checkPasswordMissingSpecialSign() {

        String p = "ABcdef12";
        assertThrows(PasswordSpecialSignException.class, () -> userService.checkPassword(p));
    }

    @Test
    void US_21_checkPasswordValidPassword() {
        String p = "ABcdef12!";
        assertDoesNotThrow(() -> userService.checkPassword(p));
    }



    @Test // Path: 1,2,8  (size=0 -> loop not entered -> return false)
    void US_22_hasUpperForEmptyString() {
        assertFalse(userService.hasUpper(""));
    }

    @Test // Path: 1,2,3,5,6,2,3,5,6,2,8  (2 iterations, isUpper=false both times, then i==size)
    void US_23_hasUpperReturnsFalseNoUpper() {
        assertFalse(userService.hasUpper("aa"));
    }

    @Test // Path: 1,2,3,4,5,6,2,8  (1 iteration, uppercase once, count!=2, then exit)
    void US_24_hasUpperReturnsFalseOneUpper() {
        assertFalse(userService.hasUpper("A"));
    }

    @Test // Path: 1,2,3,4,5,6,2,3,4,5,7  (2 uppercase -> count==2 -> return true)
    void US_25_hasUpperReturnsTrueTwoUpper() {
        assertTrue(userService.hasUpper("AB"));
    }

}
