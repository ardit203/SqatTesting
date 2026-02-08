package finki.ukim.mk.onlineclothingstore.model.exceptions;

public class PasswordCannotBeEmptyException extends RuntimeException{
    public PasswordCannotBeEmptyException() {
        super("Password cannot be empty!");
    }
}
