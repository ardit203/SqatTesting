package finki.ukim.mk.onlineclothingstore.model.exceptions;

public class OrderNotFoundException extends RuntimeException {
    public OrderNotFoundException(Long id) {
        super(String.format("Order with id %d does not exist",id));
    }
}
