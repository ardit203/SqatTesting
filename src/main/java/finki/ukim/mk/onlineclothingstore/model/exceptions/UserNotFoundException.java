package finki.ukim.mk.onlineclothingstore.model.exceptions;

public class UserNotFoundException extends RuntimeException{
    public UserNotFoundException() {
        super("User was not found or is not logged in!");
    }

    public UserNotFoundException(String username) {
        super(String.format("User with username %s does not exist!", username));
    }
}
