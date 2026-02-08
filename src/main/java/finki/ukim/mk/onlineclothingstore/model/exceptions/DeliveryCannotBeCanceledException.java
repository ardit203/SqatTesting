package finki.ukim.mk.onlineclothingstore.model.exceptions;

public class DeliveryCannotBeCanceledException extends RuntimeException {
    public DeliveryCannotBeCanceledException(Long id) {
        super(String.format("Delivery with id %d cannot be canceled!", id));
    }
}
