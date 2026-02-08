package finki.ukim.mk.onlineclothingstore.model.exceptions;

public class PasswordUpperCaseException extends RuntimeException{
    public PasswordUpperCaseException(int upperCaseCount) {
        super(String.format("Password must contain at least %d uppercase letters (A-Z).", upperCaseCount));
    }
}
