package finki.ukim.mk.onlineclothingstore.dto;

import finki.ukim.mk.onlineclothingstore.model.Order;
import finki.ukim.mk.onlineclothingstore.model.OrderVariant;
import finki.ukim.mk.onlineclothingstore.model.enums.DeliveryStatus;

import java.util.List;

public record OrderDto(
        Long orderId,
        List<OrderVariantDto> products,
        DeliveryStatus status,
        Double total
) {
    public static OrderDto from(Order order, List<OrderVariant> orderVariants){
        return new OrderDto(
                order.getId(),
                OrderVariantDto.from(orderVariants),
                order.getDelivery().getStatus(),
                order.getTotal()
        );
    }
}
