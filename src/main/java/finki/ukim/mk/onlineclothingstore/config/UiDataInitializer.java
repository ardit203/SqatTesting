package finki.ukim.mk.onlineclothingstore.config;

import finki.ukim.mk.onlineclothingstore.model.User;
import finki.ukim.mk.onlineclothingstore.model.enums.Department;
import finki.ukim.mk.onlineclothingstore.model.enums.Role;
import finki.ukim.mk.onlineclothingstore.model.enums.Size;
import finki.ukim.mk.onlineclothingstore.repository.UserRepository;
import finki.ukim.mk.onlineclothingstore.service.CategoryService;
import finki.ukim.mk.onlineclothingstore.service.ProductService;
import finki.ukim.mk.onlineclothingstore.service.VariantService;
import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import static finki.ukim.mk.onlineclothingstore.config.DataInitializer.*;

@Profile("ui")
@Component
@AllArgsConstructor
public class UiDataInitializer {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final CategoryService categoryService;
    private final ProductService productService;
    private final VariantService variantService;

    @Profile("ui")
    @PostConstruct
    public void ui(){
        Integer stock10 = 10;
        Integer stock5 = 5;
        Integer stock3 = 3;
        categoryService.create("T-Shirts", "T-Shirts");
        categoryService.create("Jeans", "Jeans");
        categoryService.create("Pants", "Pants");
        categoryService.create("Jackets", "Jackets");
        categoryService.create("Dresses", "Dresses");
        categoryService.create("Hoodies", "Hoodies");

        productService.create("Nirvana", 22.90, getDescription(), Department.KIDS, "/uploads/ui/1.jpg", 1L);
        variantService.create(Size.S, stock10, 1L);
        variantService.create(Size.XS, stock5, 1L);

        productService.create("Lady Gaga", 22.90, getDescription(), Department.KIDS, "/uploads/ui/2.jpg", 1L);
        variantService.create(Size.XS, stock3, 2L);
        variantService.create(Size.S, stock10, 2L);

        productService.create("Contrast Heart Rib", 39.90, getDescription(), Department.MEN, "/uploads/ui/3.jpg", 1L);
        variantService.create(Size.M, stock5, 3L);
        variantService.create(Size.L, stock3, 3L);
        variantService.create(Size.XL, stock10, 3L);

        productService.create("Short Plaid Dress", 69.90, getDescription(), Department.WOMEN, "/uploads/ui/4.jpg", 5L);
        variantService.create(Size.S, stock5, 4L);
        variantService.create(Size.M, stock3, 4L);
        variantService.create(Size.L, stock10, 4L);

        productService.create("Lace Capri Leggings", 39.90, getDescription(), Department.WOMEN, "/uploads/ui/5.jpg", 3L);
        variantService.create(Size.L, stock5, 5L);
        variantService.create(Size.XL, stock3, 5L);
        variantService.create(Size.XXL, stock10, 5L);

        productService.create("Embroidered Cupid Balloon Fit Jeans", 79.90, getDescription(), Department.MEN, "/uploads/ui/6.jpg", 2L);
        variantService.create(Size.M, stock5, 6L);
        variantService.create(Size.L, stock3, 6L);
        variantService.create(Size.XXXL, stock10, 6L);
        variantService.create(Size.XXXXL, stock3, 6L);

        User user = userRepository.save(new User(
                "username",
                passwordEncoder.encode("USER@123"),
                "User",
                "User",
                Role.ROLE_USER
        ));
    }
}
