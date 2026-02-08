package finki.ukim.mk.onlineclothingstore.repository;

import finki.ukim.mk.onlineclothingstore.model.CartVariant;
import finki.ukim.mk.onlineclothingstore.model.ShoppingCart;
import finki.ukim.mk.onlineclothingstore.model.User;
import finki.ukim.mk.onlineclothingstore.model.Variant;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CartVariantRepository extends JpaRepository<CartVariant, Long> {
    List<CartVariant> findByCart(ShoppingCart cart);
    Optional<CartVariant> findByCartAndVariant(ShoppingCart cart, Variant variant);
    List<CartVariant> findByCart_User(User cartUser);
    boolean existsCartVariantByCart_User(User cartUser);
}
