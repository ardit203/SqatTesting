package finki.ukim.mk.onlineclothingstore.model.exceptions;

public class NewPasswordsDoNotMatchException extends RuntimeException{
    public NewPasswordsDoNotMatchException() {
        super("Your new passwords do not match!");
    }
}
