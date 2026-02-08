package finki.ukim.mk.onlineclothingstore.dto;

import finki.ukim.mk.onlineclothingstore.model.OrderVariant;
import finki.ukim.mk.onlineclothingstore.model.Variant;
import finki.ukim.mk.onlineclothingstore.model.enums.Department;
import finki.ukim.mk.onlineclothingstore.model.enums.Size;

import java.util.List;

public record OrderVariantDto(
        Long productId,
        Long variantId,
        String productName,
        Size size,
        String image,
        Department department,
        String categoryName,
        Integer quantity
) {
    public static OrderVariantDto from(OrderVariant orderVariant){
        return new OrderVariantDto(
                orderVariant.getVariant().getProduct().getId(),
                orderVariant.getVariant().getId(),
                orderVariant.getVariant().getProduct().getName(),
                orderVariant.getVariant().getSize(),
                orderVariant.getVariant().getProduct().getImage(),
                orderVariant.getVariant().getProduct().getDepartment(),
                orderVariant.getVariant().getProduct().getCategory().getName(),
                orderVariant.getQuantity()
                );
    }

    public static List<OrderVariantDto> from(List<OrderVariant> orderVariants){
        return orderVariants.stream().map(OrderVariantDto::from).toList();
    }
}
