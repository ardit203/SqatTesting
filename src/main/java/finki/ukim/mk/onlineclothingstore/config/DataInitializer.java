package finki.ukim.mk.onlineclothingstore.config;

import finki.ukim.mk.onlineclothingstore.model.Product;
import finki.ukim.mk.onlineclothingstore.model.User;
import finki.ukim.mk.onlineclothingstore.model.Variant;
import finki.ukim.mk.onlineclothingstore.model.enums.Department;
import finki.ukim.mk.onlineclothingstore.model.enums.Role;
import finki.ukim.mk.onlineclothingstore.model.enums.Size;
import finki.ukim.mk.onlineclothingstore.repository.UserRepository;
import finki.ukim.mk.onlineclothingstore.service.CategoryService;
import finki.ukim.mk.onlineclothingstore.service.OrderService;
import finki.ukim.mk.onlineclothingstore.service.ProductService;
import finki.ukim.mk.onlineclothingstore.service.VariantService;
import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Profile("h2")
@Component
@AllArgsConstructor
public class DataInitializer {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final CategoryService categoryService;
    private final ProductService productService;
    private final VariantService variantService;
    private final OrderService orderService;


    private static List<String> descriptions = List.of(
            "A stylish and comfortable piece made for everyday wear. Soft feel, easy to match, and designed for a clean modern look—perfect for school, work, or going out.",
            "Level up your fit with this must-have piece. Comfortable, bold, and easy to style—wear it solo or layer it for a full streetwear vibe.",
            "Crafted with attention to detail and a premium finish. A timeless design that looks sharp, feels comfortable, and fits perfectly into a classy wardrobe.",
            "Made for movement and all-day comfort. Lightweight, breathable, and flexible—ideal for training, walking, or casual everyday outfits.",
            "Warm, soft, and built for comfort. Perfect for cold days, layering, and relaxing while still looking clean and stylish."
    );
    private static Random random = new Random();


    @PostConstruct
    public void init() {
        categoryService.create("T-Shirts", "T-Shirts");
        categoryService.create("Jeans", "Jeans");
        categoryService.create("Pants", "Pants");
        categoryService.create("Jackets", "Jackets");
        categoryService.create("Dresses", "Dresses");
        categoryService.create("Hoodies", "Hoodies");

        //create(Size size, Integer stock, Long productId);

        //T-Shirts----------------------------------------------------------------------------------------------------------------------------
        //KIDS
        productService.create("Nirvana", 22.90, getDescription(), Department.KIDS, "/uploads/products/1.jpg", 1L);
        variantService.create(Size.S, getStock(), 1L);
        variantService.create(Size.XS, getStock(), 1L);

        productService.create("Rolling Stone", 19.90, getDescription(), Department.KIDS, "/uploads/products/2.jpg", 1L);
        variantService.create(Size.S, getStock(), 2L);
        variantService.create(Size.XS, getStock(), 2L);
        variantService.create(Size.M, getStock(), 2L);

        productService.create("Lady Gaga", 22.90, getDescription(), Department.KIDS, "/uploads/products/3.jpg", 1L);
        variantService.create(Size.XS, getStock(), 3L);
        variantService.create(Size.S, getStock(), 3L);

        productService.create("Kpop Demon Hunters", 22.90, getDescription(), Department.KIDS, "/uploads/products/4.jpg", 1L);
        variantService.create(Size.S, getStock(), 4L);
        variantService.create(Size.XS, getStock(), 4L);

        productService.create("Raised Skate", 14.90, getDescription(), Department.KIDS, "/uploads/products/5.jpg", 1L);
        variantService.create(Size.XS, getStock(), 5L);
        variantService.create(Size.S, getStock(), 5L);
        //WOMEN

        productService.create("Short Sleeve Lace", 35.90, getDescription(), Department.WOMEN, "/uploads/products/6.jpg", 1L);
        variantService.create(Size.XS, getStock(), 6L);
        variantService.create(Size.S, getStock(), 6L);
        variantService.create(Size.M, getStock(), 6L);
        variantService.create(Size.L, getStock(), 6L);

        productService.create("Shirt-V-Neck", 17.90, getDescription(), Department.WOMEN, "/uploads/products/7.jpg", 1L);
        variantService.create(Size.XS, getStock(), 7L);
        variantService.create(Size.S, getStock(), 7L);
        variantService.create(Size.L, getStock(), 7L);

        productService.create("Raised Bow", 29.90, getDescription(), Department.WOMEN, "/uploads/products/8.jpg", 1L);
        variantService.create(Size.XS, getStock(), 8L);
        variantService.create(Size.S, getStock(), 8L);


        productService.create("Shirt Long Sleeve Text T-Shirt", 29.90, getDescription(), Department.WOMEN, "/uploads/products/9.jpg", 1L);
        variantService.create(Size.S, getStock(), 9L);
        variantService.create(Size.L, getStock(), 9L);
        variantService.create(Size.XL, getStock(), 9L);

        productService.create("Lace Ribbed", 29.90, getDescription(), Department.WOMEN, "/uploads/products/10.jpg", 1L);
        variantService.create(Size.S, getStock(), 10L);

        //MEN
        productService.create("Contrast Heart Rib", 39.90, getDescription(), Department.MEN, "/uploads/products/11.jpg", 1L);
        variantService.create(Size.M, getStock(), 11L);
        variantService.create(Size.L, getStock(), 11L);
        variantService.create(Size.XL, getStock(), 11L);

        productService.create("Dice Print", 45.90, getDescription(), Department.MEN, "/uploads/products/12.jpg", 1L);
        variantService.create(Size.S, getStock(), 12L);
        variantService.create(Size.XL, getStock(), 12L);
        variantService.create(Size.XXL, getStock(), 12L);

        productService.create("Basic Textured", 29.90, getDescription(), Department.MEN, "/uploads/products/13.jpg", 1L);
        variantService.create(Size.S, getStock(), 13L);
        variantService.create(Size.M, getStock(), 13L);
        variantService.create(Size.L, getStock(), 13L);
        variantService.create(Size.XL, getStock(), 13L);

        productService.create("Washed Tank Top", 29.90, getDescription(), Department.MEN, "/uploads/products/14.jpg", 1L);
        variantService.create(Size.M, getStock(), 14L);
        variantService.create(Size.XXXL, getStock(), 14L);


        //JEANS--------------------------------------------------------------------------
        //KIDS
        productService.create("Distressed Wide Leg Jeans", 39.90, getDescription(), Department.KIDS, "/uploads/products/15.jpg", 2L);
        variantService.create(Size.XS, getStock(), 15L);
        variantService.create(Size.S, getStock(), 15L);

        productService.create("Wide Leg Jeans", 39.90, getDescription(), Department.KIDS, "/uploads/products/16.jpg", 2L);
        variantService.create(Size.XS, getStock(), 16L);
        variantService.create(Size.S, getStock(), 16L);
        variantService.create(Size.M, getStock(), 16L);

        productService.create("Sailor Jeans", 35.90, getDescription(), Department.KIDS, "/uploads/products/17.jpg", 2L);
        variantService.create(Size.XS, getStock(), 17L);
        variantService.create(Size.S, getStock(), 17L);

        productService.create("Balloon Jeans", 35.90, getDescription(), Department.KIDS, "/uploads/products/18.jpg", 2L);
        variantService.create(Size.M, getStock(), 18L);

        productService.create("Relaxed Baggy Jeans", 35.90, getDescription(), Department.KIDS, "/uploads/products/19.jpg", 2L);
        variantService.create(Size.XS, getStock(), 19L);
        variantService.create(Size.S, getStock(), 19L);
        variantService.create(Size.M, getStock(), 19L);
        variantService.create(Size.L, getStock(), 19L);

        //WOMEN
        productService.create("Z.01 Mom High Waist Jeans", 49.90, getDescription(), Department.WOMEN, "/uploads/products/20.jpg", 2L);
        variantService.create(Size.XS, getStock(), 20L);
        variantService.create(Size.S, getStock(), 20L);

        productService.create("TRF Mid-Rise Balloon Jeans with Loops", 69.90, getDescription(), Department.WOMEN, "/uploads/products/21.jpg", 2L);
        variantService.create(Size.XS, getStock(), 21L);
        variantService.create(Size.S, getStock(), 21L);
        variantService.create(Size.XL, getStock(), 21L);

        productService.create("Z1975 Loose Straight Leg Mid-Rise Jeans", 69.90, getDescription(), Department.WOMEN, "/uploads/products/22.jpg", 2L);
        variantService.create(Size.S, getStock(), 22L);
        variantService.create(Size.M, getStock(), 22L);
        variantService.create(Size.XXL, getStock(), 22L);

        productService.create("Embroidered Cupid Balloon Fit Jeans", 79.90, getDescription(), Department.MEN, "/uploads/products/23.jpg", 2L);
        variantService.create(Size.M, getStock(), 23L);
        variantService.create(Size.L, getStock(), 23L);
        variantService.create(Size.XXXL, getStock(), 23L);
        variantService.create(Size.XXXXL, getStock(), 23L);

        productService.create("Baggy Fit Jeans", 79.90, getDescription(), Department.MEN, "/uploads/products/24.jpg", 2L);
        variantService.create(Size.S, getStock(), 24L);
        variantService.create(Size.M, getStock(), 24L);
        variantService.create(Size.XL, getStock(), 24L);

        productService.create("Flare Fit Jeans", 79.90, getDescription(), Department.MEN, "/uploads/products/25.jpg", 2L);
        variantService.create(Size.M, getStock(), 25L);

        productService.create("Straight Fit Jeans", 59.90, getDescription(), Department.MEN, "/uploads/products/26.jpg", 2L);
        variantService.create(Size.XL, getStock(), 26L);
        variantService.create(Size.XXL, getStock(), 26L);

        //PANTS----------------------------------------------------------------------
        //KIDS
        productService.create("Corduroy Barrel Pants", 45.90, getDescription(), Department.KIDS, "/uploads/products/27.jpg", 3L);
        variantService.create(Size.XS, getStock(), 27L);
        variantService.create(Size.S, getStock(), 27L);
        variantService.create(Size.M, getStock(), 27L);

        productService.create("Knit Wide Leg Pants", 32.90, getDescription(), Department.KIDS, "/uploads/products/28.jpg", 3L);
        variantService.create(Size.XS, getStock(), 28L);
        variantService.create(Size.S, getStock(), 28L);

        productService.create("Baggy Twill Pants", 35.90, getDescription(), Department.KIDS, "/uploads/products/29.jpg", 3L);
        variantService.create(Size.XS, getStock(), 29L);
        variantService.create(Size.S, getStock(), 29L);

        productService.create("Label Cargo Jogger Pants", 35.90, getDescription(), Department.KIDS, "/uploads/products/30.jpg", 3L);
        variantService.create(Size.XS, getStock(), 30L);
        variantService.create(Size.S, getStock(), 30L);
        variantService.create(Size.M, getStock(), 30L);

        //WOMEN
        productService.create("Washed Effect Flare Pants", 39.90, getDescription(), Department.WOMEN, "/uploads/products/31.jpg", 3L);
        variantService.create(Size.S, getStock(), 31L);
        variantService.create(Size.M, getStock(), 31L);
        variantService.create(Size.L, getStock(), 31L);

        productService.create("Lace Capri Leggings", 39.90, getDescription(), Department.WOMEN, "/uploads/products/32.jpg", 3L);
        variantService.create(Size.L, getStock(), 32L);
        variantService.create(Size.XL, getStock(), 32L);
        variantService.create(Size.XXL, getStock(), 32L);

        productService.create("Pinstripe Flare Pants", 49.90, getDescription(), Department.WOMEN, "/uploads/products/33.jpg", 3L);
        variantService.create(Size.XS, getStock(), 33L);
        variantService.create(Size.S, getStock(), 33L);
        variantService.create(Size.M, getStock(), 33L);

        //MEN
        productService.create("Flowy Relaxed Fit Pants", 69.90, getDescription(), Department.MEN, "/uploads/products/34.jpg", 3L);
        variantService.create(Size.M, getStock(), 34L);
        variantService.create(Size.L, getStock(), 34L);
        variantService.create(Size.XL, getStock(), 34L);

        productService.create("Relaxed Fit 100% Linen Pants", 67.90, getDescription(), Department.MEN, "/uploads/products/35.jpg", 3L);
        variantService.create(Size.M, getStock(), 35L);
        variantService.create(Size.L, getStock(), 35L);

        productService.create("Parachute Cargo Pants", 79.90, getDescription(), Department.MEN, "/uploads/products/36.jpg", 3L);
        variantService.create(Size.M, getStock(), 36L);
        variantService.create(Size.L, getStock(), 36L);
        variantService.create(Size.XL, getStock(), 36L);
        variantService.create(Size.XXL, getStock(), 36L);

        //JACKETS------------------------------------------------------------------------------
        //KIDS
        productService.create("100% Leather Jacket", 139.90, getDescription(), Department.KIDS, "/uploads/products/37.jpg", 4L);
        variantService.create(Size.XS, getStock(), 37L);
        variantService.create(Size.S, getStock(), 37L);

        productService.create("Long Belted Trench Coat", 49.90, getDescription(), Department.KIDS, "/uploads/products/38.jpg", 4L);
        variantService.create(Size.XS, getStock(), 38L);
        variantService.create(Size.S, getStock(), 38L);

        productService.create("Embroidered Text Faux Bomber Jacket", 49.90, getDescription(), Department.KIDS, "/uploads/products/39.jpg", 4L);
        variantService.create(Size.XS, getStock(), 39L);
        variantService.create(Size.S, getStock(), 39L);
        variantService.create(Size.M, getStock(), 39L);

        productService.create("Water Repellent Hoodie Technical Jacket", 49.90, getDescription(), Department.KIDS, "/uploads/products/40.jpg", 4L);
        variantService.create(Size.XS, getStock(), 40L);
        variantService.create(Size.S, getStock(), 40L);

        //WOMEN
        productService.create("Flowy Belted Jacket ZW Collection", 169.90, getDescription(), Department.WOMEN, "/uploads/products/41.jpg", 4L);
        variantService.create(Size.XS, getStock(), 41L);
        variantService.create(Size.S, getStock(), 41L);
        variantService.create(Size.M, getStock(), 41L);

        productService.create("Corduroy Jacket With Faux Leather Collar", 79.90, getDescription(), Department.WOMEN, "/uploads/products/42.jpg", 4L);
        variantService.create(Size.XS, getStock(), 42L);
        variantService.create(Size.S, getStock(), 42L);
        variantService.create(Size.M, getStock(), 42L);

        productService.create("Padded Combination Collar Jacket", 89.90, getDescription(), Department.WOMEN, "/uploads/products/43.jpg", 4L);
        variantService.create(Size.XS, getStock(), 43L);
        variantService.create(Size.S, getStock(), 43L);
        variantService.create(Size.M, getStock(), 43L);
        variantService.create(Size.XL, getStock(), 43L);
        //MEN
        productService.create("Origins Wool Zippered Jacket", 229.90, getDescription(), Department.MEN, "/uploads/products/44.jpg", 4L);
        variantService.create(Size.S, getStock(), 44L);
        variantService.create(Size.M, getStock(), 44L);

        productService.create("Water Repellent Technical Jacket With Fleece", 99.90, getDescription(), Department.MEN, "/uploads/products/45.jpg", 4L);
        variantService.create(Size.L, getStock(), 45L);
        variantService.create(Size.XL, getStock(), 45L);
        variantService.create(Size.XXXL, getStock(), 45L);

        productService.create("Faux Leather Jacket", 119.90, getDescription(), Department.MEN, "/uploads/products/46.jpg", 4L);
        variantService.create(Size.S, getStock(), 46L);
        variantService.create(Size.L, getStock(), 46L);
        variantService.create(Size.XL, getStock(), 46L);

        //DRESSES---------------------------------------------------------------------
        //KIDS
        productService.create("Plaid Dress", 45.90, getDescription(), Department.KIDS, "/uploads/products/47.jpg", 5L);
        variantService.create(Size.XS, getStock(), 47L);
        variantService.create(Size.S, getStock(), 47L);


        productService.create("Buttoned Textured Pinafore", 35.90, getDescription(), Department.KIDS, "/uploads/products/48.jpg", 5L);
        variantService.create(Size.XS, getStock(), 48L);
        variantService.create(Size.S, getStock(), 48L);

        //WOMEN
        productService.create("Sparkly Lace Halter Dress", 59.00, getDescription(), Department.WOMEN, "/uploads/products/49.jpg", 5L);
        variantService.create(Size.XS, getStock(), 49L);
        variantService.create(Size.S, getStock(), 49L);

        productService.create("Short Plaid Dress", 69.90, getDescription(), Department.WOMEN, "/uploads/products/50.jpg", 5L);
        variantService.create(Size.S, getStock(), 50L);
        variantService.create(Size.M, getStock(), 50L);
        variantService.create(Size.L, getStock(), 50L);

        productService.create("TRF Denim Zipper Dress", 69.90, getDescription(), Department.WOMEN, "/uploads/products/51.jpg", 5L);
        variantService.create(Size.S, getStock(), 51L);
        variantService.create(Size.M, getStock(), 51L);
        variantService.create(Size.L, getStock(), 51L);
        variantService.create(Size.XL, getStock(), 51L);
        //Hoodies----------------------------------------------------------------------------------------------------------------------------------------------
        //MEN
        productService.create("Compact Boxy Fit Hoodie", 79.90, getDescription(), Department.MEN, "/uploads/products/52.jpg", 6L);
        variantService.create(Size.M, getStock(), 52L);
        variantService.create(Size.L, getStock(), 52L);
        variantService.create(Size.XL, getStock(), 52L);
        variantService.create(Size.XXL, getStock(), 52L);
        variantService.create(Size.XXXXL, getStock(), 52L);

        productService.create("Basic Hoodie", 49.90, getDescription(), Department.MEN, "/uploads/products/53.jpg", 6L);
        variantService.create(Size.S, getStock(), 53L);
        variantService.create(Size.M, getStock(), 53L);

        productService.create("Technical Zip Hoodie", 79.90, getDescription(), Department.MEN, "/uploads/products/54.jpg", 6L);
        variantService.create(Size.S, getStock(), 54L);
        variantService.create(Size.M, getStock(), 54L);
        variantService.create(Size.L, getStock(), 54L);

        productService.create("Technical Combined Fleece Hoodie", 89.90, getDescription(), Department.MEN, "/uploads/products/55.jpg", 6L);
        variantService.create(Size.M, getStock(), 55L);
        variantService.create(Size.L, getStock(), 55L);
        variantService.create(Size.XL, getStock(), 55L);
        variantService.create(Size.XXL, getStock(), 55L);


        User user = userRepository.save(new User(
                "user",
                passwordEncoder.encode("user"),
                "user",
                "user",
                Role.ROLE_USER
        ));

        User admin = userRepository.save(new User(
                "admin",
                passwordEncoder.encode("admin"),
                "admin",
                "admin",
                Role.ROLE_ADMIN
        ));

        //create(User user, String address, String email, String phone, String city,
        //int zip, String country, String paymentMethod, Double total);

//        orderService.create(user, "", "", "", "", 0, "", "CASH", 350.0);
//        orderService.create(user, "", "", "", "", 0, "", "CASH", 350.0);
//        orderService.create(user, "", "", "", "", 0, "", "CASH", 350.0);
//        orderService.create(user, "", "", "", "", 0, "", "CASH", 350.0);
//        orderService.create(user, "", "", "", "", 0, "", "CASH", 350.0);
//        orderService.create(user, "", "", "", "", 0, "", "CASH", 350.0);
//
//        orderService.create(admin, "", "", "", "", 0, "", "CASH", 350.0);
//        orderService.create(admin, "", "", "", "", 0, "", "CASH", 350.0);
//        orderService.create(admin, "", "", "", "", 0, "", "CASH", 350.0);
//        orderService.create(admin, "", "", "", "", 0, "", "CASH", 350.0);
//        orderService.create(admin, "", "", "", "", 0, "", "CASH", 350.0);
//        orderService.create(admin, "", "", "", "", 0, "", "CASH", 350.0);


    }


    public static String getDescription() {
        return descriptions.get(random.nextInt(5));
    }

    public int getStock() {
        return random.nextInt(1, 51);
    }
}
