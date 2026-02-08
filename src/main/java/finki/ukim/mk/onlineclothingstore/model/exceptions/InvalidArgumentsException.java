package finki.ukim.mk.onlineclothingstore.model.exceptions;

import org.springframework.security.core.AuthenticationException;

public class InvalidArgumentsException extends AuthenticationException {
    public InvalidArgumentsException() {
        super("Username or password cannot be empty!");
    }
}
