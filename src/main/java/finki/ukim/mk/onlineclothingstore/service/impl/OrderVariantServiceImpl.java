package finki.ukim.mk.onlineclothingstore.service.impl;

import finki.ukim.mk.onlineclothingstore.model.*;
import finki.ukim.mk.onlineclothingstore.model.exceptions.NegativeQuantityException;
import finki.ukim.mk.onlineclothingstore.model.exceptions.OrderVariantNotFoundException;
import finki.ukim.mk.onlineclothingstore.repository.OrderVariantRepository;
import finki.ukim.mk.onlineclothingstore.service.CartVariantService;
import finki.ukim.mk.onlineclothingstore.service.OrderService;
import finki.ukim.mk.onlineclothingstore.service.OrderVariantService;
import finki.ukim.mk.onlineclothingstore.service.VariantService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@AllArgsConstructor
@Service
public class OrderVariantServiceImpl implements OrderVariantService {
    private final OrderService orderService;
    private final VariantService variantService;
    private final OrderVariantRepository orderVariantRepository;
    private final CartVariantService cartVariantService;

    @Override
    public OrderVariant findById(Long id) {
        return orderVariantRepository.findById(id)
                .orElseThrow(() -> new OrderVariantNotFoundException(id));
    }


    public List<OrderVariant> findByOrderId(Long orderId) {
        Order order = orderService.findById(orderId);
        return orderVariantRepository.findByOrder(order);
    }


    private OrderVariant create(Long orderId, Long variantId, Integer quantity) {
        if(quantity <= 0){
            throw new NegativeQuantityException(quantity);
        }

        Order order = orderService.findById(orderId);
        Variant variant = variantService.findById(variantId);
        return orderVariantRepository.save(new OrderVariant(order, variant, quantity));
    }

    @Transactional
    @Override
    public List<OrderVariant> addVariantsToOrder(Long orderId, List<CartVariant> cartVariants) {
        if(cartVariants == null || cartVariants.isEmpty()){
            return List.of();
        }
        Order order = orderService.findById(orderId);
        cartVariantService.checkForSufficientStocks(cartVariants);
        List<OrderVariant> orderVariants = new ArrayList<>();
        for (CartVariant cartVariant : cartVariants) {
            int quantity = cartVariant.getQuantity();
            variantService.removeFromStock(cartVariant.getVariant().getId(), quantity);
            cartVariantService.deleteById(cartVariant.getId());
            orderVariants.add(this.create(order.getId(), cartVariant.getVariant().getId(), quantity));
        }
        return orderVariants;
    }

    @Transactional
    @Override
    public void addVariantsToStockAfterOrderCancellation(Long orderId) {
        List<OrderVariant> orderVariants = findByOrderId(orderId);
        for (OrderVariant orderVariant : orderVariants) {
            int quantity = orderVariant.getQuantity();
            variantService.addToStock(orderVariant.getVariant().getId(), quantity);
        }
    }

    @Override
    public void deleteById(Long id) {
        orderVariantRepository.deleteById(id);
    }
}
