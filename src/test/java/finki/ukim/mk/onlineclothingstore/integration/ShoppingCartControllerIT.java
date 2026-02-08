package finki.ukim.mk.onlineclothingstore.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import finki.ukim.mk.onlineclothingstore.dto.CartVariantDto;
import finki.ukim.mk.onlineclothingstore.model.*;
import finki.ukim.mk.onlineclothingstore.model.enums.Department;
import finki.ukim.mk.onlineclothingstore.model.enums.Role;
import finki.ukim.mk.onlineclothingstore.model.enums.Size;
import finki.ukim.mk.onlineclothingstore.repository.*;
import finki.ukim.mk.onlineclothingstore.service.ShoppingCartService;
import finki.ukim.mk.onlineclothingstore.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.closeTo;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class ShoppingCartControllerIT {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;

    @Autowired private UserService userService;
    @Autowired private ShoppingCartService shoppingCartService;

    @Autowired private UserRepository userRepository;
    @Autowired private CategoryRepository categoryRepository;
    @Autowired private ProductRepository productRepository;
    @Autowired private VariantRepository variantRepository;
    @Autowired private CartVariantRepository cartVariantRepository;

    private static final String USERNAME = "cart_user";
    private static final String PASS = "AA11.!aaaa";

    private User userEntity;
    private Variant variantStock10Price50;
    private Variant variantStock1Price80;

    @BeforeEach
    void setUp() {
        if (userRepository.findByUsername(USERNAME).isEmpty()) {
            userService.register(USERNAME, PASS, PASS, "Ardit", "Selmani", Role.ROLE_USER);
        }
        userEntity = userRepository.findByUsername(USERNAME).orElseThrow();

        variantStock10Price50 = createVariant("V1", 50.0, 10, Size.M);
        variantStock1Price80  = createVariant("V2", 80.0, 1, Size.L);
    }

    private Variant createVariant(String productName, double price, int stock, Size size) {
        Category c = categoryRepository.save(new Category("Cat-" + productName, "desc"));
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

    private ShoppingCart getOrCreateCart() {
        return shoppingCartService.findOrCreate(userEntity.getUsername());
    }

    private CartVariant addCartVariant(ShoppingCart cart, Variant v, int qty) {
        return cartVariantRepository.save(new CartVariant(cart, v, qty));
    }

    // --------------------------------
    // GET /cart
    // --------------------------------

    @Test
    void SC_01_getCart_anonymous_redirectsToLogin() throws Exception {
        mockMvc.perform(get("/cart"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));
    }

    @Test
    void SC_02_getCart_user_emptyCart_contract() throws Exception {
        ShoppingCart cart = getOrCreateCart();
        cartVariantRepository.deleteAll(cartVariantRepository.findByCart(cart));

        mockMvc.perform(get("/cart").with(user(userEntity)))
                .andExpect(status().isOk())
                .andExpect(view().name("master-template"))
                .andExpect(model().attribute("bodyContent", "userTemplates/cart"))
                .andExpect(model().attribute("cartEmpty", true))
                .andExpect(model().attributeExists("cartVariants", "total"))
                .andExpect(model().attribute("total", closeTo(0.0, 0.0001)));
    }

    @Test
    void SC_03_getCart_user_nonEmptyCart_contract_totalComputed() throws Exception {
        ShoppingCart cart = getOrCreateCart();
        cartVariantRepository.deleteAll(cartVariantRepository.findByCart(cart));

        addCartVariant(cart, variantStock10Price50, 2);

        MvcResult res = mockMvc.perform(get("/cart").with(user(userEntity)))
                .andExpect(status().isOk())
                .andExpect(view().name("master-template"))
                .andExpect(model().attribute("bodyContent", "userTemplates/cart"))
                .andExpect(model().attribute("cartEmpty", false))
                .andExpect(model().attribute("total", closeTo(100.0, 0.0001)))
                .andExpect(model().attributeExists("cartVariants"))
                .andReturn();

        @SuppressWarnings("unchecked")
        List<CartVariant> list = (List<CartVariant>) res.getModelAndView().getModel().get("cartVariants");
        assertThat(list).hasSize(1);
        assertThat(list.get(0).getQuantity()).isEqualTo(2);
    }

    // --------------------------------
    // GET /cart/addToCart/{variantId}/{quantity}
    // --------------------------------

    @Test
    void SC_04_addToCart_valid_returns200_andPersistsCartVariant() throws Exception {
        ShoppingCart cart = getOrCreateCart();
        cartVariantRepository.deleteAll(cartVariantRepository.findByCart(cart));

        mockMvc.perform(get("/cart/addToCart/{variantId}/{quantity}",
                        variantStock10Price50.getId(), 3)
                        .with(user(userEntity)))
                .andExpect(status().isOk());

        List<CartVariant> items = cartVariantRepository.findByCart(cart);
        assertThat(items).hasSize(1);
        assertThat(items.get(0).getVariant().getId()).isEqualTo(variantStock10Price50.getId());
        assertThat(items.get(0).getQuantity()).isEqualTo(3);
    }

    @Test
    void SC_05_addToCart_invalidQuantity_returns500_andNoMutation() throws Exception {
        ShoppingCart cart = getOrCreateCart();
        cartVariantRepository.deleteAll(cartVariantRepository.findByCart(cart));

        mockMvc.perform(get("/cart/addToCart/{variantId}/{quantity}",
                        variantStock10Price50.getId(), 0)
                        .with(user(userEntity)))
                .andExpect(status().is5xxServerError());

        assertThat(cartVariantRepository.findByCart(cart)).isEmpty();
    }

    // --------------------------------
    // GET /cart/updateCart/{cartVariantId}/{quantity}
    // --------------------------------

    @Test
    void SC_06_updateCart_valid_returns200_andUpdatesQuantity() throws Exception {
        ShoppingCart cart = getOrCreateCart();
        cartVariantRepository.deleteAll(cartVariantRepository.findByCart(cart));

        CartVariant cv = addCartVariant(cart, variantStock10Price50, 1);

        mockMvc.perform(get("/cart/updateCart/{id}/{quantity}", cv.getId(), 4)
                        .with(user(userEntity)))
                .andExpect(status().isOk());

        CartVariant updated = cartVariantRepository.findById(cv.getId()).orElseThrow();
        assertThat(updated.getQuantity()).isEqualTo(4);
    }

    @Test
    void SC_07_updateCart_invalidQuantity_returns500_andQuantityUnchanged() throws Exception {
        ShoppingCart cart = getOrCreateCart();
        cartVariantRepository.deleteAll(cartVariantRepository.findByCart(cart));

        CartVariant cv = addCartVariant(cart, variantStock10Price50, 2);

        mockMvc.perform(get("/cart/updateCart/{id}/{quantity}", cv.getId(), 0)
                        .with(user(userEntity)))
                .andExpect(status().is5xxServerError());

        CartVariant unchanged = cartVariantRepository.findById(cv.getId()).orElseThrow();
        assertThat(unchanged.getQuantity()).isEqualTo(2);
    }

    // --------------------------------
    // GET /cart/removeFromCart/{cartVariantId}
    // --------------------------------

    @Test
    void SC_08_removeFromCart_redirects_andDeletesCartVariant() throws Exception {
        ShoppingCart cart = getOrCreateCart();
        cartVariantRepository.deleteAll(cartVariantRepository.findByCart(cart));

        CartVariant cv = addCartVariant(cart, variantStock10Price50, 1);

        mockMvc.perform(get("/cart/removeFromCart/{id}", cv.getId())
                        .with(user(userEntity)))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/cart"));

        assertThat(cartVariantRepository.findById(cv.getId())).isEmpty();
    }

    // --------------------------------
    // POST /cart/check
    // --------------------------------

    @Test
    void SC_09_checkForStock_sufficientStock_returns200_redirectOrder() throws Exception {
        ShoppingCart cart = getOrCreateCart();
        cartVariantRepository.deleteAll(cartVariantRepository.findByCart(cart));

        CartVariant cv = addCartVariant(cart, variantStock10Price50, 1);
        List<CartVariantDto> req = List.of(new CartVariantDto(
                cv.getId(),
                cart.getId(),
                variantStock10Price50.getId(),
                1
        ));

        mockMvc.perform(post("/cart/check")
                        .with(user(userEntity))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.redirect").value("/order"));
    }

    @Test
    void SC_10_checkForStock_insufficientStock_returns409_withError() throws Exception {
        ShoppingCart cart = getOrCreateCart();
        cartVariantRepository.deleteAll(cartVariantRepository.findByCart(cart));

        CartVariant cv = addCartVariant(cart, variantStock1Price80, 5);
        List<CartVariantDto> req = List.of(new CartVariantDto(
                cv.getId(),
                cart.getId(),
                variantStock1Price80.getId(),
                5
        ));

        mockMvc.perform(post("/cart/check")
                        .with(user(userEntity))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isConflict())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.error").exists());
    }
}
