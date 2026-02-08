package finki.ukim.mk.onlineclothingstore.web.controller.user;


import finki.ukim.mk.onlineclothingstore.model.*;
import finki.ukim.mk.onlineclothingstore.service.*;
import lombok.AllArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/order")
@AllArgsConstructor
public class OrderController {
    private final OrderService orderService;
    private final CartVariantService cartVariantService;
    private final PricingService pricingService;
    private final DeliveryService deliveryService;
    private final OrderVariantService orderVariantService;

    @PreAuthorize("hasRole('USER')")
    @GetMapping
    public String getOrderPage(Model model, @AuthenticationPrincipal User user) {
        if (user == null) {
            return "redirect:/login";
        }
        model.addAttribute("total", pricingService.calculateTotal(user.getUsername()));
        model.addAttribute("cartEmpty", cartVariantService.isEmpty(user.getUsername()));
        model.addAttribute("bodyContent", "userTemplates/order");
        return "master-template";
    }

    @PreAuthorize("hasRole('USER')")
    @PostMapping
    public String order(@RequestParam String email,
                        @RequestParam String phone,
                        @RequestParam String address,
                        @RequestParam String city,
                        @RequestParam Integer zip,
                        @RequestParam String country,
                        @RequestParam String paymentMethod,
                        @AuthenticationPrincipal User user,
                        Model model){
        if(user == null){
            return "redirect:/login";
        }
        List<CartVariant> cartVariants = cartVariantService.findByUsername(user.getUsername());
        Double total = pricingService.calculateTotal(cartVariants);
        Order order = orderService.create(user.getUsername(), address, email,phone,city,zip,country,paymentMethod, total);
        try {
            orderVariantService.addVariantsToOrder(order.getId(), cartVariants);
        }catch (Exception e){
            orderService.deleteById(order.getId());
            model.addAttribute("error", e.getMessage());
            return getOrderPage(model, user);
        }

        return "redirect:/profile";
    }

    @PreAuthorize("hasRole('USER')")
    @GetMapping("/cancel/{id}")
    public String cancelOrder(@PathVariable Long id){
        Order order = orderService.findById(id);
        deliveryService.cancel(order.getDelivery().getId());
        orderVariantService.addVariantsToStockAfterOrderCancellation(order.getId());
        return "redirect:/profile";
    }
}
