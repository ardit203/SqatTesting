package finki.ukim.mk.onlineclothingstore.integration;

import finki.ukim.mk.onlineclothingstore.model.Order;
import finki.ukim.mk.onlineclothingstore.model.User;
import finki.ukim.mk.onlineclothingstore.model.enums.Role;
import finki.ukim.mk.onlineclothingstore.repository.OrderRepository;
import finki.ukim.mk.onlineclothingstore.repository.UserRepository;
import finki.ukim.mk.onlineclothingstore.service.OrderService;
import finki.ukim.mk.onlineclothingstore.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class ProfileControllerIT {

    @Autowired private MockMvc mockMvc;

    @Autowired private UserService userService;
    @Autowired private OrderService orderService;

    @Autowired private UserRepository userRepository;
    @Autowired private OrderRepository orderRepository;

    @Autowired private PasswordEncoder passwordEncoder;

    private static final String USERNAME = "profile_user";
    private static final String ADMIN_USERNAME = "profile_admin";

    private static final String INITIAL_PASSWORD = "AA11.!aaaa";
    private static final String NEW_PASSWORD = "BB22.!bbbb";

    private User userEntity;
    private User adminEntity;

    @BeforeEach
    void setUp() {
        if (userRepository.findByUsername(USERNAME).isEmpty()) {
            userService.register(USERNAME, INITIAL_PASSWORD, INITIAL_PASSWORD, "Ardit", "Selmani", Role.ROLE_USER);
        }
        userEntity = userRepository.findByUsername(USERNAME).orElseThrow();

        if (userRepository.findByUsername(ADMIN_USERNAME).isEmpty()) {
            userRepository.save(new User(ADMIN_USERNAME, "pass", "Admin", "User", Role.ROLE_ADMIN));
        }
        adminEntity = userRepository.findByUsername(ADMIN_USERNAME).orElseThrow();
    }

    // -------------------------
    // GET /profile
    // -------------------------

    @Test
    void PRC_01_getProfile_anonymous_redirectsToLogin() throws Exception {
        mockMvc.perform(get("/profile"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));
    }

    @Test
    void PRC_02_getProfile_admin_forbidden() throws Exception {
        mockMvc.perform(get("/profile").with(user(adminEntity)))
                .andExpect(status().isForbidden());
    }

    @Test
    void PRC_03_getProfile_user_noOrders_contractAndEmptyLists() throws Exception {
        List<Order> existing = orderRepository.findByUser(userEntity);
        assertThat(existing).isEmpty();

        MvcResult res = mockMvc.perform(get("/profile").with(user(userEntity)))
                .andExpect(status().isOk())
                .andExpect(view().name("master-template"))
                .andExpect(model().attribute("bodyContent", "userTemplates/profile"))
                .andExpect(model().attributeExists("cartEmpty", "user", "orders", "orderDtos"))
                .andReturn();

        Map<String, Object> model = res.getModelAndView().getModel();

        @SuppressWarnings("unchecked")
        List<Order> orders = (List<Order>) model.get("orders");
        assertThat(orders).isEmpty();

        @SuppressWarnings("unchecked")
        List<?> orderDtos = (List<?>) model.get("orderDtos");
        assertThat(orderDtos).isEmpty();
    }

    @Test
    void PRC_04_getProfile_user_withOrders_contractAndNonEmptyLists() throws Exception {
        orderService.create(
                userEntity.getUsername(),
                "Address 1",
                "mail@test.com",
                "070000000",
                "Skopje",
                1000,
                "MK",
                "CASH",
                120.0
        );

        MvcResult res = mockMvc.perform(get("/profile").with(user(userEntity)))
                .andExpect(status().isOk())
                .andExpect(view().name("master-template"))
                .andExpect(model().attribute("bodyContent", "userTemplates/profile"))
                .andExpect(model().attributeExists("cartEmpty", "user", "orders", "orderDtos"))
                .andReturn();

        Map<String, Object> model = res.getModelAndView().getModel();

        @SuppressWarnings("unchecked")
        List<Order> orders = (List<Order>) model.get("orders");
        assertThat(orders).isNotEmpty();

        @SuppressWarnings("unchecked")
        List<?> orderDtos = (List<?>) model.get("orderDtos");
        assertThat(orderDtos).isNotEmpty();
    }

    // -------------------------
    // GET /profile/edit-profile
    // -------------------------

    @Test
    void PRC_05_getEditProfile_user_contract() throws Exception {
        mockMvc.perform(get("/profile/edit-profile").with(user(userEntity)))
                .andExpect(status().isOk())
                .andExpect(view().name("master-template"))
                .andExpect(model().attribute("bodyContent", "userTemplates/profile-form"))
                .andExpect(model().attributeExists("user", "cartEmpty", "passwordMessage"))
                .andExpect(model().attribute("passwordMessage", UserService.message));
    }

    // -------------------------
    // POST /profile/edit-profile
    // -------------------------

    @Test
    void PRC_06_postEditProfile_validPasswords_redirectsAndUpdatesPassword() throws Exception {
        mockMvc.perform(post("/profile/edit-profile").with(user(userEntity)).with(csrf())
                        .param("currentPassword", INITIAL_PASSWORD)
                        .param("newPassword", NEW_PASSWORD)
                        .param("confirmNewPassword", NEW_PASSWORD))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/profile"));

        User updated = userRepository.findByUsername(USERNAME).orElseThrow();
        assertThat(passwordEncoder.matches(NEW_PASSWORD, updated.getPassword())).isTrue();
        assertThat(passwordEncoder.matches(INITIAL_PASSWORD, updated.getPassword())).isFalse();
    }

    @Test
    void PRC_07_postEditProfile_wrongCurrentPassword_returnsFormWithError_passwordUnchanged() throws Exception {
        String wrongCurrent = "WrongPass11.!A";

        mockMvc.perform(post("/profile/edit-profile").with(user(userEntity)).with(csrf())
                        .param("currentPassword", wrongCurrent)
                        .param("newPassword", NEW_PASSWORD)
                        .param("confirmNewPassword", NEW_PASSWORD))
                .andExpect(status().isOk())
                .andExpect(view().name("master-template"))
                .andExpect(model().attribute("bodyContent", "userTemplates/profile-form"))
                .andExpect(model().attributeExists("error"))
                .andExpect(model().attributeExists("passwordMessage"));

        User unchanged = userRepository.findByUsername(USERNAME).orElseThrow();
        assertThat(passwordEncoder.matches(INITIAL_PASSWORD, unchanged.getPassword())).isTrue();
        assertThat(passwordEncoder.matches(NEW_PASSWORD, unchanged.getPassword())).isFalse();
    }
}
