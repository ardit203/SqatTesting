package finki.ukim.mk.onlineclothingstore.model.exceptions;

public class ShoppingCartNotFoundException extends RuntimeException{
    public ShoppingCartNotFoundException(Long id) {
        super(String.format("Shopping Cart with id: %d does not exist!",id));
    }
}
