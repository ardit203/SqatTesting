package finki.ukim.mk.onlineclothingstore.model.exceptions;

public class NewPasswordCannotBeSameAsOldException extends RuntimeException{
    public NewPasswordCannotBeSameAsOldException() {
        super("Your new password cannot be the same as the old one!");
    }
}
