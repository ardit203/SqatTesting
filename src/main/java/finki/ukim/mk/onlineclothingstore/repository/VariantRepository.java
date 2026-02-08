package finki.ukim.mk.onlineclothingstore.repository;

import finki.ukim.mk.onlineclothingstore.model.Product;
import finki.ukim.mk.onlineclothingstore.model.Variant;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface VariantRepository extends JpaSpecificationRepository<Variant, Long> {
    List<Variant> findByProduct(Product product);
}
