package finki.ukim.mk.onlineclothingstore.service;

import finki.ukim.mk.onlineclothingstore.model.Product;
import finki.ukim.mk.onlineclothingstore.model.enums.Department;
import org.springframework.data.domain.Page;

import java.util.List;

public interface ProductService {
    List<Product> findAll();

    Product findById(Long id);

    Product create(String name, Double price, String description, Department department, String image, Long categoryId);

    Product create(String name, Double price, String description, Department department, Long categoryId);

    Product addImage(Long id, String image);

    Product update(Long id, String name, Double price, String description, Department department, String image, Long categoryId);

    void deleteById(Long id);

    Page<Product> findPage(Long id, String name, Double greaterThan, Double lessThan, Department department, Long categoryId, Integer pageNum, Integer pageSize, String sort);
}
