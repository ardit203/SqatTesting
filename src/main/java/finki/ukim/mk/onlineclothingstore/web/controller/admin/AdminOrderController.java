package finki.ukim.mk.onlineclothingstore.web.controller.admin;

import finki.ukim.mk.onlineclothingstore.model.Order;
import finki.ukim.mk.onlineclothingstore.model.User;
import finki.ukim.mk.onlineclothingstore.model.enums.DeliveryStatus;
import finki.ukim.mk.onlineclothingstore.service.DeliveryService;
import finki.ukim.mk.onlineclothingstore.service.OrderService;
import finki.ukim.mk.onlineclothingstore.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequestMapping("admin/order")
@AllArgsConstructor
public class AdminOrderController {
    private final OrderService orderService;
    private final DeliveryService deliveryService;
    private final UserService userService;

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public String getAdminOrderPage(@RequestParam(required = false) Long id,
                                    @RequestParam(required = false) DeliveryStatus status,
                                    @RequestParam(required = false) String username,
                                    @RequestParam(required = false) LocalDateTime fromDate,
                                    @RequestParam(required = false) LocalDateTime toDate,
                                    @RequestParam(required = false) Double fromPrice,
                                    @RequestParam(required = false) Double toPrice,
                                    @RequestParam(defaultValue = "1") Integer pageNum,
                                    @RequestParam(defaultValue = "10") Integer pageSize,
                                    @RequestParam(defaultValue = "byUsernameAsc") String sort,
                                    Model model){
        Page<Order> page = orderService.findPage(id, status,username,fromDate,toDate,fromPrice,toPrice,pageNum,pageSize,sort);
        model.addAttribute("page", page);
        model.addAttribute("users", userService.findAllUsernames());
        model.addAttribute("statuses", DeliveryStatus.values());
        model.addAttribute("orderDtos", orderService.toOrderDtos(page.getContent()));
        model.addAttribute("bodyContent", "adminTemplates/admin-order");

        model.addAttribute("selectedId", id);
        model.addAttribute("selectedStatus", status);
        model.addAttribute("selectedUsername", username);
        model.addAttribute("selectedFromDate", fromDate);
        model.addAttribute("selectedToDate", toDate);
        model.addAttribute("selectedFromPrice", fromPrice);
        model.addAttribute("selectedToPrice", toPrice);
        model.addAttribute("selectedSort", sort);
        return "admin-template";
    }
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/confirm/{id}")
    public String confirm(@PathVariable Long id){
        Order order = orderService.findById(id);
        deliveryService.confirm(order.getDelivery().getId());
        return "redirect:/admin/order";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/ship/{id}")
    public String ship(@PathVariable Long id){
        Order order = orderService.findById(id);
        deliveryService.ship(order.getDelivery().getId());
        return "redirect:/admin/order";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/deliver/{id}")
    public String deliver(@PathVariable Long id){
        Order order = orderService.findById(id);
        deliveryService.deliver(order.getDelivery().getId());
        return "redirect:/admin/order";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Long id){
        try {
            orderService.deleteById(id);

        }catch (Exception e){
            System.out.println(e.getMessage());
        }
        return "redirect:/admin/order";
    }
}
