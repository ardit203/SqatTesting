package finki.ukim.mk.onlineclothingstore.model.exceptions;

public class InvalidStockValueException extends RuntimeException{
    public InvalidStockValueException() {
        super("Stock value cannot be less than 0!");
    }
}
