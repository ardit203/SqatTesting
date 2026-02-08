package finki.ukim.mk.onlineclothingstore.service.impl;

import finki.ukim.mk.onlineclothingstore.model.Category;
//import finki.ukim.mk.onlineclothingstore.model.Discount;
import finki.ukim.mk.onlineclothingstore.model.exceptions.CategoryNotFoundException;
import finki.ukim.mk.onlineclothingstore.repository.CategoryRepository;
import finki.ukim.mk.onlineclothingstore.service.CategoryService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;
    @Override
    public List<Category> findAll() {
        return categoryRepository.findAll();
    }

    @Override
    public Category findById(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new CategoryNotFoundException(id));
    }

    @Override
    public Category create(String name, String description) {
        return categoryRepository.save(new Category(name, description));
    }

    @Override
    public Category update(Long id, String name, String description) {
        Category category = findById(id);
        category.setName(name);
        category.setDescription(description);

        return categoryRepository.save(category);
    }

    @Override
    public void deleteById(Long id) {
        categoryRepository.deleteById(id);
    }
}
