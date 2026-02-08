package finki.ukim.mk.onlineclothingstore.dto;

import finki.ukim.mk.onlineclothingstore.model.Product;
import finki.ukim.mk.onlineclothingstore.model.Variant;
import finki.ukim.mk.onlineclothingstore.model.enums.Size;

import java.util.List;

public record VariantDto(Long id, Long productId, Size size, Integer stock) {
    public static VariantDto from(Variant variant){
        return new VariantDto(variant.getId(), variant.getProduct().getId(), variant.getSize(), variant.getStock());
    }

    public static List<VariantDto> from(List<Variant> variants){
        return variants.stream().map(VariantDto::from).toList();
    }
}
