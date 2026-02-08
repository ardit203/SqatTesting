package finki.ukim.mk.onlineclothingstore.model.exceptions;

public class VariantNotFoundException extends RuntimeException{
    public VariantNotFoundException(Long id) {
        super(String.format("Variant with id: %d does not exist!",id));
    }
}