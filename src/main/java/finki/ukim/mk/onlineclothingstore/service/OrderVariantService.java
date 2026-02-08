package finki.ukim.mk.onlineclothingstore.service;

import finki.ukim.mk.onlineclothingstore.model.*;

import java.util.List;

public interface OrderVariantService {
    OrderVariant findById(Long id);

    List<OrderVariant> addVariantsToOrder(Long orderId, List<CartVariant> cartVariants);

    void addVariantsToStockAfterOrderCancellation(Long orderId);

    void deleteById(Long id);
}