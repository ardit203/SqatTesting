package finki.ukim.mk.onlineclothingstore.web.controller.user;

import finki.ukim.mk.onlineclothingstore.service.RatingService;
import lombok.AllArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/rating")
@AllArgsConstructor
public class RatingController {
    private final RatingService ratingService;

    @PreAuthorize("hasRole('USER')")
    @PostMapping("/{productId}")
    public String rate(@PathVariable Long productId, @RequestParam Integer rating){
        ratingService.create(rating, productId);
        return "redirect:/products/details/" + productId.toString();
    }
}
