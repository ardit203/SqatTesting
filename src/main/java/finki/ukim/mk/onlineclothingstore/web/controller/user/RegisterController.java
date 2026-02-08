package finki.ukim.mk.onlineclothingstore.web.controller.user;


import finki.ukim.mk.onlineclothingstore.model.enums.Role;
import finki.ukim.mk.onlineclothingstore.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/register")
@AllArgsConstructor
public class RegisterController {
    private final UserService userService;

    @GetMapping
    public String getRegisterPage(Model model) {
        model.addAttribute("passwordMessage", UserService.message);
        model.addAttribute("bodyContent", "register");
        return "register";
    }

    @PostMapping
    public String register(@RequestParam String name,
                           @RequestParam String surname,
                           @RequestParam String username,
                           @RequestParam String password,
                           @RequestParam String repeatedPassword,
                           Model model) {

        try {
            this.userService.register(username, password, repeatedPassword, name, surname, Role.ROLE_USER);
            return "redirect:/login";
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("name", name);
            model.addAttribute("surname", surname);
            model.addAttribute("username", username);
            return getRegisterPage(model);
        }

    }

}

