package finki.ukim.mk.onlineclothingstore.model.exceptions;

public class ProductNotFoundException extends RuntimeException{
    public ProductNotFoundException(Long id) {
        super(String.format("Product with id: %d does not exist!",id));
    }
}