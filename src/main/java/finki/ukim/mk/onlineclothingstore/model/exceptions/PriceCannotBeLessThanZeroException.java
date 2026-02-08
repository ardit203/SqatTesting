package finki.ukim.mk.onlineclothingstore.model.exceptions;

public class PriceCannotBeLessThanZeroException extends RuntimeException{
    public PriceCannotBeLessThanZeroException() {
        super("Price cannot be less than 0.");
    }
}
