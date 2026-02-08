package finki.ukim.mk.onlineclothingstore.model.exceptions;

public class OrderVariantNotFoundException extends RuntimeException{
    public OrderVariantNotFoundException(Long id) {
        super(String.format("Order Variant with id: %d does not exist!",id));
    }
}