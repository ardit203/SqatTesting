package finki.ukim.mk.onlineclothingstore.web.controller.user;


import jakarta.servlet.http.HttpSession;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/login")
@AllArgsConstructor
public class LoginController {

    @GetMapping
    public String getLoginPage(@RequestParam(required = false) String error,
                               HttpSession session,
                               Model model) {
        if (error != null) {
            model.addAttribute("error", session.getAttribute("LOGIN_ERROR"));
            session.removeAttribute("LOGIN_ERROR");
        }
        return "login";
    }
}
