package finki.ukim.mk.onlineclothingstore.web.controller.admin;

import finki.ukim.mk.onlineclothingstore.dto.VariantDto;
import finki.ukim.mk.onlineclothingstore.model.Category;
import finki.ukim.mk.onlineclothingstore.model.Product;
import finki.ukim.mk.onlineclothingstore.model.Rating;
import finki.ukim.mk.onlineclothingstore.model.Variant;
import finki.ukim.mk.onlineclothingstore.model.enums.Department;
import finki.ukim.mk.onlineclothingstore.service.*;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Controller
@RequestMapping("/admin/products")
@AllArgsConstructor
public class AdminProductController {
    private final ProductService productService;
    private final CategoryService categoryService;
    private final VariantService variantService;
    private final StorageService storageService;
    private final RatingService ratingService;

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public String getProductsPage(@RequestParam(required = false) Long id,
                                  @RequestParam(required = false) String name,
                                  @RequestParam(required = false) Double greaterThan,
                                  @RequestParam(required = false) Double lessThan,
                                  @RequestParam(required = false) Long categoryId,
                                  @RequestParam(required = false) Department department,
                                  @RequestParam(defaultValue = "1") Integer pageNum,
                                  @RequestParam(defaultValue = "10") Integer pageSize,
                                  @RequestParam(defaultValue = "byPriceAsc") String sort,
                                  Model model) {

        Page<Product> page = productService.findPage(id, name, greaterThan, lessThan, department, categoryId, pageNum, pageSize, sort);
        model.addAttribute("page", page);
        model.addAttribute("departments", Department.values());
        model.addAttribute("categories", categoryService.findAll());
        model.addAttribute("bodyContent", "adminTemplates/admin-products");

        List<Variant> variants = variantService.getVariantsForProducts(page.getContent());
        System.out.println(variants);
        model.addAttribute("variants", VariantDto.from(variants));

        model.addAttribute("selectedId", id);
        model.addAttribute("selectedName", name);
        model.addAttribute("selectedGreater", greaterThan);
        model.addAttribute("selectedLess", lessThan);
        model.addAttribute("selectedCat", categoryId);
        model.addAttribute("selectedDepartment", department);
        model.addAttribute("selectedSort", sort);


        return "admin-template";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/details/{id}")
    public String details(@PathVariable Long id, Model model){
        Product product = productService.findById(id);
        List<Variant> variants = variantService.findByProductId(id);
        List<Rating> ratings = ratingService.findByProductId(id);
        model.addAttribute("product", product);
        model.addAttribute("variants", variants);
        model.addAttribute("avgRating", ratingService.getAvgRating(ratings));
        model.addAttribute("ratingCount", ratings.size());
        model.addAttribute("bodyContent", "adminTemplates/admin-product-details");
        return "admin-template";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/add")
    public String getAddForm(Model model) {
        model.addAttribute("categories", categoryService.findAll());
        model.addAttribute("departments", Department.values());
        model.addAttribute("bodyContent", "adminTemplates/admin-product-form");
        return "admin-template";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/add")
    public String add(@RequestParam String name,
                      @RequestParam Double price,
                      @RequestParam Long categoryId,
                      @RequestParam Department department,
                      @RequestParam MultipartFile image,
                      @RequestParam String description,
                      Model model) throws IOException {
        try {
            Product product = productService.create(name, price, description, department, categoryId);
            String imageUrl = storageService.saveProductImage(product.getId(), image);
            productService.addImage(product.getId(), imageUrl);
            return "redirect:/admin/products";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return getAddForm(model);
        }
    }


    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/edit/{id}")
    public String getEditForm(@PathVariable Long id, Model model) {
        model.addAttribute("categories", categoryService.findAll());
        model.addAttribute("departments", Department.values());
        Product product = productService.findById(id);
        model.addAttribute("product", product);
        model.addAttribute("hasImage", product.getImage() != null);
        model.addAttribute("bodyContent", "adminTemplates/admin-product-form");
        return "admin-template";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/edit/{id}")
    public String edit(@PathVariable Long id,
                       @RequestParam String name,
                       @RequestParam Double price,
                       @RequestParam Long categoryId,
                       @RequestParam Department department,
                       @RequestParam(required = false) MultipartFile image,
                       @RequestParam String description,
                       Model model) {
        try {
            Product product = productService.findById(id);
            String imageUrl = storageService.replaceProductImage(product.getId(), image, product.getImage());
            productService.update(id, name, price, description, department, imageUrl, categoryId);
            return "redirect:/admin/products";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return getEditForm(id, model);
        }
    }


    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Long id) throws IOException {
        Product product = productService.findById(id);
        storageService.deleteByPublicUrl(product.getImage());
        productService.deleteById(id);
        return "redirect:/admin/products";
    }
}
