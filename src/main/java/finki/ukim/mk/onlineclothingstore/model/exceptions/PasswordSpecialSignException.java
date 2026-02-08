package finki.ukim.mk.onlineclothingstore.model.exceptions;

public class PasswordSpecialSignException extends RuntimeException{
    public PasswordSpecialSignException(String specialSigns) {
        super(String.format("Password must contain at least one special character: %s", specialSigns));
    }
}
