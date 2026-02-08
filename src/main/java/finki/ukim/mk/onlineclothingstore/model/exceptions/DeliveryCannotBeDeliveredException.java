package finki.ukim.mk.onlineclothingstore.model.exceptions;

public class DeliveryCannotBeDeliveredException extends RuntimeException {
    public DeliveryCannotBeDeliveredException(Long id) {
        super(String.format("Delivery with id %d cannot be delivered!", id));
    }
}