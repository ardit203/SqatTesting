package finki.ukim.mk.onlineclothingstore.service.impl;

import finki.ukim.mk.onlineclothingstore.model.Category;
import finki.ukim.mk.onlineclothingstore.model.Product;
import finki.ukim.mk.onlineclothingstore.model.enums.Department;
import finki.ukim.mk.onlineclothingstore.model.exceptions.IllegalImageUrlException;
import finki.ukim.mk.onlineclothingstore.model.exceptions.PriceCannotBeLessThanZeroException;
import finki.ukim.mk.onlineclothingstore.model.exceptions.ProductNotFoundException;
import finki.ukim.mk.onlineclothingstore.repository.ProductRepository;
import finki.ukim.mk.onlineclothingstore.service.CategoryService;
import finki.ukim.mk.onlineclothingstore.service.ProductService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;

import static finki.ukim.mk.onlineclothingstore.service.FieldFilterSpecification.*;

@Service
@AllArgsConstructor
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;
    private final CategoryService categoryService;

    @Override
    public List<Product> findAll() {
        return productRepository.findAll();
    }

    @Override
    public Product findById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));
    }

    @Override
    public Product create(String name, Double price, String description, Department department, String image, Long categoryId) {
        if(price < 0){
            throw new PriceCannotBeLessThanZeroException();
        }
        Category category = categoryService.findById(categoryId);
        return productRepository.save(new Product(name, description, price, department, image, category));
    }

    @Override
    public Product create(String name, Double price, String description, Department department, Long categoryId) {
        if(price < 0){
            throw new PriceCannotBeLessThanZeroException();
        }
        Category category = categoryService.findById(categoryId);
        return productRepository.save(new Product(name, description, price, department, category));
    }

    @Override
    public Product addImage(Long id, String image) {
        if(image == null || image.isEmpty()){
            throw new IllegalImageUrlException();
        }
        Product product = findById(id);
        product.setImage(image);
        return productRepository.save(product);
    }

    @Override
    public Product update(Long id, String name, Double price, String description, Department department, String image, Long categoryId) {
        if(price < 0){
            throw new PriceCannotBeLessThanZeroException();
        }
        Category category = categoryService.findById(categoryId);
        Product product = findById(id);
        product.setName(name);
        product.setPrice(price);
        product.setCategory(category);
        product.setDescription(description);
        product.setDepartment(department);
        product.setImage(image);

        return productRepository.save(product);
    }

    @Override
    public void deleteById(Long id) {
        productRepository.deleteById(id);
    }

    @Override
    public Page<Product> findPage(Long id, String name, Double greaterThan, Double lessThan, Department department, Long categoryId, Integer pageNum, Integer pageSize, String sort) {
        Specification<Product> specification = Specification.allOf(
                filterEquals(Product.class, "id", id),
                filterContainsText(Product.class, "name", name),
                greaterThan(Product.class, "price", greaterThan),
                lessThan(Product.class, "price", lessThan),
                filterEquals(Product.class, "category.id", categoryId),
                filterEqualsV(Product.class, "department", department)
        );

        if(pageNum == 0){
            pageNum = 1;
        }

        if(pageSize < 3){
            pageSize = 3;
        }


        return this.productRepository.findAll(
                specification,
                PageRequest.of(pageNum - 1, pageSize, sortFactory(sort)));
    }


    private Sort sortFactory(String sort) {
        switch (sort) {
            case "byPriceDesc":
                return Sort.by(Sort.Direction.DESC, "price");
            case "byIdAsc":
                return Sort.by(Sort.Direction.ASC, "id");
            case "byIdDesc":
                return Sort.by(Sort.Direction.DESC, "id");
            case "byNameAsc":
                return Sort.by(Sort.Direction.ASC, "name");
            case "byNameDesc":
                return Sort.by(Sort.Direction.DESC, "name");
            default:
                return Sort.by(Sort.Direction.ASC, "price");
        }
    }
}
