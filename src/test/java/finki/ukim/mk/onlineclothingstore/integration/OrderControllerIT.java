package finki.ukim.mk.onlineclothingstore.integration;

import finki.ukim.mk.onlineclothingstore.model.*;
import finki.ukim.mk.onlineclothingstore.model.enums.Department;
import finki.ukim.mk.onlineclothingstore.model.enums.Role;
import finki.ukim.mk.onlineclothingstore.model.enums.Size;
import finki.ukim.mk.onlineclothingstore.model.enums.DeliveryStatus;
import finki.ukim.mk.onlineclothingstore.repository.*;
import finki.ukim.mk.onlineclothingstore.service.OrderService;
import finki.ukim.mk.onlineclothingstore.service.OrderVariantService;
import org.assertj.core.api.Assertions;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import java.util.List;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class OrderControllerIT {

    @Autowired private MockMvc mockMvc;

    @Autowired private UserRepository userRepository;
    @Autowired private CategoryRepository categoryRepository;
    @Autowired private ProductRepository productRepository;
    @Autowired private VariantRepository variantRepository;
    @Autowired private ShoppingCartRepository shoppingCartRepository;
    @Autowired private CartVariantRepository cartVariantRepository;
    @Autowired private OrderRepository orderRepository;
    @Autowired private OrderVariantRepository orderVariantRepository;
    @Autowired private DeliveryRepository deliveryRepository;

    @Autowired private OrderService orderService;
    @Autowired private OrderVariantService orderVariantService;

    @PersistenceContext
    private EntityManager em;

    private User persistUser(String username, Role role) {
        User u = new User(username, "pass123", "Test", "User", role);
        return userRepository.save(u);
    }

    private Variant persistVariant(double price, int stock, Size size) {
        Category c = categoryRepository.save(new Category("Cat", "Cat desc"));
        Product p = productRepository.save(new Product(
                "Prod",
                "Prod desc",
                price,
                Department.MEN,
                "/uploads/test.jpg",
                c
        ));
        return variantRepository.save(new Variant(p, size, stock));
    }

    private ShoppingCart persistCart(User user) {
        return shoppingCartRepository.save(new ShoppingCart(user));
    }

    private CartVariant persistCartVariant(ShoppingCart cart, Variant variant, int qty) {
        return cartVariantRepository.save(new CartVariant(cart, variant, qty));
    }

    // ---------------------------
    // GET /order
    // ---------------------------

    @Test
    void OC_01_getOrder_anonymous_redirectsToLogin() throws Exception {
        mockMvc.perform(get("/order"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));
    }

    @Test
    void OC_02_getOrder_admin_forbidden_accessDeniedHandling() throws Exception {
        User admin = persistUser("admin1", Role.ROLE_ADMIN);

        MvcResult result = mockMvc.perform(get("/order").with(user(admin)))
                .andExpect(status().isForbidden())
                .andReturn();

        MockHttpServletResponse res = result.getResponse();
        String forwarded = res.getForwardedUrl();
        String redirected = res.getRedirectedUrl();

        if (forwarded != null) {
            Assertions.assertThat(forwarded).contains("access-denied");
        } else if (redirected != null) {
            Assertions.assertThat(redirected).contains("access-denied");
        }
    }

    @Test
    void OC_03_getOrder_user_emptyCart_contractIsCorrect() throws Exception {
        User user = persistUser("user_empty", Role.ROLE_USER);
        persistCart(user);

        mockMvc.perform(get("/order").with(user(user)))
                .andExpect(status().isOk())
                .andExpect(view().name("master-template"))
                .andExpect(model().attribute("bodyContent", "userTemplates/order"))
                .andExpect(model().attribute("cartEmpty", true))
                .andExpect(model().attribute("total", Matchers.closeTo(0.0, 0.0001)));
    }

    @Test
    void OC_04_getOrder_user_nonEmptyCart_contractIsCorrect_totalComputed() throws Exception {
        User user = persistUser("user_cart", Role.ROLE_USER);

        Variant v = persistVariant(100.0, 10, Size.M);
        ShoppingCart cart = persistCart(user);
        persistCartVariant(cart, v, 2); // 2 × 100 = 200

        mockMvc.perform(get("/order").with(user(user)))
                .andExpect(status().isOk())
                .andExpect(view().name("master-template"))
                .andExpect(model().attribute("bodyContent", "userTemplates/order"))
                .andExpect(model().attribute("cartEmpty", false))
                .andExpect(model().attribute("total", Matchers.closeTo(200.0, 0.0001)));
    }

    // ---------------------------
    // POST /order
    // ---------------------------

    @Test
    void OC_05_postOrder_sufficientStock_redirectsToProfile_andPersistsSideEffects() throws Exception {
        User user = persistUser("user_ok", Role.ROLE_USER);

        Variant v = persistVariant(50.0, 10, Size.S);
        ShoppingCart cart = persistCart(user);
        CartVariant cv = persistCartVariant(cart, v, 3);

        mockMvc.perform(post("/order").with(user(user))
                        .param("email", "test@mail.com")
                        .param("phone", "070000000")
                        .param("address", "Test Address 1")
                        .param("city", "Skopje")
                        .param("zip", "1000")
                        .param("country", "MK")
                        .param("paymentMethod", "CASH"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/profile"));

        em.flush();
        em.clear();

        User dbUser = userRepository.findByUsername(user.getUsername()).orElseThrow();
        List<Order> orders = orderRepository.findByUser(dbUser);
        Assertions.assertThat(orders).hasSize(1);

        Order order = orders.get(0);
        Assertions.assertThat(order.getTotal()).isCloseTo(150.0, Assertions.within(0.0001));

        List<OrderVariant> ovs = orderVariantRepository.findByOrder(order);
        Assertions.assertThat(ovs).hasSize(1);
        Assertions.assertThat(ovs.get(0).getQuantity()).isEqualTo(3);
        Assertions.assertThat(ovs.get(0).getVariant().getId()).isEqualTo(v.getId());


        Assertions.assertThat(cartVariantRepository.findById(cv.getId())).isEmpty();

        //10 - 3 = 7
        Variant dbVariant = variantRepository.findById(v.getId()).orElseThrow();
        Assertions.assertThat(dbVariant.getStock()).isEqualTo(7);
    }

    @Test
    void OC_06_postOrder_insufficientStock_returnsOrderPageWithError_andDeletesOrder() throws Exception {
        User user = persistUser("user_fail", Role.ROLE_USER);

        Variant v = persistVariant(80.0, 1, Size.L);
        ShoppingCart cart = persistCart(user);
        CartVariant cv = persistCartVariant(cart, v, 5);

        mockMvc.perform(post("/order").with(user(user))
                        .param("email", "test@mail.com")
                        .param("phone", "070000000")
                        .param("address", "Test Address 2")
                        .param("city", "Skopje")
                        .param("zip", "1000")
                        .param("country", "MK")
                        .param("paymentMethod", "CASH"))
                .andExpect(status().isOk())
                .andExpect(view().name("master-template"))
                .andExpect(model().attribute("bodyContent", "userTemplates/order"))
                .andExpect(model().attributeExists("error"));

        em.flush();
        em.clear();

        User dbUser = userRepository.findByUsername(user.getUsername()).orElseThrow();

        Assertions.assertThat(orderRepository.findByUser(dbUser)).isEmpty();

        Assertions.assertThat(cartVariantRepository.findById(cv.getId())).isPresent();

        Variant dbVariant = variantRepository.findById(v.getId()).orElseThrow();
        Assertions.assertThat(dbVariant.getStock()).isEqualTo(1);
    }

    // ---------------------------
    // GET /order/cancel/{id}
    // ---------------------------

    @Test
    void OC_07_cancelOrder_validOrder_redirectsToProfile_deliveryCanceled_stockRestored() throws Exception {
        User user = persistUser("user_cancel", Role.ROLE_USER);

        Variant v = persistVariant(40.0, 10, Size.M);
        ShoppingCart cart = persistCart(user);
        persistCartVariant(cart, v, 2);

        Order order = orderService.create(
                user.getUsername(),
                "Addr",
                "mail@test.com",
                "070000000",
                "Skopje",
                1000,
                "MK",
                "CASH",
                80.0
        );

        List<CartVariant> cartVariants = cartVariantRepository.findByCart_User(user);
        orderVariantService.addVariantsToOrder(order.getId(), cartVariants);

        em.flush();
        em.clear();

        Assertions.assertThat(variantRepository.findById(v.getId()).orElseThrow().getStock()).isEqualTo(8);

        mockMvc.perform(get("/order/cancel/" + order.getId()).with(user(user)))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/profile"));

        em.flush();
        em.clear();

        Order dbOrder = orderRepository.findById(order.getId()).orElseThrow();
        Delivery dbDelivery = deliveryRepository.findById(dbOrder.getDelivery().getId()).orElseThrow();

        Assertions.assertThat(dbDelivery.getStatus()).isEqualTo(DeliveryStatus.CANCELLED);

        Variant dbVariant = variantRepository.findById(v.getId()).orElseThrow();
        Assertions.assertThat(dbVariant.getStock()).isEqualTo(10);
    }
}
