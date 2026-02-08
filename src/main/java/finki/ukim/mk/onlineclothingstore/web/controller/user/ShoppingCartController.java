package finki.ukim.mk.onlineclothingstore.web.controller.user;


import finki.ukim.mk.onlineclothingstore.dto.CartVariantDto;
import finki.ukim.mk.onlineclothingstore.model.CartVariant;
import finki.ukim.mk.onlineclothingstore.model.ShoppingCart;
import finki.ukim.mk.onlineclothingstore.model.User;
import finki.ukim.mk.onlineclothingstore.model.exceptions.VariantOutOfStockException;
import finki.ukim.mk.onlineclothingstore.service.CartVariantService;
import finki.ukim.mk.onlineclothingstore.service.ShoppingCartService;
import finki.ukim.mk.onlineclothingstore.service.VariantService;
import finki.ukim.mk.onlineclothingstore.service.impl.PricingServiceImpl;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/cart")
@AllArgsConstructor
public class ShoppingCartController {
    private final ShoppingCartService shoppingCartService;
    private final CartVariantService cartVariantService;
    private final PricingServiceImpl pricingService;

    @PreAuthorize("hasRole('USER')")
    @GetMapping
    public String getCartPage(Model model, @AuthenticationPrincipal User user) {
        if (user == null) {
            return "redirect:/login";
        }

        ShoppingCart cart = shoppingCartService.findOrCreate(user.getUsername());
        List<CartVariant> cartVariants = cartVariantService.findByCartId(cart.getId());

        model.addAttribute("cartVariants", cartVariants);
        model.addAttribute("total", pricingService.calculateTotal(cartVariants));
        model.addAttribute("bodyContent", "userTemplates/cart");
        model.addAttribute("cartEmpty", cartVariantService.isEmpty(user.getUsername()));

        return "master-template";
    }

    @PreAuthorize("hasRole('USER')")
    @GetMapping("/addToCart/{variantId}/{quantity}")
    public ResponseEntity<?> addToCart(@PathVariable Long variantId,
                                       @PathVariable Integer quantity,
                                       @AuthenticationPrincipal User user) {
        if (user == null) {
            return ResponseEntity.notFound().build();
        }
        try {
            ShoppingCart cart = shoppingCartService.findOrCreate(user.getUsername());
            cartVariantService.createOrMerge(cart.getId(), variantId, quantity);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @PreAuthorize("hasRole('USER')")
    @GetMapping("/updateCart/{cartVariantId}/{quantity}")
    public ResponseEntity<?> updateCart(@PathVariable Long cartVariantId,
                                        @PathVariable Integer quantity,
                                        @AuthenticationPrincipal User user) {
        if (user == null) {
            return ResponseEntity.notFound().build();
        }
        try {
            cartVariantService.update(cartVariantId, quantity);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }


    @PreAuthorize("hasRole('USER')")
    @GetMapping("/removeFromCart/{cartVariantId}")
    public String removeFromCart(@PathVariable Long cartVariantId) {
        cartVariantService.deleteById(cartVariantId);
        return "redirect:/cart";
    }

    @PreAuthorize("hasRole('USER')")
    @PostMapping("/check")
    public ResponseEntity<?> checkForStock(@AuthenticationPrincipal User user,
                                           @RequestBody List<CartVariantDto> cartVariantDtos) {
        if (user == null) {
            return ResponseEntity.status(401).body(Map.of("redirect", "/login"));
        }

        try {
            List<CartVariant> cartVariants = cartVariantService.toCartVariants(cartVariantDtos);
            cartVariantService.checkForSufficientStocks(cartVariants);

            return ResponseEntity.ok(Map.of("redirect", "/order"));
        } catch (VariantOutOfStockException e) {
            return ResponseEntity.status(409).body(Map.of("error", e.getMessage()));
        }
    }

}
