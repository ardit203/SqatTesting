package finki.ukim.mk.onlineclothingstore.service.impl;

import finki.ukim.mk.onlineclothingstore.model.ShoppingCart;
import finki.ukim.mk.onlineclothingstore.model.User;
import finki.ukim.mk.onlineclothingstore.model.exceptions.ShoppingCartNotFoundException;
import finki.ukim.mk.onlineclothingstore.repository.ShoppingCartRepository;
import finki.ukim.mk.onlineclothingstore.service.ShoppingCartService;
import finki.ukim.mk.onlineclothingstore.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class ShoppingCartServiceImpl implements ShoppingCartService {
    private final ShoppingCartRepository shoppingCartRepository;
    private final UserService userService;

    @Override
    public ShoppingCart findById(Long id) {
        return shoppingCartRepository.findById(id)
                .orElseThrow(() -> new ShoppingCartNotFoundException(id));
    }

    @Override
    public ShoppingCart findOrCreate(String username) {
        User user = userService.findByUsername(username);
        return shoppingCartRepository.findByUser(user)
                .orElseGet(() -> shoppingCartRepository.save(new ShoppingCart(user)));
    }

    @Override
    public void deleteById(Long id) {
        shoppingCartRepository.deleteById(id);
    }
}
