package finki.ukim.mk.onlineclothingstore.service;

import finki.ukim.mk.onlineclothingstore.dto.CartVariantDto;
import finki.ukim.mk.onlineclothingstore.model.CartVariant;

import java.util.List;

public interface CartVariantService {
    CartVariant findById(Long id);

    List<CartVariant> findByCartId(Long cartId);

    List<CartVariant> findByUsername(String username);

    //If it doesnt exist it creates it, if it exists then it only increases the quantity by summing the new quantity with the old
    CartVariant createOrMerge(Long cartId, Long variantId, Integer quantity);


    //Updating by replacing the old quantity with the new one
    CartVariant update(Long id, Integer quantity);

    //Updates the cartVariants, that were modified from the "cart.html" template or from "/cart" path
    List<CartVariant> toCartVariants(List<CartVariantDto> cartVariantDtos);

    void deleteById(Long id);

    boolean isEmpty(String username);

    void checkForSufficientStocks(List<CartVariant> cartVariants);
}
