package finki.ukim.mk.onlineclothingstore.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import finki.ukim.mk.onlineclothingstore.dto.CartVariantDto;
import finki.ukim.mk.onlineclothingstore.model.*;
import finki.ukim.mk.onlineclothingstore.model.enums.Department;
import finki.ukim.mk.onlineclothingstore.model.enums.DeliveryStatus;
import finki.ukim.mk.onlineclothingstore.model.enums.Role;
import finki.ukim.mk.onlineclothingstore.model.enums.Size;
import finki.ukim.mk.onlineclothingstore.repository.*;
import finki.ukim.mk.onlineclothingstore.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class FullWorkflowIT {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;

    @Autowired private UserService userService;

    @Autowired private UserRepository userRepository;
    @Autowired private CategoryRepository categoryRepository;
    @Autowired private ProductRepository productRepository;
    @Autowired private VariantRepository variantRepository;
    @Autowired private ShoppingCartRepository shoppingCartRepository;
    @Autowired private CartVariantRepository cartVariantRepository;
    @Autowired private OrderRepository orderRepository;
    @Autowired private OrderVariantRepository orderVariantRepository;
    @Autowired private DeliveryRepository deliveryRepository;

    private User wfUser;
    private Variant vStock10Price50;

    private static final String PASS = "AA11.!aaaa";

    @BeforeEach
    void setUp() {
        String username = "wf_user_" + System.nanoTime();

        userService.register(username, PASS, PASS, "Ardit", "Selmani", Role.ROLE_USER);
        wfUser = userRepository.findByUsername(username).orElseThrow();

        vStock10Price50 = createVariant("WF-Product", 50.0, 10, Size.M);
    }

    private Variant createVariant(String productName, double price, int stock, Size size) {
        Category c = categoryRepository.save(new Category("WF-Cat-" + productName, "desc"));
        Product p = productRepository.save(new Product(
                productName,
                "desc",
                price,
                Department.MEN,
                "/uploads/test.jpg",
                c
        ));
        return variantRepository.save(new Variant(p, size, stock));
    }

    private ShoppingCart getCart(User u) {
        return shoppingCartRepository.findByUser(u).orElseThrow();
    }

    private List<CartVariantDto> currentCartDtos(User u) {
        List<CartVariant> cvs = cartVariantRepository.findByCart_User(u);
        return CartVariantDto.from(cvs);
    }

    @Test
    void WF_01_cart_check_stock_then_order_success_createsOrder_clearsCart_reducesStock() throws Exception {
        int qty = 2;

        // 1) Add item to cart
        mockMvc.perform(get("/cart/addToCart/{variantId}/{quantity}", vStock10Price50.getId(), qty)
                        .with(user(wfUser)))
                .andExpect(status().isOk());

        // 2) Stock check
        List<CartVariantDto> cartDtos = currentCartDtos(wfUser);

        mockMvc.perform(post("/cart/check")
                        .with(user(wfUser))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(cartDtos)))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.redirect").value("/order"));

        // 3) Place order
        mockMvc.perform(post("/order")
                        .with(user(wfUser))
                        .param("email", "wf@test.com")
                        .param("phone", "070000000")
                        .param("address", "WF Address 1")
                        .param("city", "Skopje")
                        .param("zip", "1000")
                        .param("country", "MK")
                        .param("paymentMethod", "CASH"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/profile"));

        // 4) Assert DB side effects
        List<Order> orders = orderRepository.findByUser(wfUser);
        assertThat(orders).hasSize(1);

        Order order = orders.get(0);
        assertThat(order.getTotal()).isEqualTo(50.0 * qty);

        // delivery created
        Delivery delivery = deliveryRepository.findById(order.getDelivery().getId()).orElseThrow();
        assertThat(delivery.getStatus()).isEqualTo(DeliveryStatus.PENDING);

        // orderVariants created
        List<OrderVariant> ovs = orderVariantRepository.findByOrder(order);
        assertThat(ovs).hasSize(1);
        assertThat(ovs.get(0).getVariant().getId()).isEqualTo(vStock10Price50.getId());
        assertThat(ovs.get(0).getQuantity()).isEqualTo(qty);

        // cart cleared
        assertThat(cartVariantRepository.findByCart_User(wfUser)).isEmpty();

        // stock reduced
        Variant updated = variantRepository.findById(vStock10Price50.getId()).orElseThrow();
        assertThat(updated.getStock()).isEqualTo(10 - qty);
    }

    @Test
    void WF_02_placeOrder_then_cancel_restoresStock_and_marksDeliveryCancelled() throws Exception {
        int qty = 3;

        // Place order
        mockMvc.perform(get("/cart/addToCart/{variantId}/{quantity}", vStock10Price50.getId(), qty)
                        .with(user(wfUser)))
                .andExpect(status().isOk());

        List<CartVariantDto> cartDtos = currentCartDtos(wfUser);
        mockMvc.perform(post("/cart/check")
                        .with(user(wfUser))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(cartDtos)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.redirect").value("/order"));

        mockMvc.perform(post("/order")
                        .with(user(wfUser))
                        .param("email", "wf@test.com")
                        .param("phone", "070000000")
                        .param("address", "WF Address 2")
                        .param("city", "Skopje")
                        .param("zip", "1000")
                        .param("country", "MK")
                        .param("paymentMethod", "CASH"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/profile"));

        Order order = orderRepository.findByUser(wfUser).get(0);

        // after order, stock reduced
        Variant afterOrder = variantRepository.findById(vStock10Price50.getId()).orElseThrow();
        assertThat(afterOrder.getStock()).isEqualTo(10 - qty);

        // Cancel
        mockMvc.perform(get("/order/cancel/{id}", order.getId())
                        .with(user(wfUser)))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/profile"));

        // Assert: delivery status CANCELLED
        Delivery delivery = deliveryRepository.findById(order.getDelivery().getId()).orElseThrow();
        assertThat(delivery.getStatus()).isEqualTo(DeliveryStatus.CANCELLED);

        // stock restored back to original
        Variant restored = variantRepository.findById(vStock10Price50.getId()).orElseThrow();
        assertThat(restored.getStock()).isEqualTo(10);
    }
}
