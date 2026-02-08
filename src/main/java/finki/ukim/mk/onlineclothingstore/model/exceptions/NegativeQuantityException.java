package finki.ukim.mk.onlineclothingstore.model.exceptions;

public class NegativeQuantityException extends RuntimeException{
    public NegativeQuantityException(int quantity) {
        super(String.format("Quantity must be at least 1. You entered %d.", quantity));
    }

}
