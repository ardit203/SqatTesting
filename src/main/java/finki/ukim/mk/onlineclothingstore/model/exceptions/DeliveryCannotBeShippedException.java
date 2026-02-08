package finki.ukim.mk.onlineclothingstore.model.exceptions;

public class DeliveryCannotBeShippedException extends RuntimeException {
    public DeliveryCannotBeShippedException(Long id) {
        super(String.format("Delivery with id %d cannot be shipped!", id));
    }
}
