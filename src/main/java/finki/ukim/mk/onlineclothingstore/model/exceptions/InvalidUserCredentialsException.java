package finki.ukim.mk.onlineclothingstore.model.exceptions;

import org.springframework.security.core.AuthenticationException;

public class InvalidUserCredentialsException extends AuthenticationException {
    public InvalidUserCredentialsException() {
        super("Incorrect username or password!");
    }
}
