package finki.ukim.mk.onlineclothingstore.service;

import finki.ukim.mk.onlineclothingstore.model.Category;
//import finki.ukim.mk.onlineclothingstore.model.Discount;

import java.util.List;

public interface CategoryService {
    List<Category> findAll();

    Category findById(Long id);

    Category create(String name, String description);

    Category update(Long id, String name, String description);

    void deleteById(Long id);
}