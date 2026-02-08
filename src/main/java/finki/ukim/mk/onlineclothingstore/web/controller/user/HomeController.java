package finki.ukim.mk.onlineclothingstore.web.controller.user;


import finki.ukim.mk.onlineclothingstore.model.User;
import finki.ukim.mk.onlineclothingstore.service.CartVariantService;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping(path = {"/", "/home"})
@AllArgsConstructor
public class HomeController {
    private final CartVariantService cartVariantService;

    @GetMapping
    public String getHomePage(Model model, @AuthenticationPrincipal User user){
        if(user != null){
            model.addAttribute("cartEmpty", cartVariantService.isEmpty(user.getUsername()));
        }

        model.addAttribute("bodyContent", "userTemplates/home");

        return "master-template";
    }

    @GetMapping("/access-denied")
    public String getAccessDeniedPage(Model model) {
        model.addAttribute("bodyContent", "userTemplates/access-denied");
        return "master-template";
    }

}
