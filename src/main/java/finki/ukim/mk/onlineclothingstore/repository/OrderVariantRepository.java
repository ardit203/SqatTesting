package finki.ukim.mk.onlineclothingstore.repository;

import finki.ukim.mk.onlineclothingstore.model.Order;
import finki.ukim.mk.onlineclothingstore.model.OrderVariant;
import finki.ukim.mk.onlineclothingstore.model.Variant;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface OrderVariantRepository extends JpaRepository<OrderVariant, Long> {
    List<OrderVariant> findByOrder(Order order);
    Optional<OrderVariant> findByOrderAndVariant(Order order, Variant variant);
}
