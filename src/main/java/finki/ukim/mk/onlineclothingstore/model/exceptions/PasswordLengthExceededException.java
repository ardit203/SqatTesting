package finki.ukim.mk.onlineclothingstore.model.exceptions;

public class PasswordLengthExceededException extends RuntimeException{
    public PasswordLengthExceededException(int length) {
        super(String.format("Password cannot contain more than %d letters", length));
    }
}
