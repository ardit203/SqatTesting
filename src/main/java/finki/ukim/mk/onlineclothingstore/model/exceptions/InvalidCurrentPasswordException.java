package finki.ukim.mk.onlineclothingstore.model.exceptions;

public class InvalidCurrentPasswordException extends RuntimeException{
    public InvalidCurrentPasswordException() {
        super("Your password does not match the current password!");
    }
}
