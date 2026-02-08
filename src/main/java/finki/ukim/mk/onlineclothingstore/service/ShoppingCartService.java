package finki.ukim.mk.onlineclothingstore.service;

import finki.ukim.mk.onlineclothingstore.model.ShoppingCart;
import finki.ukim.mk.onlineclothingstore.model.User;

import java.time.LocalDateTime;

public interface ShoppingCartService {
    ShoppingCart findById(Long id);

    ShoppingCart findOrCreate(String username);

    void deleteById(Long id);
}