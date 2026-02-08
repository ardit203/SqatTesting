package finki.ukim.mk.onlineclothingstore.model.exceptions;

public class DeliveryNotFoundException extends RuntimeException {
    public DeliveryNotFoundException(Long id) {
        super(String.format("Delivery with id %d does not exist", id));
    }
}
