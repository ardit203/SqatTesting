package finki.ukim.mk.onlineclothingstore.integration;

import finki.ukim.mk.onlineclothingstore.model.User;
import finki.ukim.mk.onlineclothingstore.model.enums.Role;
import finki.ukim.mk.onlineclothingstore.repository.UserRepository;
import finki.ukim.mk.onlineclothingstore.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class RegisterControllerIT {

    @Autowired private MockMvc mockMvc;

    @Autowired private UserRepository userRepository;


    private static final String VALID_PASSWORD = "AAa11.!aaaa";
    private static final String VALID_PASSWORD_2 = "BBb22.!bbbb";

    @BeforeEach
    void seedExistingUserForNegativeTests() {
        if (userRepository.findByUsername("existingUser").isEmpty()) {
            userRepository.save(new User("existingUser", "pass", "Existing", "User", Role.ROLE_USER));
        }
    }

    // -------------------------
    // GET /register
    // -------------------------

    @Test
    void RC_01_getRegisterPage_contract() throws Exception {
        mockMvc.perform(get("/register"))
                .andExpect(status().isOk())
                .andExpect(view().name("register"))
                .andExpect(model().attribute("bodyContent", "register"))
                .andExpect(model().attributeExists("passwordMessage"));

        mockMvc.perform(get("/register"))
                .andExpect(model().attribute("passwordMessage", UserService.message));
    }

    // -------------------------
    // POST /register
    // -------------------------

    @Test
    void RC_02_register_validInputs_redirectsToLogin_andPersistsUser() throws Exception {
        String username = "newUser1";

        mockMvc.perform(post("/register")
                        .param("name", "Ardit")
                        .param("surname", "Selmani")
                        .param("username", username)
                        .param("password", VALID_PASSWORD)
                        .param("repeatedPassword", VALID_PASSWORD))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));

        assertThat(userRepository.findByUsername(username)).isPresent();
    }

    @Test
    void RC_03_register_usernameAlreadyExists_returnsRegister_withError_andEchoesInputs() throws Exception {
        mockMvc.perform(post("/register")
                        .param("name", "Ardit")
                        .param("surname", "Selmani")
                        .param("username", "existingUser")
                        .param("password", VALID_PASSWORD)
                        .param("repeatedPassword", VALID_PASSWORD))
                .andExpect(status().isOk())
                .andExpect(view().name("register"))
                .andExpect(model().attribute("bodyContent", "register"))
                .andExpect(model().attributeExists("passwordMessage"))
                .andExpect(model().attributeExists("error"))
                .andExpect(model().attribute("name", "Ardit"))
                .andExpect(model().attribute("surname", "Selmani"))
                .andExpect(model().attribute("username", "existingUser"));
    }

    @Test
    void RC_04_register_passwordMismatch_returnsRegister_withError_andEchoesInputs() throws Exception {
        mockMvc.perform(post("/register")
                        .param("name", "Ardit")
                        .param("surname", "Selmani")
                        .param("username", "newUser2")
                        .param("password", VALID_PASSWORD)
                        .param("repeatedPassword", VALID_PASSWORD_2))
                .andExpect(status().isOk())
                .andExpect(view().name("register"))
                .andExpect(model().attribute("bodyContent", "register"))
                .andExpect(model().attributeExists("passwordMessage"))
                .andExpect(model().attributeExists("error"))
                .andExpect(model().attribute("name", "Ardit"))
                .andExpect(model().attribute("surname", "Selmani"))
                .andExpect(model().attribute("username", "newUser2"));

        assertThat(userRepository.findByUsername("newUser2")).isEmpty();
    }
}
