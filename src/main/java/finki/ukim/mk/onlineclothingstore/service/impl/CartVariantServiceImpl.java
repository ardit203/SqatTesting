package finki.ukim.mk.onlineclothingstore.service.impl;

import finki.ukim.mk.onlineclothingstore.dto.CartVariantDto;
import finki.ukim.mk.onlineclothingstore.model.*;
import finki.ukim.mk.onlineclothingstore.model.exceptions.CartVariantNotFoundException;
import finki.ukim.mk.onlineclothingstore.model.exceptions.NegativeQuantityException;
import finki.ukim.mk.onlineclothingstore.model.exceptions.VariantOutOfStockException;
import finki.ukim.mk.onlineclothingstore.repository.CartVariantRepository;
import finki.ukim.mk.onlineclothingstore.service.CartVariantService;
import finki.ukim.mk.onlineclothingstore.service.ShoppingCartService;
import finki.ukim.mk.onlineclothingstore.service.UserService;
import finki.ukim.mk.onlineclothingstore.service.VariantService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@AllArgsConstructor
@Service
public class CartVariantServiceImpl implements CartVariantService {
    private final ShoppingCartService shoppingCartService;
    private final CartVariantRepository cartVariantRepository;
    private final VariantService variantService;
    private final UserService userService;

    @Override
    public CartVariant findById(Long id) {
        return cartVariantRepository.findById(id)
                .orElseThrow(() -> new CartVariantNotFoundException(id));
    }

    @Override
    public List<CartVariant> findByCartId(Long cartId) {
        ShoppingCart cart = shoppingCartService.findById(cartId);
        return cartVariantRepository.findByCart(cart);
    }

    @Override
    public List<CartVariant> findByUsername(String username) {
        User user = userService.findByUsername(username);
        return cartVariantRepository.findByCart_User(user);
    }

    @Override
    public CartVariant createOrMerge(Long cartId, Long variantId, Integer quantity) {
        if(quantity <= 0){
            throw new NegativeQuantityException(quantity);
        }

        ShoppingCart cart = shoppingCartService.findById(cartId);
        Variant variant = variantService.findById(variantId);

        Optional<CartVariant> optionalCartVariant = cartVariantRepository.findByCartAndVariant(cart, variant);

        if (optionalCartVariant.isPresent()) {
            CartVariant cartVariant = optionalCartVariant.get();
            cartVariant.setQuantity(cartVariant.getQuantity() + quantity);
            return cartVariantRepository.save(cartVariant);
        }

        return cartVariantRepository.save(new CartVariant(cart, variant, quantity));
    }


    @Override
    public CartVariant update(Long id, Integer quantity) {
        if(quantity <= 0){
            throw new NegativeQuantityException(quantity);
        }
        CartVariant cartVariant = cartVariantRepository.findById(id)
                        .orElseThrow(() -> new CartVariantNotFoundException(id));
        cartVariant.setQuantity(quantity);
        return cartVariantRepository.save(cartVariant);
    }

    @Transactional
    @Override
    public List<CartVariant> toCartVariants(List<CartVariantDto> cartVariantDtos) {
        if(cartVariantDtos == null){
            return List.of();
        }
        List<CartVariant> cartVariants = new ArrayList<>();
        for (CartVariantDto cartVariantDto : cartVariantDtos) {
            CartVariant cartVariant = findById(cartVariantDto.cartVariantId());
            cartVariants.add(cartVariant);
        }
        return cartVariants;
    }

    @Override
    public void deleteById(Long id) {
        cartVariantRepository.deleteById(id);
    }

    @Override
    public boolean isEmpty(String username) {
        User user = userService.findByUsername(username);
        return !cartVariantRepository.existsCartVariantByCart_User(user);
    }

    @Transactional
    @Override
    public void checkForSufficientStocks(List<CartVariant> cartVariants) {
        if (cartVariants == null){
            return;
        }
        for (CartVariant cartVariant : cartVariants) {
            if (cartVariant.getQuantity() > cartVariant.getVariant().getStock()) {
                throw new VariantOutOfStockException(
                        cartVariant.getVariant().getProduct().getId(),
                        cartVariant.getVariant().getProduct().getName(),
                        cartVariant.getVariant().getSize(),
                        cartVariant.getQuantity(),
                        cartVariant.getVariant().getStock()
                );
            }
        }
    }
}
