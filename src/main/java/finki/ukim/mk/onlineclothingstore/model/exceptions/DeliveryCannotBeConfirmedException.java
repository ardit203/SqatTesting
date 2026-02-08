package finki.ukim.mk.onlineclothingstore.model.exceptions;

public class DeliveryCannotBeConfirmedException extends RuntimeException{
    public DeliveryCannotBeConfirmedException(Long id) {
        super(String.format("Delivery with id %d cannot be confirmed!", id));
    }
}
