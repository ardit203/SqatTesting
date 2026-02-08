package finki.ukim.mk.onlineclothingstore.service.impl;

import finki.ukim.mk.onlineclothingstore.model.Product;
import finki.ukim.mk.onlineclothingstore.model.Variant;
import finki.ukim.mk.onlineclothingstore.model.enums.Size;
import finki.ukim.mk.onlineclothingstore.model.exceptions.InvalidStockValueException;
import finki.ukim.mk.onlineclothingstore.model.exceptions.NegativeQuantityException;
import finki.ukim.mk.onlineclothingstore.model.exceptions.VariantNotFoundException;
import finki.ukim.mk.onlineclothingstore.model.exceptions.VariantOutOfStockException;
import finki.ukim.mk.onlineclothingstore.repository.VariantRepository;
import finki.ukim.mk.onlineclothingstore.service.ProductService;
import finki.ukim.mk.onlineclothingstore.service.VariantService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;

@Service
@AllArgsConstructor
public class VariantServiceImpl implements VariantService {
    private final VariantRepository variantRepository;
    private final ProductService productService;

    @Override
    public List<Variant> findByProductId(Long productId) {
        return variantRepository.findByProduct(productService.findById(productId));
    }

    @Override
    public Variant findById(Long id) {
        return variantRepository.findById(id)
                .orElseThrow(() -> new VariantNotFoundException(id));
    }

    @Override
    public Variant create(Size size, Integer stock, Long productId) {
        if(stock < 0){
            throw new InvalidStockValueException();
        }
        Product product = productService.findById(productId);
        return variantRepository.save(new Variant(product, size, stock));
    }

    @Override
    public Variant update(Long id, Size size, Integer stock, Long productId) {
        if(stock < 0){
            throw new InvalidStockValueException();
        }
        Product product = productService.findById(productId);
        Variant variant = findById(id);

        variant.setSize(size);
        variant.setStock(stock);
        variant.setProduct(product);

        return variantRepository.save(variant);
    }

    @Override
    public void deleteById(Long id) {
        variantRepository.deleteById(id);
    }

    @Override
    public Variant addToStock(Long id, Integer quantity) {
        if(quantity <= 0){
            throw new NegativeQuantityException(quantity);
        }
        Variant variant = findById(id);
        variant.addToStock(quantity);
        return variantRepository.save(variant);
    }

    @Override
    public Variant removeFromStock(Long id, Integer quantity) {
        if(quantity <= 0){
            throw new NegativeQuantityException(quantity);
        }
        Variant variant = findById(id);

        if(variant.getStock() - quantity < 0){
            throw new VariantOutOfStockException(variant.getProduct().getId(), variant.getProduct().getName(), variant.getSize(), variant.getStock(), quantity);
        }

        variant.removeFromStock(quantity);
        return variantRepository.save(variant);
    }

    @Override
    public List<Variant> getVariantsForProducts(List<Product> products) {
        if(products == null){
            return List.of();
        }

        return products.stream()
                .map(variantRepository::findByProduct)
                .flatMap(Collection::stream)
                .toList();
    }
}
