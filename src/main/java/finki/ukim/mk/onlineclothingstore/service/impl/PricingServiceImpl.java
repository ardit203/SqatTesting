package finki.ukim.mk.onlineclothingstore.service.impl;

import finki.ukim.mk.onlineclothingstore.model.CartVariant;
import finki.ukim.mk.onlineclothingstore.model.ShoppingCart;
import finki.ukim.mk.onlineclothingstore.model.User;
import finki.ukim.mk.onlineclothingstore.service.CartVariantService;
import finki.ukim.mk.onlineclothingstore.service.PricingService;
import finki.ukim.mk.onlineclothingstore.service.ShoppingCartService;
import finki.ukim.mk.onlineclothingstore.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class PricingServiceImpl implements PricingService {
    private final ShoppingCartService shoppingCartService;
    private final CartVariantService cartVariantService;

    @Override
    public Double calculateTotal(List<CartVariant> cartVariants){
        if(cartVariants == null){
            return 0.0;
        }
        return cartVariants.stream()
                .mapToDouble(cartVariant -> cartVariant.getQuantity() * cartVariant.getVariant().getProduct().getPrice())
                .sum();
    }

    @Override
    public Double calculateTotal(String username){
        ShoppingCart cart = shoppingCartService.findOrCreate(username);
        List<CartVariant> cartVariants = cartVariantService.findByCartId(cart.getId());
        return calculateTotal(cartVariants);
    }
}
