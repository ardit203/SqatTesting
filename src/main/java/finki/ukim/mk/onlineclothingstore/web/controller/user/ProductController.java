package finki.ukim.mk.onlineclothingstore.web.controller.user;


import finki.ukim.mk.onlineclothingstore.dto.VariantDto;
import finki.ukim.mk.onlineclothingstore.model.Product;
import finki.ukim.mk.onlineclothingstore.model.Rating;
import finki.ukim.mk.onlineclothingstore.model.User;
import finki.ukim.mk.onlineclothingstore.model.Variant;
import finki.ukim.mk.onlineclothingstore.model.enums.Department;
import finki.ukim.mk.onlineclothingstore.service.*;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequestMapping("/products")
@AllArgsConstructor
public class ProductController {
    private final ProductService productService;
    private final CartVariantService cartVariantService;
    private final CategoryService categoryService;
    private final RatingService ratingService;
    private final VariantService variantService;

    @PreAuthorize("hasRole('USER')")
    @GetMapping
    public String getProductsPage(@RequestParam(required = false) String name,
                                  @RequestParam(required = false) Double greaterThan,
                                  @RequestParam(required = false) Double lessThan,
                                  @RequestParam(required = false) Long categoryId,
                                  @RequestParam(required = false) Department department,
                                  @RequestParam(defaultValue = "1") Integer pageNum,
                                  @RequestParam(defaultValue = "9") Integer pageSize,
                                  @RequestParam(defaultValue = "byPriceAsc") String sort,
                                  @AuthenticationPrincipal User user,
                                  Model model) {
        Page<Product> page = productService.findPage(null, name, greaterThan, lessThan, department, categoryId, pageNum, pageSize, sort);
        model.addAttribute("page", page);
        model.addAttribute("departments", Department.values());
        model.addAttribute("categories", categoryService.findAll());
        model.addAttribute("bodyContent", "userTemplates/products");
        List<Variant> variants = variantService.getVariantsForProducts(page.getContent());
        System.out.println(variants);
        model.addAttribute("variants", VariantDto.from(variants));

        model.addAttribute("selectedName", name);
        model.addAttribute("selectedGreater", greaterThan);
        model.addAttribute("selectedLess", lessThan);
        model.addAttribute("selectedCat", categoryId);
        model.addAttribute("selectedDepartment", department);
        model.addAttribute("selectedSort", sort);

        if (user != null) {
            model.addAttribute("cartEmpty", cartVariantService.isEmpty(user.getUsername()));
        }


        return "master-template";
    }

    @PreAuthorize("hasRole('USER')")
    @GetMapping("/details/{id}")
    public String getProductDetails(@PathVariable Long id, @AuthenticationPrincipal User user, Model model) {
        Product product = productService.findById(id);
        model.addAttribute("product", product);
        List<Rating> ratings = ratingService.findByProductId(id);

        model.addAttribute("avgRating", ratingService.getAvgRating(ratings));
        model.addAttribute("ratingCount", ratings.size());
        model.addAttribute("variants", VariantDto.from(variantService.findByProductId(id)));


        if (user != null) {
            model.addAttribute("cartEmpty", cartVariantService.isEmpty(user.getUsername()));
        }

        model.addAttribute("bodyContent", "userTemplates/product-details");
        return "master-template";
    }
}
