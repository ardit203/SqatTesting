package finki.ukim.mk.onlineclothingstore.model.exceptions;

public class PasswordLengthException extends RuntimeException{
    public PasswordLengthException(int length) {
        super(String.format("Password must be at least %d characters long.", length));
    }
}
