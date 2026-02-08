package finki.ukim.mk.onlineclothingstore.service;

import finki.ukim.mk.onlineclothingstore.model.Product;
import finki.ukim.mk.onlineclothingstore.model.Variant;
import finki.ukim.mk.onlineclothingstore.model.enums.Size;
import org.springframework.data.domain.Page;

import java.util.List;

public interface VariantService {

    List<Variant> findByProductId(Long productId);

    Variant findById(Long id);

    Variant create(Size size, Integer stock, Long productId);

    Variant update(Long id, Size size, Integer stock, Long productId);

    void deleteById(Long id);

    Variant addToStock(Long id, Integer quantity);

    Variant removeFromStock(Long id, Integer quantity);

    List<Variant> getVariantsForProducts(List<Product> products);
}
