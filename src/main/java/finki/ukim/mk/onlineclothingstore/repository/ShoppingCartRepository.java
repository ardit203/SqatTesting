package finki.ukim.mk.onlineclothingstore.repository;

import finki.ukim.mk.onlineclothingstore.model.ShoppingCart;
import finki.ukim.mk.onlineclothingstore.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ShoppingCartRepository extends JpaRepository<ShoppingCart, Long> {
    Optional<ShoppingCart> findByUser(User user);
}
