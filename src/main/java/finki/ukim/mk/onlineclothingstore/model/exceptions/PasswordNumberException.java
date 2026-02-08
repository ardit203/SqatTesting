package finki.ukim.mk.onlineclothingstore.model.exceptions;

public class PasswordNumberException extends RuntimeException{
    public PasswordNumberException(int numberCount) {
        super(String.format("Password must contain at least %d numbers (0-9).", numberCount));
    }
}
