package finki.ukim.mk.onlineclothingstore.web.controller.user;


import finki.ukim.mk.onlineclothingstore.model.Order;
import finki.ukim.mk.onlineclothingstore.model.User;
import finki.ukim.mk.onlineclothingstore.service.CartVariantService;
import finki.ukim.mk.onlineclothingstore.service.OrderService;
import finki.ukim.mk.onlineclothingstore.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequestMapping("/profile")
@AllArgsConstructor
public class ProfileController {
    private final OrderService orderService;
    private final CartVariantService cartVariantService;
    private final UserService userService;

    @PreAuthorize("hasRole('USER')")
    @GetMapping
    public String getProfilePage(@AuthenticationPrincipal User user, Model model) {
        if(user == null){
            return "redirect:/login";
        }
        List<Order> orders = orderService.findByUsername(user.getUsername());
        model.addAttribute("cartEmpty", cartVariantService.isEmpty(user.getUsername()));
        model.addAttribute("user", user);
        model.addAttribute("orders", orders);
        model.addAttribute("bodyContent", "userTemplates/profile");
        model.addAttribute("orderDtos", orderService.toOrderDtos(orders));
        return "master-template";
    }

    @PreAuthorize("hasRole('USER')")
    @GetMapping("/edit-profile")
    public String getEditProfilePage(@AuthenticationPrincipal User user, Model model) {
        if(user == null){
            return  "redirect:/login";
        }
        model.addAttribute("user", user);
        model.addAttribute("cartEmpty", cartVariantService.isEmpty(user.getUsername()));
        model.addAttribute("passwordMessage", UserService.message);
        model.addAttribute("bodyContent", "userTemplates/profile-form");
        return "master-template";
    }

    @PreAuthorize("hasRole('USER')")
    @PostMapping("/edit-profile")
    public String editProfile(@AuthenticationPrincipal User user,
                              @RequestParam String currentPassword,
                              @RequestParam String newPassword,
                              @RequestParam String confirmNewPassword,
                              Model model) {
        try {
            userService.changePassword(user, currentPassword, newPassword, confirmNewPassword);
            return "redirect:/profile";
        }catch (RuntimeException e){
            model.addAttribute("error", e.getMessage());
            return getEditProfilePage(user, model);
        }
    }
}
