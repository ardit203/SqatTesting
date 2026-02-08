package finki.ukim.mk.onlineclothingstore.model.exceptions;

public class CartVariantNotFoundException extends RuntimeException {
    public CartVariantNotFoundException(Long id) {
        super(String.format("CartVariant with id %d does not exist", id));
    }
}
