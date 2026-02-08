package finki.ukim.mk.onlineclothingstore.model.exceptions;

public class DiscountNotFoundException extends RuntimeException {
    public DiscountNotFoundException(Long id) {
        super(String.format("Discount with id %d does not exist!", id));
    }
}
