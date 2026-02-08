package finki.ukim.mk.onlineclothingstore.web.controller.admin;



import finki.ukim.mk.onlineclothingstore.model.enums.Size;
import finki.ukim.mk.onlineclothingstore.service.ProductService;
import finki.ukim.mk.onlineclothingstore.service.StorageService;
import finki.ukim.mk.onlineclothingstore.service.VariantService;
import lombok.AllArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;


@Controller
@RequestMapping("/admin/variants")
@AllArgsConstructor
public class AdminVariantController {
    private final VariantService variantService;
    private final ProductService productService;


    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/add/{productId}")
    public String getAddForm(@PathVariable Long productId, Model model){
        model.addAttribute("sizes", Size.values());
        model.addAttribute("bodyContent", "adminTemplates/admin-variant-form");
        model.addAttribute("product", productService.findById(productId));
        return "admin-template";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/add/{productId}")
    public String add(@PathVariable Long productId, @RequestParam Size size, @RequestParam Integer stock, Model model){
        try {
            variantService.create(size, stock, productId);
            return "redirect:/admin/products/details/" + productId;
        }catch (Exception e){
            model.addAttribute("error", String.format("A variant with size %c%s%c already exists for this product.", '"', size, '"'));
            return getAddForm(productId, model);
        }
    }


    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/edit/{productId}/{id}")
    public String getEditForm(@PathVariable Long productId, @PathVariable Long id, Model model){
        model.addAttribute("sizes", Size.values());
        model.addAttribute("product", productService.findById(productId));
        model.addAttribute("variant", variantService.findById(id));
        model.addAttribute("bodyContent", "adminTemplates/admin-variant-form");

        return "admin-template";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/edit/{productId}/{id}")
    public String add(@PathVariable Long productId,
                      @PathVariable Long id,
                      @RequestParam Size size,
                      @RequestParam Integer stock,
                      Model model){
        try {
            variantService.update(id,size, stock, productId);
            return "redirect:/admin/products/details/" + productId;
        }catch (Exception e){
            model.addAttribute("error", String.format("A variant with size %c%s%c already exists for this product.", '"', size, '"'));
            return getEditForm(id,productId, model);
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/delete/{productId}/{id}")
    public String delete(@PathVariable Long productId, @PathVariable Long id){
        variantService.deleteById(id);
        return "redirect:/admin/products/details/"+productId;
    }

}
