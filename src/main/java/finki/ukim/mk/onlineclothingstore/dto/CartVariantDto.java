package finki.ukim.mk.onlineclothingstore.dto;

import finki.ukim.mk.onlineclothingstore.model.CartVariant;
import finki.ukim.mk.onlineclothingstore.model.Variant;

import java.util.List;

public record CartVariantDto(Long cartVariantId, Long cartId, Long variantId, Integer quantity) {
    public static CartVariantDto from(CartVariant cartVariant) {
        return new CartVariantDto(
                cartVariant.getId(),
                cartVariant.getCart().getId(),
                cartVariant.getVariant().getId(),
                cartVariant.getQuantity()
        );
    }

    public static List<CartVariantDto> from(List<CartVariant> cartVariants) {
        return cartVariants.stream().map(CartVariantDto::from).toList();
    }
}
