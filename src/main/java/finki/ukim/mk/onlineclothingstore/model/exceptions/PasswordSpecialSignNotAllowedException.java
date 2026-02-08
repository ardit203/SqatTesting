package finki.ukim.mk.onlineclothingstore.model.exceptions;

public class PasswordSpecialSignNotAllowedException extends RuntimeException{
    public PasswordSpecialSignNotAllowedException(String specialSigns) {
        super(String.format("Password contains a character that is not allowed. Allowed special characters are: %s",specialSigns));
    }
}
