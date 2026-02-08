package finki.ukim.mk.onlineclothingstore.model.exceptions;

import finki.ukim.mk.onlineclothingstore.model.enums.Size;

public class VariantOutOfStockException extends RuntimeException{
    public VariantOutOfStockException(Long productId, String productName, Size size, Integer quantity, Integer stock) {
        super(String.format(
                "Sorry, %s (size %s) is currently low on stock. You requested %d, but only %d are available.",
                productName, size, quantity, stock
        ));

    }
}
